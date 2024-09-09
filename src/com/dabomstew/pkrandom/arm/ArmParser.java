package com.dabomstew.pkrandom.arm;

import com.dabomstew.pkrandom.romhandlers.ParagonLiteAddressMap;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteOverlay;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.zip.DataFormatException;

public class ArmParser {
    ParagonLiteAddressMap globalAddressMap;

    String varDefines = "";
    Map<String, Integer> labelAddressMap = new HashMap<>();
    Map<Integer, Integer> dataRamAddressMap = new TreeMap<>();
    Map<Integer, Integer> redirectorAddressMap = new TreeMap<>();
    ParagonLiteOverlay overlay;
    int initialRamAddress = 0;
    int currentRamAddress = 0;
    int size = 0;

    ScriptEngine engine;

    public ArmParser() {
        setupJavaScriptEngine();
    }

    public ArmParser(ParagonLiteAddressMap globalAddressMap) {
        this.globalAddressMap = globalAddressMap;
        setupJavaScriptEngine();
    }

    private void setupJavaScriptEngine() {
        ScriptEngineManager mgr = new ScriptEngineManager();
        engine = mgr.getEngineByName("JavaScript");

        if (engine != null)
            return;

        NashornScriptEngineFactory engineFactory = new NashornScriptEngineFactory();
        engine = engineFactory.getScriptEngine();
    }

    public byte[] parse(List<String> lines) {
        return parse(lines, null, 0);
    }

    public byte[] parse(List<String> lines, ParagonLiteOverlay overlay, int initialRamAddress) {
        if (!isWordAligned(initialRamAddress))
            throw new RuntimeException("Initial RAM offset must be word-aligned.");

        List<Byte> bytes = new ArrayList<>();

        labelAddressMap.clear();
        dataRamAddressMap.clear();
        redirectorAddressMap.clear();

        this.overlay = overlay;
        this.initialRamAddress = initialRamAddress;
        this.currentRamAddress = initialRamAddress;

        StringBuilder varDefinesStringBuilder = new StringBuilder();
        for (int i = 0; i < lines.size(); ++i) {
            String str = stripComment(lines.get(i)).trim();
            if (str.endsWith(":")) {
                // Label
                String labelName = str.substring(0, str.length() - 1).trim();
                labelAddressMap.put(labelName, currentRamAddress);
            } else if (str.toUpperCase().startsWith("#DEFINE ")) {
                String[] defSplit = str.split(" ", 3);
                String varName = defSplit[1];
                String varValue = defSplit[2];
                varDefinesStringBuilder.append(varName);
                varDefinesStringBuilder.append('=');
                varDefinesStringBuilder.append(varValue);
                varDefinesStringBuilder.append(';');
            } else if (str.toUpperCase().startsWith("#SWITCH ")) {
                String[] switchSplit = str.split(" ", 3);
                String switchRegister1 = switchSplit[1];
                String switchRegister2 = switchSplit.length == 3 ? switchSplit[2] : switchRegister1;
                List<String> oldLines = lines;
                lines = new ArrayList<>(lines.size() + 5);
                for (int j = 0; j < i; ++j) {
                    lines.add(oldLines.get(j));
                }
                lines.add(String.format("add %s, %s, %s", switchRegister1, switchRegister2, switchRegister2));
                lines.add(String.format("add %s, pc", switchRegister1));
                lines.add(String.format("ldrh %s, [%s, #2]", switchRegister1, switchRegister1));
                lines.add(String.format("add pc, %s", switchRegister1));
                for (int j = i + 1; j < oldLines.size(); ++j) {
                    lines.add(oldLines.get(j));
                }
                --i;
                continue;
            } else if (str.toUpperCase().startsWith("#SWITCH_FULL ")) {
                String[] switchSplit = str.split(" ", 3);
                String switchRegister1 = switchSplit[1];
                String switchRegister2 = switchSplit.length == 3 ? switchSplit[2] : switchRegister1;
                List<String> oldLines = lines;
                lines = new ArrayList<>(lines.size() + 5);
                for (int j = 0; j < i; ++j) {
                    lines.add(oldLines.get(j));
                }
                lines.add(String.format("add %s, %s, %s", switchRegister1, switchRegister2, switchRegister2));
                lines.add(String.format("add %s, pc", switchRegister1));
                lines.add(String.format("ldrh %s, [%s, #6]", switchRegister1, switchRegister1));
                lines.add(String.format("lsl %s, #16", switchRegister1));
                lines.add(String.format("asr %s, #16", switchRegister1));
                lines.add(String.format("add pc, %s", switchRegister1));
                for (int j = i + 1; j < oldLines.size(); ++j) {
                    lines.add(oldLines.get(j));
                }
                --i;
                continue;
            }

            currentRamAddress += getLineByteLength(str);
        }
        this.varDefines = varDefinesStringBuilder.toString();


        try {
            Object evalObj = engine.eval(varDefines + "0");
            if (!(evalObj instanceof Integer intValue) || intValue != 0)
                throw new RuntimeException("Error while parsing defines: " + varDefines);
        } catch (ScriptException e) {
            throw new RuntimeException("Error while parsing defines: " + e);
        }

        int lastNonSwitchAddress = initialRamAddress;
        currentRamAddress = initialRamAddress;
        for (int i = 0; i < lines.size(); ++i) {
            String str = stripComment(lines.get(i)).trim();
            if (str.toUpperCase().startsWith("#CASE ")) {
                String label = str.split(" ", 2)[1].trim();
                if (!labelAddressMap.containsKey(label))
                    throw new RuntimeException(String.format("Could not find label \"%s\" for case %d", label, i));
                int labelAddress = labelAddressMap.get(label);
                int offset = labelAddress - (lastNonSwitchAddress + 4);
                lines.set(i, "dcw " + offset);
            } else {
                lastNonSwitchAddress = currentRamAddress;
            }

            currentRamAddress += getLineByteLength(str);
        }

        size = alignNextWord(currentRamAddress) - initialRamAddress;

        currentRamAddress = initialRamAddress;
        for (String str : lines) {
            str = stripComment(str).trim();

            if (str.isEmpty() || str.endsWith(":"))
                continue;

            int firstSpaceIndex = str.indexOf(' ');
            if (firstSpaceIndex <= 0)
                throw new RuntimeException("Could not parse " + str);

            String op = str.substring(0, firstSpaceIndex).toLowerCase();
            String args = str.substring(firstSpaceIndex + 1).replace(" ", "");

            byte[] instructionBytes;
            try {
                instructionBytes = parseInstruction(op, args);
            } catch (DataFormatException e) {
                throw new RuntimeException(e);
            }

            for (byte instructionByte : instructionBytes)
                bytes.add(instructionByte);

            currentRamAddress += instructionBytes.length;
        }

        byte[] returnValue = new byte[size];
        for (int i = 0; i < bytes.size(); ++i)
            returnValue[i] = bytes.get(i);

        // Add No Op
        if (!isWordAligned(bytes.size()) && !dataRamAddressMap.isEmpty()) {
            returnValue[bytes.size()] = (byte) 0xC0;
            returnValue[bytes.size() + 1] = (byte) 0x46;
        }

        for (Map.Entry<Integer, Integer> entry : dataRamAddressMap.entrySet()) {
            int dataValue = entry.getKey();

            int offset = entry.getValue() - initialRamAddress;
            returnValue[offset] = (byte) (dataValue & 0xFF);
            returnValue[offset + 1] = (byte) ((dataValue >> 8) & 0xFF);
            returnValue[offset + 2] = (byte) ((dataValue >> 16) & 0xFF);
            returnValue[offset + 3] = (byte) ((dataValue >> 24) & 0xFF);

            // Assume function refs are between 0x02000000 and 0x08000000
            if (globalAddressMap == null || overlay == null || !ParagonLiteAddressMap.isValidAddress(dataValue, true))
                continue;

            int targetAddress = alignWord(dataValue);

            globalAddressMap.addReference(overlay, targetAddress, entry.getValue());
        }

        return returnValue;
    }

    public int getByteLength(List<String> lines) {
        byte[] bytes = parse(lines);
        return bytes.length;
    }

    public int getFuncSize(ParagonLiteOverlay overlay, int initialRamAddress) {
        if (overlay.readWord(initialRamAddress) == 0)
            throw new RuntimeException("Empty function");

        final AtomicReference<Integer> maxOffset = new AtomicReference<>(initialRamAddress);

        traverseData(overlay, initialRamAddress, (context) -> {
            maxOffset.set(Math.max(maxOffset.get(), context.ramAddress + 2));

            // PC-relative load
            if ((context.instruction & 0xF800) == 0x4800) {
                int imm = context.instruction & 0x00FF;
                int dataOffset = alignWord(context.ramAddress + ((imm << 2) + 4));
                maxOffset.set(Math.max(maxOffset.get(), dataOffset + 4));
            }
        });

        return alignNextWord(maxOffset.get()) - initialRamAddress;
    }

    public Map<Integer, Set<Integer>> getOutgoingCodeReferences(ParagonLiteOverlay overlay, int initialRamAddress) {
        AtomicReference<Map<Integer, Set<Integer>>> mapRef = new AtomicReference<>(new HashMap<>());

        traverseData(overlay, initialRamAddress, (context) -> {
            Map<Integer, Set<Integer>> map = mapRef.get();

            // PC-relative load
            if ((context.instruction & 0xF800) == 0x4800) {
                int imm = context.instruction & 0x00FF;
                int ramAddress = alignWord(context.ramAddress + ((imm << 2) + 4));
                int romAddress = overlay.ramToRomAddress(ramAddress);

                int value = overlay.readWord(romAddress);
                if (!ParagonLiteAddressMap.isValidAddress(value, true))
                    return;

                int address = alignWord(value);
                if (!map.containsKey(address))
                    map.put(address, new HashSet<>());
                map.get(address).add(ramAddress);
                return;
            }

            // long branch with link (high)
            if ((context.instruction & 0xF800) == 0xF000) {
                int ramAddress = context.ramAddress + 2;
                int romAddress = overlay.ramToRomAddress(ramAddress);

                int nextInstruction = overlay.readUnsignedHalfword(romAddress);
                if ((nextInstruction & 0xE800) != 0xE800) // can be blx
                    return;

                int high = ((context.instruction & 0x07FF) << 21) >> 9;
                int low = (nextInstruction & 0x07FF) << 1;
                int offset = (high | low);

                int value = context.ramAddress + offset + 4;
                if (!ParagonLiteAddressMap.isValidAddress(value, false))
                    return;

                int address = alignWord(value);
                if (!map.containsKey(address))
                    map.put(address, new HashSet<>());
                map.get(address).add(context.ramAddress);
            }
        });

        return mapRef.get();
    }

    public Map<Integer, Set<Integer>> getOutgoingDataReferences(byte[] data, int startingOffset, int overlayAddress, int size, String refPattern) {
        Map<Integer, Set<Integer>> map = new HashMap<>();

        if (refPattern == null || !refPattern.contains("*")) {
            return map;
        }

        if (refPattern.endsWith("*"))
            refPattern += "0";

        String[] patternSplit = refPattern.split("\\*");

        int dataIndex = 0;
        int patternOffset = 0;
        while (dataIndex < size) {
            if (patternOffset != 0) {
                int offset = startingOffset + dataIndex;
                if (offset < 0 || offset >= data.length)
                    throw new RuntimeException();

                int destinationAddress = alignWord(readWord(data, offset));
                if (ParagonLiteAddressMap.isValidAddress(destinationAddress, false)) {
                    if (!map.containsKey(destinationAddress))
                        map.put(destinationAddress, new HashSet<>());
                    map.get(destinationAddress).add(overlayAddress + offset);
                }

                dataIndex += 4;
            }

            String patternValue = patternSplit[patternOffset];
            if (patternValue != null && !patternValue.isEmpty()) {
                dataIndex += Integer.parseInt(patternValue);
            }
            patternOffset = (patternOffset + 1) % patternSplit.length;
        }

        return map;
    }

    private static class TraversalContext {
        int ramAddress;
        int instruction = -1;

        private TraversalContext(int ramAddress) {
            this.ramAddress = ramAddress;
        }
    }

    private void traverseData(ParagonLiteOverlay overlay, int initialRamAddress, Consumer<TraversalContext> action) {
        if (!isWordAligned(initialRamAddress))
            throw new RuntimeException("StartingOffset must be word-aligned.");

        Queue<TraversalContext> queue = new LinkedList<>();
        queue.add(new TraversalContext(initialRamAddress));

        Set<Integer> seen = new HashSet<>();

        while (!queue.isEmpty()) {
            TraversalContext context = queue.poll();
            if (!seen.add(context.ramAddress))
                continue;

            // read instruction
            int romAddress = overlay.ramToRomAddress(context.ramAddress);
            context.instruction = overlay.readUnsignedHalfword(romAddress);
            action.accept(context); // process lambda

            // conditional branch
            if ((context.instruction & 0xF000) == 0xD000) {
                if (((context.instruction >> 8) & 0x0F) > 13)
                    // undefined or swi
                    continue;

                // signed
                int branchOffset = (byte) (context.instruction & 0x00FF);
                branchOffset = (branchOffset << 1) + 4;

                TraversalContext newContext = new TraversalContext(context.ramAddress + branchOffset);

                queue.add(newContext); // jump
            }

            // unconditional branch
            if ((context.instruction & 0xF800) == 0xE000) {
                // two's compliment 12-bit
                context.ramAddress += (((context.instruction & 0x07FF) << 21) >> 20) + 4;
                queue.add(context);
                continue;
            }

            // add pc (switch)
            if ((context.instruction & 0xFFC7) == 0x4487) {
                Set<Integer> destinations = new HashSet<>();

                for (int i = 0; ; ++i) {
                    int switchJumpRamAddress = context.ramAddress + 2 + i * 2;
                    int switchJumpRomAddress = overlay.ramToRomAddress(switchJumpRamAddress);
                    if (destinations.contains(switchJumpRamAddress) || seen.contains(switchJumpRamAddress))
                        break;

                    // Check to see if we have a queued
                    boolean foundMatchingDestination = false;
                    for (TraversalContext queuedContext : queue) {
                        if (queuedContext.ramAddress == switchJumpRamAddress) {
                            foundMatchingDestination = true;
                            break;
                        }
                    }
                    if (foundMatchingDestination)
                        break;

                    int switchJump = overlay.readUnsignedHalfword(switchJumpRomAddress);
                    int destination = context.ramAddress + switchJump + 4;
                    destinations.add(destination);
                }

                for (int destination : destinations)
                    queue.add(new TraversalContext(destination));

                continue;
            }

            // pop (with PC)
            if ((context.instruction & 0xFF00) == 0xBD00) {
                continue;
            }

            // branch exchange
            if ((context.instruction & 0xFF80) == 0x4700) {
                continue;
            }

            context.ramAddress += 2;
            queue.add(context);
        }
    }

    private int getLineByteLength(String line) {
        line = stripComment(line).trim();

        if (line.isEmpty())
            return 0;

        if (line.endsWith(":"))
            return 0;

        if (line.toUpperCase().startsWith("#DEFINE "))
            return 0;

        if (line.toUpperCase().startsWith("#CASE "))
            return 2;

        line = line.toLowerCase();

        if (line.startsWith("bl ") || line.startsWith("blx "))
            return 4;

        if (line.startsWith("dcb "))
            return 1;

        if (line.startsWith("dcw "))
            return 2;

        if (line.startsWith("dcd "))
            return 4;

        return 2;
    }

    private String stripComment(String line) {
        int commentStartIndex = line.indexOf(';');
        if (commentStartIndex > -1) {
            line = line.substring(0, commentStartIndex);
        }
        return line;
    }

    private byte[] parseInstruction(String op, String args) throws DataFormatException {
        return switch (op) {
            case "adc" -> format4(args.split(","), 5);
            case "add" -> parseAdd(args);
            case "and" -> format4(args.split(","), 0);
            case "asr" -> parseAsr(args);
            case "b" -> format18(args.split(","));
            case "bic" -> format4(args.split(","), 14);
            case "bl" -> format19(args.split(","), false);
            case "blx" -> format19(args.split(","), true);
            case "bx" -> format5(args.split(","), 3);
            case "cmn" -> format4(args.split(","), 11);
            case "cmp" -> parseCmp(args);
            case "eor" -> format4(args.split(","), 1);
            case "ldmia", "stmia" -> parseLdmiaStmia(args, op);
            case "ldr", "str" -> parseLdrStr(args, op);
            case "ldrb", "strb" -> parseLdrbStrb(args, op);
            case "ldrh", "strh" -> parseLdrhStrh(args, op);
            case "ldsb" -> parseLdsb(args);
            case "ldsh" -> parseLdsh(args);
            case "lsl" -> parseLslLsr(args, 0);
            case "lsr" -> parseLslLsr(args, 1);
            case "mov" -> parseMov(args);
            case "mul" -> format4(args.split(","), 13);
            case "mvn" -> format4(args.split(","), 15);
            case "neg" -> format4(args.split(","), 9);
            case "orr" -> format4(args.split(","), 12);
            case "pop", "push" -> parsePopPush(args, op);
            case "ror" -> format4(args.split(","), 7);
            case "sbc" -> format4(args.split(","), 6);
            case "swi" -> format17(args.split(","));
            case "sub" -> parseSub(args);
            case "tst" -> format4(args.split(","), 8);

            // Conditional Branching
            case "beq" -> format16(args.split(","), 0);
            case "bne" -> format16(args.split(","), 1);
            case "bcs" -> format16(args.split(","), 2);
            case "bcc" -> format16(args.split(","), 3);
            case "bmi" -> format16(args.split(","), 4);
            case "bpl" -> format16(args.split(","), 5);
            case "bvs" -> format16(args.split(","), 6);
            case "bvc" -> format16(args.split(","), 7);
            case "bhi" -> format16(args.split(","), 8);
            case "bls" -> format16(args.split(","), 9);
            case "bge" -> format16(args.split(","), 10);
            case "blt" -> format16(args.split(","), 11);
            case "bgt" -> format16(args.split(","), 12);
            case "ble" -> format16(args.split(","), 13);

            // Data
            case "dcb" -> getDataBytes(args, 1, Byte.MIN_VALUE, Byte.MAX_VALUE);
            case "dcw" -> getDataBytes(args, 2, Short.MIN_VALUE, Short.MAX_VALUE);
            case "dcd" -> getDataBytes(args, 4, Integer.MIN_VALUE, Integer.MAX_VALUE);

            default -> new byte[0];
        };

    }

    private byte[] parseAdd(String argsStr) throws DataFormatException {
        // Format 2     (3) ADD Rd, Rs, Rn/#Offset3
        // Format 3     (2) ADD Rd, #Offset8
        // Format 5     (2) ADD Rd/Hd, Rs/Hs
        // Format 12    (3) ADD Rd, PC/SP, #Imm
        // Format 13    (2) ADD SP, #Imm

        String[] args = argsStr.split(",");

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("sp"))
                return format13(args);

            if (args[1].startsWith("#")) {
                if (parseValue(args[1]) <= 7)
                    return format2(new String[]{args[0], args[0], args[1]}, 0);
                return format3(args, 2);
            }

            int rd = parseRegister(args[0]);
            int rs = parseRegister(args[1]);

            // Prefer format2
            if (rd < 8 && rs < 8)
                return format2(new String[]{args[0], args[1], args[0]}, 0);

            return format5(args, 0);
        }

        if (args.length == 3) {
            if (args[1].equalsIgnoreCase("pc") || args[1].equalsIgnoreCase("sp"))
                return format12(args);

            return format2(args, 0);
        }

        throw new DataFormatException();
    }

    private byte[] parseAsr(String argsStr) throws DataFormatException {
        // Format 1     (3) ASR Rd, Rs, #Offset5
        // Format 4     (2) ASR Rd, Rs

        String[] args = argsStr.split(",");

        if (args.length == 2) {
            if (args[1].startsWith("#"))
                return format1(new String[]{args[0], args[0], args[1]}, 2);

            return format4(args, 4);
        }

        if (args.length == 3)
            return format1(args, 2);

        throw new DataFormatException();
    }

    private byte[] parseCmp(String argsStr) throws DataFormatException {
        // Format 3     (2) CMP Rd, #Offset8        op 1
        // Format 4     (2) CMP Rd, Rs              op 10
        // Format 5     (2) CMP Rd/Hd, Rs/Hs        op 1

        String[] args = argsStr.split(",");

        if (args.length != 2)
            throw new DataFormatException();

        if (args[1].startsWith("#"))
            return format3(args, 1);

        int rd = parseRegister(args[0]);
        int rs = parseRegister(args[1]);

        if (rd > 7 || rs > 7)
            return format5(args, 1);

        return format4(args, 10);
    }

    private byte[] parseLdmiaStmia(String argsStr, String op) throws DataFormatException {
        // Format 15    LDMIA Rb!, { Rlist }

        String[] args = argsStr.split(",");

        if (args.length < 2)
            throw new DataFormatException();

        String rbStr = args[0];
        if (!rbStr.endsWith("!"))
            throw new DataFormatException();

        args[0] = rbStr.substring(0, rbStr.length() - 1);

        String rlistFirst = args[1];
        if (!rlistFirst.startsWith("{"))
            throw new DataFormatException();

        args[1] = rlistFirst.substring(1);

        String rlistLast = args[args.length - 1];
        if (!rlistLast.endsWith("}"))
            throw new DataFormatException();

        args[args.length - 1] = rlistLast.substring(0, rlistLast.length() - 1);

        if (op.equalsIgnoreCase("ldmia"))
            return format15(args, 1);

        if (op.equalsIgnoreCase("stmia"))
            return format15(args, 0);

        throw new DataFormatException();
    }

    private byte[] parseLdrStr(String argsStr, String op) throws DataFormatException {
        // Format 6     LDR Rd, [PC, #Imm]
        // Format 7     LDR Rd, [Rb, Ro]
        // Format 9     LDR Rd, [Rb, #Imm]
        // Format 11    LDR Rd, [SP, #Imm]

        String[] args = argsStr.split(",");
        if (args.length == 2) {

            // Hack to allow "LDR Rd, =Number" to use "LDR Rd, [PC, #NumberAddressOffset]"
            if (args[1].startsWith("=")) {
                int imm = parseValue(args[1]);
                if (!dataRamAddressMap.containsKey(imm)) {
                    int dataAddress = alignNextWord(initialRamAddress + size);

                    dataRamAddressMap.put(imm, dataAddress);
                    size = dataAddress - initialRamAddress + 4;
                }

                imm = dataRamAddressMap.get(imm) - alignWord(currentRamAddress) - 4;
                return format6(new String[]{args[0], "PC", "#" + imm});
            }

            args = new String[]{args[0], args[1].substring(0, args[1].length() - 1), "#0]"};
        }

        if (args.length != 3)
            throw new DataFormatException();

        if (!args[1].startsWith("["))
            throw new DataFormatException();

        if (!args[2].endsWith("]"))
            throw new DataFormatException();

        args[1] = args[1].substring(1);
        args[2] = args[2].substring(0, args[2].length() - 1);

        if (op.equalsIgnoreCase("ldr") && args[1].equalsIgnoreCase("pc"))
            return format6(args);

        int loadStoreBit = -1;

        if (op.equalsIgnoreCase("ldr"))
            loadStoreBit = 1;
        if (op.equalsIgnoreCase("str"))
            loadStoreBit = 0;

        if (loadStoreBit == -1)
            throw new DataFormatException();

        if (args[1].equalsIgnoreCase("sp"))
            return format11(args, loadStoreBit);

        if (args[2].startsWith("#"))
            return format9(args, loadStoreBit, 0);

        return format7(args, loadStoreBit, 0);
    }

    private byte[] parseLdrbStrb(String argsStr, String op) throws DataFormatException {
        // Format 7     LDRB Rd, [Rb, Ro]
        // Format 9     LDRB Rd, [Rb, #Imm]

        String[] args = argsStr.split(",");
        if (args.length == 2)
            args = new String[]{args[0], args[1].substring(0, args[1].length() - 1), "#0]"};

        if (args.length != 3)
            throw new DataFormatException();

        if (!args[1].startsWith("["))
            throw new DataFormatException();

        if (!args[2].endsWith("]"))
            throw new DataFormatException();

        args[1] = args[1].substring(1);
        args[2] = args[2].substring(0, args[2].length() - 1);

        int loadStoreFlag = -1;

        if (op.equalsIgnoreCase("ldrb"))
            loadStoreFlag = 1;
        if (op.equalsIgnoreCase("strb"))
            loadStoreFlag = 0;

        if (loadStoreFlag == -1)
            throw new DataFormatException();

        if (args[2].startsWith("#"))
            return format9(args, loadStoreFlag, 1);

        return format7(args, loadStoreFlag, 1);
    }

    private byte[] parseLdrhStrh(String argsStr, String op) throws DataFormatException {
        // Format 8     LDRH Rd, [Rb, Ro]
        // Format 10    LDRH Rd, [Rb, #Imm]

        String[] args = argsStr.split(",");
        if (args.length == 2)
            args = new String[]{args[0], args[1].substring(0, args[1].length() - 1), "#0]"};

        if (args.length != 3)
            throw new DataFormatException();

        if (!args[1].startsWith("["))
            throw new DataFormatException();

        if (!args[2].endsWith("]"))
            throw new DataFormatException();

        args[1] = args[1].substring(1);
        args[2] = args[2].substring(0, args[2].length() - 1);

        int loadStoreFlag = -1;

        if (op.equalsIgnoreCase("ldrh"))
            loadStoreFlag = 1;
        if (op.equalsIgnoreCase("strh"))
            loadStoreFlag = 0;

        if (args[2].startsWith("#"))
            return format10(args, loadStoreFlag);

        return format8(args, 0, loadStoreFlag);
    }

    private byte[] parseLdsb(String argsStr) throws DataFormatException {
        // Format 8     LDSB Rd, [Rb, Ro]

        String[] args = argsStr.split(",");
        if (args.length != 3)
            throw new DataFormatException();

        if (!args[1].startsWith("["))
            throw new DataFormatException();

        if (!args[2].endsWith("]"))
            throw new DataFormatException();

        args[1] = args[1].substring(1);
        args[2] = args[2].substring(0, args[2].length() - 1);

        return format8(args, 1, 0);
    }

    private byte[] parseLdsh(String argsStr) throws DataFormatException {
        // Format 8     LDSH Rd, [Rb, Ro]

        String[] args = argsStr.split(",");
        if (args.length != 3)
            throw new DataFormatException();

        if (!args[1].startsWith("["))
            throw new DataFormatException();

        if (!args[2].endsWith("]"))
            throw new DataFormatException();

        args[1] = args[1].substring(1);
        args[2] = args[2].substring(0, args[2].length() - 1);

        return format8(args, 1, 1);
    }

    private byte[] parseLslLsr(String argsStr, int isRightShift) throws DataFormatException {
        // Format 1     (3) LSL/LSR Rd, Rs, #Offset5 or... LSL/LSR Rd, #Offset5
        // Format 4     (2) LSL/LSR Rd, Rs

        String[] args = argsStr.split(",");

        if (args.length == 2) {
            if (args[1].startsWith("#"))
                return format1(new String[]{args[0], args[0], args[1]}, isRightShift);

            return format4(args, 2 + isRightShift);
        }

        if (args.length == 3)
            return format1(args, isRightShift);

        throw new DataFormatException();
    }

    private byte[] parseMov(String argsStr) throws DataFormatException {
        // Format 3     (2) MOV Rd, #Offset8
        // Format 5     (2) MOV Rd, Hs

        String[] args = argsStr.split(",");
        if (args.length != 2)
            throw new DataFormatException();

        if (args[1].startsWith("#"))
            return format3(args, 0);

        // Hack to allow "MOV Rd, Rs" to become "ADD Rd, Rs, #0" internally; Format 2 with "ADD" opcode (0)
        // Only used when both registers are low, otherwise use Format 5
        int rd = parseRegister(args[0]);
        int rs = parseRegister(args[1]);
        if (rd <= 7 && rs <= 7) {
            args = new String[]{args[0], args[1], "#0"};
            return format2(args, 0);
        }

        if (rd <= 7 && (rs == 13 || rs == 15)) {
            args = new String[]{args[0], args[1], "#0"};
            return format12(args);
        }

        return format5(args, 2);
    }

    private byte[] parsePopPush(String argsStr, String op) throws DataFormatException {
        // Format 14    PUSH/POP {Rlist}

        if (!argsStr.startsWith("{") || !argsStr.endsWith("}"))
            throw new DataFormatException();

        String[] args = argsStr.substring(1, argsStr.length() - 1).split(",");

        return format14(args, op);
    }

    private byte[] parseSub(String argsStr) throws DataFormatException {
        // Format 2     (3) SUB Rd, Rs, Rn/#Offset3
        // Format 3     (2) SUB Rd, #Offset8
        // Format 13    (2) SUB SP, #Imm

        String[] args = argsStr.split(",");

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("sp"))
                return format13(new String[]{args[0], "#-(" + args[1].substring(1) + ")"}); // flip

            if (args[1].startsWith("#"))
                return format3(args, 3);

            return format2(new String[]{args[0], args[0], args[1]}, 1);
        }

        if (args.length == 3)
            return format2(args, 1);

        throw new DataFormatException();
    }

    private void throwParseError(int formatNumber, String op, String[] args, String message) {
        throw new IllegalArgumentException(String.format("Parse Error - Format %d: %s %s; %s", formatNumber, op, String.join(" ", args), message));
    }

    private void assertRegisterRange(int formatNumber, String op, String[] args, String register, String registerName, int registerValue, int registerValueMin, int registerValueMax) {
        if (registerValue < registerValueMin || registerValue > registerValueMax)
            throwParseError(formatNumber, op, args, String.format("%s (%s) must be in range of r%d-r%d", register, registerName, registerValueMin, registerValueMax));
    }

    private void assertImmediateRange(int formatNumber, String op, String[] args, int immediateValue, int immediateValueMin, int immediateValueMax) {
        if (immediateValue < immediateValueMin || immediateValue > immediateValueMax)
            throwParseError(formatNumber, op, args, String.format("imm (%d) must be in range of %d-%d", immediateValue, immediateValueMin, immediateValueMax));
    }

    // Format 1: move shifted register
    private byte[] format1(String[] args, int opcode) throws DataFormatException {
        if (opcode < 0 || opcode > 2)
            throw new DataFormatException();

        int rd = parseRegister(args[0]);
        int rs = parseRegister(args[1]);
        int imm = parseValue(args[2]);

        if (rd < 0 || rd > 7 || rs < 0 || rs > 7 || imm < 0 || imm > 31)
            throw new DataFormatException();

        return halfwordToBytes(rd | (rs << 3) | (imm << 6) | (opcode << 11));
    }

    // Format 2: add/subtract
    private byte[] format2(String[] args, int op) throws DataFormatException {
        int rd = parseRegister(args[0]);
        int rs = parseRegister(args[1]);

        boolean isImmediate = args[2].startsWith("#");
        int rnOrImm = isImmediate ? parseValue(args[2]) : parseRegister(args[2]);

        String opName = op == 0 ? "add" : "sub";
        assertRegisterRange(2, opName, args, "rd", args[0], rd, 0, 7);
        assertRegisterRange(2, opName, args, "rs", args[1], rs, 0, 7);
        if (isImmediate)
            assertImmediateRange(2, opName, args, rnOrImm, 0, 7);
        else
            assertRegisterRange(2, opName, args, "rn", args[2], rnOrImm, 0, 7);

        return halfwordToBytes(rd | (rs << 3) | (rnOrImm << 6) | (op << 9) | ((isImmediate ? 1 : 0) << 10) | 0x1800);
    }

    // Format 3: move/compare/add/subtract immediate
    private byte[] format3(String[] args, int op) throws DataFormatException {
        if (op < 0 || op > 3)
            throw new DataFormatException();

        int rd = parseRegister(args[0]);
        int imm = parseValue(args[1]);

        if (rd < 0 || rd > 7)
            throw new DataFormatException();

        // Hack to allow "MOV Rd, #BigNum" to use "LDR Rd, [PC, #BigNumAddressOffset]" 
        if (imm < 0 || imm > 255) {
            // 0 when word-aligned, 1 when not word-aligned (but still halfword-aligned)
            if (!dataRamAddressMap.containsKey(imm)) {
                int dataAddress = alignWord(initialRamAddress + size + 3);

                dataRamAddressMap.put(imm, dataAddress);
                size = dataAddress - initialRamAddress + 4;
            }

            imm = dataRamAddressMap.get(imm) - alignWord(currentRamAddress) - 4;
            return format6(new String[]{args[0], "PC", "#" + imm});
        }

        return halfwordToBytes(imm | (rd << 8) | (op << 11) | 0x2000);
    }

    // Format 4: ALU operations
    private byte[] format4(String[] args, int op) throws DataFormatException {        
        if (args.length != 2)
            throw new DataFormatException();

        if (op < 0 || op > 15)
            throw new DataFormatException();

        int rd = parseRegister(args[0]);
        int rs = parseRegister(args[1]);

        if (rd < 0 || rd > 7 || rs < 0 || rs > 7)
            throw new DataFormatException();

        return halfwordToBytes(rd | (rs << 3) | (op << 6) | 0x4000);
    }

    // Format 5: Hi register operations/branch exchange
    private byte[] format5(String[] args, int op) throws DataFormatException {
        if (op < 0 || op > 3)
            throw new DataFormatException();

        // branch exchange
        if (args.length == 1) {
            int rs = parseRegister(args[0]);
            if (rs < 0 || rs > 15)
                throw new DataFormatException();

            return halfwordToBytes(rs << 3 | 0x4700);
        }

        int rd = parseRegister(args[0]);
        int rs = parseRegister(args[1]);

        if (rd < 0 || rd > 15 || rs < 0 || rs > 15)
            throw new DataFormatException();

        int h1 = rd > 7 ? 1 : 0;
        int h2 = rs > 7 ? 1 : 0;

        rd &= 0x07;
        rs &= 0x07;

        return halfwordToBytes(rd | (rs << 3) | (h2 << 6) | (h1 << 7) | (op << 8) | 0x4400);
    }

    // Format 6: PC-relative load
    private byte[] format6(String[] args) throws DataFormatException {
        int rd = parseRegister(args[0]);
        int imm = parseValue(args[2]);

        if (rd < 0 || rd > 7 || imm < 0 || imm > 1020)
            throw new DataFormatException();

        if (!isWordAligned(imm))
            throw new DataFormatException("Immediate must be word-aligned.");

        imm = (imm >> 2) & 0xFF;

        return halfwordToBytes(imm | (rd << 8) | 0x4800);
    }

    // Format 7: load/store with register offset
    private byte[] format7(String[] args, int loadStoreFlag, int byteWordFlag) throws DataFormatException {
        if (loadStoreFlag < 0 || loadStoreFlag > 1 || byteWordFlag < 0 || byteWordFlag > 1)
            throw new DataFormatException();

        int rd = parseRegister(args[0]);
        int rb = parseRegister(args[1]);
        int ro = parseRegister(args[2]);

        if (rd < 0 || rd > 7 || rb < 0 || rb > 7 || ro < 0 || ro > 7)
            throw new DataFormatException();

        return halfwordToBytes(rd | (rb << 3) | (ro << 6) | (byteWordFlag << 10) | (loadStoreFlag << 11) | 0x5000);
    }

    // Format 8: load/store sign-extended byte/halfword
    private byte[] format8(String[] args, int signExtendedFlag, int hFlag) throws DataFormatException {
        if (signExtendedFlag < 0 || signExtendedFlag > 1 || hFlag < 0 || hFlag > 1)
            throw new DataFormatException();

        int rd = parseRegister(args[0]);
        int rb = parseRegister(args[1]);
        int ro = parseRegister(args[2]);

        if (rd < 0 || rd > 7 || rb < 0 || rb > 7 || ro < 0 || ro > 7)
            throw new DataFormatException();

        return halfwordToBytes(rd | (rb << 3) | (ro << 6) | (signExtendedFlag << 10) | (hFlag << 11) | 0x5200);
    }

    // Format 9: load/store with immediate offset
    private byte[] format9(String[] args, int loadStoreFlag, int byteWordFlag) throws DataFormatException {
        if (loadStoreFlag < 0 || loadStoreFlag > 1 || byteWordFlag < 0 || byteWordFlag > 1)
            throw new DataFormatException();

        int rd = parseRegister(args[0]);
        int rb = parseRegister(args[1]);
        int imm = parseValue(args[2]);
        if (rd < 0 || rd > 7 || rb < 0 || rb > 7 || imm < 0)
            throw new DataFormatException();

        if (byteWordFlag == 0) {
            if (!isWordAligned(imm))
                throw new DataFormatException("Immediate must be word-aligned for STR and LDR.");

            imm >>= 2;
        }

        if (imm > 31)
            throw new DataFormatException();

        return halfwordToBytes(rd | (rb << 3) | (imm << 6) | (loadStoreFlag << 11) | (byteWordFlag << 12) | 0x6000);
    }

    // Format 10: load/store halfword
    private byte[] format10(String[] args, int loadStoreBit) throws DataFormatException {
        if (loadStoreBit < 0 || loadStoreBit > 1)
            throw new DataFormatException();

        int rd = parseRegister(args[0]);
        int rb = parseRegister(args[1]);
        int imm = parseValue(args[2]);
        if (rd < 0 || rd > 7 || rb < 0 || rb > 7 || imm < 0 || imm > 62)
            throw new DataFormatException();

        if (!isHalfwordAligned(imm))
            throw new DataFormatException("Immediate must be halfword-aligned.");

        imm >>= 1;

        return halfwordToBytes(rd | (rb << 3) | (imm << 6) | (loadStoreBit << 11) | 0x8000);
    }

    // Format 11: SP-relative load/store
    private byte[] format11(String[] args, int loadStoreBit) throws DataFormatException {
        if (loadStoreBit < 0 || loadStoreBit > 1)
            throw new DataFormatException();

        int rd = parseRegister(args[0]);
        int imm = parseValue(args[2]);
        if (rd < 0 || rd > 7 || imm < 0 || imm > 1020)
            throw new DataFormatException();

        if (!isWordAligned(imm))
            throw new DataFormatException("Immediate must be word-aligned.");

        imm >>= 2;

        return halfwordToBytes(imm | (rd << 8) | (loadStoreBit << 11) | 0x9000);
    }

    // Format 12: load address
    private byte[] format12(String[] args) throws DataFormatException {
        int rd = parseRegister(args[0]);
        int source = switch (parseRegister(args[1])) {
            case 13 -> 1;
            case 15 -> 0;
            default -> throw new DataFormatException();
        };
        int imm = parseValue(args[2]);
        if (rd < 0 || rd > 7 || imm < 0 || imm > 1020)
            throw new DataFormatException();

        if (!isWordAligned(imm))
            throw new DataFormatException("Immediate must be word-aligned.");

        imm >>= 2;

        return halfwordToBytes(imm | (rd << 8) | (source << 11) | 0xA000);
    }

    // Format 13: add offset to Stack Pointer
    private byte[] format13(String[] args) throws DataFormatException {
        int imm = parseValue(args[1]);
        if (imm < -508 || imm > 508)
            throw new DataFormatException();

        int signFlag = imm < 0 ? 1 : 0;
        imm = Math.abs(imm);
        if (!isWordAligned(imm))
            throw new DataFormatException("Immediate must be word-aligned.");

        imm >>= 2;

        return halfwordToBytes(imm | (signFlag << 7) | 0xB000);
    }

    // Format 14: push/pop registers
    private byte[] format14(String[] args, String op) throws DataFormatException {
        int rlist = 0;
        int pclrBit = 0;
        for (String arg : args) {
            if (arg.contains("-")) {
                String[] registers = arg.split("-");
                int rStart = parseRegister(registers[0]);
                int rEnd = parseRegister(registers[1]);

                if (rStart < 0 || rStart > 7 || rEnd < 0 || rEnd > 7)
                    throw new DataFormatException();

                for (int i = rStart; i <= rEnd; ++i) {
                    rlist |= 1 << i;
                }

                continue;
            }

            int register = parseRegister(arg);
            if (register < 0)
                throw new DataFormatException();

            if (register <= 7) {
                rlist |= 1 << register;
                continue;
            }

            if (op.equalsIgnoreCase("push") && arg.equalsIgnoreCase("lr")) {
                pclrBit = 1;
                continue;
            }

            if (op.equalsIgnoreCase("pop") && arg.equalsIgnoreCase("pc")) {
                pclrBit = 1;
                continue;
            }

            throw new DataFormatException();
        }

        int loadStoreBit = -1;

        if (op.equalsIgnoreCase("push"))
            loadStoreBit = 0;
        if (op.equalsIgnoreCase("pop"))
            loadStoreBit = 1;

        if (loadStoreBit == -1)
            throw new DataFormatException();

        return halfwordToBytes(rlist | (pclrBit << 8) | (loadStoreBit << 11) | 0xB400);
    }

    // Format 15: multiple load/store
    private byte[] format15(String[] args, int loadStoreBit) throws DataFormatException {
        if (loadStoreBit < 0 || loadStoreBit > 1)
            throw new DataFormatException();

        int rb = parseRegister(args[0]);
        if (rb < 0 || rb > 7)
            throw new DataFormatException();

        int rlist = 0;
        for (int i = 1; i < args.length; ++i) {
            if (args[i].contains("-")) {
                String[] registers = args[i].split("-");
                int rStart = parseRegister(registers[0]);
                int rEnd = parseRegister(registers[1]);

                if (rStart < 0 || rStart > 7 || rEnd < 0 || rEnd > 7)
                    throw new DataFormatException();

                for (int j = rStart; j <= rEnd; ++j) {
                    rlist |= 1 << j;
                }

                continue;
            }

            int register = parseRegister(args[i]);
            if (register < 0 || register > 7)
                throw new DataFormatException();

            rlist |= 1 << register;
        }

        return halfwordToBytes(rlist | (rb << 8) | (loadStoreBit << 11) | 0xC000);
    }

    // Format 16: conditional branch
    private byte[] format16(String[] args, int condition) throws DataFormatException {
        if (condition < 0 || condition > 13)
            throw new DataFormatException();

        if (!labelAddressMap.containsKey(args[0]))
            throw new DataFormatException(String.format("Could not find label \"%s\"", args[0]));

        int jumpAddress = labelAddressMap.get(args[0]);
        int offset = jumpAddress - currentRamAddress - 4;
        if (offset < -256 || offset > 255)
            throw new DataFormatException("Offset is too large");

        offset = (offset >> 1) & 0xFF;

        return halfwordToBytes(offset | (condition << 8) | 0xD000);
    }

    // Format 17: software interrupt
    private byte[] format17(String[] args) throws DataFormatException {
        int value = Integer.parseInt(args[0]);
        if (value < 0 || value > 255)
            throw new DataFormatException();

        return halfwordToBytes(value | 0xDF00);
    }

    // Format 18: unconditional branch
    private byte[] format18(String[] args) throws DataFormatException {
        if (args.length != 1)
            throw new DataFormatException();

        if (!labelAddressMap.containsKey(args[0]))
            throw new DataFormatException();

        int jumpAddress = labelAddressMap.get(args[0]);

        int offset = (jumpAddress - currentRamAddress - 4);
        if (offset < -2048 || offset > 2047)
            throw new DataFormatException("Offset is too large");

        offset = (offset >> 1) & 0x07FF;

        return halfwordToBytes(offset | 0xE000);
    }

    // Format 19: long branch with link
    private byte[] format19(String[] args, boolean exchangeInstructionSet) throws DataFormatException {
        if (globalAddressMap == null || overlay == null) {
            // Expected for getFuncSize only
            return new byte[4];
        }

        int funcRamAddress;
        int targetFunctionEncoding;

        if (args[0].contains("::")) {
            String[] funcStrs = args[0].split("::");
            if (funcStrs.length != 2)
                throw new DataFormatException("Expected Namespace::FuncName format but got " + args[0] + ".");

            String namespace = funcStrs[0];
            String funcName = funcStrs[1];

            globalAddressMap.addReference(namespace, funcName, overlay, currentRamAddress);
            ParagonLiteAddressMap.AddressBase addressBase = globalAddressMap.getAddressData(namespace, funcName);
            if (!(addressBase instanceof ParagonLiteAddressMap.CodeAddress codeAddress))
                throw new DataFormatException();

            funcRamAddress = codeAddress.getRamAddress();
            targetFunctionEncoding = codeAddress.getEncoding();
//            if (targetFunctionEncoding == 2 && exchangeInstructionSet)
//                throw new DataFormatException("BLX was used on a Thumb function");
//            if (targetFunctionEncoding == 4 && !exchangeInstructionSet)
//                throw new DataFormatException("BL was used on an ARM function");

            // TODO: Give proper warning when this was changed
            exchangeInstructionSet = targetFunctionEncoding == 4;

            if (!isWordAligned(funcRamAddress))
                throw new DataFormatException(String.format("Function address of %s (0x%08X) is not word-aligned", args[0], funcRamAddress));
        } else {
            funcRamAddress = parseValue(args[0]);
            targetFunctionEncoding = 2; // TODO: Assume Thumb for now
        }

        int funcOffset = funcRamAddress - ((exchangeInstructionSet ? alignWord(currentRamAddress) : currentRamAddress) + 4);

        int min = 0xFFC00000; // -4194304
        int max = 0x003FFFFF; // +4194303

        if ((funcOffset < min || funcOffset > max) && overlay != null) {
            if (!redirectorAddressMap.containsKey(funcRamAddress)) { // TODO: Clear on begin parse
                int redirectorRomAddress = overlay.allocateRomNear(16, initialRamAddress, size);
                overlay.writeHalfword(redirectorRomAddress, 0x4778); // bx pc
                overlay.writeHalfword(redirectorRomAddress + 0x02, 0x46C0); // nop
                overlay.writeWord(redirectorRomAddress + 0x04, 0xE59FC000, false); // ldr r12, =(address+1 or address+0)
                overlay.writeWord(redirectorRomAddress + 0x08, 0xE12FFF1C, false); // bx r12
                overlay.writeWord(redirectorRomAddress + 0x0C, funcRamAddress + (targetFunctionEncoding == 4 ? 0 : 1), true);

                int redirectorRamAddress = overlay.romToRamAddress(redirectorRomAddress);
                redirectorAddressMap.put(funcRamAddress, redirectorRamAddress);
            }

            funcRamAddress = redirectorAddressMap.get(funcRamAddress);
            exchangeInstructionSet = false;
            funcOffset = funcRamAddress - (currentRamAddress + 4);
        }

        if (funcOffset < min || funcOffset > max)
            throw new DataFormatException();

        int offsetHigh = (funcOffset >> 12) & 0x07FF;
        int offsetLow = (funcOffset >> 1) & 0x07FF;

        int instruction1 = offsetHigh | 0xF000;
        int instruction2 = offsetLow | (exchangeInstructionSet ? 0xE800 : 0xF800);

        return new byte[]{
                (byte) (instruction1 & 0xFF),
                (byte) ((instruction1 >> 8) & 0xFF),
                (byte) (instruction2 & 0xFF),
                (byte) ((instruction2 >> 8) & 0xFF)
        };
    }

    // Data
    private byte[] getDataBytes(String valueStr, int size, int minValue, int maxValue) throws DataFormatException {
        int value = parseValue(valueStr);
        if (value < minValue || value > maxValue)
            throw new DataFormatException("Value " + value + " was outside of range.");

        if (size == 1)
            return new byte[]{(byte) (value & 0xFF)};

        if (size == 2)
            return new byte[]{
                    (byte) (value & 0xFF),
                    (byte) ((value >> 8) & 0xFF),
            };

        if (size == 4)
            return new byte[]{
                    (byte) (value & 0xFF),
                    (byte) ((value >> 8) & 0xFF),
                    (byte) ((value >> 16) & 0xFF),
                    (byte) ((value >> 24) & 0xFF),
            };

        throw new DataFormatException();
    }

    private int parseRegister(String registerStr) {
        return switch (registerStr.toLowerCase()) {
            case "r0", "r00" -> 0;
            case "r1", "r01" -> 1;
            case "r2", "r02" -> 2;
            case "r3", "r03" -> 3;
            case "r4", "r04" -> 4;
            case "r5", "r05" -> 5;
            case "r6", "r06" -> 6;
            case "r7", "r07" -> 7;
            case "r8", "r08" -> 8;
            case "r9", "r09", "sb" -> 9;
            case "r10", "sl" -> 10;
            case "r11" -> 11;
            case "r12", "ip" -> 12;
            case "r13", "sp" -> 13;
            case "r14", "lr" -> 14;
            case "r15", "pc" -> 15;
            default -> -1;
        };
    }

    private int parseValue(String valueStr) throws DataFormatException {
        if (valueStr.startsWith("=") || valueStr.startsWith("#")) {
            valueStr = valueStr.substring(1).trim(); // remove = or #
        }

        String fullStr = varDefines + globalAddressMap.applyDefinitions(valueStr);

        try {
            Object evalObj = engine.eval(fullStr);
            if (evalObj instanceof Double)
                return (int) Math.round((double) evalObj);
            return (int) evalObj;
        } catch (ScriptException e) {
            throw new DataFormatException(String.format("Could not parse %s: %s", valueStr, e));
        }
    }

    private byte[] halfwordToBytes(int word) {
        return new byte[]{
                (byte) (word & 0xFF),
                (byte) ((word >> 8) & 0xFF)
        };
    }

    private static int readUnsignedHalfword(byte[] data, int offset) {
        if (offset < 0 || offset + 1 >= data.length)
            throw new RuntimeException();

        return ((data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8));
    }

    private static int readWord(byte[] data, int offset) {
        if (offset + 3 >= data.length)
            throw new RuntimeException();

        return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8) | ((data[offset + 2] & 0xFF) << 16)
                | ((data[offset + 3] & 0xFF) << 24);
    }

    private static boolean isHalfwordAligned(int address) {
        return address % 2 == 0;
    }

    private static boolean isWordAligned(int address) {
        return address % 4 == 0;
    }

    private static int alignWord(int address) {
        return address & 0xFFFFFFFC;
    }

    private static int alignNextWord(int address) {
        return alignWord(address + 3);
    }
}
