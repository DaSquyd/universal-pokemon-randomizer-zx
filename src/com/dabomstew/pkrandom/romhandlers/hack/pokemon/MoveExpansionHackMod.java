package com.dabomstew.pkrandom.romhandlers.hack.pokemon;

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.newnds.NARCArchive;
import com.dabomstew.pkrandom.pokemon.*;
import com.dabomstew.pkrandom.romhandlers.OverlayId;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteOverlay;
import com.dabomstew.pkrandom.romhandlers.hack.HackMod;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class MoveExpansionHackMod extends HackMod {
    private final String iniFileName;
    private final String narcFileName;
    private final String textsFileName;

    public MoveExpansionHackMod(String narcFileName, String textsFileName) {
        this.iniFileName = null;
        this.narcFileName = narcFileName;
        this.textsFileName = textsFileName;
    }

    public MoveExpansionHackMod(String iniFileName, String narcFileName, String textsFileName) {
        this.iniFileName = iniFileName;
        this.narcFileName = narcFileName;
        this.textsFileName = textsFileName;
    }

    public MoveExpansionHackMod() {
        this.iniFileName = null;
        this.narcFileName = null;
        this.textsFileName = null;
    }

    @Override
    public Set<Class<? extends HackMod>> getDependencies() {
        return Set.of();
    }

    @Override
    public void registerGlobalValues(Context context) {
    }

    @Override
    public void apply(Context context) {
        ParagonLiteOverlay battleOvl = context.overlays.get(OverlayId.BATTLE);
        ParagonLiteOverlay battleLevelOvl = context.overlays.get(OverlayId.BATTLE_LEVEL);

        // HACK: We have to allocate to Battle overlay and jump to that because we can't allocate new space in BattleLevel
        List<String> btlvEffVMLoadScriptLines = readLines("battlelevel/btlveffvm_load_script.s");
        battleOvl.writeCode(btlvEffVMLoadScriptLines, "BtlvEffVM_LoadScript", true);

        List<String> btlvEffVMLoadScriptJumpLines = readLines("battlelevel/btlveffvm_load_script_jump.s");
        battleLevelOvl.writeCodeForceInline(btlvEffVMLoadScriptJumpLines, "BtlvEffVM_LoadScript", true);

        List<String> playMoveAnimationLines = readLines("battlelevel/play_move_animation.s");
        battleOvl.writeCode(playMoveAnimationLines, "PlayMoveAnimation", true);

        List<String> playMoveAnimationJumpLines = readLines("battlelevel/play_move_animation_jump.s");
        battleLevelOvl.writeCodeForceInline(playMoveAnimationJumpLines, "PlayMoveAnimation", true);
        
        if (iniFileName == null) {
            loadFromNarc(context);
            loadMoveTexts(context);
        }
        else if (narcFileName != null) {
            loadOnlyNewMoves(context);
            loadMoveTexts(context);
            loadMovesFromIni(context);
        }
        
        int maxMoveId = 0;
        for (Move mv : context.moves) {
            if (mv.number > maxMoveId)
                maxMoveId = mv.number;
        }

        int moveDataSize = 36;
        context.arm9.replaceData(new byte[moveDataSize * (maxMoveId + 1)], "Data_MoveCache");
    }

    private void loadFromNarc(Context context) {
        try {
            byte[] bytes = readBytes(narcFileName);
            NARCArchive narc = new NARCArchive(bytes);
            context.romHandler.loadMoves(narc, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadMoveTexts(Context context) {
        Scanner sc;
        try {
            sc = new Scanner(FileFunctions.openConfig("paragonlite/" + textsFileName), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<String> names = context.romHandler.getStrings(false, context.romHandler.getRomInt("MoveNamesTextOffset"));
        List<String> descriptions = context.romHandler.getStrings(false, context.romHandler.getRomInt("MoveDescriptionsTextOffset"));

        for (int moveNumber = 0; sc.hasNextLine(); ++moveNumber) {
            String[] line = sc.nextLine().split("\t");
            if (line.length == 0)
                continue;

            String name = line[0];
            String description = line.length >= 2 ? line[1] : "";

            if (moveNumber < names.size())
                names.set(moveNumber, name);
            if (moveNumber < descriptions.size())
                descriptions.set(moveNumber, description);

            if (moveNumber < context.moves.size()) {
                Move move = context.moves.get(moveNumber);
                move.name = name;
                move.description = description;
            }
        }

        context.romHandler.setStrings(false, context.romHandler.getRomInt("MoveNamesTextOffset"), names);
    }
    
    private void loadOnlyNewMoves(Context context) {
        try {
            byte[] bytes = readBytes(narcFileName);
            NARCArchive reduxMovesNarc = new NARCArchive(bytes);
            context.romHandler.loadMoves(reduxMovesNarc, Moves.fusionBolt + 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void loadMovesFromIni(Context context) {
        Scanner sc;
        try {
            sc = new Scanner(FileFunctions.openConfig("paragonlite/moves.ini"), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Move move = null;
        while (sc.hasNextLine()) {
            String q = sc.nextLine().trim();
            if (q.contains("#")) {
                q = q.substring(0, q.indexOf("#")).trim();
            }

            if (q.isEmpty()) continue;

            if (q.startsWith("[") && q.endsWith("]")) {
                // New move
                String numStr = q.substring(1, 4);
                int num = Integer.parseInt(numStr);
                move = context.moves.get(num);

                String nameStr = q.substring(5, q.length() - 1).trim();

                // validate move
                String regex = "[- ]";
                String moveNameCheck = move.name.replaceAll(regex, "");
                String nameStrCheck = nameStr.replaceAll(regex, "");
//                if (!moveNameCheck.equalsIgnoreCase(nameStrCheck) && num <= Moves.fusionBolt) {
//                    throw new RuntimeException(String.format("Move names didn't match: %s and %s", move.name, nameStr));
//                }

                continue;
            }

            if (move == null) {
                System.err.println("no move at " + q);
                continue;
            }

            String[] r = q.split("=", 2);
            if (r.length == 1) {
                System.err.println("invalid entry " + q);
                continue;
            }

            if (r[1].endsWith("\r\n")) {
                r[1] = r[1].substring(0, r[1].length() - 2);
            }

            String key = r[0].trim();
            String value = r[1].trim();

            switch (key) {
                case "Name": {
                    if (!value.startsWith("\"") || !value.endsWith("\""))
                        throw new RuntimeException(String.format("Value must be in quotations for %s: \"%s\"", key, value));
                    move.name = value.substring(1, value.length() - 1);
                    break;
                }
                case "Description": {
                    if (!value.startsWith("\"") || !value.endsWith("\""))
                        throw new RuntimeException(String.format("Value must be in quotations for %s: \"%s\"", key, value));
                    move.description = value.substring(1, value.length() - 1);
                    break;
                }
                case "Type": {
                    move.type = Type.valueOf(value);
                    break;
                }
                case "Qualities": {
                    move.qualities = MoveQualities.valueOf(value);
                    break;
                }
                case "Category": {
                    move.category = MoveCategory.valueOf(value);
                    if (move.hasPerfectAccuracy()) move.accuracy = Move.getPerfectAccuracy();

                    break;
                }
                case "Power": {
                    move.power = Integer.parseInt(value);
                    break;
                }
                case "Accuracy": {
                    if (value.equals("--")) {
                        move.accuracy = Move.getPerfectAccuracy();
                        break;
                    }

                    move.accuracy = Integer.parseInt(value);
                    break;
                }
                case "PP": {
                    move.pp = Integer.parseInt(value);
                    break;
                }
                case "Priority": {
                    move.priority = Integer.parseInt(value);
                    break;
                }
                case "Hits": {
                    if (value.equals("[]")) {
                        move.minHits = 0;
                        move.maxHits = 0;
                        break;
                    }

                    if (value.startsWith("[") && value.endsWith("]")) {
                        String[] minMaxValue = value.substring(1, value.length() - 1).split(",", 2);
                        move.minHits = Integer.parseInt(minMaxValue[0].trim());
                        move.maxHits = Integer.parseInt(minMaxValue[1].trim());
                        break;
                    }

                    int hits = Integer.parseInt(value);
                    move.minHits = hits;
                    move.maxHits = hits;
                    break;
                }
                case "StatusType": {
                    move.statusType = MoveStatusType.valueOf(value);
                    if (move.statusType == MoveStatusType.NONE) move.statusPercentChance = 0;

                    break;
                }
                case "StatusChance": {
                    move.statusPercentChance = Integer.parseInt(value);
                    break;
                }
                case "StatusTurns": {
                    if (value.startsWith("[") && value.endsWith("]")) {
                        String[] minMaxValue = value.substring(1, value.length() - 1).split(",", 2);
                        move.statusMinTurns = Integer.parseInt(minMaxValue[0].trim());
                        move.statusMaxTurns = Integer.parseInt(minMaxValue[1].trim());
                        break;
                    }

                    int turns = Integer.parseInt(value);
                    move.statusMinTurns = turns;
                    move.statusMaxTurns = turns;
                    break;
                }
                case "CriticalChance": {
                    move.criticalChance = CriticalChance.valueOf(value);
                    break;
                }
                case "FlinchChance": {
                    move.flinchPercentChance = Integer.parseInt(value);
                    break;
                }
                case "Effect": {
                    move.effect = MoveEffect.valueOf(value);
                    break;
                }
                case "Recoil": {
                    move.recoil = Integer.parseInt(value);
                    break;
                }
                case "Heal": {
                    move.heal = Integer.parseInt(value);
                    break;
                }
                case "Target": {
                    move.target = MoveTarget.valueOf(value);
                    break;
                }
                case "StatChanges": {
                    if (!value.startsWith("[") || !value.endsWith("]"))
                        throw new RuntimeException();

                    String[] values = value.substring(1, value.length() - 1).split(",");

                    for (int i = 0; i < 3; ++i) {
                        if (i >= values.length) {
                            move.statChanges[i] = new Move.StatChange();
                            continue;
                        }

                        int defaultChance = move.category == MoveCategory.STATUS ? 0 : 100;
                        move.statChanges[i] = new Move.StatChange(values[i].trim(), defaultChance);
                    }

                    break;
                }
                case "IsStolenBySnatch": {
                    switch (value) {
                        case "TRUE":
                            move.isStolenBySnatch = true;
                            break;
                        case "FALSE":
                            move.isStolenBySnatch = false;
                            break;
                        default:
                            throw new RuntimeException(String.format("Unknown flag value for %s: \"%s\"", key, value));
                    }

                    break;
                }
                case "IsCopiedByMirrorMove": {
                    switch (value) {
                        case "TRUE":
                            move.isCopiedByMirrorMove = true;
                            break;
                        case "FALSE":
                            move.isCopiedByMirrorMove = false;
                            break;
                        default:
                            throw new RuntimeException(String.format("Unknown flag value for %s: \"%s\"", key, value));
                    }

                    break;
                }
                case "IsPunch": {
                    switch (value) {
                        case "TRUE":
                            move.isPunchMove = true;
                            break;
                        case "FALSE":
                            move.isPunchMove = false;
                            break;
                        default:
                            throw new RuntimeException(String.format("Unknown flag value for %s: \"%s\"", key, value));
                    }

                    break;
                }
                case "BypassesSubstitute": {
                    switch (value) {
                        case "TRUE":
                            move.bypassesSubstitute = true;
                            break;
                        case "FALSE":
                            move.bypassesSubstitute = false;
                            break;
                        default:
                            throw new RuntimeException(String.format("Unknown flag value for %s: \"%s\"", key, value));
                    }

                    break;
                }
                default:
                    throw new RuntimeException(String.format("Unknown parameter \"%s\"", key));
            }
        }
    }

    @Override
    public void Merge(HackMod other) {
    }
}
