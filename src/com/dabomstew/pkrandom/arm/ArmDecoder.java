package com.dabomstew.pkrandom.arm;

import com.dabomstew.pkrandom.arm.argtypes.ArmArg_Register;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;
import com.dabomstew.pkrandom.arm.formats.*;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteAddressMap;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteOverlay;

import java.util.*;

public class ArmDecoder {
    static final ArmThumbFormat[] thumbFormats = new ArmThumbFormat[]{
            null,
            new ArmThumbFormat_01(),
            new ArmThumbFormat_02(),
            new ArmThumbFormat_03(),
            new ArmThumbFormat_04(),
            new ArmThumbFormat_05(),
            new ArmThumbFormat_06(),
            new ArmThumbFormat_07(),
            new ArmThumbFormat_08(),
            new ArmThumbFormat_09(),
            new ArmThumbFormat_10(),
            new ArmThumbFormat_11(),
            new ArmThumbFormat_12(),
            new ArmThumbFormat_13(),
            new ArmThumbFormat_14(),
            new ArmThumbFormat_15(),
            new ArmThumbFormat_16(),
            new ArmThumbFormat_17(),
            new ArmThumbFormat_18(),
            new ArmThumbFormat_19(),
    };

    public List<String> decode(ParagonLiteOverlay overlay, int initialRamAddress, byte[] bytes, ParagonLiteAddressMap addressMap) throws ArmDecodeException {
        List<String> lines = new ArrayList<>();

        Map<Integer, Integer> dataValues = new HashMap<>();
        Set<Integer> labelAddresses = new HashSet<>();

        // Run through twice, only adding to lines on the second iteration
        for (int i = 0; i < 2; i++) {
            int currentRamAddress = initialRamAddress;

            int switchStartAddress = 0;
            int switchCasesRemaining = 0;
            
            while (currentRamAddress < initialRamAddress + bytes.length) {
                int offset = currentRamAddress - initialRamAddress;

                // Switch
                if (switchCasesRemaining != 0) {
                    if (dataValues.containsKey(currentRamAddress) || labelAddresses.contains(currentRamAddress)) {
                        switchCasesRemaining = 0;
                        continue;
                    }

                    int switchOffset = readUnsignedHalfword(bytes, offset);
                    int caseLabelAddress = switchStartAddress + switchOffset + 4;

                    labelAddresses.add(caseLabelAddress);
                    if (i > 0)
                        addTabbedLine(lines, String.format("#CASE %s", getLabelName(caseLabelAddress)));

                    if (switchCasesRemaining > 0)
                        switchCasesRemaining--;

                    currentRamAddress += 2;
                    continue;
                }

                // Apply label
                if (i > 0 && labelAddresses.contains(currentRamAddress)) {
                    lines.add("");
                    lines.add(String.format("%s:", getLabelName(currentRamAddress)));
                }

                // Check if this is data
                if (dataValues.containsKey(currentRamAddress)) {
                    int dataValue = readWord(bytes, offset);
                    ArmDataLine dataLine = new ArmDataLine(dataValue);
                    if (i > 0)
                        addTabbedLine(lines, dataLine.toString());
                    currentRamAddress += 4;
                    continue;
                }

                IsSwitchResult isSwitchResult = isSwitch(bytes, offset);
                if (isSwitchResult.isSuccess) {
                    if (i > 0) {
                        if (isSwitchResult.register1 == isSwitchResult.register2)
                            addTabbedLine(lines, String.format("#SWITCH r%d", isSwitchResult.register1));
                        else
                            addTabbedLine(lines, String.format("#SWITCH r%d r%d", isSwitchResult.register1, isSwitchResult.register2));
                    }
                    
                    switchStartAddress = currentRamAddress + 10;
                    switchCasesRemaining = isSwitchResult.count;

                    currentRamAddress += 12;
                    continue;
                }

                int instruction = readUnsignedHalfword(bytes, offset);
                if (instruction == 0x46C0) {// nop
                    currentRamAddress += 2;
                    continue;
                }

                ArmFormat format = getThumbFormat(instruction);
                if (format == null)
                    throw new ArmDecodeException();

                int formatSize = format.getSize();
                if (formatSize == 4)
                    instruction = readWord(bytes, offset);

                ArmLine line = format.decode(instruction);
                ArmContext context = new ArmContext(overlay, bytes, initialRamAddress, currentRamAddress, dataValues, labelAddresses, addressMap);
                line.updateContext(context);
                if (i > 0)
                    addTabbedLine(lines, line.toString(context));

                currentRamAddress += formatSize;
            }
        }

        return lines;
    }

    private static class IsSwitchResult {
        boolean isSuccess = false;
        int register1 = -1;
        int register2 = -1;
        int count = -1;
    }

    private IsSwitchResult isSwitch(byte[] bytes, int offset) throws ArmDecodeException {
        IsSwitchResult result = new IsSwitchResult();

        if (offset + 12 > bytes.length)
            return result;

        int instruction0 = readUnsignedHalfword(bytes, offset);
        ArmThumbFormat format0 = getThumbFormat(instruction0);
        if (format0 == null || format0 != thumbFormats[2] /* Add/subtract */)
            return result;

        int instruction1 = readUnsignedHalfword(bytes, offset + 2);
        ArmThumbFormat format1 = getThumbFormat(instruction1);
        if (format1 == null || format1 != thumbFormats[5] /* Hi register operations/branch exchange */)
            return result;

        int instruction2 = readUnsignedHalfword(bytes, offset + 4);
        ArmThumbFormat format2 = getThumbFormat(instruction2);
        if (format2 == null || format2 != thumbFormats[10] /* Load/store halfword */)
            return result;

        int instruction3 = readUnsignedHalfword(bytes, offset + 6);
        ArmThumbFormat format3 = getThumbFormat(instruction3);
        if (format3 == null || format3 != thumbFormats[1] /* Move shifted register */)
            return result;

        int instruction4 = readUnsignedHalfword(bytes, offset + 8);
        ArmThumbFormat format4 = getThumbFormat(instruction4);
        if (format4 == null || format4 != thumbFormats[1] /* Move shifted register */)
            return result;

        int instruction5 = readUnsignedHalfword(bytes, offset + 10);
        ArmThumbFormat format5 = getThumbFormat(instruction5);
        if (format5 == null || format5 != thumbFormats[5] /* Hi register operations/branch exchange */)
            return result;

        ArmInstructionLine line0 = (ArmInstructionLine) format0.decode(instruction0);
        int register1 = line0.args[0].getValue();
        int register2 = line0.args[1].getValue();
        if (line0.args[2].getValue() != register2)
            return result;

        ArmInstructionLine line1 = (ArmInstructionLine) format1.decode(instruction1);
        if (line1.args[0].getValue() != register1 || line1.args[1].getValue() != ArmArg_Register.Type.pc.ordinal())
            return result;

        ArmInstructionLine line2 = (ArmInstructionLine) format2.decode(instruction2);
        if (line2.args[0].getValue() != register1 || line2.args[1].getValue() != register1 || line2.args[2].getValue() != 6)
            return result;

        ArmInstructionLine line3 = (ArmInstructionLine) format3.decode(instruction3);
        if (line3.args[0].getValue() != register1 || line3.args[1].getValue() != register1 || line3.args[2].getValue() != 16)
            return result;

        ArmInstructionLine line4 = (ArmInstructionLine) format4.decode(instruction4);
        if (line4.args[0].getValue() != register1 || line4.args[1].getValue() != register1 || line4.args[2].getValue() != 16)
            return result;

        ArmInstructionLine line5 = (ArmInstructionLine) format5.decode(instruction5);
        if (line5.args[0].getValue() != ArmArg_Register.Type.pc.ordinal() || line5.args[1].getValue() != register1)
            return result;

        // Confirm success
        result.isSuccess = true;
        result.register1 = register1;
        result.register2 = register2;

        // Check for previous setting of max value        
        for (int i = 1; i <= 3; i++) {
            int reverseOffset = offset - i * 2;
            if (reverseOffset < 0)
                break;

            int reverseInstruction = readUnsignedHalfword(bytes, reverseOffset);
            ArmFormat reverseFormat = getThumbFormat(reverseInstruction);
            if (reverseFormat == null || reverseFormat != thumbFormats[3])
                continue;
            
            ArmInstructionLine reverseLine = (ArmInstructionLine) reverseFormat.decode(reverseInstruction);

            if (reverseLine.operation.equalsIgnoreCase("cmp") && reverseLine.args[0].getValue() == register2) {
                result.count = reverseLine.args[1].getValue() + 1;
                break;
            }
        }

        return result;
    }

    private String getLabelName(int address) {
        return String.format("Label_0x%08X", address);
    }

    private void addTabbedLine(List<String> lines, String newLine) {
        lines.add(String.format("    %s", newLine));
    }

    private int readUnsignedHalfword(byte[] data, int offset) {
        return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
    }

    private int readWord(byte[] data, int offset) {
        return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8) | ((data[offset + 2] & 0xFF) << 16) | ((data[offset + 3] & 0xFF) << 24);
    }
    
    private ArmThumbFormat getThumbFormat(int instruction) {
        ArmFormat.Comparator comparator = new ArmFormat.Comparator();
        int formatIndex = Arrays.binarySearch(thumbFormats, instruction, comparator);
        if (formatIndex < 0)
            return null;
        
        for (int i = formatIndex + 1; i < thumbFormats.length; i++) {
            if (comparator.compare(thumbFormats[i], instruction) != 0)
                break;
            
            formatIndex++;
        }
        
        return thumbFormats[formatIndex];
    }
}
