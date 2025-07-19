package com.dabomstew.pkrandom.romhandlers.hack;

import com.dabomstew.pkrandom.Utils;
import com.dabomstew.pkrandom.arm.ArmParser;
import com.dabomstew.pkrandom.romhandlers.*;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.*;
import java.util.function.Function;

public abstract class BattleObjectHackModCollection<T extends BattleObjectHackMod> extends HackModCollection<T> {

    protected static class NewFirstComparator implements Comparator<HackMod> {
        int maxExisting;

        protected NewFirstComparator(int maxExisting) {
            this.maxExisting = maxExisting;
        }

        @Override
        public int compare(HackMod o1, HackMod o2) {
            BattleObjectHackMod bo1 = (BattleObjectHackMod) o1;
            BattleObjectHackMod bo2 = (BattleObjectHackMod) o2;

            if (bo1.number > maxExisting == bo2.number > maxExisting)
                return Integer.compare(bo1.number, bo2.number);

            return bo1.number > maxExisting ? -1 : 1;
        }
    }

    Gen5RomHandler romHandler;
    protected ParagonLiteOverlay battleOvl;
    protected ParagonLiteOverlay battleServerOvl;
    protected ParagonLiteAddressMap globalAddressMap;
    
    List<String> names;
    List<String> descriptions;
    List<String> explanations;

    protected class EventHandler {
        int type;
        int address = -1;

        EventHandler(int type) {
            this.type = type;
        }

        EventHandler(int type, String filename) {
            setFromFuncName(type, filename);
        }

        EventHandler(int type, int existingObject) {
            this.type = type;

            int referenceAddress = getEventHandlerFuncReferenceAddress(existingObject, getEffectListNumReferenceOffset(), getEffectListCount(), type);
            this.address = battleOvl.readWord(referenceAddress) - 1;
        }

        protected void setFromFuncName(int type, String fullFuncName) {
            this.type = type;

            if (fullFuncName.contains("::")) {
                address = getFuncAddress(fullFuncName);
                return;
            }

            String fileName = fullFuncName;
            if (!fileName.endsWith(".s"))
                fileName += ".s";

            if (!battleOvl.isValidLabel(fullFuncName)) {
                List<String> lines = readLines(String.format("eventhandlers/%s/%s", getFunctionDirectory(), fileName));

                battleOvl.writeCode(lines, fullFuncName, false);
            }

            address = battleOvl.getRamAddress(fullFuncName);
        }
    }

    protected class Table {
        private final SortedSet<Integer> values;
        private final int compareAddress;
        private final int dataRefAddress;
        private final int initialCount;
        private final Function<BattleObjectHackMod, Boolean> updatePredicate;

        protected Table(String functionName, int compareOffset, int dataOffset, Function<BattleObjectHackMod, Boolean> updatePredicate) {
            int functionRomAddress = battleServerOvl.getRomAddress(functionName);
            compareAddress = functionRomAddress + compareOffset;
            dataRefAddress = functionRomAddress + dataOffset;
            this.updatePredicate = updatePredicate;

            initialCount = battleServerOvl.readUnsignedByte(compareAddress);
            int dataRamAddress = battleServerOvl.readWord(dataRefAddress);
            int dataRomAddress = battleServerOvl.ramToRomAddress(dataRamAddress);

            values = new TreeSet<>();
            for (int i = 0; i < initialCount; ++i) {
                int ability = battleServerOvl.readUnsignedHalfword(dataRomAddress + i * 2);
                values.add(ability);
            }
        }

        public void update(BattleObjectHackMod hackMod) {
            Boolean newValue = updatePredicate.apply(hackMod);
            if (newValue == null)
                return;

            update(hackMod.number, newValue);
        }

        public void update(int number, boolean newValue) {
            if (newValue)
                values.add(number);
            else
                values.remove(number);
        }

        public void apply() {
            if (values.size() > 255)
                throw new RuntimeException();

            // count
            battleServerOvl.writeByte(compareAddress, values.size());

            byte[] bytes = getValuesAsBytes();

            if (values.size() > initialCount) {
                // too large, must go in Battle

                int romAddress = battleOvl.writeDataUnnamed(bytes);
                int ramAddress = battleOvl.romToRamAddress(romAddress);

                battleServerOvl.writeWord(dataRefAddress, ramAddress, true);
                return;
            }

            int dataRamAddress = battleServerOvl.readWord(dataRefAddress);
            int dataRomAddress = battleServerOvl.ramToRomAddress(dataRamAddress);
            battleServerOvl.writeBytes(dataRomAddress, bytes);
        }

        private byte[] getValuesAsBytes() {
            byte[] bytes = new byte[values.size() * 2];
            int i = 0;
            for (int ability : values) {
                bytes[i] = (byte) (ability & 0xFF);
                bytes[i + 1] = (byte) ((ability >> 8) & 0xFF);
                ++i;
            }

            return bytes;
        }
    }

    @SafeVarargs
    BattleObjectHackModCollection(T... hackMods) {
        super(Arrays.stream(hackMods).toList());
    }

    @Override
    public void apply(Context context) {
        romHandler = context.romHandler();
        battleOvl = context.overlays().get(OverlayId.BATTLE);
        battleServerOvl = context.overlays().get(OverlayId.BATTLE_SERVER);
        globalAddressMap = context.globalAddressMap();

        sortHackMods(new NewFirstComparator(getMaxVanillaObjectNumber()));
        
        ParagonLiteOverlay overlay = getOverlay();

        names = getTexts(getNamesTextOffsetKey());
        descriptions = getTexts(getDescriptionsTextOffsetKey());
        explanations = romHandler.isUpperVersion() ? getTexts(getExplanationsTextOffsetKey()) : null;

        registerBattleObjects();

        List<Table> tables = getTables();

        String effectListLabel = getEffectListLabel();
        int baseListRomAddress = overlay.getRomAddress(effectListLabel);
        int baseEffectListCount = getEffectListCount();

        Map<Integer, List<EventHandler>> objectToEffects = new TreeMap<>();
        Map<Integer, Integer> existingRedirectors = new TreeMap<>();
        for (int i = 0; i < baseEffectListCount; ++i) {
            int number = overlay.readWord(baseListRomAddress + 8 * i);
            int redirectorAddress = overlay.readWord(baseListRomAddress + 8 * i + 4);
            objectToEffects.put(number, null);
            existingRedirectors.put(number, redirectorAddress);
        }

        for (BattleObjectHackMod hackMod : hackMods) {
            int number = hackMod.number;

            // Name
            String name = hackMod.getName(context);
            setText(hackMod, names, name);

            // Description
            GameText description = hackMod.getDescription(context);
            setText(hackMod, descriptions, description);

            // Explanation
            GameText explanation = hackMod.getExplanation(context);
            setText(hackMod, explanations, explanation);

            // Event Handlers
            List<BattleObjectHackMod.QueueEntry> queueEntries = new ArrayList<>();
            hackMod.populateQueueEntries(context, queueEntries);

            // Tables
            for (Table table : tables)
                table.update(hackMod);

            // Effects
            if (queueEntries.isEmpty()) {
                objectToEffects.remove(number);
                continue;
            }

            List<EventHandler> eventHandlers = new ArrayList<>(queueEntries.size());
            for (BattleObjectHackMod.QueueEntry queueEntry : queueEntries) {
                switch (queueEntry.type) {
                    case EXISTING -> eventHandlers.add(new EventHandler(queueEntry.eventType));
                    case NEW -> eventHandlers.add(new EventHandler(queueEntry.eventType, queueEntry.filename));
                    case CLONE -> eventHandlers.add(new EventHandler(queueEntry.eventType, queueEntry.cloneNumber));
                    default -> throw new IllegalStateException("Unexpected value: " + queueEntry.type);
                }
            }
            objectToEffects.put(number, eventHandlers);
        }
        
        boolean useTerminator = baseEffectListCount <= 255 && objectToEffects.size() > 255;

        // relocate
        
        int newSize = (objectToEffects.size() + (useTerminator ? 1 : 0)) * 8;
        byte[] newData = new byte[newSize];
        int effectIndex = 0;
        for (int number : objectToEffects.keySet()) {
            writeWord(newData, effectIndex * 8, number);

            List<EventHandler> eventHandlers = objectToEffects.get(number);
            if (eventHandlers == null) {
                if (!existingRedirectors.containsKey(number))
                    throw new RuntimeException();

                int existingRedirector = existingRedirectors.get(number);
                writeWord(newData, effectIndex * 8 + 4, existingRedirector);
            }

            ++effectIndex;
        }
        
        if (useTerminator)
            writeWord(newData, newSize - 8, 0xFFFFFFFF);
        
        int newEventListAddress = overlay.newData(newData, effectListLabel, "4*");

        // Populate new values
        effectIndex = 0;
        for (int number : objectToEffects.keySet()) {
            List<EventHandler> eventHandlers = objectToEffects.get(number);

            if (eventHandlers == null)
                continue;

            setBattleObject(number, effectIndex, newEventListAddress, eventHandlers);

            ++effectIndex;
        }

        int addFunctionRomAddress = getAddFunctionRomAddress();
        int effectListNumReferenceOffset = getEffectListNumReferenceOffset();
        int effectListFuncReferenceOffset = getEffectListFuncReferenceOffset();
        int effectListCompareOffset = getEffectListCompareOffset();
        int effectListCountOffset = getEffectListCountOffset();

        overlay.writeWord(addFunctionRomAddress + effectListNumReferenceOffset, newEventListAddress, false);
        overlay.writeWord(addFunctionRomAddress + effectListFuncReferenceOffset, newEventListAddress + 4, false);
        
        if (useTerminator) {
            // we've exceeded the normal amount for byte comparison
            // we use a terminating value of -1 and loop
            
            if (effectListCompareOffset <= 0)
                throw new RuntimeException();
            
            int effectListComparisonRomAddress = addFunctionRomAddress + effectListCompareOffset;
            
            overlay.writeHalfword(effectListComparisonRomAddress, 0x2800); // cmp r0, #0
            overlay.writeByte(effectListComparisonRomAddress + 3, 0xDC); // bgt
        } else if (effectListCompareOffset > 0) {
            // simple comparison edit
            
            if (objectToEffects.size() > 255)
                throw new RuntimeException();
            
            int effectListCompareRomAddress = addFunctionRomAddress + effectListCompareOffset;
            overlay.writeByte(effectListCompareRomAddress, objectToEffects.size());
        } else if (effectListCountOffset > 0) {
            // loaded value compare

            int effectListCountRomAddress = addFunctionRomAddress + effectListCountOffset;
            overlay.writeWord(effectListCountRomAddress, objectToEffects.size(), false);
        } else {
            throw new RuntimeException();
        }

        setTexts(getNamesTextOffsetKey(), names);
        setTexts(getDescriptionsTextOffsetKey(), descriptions);
        if (romHandler.isUpperVersion())
            setTexts(getExplanationsTextOffsetKey(), explanations);

        for (Table table : tables)
            table.apply();
    }

    protected abstract String getBattleObjectTypeName();
    protected abstract String getFunctionDirectory();

    protected abstract String getNamesTextOffsetKey();

    protected abstract String getDescriptionsTextOffsetKey();

    protected abstract String getExplanationsTextOffsetKey();

    protected abstract List<Table> getTables();

    protected void setText(BattleObjectHackMod hackMod, List<String> texts, GameText text) {
        setText(hackMod, texts, text.toString());
    }

    protected void setText(BattleObjectHackMod hackMod, List<String> texts, String text) {
        if (texts == null || text == null)
            return;
        
        while (hackMod.number <= texts.size())
            texts.add("");

        texts.set(hackMod.number, text);
    }

    protected abstract int getMaxVanillaObjectNumber();

    private int getFuncAddress(String fullFuncName) {
        if (!fullFuncName.contains("::"))
            throw new RuntimeException(String.format("Could not find function \"%s\".", fullFuncName));

        String[] strs = fullFuncName.split("::", 2);
        return globalAddressMap.getRamAddress(strs[0], strs[1]);
    }

    protected int getEventHandlerFuncReferenceAddress(int objectNumber, int objectListAddress, int objectListCount, int eventType) {
        int redirectorAddress = getRedirectorAddress(objectNumber, objectListAddress, objectListCount);
        int eventListCount = getEventHandlerListCountFromRedirector(redirectorAddress);
        int eventListAddress = getEventHandlerListAddressFromRedirector(redirectorAddress);

        for (int i = 0; i < eventListCount; ++i) {
            if (eventType == battleOvl.readWord(eventListAddress + 8 * i)) {
                return eventListAddress + 8 * i + 4;
            }
        }

        throw new RuntimeException(String.format("Could not find event handler on object %d for event type 0x%02X", objectNumber, eventType));
    }

    private int getRedirectorAddress(int objectNumber, int objectListAddress, int objectListCount) {
        for (int i = 0; i < objectListCount; ++i) {
            if (objectNumber == battleOvl.readWord(objectListAddress + 8 * i)) {
                return battleOvl.readWord(objectListAddress + 8 * i + 4) - 1;
            }
        }

        throw new RuntimeException(String.format("Could not find object of index %d in list", objectNumber));
    }

    private int getEventHandlerListCountFromRedirector(int redirectorRamAddress) {
        int countSetAddress = getRedirectorCountSetAddress(redirectorRamAddress);
        if (countSetAddress <= 0) throw new RuntimeException();

        return battleOvl.readUnsignedByte(countSetAddress);
    }

    private int getRedirectorCountSetAddress(int funcRamAddress) {
        ParagonLiteOverlay overlay = getOverlay();
        
        int returnValue = -1;

        int funcSize = overlay.getFuncSizeRam(funcRamAddress);
        for (int i = 0; i < funcSize; i += 2) {
            int instruction = overlay.readUnsignedHalfword(funcRamAddress + i);

            // mov r1
            if ((instruction & 0xFF00) == 0x2100) {
                returnValue = funcRamAddress + i;
                continue;
            }

            // bx lr
            if ((instruction & 0xFF00) == 0x4700) break;

            // pop
            if ((instruction & 0xFE00) == 0xBC00) break;
        }

        return returnValue;
    }

    private int getEventHandlerListAddressFromRedirector(int redirectorAddress) {
        int eventListReferenceAddress = getRedirectorListReferenceRomAddress(redirectorAddress);
        if (eventListReferenceAddress <= 0) throw new RuntimeException();

        return battleOvl.readWord(eventListReferenceAddress);
    }

    private int getRedirectorListReferenceRomAddress(int funcRamAddress) {
        ParagonLiteOverlay overlay = getOverlay();
        
        int returnValue = -1;

        int funcSize = overlay.getFuncSizeRam(funcRamAddress);
        for (int i = 0; i < funcSize; i += 2) {
            int instruction = overlay.readUnsignedHalfword(overlay.ramToRomAddress(funcRamAddress) + i);

            // ldr r0
            if ((instruction & 0xFF00) == 0x4800) {
                int eventListBranchOffset = i + ((instruction & 0x00FF) << 2) + 4;
                returnValue = alignWord(funcRamAddress + eventListBranchOffset);
                continue;
            }

            // bx lr
            if ((instruction & 0xFF00) == 0x4700) break;

            // pop
            if ((instruction & 0xFE00) == 0xBC00) break;
        }

        return overlay.ramToRomAddress(returnValue);
    }

    protected int getBattleObjectIndex(int listRamAddress, int listCount, int objectNumber) {
        // Find exact match
        for (int i = 0; i < listCount; ++i) {
            int address = listRamAddress + i * 8;
            int thisObjectNumber = battleOvl.readWord(address);
            if (objectNumber == thisObjectNumber) {
                return i;
            }
        }

        // Find first empty
        for (int i = 0; i < listCount; ++i) {
            int address = listRamAddress + i * 8;
            int thisObjectNumber = battleOvl.readWord(address);
            if (thisObjectNumber == 0) {
                return i;
            }
        }

        return -1;
    }

    protected final void setBattleObject(int number, int index, int effectListAddress, List<EventHandler> eventHandlers) {
        var overlay = getOverlay();

        int eventHandlerListSize = eventHandlers.size() * 8;

        for (EventHandler eventHandler : eventHandlers) {
            if (eventHandler.address > 0) continue;

            int redirectorAddress = overlay.readWord(effectListAddress + index * 8 + 4) - 1;
            int eventHandlerListAddress = getEventHandlerListAddressFromRedirector(redirectorAddress);
            int eventHandlerListCount = getEventHandlerListCountFromRedirector(redirectorAddress);

            for (int i = 0; i < eventHandlerListCount; ++i) {
                int eventHandlerType = overlay.readWord(eventHandlerListAddress + i * 8);
                if (eventHandlerType == eventHandler.type) {
                    eventHandler.address = overlay.readWord(eventHandlerListAddress + i * 8 + 4) - 1;
                    break;
                }
            }

            if (eventHandler.address <= 0)
                throw new RuntimeException(String.format("Object %d did not have an event handler for 0x%02X", number, eventHandler.type));
        }

        // Write event handler list
        byte[] eventHandlerListData = new byte[eventHandlerListSize];
        for (int i = 0; i < eventHandlers.size(); ++i) {
            EventHandler eventHandler = eventHandlers.get(i);
            writeWord(eventHandlerListData, i * 8, eventHandler.type);
            writeWord(eventHandlerListData, i * 8 + 4, eventHandler.address + 1);
        }
        int eventHandlerListAddress = overlay.writeDataUnnamed(eventHandlerListData, "4*");

        // Write redirector function
        List<String> lines = Arrays.asList(
                "mov r1, #" + eventHandlers.size(),
                "str r1, [r0]",
                "ldr r0, =" + eventHandlerListAddress,
                "bx lr");
        int redirectorFuncAddress = overlay.writeCodeUnnamed(lines, false);

        // Write to object list
        overlay.writeWord(effectListAddress + index * 8, number, false);
        overlay.writeWord(effectListAddress + index * 8 + 4, redirectorFuncAddress + 1, true);
    }

    protected void registerBattleObjects() {
        ParagonLiteOverlay overlay = getOverlay();

        String battleObjectTypeName = getBattleObjectTypeName();
        int effectListRomAddress = overlay.getRomAddress(getEffectListLabel());
        int effectListCount = getEffectListCount();
        
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < effectListCount; ++i) {
            int objectNumberRomAddress = effectListRomAddress + i * 8;
            int sourceRomAddress = objectNumberRomAddress + 4;

            int objectNumber = overlay.readWord(objectNumberRomAddress);
            int redirectorRamAddress = overlay.readWord(sourceRomAddress) - 1;
            Utils.printProgress(effectListCount, i, String.format("%3d -> 0x%08X", objectNumber, redirectorRamAddress));

            if (objectNumber == 0)
                continue;

            int eventHandlerListCount = getEventHandlerListCountFromRedirector(redirectorRamAddress);
            int listReferenceRomAddress = getRedirectorListReferenceRomAddress(redirectorRamAddress);
            int eventHandlerListRamAddress = overlay.readWord(listReferenceRomAddress);

            for (int j = 0; j < eventHandlerListCount; ++j) {
                int eventHandlerReferenceRomAddress = overlay.ramToRomAddress(eventHandlerListRamAddress + j * 8 + 4);
                int eventHandlerRamAddress = overlay.readWord(eventHandlerReferenceRomAddress) - 1;

                String eventHandlerLabel = String.format("%sEventHandler_0x%08X", battleObjectTypeName, eventHandlerRamAddress);
                if (!globalAddressMap.isValidLabel(overlay, eventHandlerLabel))
                    // Register event handler
                    globalAddressMap.registerCodeAddress(overlay, eventHandlerLabel, eventHandlerRamAddress, 2);
            }

            // Register event handler list
            String eventHandlerListLabel = String.format("%sEventHandlerList_0x%08X", battleObjectTypeName, eventHandlerListRamAddress);
            if (!globalAddressMap.isValidLabel(overlay, eventHandlerListLabel))
                globalAddressMap.registerDataAddress(overlay, eventHandlerListLabel, eventHandlerListRamAddress, eventHandlerListCount * 8, "4*");

            // Register redirector
            String redirectorLabel = String.format("%sRedirector_0x%08X", battleObjectTypeName, redirectorRamAddress);
            if (!globalAddressMap.isValidLabel(overlay, redirectorLabel)) globalAddressMap.registerCodeAddress(overlay, redirectorLabel, redirectorRamAddress, 2);

            // Set reference from list to redirector
            globalAddressMap.addReference(overlay, redirectorRamAddress, overlay, overlay.romToRamAddress(sourceRomAddress));
        }
        
        Utils.printProgressFinished(startTime, effectListCount);
        System.out.println();
    }

    protected static int alignWord(int address) {
        return address & 0xFFFFFFFC;
    }

    protected abstract ParagonLiteOverlay getOverlay();

    protected abstract int getAddFunctionRomAddress();

    protected abstract int getEffectListCompareOffset();

    protected abstract int getEffectListCountOffset();

    protected abstract int getEffectListNumReferenceOffset();

    protected abstract int getEffectListFuncReferenceOffset();

    protected abstract String getEffectListLabel();

    protected int getEffectListCount() {
        int compareOffset = getEffectListCompareOffset();
        int countOffset = getEffectListCountOffset();

        if (compareOffset > 0 && countOffset <= 0) {
            int address = getAddFunctionRomAddress() + compareOffset;
            return battleOvl.readUnsignedByte(address);
        }

        if (compareOffset <= 0 && countOffset > 0) {
            int address = getAddFunctionRomAddress() + countOffset;
            return battleOvl.readWord(address);
        }

        throw new RuntimeException("Invalid CompareOffset and/or CountOffset");
    }

    protected List<String> getTexts(String key) {
        return romHandler.getStrings(false, romHandler.getRomInt(key));
    }

    protected void setTexts(String key, List<String> strings) {
        romHandler.setStrings(false, romHandler.getRomInt(key), strings);
    }
}
