package com.dabomstew.pkrandom.romhandlers.hack.move;

import com.dabomstew.pkrandom.romhandlers.OverlayId;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteOverlay;
import com.dabomstew.pkrandom.romhandlers.hack.HackMod;

import java.util.Set;

// Starting in Gen VI, consecutive usage of Protect, Detect, and other similar moves reduces the likelihood of success by a factor of 3 rather than 2
public class HackMod_ModernProtectMoveChance extends HackMod {
    @Override
    public Set<Class<? extends HackMod>> getDependencies() {
        return Set.of();
    }
    
    @Override
    public void registerGlobalValues(Context context) {
    }
    
    @Override
    public void apply(Context context) {
        ParagonLiteOverlay battleOvl = context.overlays().get(OverlayId.BATTLE);
        
        int romAddress = battleOvl.getRomAddress("Handler_MoveProtect_CheckFail");

        byte[] bytes = new byte[14];
        writeHalf(bytes, 0, 1);
        writeHalf(bytes, 2, 3);
        writeHalf(bytes, 4, 9);
        writeHalf(bytes, 6, 27);
        writeHalf(bytes, 8, 81);
        writeHalf(bytes, 10, 243);
        writeHalf(bytes, 12, 729);

        int count = bytes.length / 2;
        
        battleOvl.writeByte(romAddress + 0x22, count); // cmp r0, #count
        battleOvl.writeByte(romAddress + 0x26, count  - 1);  // mov r0, #(count - 1)
        battleOvl.writeHalfword(romAddress + 0x2A, 0x5A08); // ldrh r0, [r1, r0]
        
        int newDataAddress = battleOvl.romToRamAddress(battleOvl.writeData(bytes, "Data_ProtectionFailRates"));
        battleOvl.writeWord(romAddress + 0x54, newDataAddress, true);
    }

    @Override
    public void Merge(HackMod other) {
    }
}
