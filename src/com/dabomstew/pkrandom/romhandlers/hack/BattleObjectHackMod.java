package com.dabomstew.pkrandom.romhandlers.hack;

import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BattleObjectHackMod extends HackMod {
    public enum QueueEntryType {
        EXISTING,
        NEW,
        CLONE
    }

    public static class QueueEntry {
        public final QueueEntryType type;
        public final int eventType;
        public final String filename;
        public final int cloneNumber;

        public QueueEntry(int eventType) {
            this.type = QueueEntryType.EXISTING;
            this.eventType = eventType;
            this.filename = null;
            this.cloneNumber = -1;
        }

        public QueueEntry(int eventType, String filename) {
            this.type = QueueEntryType.NEW;
            this.eventType = eventType;
            this.filename = filename;
            this.cloneNumber = -1;
        }

        public QueueEntry(int eventType, int cloneNumber) {
            this.type = QueueEntryType.CLONE;
            this.eventType = eventType;
            this.filename = null;
            this.cloneNumber = cloneNumber;
        }
    }

    public final int number;

    public BattleObjectHackMod(int number) {
        this.number = number;
    }

    @Override
    public void Merge(HackMod other) {
    }

    @Override
    public Set<Class<? extends HackMod>> getDependencies() {
        return Set.of();
    }

    public String getName(Context context) {
        return null;
    }

    public GameText getDescription(Context context) {
        return null;
    }

    public Dialogue getExplanation(Context context) {
        return null;
    }

    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of();
    }

    public abstract void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries);

    @Override
    public final void apply(Context context) {
        throw new RuntimeException("Battle Object Hack Mods should not be added directly; use a container instead!");
    }
}
