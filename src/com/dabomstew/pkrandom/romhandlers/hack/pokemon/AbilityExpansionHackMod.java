package com.dabomstew.pkrandom.romhandlers.hack.pokemon;

import com.dabomstew.pkrandom.romhandlers.OverlayId;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteOverlay;
import com.dabomstew.pkrandom.romhandlers.hack.HackMod;

import java.util.List;
import java.util.Set;

public class AbilityExpansionHackMod extends HackMod {
    @Override
    public Set<Class<? extends HackMod>> getDependencies() {
        return Set.of();
    }

    @Override
    public void apply(Context context) {
        ParagonLiteOverlay storageSystemOvl = context.overlays().get(OverlayId.STORAGE_SYSTEM);

        // Updates the personal data to allow for abilities up to index 1023
        List<String> readPersonalDataLines = readLines("read_poke_personal_data.s");
        context.arm9().writeCodeForceInline(readPersonalDataLines, "ReadPokePersonalData", false);

        // Also fixes the Azurill->Marill gender bug
        List<String> readBoxDataLines = readLines("read_poke_box_data.s");
        context.arm9().writeCodeForceInline(readBoxDataLines, "ReadPokeBoxData", true);

        // Also fixes the Azurill->Marill gender bug
        List<String> writeBoxDataLines = readLines("write_poke_box_data.s");
        context.arm9().writeCodeForceInline(writeBoxDataLines, "WritePokeBoxData", true);

        // Fix Storage System to display abilities properly

        // This is the function that creates the struct used for a Pokémon's preview in the PC.
        // We are essentially swapping the markings (which is given 2 bytes despite only using 1) and ability.
        List<String> makeBox2MainLines = readLines("storagesystem/make_box2_main.s");
        storageSystemOvl.writeCodeForceInline(makeBox2MainLines, "MakeBox2Main", false);

        // This function calls PreviewCore as well as gets the Box2Main's markings, which have been moved, so we adjust
        List<String> displayPreviewLines = readLines("storagesystem/display_preview.s");
        storageSystemOvl.writeCodeForceInline(displayPreviewLines, "DisplayPreview", false);

        // Update to use the correct ability string when filling out the display field for ability
        List<String> previewAbilityLines = readLines("storagesystem/preview_ability.s");
        storageSystemOvl.writeCodeForceInline(previewAbilityLines, "Preview_Ability", false);
    }
}
