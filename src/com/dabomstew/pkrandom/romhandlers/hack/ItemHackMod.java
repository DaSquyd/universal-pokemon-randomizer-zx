package com.dabomstew.pkrandom.romhandlers.hack;

import java.util.List;

public class ItemHackMod extends BattleObjectHackMod {
    public ItemHackMod(int number) {
        super(number);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        
    }

    public String getDescription(List<String> itemDescriptions) {
        return null;
    }
}
