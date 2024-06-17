package com.dabomstew.pkrandom.arm;

import com.sun.org.apache.xml.internal.resolver.helpers.Namespaces;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;
import java.util.zip.DataFormatException;

public class ArmParser {
    Map<String, Map<String, Integer>> branchLinkRamAddressMap;
    Map<String, Integer> labelAddressMap = new HashMap<>();
    Map<Integer, Integer> dataRamAddressMap = new HashMap<>();
    int initialRamAddress = 0;
    int currentRamAddress = 0;
    int size = 0;

    public ArmParser() {
        branchLinkRamAddressMap = new HashMap<>();
    }

    public ArmParser(Map<String, Map<String, Integer>> branchLinkRamAddressMap) {
        this.branchLinkRamAddressMap = branchLinkRamAddressMap;
    }

    public byte[] parse(List<String> lines) {
        return parse(lines, 0);
    }

    public byte[] parse(List<String> lines, int inInitialRamOffset) {
        if (!isWordAligned(inInitialRamOffset))
            throw new RuntimeException("Initial RAM offset must be word-aligned.");

        List<Byte> bytes = new ArrayList<>();

        labelAddressMap.clear();
        dataRamAddressMap.clear();

        initialRamAddress = inInitialRamOffset;
        currentRamAddress = initialRamAddress;

        for (String str : lines) {
            str = str.trim();
            if (str.endsWith(":")) {
                // Label
                String labelName = str.substring(0, str.length() - 1).trim();
                labelAddressMap.put(labelName.toLowerCase(), currentRamAddress);
            }

            currentRamAddress += getLineByteLength(str);
        }

        size = alignNextWord(currentRamAddress - initialRamAddress);

        currentRamAddress = initialRamAddress;
        for (String str : lines) {
            int commentStartIndex = str.indexOf(';');
            if (commentStartIndex > -1)
                str = str.substring(0, commentStartIndex);

            str = str.trim().toLowerCase();

            if (str.isEmpty() || str.endsWith(":"))
                continue;

            int firstSpaceIndex = str.indexOf(' ');
            String op = str.substring(0, firstSpaceIndex);
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

        for (Map.Entry<Integer, Integer> entry : dataRamAddressMap.entrySet()) {
            int dataValue = entry.getKey();
            int offset = entry.getValue() - initialRamAddress;

            returnValue[offset] = (byte) (dataValue & 0xFF);
            returnValue[offset + 1] = (byte) ((dataValue >> 8) & 0xFF);
            returnValue[offset + 2] = (byte) ((dataValue >> 16) & 0xFF);
            returnValue[offset + 3] = (byte) ((dataValue >> 24) & 0xFF);
        }

        return returnValue;
    }

    public int getByteLength(List<String> lines) {
        byte[] bytes = parse(lines);
        return bytes.length;
    }

    public int getFuncSize(byte[] bytes, int startingOffset) throws DataFormatException {
        if (!isWordAligned(startingOffset))
            throw new DataFormatException("StartingOffset must be word-aligned.");

        Queue<Integer> queue = new LinkedList<>();
        queue.add(startingOffset);

        int pushCount = 0;
        int maxOffset = startingOffset;

        Set<Integer> seen = new HashSet<>();

        while (!queue.isEmpty()) {
            int offset = queue.poll();
            if (!seen.add(offset))
                continue;

            maxOffset = Math.max(maxOffset, offset + 2);

            int instruction = readUnsignedHalfword(bytes, offset);

            // conditional branch
            if ((instruction & 0xF000) == 0xD000) {
                if (((instruction >> 8) & 0x0F) > 13)
                    // undefined or swi
                    continue;

                // signed
                int branchOffset = (byte) (instruction & 0x00FF);
                branchOffset = (branchOffset << 1) + 4;

                queue.add(offset + branchOffset); // jump
            }

            // unconditional branch
            if ((instruction & 0xF800) == 0xE000) {
                // two's compliment 12-bit
                int branchOffset = (((instruction & 0x07FF) << 21) >> 20) + 4;

                queue.add(offset + branchOffset);
                continue;
            }

            // PC-relative load
            if ((instruction & 0xF800) == 0x4800) {
                int imm = instruction & 0x00FF;
                int dataOffset = alignWord(offset + ((imm << 2) + 4));
                maxOffset = Math.max(maxOffset, dataOffset + 4);
            }

            // push
            if ((instruction & 0xFE00) == 0xB400) {
                ++pushCount;

                if (maxOffset > offset + 2)
                    throw new DataFormatException("Critical error: found push before max offset");

                if (pushCount > 1) {
                    maxOffset = offset;
                    continue;
                }
            }

            // pop
            if ((instruction & 0xFE00) == 0xBC00) {
                continue;
            }

            // branch exchange
            if ((instruction & 0xFF80) == 0x4700) {
                continue;
            }

            queue.add(offset + 2);
        }

        return alignNextWord(maxOffset) - startingOffset;
    }

    private int getLineByteLength(String line) {
        int commentStartIndex = line.indexOf(';');
        if (commentStartIndex > -1) {
            line = line.substring(0, commentStartIndex);
        }

        line = line.trim().toLowerCase();

        if (line.isEmpty())
            return 0;

        if (line.endsWith(":"))
            return 0;

        if (line.startsWith("bl "))
            return 4;

        if (line.startsWith("dcb "))
            return 1;

        if (line.startsWith("dcd "))
            return 4;

        return 2;
    }

    private byte[] parseInstruction(String op, String args) throws DataFormatException {
        switch (op) {
            case "adc":
                return format4(args.split(","), 5);
            case "add":
                return parseAdd(args);
            case "and":
                return format4(args.split(","), 0);
            case "asr":
                return parseAsr(args);
            case "b":
                return format18(args.split(","));
            case "bic":
                return format4(args.split(","), 14);
            case "bl":
                return format19(args.split(","));
            case "bx":
                return format5(args.split(","), 3);
            case "cmn":
                return format4(args.split(","), 11);
            case "cmp":
                return parseCmp(args);
            case "eor":
                return format4(args.split(","), 1);
            case "ldmia":
            case "stmia":
                return parseLdmiaStmia(args, op);
            case "ldr":
            case "str":
                return parseLdrStr(args, op);
            case "ldrb":
            case "strb":
                return parseLdrbStrb(args, op);
            case "ldrh":
            case "strh":
                return parseLdrhStrh(args, op);
            case "ldsb":
                return parseLdsb(args);
            case "ldsh":
                return parseLdsh(args);
            case "lsl":
                return parseLslLsr(args, 0);
            case "lsr":
                return parseLslLsr(args, 1);
            case "mov":
                return parseMov(args);
            case "mul":
                return format4(args.split(","), 13);
            case "mvn":
                return format4(args.split(","), 15);
            case "neg":
                return format4(args.split(","), 9);
            case "orr":
                return format4(args.split(","), 12);
            case "pop":
            case "push":
                return parsePopPush(args, op);
            case "ror":
                return format4(args.split(","), 7);
            case "sbc":
                return format4(args.split(","), 6);
            case "swi":
                return format17(args.split(","));
            case "sub":
                return parseSub(args);
            case "tst":
                return format4(args.split(","), 8);

            // Conditional Branching
            case "beq":
                return format16(args.split(","), 0);
            case "bne":
                return format16(args.split(","), 1);
            case "bcs":
                return format16(args.split(","), 2);
            case "bcc":
                return format16(args.split(","), 3);
            case "bmi":
                return format16(args.split(","), 4);
            case "bpl":
                return format16(args.split(","), 5);
            case "bvs":
                return format16(args.split(","), 6);
            case "bvc":
                return format16(args.split(","), 7);
            case "bhi":
                return format16(args.split(","), 8);
            case "bls":
                return format16(args.split(","), 9);
            case "bge":
                return format16(args.split(","), 10);
            case "blt":
                return format16(args.split(","), 11);
            case "bgt":
                return format16(args.split(","), 12);
            case "ble":
                return format16(args.split(","), 13);

            // Data
            case "dcb":
                return getDataBytes(args, 1, -0x80, 0xFF);
            case "dcw":
                return getDataBytes(args, 2, -0x8000, 0xFFFF);
            case "dcd":
                return getDataBytes(args, 4, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }

        return new byte[0];
    }

    private byte[] parseAdd(String argsStr) throws DataFormatException {
        // Format 2     (3) ADD Rd, Rs, Rn/#Offset3
        // Format 3     (2) ADD Rd, #Offset8
        // Format 5     (2) ADD Rd/Hd, Rs/Hs
        // Format 12    (3) ADD Rd, PC/SP, #Imm
        // Format 13    (2) ADD SP, #+/-Imm

        String[] args = argsStr.split(",");

        if (args.length == 2) {
            if (args[0].equals("sp"))
                return format13(args);

            if (args[1].startsWith("#"))
                return format3(args, 2);

            return format5(args, 0);
        }

        if (args.length == 3) {
            if (args[1].equals("pc") || args[1].equals("sp"))
                return format12(args);

            return format2(args, 0);
        }

        throw new DataFormatException();
    }

    private byte[] parseAsr(String argsStr) throws DataFormatException {
        // Format 1     (3) ASR Rd, Rs, #Offset5
        // Format 4     (2) ASR Rd, Rs

        String[] args = argsStr.split(",");

        if (args.length == 2)
            return format4(args, 4);

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

        if (op.equals("ldmia"))
            return format15(args, 1);

        if (op.equals("stmia"))
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
                int imm = parseImmediate(args[1]);
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

        if (op.equals("ldr") && args[1].equals("pc"))
            return format6(args);

        int loadStoreBit = -1;

        if (op.equals("ldr"))
            loadStoreBit = 1;
        if (op.equals("str"))
            loadStoreBit = 0;

        if (loadStoreBit == -1)
            throw new DataFormatException();

        if (args[1].equals("sp"))
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

        if (op.equals("ldrb"))
            loadStoreFlag = 1;
        if (op.equals("strb"))
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

        if (op.equals("ldrh"))
            loadStoreFlag = 1;
        if (op.equals("strh"))
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

        return format5(args, 10);
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

        String[] args = argsStr.split(",");

        if (args.length == 2)
            return format3(args, 3);

        if (args.length == 3)
            return format2(args, 1);

        throw new DataFormatException();
    }

    // Format 1: move shifted register
    private byte[] format1(String[] args, int opcode) throws DataFormatException {
        if (opcode < 0 || opcode > 2)
            throw new DataFormatException();

        int rd = parseRegister(args[0]);
        int rs = parseRegister(args[1]);
        int imm = parseImmediate(args[2]);

        if (rd < 0 || rd > 7 || rs < 0 || rs > 7 || imm < 0 || imm > 31)
            throw new DataFormatException();

        return halfwordToBytes(rd | (rs << 3) | (imm << 6) | (opcode << 11));
    }

    // Format 2: add/subtract
    private byte[] format2(String[] args, int op) throws DataFormatException {
        int rd = parseRegister(args[0]);
        int rs = parseRegister(args[1]);

        boolean isImmediate = args[2].startsWith("#");
        int rnOrImm = isImmediate ? parseImmediate(args[2]) : parseRegister(args[2]);

        if (rd < 0 || rd > 7 || rs < 0 || rs > 7 || rnOrImm < 0 || rnOrImm > 7)
            throw new DataFormatException();

        return halfwordToBytes(rd | (rs << 3) | (rnOrImm << 6) | (op << 9) | ((isImmediate ? 1 : 0) << 10) | 0x1800);
    }

    // Format 3: move/compare/add/subtract immediate
    private byte[] format3(String[] args, int op) throws DataFormatException {
        if (op < 0 || op > 3)
            throw new DataFormatException();

        int rd = parseRegister(args[0]);
        int imm = parseImmediate(args[1]);

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
        int imm = parseImmediate(args[2]);

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
        int ro = parseRegister(args[1]);

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
        int imm = parseImmediate(args[2]);
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
        int imm = parseImmediate(args[2]);
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
        int imm = parseImmediate(args[2]);
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
        int source;
        switch (parseRegister(args[1])) {
            case 13:
                source = 1;
                break;
            case 15:
                source = 0;
                break;
            default:
                throw new DataFormatException();
        }
        int imm = parseImmediate(args[2]);
        if (rd < 0 || rd > 7 || imm < 0 || imm > 1020)
            throw new DataFormatException();

        if (!isWordAligned(imm))
            throw new DataFormatException("Immediate must be word-aligned.");

        imm >>= 2;

        return halfwordToBytes(imm | (rd << 8) | (source << 11) | 0xA000);
    }

    // Format 13: add offset to Stack Pointer
    private byte[] format13(String[] args) throws DataFormatException {
        int imm = parseImmediate(args[1]);
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

            if (op.equals("push") && arg.equals("lr")) {
                pclrBit = 1;
                continue;
            }

            if (op.equals("pop") && arg.equals("pc")) {
                pclrBit = 1;
                continue;
            }

            throw new DataFormatException();
        }

        int loadStoreBit = -1;

        if (op.equals("push"))
            loadStoreBit = 0;
        if (op.equals("pop"))
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
            throw new DataFormatException();

        int jumpAddress = labelAddressMap.get(args[0]);
        int offset = jumpAddress - currentRamAddress;

        offset = ((offset - 4) >> 1) & 0xFF;

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
        int offset = ((jumpAddress - currentRamAddress - 4) >> 1) & 0x07FF;

        return halfwordToBytes(offset | 0xE000);
    }

    // Format 19: long branch with link
    private byte[] format19(String[] args) throws DataFormatException {
        if (branchLinkRamAddressMap.isEmpty()) {
            // Expected for getFuncSize only
            return new byte[4];
        }

        String[] funcStrs = args[0].split("::");
        if (funcStrs.length != 2)
            throw new DataFormatException("Expected Namespace::FuncName format but got " + args[0] + ".");

        String namespace = funcStrs[0];
        String funcName = funcStrs[1];

        if (!branchLinkRamAddressMap.containsKey(namespace))
            throw new DataFormatException("Namespace '" + namespace + "' was not found.");

        Map<String, Integer> namespaceFunctions = branchLinkRamAddressMap.get(namespace);
        if (!namespaceFunctions.containsKey(funcName))
            throw new DataFormatException("Function '" + funcName + "' not found in namespace '" + namespace + "'.");

        int funcAddress = namespaceFunctions.get(funcName);
        if (!isWordAligned(funcAddress))
            throw new DataFormatException(String.format("Function address of %s (0x%X) is not word-aligned", args[0], funcAddress));

        funcAddress = funcAddress - currentRamAddress - 4;

        int offsetHigh = (funcAddress >> 12) & 0x07FF;
        int offsetLow = (funcAddress >> 1) & 0x07FF;

        int instruction1 = offsetHigh | 0xF000;
        int instruction2 = offsetLow | 0xF800;

        return new byte[]{
                (byte) (instruction1 & 0xFF),
                (byte) ((instruction1 >> 8) & 0xFF),
                (byte) (instruction2 & 0xFF),
                (byte) ((instruction2 >> 8) & 0xFF)
        };
    }

    // Data
    private byte[] getDataBytes(String valueStr, int size, int minValue, int maxValue) throws DataFormatException {
        int value;
        if (valueStr.startsWith("0x"))
            value = Integer.parseInt(valueStr.substring(2), 16);
        else
            value = Integer.parseInt(valueStr);

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
        switch (registerStr) {
            case "r0":
                return 0;
            case "r1":
                return 1;
            case "r2":
                return 2;
            case "r3":
                return 3;
            case "r4":
                return 4;
            case "r5":
                return 5;
            case "r6":
                return 6;
            case "r7":
                return 7;
            case "r8":
                return 8;
            case "r9":
            case "sb":
                return 9;
            case "r10":
            case "sl":
                return 10;
            case "r11":
                return 11;
            case "r12":
            case "ip":
                return 12;
            case "r13":
            case "sp":
                return 13;
            case "r14":
            case "lr":
                return 14;
            case "r15":
            case "pc":
                return 15;
            default:
                return -1;
        }
    }

    private int parseImmediate(String immediateStr) throws DataFormatException {
        if (immediateStr.startsWith("=") && immediateStr.contains("::")) {
            immediateStr = immediateStr.substring(1);
            String[] strs = immediateStr.split("::", 2);

            Set<String> namespaces = branchLinkRamAddressMap.keySet();
            String namespace = null;
            for (String thisNamespace : namespaces) {
                if (strs[0].endsWith(thisNamespace)) {
                    int strLen = thisNamespace.length();
                    namespace = thisNamespace;
                    break;
                }
            }

            if (namespace == null)
                throw new DataFormatException(String.format("Could not parse %s", immediateStr));

            Map<String, Integer> namespaceFuncs = branchLinkRamAddressMap.get(namespace);
            Set<String> funcNames = namespaceFuncs.keySet();
            String funcName = null;
            for (String thisFuncName : funcNames) {
                if (strs[1].startsWith(thisFuncName)) {
                    funcName = thisFuncName;
                    break;
                }
            }

            if (funcName == null)
                throw new DataFormatException(String.format("Could not parse %s", immediateStr));

            String fullFuncName = String.format("%s::%s", namespace, funcName);
            int funcAddress = namespaceFuncs.get(funcName);
            immediateStr = immediateStr.replace(fullFuncName, Integer.toString(funcAddress));

            ScriptEngineManager mgr = new ScriptEngineManager();
            ScriptEngine engine = mgr.getEngineByName("JavaScript");
            try {
                Object evalObj = engine.eval(immediateStr);
                return (Integer) evalObj;
            } catch (ScriptException e) {
                throw new DataFormatException(String.format("Could not parse %s", immediateStr));
            }
        }

        if (immediateStr.startsWith("=0x") || immediateStr.startsWith("#0x"))
            return Integer.parseInt(immediateStr.substring(3), 16);

        if (immediateStr.startsWith("=-0x") || immediateStr.startsWith("#-0x"))
            return -Integer.parseInt(immediateStr.substring(4), 16);

        try {
            return Integer.parseInt(immediateStr.substring(1));
        } catch (NumberFormatException e) {
            throw new DataFormatException();
        }
    }

    private byte[] halfwordToBytes(int word) {
        return new byte[]{
                (byte) (word & 0xFF),
                (byte) ((word >> 8) & 0xFF)
        };
    }

    private static int readByte(byte[] data, int offset) {
        return data[offset] & 0xFF;
    }

    private static int readUnsignedHalfword(byte[] data, int offset) {
        return ((data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8));
    }

    private static int readSignedHalfword(byte[] data, int offset) {
        return (short) (((data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8)));
    }

    private static int readWord(byte[] data, int offset) {
        return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8) | ((data[offset + 2] & 0xFF) << 16)
                | ((data[offset + 3] & 0xFF) << 24);
    }

    private static boolean isHalfwordAligned(int address) {
        return address % 2 == 0;
    }

    private static boolean isWordAligned(int address) {
        return address % 4 == 0;
    }

    private static int alignHalfword(int address) {
        return address & 0xFFFFFFF7;
    }

    private static int alignWord(int address) {
        return address & 0xFFFFFFFC;
    }

    private static int alignNextWord(int address) {
        return alignWord(address + 3);
    }
}
