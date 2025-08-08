package com.dabomstew.pkrandom.romhandlers.hack.move.modern;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.OverlayId;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteOverlay;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

// Starting in Gen VI, consecutive usage of Protect, Detect, and other similar moves reduces the likelihood of success by a factor of 3 rather than 2
public class MoveHackMod_182_Protect_MoveChance extends MoveHackMod {
    public MoveHackMod_182_Protect_MoveChance() {
        super(Moves.protect);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        return true;
    }

    @Override
    public void postWriteEventHandlers(Context context) {
        ParagonLiteOverlay battleOvl = context.overlays.get(OverlayId.BATTLE);

        int romAddress = battleOvl.getRomAddress("MoveHandler_Protect_CheckFail");

        int maxStage = 7;
        byte[] bytes = new byte[maxStage * 2];
        for (int i = 0; i < maxStage; i++)
            writeHalf(bytes, i * 2, (int)Math.pow(3, i));

        int count = bytes.length / 2;

        battleOvl.writeByte(romAddress + 0x22, count); // cmp r0, #count
        battleOvl.writeByte(romAddress + 0x26, count - 1);  // mov r0, #(count - 1)
        battleOvl.writeHalfword(romAddress + 0x2A, 0x5A08); // ldrh r0, [r1, r0]

        int newDataAddress = battleOvl.romToRamAddress(battleOvl.writeData(bytes, "Data_ProtectionFailRates"));
        battleOvl.writeWord(romAddress + 0x54, newDataAddress, true);
    }
}
