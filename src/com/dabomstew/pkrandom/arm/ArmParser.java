package com.dabomstew.pkrandom.arm;

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.arm.exceptions.*;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteAddressMap;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteOverlay;
import org.openjdk.nashorn.api.scripting.JSObject;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.*;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ArmParser {
    ParagonLiteAddressMap globalAddressMap;

    Map<String, Integer> labelAddressMap = new HashMap<>();
    Map<Integer, SortedSet<Integer>> dataRamAddressMap = new TreeMap<>();

    private static class AddressPair {
        ParagonLiteOverlay overlay;
        int address;

        AddressPair(ParagonLiteOverlay overlay, int address) {
            this.overlay = overlay;
            this.address = address;
        }

        @Override
        public int hashCode() {
            return address;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof AddressPair other && overlay == other.overlay && address == other.address;
        }
    }

    Map<Integer, AddressPair> instructionAddressToRedirector = new TreeMap<>();
    List<AddressPair> redirectors = new ArrayList<>();

    ParagonLiteOverlay overlay;
    int initialRamAddress = 0;
    int currentRamAddress = 0;
    int size = 0;
    
    ScriptEngineManager engineManager;
    ScriptEngine engine;
    ScriptContext currentContext;

    public ArmParser() {
        setupJavaScriptEngineManager();
    }

    public ArmParser(ParagonLiteAddressMap globalAddressMap) {
        this.globalAddressMap = globalAddressMap;
        setupJavaScriptEngineManager();
    }

    private void setupJavaScriptEngineManager() {
        engineManager = new ScriptEngineManager();

        engine = engineManager.getEngineByName("nashorn");
        if (engine == null) {
            NashornScriptEngineFactory engineFactory = new NashornScriptEngineFactory();
            engine = engineFactory.getScriptEngine();
        }

        setEnums();
        setStructs();
    }

    private void setEnums() {
        Scanner sc;
        try {
            InputStream stream = FileFunctions.openConfig("paragonlite/enums.ini");
            sc = new Scanner(stream, StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (!line.contains("="))
                continue;

            String[] keyVal = line.split("=", 2);
            String key = keyVal[0].trim();
            String valStr = keyVal[1].trim();
            int val = parseInt(valStr);

            engineManager.put(key, val);
        }
    }

    private void setStructs() {
        String[] structNames = new String[]{
                "ActionOrderWork",
                "BattleField",
                "BattleParty",
                "Box2Main",
                "BoxPoke",
                "BoxPokeBlockA",
                "BoxPokeBlockB",
                "BoxPokeBlockC",
                "BoxPokeBlockD",
                "BtlClientWk",
                "BtlEventFactor",
                "BtlvMcss",
                "BtlvMcssData",
                "HandlerParam_AddSideStatus",
                "HandlerParam_ChangeHP",
                "HandlerParam_ChangeStatStage",
                "HandlerParam_ChangeTerrain",
                "HandlerParam_ChangeType",
                "HandlerParam_ChangeWeather",
                "HandlerParam_ConsumeItem",
                "HandlerParam_CureCondition",
                "HandlerParam_Damage",
                "HandlerParam_ForceUseItem",
                "HandlerParam_Message",
                "HandlerParam_RecoverHP",
                "HandlerParam_RemoveSideCondition",
                "HandlerParam_SetAnimation",
                "HandlerParam_SetTurnFlag",
                "HandlerParam_SwapItem",
                "LCG_Context",
                "MainModule",
                "MoveParam",
                "PartyPoke",
                "PokeCon",
                "ServerFlow",
                "SideStatus",
                "TrainerAIEnv",
                "TrainerData",
                "TrainerData_Flags",
                "TrainerPoke",
                "TrainerPoke_BasicFlags",
                "TrainerPoke_Header",
                "TrainerPoke_Header_Flags",
                "TrainerPoke_Header_Slot",
                "TrainerPoke_StatModifiers",
                "UnovaLink_MenuParam",
                "UnovaLink_MenuParam_Button",
                "UnovaLink_MenuWork",
                "UnovaLinkWork",
        };

        for (String structName : structNames) {
            String filename = String.format("paragonlite/structs/%s.json", structName);

            Scanner sc;
            try {
                InputStream stream = FileFunctions.openConfig(filename);
                if (stream == null) {
                    throw new RuntimeException(String.format("Could not find %s", filename));
                }
                sc = new Scanner(stream, StandardCharsets.UTF_8);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            StringBuilder stringBuilder = new StringBuilder();
            while (sc.hasNextLine()) {
                stringBuilder.append(sc.nextLine());
            }

            Object object;
            try {
                // Is there a better way to do this?
                object = engine.eval(String.format("function f(){return %s} f()", stringBuilder));
            } catch (ScriptException e) {
                throw new RuntimeException(e);
            }

            engineManager.put(structName, object);
        }
    }

    
    public boolean hasGlobalValue(String name) {
        return engineManager.get(name) != null;
    }
    
    public void addGlobalValue(String name, int value) {
        engineManager.put(name, value);
    }

    public void addGlobalValue(String name, double value) {
        engineManager.put(name, value);
    }

    public void addGlobalValue(String name, boolean value) {        
        engineManager.put(name, value ? 1 : 0);
    }

    public Object getGlobalValue(String name) {
        return engineManager.get(name);
    }

    public void addStruct(String structName) {
        JSObject object;
        try {
            object = (JSObject) engine.eval("Object");
            engineManager.put(structName, object);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public void setStructFieldOffset(String structName, String memberName, int offset) {
        JSObject structObject = (JSObject) engineManager.get(structName);
        structObject.setMember(memberName, offset);
    }

    private int parseInt(String str) {
        if (str.startsWith("0x"))
            return Integer.parseInt(str, 2, str.length(), 16);

        return Integer.parseInt(str);
    }

    enum IfState {
        Processing,
        Idle,
        Complete,
        Invalid
    }

    private static class ParseLine {
        int number;
        String str;

        ParseLine(int number, String str) {
            this.number = number;
            this.str = str;
        }
    }

    public byte[] parse(List<String> lines, ParagonLiteOverlay overlay, int initialRamAddress) throws ArmParseException {
        if (!isWordAligned(initialRamAddress))
            throw new RuntimeException("Initial RAM offset must be word-aligned.");

        List<ParseLine> parseLines = new ArrayList<>(lines.size());
        for (int i = 0; i < lines.size(); ++i)
            parseLines.add(new ParseLine(i, lines.get(i)));

        List<Byte> bytes = new ArrayList<>();

        Object debugGlobalValue = getGlobalValue("DEBUG");
        boolean isDebug = debugGlobalValue instanceof Integer && (int) debugGlobalValue == 1;

        size = 0;
        labelAddressMap.clear();
        dataRamAddressMap.clear();
        instructionAddressToRedirector.clear();
        redirectors.clear();

        this.overlay = overlay;
        this.initialRamAddress = initialRamAddress;
        this.currentRamAddress = initialRamAddress;

        currentContext = new SimpleScriptContext();
        currentContext.setBindings(engineManager.getBindings(), ScriptContext.GLOBAL_SCOPE);

        Stack<IfState> ifStateStack = new Stack<>();

        for (int i = 0; i < parseLines.size(); ++i) {
            int lineNumber = parseLines.get(i).number;
            String str = stripComment(parseLines.get(i).str).trim();
            if (str.toUpperCase().startsWith("#ELSE")) {
                if (ifStateStack.isEmpty())
                    throw new ArmParseException(lineNumber, str, "ELSE used when no IF was stated");

                switch (ifStateStack.pop()) {
                    case Processing, Complete -> ifStateStack.push(IfState.Complete);
                    case Idle -> ifStateStack.push(IfState.Processing);
                    case Invalid -> ifStateStack.push(IfState.Invalid);
                    default -> throw new IllegalStateException("Unexpected value: " + ifStateStack.pop());
                }

                continue;
            } else if (str.toUpperCase().startsWith("#ELIF ")) {
                if (ifStateStack.isEmpty())
                    throw new ArmParseException(lineNumber, str, "ELIF used when no IF was stated");

                String[] elifSplit = str.split(" ", 2);
                String expression = elifSplit[1];
                int expressionValue = parseValue(lineNumber, str, expression);

                switch (ifStateStack.pop()) {
                    case Processing, Complete -> ifStateStack.push(IfState.Complete);
                    case Idle -> ifStateStack.push(expressionValue == 0 ? IfState.Idle : IfState.Processing);
                    case Invalid -> ifStateStack.push(IfState.Invalid);
                    default -> throw new IllegalStateException("Unexpected value: " + ifStateStack.pop());
                }

                continue;
            } else if (str.toUpperCase().startsWith("#ENDIF")) {
                if (ifStateStack.isEmpty())
                    throw new ArmParseException(lineNumber, str, "ENDIF used when no IF was stated");

                ifStateStack.pop();
                continue;
            }

            if (!ifStateStack.isEmpty() && ifStateStack.peek() != IfState.Processing) {
                parseLines.get(i).str = "";
                continue;
            }

            if (str.toUpperCase().startsWith("#IF ")) {
                if (!ifStateStack.isEmpty() && ifStateStack.peek() != IfState.Processing) {
                    ifStateStack.push(IfState.Invalid);
                    continue;
                }

                String[] ifSplit = str.split(" ", 2);
                String expression = ifSplit[1];
                int expressionValue = parseValue(lineNumber, str, expression);

                ifStateStack.push(expressionValue == 0 ? IfState.Idle : IfState.Processing);
                continue;
            }

            if (str.toUpperCase().startsWith("#DEFINE ")) {
                String[] defSplit = str.split(" ", 3);
                String varName = defSplit[1];
                String varValueStr = defSplit[2];
                int varValue = parseValue(lineNumber, str, varValueStr);
                currentContext.setAttribute(varName, varValue, ScriptContext.ENGINE_SCOPE);
            } else if (str.toUpperCase().startsWith("#SWITCH ")) {
                String[] switchSplit = str.split(" ", 3);
                String switchRegister1 = switchSplit[1];
                String switchRegister2 = switchSplit.length == 3 ? switchSplit[2] : switchRegister1;
                List<ParseLine> oldParseLines = parseLines;
                parseLines = new ArrayList<>(parseLines.size() + 5);
                for (int j = 0; j < i; ++j)
                    parseLines.add(oldParseLines.get(j));

                parseLines.add(new ParseLine(lineNumber, String.format("add %s, %s, %s", switchRegister1, switchRegister2, switchRegister2)));
                parseLines.add(new ParseLine(lineNumber, String.format("add %s, pc", switchRegister1)));
                parseLines.add(new ParseLine(lineNumber, String.format("ldrh %s, [%s, #2]", switchRegister1, switchRegister1)));
                parseLines.add(new ParseLine(lineNumber, String.format("add pc, %s", switchRegister1)));
                for (int j = i + 1; j < oldParseLines.size(); ++j)
                    parseLines.add(oldParseLines.get(j));

                --i;
                continue;
            } else if (str.toUpperCase().startsWith("#SWITCH_FULL ")) {
                String[] switchSplit = str.split(" ", 3);
                String switchRegister1 = switchSplit[1];
                String switchRegister2 = switchSplit.length == 3 ? switchSplit[2] : switchRegister1;
                List<ParseLine> oldParseLines = parseLines;
                parseLines = new ArrayList<>(parseLines.size() + 5);
                for (int j = 0; j < i; ++j)
                    parseLines.add(oldParseLines.get(j));

                parseLines.add(new ParseLine(lineNumber, String.format("add %s, %s, %s", switchRegister1, switchRegister2, switchRegister2)));
                parseLines.add(new ParseLine(lineNumber, String.format("add %s, pc", switchRegister1)));
                parseLines.add(new ParseLine(lineNumber, String.format("ldrh %s, [%s, #6]", switchRegister1, switchRegister1)));
                parseLines.add(new ParseLine(lineNumber, String.format("lsl %s, #16", switchRegister1)));
                parseLines.add(new ParseLine(lineNumber, String.format("asr %s, #16", switchRegister1)));
                parseLines.add(new ParseLine(lineNumber, String.format("add pc, %s", switchRegister1)));

                for (int j = i + 1; j < oldParseLines.size(); ++j)
                    parseLines.add(oldParseLines.get(j));

                --i;
                continue;
            } else if (str.toUpperCase().startsWith("#READ_BITS(") || str.toUpperCase().startsWith("#READ_BITS_SIGNED(") && str.endsWith(")")) {
                boolean isSigned = str.toUpperCase().startsWith("#READ_BITS_SIGNED");

                String[] strArgs = str.substring(11, str.length() - 1).split(",");
                if (strArgs.length < 3 || strArgs.length > 4)
                    throw new ArmParseException(lineNumber, str, "incorrect number of arguments for READ_BITS(); expected 3 or 4");

                if (strArgs.length == 3)
                    strArgs = new String[]{strArgs[0], strArgs[0], strArgs[1], strArgs[2]};

                for (int j = 0; j < strArgs.length; ++j)
                    strArgs[j] = strArgs[j].trim();

                int rd = parseRegister(strArgs[0]);
                int rs = parseRegister(strArgs[1]);
                int bitOffset = parseValue(lineNumber, str, strArgs[2]);
                int bitLength = parseValue(lineNumber, str, strArgs[3]);
                int leftShift = 32 - (bitOffset + bitLength);
                int rightShift = 32 - bitLength;

                List<ParseLine> oldParseLines = parseLines;
                int newLines = (leftShift > 0 ? 1 : 0) + (rightShift > 0 ? 1 : 0);
                parseLines = new ArrayList<>(parseLines.size() + newLines);
                for (int j = 0; j < i; ++j)
                    parseLines.add(oldParseLines.get(j));

                String rightShiftOperator = isSigned ? "asr" : "lsr";

                if (leftShift > 0) {
                    parseLines.add(new ParseLine(lineNumber, String.format("lsl r%d, r%d, #%d", rd, rs, leftShift)));

                    if (rightShift > 0)
                        parseLines.add(new ParseLine(lineNumber, String.format("%s r%d, #%d", rightShiftOperator, rd, rightShift)));
                } else if (rightShift > 0) {
                    parseLines.add(new ParseLine(lineNumber, String.format("%s r%d, r%d, #%d", rightShiftOperator, rd, rs, rightShift)));
                }

                for (int j = i + 1; j < oldParseLines.size(); ++j)
                    parseLines.add(oldParseLines.get(j));

                --i;
                continue;
            } else if (str.toUpperCase().startsWith("#PRINTF") && str.endsWith(")")) {
                if (isDebug) {
                    int openParen = str.indexOf('(');
                    String[] printfFullStrArgs = str.substring(openParen + 1, str.length() - 1).split(",");
                    for (int j = 0; j < printfFullStrArgs.length; ++j)
                        printfFullStrArgs[j] = printfFullStrArgs[j].trim();

                    List<String> printfStrArgs = new ArrayList<>(printfFullStrArgs.length);
                    for (String tempPrintStrArg : printfFullStrArgs) {
                        if (!tempPrintStrArg.startsWith("\"") && tempPrintStrArg.indexOf(']') > -1) {
                            int lastIndex = printfStrArgs.size() - 1;
                            String lastArg = printfStrArgs.get(lastIndex);
                            lastArg = String.format("%s, %s", lastArg, tempPrintStrArg);
                            printfStrArgs.set(lastIndex, lastArg);
                            continue;
                        }

                        printfStrArgs.add(tempPrintStrArg);
                    }


                    if (printfStrArgs.isEmpty())
                        throw new ArmParseException(lineNumber, str, "printf did not contain any arguments");

                    printfStrArgs.replaceAll(String::trim);

                    String printStrMsg = printfStrArgs.remove(0);
                    if (!printStrMsg.startsWith("\"") || !printStrMsg.endsWith("\""))
                        throw new ArmParseException(lineNumber, str, "first argument of printf must be a string");

                    printStrMsg = printStrMsg.substring(1, printStrMsg.length() - 1); // remove outer quotes

                    byte[] printStrMsgBytesWithNewLine = new byte[printStrMsg.length() + 2];
                    byte[] rawPrintStrBytes = printStrMsg.getBytes(StandardCharsets.UTF_8);
                    System.arraycopy(rawPrintStrBytes, 0, printStrMsgBytesWithNewLine, 0, rawPrintStrBytes.length);

                    printStrMsgBytesWithNewLine[printStrMsgBytesWithNewLine.length - 2] = '\n';
                    printStrMsgBytesWithNewLine[printStrMsgBytesWithNewLine.length - 1] = '\0';

                    String bufferLabel = "Data_RAM_StrBuf";
                    ParagonLiteAddressMap.AddressBase bufferAddressData = globalAddressMap.getAddressData(overlay, bufferLabel);
                    if (bufferAddressData == null) {
                        overlay.writeData(new byte[256], bufferLabel, "");
                        bufferAddressData = globalAddressMap.getAddressData(overlay, bufferLabel);
                    }

                    String label = String.format("Data_PRINTF_%s", printStrMsg);
                    ParagonLiteAddressMap.AddressBase addressData = globalAddressMap.getAddressData(overlay, label);
                    if (addressData == null) {
                        overlay.writeData(printStrMsgBytesWithNewLine, label, "");
                        addressData = globalAddressMap.getAddressData(overlay, label);
                    }

                    if (printfStrArgs.size() > 32)
                        throw new ArmParseException(lineNumber, str, "Cannot currently support more than 32 args");

                    int printfRegisterArgCount = Math.min(printfStrArgs.size(), 2);
                    int printfPushPopCount = 2 + printfRegisterArgCount;
                    int printfMaxPushPopRegister = printfPushPopCount - 1;
                    int printfPushPopSize = printfPushPopCount * 4;
                    int printfStackCount = Math.max(0, printfStrArgs.size() - 2);

                    List<ParseLine> printfSetArgParseLines = new ArrayList<>();

                    // Check for use of r2 in the second arg (index 1)
                    if (printfStrArgs.size() >= 2 && printfStrArgs.get(1).contains("r2")) {
                        throw new ArmParseException(lineNumber, str, "We currently cannot use r2 in this arg slot");
                    }

                    int printfStackSize = printfStackCount * 4;
                    int printfOldStackOffset = printfPushPopSize + printfStackSize;

                    // do stack params first
                    int[] printfOrderedArgs = new int[printfStrArgs.size()];
                    for (int j = 2; j < printfStrArgs.size(); ++j)
                        printfOrderedArgs[j - 2] = j;
                    for (int j = 0; j < printfRegisterArgCount; ++j)
                        printfOrderedArgs[printfOrderedArgs.length - printfRegisterArgCount + j] = j;

                    for (int j : printfOrderedArgs) {
                        String printfStrArg = printfStrArgs.get(j);
                        int printfArgAsRegister = parseRegister(printfStrArg);
                        if (printfArgAsRegister > -1) {
                            if (j < 2)
                                printfSetArgParseLines.add(new ParseLine(lineNumber, String.format("mov r%d, r%d", j + 2, printfArgAsRegister)));
                            else
                                printfSetArgParseLines.add(new ParseLine(lineNumber, String.format("str r%d, [sp, #%d]", printfArgAsRegister, (j - 2) * 4)));

                            continue;
                        }

                        int openBracketIndex = printfStrArg.indexOf('[');
                        if (openBracketIndex > -1 && printfStrArg.endsWith("]")) {
                            String printfLoadOpCode = printfStrArg.substring(0, openBracketIndex).trim();
                            String printfLoadParams = printfStrArg.substring(openBracketIndex).trim();

                            String printfLoadParamEnd = printfLoadParams.replaceFirst("\\[ *sp *, *#", "");
                            if (!printfLoadParams.equals(printfLoadParamEnd)) {
                                printfLoadParamEnd = printfLoadParamEnd.substring(0, printfLoadParamEnd.length() - 1);
                                printfLoadParams = String.format("[sp, #((%s) + %d)]", printfLoadParamEnd, printfOldStackOffset);
                            }

                            if (j < 2)
                                printfSetArgParseLines.add(new ParseLine(lineNumber, String.format("%s r%d, %s", printfLoadOpCode, j + 2, printfLoadParams)));
                            else {
                                printfSetArgParseLines.add(new ParseLine(lineNumber, String.format("%s r0, %s", printfLoadOpCode, printfLoadParams)));
                                printfSetArgParseLines.add(new ParseLine(lineNumber, String.format("str r0, [sp, #%d]", (j - 2) * 4)));
                            }

                            continue;
                        }

                        int printStrArgValue = parseValue(lineNumber, str, printfStrArg);
                        if (printStrArgValue >= 0 && printStrArgValue <= 255) {
                            if (j < 2)
                                printfSetArgParseLines.add(new ParseLine(lineNumber, String.format("mov r%d, #%d", j + 2, printStrArgValue)));
                            else {
                                printfSetArgParseLines.add(new ParseLine(lineNumber, String.format("mov r0, #%d", printStrArgValue)));
                                printfSetArgParseLines.add(new ParseLine(lineNumber, String.format("str r0, [sp, #%d]", (j - 2) * 4)));
                            }
                            continue;
                        }

                        boolean resolvedCondensedValue = false;
                        for (int shift = 1; shift < 32; ++shift) {
                            int condensedValue = printStrArgValue >>> shift;
                            if (condensedValue << shift != printStrArgValue)
                                break;

                            if (condensedValue > 255)
                                continue;

                            if (j < 2) {
                                printfSetArgParseLines.add(new ParseLine(lineNumber, String.format("mov r%d, #%d", j + 2, condensedValue)));
                                printfSetArgParseLines.add(new ParseLine(lineNumber, String.format("lsl r%d, #%d", j + 2, shift)));
                            } else {
                                printfSetArgParseLines.add(new ParseLine(lineNumber, String.format("mov r0, #%d", condensedValue)));
                                printfSetArgParseLines.add(new ParseLine(lineNumber, String.format("lsl r0, #%d", shift)));
                                printfSetArgParseLines.add(new ParseLine(lineNumber, String.format("str r0, [sp, #%d]", (j - 2) * 4)));
                            }

                            resolvedCondensedValue = true;
                            break;
                        }

                        if (resolvedCondensedValue)
                            continue;

                        if (j < 2)
                            printfSetArgParseLines.add(new ParseLine(lineNumber, String.format("ldr r%d, =%d", j, printStrArgValue)));
                        else {
                            printfSetArgParseLines.add(new ParseLine(lineNumber, String.format("ldr r0, =%d", printStrArgValue)));
                            printfSetArgParseLines.add(new ParseLine(lineNumber, String.format("str r0, [sp, #%d]", (j - 2) * 4)));
                        }
                    }

                    int baseNewLines = 7 + (printfStackCount > 0 ? 2 : 0);
                    List<ParseLine> oldParseLines = parseLines;
                    parseLines = new ArrayList<>(parseLines.size() + baseNewLines + printfSetArgParseLines.size());
                    for (int j = 0; j < i; ++j)
                        parseLines.add(oldParseLines.get(j));

                    parseLines.add(new ParseLine(lineNumber, String.format("push {r0-r%d}", printfMaxPushPopRegister)));
                    if (printfStackCount > 0)
                        parseLines.add(new ParseLine(lineNumber, String.format("sub sp, #%d", printfStackSize)));

                    parseLines.addAll(printfSetArgParseLines);

                    parseLines.add(new ParseLine(lineNumber, String.format("ldr r0, =%d", bufferAddressData.getRamAddress())));
                    parseLines.add(new ParseLine(lineNumber, String.format("ldr r1, =%d", addressData.getRamAddress())));
                    parseLines.add(new ParseLine(lineNumber, "blx ARM9::sprintf"));
                    parseLines.add(new ParseLine(lineNumber, String.format("ldr r0, =%d", bufferAddressData.getRamAddress())));
                    parseLines.add(new ParseLine(lineNumber, "swi 0xFC"));

                    if (printfStackCount > 0)
                        parseLines.add(new ParseLine(lineNumber, String.format("add sp, #%d", printfStackSize)));
                    parseLines.add(new ParseLine(lineNumber, String.format("pop {r0-r%d}", printfMaxPushPopRegister)));

                    for (int j = i + 1; j < oldParseLines.size(); ++j)
                        parseLines.add(oldParseLines.get(j));

                    --i;
                } else {
                    parseLines.get(i).str = "";
                }

                continue;
            } else if (str.endsWith(":")) {
                // Label
                String labelName = str.substring(0, str.length() - 1).trim();
                labelAddressMap.put(labelName, currentRamAddress);
            }

            currentRamAddress += getLineByteLength(lineNumber, str);
        }

        if (!ifStateStack.isEmpty())
            throw new ArmParseException(parseLines.size(), "", "No #ENDIF found");

        int lastNonSwitchAddress = initialRamAddress;
        currentRamAddress = initialRamAddress;
        for (ParseLine parseLine : parseLines) {
            int lineNumber = parseLine.number;
            String str = parseLine.str;
            if (str == null)
                continue;

            str = stripComment(str).trim();
            if (str.toLowerCase().startsWith("#case ")) {
                String label = str.split(" ", 2)[1].trim();
                if (!labelAddressMap.containsKey(label))
                    throw new ExpectedLabelException(lineNumber, str, label);
                int labelAddress = labelAddressMap.get(label);
                int offset = labelAddress - (lastNonSwitchAddress + 4);
                parseLine.str = "dcw " + offset;
            } else if (str.startsWith("dcd ")) {
                String dataValueStr = str.split(" ", 2)[1].trim();
                int dataValue = parseValue(lineNumber, str, dataValueStr);

                if (!dataRamAddressMap.containsKey(dataValue))
                    dataRamAddressMap.put(dataValue, new TreeSet<>());
                SortedSet<Integer> dataRamAddresses = dataRamAddressMap.get(dataValue);
                dataRamAddresses.add(alignNextWord(currentRamAddress));
            } else {
                lastNonSwitchAddress = currentRamAddress;
            }

            currentRamAddress += getLineByteLength(lineNumber, str);
        }

        size = alignNextWord(currentRamAddress) - initialRamAddress;

        currentRamAddress = initialRamAddress;
        for (ParseLine parseLine : parseLines) {
            int lineNumber = parseLine.number;
            String str = parseLine.str;
            str = stripComment(str).trim();

            if (str.startsWith("#") || str.isEmpty() || str.endsWith(":"))
                continue;

            int firstSpaceIndex = str.indexOf(' ');
            if (firstSpaceIndex <= 0)
                throw new RuntimeException("Could not parse " + str);

            String op = str.substring(0, firstSpaceIndex).toLowerCase();
            String args = str.substring(firstSpaceIndex + 1).replace(" ", "");

            byte[] instructionBytes = parseInstruction(lineNumber, op, args);

            for (byte instructionByte : instructionBytes)
                bytes.add(instructionByte);

            currentRamAddress += instructionBytes.length;
        }

        boolean addedData = false;
        for (Map.Entry<Integer, SortedSet<Integer>> entry : dataRamAddressMap.entrySet()) {
            SortedSet<Integer> addresses = entry.getValue();
            for (Integer address : addresses) {
                if (address >= currentRamAddress) {
                    currentRamAddress = address + 4;
                    addedData = true;
                }
            }
        }

        currentRamAddress = alignNextWord(currentRamAddress);
        int redirectorAddressesSize = redirectors.size() * 16;
        size = currentRamAddress - initialRamAddress + redirectorAddressesSize;

        byte[] returnValue = new byte[size];
        for (int i = 0; i < bytes.size(); ++i)
            returnValue[i] = bytes.get(i);

        // Add No Op
        if (!isWordAligned(bytes.size()) && (addedData || !redirectors.isEmpty()))
            writeHalfword(returnValue, bytes.size(), 0x46C0);

        // Register any of the values in the ldr data as addresses if applicable
        for (Map.Entry<Integer, SortedSet<Integer>> entry : dataRamAddressMap.entrySet()) {
            int dataValue = entry.getKey();

            for (int dataAddress : entry.getValue()) {
                int offset = dataAddress - initialRamAddress;
                writeWord(returnValue, offset, dataValue);

                if (globalAddressMap == null || overlay == null || !ParagonLiteAddressMap.isValidAddress(dataValue, true))
                    continue;

                int targetAddress = alignWord(dataValue);

                globalAddressMap.addReference(overlay, targetAddress, dataAddress);
            }
        }

        Map<AddressPair, Integer> addressPairToRedirectorAddress = new HashMap<>(redirectors.size());

        // Add redirector functions
        for (AddressPair redirector : redirectors) {
            addressPairToRedirectorAddress.put(redirector, currentRamAddress);
            int offset = currentRamAddress - initialRamAddress;

            writeHalfword(returnValue, offset, 0x4778); // bx pc
            writeHalfword(returnValue, offset + 2, 0x46C0); // nop
            writeWord(returnValue, offset + 4, 0xE59FC000); // ldr r12, =(address+1 or address+0)
            writeWord(returnValue, offset + 8, 0xE12FFF1C); // bx r12

            if (redirector.overlay == null)
                redirector.overlay = globalAddressMap.findOverlay(redirector.address, overlay);

            ParagonLiteAddressMap.CodeAddress codeAddress = (ParagonLiteAddressMap.CodeAddress) globalAddressMap.getAddressData(redirector.overlay, redirector.address);
            writeWord(returnValue, offset + 12, redirector.address + (codeAddress.getEncoding() == 4 ? 0 : 1));

            currentRamAddress += 16;
        }

        // Update existing cross-module branch instructions to jump to the newly added functions
        for (Map.Entry<Integer, AddressPair> entry : instructionAddressToRedirector.entrySet()) {
            int instructionAddress = entry.getKey();
            int instructionOffset = instructionAddress - initialRamAddress;
            AddressPair redirector = entry.getValue();

            int redirectorAddress = addressPairToRedirectorAddress.get(redirector);

            int tempCurrentRamAddress = currentRamAddress;
            currentRamAddress = instructionAddress;
            byte[] instructionBytes = format19(-1, "", new String[]{Integer.toString(redirectorAddress)}, false);
            currentRamAddress = tempCurrentRamAddress;

            System.arraycopy(instructionBytes, 0, returnValue, instructionOffset, instructionBytes.length);
        }

        currentContext = null;

        if (returnValue.length > 0x02000000)
            throw new ArmParseException();

        return returnValue;
    }

    public int getByteLength(ParagonLiteOverlay overlay, List<String> lines) throws ArmParseException {
        byte[] bytes = parse(lines, overlay, overlay.getAddress()); // uses base address as default
        return bytes.length;
    }

    public int getFuncSize(ParagonLiteOverlay overlay, int initialRamAddress) {
        if (overlay.readUnsignedHalfword(initialRamAddress) == 0)
            throw new RuntimeException("Empty function");

        final AtomicReference<Integer> maxOffset = new AtomicReference<>(initialRamAddress);
        final AtomicReference<List<Integer>> branchDestinations = new AtomicReference<>(new ArrayList<>());

        traverseData(overlay, initialRamAddress, (context) -> {
            maxOffset.set(Math.max(maxOffset.get(), context.ramAddress + 2));

            // Long Branch with link
            if ((context.instruction & 0xF800) == 0xF000) {
                int nextInstruction = overlay.readUnsignedHalfword(overlay.ramToRomAddress(context.ramAddress) + 0x02);

                int high = context.instruction & 0x07FF;
                int low = nextInstruction & 0x07FF;

                int offset = (((high << 21) >> 9) | (low << 1)) + 4;
                int destination = context.ramAddress + offset;
                branchDestinations.get().add(destination);
            }

            // PC-relative load
            if ((context.instruction & 0xF800) == 0x4800) {
                int imm = context.instruction & 0x00FF;
                int dataOffset = alignWord(context.ramAddress + ((imm << 2) + 4));
                maxOffset.set(Math.max(maxOffset.get(), dataOffset + 4));
            }
        });

        int endRamAddress = alignNextWord(maxOffset.get());

        // Check for cross-module jumps
        Collections.sort(branchDestinations.get());
        for (int destination : branchDestinations.get()) {
            if (destination != endRamAddress)
                continue;

            int romDestination = overlay.ramToRomAddress(destination);

            // bx pc
            if (overlay.readUnsignedHalfword(romDestination) != 0x4778)
                continue;

            // nop
            if (overlay.readUnsignedHalfword(romDestination + 0x02) != 0x46C0)
                continue;

            // ldr r12, #0x08
            if (overlay.readWord(romDestination + 0x04) != 0xE59FC000)
                continue;

            // bx r12
            if (overlay.readWord(romDestination + 0x08) != 0xE12FFF1C)
                continue;

            endRamAddress += 16;
        }

        return endRamAddress - initialRamAddress;
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

    private int getLineByteLength(int lineNum, String line) {
        line = stripComment(line).trim();

        String lowerLine = line.toLowerCase();

        if (line.isEmpty())
            return 0;

        if (line.endsWith(":"))
            return 0;

        if (lowerLine.startsWith("#define "))
            return 0;

        if (lowerLine.startsWith("#if"))
            return 0;

        if (lowerLine.startsWith("#else"))
            return 0;

        if (lowerLine.startsWith("#elif"))
            return 0;

        if (lowerLine.startsWith("#endif"))
            return 0;

        if (lowerLine.startsWith("#case "))
            return 2;

        // check for simplification
        if (lowerLine.startsWith("ldr") && line.contains("=")) {
            int equalsIndex = line.indexOf('=');
            String valueStr = line.substring(equalsIndex).trim();
            int value;
            try {
                value = parseValue(lineNum, line, valueStr);
            } catch (ArmParseException e) {
                throw new RuntimeException(e);
            }

            int shift = 0;
            while (value >> shift >= 256 && ((value >> shift) & 1) == 0)
                ++shift;

            int reducedValue = value >> shift;
            return shift > 0 && reducedValue < 256 ? 4 : 2;
        }

        if (lowerLine.startsWith("bl "))
            return 4;

        if (lowerLine.startsWith("blx ")) {
            String args = line.split(" ", 2)[1].trim();
            return parseRegister(args) == -1 ? 4 : 2;
        }

        if (lowerLine.startsWith("dcb "))
            return 1;

        if (lowerLine.startsWith("dcw "))
            return 2;

        if (lowerLine.startsWith("dcd "))
            return isWordAligned(currentRamAddress) ? 4 : 6;

        return 2;
    }

    private String stripComment(String line) {
        int commentStartIndex = line.indexOf(';');
        if (commentStartIndex > -1) {
            line = line.substring(0, commentStartIndex);
        }
        return line;
    }

    private byte[] parseInstruction(int line, String op, String args) throws ArmParseException {
        return switch (op) {
            case "adc" -> format4(line, op, args.split(","), 5);
            case "add" -> parseAdd(line, op, args);
            case "and" -> format4(line, op, args.split(","), 0);
            case "asr" -> parseAsr(line, op, args);
            case "b" -> format18(line, op, args.split(","));
            case "bic" -> format4(line, op, args.split(","), 14);
            case "bl" -> format19(line, op, args.split(","), false);
            case "blx" -> parseBlx(line, op, args);
            case "bx" -> format5(line, op, args.split(","), 3);
            case "cmn" -> format4(line, op, args.split(","), 11);
            case "cmp" -> parseCmp(line, op, args);
            case "eor" -> format4(line, op, args.split(","), 1);
            case "ldm", "stm" -> parseLdmStmia(line, op, args);
            case "ldr", "str" -> parseLdrStr(line, op, args);
            case "ldrb", "strb" -> parseLdrbStrb(line, op, args);
            case "ldrh", "strh" -> parseLdrhStrh(line, op, args);
            case "ldsb" -> parseLdsb(line, op, args);
            case "ldsh" -> parseLdsh(line, op, args);
            case "lsl" -> parseLslLsr(line, op, args, 0);
            case "lsr" -> parseLslLsr(line, op, args, 1);
            case "mov" -> parseMov(line, op, args);
            case "mul" -> format4(line, op, args.split(","), 13);
            case "mvn" -> format4(line, op, args.split(","), 15);
            case "neg" -> format4(line, op, args.split(","), 9);
            case "orr" -> format4(line, op, args.split(","), 12);
            case "pop", "push" -> parsePopPush(line, op, args);
            case "ror" -> format4(line, op, args.split(","), 7);
            case "sbc" -> format4(line, op, args.split(","), 6);
            case "swi" -> format17(line, op, args.split(","));
            case "sub" -> parseSub(line, op, args);
            case "tst" -> format4(line, op, args.split(","), 8);

            // Conditional Branching
            case "beq" -> format16(line, op, args.split(","), 0);
            case "bne" -> format16(line, op, args.split(","), 1);
            case "bcs" -> format16(line, op, args.split(","), 2);
            case "bcc" -> format16(line, op, args.split(","), 3);
            case "bmi" -> format16(line, op, args.split(","), 4);
            case "bpl" -> format16(line, op, args.split(","), 5);
            case "bvs" -> format16(line, op, args.split(","), 6);
            case "bvc" -> format16(line, op, args.split(","), 7);
            case "bhi" -> format16(line, op, args.split(","), 8);
            case "bls" -> format16(line, op, args.split(","), 9);
            case "bge" -> format16(line, op, args.split(","), 10);
            case "blt" -> format16(line, op, args.split(","), 11);
            case "bgt" -> format16(line, op, args.split(","), 12);
            case "ble" -> format16(line, op, args.split(","), 13);

            // Data
            case "dcb" -> getDataBytes(line, op, args, 1, Byte.MIN_VALUE, Byte.MAX_VALUE);
            case "dcw" -> getDataBytes(line, op, args, 2, Short.MIN_VALUE, Short.MAX_VALUE);
            case "dcd" -> getDataBytes(line, op, args, 4, Integer.MIN_VALUE, Integer.MAX_VALUE);

            default -> new byte[0];
        };

    }

    private byte[] parseAdd(int line, String op, String argsStr) throws ArmParseException {
        // Format 2     (3) ADD Rd, Rs, Rn/#Offset3
        // Format 3     (2) ADD Rd, #Offset8
        // Format 5     (2) ADD Rd/Hd, Rs/Hs
        // Format 12    (3) ADD Rd, PC/SP, #Imm
        // Format 13    (2) ADD SP, #Imm

        String[] args = argsStr.split(",");
        for (var i = 0; i < args.length; ++i)
            args[i] = args[i].trim();

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("sp"))
                return format13(line, op, args);

            if (args[1].startsWith("#")) {
                if (parseValue(line, op, args[1]) <= 7)
                    return format2(line, op, new String[]{args[0], args[0], args[1]}, 0);
                return format3(line, op, args, 2);
            }

            int rd = parseRegister(args[0]);
            int rs = parseRegister(args[1]);

            // Prefer format2
            if (rd < 8 && rs < 8)
                return format2(line, op, new String[]{args[0], args[1], args[0]}, 0);

            return format5(line, op, args, 0);
        }

        if (args.length == 3) {
            if (args[1].equalsIgnoreCase("pc") || args[1].equalsIgnoreCase("sp"))
                return format12(line, op, args);

            return format2(line, op, args, 0);
        }

        throw new ArmParseArgCountException(line, op, args, 2, 3);
    }

    private byte[] parseAsr(int line, String op, String argsStr) throws ArmParseException {
        // Format 1     (3) ASR Rd, Rs, #Offset5
        // Format 4     (2) ASR Rd, Rs

        String[] args = argsStr.split(",");
        for (var i = 0; i < args.length; ++i)
            args[i] = args[i].trim();

        if (args.length == 2) {
            if (args[1].startsWith("#"))
                return format1(line, op, new String[]{args[0], args[0], args[1]}, 2);

            return format4(line, op, args, 4);
        }

        if (args.length == 3)
            return format1(line, op, args, 2);

        throw new ArmParseArgCountException(line, op, args, 2, 3);
    }

    private byte[] parseBlx(int line, String op, String argsStr) throws ArmParseException {
        int register = parseRegister(argsStr);
        if (register == -1)
            return format19(line, op, argsStr.split(","), true);

        byte[] bytes = format5(line, op, argsStr.split(","), 3);
        bytes[0] |= (byte) 0x80; // sets blx mode
        return bytes;
    }

    private byte[] parseCmp(int line, String op, String argsStr) throws ArmParseException {
        // Format 3     (2) CMP Rd, #Offset8        op 1
        // Format 4     (2) CMP Rd, Rs              op 10
        // Format 5     (2) CMP Rd/Hd, Rs/Hs        op 1

        String[] args = argsStr.split(",");
        for (var i = 0; i < args.length; ++i)
            args[i] = args[i].trim();

        if (args.length != 2)
            throw new ArmParseArgCountException(line, op, args, 2);

        if (args[1].startsWith("#"))
            return format3(line, op, args, 1);

        int rd = parseRegister(args[0]);
        int rs = parseRegister(args[1]);

        if (rd > 7 || rs > 7)
            return format5(line, op, args, 1);

        return format4(line, op, args, 10);
    }

    private byte[] parseLdmStmia(int line, String op, String argsStr) throws ArmParseException {
        // Format 15    LDM Rb!, { Rlist }

        String[] args = argsStr.split(",");
        for (var i = 0; i < args.length; ++i)
            args[i] = args[i].trim();

        if (args.length < 2)
            throw new ArmParseArgCountException(line, op, args, 2);

        String formatMessage = "Expected format {rx, rx-rx}!";

        String rbStr = args[0];
        if (!rbStr.endsWith("!"))
            throw new ArmParseException(line, op, args, formatMessage);

        args[0] = rbStr.substring(0, rbStr.length() - 1);

        String rlistFirst = args[1];
        if (!rlistFirst.startsWith("{"))
            throw new ArmParseException(line, op, args, formatMessage);

        args[1] = rlistFirst.substring(1);

        String rlistLast = args[args.length - 1];
        if (!rlistLast.endsWith("}"))
            throw new ArmParseException(line, op, args, formatMessage);

        args[args.length - 1] = rlistLast.substring(0, rlistLast.length() - 1);

        return switch (op.toLowerCase()) {
            case "ldm" -> format15(line, op, args, true);
            case "stm" -> format15(line, op, args, false);
            default -> throw new ExpectedOpException(line, op, args, "ldm", "stm");
        };
    }

    private byte[] parseLdrStr(int line, String op, String argsStr) throws ArmParseException {
        // Format 6     LDR Rd, [PC, #Imm]
        // Format 7     LDR Rd, [Rb, Ro]
        // Format 9     LDR Rd, [Rb, #Imm]
        // Format 11    LDR Rd, [SP, #Imm]

        String[] args = argsStr.split(",");
        for (var i = 0; i < args.length; ++i)
            args[i] = args[i].trim();
        
        if (args.length == 2) {

            // Hack to allow "LDR Rd, =Number" to use "LDR Rd, [PC, #NumberAddressOffset]"
            if (args[1].startsWith("=")) {
                int imm = parseValue(line, op, args, args[1]);

                // check to see if this can be conveniently simplified with a move and shift
                int shift = 0;
                while (imm >> shift >= 256 && ((imm >> shift) & 1) == 0)
                    ++shift;

                int reducedImm = imm >> shift;
                if (reducedImm >= 0 && reducedImm < 256) {
                    byte[] movBytes = parseInstruction(line, "mov", String.format("%s, #%d", args[0], reducedImm));
                    if (shift == 0)
                        return movBytes;

                    byte[] lslBytes = parseInstruction(line, "lsl", String.format("%s, #%d", args[0], shift));
                    return new byte[]{movBytes[0], movBytes[1], lslBytes[0], lslBytes[1]};
                }

                if (!dataRamAddressMap.containsKey(imm))
                    dataRamAddressMap.put(imm, new TreeSet<>());
                SortedSet<Integer> dataRamAddresses = dataRamAddressMap.get(imm);

                int maxAddress = alignWord(currentRamAddress + (255 << 2) + 4);

                int newImm = -1;
                for (int dataAddress : dataRamAddresses) {
                    if (dataAddress > currentRamAddress && dataAddress <= maxAddress) {
                        newImm = dataAddress - currentRamAddress;
                        break;
                    }
                }

                if (newImm == -1) {
                    int endDataAddress = alignNextWord(initialRamAddress + size);
                    if (endDataAddress <= maxAddress) {
                        newImm = endDataAddress - currentRamAddress;
                        dataRamAddresses.add(alignNextWord(endDataAddress));
                        size = endDataAddress - initialRamAddress + 4;
                    }
                }

                return format6(line, op, new String[]{args[0], "PC", "#" + newImm});
            }

            args = new String[]{args[0], args[1].substring(0, args[1].length() - 1), "#0]"};
        }

        if (args.length != 3)
            throw new ArmParseArgCountException(line, op, args, 3);

        String formatMessage = "Must follow format [rx, rx/imm]";

        if (!args[1].startsWith("["))
            throw new ArmParseException(line, op, args, formatMessage);

        if (!args[2].endsWith("]"))
            throw new ArmParseException(line, op, args, formatMessage);

        args[1] = args[1].substring(1);
        args[2] = args[2].substring(0, args[2].length() - 1);

        if (op.equalsIgnoreCase("ldr") && args[1].equalsIgnoreCase("pc"))
            return format6(line, op, args);

        boolean loadStore;
        switch (op.toLowerCase()) {
            case "ldr" -> loadStore = true;
            case "str" -> loadStore = false;
            default -> throw new ExpectedOpException(line, op, args, "ldr", "str");
        }

        if (args[1].equalsIgnoreCase("sp"))
            return format11(line, op, args, loadStore);

        if (args[2].startsWith("#"))
            return format9(line, op, args, loadStore, false);

        return format7(line, op, args, loadStore, false);
    }

    private byte[] parseLdrbStrb(int line, String op, String argsStr) throws ArmParseException {
        // Format 7     LDRB Rd, [Rb, Ro]
        // Format 9     LDRB Rd, [Rb, #Imm]

        String[] args = argsStr.split(",");
        for (var i = 0; i < args.length; ++i)
            args[i] = args[i].trim();
        
        if (args.length == 2)
            args = new String[]{args[0], args[1].substring(0, args[1].length() - 1), "#0]"};

        if (args.length != 3)
            throw new ArmParseArgCountException(line, op, args, 3);

        String formatMessage = "Must follow format [rx, rx/imm]";

        if (!args[1].startsWith("["))
            throw new ArmParseException(line, op, args, formatMessage);

        if (!args[2].endsWith("]"))
            throw new ArmParseException(line, op, args, formatMessage);

        args[1] = args[1].substring(1);
        args[2] = args[2].substring(0, args[2].length() - 1);

        boolean loadStore;
        switch (op.toLowerCase()) {
            case "ldrb" -> loadStore = true;
            case "strb" -> loadStore = false;
            default -> throw new ExpectedOpException(line, op, args, "ldrb", "strb");
        }

        if (args[2].startsWith("#"))
            return format9(line, op, args, loadStore, true);

        return format7(line, op, args, loadStore, true);
    }

    private byte[] parseLdrhStrh(int line, String op, String argsStr) throws ArmParseException {
        // Format 8     LDRH Rd, [Rb, Ro]
        // Format 10    LDRH Rd, [Rb, #Imm]

        String[] args = argsStr.split(",");
        for (var i = 0; i < args.length; ++i)
            args[i] = args[i].trim();
        
        if (args.length == 2)
            args = new String[]{args[0], args[1].substring(0, args[1].length() - 1), "#0]"};

        if (args.length != 3)
            throw new ArmParseArgCountException(line, op, args, 3);

        String formatMessage = "Must follow format [rx, rx/imm]";

        if (!args[1].startsWith("["))
            throw new ArmParseException(line, op, args, formatMessage);

        if (!args[2].endsWith("]"))
            throw new ArmParseException(line, op, args, formatMessage);

        args[1] = args[1].substring(1);
        args[2] = args[2].substring(0, args[2].length() - 1);

        boolean loadStore;
        switch (op.toLowerCase()) {
            case "ldrh" -> loadStore = true;
            case "strh" -> loadStore = false;
            default -> throw new ExpectedOpException(line, op, args, "ldrh", "strh");
        }

        if (args[2].startsWith("#"))
            return format10(line, op, args, loadStore);

        return format8(line, op, args, false, loadStore);
    }

    private byte[] parseLdsb(int line, String op, String argsStr) throws ArmParseException {
        // Format 8     LDSB Rd, [Rb, Ro]

        String[] args = argsStr.split(",");
        for (var i = 0; i < args.length; ++i)
            args[i] = args[i].trim();

        if (args.length != 3)
            throw new ArmParseArgCountException(line, op, args, 3);

        String formatMessage = "Must follow format [rx, rx/imm]";

        if (!args[1].startsWith("["))
            throw new ArmParseException(line, op, args, formatMessage);

        if (!args[2].endsWith("]"))
            throw new ArmParseException(line, op, args, formatMessage);

        args[1] = args[1].substring(1);
        args[2] = args[2].substring(0, args[2].length() - 1);

        return format8(line, op, args, true, false);
    }

    private byte[] parseLdsh(int line, String op, String argsStr) throws ArmParseException {
        // Format 8     LDSH Rd, [Rb, Ro]

        String[] args = argsStr.split(",");
        for (var i = 0; i < args.length; ++i)
            args[i] = args[i].trim();

        if (args.length != 3)
            throw new ArmParseArgCountException(line, op, args, 3);

        String formatMessage = "Must follow format [rx, rx/imm]";

        if (!args[1].startsWith("["))
            throw new ArmParseException(line, op, args, formatMessage);

        if (!args[2].endsWith("]"))
            throw new ArmParseException(line, op, args, formatMessage);

        args[1] = args[1].substring(1);
        args[2] = args[2].substring(0, args[2].length() - 1);

        return format8(line, op, args, true, true);
    }

    private byte[] parseLslLsr(int line, String op, String argsStr, int isRightShift) throws ArmParseException {
        // Format 1     (3) LSL/LSR Rd, Rs, #Offset5 or... LSL/LSR Rd, #Offset5
        // Format 4     (2) LSL/LSR Rd, Rs

        String[] args = argsStr.split(",");
        for (var i = 0; i < args.length; ++i)
            args[i] = args[i].trim();

        if (args.length == 2) {
            if (args[1].startsWith("#"))
                return format1(line, op, new String[]{args[0], args[0], args[1]}, isRightShift);

            return format4(line, op, args, 2 + isRightShift);
        }

        if (args.length == 3)
            return format1(line, op, args, isRightShift);

        throw new ArmParseArgCountException(line, op, args, 2, 3);
    }

    private byte[] parseMov(int line, String op, String argsStr) throws ArmParseException {
        // Format 3     (2) MOV Rd, #Offset8
        // Format 5     (2) MOV Rd, Hs

        String[] args = argsStr.split(",");
        for (var i = 0; i < args.length; ++i)
            args[i] = args[i].trim();
        
        if (args.length != 2)
            throw new ArmParseArgCountException(line, op, args, 2);

        if (args[1].startsWith("#"))
            return format3(line, op, args, 0);

        // Hack to allow "MOV Rd, Rs" to become "ADD Rd, Rs, #0" internally; Format 2 with "ADD" opcode (0)
        // Only used when both registers are low, otherwise use Format 5
        int rd = parseRegister(args[0]);
        int rs = parseRegister(args[1]);
        if (rd <= 7 && rs <= 7) {
            args = new String[]{args[0], args[1], "#0"};
            return format2(line, op, args, 0);
        }

        if (rd <= 7 && (rs == 13 || rs == 15)) {
            args = new String[]{args[0], args[1], "#0"};
            return format12(line, op, args);
        }

        return format5(line, op, args, 2);
    }

    private byte[] parsePopPush(int line, String op, String argsStr) throws ArmParseException {
        // Format 14    PUSH/POP {Rlist}

        if (!argsStr.startsWith("{") || !argsStr.endsWith("}")) {
            switch (op.toLowerCase()) {
                case "push" -> throw new ArmParseException(line, String.format("%s %s", op, argsStr), "Must follow format: {rx, rx-rx, lr}");
                case "pop" -> throw new ArmParseException(line, String.format("%s %s", op, argsStr), "Must follow format: {rx, rx-rx, pc}");
                default -> throw new ExpectedOpException(line, op, new String[]{argsStr}, "push", "pop");
            }
        }

        String[] args = argsStr.substring(1, argsStr.length() - 1).split(",");

        return format14(line, op, args);
    }

    private byte[] parseSub(int line, String op, String argsStr) throws ArmParseException {
        // Format 2     (3) SUB Rd, Rs, Rn/#Offset3
        // Format 3     (2) SUB Rd, #Offset8
        // Format 13    (2) SUB SP, #Imm

        String[] args = argsStr.split(",");
        for (var i = 0; i < args.length; ++i)
            args[i] = args[i].trim();

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("sp"))
                return format13(line, op, new String[]{args[0], "#-(" + args[1].substring(1) + ")"}); // flip

            if (args[1].startsWith("#"))
                return format3(line, op, args, 3);

            return format2(line, op, new String[]{args[0], args[0], args[1]}, 1);
        }

        if (args.length == 3)
            return format2(line, op, args, 1);

        throw new ArmParseArgCountException(line, op, args, 2, 3);
    }

    // Format 1: move shifted register    
    private byte[] format1(int line, String op, String[] args, int opcode) throws ArmParseException {
        if (opcode < 0 || opcode > 2)
            throw new UnexpectedOpcodeException(line, op, args, opcode, 0, 2);

        int rd = parseRegister(args[0]);
        int rs = parseRegister(args[1]);
        int imm = parseValue(line, op, args, args[2]);

        if (rd < 0 || rd > 7)
            throw new ExpectedLowRegisterException(line, op, args, 0);

        if (rs < 0 || rs > 7)
            throw new ExpectedLowRegisterException(line, op, args, 1);

        if (imm < 0 || imm > 31)
            throw new ExpectedNumberException(line, op, args, 2, imm, 0, 31);

        return halfwordToBytes(rd | (rs << 3) | (imm << 6) | (opcode << 11));
    }

    // Format 2: add/subtract
    private byte[] format2(int line, String op, String[] args, int opcode) throws ArmParseException {
        int rd = parseRegister(args[0]);
        int rs = parseRegister(args[1]);

        boolean isImmediate = args[2].startsWith("#");
        int rnOrImm = isImmediate ? parseValue(line, op, args, args[2]) : parseRegister(args[2]);

        if (opcode != 0 && opcode != 1)
            throw new UnexpectedOpcodeException(line, op, args, opcode, 0, 1);

        if (rd < 0 || rd > 7)
            throw new ExpectedLowRegisterException(line, op, args, 0);

        if (rs < 0 || rs > 7)
            throw new ExpectedLowRegisterException(line, op, args, 1);

        if (rnOrImm < 0 || rnOrImm > 7) {
            if (isImmediate)
                throw new ExpectedNumberException(line, op, args, 2, rnOrImm, 0, 7);
            else
                throw new ExpectedLowRegisterException(line, op, args, 2);
        }

        return halfwordToBytes(rd | (rs << 3) | (rnOrImm << 6) | (opcode << 9) | ((isImmediate ? 1 : 0) << 10) | 0x1800);
    }

    // Format 3: move/compare/add/subtract immediate
    private byte[] format3(int line, String op, String[] args, int opcode) throws ArmParseException {
        if (opcode < 0 || opcode > 3)
            throw new UnexpectedOpcodeException(line, op, args, opcode, 0, 3);

        int rd = parseRegister(args[0]);
        int imm = parseValue(line, op, args, args[1]);

        if (rd < 0 || rd > 7)
            throw new ExpectedLowRegisterException(line, op, args, 0);

        if (imm < 0 || imm > 255) {
            throw new ExpectedNumberException(line, op, args, 1, imm, 0, 255);
        }

        return halfwordToBytes(imm | (rd << 8) | (opcode << 11) | 0x2000);
    }

    // Format 4: ALU operations
    private byte[] format4(int line, String op, String[] args, int opcode) throws ArmParseException {
        if (args.length != 2)
            throw new ArmParseArgCountException(line, op, args, 2);

        if (opcode < 0 || opcode > 15)
            throw new UnexpectedOpcodeException(line, op, args, opcode, 0, 15);

        int rd = parseRegister(args[0]);
        int rs = parseRegister(args[1]);

        if (rd < 0 || rd > 7)
            throw new ExpectedLowRegisterException(line, op, args, 0);

        if (rs < 0 || rs > 7)
            throw new ExpectedLowRegisterException(line, op, args, 1);

        return halfwordToBytes(rd | (rs << 3) | (opcode << 6) | 0x4000);
    }

    // Format 5: Hi register operations/branch exchange
    private byte[] format5(int line, String op, String[] args, int opcode) throws ArmParseException {
        if (opcode < 0 || opcode > 3)
            throw new UnexpectedOpcodeException(line, op, args, opcode, 0, 3);

        // branch exchange
        if (args.length == 1) {
            int rs = parseRegister(args[0]);
            if (rs < 0 || rs > 15)
                throw new ExpectedRegisterException(line, op, args, 0);

            return halfwordToBytes(rs << 3 | 0x4700);
        }

        int rd = parseRegister(args[0]);
        int rs = parseRegister(args[1]);

        if (rd < 0 || rd > 15)
            throw new ExpectedRegisterException(line, op, args, 0);

        if (rs < 0 || rs > 15)
            throw new ExpectedRegisterException(line, op, args, 1);

        int h1 = rd > 7 ? 1 : 0;
        int h2 = rs > 7 ? 1 : 0;

        rd &= 0x07;
        rs &= 0x07;

        return halfwordToBytes(rd | (rs << 3) | (h2 << 6) | (h1 << 7) | (opcode << 8) | 0x4400);
    }

    // Format 6: PC-relative load
    private byte[] format6(int line, String op, String[] args) throws ArmParseException {
        int rd = parseRegister(args[0]);
        int imm = alignNextWord(parseValue(line, op, args, args[2]));

        if (rd < 0 || rd > 7)
            throw new ExpectedLowRegisterException(line, op, args, 0);

        int immMax = (255 << 2) + 4;
        if (imm < 0 || imm > immMax)
            throw new ExpectedNumberException(line, op, args, 1, imm, 0, immMax);

        imm = ((imm - 4) >> 2) & 0xFF;

        return halfwordToBytes(imm | (rd << 8) | 0x4800);
    }

    // Format 7: load/store with register offset
    private byte[] format7(int line, String op, String[] args, boolean loadStore, boolean byteWord) throws ArmParseException {
        int loadStoreFlag = loadStore ? 1 : 0;
        int byteWordFlag = byteWord ? 1 : 0;

        int rd = parseRegister(args[0]);
        int rb = parseRegister(args[1]);
        int ro = parseRegister(args[2]);

        if (rd < 0 || rd > 7)
            throw new ExpectedLowRegisterException(line, op, args, 0);

        if (rb < 0 || rb > 7)
            throw new ExpectedLowRegisterException(line, op, args, 1);

        if (ro < 0 || ro > 7)
            throw new ExpectedLowRegisterException(line, op, args, 2);

        return halfwordToBytes(rd | (rb << 3) | (ro << 6) | (byteWordFlag << 10) | (loadStoreFlag << 11) | 0x5000);
    }

    // Format 8: load/store sign-extended byte/halfword
    private byte[] format8(int line, String op, String[] args, boolean signExtended, boolean high) throws ArmParseException {
        int signExtendedFlag = signExtended ? 1 : 0;
        int highFlag = high ? 1 : 0;

        int rd = parseRegister(args[0]);
        int rb = parseRegister(args[1]);
        int ro = parseRegister(args[2]);

        if (rd < 0 || rd > 7)
            throw new ExpectedLowRegisterException(line, op, args, 0);

        if (rb < 0 || rb > 7)
            throw new ExpectedLowRegisterException(line, op, args, 1);

        if (ro < 0 || ro > 7)
            throw new ExpectedLowRegisterException(line, op, args, 2);

        return halfwordToBytes(rd | (rb << 3) | (ro << 6) | (signExtendedFlag << 10) | (highFlag << 11) | 0x5200);
    }

    // Format 9: load/store with immediate offset
    private byte[] format9(int line, String op, String[] args, boolean loadStore, boolean byteWord) throws ArmParseException {
        int loadStoreFlag = loadStore ? 1 : 0;
        int byteWordFlag = byteWord ? 1 : 0;

        int rd = parseRegister(args[0]);
        int rb = parseRegister(args[1]);
        int imm = parseValue(line, op, args, args[2]);

        if (rd < 0 || rd > 7)
            throw new ExpectedLowRegisterException(line, op, args, 0);

        if (rb < 0 || rb > 7)
            throw new ExpectedLowRegisterException(line, op, args, 1);

        int immMax = 0x1F << (byteWordFlag == 0 ? 2 : 0);
        if (imm < 0 || imm > immMax)
            throw new ExpectedNumberException(line, op, args, 2, imm, 0, immMax);

        if (byteWordFlag == 0) {
            if (!isWordAligned(imm))
                throw new ImmediateWordAlignmentException(line, op, args, 2, imm);

            imm = imm >> 2;
        }

        return halfwordToBytes(rd | (rb << 3) | (imm << 6) | (loadStoreFlag << 11) | (byteWordFlag << 12) | 0x6000);
    }

    // Format 10: load/store halfword
    private byte[] format10(int line, String op, String[] args, boolean loadStore) throws ArmParseException {
        int loadStoreFlag = loadStore ? 1 : 0;

        int rd = parseRegister(args[0]);
        int rb = parseRegister(args[1]);
        int imm = parseValue(line, op, args, args[2]);

        if (rd < 0 || rd > 7)
            throw new ExpectedLowRegisterException(line, op, args, 0);

        if (rb < 0 || rb > 7)
            throw new ExpectedLowRegisterException(line, op, args, 1);

        if (imm < 0 || imm > 62)
            throw new ExpectedNumberException(line, op, args, 2, imm, 0, 62);

        if (!isHalfwordAligned(imm))
            throw new ImmediateHalfwordAlignmentException(line, op, args, 2, imm);

        imm >>= 1;

        return halfwordToBytes(rd | (rb << 3) | (imm << 6) | (loadStoreFlag << 11) | 0x8000);
    }

    // Format 11: SP-relative load/store
    private byte[] format11(int line, String op, String[] args, boolean loadStore) throws ArmParseException {
        int loadStoreFlag = loadStore ? 1 : 0;

        int rd = parseRegister(args[0]);
        int imm = parseValue(line, op, args, args[2]);

        if (rd < 0 || rd > 7)
            throw new ExpectedLowRegisterException(line, op, args, 0);

        if (imm < 0 || imm > 1020)
            throw new ExpectedNumberException(line, op, args, 2, imm, 0, 1020);

        if (!isWordAligned(imm))
            throw new ImmediateWordAlignmentException(line, op, args, 2, imm);

        imm >>= 2;

        return halfwordToBytes(imm | (rd << 8) | (loadStoreFlag << 11) | 0x9000);
    }

    // Format 12: load address
    private byte[] format12(int line, String op, String[] args) throws ArmParseException {
        int rd = parseRegister(args[0]);
        int source = switch (parseRegister(args[1])) {
            case 13 -> 1; // sp
            case 15 -> 0; // pc
            default -> throw new ExpectedRegisterException(line, op, args, 1, new String[]{"sp", "pc"});
        };
        int imm = parseValue(line, op, args, args[2]);

        if (rd < 0 || rd > 7)
            throw new ExpectedLowRegisterException(line, op, args, 0);

        if (imm < 0 || imm > 1020)
            throw new ExpectedNumberException(line, op, args, 2, imm, 0, 1020);

        if (!isWordAligned(imm))
            throw new ImmediateWordAlignmentException(line, op, args, 2, imm);

        imm >>= 2;

        return halfwordToBytes(imm | (rd << 8) | (source << 11) | 0xA000);
    }

    // Format 13: add offset to Stack Pointer
    private byte[] format13(int line, String op, String[] args) throws ArmParseException {
        int imm = parseValue(line, op, args, args[1]);
        int immMin = -508;
        int immMax = 508;
        if (imm < immMin || imm > immMax)
            throw new ExpectedNumberException(line, op, args, 1, imm, immMin, immMax);

        int signFlag = imm < 0 ? 1 : 0;
        imm = Math.abs(imm);
        if (!isWordAligned(imm))
            throw new ImmediateWordAlignmentException(line, op, args, 1, imm);

        imm >>= 2;

        return halfwordToBytes(imm | (signFlag << 7) | 0xB000);
    }

    // Format 14: push/pop registers
    private byte[] format14(int line, String op, String[] args) throws ArmParseException {
        int rlist = 0;
        int pclrBit = 0;

        List<String> tokensList = new ArrayList<>();
        for (String s : args) {
            String[] argTokens = s.split("-");
            for (String argToken : argTokens)
                tokensList.add(argToken.trim());
        }
        String[] tokens = new String[tokensList.size()];
        for (int i = 0; i < tokens.length; ++i)
            tokens[i] = tokensList.get(i);

        int currentToken = 0;

        for (String arg : args) {
            if (arg.contains("-")) {
                String[] registers = arg.split("-");
                int rStart = parseRegister(registers[0]);
                int rEnd = parseRegister(registers[1]);

                if (rStart < 0 || rStart > 7)
                    throw new ExpectedLowRegisterException(line, op, tokens, currentToken, registers[0]);
                ++currentToken;

                if (rEnd < 0 || rEnd > 7)
                    throw new ExpectedLowRegisterException(line, op, tokens, currentToken, registers[1]);
                ++currentToken;

                for (int i = rStart; i <= rEnd; ++i)
                    rlist |= 1 << i;

                continue;
            }

            int register = parseRegister(arg);
            if (register < 0)
                throw new ExpectedRegisterException(line, op, tokens, currentToken, new String[]{"r0-r7", "lr", "pc"});

            if (register <= 7) {
                rlist |= 1 << register;
                ++currentToken;
                continue;
            }

            switch (op.toLowerCase()) {
                case "push": {
                    if (arg.equalsIgnoreCase("lr"))
                        pclrBit = 1;
                    else
                        throw new ExpectedRegisterException(line, op, tokens, currentToken, arg, new String[]{"lr"});
                    break;
                }
                case "pop": {
                    if (arg.equalsIgnoreCase("pc"))
                        pclrBit = 1;
                    else
                        throw new ExpectedRegisterException(line, op, tokens, currentToken, arg, new String[]{"pc"});
                    break;
                }
                default:
                    throw new ExpectedOpException(line, op, args, "push", "pop");
            }

            ++currentToken;
        }

        int loadStoreBit;
        switch (op.toLowerCase()) {
            case "push" -> loadStoreBit = 0;
            case "pop" -> loadStoreBit = 1;
            default -> throw new ExpectedOpException(line, op, args, "push", "pop");
        }

        return halfwordToBytes(rlist | (pclrBit << 8) | (loadStoreBit << 11) | 0xB400);
    }

    // Format 15: multiple load/store
    private byte[] format15(int line, String op, String[] args, boolean loadStore) throws ArmParseException {
        int loadStoreFlag = loadStore ? 1 : 0;

        int rb = parseRegister(args[0]);
        if (rb < 0 || rb > 7)
            throw new ExpectedLowRegisterException(line, op, args, 0);

        List<String> tokensList = new ArrayList<>();
        for (String s : args) {
            String[] argTokens = s.split("-");
            for (String argToken : argTokens)
                tokensList.add(argToken.trim());
        }
        String[] tokens = new String[tokensList.size()];
        for (int i = 0; i < tokens.length; ++i)
            tokens[i] = tokensList.get(i);

        int currentToken = 0;

        int rlist = 0;
        for (int i = 1; i < args.length; ++i) {
            if (args[i].contains("-")) {
                String[] registers = args[i].split("-");
                int rStart = parseRegister(registers[0]);
                int rEnd = parseRegister(registers[1]);

                if (rStart < 0 || rStart > 7)
                    throw new ExpectedLowRegisterException(line, op, tokens, currentToken, registers[0]);
                ++currentToken;

                if (rEnd < 0 || rEnd > 7)
                    throw new ExpectedLowRegisterException(line, op, tokens, currentToken, registers[1]);
                ++currentToken;

                for (int j = rStart; j <= rEnd; ++j)
                    rlist |= 1 << j;

                continue;
            }

            int register = parseRegister(args[i]);
            if (register < 0 || register > 7)
                throw new ExpectedLowRegisterException(line, op, tokens, currentToken, args[i]);

            rlist |= 1 << register;
        }

        return halfwordToBytes(rlist | (rb << 8) | (loadStoreFlag << 11) | 0xC000);
    }

    // Format 16: conditional branch
    private byte[] format16(int line, String op, String[] args, int condition) throws ArmParseException {
        if (condition < 0 || condition > 13)
            throw new UnexpectedOpcodeException(line, op, args, condition, 0, 13);

        if (!labelAddressMap.containsKey(args[0]))
            throw new ExpectedLabelException(line, op, args, 0);

        int jumpAddress = labelAddressMap.get(args[0]);
        int offset = jumpAddress - currentRamAddress - 4;
        if (offset < -256 || offset > 255)
            throw new BranchOffsetException(line, op, args, 0, offset, -256, 255);

        offset = (offset >> 1) & 0xFF;

        return halfwordToBytes(offset | (condition << 8) | 0xD000);
    }

    // Format 17: software interrupt
    private byte[] format17(int line, String op, String[] args) throws ArmParseException {
        int value = parseValue(line, op, args, args[0]);
        if (value < 0 || value > 255)
            throw new ExpectedNumberException(line, op, args, 0, value, 0, 255);

        return halfwordToBytes(value | 0xDF00);
    }

    // Format 18: unconditional branch
    private byte[] format18(int line, String op, String[] args) throws ArmParseException {
        if (args.length != 1)
            throw new ArmParseArgCountException(line, op, args, 1);

        if (!labelAddressMap.containsKey(args[0]))
            throw new ExpectedLabelException(line, op, args, 0);

        int jumpAddress = labelAddressMap.get(args[0]);

        int offset = (jumpAddress - currentRamAddress - 4);
        if (offset < -2048 || offset > 2047)
            throw new BranchOffsetException(line, op, args, 0, offset, -2048, 2047);

        offset = (offset >> 1) & 0x07FF;

        return halfwordToBytes(offset | 0xE000);
    }

    // Format 19: long branch with link
    private byte[] format19(int line, String op, String[] args, boolean exchangeInstructionSet) throws ArmParseException {
        if (globalAddressMap == null || overlay == null) {
            // Expected for getFuncSize only
            return new byte[4];
        }

        String namespace = null;
        int funcRamAddress;
        int targetFunctionEncoding;

        if (args[0].contains("::")) {
            String[] funcStrs = args[0].split("::", 2);

            namespace = funcStrs[0];
            String funcName = funcStrs[1];

            globalAddressMap.addReference(namespace, funcName, overlay, currentRamAddress);
            ParagonLiteAddressMap.AddressBase addressBase = globalAddressMap.getAddressData(namespace, funcName);
            if (!(addressBase instanceof ParagonLiteAddressMap.CodeAddress codeAddress))
                throw new ArmParseException(line, op, args, String.format("%s::%s is not a code address", namespace, funcName));

            funcRamAddress = codeAddress.getRamAddress();
            targetFunctionEncoding = codeAddress.getEncoding();
//            if (targetFunctionEncoding == 2 && exchangeInstructionSet)
//                throw new DataFormatException("BLX was used on a Thumb function");
//            if (targetFunctionEncoding == 4 && !exchangeInstructionSet)
//                throw new DataFormatException("BL was used on an ARM function");

            // TODO: Give proper warning when this was changed
            exchangeInstructionSet = targetFunctionEncoding == 4;
        } else {
            funcRamAddress = parseValue(line, op, args, args[0]);
            targetFunctionEncoding = 2; // TODO: Assume Thumb for now
        }

        int funcOffset = funcRamAddress - ((exchangeInstructionSet ? alignWord(currentRamAddress) : currentRamAddress) + 4);

        int min = 0xFFC00000; // -4194304
        int max = 0x003FFFFF; // +4194303

        if ((funcOffset < min || funcOffset > max) && overlay != null) {
            AddressPair addressPair = new AddressPair(globalAddressMap.getOverlay(namespace), funcRamAddress);

            if (!redirectors.contains(addressPair))
                redirectors.add(addressPair);

            instructionAddressToRedirector.put(currentRamAddress, addressPair);

            exchangeInstructionSet = false;
            funcOffset = 0;
        }

        if (funcOffset < min || funcOffset > max)
            throw new BranchOffsetException(line, op, args, 0, funcOffset, min, max);

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
    private byte[] getDataBytes(int line, String op, String valueStr, int size, int minValue, int maxValue) throws ArmParseException {
        int value = parseValue(line, op, valueStr, valueStr);
        if (value < minValue || value > maxValue)
            throw new ExpectedNumberException(line, op, new String[]{valueStr}, 0, value, minValue, maxValue);

        if (size == 1)
            return new byte[]{(byte) (value & 0xFF)};

        if (size == 2)
            return new byte[]{
                    (byte) (value & 0xFF),
                    (byte) ((value >> 8) & 0xFF),
            };

        if (size == 4) {
            if (isWordAligned(currentRamAddress))
                return new byte[]{
                        (byte) (value & 0xFF),
                        (byte) ((value >> 8) & 0xFF),
                        (byte) ((value >> 16) & 0xFF),
                        (byte) ((value >> 24) & 0xFF),
                };

            return new byte[]{
                    (byte) 0xC0, (byte) 0x46,
                    (byte) (value & 0xFF),
                    (byte) ((value >> 8) & 0xFF),
                    (byte) ((value >> 16) & 0xFF),
                    (byte) ((value >> 24) & 0xFF),
            };
        }

        throw new ArmParseException(line, op, new String[]{valueStr}, String.format("Unexpected size %d", size));
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

    private int parseValue(int line, String op, String[] args, String valueStr) throws ArmParseException {
        String joinedArgs = String.join(", ", args).trim();
        return parseValue(line, op, joinedArgs, valueStr);
    }

    private int parseValue(int line, String op, String args, String valueStr) throws ArmParseException {
        String lineStr = String.format("%s %s", op, args).trim();
        return parseValue(line, lineStr, valueStr);
    }

    private int parseValue(int line, String lineStr, String valueStr) throws ArmParseException {
        if (valueStr.startsWith("=") || valueStr.startsWith("#")) {
            valueStr = valueStr.substring(1).trim(); // remove = or #
        }

        String fullStr = globalAddressMap.replaceLabelsInExpression(valueStr);
        
        try {
            Object evalObj = engine.eval(fullStr, currentContext);
            if (evalObj instanceof Double asDouble)
                return (int) Math.round(asDouble);
            if (evalObj instanceof Integer asInt)
                return asInt;
            if (evalObj instanceof Boolean asBoolean)
                return asBoolean ? 1 : 0;
            throw new ArmParseEvalException(line, lineStr, valueStr);
        } catch (ScriptException e) {
            throw new ArmParseEvalException(line, lineStr, valueStr);
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

    private static void writeHalfword(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }

    private static void writeWord(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
        data[offset + 2] = (byte) ((value >> 16) & 0xFF);
        data[offset + 3] = (byte) ((value >> 24) & 0xFF);
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
