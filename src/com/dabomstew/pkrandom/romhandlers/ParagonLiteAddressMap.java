package com.dabomstew.pkrandom.romhandlers;

import com.dabomstew.pkrandom.Utils;
import com.dabomstew.pkrandom.arm.ArmParser;

import java.util.*;

public class ParagonLiteAddressMap {
    public interface LabeledAddressInterface {
        String getLabel();
    }

    public interface ReferenceAddressInterface {
    }

    public static abstract class AddressBase {
        ParagonLiteOverlay overlay;
        int address;
        Set<ReferenceAddressInterface> incomingReferences;

        // Instruction
        AddressBase(ParagonLiteOverlay overlay, int address) {
            this.overlay = overlay;
            this.address = address;
            incomingReferences = new HashSet<>();
        }

        public int getRamAddress() {
            return address;
        }

        public void addReference(ReferenceAddressInterface addressData) {
            incomingReferences.add(addressData);
        }

        public int removeReference(ReferenceAddressInterface addressData) {
            incomingReferences.remove(addressData);
            return incomingReferences.size();
        }

        abstract boolean hasSize();

        abstract int getSize();

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AddressBase))
                return false;

            return overlay == ((AddressBase) obj).overlay && address == ((AddressBase) obj).address;
        }

        @Override
        public int hashCode() {
            return (overlay.number << 23) ^ address;
        }
    }

    public static class CodeAddress extends AddressBase implements LabeledAddressInterface {
        String label;
        int encoding;
        int size;

        CodeAddress(ParagonLiteOverlay overlay, String label, int address, int encoding) {
            super(overlay, address);
            this.label = label;
            this.encoding = encoding;

            switch (encoding) {
                case 2:
                    size = overlay.getFuncSizeRam(address);
                    break;
                case 4:
                    size = -1;
                    break;
                default:
                    throw new RuntimeException("Encoding should be 2 bytes (ARM Thumb) or 4 bytes (ARM)");
            }
        }

        public int getEncoding() {
            return encoding;
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        public boolean hasSize() {
            return encoding == 2;
        }

        @Override
        public int getSize() {
            if (encoding != 2)
                throw new RuntimeException(); // For now, we shouldn't be getting size of ARM functions

            return size;
        }
    }

    public static class DataAddress extends AddressBase implements LabeledAddressInterface {
        String label;
        int size;
        String refPattern;

        DataAddress(ParagonLiteOverlay overlay, String label, int address, int size, String refPattern) {
            super(overlay, address);

            this.label = label;
            this.size = size;
            this.refPattern = refPattern;

            if (size < 0)
                throw new RuntimeException();
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        boolean hasSize() {
            return size > 0;
        }

        @Override
        public int getSize() {
            return size;
        }
    }

    public static class ReferenceAddress extends AddressBase implements ReferenceAddressInterface {
        ReferenceAddress(ParagonLiteOverlay overlay, int address) {
            super(overlay, address);
        }

        @Override
        boolean hasSize() {
            return true;
        }

        @Override
        public int getSize() {
            return 4;
        }
    }

    private final Map<ParagonLiteOverlay, Map<String, LabeledAddressInterface>> labelMap = new HashMap<>();
    private final Map<ParagonLiteOverlay, TreeMap<Integer, LabeledAddressInterface>> addressMap = new HashMap<>();
    private final Map<String, ParagonLiteOverlay> namespaceToOverlay = new HashMap<>();

    public ParagonLiteAddressMap() {
    }

    private void addToMaps(ParagonLiteOverlay overlay, LabeledAddressInterface labeledAddress) {
        if (!(labeledAddress instanceof AddressBase))
            throw new RuntimeException();

        String label = labeledAddress.getLabel();
        labelMap.get(overlay).put(label, labeledAddress);
        addressMap.get(overlay).put(((AddressBase) labeledAddress).address, labeledAddress);
    }

    public void registerOverlay(ParagonLiteOverlay overlay) {
        if (labelMap.containsKey(overlay))
            throw new RuntimeException();

        labelMap.put(overlay, new HashMap<>());
        addressMap.put(overlay, new TreeMap<>());
        namespaceToOverlay.put(overlay.name, overlay);
    }

    public void registerCodeAddress(String namespace, String label, int address, int encoding, boolean addReferences) {
        ParagonLiteOverlay overlay = namespaceToOverlay.get(namespace);
        registerCodeAddress(overlay, label, address, encoding, addReferences);
    }

    public void registerCodeAddress(ParagonLiteOverlay overlay, String label, int address, int encoding) {
        registerCodeAddress(overlay, label, address, encoding, true);
    }

    public void registerCodeAddress(ParagonLiteOverlay overlay, String label, int address, int encoding, boolean addReferences) {
        // Already exists
        if (labelMap.get(overlay).containsKey(label))
            throw new RuntimeException(String.format("An entry for %s already exists in %s", label, overlay));

        CodeAddress codeAddress = new CodeAddress(overlay, label, address, encoding);

        if (addReferences)
            addCodeReferences(overlay, address);

        addToMaps(overlay, codeAddress);
    }

    public void registerDataAddress(String namespace, String label, int address, int size, String refPattern, boolean addReferences) {
        ParagonLiteOverlay overlay = namespaceToOverlay.get(namespace);
        registerDataAddress(overlay, label, address, size, refPattern, addReferences);
    }

    public void registerDataAddress(ParagonLiteOverlay overlay, String label, int address, int size, String refPattern) {
        registerDataAddress(overlay, label, address, size, refPattern, true);
    }

    public void registerDataAddress(ParagonLiteOverlay overlay, String label, int address, int size, String refPattern, boolean addReferences) {
        if (labelMap.get(overlay) == null)
            throw new RuntimeException();

        // Already exists        
        if (labelMap.get(overlay).containsKey(label))
            throw new RuntimeException(String.format("An entry for %s already exists in %s", label, overlay));

        DataAddress dataAddress = new DataAddress(overlay, label, address, size, refPattern);

        if (addReferences)
            addDataReferences(overlay, address);

        addToMaps(overlay, dataAddress);
    }

    public void relocateCodeAddress(ParagonLiteOverlay overlay, String label, int newAddress) {
        int oldAddress = getRamAddress(overlay, label);
        relocateCodeAddress(overlay, oldAddress, newAddress);
    }

    // Relocate existing code
    public void relocateCodeAddress(ParagonLiteOverlay overlay, int oldAddress, int newAddress) {
        if (oldAddress == newAddress)
            throw new RuntimeException();

        LabeledAddressInterface labeledAddress = addressMap.get(overlay).get(oldAddress);
        if (!(labeledAddress instanceof CodeAddress codeAddress))
            throw new RuntimeException();

        if (!codeAddress.incomingReferences.isEmpty()) {
            ArmParser armParser = new ArmParser(this);
            Map<Integer, Set<Integer>> oldOutgoingReferences = armParser.getOutgoingCodeReferences(overlay, oldAddress);
            Map<Integer, Set<Integer>> newOutgoingReferences = armParser.getOutgoingCodeReferences(overlay, newAddress);

            relocateAddressInternal(codeAddress, newAddress, oldOutgoingReferences, newOutgoingReferences);
        }

        addressMap.get(overlay).put(newAddress, codeAddress);
        addressMap.get(overlay).remove(codeAddress.address);
        codeAddress.address = newAddress;
    }

    public void relocateDataAddress(ParagonLiteOverlay overlay, String label, int newAddress, int newSize, String refPattern) {
        int oldAddress = getRamAddress(overlay, label);
        relocateDataAddress(overlay, oldAddress, newAddress, newSize, refPattern);
    }

    public void relocateDataAddress(ParagonLiteOverlay overlay, int oldAddress, int newAddress, int newSize, String refPattern) {
        LabeledAddressInterface labeledAddress = addressMap.get(overlay).get(oldAddress);
        if (!(labeledAddress instanceof DataAddress dataAddress))
            throw new RuntimeException();

        addressMap.get(overlay).put(newAddress, dataAddress);

        int oldSize = dataAddress.size;

        if (dataAddress.incomingReferences.isEmpty())
            return;

        ArmParser armParser = new ArmParser(this);
        int oldStartingOffset = oldAddress - overlay.address;
        Map<Integer, Set<Integer>> oldOutgoingReferences = armParser.getOutgoingDataReferences(overlay.data, oldStartingOffset,
                overlay.address, oldSize, refPattern);
        int newStartingOffset = newAddress - overlay.address;
        Map<Integer, Set<Integer>> newOutgoingReferences = armParser.getOutgoingDataReferences(overlay.data, newStartingOffset,
                overlay.address, newSize, refPattern);

        relocateAddressInternal(dataAddress, newAddress, oldOutgoingReferences, newOutgoingReferences);

        dataAddress.address = newAddress;
        dataAddress.size = newSize;
    }

    private void relocateAddressInternal(AddressBase addressBase, int newAddress, Map<Integer, Set<Integer>> oldOutgoingReferences, Map<Integer, Set<Integer>> newOutgoingReferences) {
        long startTime = System.currentTimeMillis();
        System.out.printf("relocating address 0x%08X -> 0x%08X", addressBase.address, newAddress);

        int total = oldOutgoingReferences.size() + newOutgoingReferences.size() + addressBase.incomingReferences.size();
        int current = 0;

        int oldAddress = addressBase.address;

        // Gather old outgoing references
        Map<AddressBase, List<ReferenceAddressInterface>> referencesToRemove = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> entry : oldOutgoingReferences.entrySet()) {
            int destinationAddress = entry.getKey();
            ParagonLiteOverlay destinationOverlay = findOverlay(destinationAddress, addressBase.overlay);
            if (destinationOverlay == null) {
                Utils.printProgress(total, current++, String.format("old outgoing: 0x%08X", destinationAddress));
                continue;
            }

            LabeledAddressInterface destinationLabeledAddress = addressMap.get(destinationOverlay).get(destinationAddress);
            if (destinationLabeledAddress == null) {
                Utils.printProgress(total, current++, String.format("old outgoing: %s::Unk_%X", destinationOverlay.name, destinationAddress));
                continue;
            }

            AddressBase destinationAddressBase = (AddressBase) destinationLabeledAddress;
            Utils.printProgress(total, current++, String.format("old outgoing: %s::%s (0x%08X)", destinationOverlay.name, destinationLabeledAddress.getLabel(), destinationAddress));

            for (int sourceAddress : entry.getValue()) {
                ReferenceAddress referenceAddress = new ReferenceAddress(addressBase.overlay, sourceAddress);

                if (!referencesToRemove.containsKey(destinationAddressBase)) {
                    referencesToRemove.put(destinationAddressBase, new ArrayList<>());
                    ++total;
                }

                referencesToRemove.get(destinationAddressBase).add(referenceAddress);
            }
        }

        // Add new outgoing references to destinations
        for (Map.Entry<Integer, Set<Integer>> entry : newOutgoingReferences.entrySet()) {
            int destinationAddress = entry.getKey();

            ParagonLiteOverlay destinationOverlay = findOverlay(destinationAddress, addressBase.overlay);
            if (destinationOverlay == null) {
                Utils.printProgress(total, current++, String.format("new outgoing: 0x%08X", destinationAddress));
                continue;
            }

            AddressBase destinationAddressBase = getAddressData(destinationOverlay, destinationAddress);
            if (destinationAddressBase == null)
                continue;

            if (destinationAddressBase instanceof LabeledAddressInterface labeledAddress)
                Utils.printProgress(total, current++, String.format("new outgoing: %s::%s (0x%08X)",
                        destinationAddressBase.overlay.name, labeledAddress.getLabel(), destinationAddress));
            else
                Utils.printProgress(total, current++, String.format("new outgoing: 0x%08X", destinationAddress));

            for (int sourceAddress : entry.getValue()) {
                ReferenceAddress referenceAddress = new ReferenceAddress(addressBase.overlay, sourceAddress);
                destinationAddressBase.addReference(referenceAddress);
            }
        }

        // Remove old outgoing references
        for (Map.Entry<AddressBase, List<ReferenceAddressInterface>> entry : referencesToRemove.entrySet()) {
            AddressBase destinationAddressBase = entry.getKey();
            if (destinationAddressBase instanceof LabeledAddressInterface labeledAddress)
                Utils.printProgress(total, current++, String.format("remove outgoing: %s::%s (0x%08X)",
                        destinationAddressBase.overlay.name, labeledAddress.getLabel(), destinationAddressBase.address));
            else
                Utils.printProgress(total, current++, String.format("remove outgoing: 0x%08X", destinationAddressBase.address));

            for (ReferenceAddressInterface referenceAddress : entry.getValue()) {
                destinationAddressBase.removeReference(referenceAddress);
            }
        }

        // Update incoming references
        // We convert to a list first because the original will have entries removed as this iterates
        List<ReferenceAddressInterface> incomingReferences = addressBase.incomingReferences.stream().toList();
        for (ReferenceAddressInterface source : incomingReferences) {
            if (!(source instanceof ReferenceAddress referenceAddress))
                throw new RuntimeException();

            ParagonLiteOverlay sourceOverlay = referenceAddress.overlay;
            int sourceAddress = referenceAddress.address;

            Utils.printProgress(total, current++, String.format("incoming: %s 0x%08X", sourceOverlay.name, sourceAddress));

            int value = sourceOverlay.readWord(sourceAddress);
            if (value == oldAddress) {
                sourceOverlay.writeWord(sourceAddress, newAddress, true);
                continue;
            }

            if (value == oldAddress + 1) {
                sourceOverlay.writeWord(sourceAddress, newAddress + 1, true);
                continue;
            }

            // long branch with link
            if ((value & 0xF800E800) != 0xF800E000)
                throw new RuntimeException();

            int offset = newAddress - (sourceAddress + 4);

            int instruction1 = (value & 0xF800) | ((offset >> 12) & 0x07FF);
            int instruction2 = ((value >> 16) & 0xF800) | ((offset >> 1) & 0x07FF);

            sourceOverlay.writeHalfword(sourceAddress, instruction1);
            sourceOverlay.writeHalfword(sourceAddress + 2, instruction2);
        }

        Utils.printProgressFinished(startTime, total);
    }

    public void removeAddressData(ParagonLiteOverlay overlay, int address) {
        LabeledAddressInterface labeledAddress = addressMap.get(overlay).get(address);
        if (labeledAddress == null)
            return;

        String label = labeledAddress.getLabel();
        removeAddressData(overlay, label, address);
    }

    private void removeAddressData(ParagonLiteOverlay overlay, String label, int address) {
        Map<String, LabeledAddressInterface> labelOverlayMap = labelMap.get(overlay); // 915
        Map<Integer, LabeledAddressInterface> addressOverlayMap = addressMap.get(overlay); // 916

        if (labelOverlayMap.size() == addressOverlayMap.size()) {
            labelOverlayMap.remove(label);
            addressOverlayMap.remove(address);
            return;
        }

        // At this point, we are assuming that there was a relocation
        // We should only have one more address and one fewer label
        if (labelOverlayMap.size() + 1 != addressOverlayMap.size())
            throw new RuntimeException();

        addressOverlayMap.remove(address);
    }

    public AddressBase getAddressData(String namespace, String label) {
        ParagonLiteOverlay overlay = namespaceToOverlay.get(namespace);
        return getAddressData(overlay, label);
    }

    public AddressBase getAddressData(ParagonLiteOverlay overlay, String label) {
        if (overlay == null)
            return null;

        LabeledAddressInterface labeledAddress = labelMap.get(overlay).get(label);
        if (!(labeledAddress instanceof AddressBase))
            throw new RuntimeException(String.format("Unknown function \"%s::%s\"", overlay.name, label));

        return (AddressBase) labeledAddress;
    }

    public AddressBase getAddressData(ParagonLiteOverlay overlay, int address) {
        if (overlay == null)
            return null;

        LabeledAddressInterface labeledAddress = addressMap.get(overlay).get(address);
        if (labeledAddress == null)
            return null;

        if (!(labeledAddress instanceof AddressBase))
            throw new RuntimeException();

        return (AddressBase) labeledAddress;
    }

    public void addReference(int destinationAddress, ParagonLiteOverlay sourceOverlay, int sourceAddress) {
        ParagonLiteOverlay destinationOverlay = findOverlay(destinationAddress, sourceOverlay);
        addReference(destinationOverlay, destinationAddress, sourceOverlay, sourceAddress);
    }

    public void addReference(ParagonLiteOverlay destinationOverlay, int destinationAddress, int sourceAddress) {
        ParagonLiteOverlay sourceOverlay = findOverlay(sourceAddress, destinationOverlay);
        addReference(destinationOverlay, destinationAddress, sourceOverlay, sourceAddress);
    }

    public void addReference(String destinationNamespace, String destinationLabel, ParagonLiteOverlay sourceOverlay, int sourceAddress) {
        ParagonLiteOverlay destinationOverlay = namespaceToOverlay.get(destinationNamespace);
        LabeledAddressInterface labeledAddress = labelMap.get(destinationOverlay).get(destinationLabel);
        if (!(labeledAddress instanceof AddressBase destinationAddressBase))
            throw new RuntimeException(String.format("Could not find function %s in file %s", destinationLabel, destinationOverlay.name));

        addReference(destinationOverlay, destinationAddressBase.address, sourceOverlay, sourceAddress);
    }

    public void addReference(ParagonLiteOverlay destinationOverlay, int destinationAddress, ParagonLiteOverlay sourceOverlay, int sourceAddress) {
        if (destinationOverlay == null)
            return; // invalid overlay

        LabeledAddressInterface labeledAddress = addressMap.get(destinationOverlay).get(destinationAddress);
        if (labeledAddress == null)
            return; // unknown address

        if (!(labeledAddress instanceof AddressBase addressBase))
            throw new RuntimeException();

        addressBase.addReference(new ReferenceAddress(sourceOverlay, sourceAddress));
    }

    public void addAllReferences() {

        System.out.println("adding references...");
        for (Map.Entry<ParagonLiteOverlay, TreeMap<Integer, LabeledAddressInterface>> overlayEntry : addressMap.entrySet()) {
            ParagonLiteOverlay overlay = overlayEntry.getKey();
            System.out.println("- " + overlay.name);

            int current = 0;
            int total = overlayEntry.getValue().size();
            long startTime = System.currentTimeMillis();

            for (Map.Entry<Integer, LabeledAddressInterface> entry : overlayEntry.getValue().entrySet()) {
                Utils.printProgress(total, current, String.format("%s::%s", overlay.name, entry.getValue().getLabel()));

                // This is disgusting
                if (entry.getValue() instanceof CodeAddress)
                    addCodeReferences(overlay, entry.getKey());
                else if (entry.getValue() instanceof DataAddress)
                    addDataReferences(overlay, entry.getKey());
                else
                    throw new RuntimeException();

                ++current;
            }
            Utils.printProgressFinished(startTime, total);
        }
        System.out.println();
    }

    public void addCodeReferences(ParagonLiteOverlay overlay, int address) {
        AddressBase addressBase = getAddressData(overlay, address);
        if (addressBase == null)
            return;

        if (!(addressBase instanceof CodeAddress))
            throw new RuntimeException();

        ArmParser armParser = new ArmParser(this);
        Map<Integer, Set<Integer>> references = armParser.getOutgoingCodeReferences(overlay, address);

        addReferencesInternal(overlay, references);
    }

    public void addDataReferences(ParagonLiteOverlay overlay, int address) {
        AddressBase addressBase = getAddressData(overlay, address);
        if (addressBase == null)
            return;

        if (!(addressBase instanceof DataAddress dataAddress))
            throw new RuntimeException();

        int size = dataAddress.getSize();

        ArmParser armParser = new ArmParser(this);
        int startingOffset = address - overlay.address;
        Map<Integer, Set<Integer>> references = armParser.getOutgoingDataReferences(overlay.data, startingOffset, overlay.address, size, dataAddress.refPattern);

        addReferencesInternal(overlay, references);
    }

    private void addReferencesInternal(ParagonLiteOverlay sourceOverlay, Map<Integer, Set<Integer>> references) {
        for (Map.Entry<Integer, Set<Integer>> entry : references.entrySet()) {
            int destinationAddress = entry.getKey();
            for (int sourceAddress : entry.getValue()) {
                addReference(destinationAddress, sourceOverlay, sourceAddress);
            }
        }
    }

    public static class RemovalData {
        final int address;
        final int referenceCount;
        final int size;

        public RemovalData(int address, int referenceCount, int size) {
            this.address = address;
            this.referenceCount = referenceCount;
            this.size = size;
        }
    }

    public RemovalData removeReference(int destinationAddress, ParagonLiteOverlay sourceOverlay, int sourceAddress) {
        ParagonLiteOverlay destinationOverlay = findOverlay(destinationAddress, sourceOverlay);
        if (destinationOverlay == null)
            return null;

        LabeledAddressInterface destinationLabeledAddress = addressMap.get(destinationOverlay).get(destinationAddress);
        if (destinationLabeledAddress == null)
            return null;

        if (!(destinationLabeledAddress instanceof AddressBase destinationAddressBase))
            throw new RuntimeException();

        int count = destinationAddressBase.removeReference(new ReferenceAddress(sourceOverlay, sourceAddress));
        if (count == 0) {
            removeAddressData(destinationOverlay, destinationAddress);
        }

        if (!destinationAddressBase.hasSize())
            return null;

        return new RemovalData(destinationAddress, count, destinationAddressBase.getSize());
    }

    public int getRamAddress(String namespace, String label) {
        return getAddressData(namespace, label).address;
    }

    public int getRamAddress(ParagonLiteOverlay overlay, String label) {
        return getAddressData(overlay, label).address;
    }

    public int getRomAddress(ParagonLiteOverlay overlay, String label) {
        int ramAddress = getAddressData(overlay, label).address;
        return overlay.ramToRomAddress(ramAddress);
    }

    public ParagonLiteOverlay findOverlay(int address, ParagonLiteOverlay contextOverlay) {
        List<ParagonLiteOverlay> overlays = new ArrayList<>();
        for (Map.Entry<ParagonLiteOverlay, Map<String, LabeledAddressInterface>> entry : labelMap.entrySet()) {
            ParagonLiteOverlay ovl = entry.getKey();
            int ovlEarliestRamAddress = ovl.getLowerBoundRamAddress();
            int ovlEnd = ovl.getUpperBoundRamAddress();
            if (address >= ovlEarliestRamAddress && address < ovlEnd)
                overlays.add(ovl);
        }

        if (overlays.isEmpty())
            return null; // don't default

        if (overlays.size() == 1)
            return overlays.get(0);

        ParagonLiteOverlay selectedOverlay = null;
        for (ParagonLiteOverlay overlay : overlays) {
            if (!contextOverlay.hasContextOverlay(overlay))
                continue;
                
            if (!addressMap.get(overlay).containsKey(address))
                continue;

            if (selectedOverlay != null)
                throw new RuntimeException(String.format("Already found Overlay %d (%s) to contain address 0x%08X", overlay.number, overlay.name, address));

            selectedOverlay = overlay;
        }

//        if (selectedOverlay == null)
//            throw new RuntimeException(String.format("Could not find function at address 0x%08X within context of %s", address, contextOverlay));
        
        return selectedOverlay == null ? contextOverlay : selectedOverlay;
    }

    public String replaceLabelsInExpression(String expression) {
        if (!expression.contains("::"))
            return expression;

        for (Map.Entry<ParagonLiteOverlay, Map<String, LabeledAddressInterface>> overlayEntry : labelMap.entrySet()) {
            ParagonLiteOverlay overlay = overlayEntry.getKey();
            String namespace = overlay.name;
            if (!expression.contains(namespace + "::"))
                continue;

            Map<String, LabeledAddressInterface> addressDataMap = overlayEntry.getValue();
            for (Map.Entry<String, LabeledAddressInterface> addressDataEntry : addressDataMap.entrySet()) {
                if (!(addressDataEntry.getValue() instanceof AddressBase addressBase))
                    throw new RuntimeException();

                String label = addressDataEntry.getKey();
                int address = addressBase.address;
                expression = expression.replace(String.format("%s::%s", namespace, label), String.valueOf(address));
            }
        }

        return expression;
    }

    public boolean isValidLabel(ParagonLiteOverlay overlay, String label) {
        return labelMap.get(overlay).containsKey(label);
    }

    public static boolean isValidAddress(int address, boolean allowPlus1) {
        return address >= 0x0200000 && address < 0x08000000 && address % 4 <= (allowPlus1 ? 1 : 0);
    }
}
