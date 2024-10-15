package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  AbstractRomHandler.java - a base class for all rom handlers which     --*/
/*--                            implements the majority of the actual       --*/
/*--                            randomizer logic by building on the base    --*/
/*--                            getters & setters provided by each concrete --*/
/*--                            handler.                                    --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
/*--  Originally part of "Universal Pokemon Randomizer" by Dabomstew        --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2020.                                 --*/
/*--                                                                        --*/
/*--  The custom code written here is licensed under the terms of the GPL:  --*/
/*--                                                                        --*/
/*--  This program is free software: you can redistribute it and/or modify  --*/
/*--  it under the terms of the GNU General Public License as published by  --*/
/*--  the Free Software Foundation, either version 3 of the License, or     --*/
/*--  (at your option) any later version.                                   --*/
/*--                                                                        --*/
/*--  This program is distributed in the hope that it will be useful,       --*/
/*--  but WITHOUT ANY WARRANTY; without even the implied warranty of        --*/
/*--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          --*/
/*--  GNU General Public License for more details.                          --*/
/*--                                                                        --*/
/*--  You should have received a copy of the GNU General Public License     --*/
/*--  along with this program. If not, see <http://www.gnu.org/licenses/>.  --*/
/*----------------------------------------------------------------------------*/

import com.dabomstew.pkrandom.*;
import com.dabomstew.pkrandom.constants.*;
import com.dabomstew.pkrandom.exceptions.RandomizationException;
import com.dabomstew.pkrandom.pokemon.*;

import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractRomHandler implements RomHandler {

    private boolean restrictionsSet;
    protected List<Pokemon> mainPokemonList;
    protected List<Pokemon> mainPokemonListInclFormes;
    private List<Pokemon> altFormesList;
    private List<MegaEvolution> megaEvolutionsList;
    private List<Pokemon> noLegendaryList, onlyLegendaryList, ultraBeastList;
    private List<Pokemon> noLegendaryListInclFormes, onlyLegendaryListInclFormes;
    private List<Pokemon> noLegendaryAltsList, onlyLegendaryAltsList;
    private List<Pokemon> pickedStarters;
    protected final Random random;
    private final Random cosmeticRandom;
    protected PrintStream logStream;
    private List<Pokemon> alreadyPicked = new ArrayList<>();
    private Map<Pokemon, Integer> placementHistory = new HashMap<>();
    private Map<Integer, Integer> itemPlacementHistory = new HashMap<>();
    private int fullyEvolvedRandomSeed;
    boolean isORAS = false;
    boolean isSM = false;
    int firstShop = 0;

    /* Constructor */

    public AbstractRomHandler(Random random, PrintStream logStream) {
        this.random = random;
        this.cosmeticRandom = RandomSource.cosmeticInstance();
        this.fullyEvolvedRandomSeed = -1;
        this.logStream = logStream;
    }

    /*
     * Public Methods, implemented here for all gens. Unlikely to be overridden.
     */

    public void setLog(PrintStream logStream) {
        this.logStream = logStream;
    }

    public void setPokemonPool(Settings settings) {
        GenRestrictions restrictions = null;
        if (settings != null) {
            restrictions = settings.getCurrentRestrictions();

            // restrictions should already be null if "Limit Pokemon" is disabled, but this is a safeguard
            if (!settings.isLimitPokemon()) {
                restrictions = null;
            }
        }

        restrictionsSet = true;
        mainPokemonList = this.allPokemonWithoutNull();
        mainPokemonListInclFormes = this.allPokemonInclFormesWithoutNull();
        altFormesList = this.getAltFormes();
        megaEvolutionsList = this.getMegaEvolutions();
        if (restrictions != null) {
            mainPokemonList = new ArrayList<>();
            mainPokemonListInclFormes = new ArrayList<>();
            megaEvolutionsList = new ArrayList<>();
            List<Pokemon> allPokemon = this.getPokemon();

            if (restrictions.allow_gen1) {
                addPokesFromRange(mainPokemonList, allPokemon, Species.bulbasaur, Species.mew);
            }

            if (restrictions.allow_gen2 && allPokemon.size() > Gen2Constants.pokemonCount) {
                addPokesFromRange(mainPokemonList, allPokemon, Species.chikorita, Species.celebi);
            }

            if (restrictions.allow_gen3 && allPokemon.size() > Gen3Constants.pokemonCount) {
                addPokesFromRange(mainPokemonList, allPokemon, Species.treecko, Species.deoxys);
            }

            if (restrictions.allow_gen4 && allPokemon.size() > Gen4Constants.pokemonCount) {
                addPokesFromRange(mainPokemonList, allPokemon, Species.turtwig, Species.arceus);
            }

            if (restrictions.allow_gen5 && allPokemon.size() > Gen5Constants.pokemonCount) {
                addPokesFromRange(mainPokemonList, allPokemon, Species.victini, Species.genesect);
            }

            if (restrictions.allow_gen6 && allPokemon.size() > Gen6Constants.pokemonCount) {
                addPokesFromRange(mainPokemonList, allPokemon, Species.chespin, Species.volcanion);
            }

            int maxGen7SpeciesID = isSM ? Species.marshadow : Species.zeraora;
            if (restrictions.allow_gen7 && allPokemon.size() > maxGen7SpeciesID) {
                addPokesFromRange(mainPokemonList, allPokemon, Species.rowlet, maxGen7SpeciesID);
            }

            // If the user specified it, add all the evolutionary relatives for everything in the mainPokemonList
            if (restrictions.allow_evolutionary_relatives) {
                addEvolutionaryRelatives(mainPokemonList);
            }

            // Now that mainPokemonList has all the selected Pokemon, update mainPokemonListInclFormes too
            addAllPokesInclFormes(mainPokemonList, mainPokemonListInclFormes);

            // Populate megaEvolutionsList with all of the mega evolutions that exist in the pool
            List<MegaEvolution> allMegaEvolutions = this.getMegaEvolutions();
            for (MegaEvolution megaEvo : allMegaEvolutions) {
                if (mainPokemonListInclFormes.contains(megaEvo.to)) {
                    megaEvolutionsList.add(megaEvo);
                }
            }
        }

        noLegendaryList = new ArrayList<>();
        noLegendaryListInclFormes = new ArrayList<>();
        onlyLegendaryList = new ArrayList<>();
        onlyLegendaryListInclFormes = new ArrayList<>();
        noLegendaryAltsList = new ArrayList<>();
        onlyLegendaryAltsList = new ArrayList<>();
        ultraBeastList = new ArrayList<>();

        for (Pokemon p : mainPokemonList) {
            if (p.isLegendary()) {
                onlyLegendaryList.add(p);
            } else if (p.isUltraBeast()) {
                ultraBeastList.add(p);
            } else {
                noLegendaryList.add(p);
            }
        }
        for (Pokemon p : mainPokemonListInclFormes) {
            if (p.isLegendary()) {
                onlyLegendaryListInclFormes.add(p);
            } else if (!ultraBeastList.contains(p)) {
                noLegendaryListInclFormes.add(p);
            }
        }
        for (Pokemon f : altFormesList) {
            if (f.isLegendary()) {
                onlyLegendaryAltsList.add(f);
            } else {
                noLegendaryAltsList.add(f);
            }
        }
    }

    private void addPokesFromRange(List<Pokemon> pokemonPool, List<Pokemon> allPokemon, int range_min, int range_max) {
        for (int i = range_min; i <= range_max; i++) {
            if (!pokemonPool.contains(allPokemon.get(i))) {
                pokemonPool.add(allPokemon.get(i));
            }
        }
    }

    private void addEvolutionaryRelatives(List<Pokemon> pokemonPool) {
        Set<Pokemon> newPokemon = new TreeSet<>();
        for (Pokemon pk : pokemonPool) {
            List<Pokemon> evolutionaryRelatives = getEvolutionaryRelatives(pk);
            for (Pokemon relative : evolutionaryRelatives) {
                if (!pokemonPool.contains(relative) && !newPokemon.contains(relative)) {
                    newPokemon.add(relative);
                }
            }
        }

        pokemonPool.addAll(newPokemon);
    }

    private void addAllPokesInclFormes(List<Pokemon> pokemonPool, List<Pokemon> pokemonPoolInclFormes) {
        List<Pokemon> altFormes = this.getAltFormes();
        for (int i = 0; i < pokemonPool.size(); i++) {
            Pokemon currentPokemon = pokemonPool.get(i);
            if (!pokemonPoolInclFormes.contains(currentPokemon)) {
                pokemonPoolInclFormes.add(currentPokemon);
            }
            for (int j = 0; j < altFormes.size(); j++) {
                Pokemon potentialAltForme = altFormes.get(j);
                if (potentialAltForme.baseForme != null && potentialAltForme.baseForme.number == currentPokemon.number) {
                    pokemonPoolInclFormes.add(potentialAltForme);
                }
            }
        }
    }

    @Override
    public void shufflePokemonStats(Settings settings) {
        boolean evolutionSanity = settings.isBaseStatsFollowEvolutions();
        boolean megaEvolutionSanity = settings.isBaseStatsFollowMegaEvolutions();

        if (evolutionSanity) {
            copyUpEvolutionsHelper(pk -> pk.shuffleStats(AbstractRomHandler.this.random),
                    (evFrom, evTo, toMonIsFinalEvo) -> evTo.copyShuffledStatsUpEvolution(evFrom)
            );
        } else {
            List<Pokemon> allPokes = this.getPokemonInclFormes();
            for (Pokemon pk : allPokes) {
                if (pk != null) {
                    pk.shuffleStats(this.random);
                }
            }
        }

        List<Pokemon> allPokes = this.getPokemonInclFormes();
        for (Pokemon pk : allPokes) {
            if (pk != null && pk.actuallyCosmetic) {
                pk.copyBaseFormeBaseStats(pk.baseForme);
            }
        }

        if (megaEvolutionSanity) {
            List<MegaEvolution> allMegaEvos = getMegaEvolutions();
            for (MegaEvolution megaEvo : allMegaEvos) {
                if (megaEvo.from.megaEvolutionsFrom.size() > 1) continue;
                megaEvo.to.copyShuffledStatsUpEvolution(megaEvo.from);
            }
        }
    }

    @Override
    public void randomizePokemonStats(Settings settings) {
        boolean evolutionSanity = settings.isBaseStatsFollowEvolutions();
        boolean megaEvolutionSanity = settings.isBaseStatsFollowMegaEvolutions();
        boolean assignEvoStatsRandomly = settings.isAssignEvoStatsRandomly();

        if (evolutionSanity) {
            if (assignEvoStatsRandomly) {
                copyUpEvolutionsHelper(pk -> pk.randomizeStatsWithinBST(AbstractRomHandler.this.random),
                        (evFrom, evTo, toMonIsFinalEvo) -> evTo.assignNewStatsForEvolution(evFrom, this.random),
                        (evFrom, evTo, toMonIsFinalEvo) -> evTo.assignNewStatsForEvolution(evFrom, this.random),
                        true
                );
            } else {
                copyUpEvolutionsHelper(pk -> pk.randomizeStatsWithinBST(AbstractRomHandler.this.random),
                        (evFrom, evTo, toMonIsFinalEvo) -> evTo.copyRandomizedStatsUpEvolution(evFrom),
                        (evFrom, evTo, toMonIsFinalEvo) -> evTo.assignNewStatsForEvolution(evFrom, this.random),
                        true
                );
            }
        } else {
            List<Pokemon> allPokes = this.getPokemonInclFormes();
            for (Pokemon pk : allPokes) {
                if (pk != null) {
                    pk.randomizeStatsWithinBST(this.random);
                }
            }
        }

        List<Pokemon> allPokes = this.getPokemonInclFormes();
        for (Pokemon pk : allPokes) {
            if (pk != null && pk.actuallyCosmetic) {
                pk.copyBaseFormeBaseStats(pk.baseForme);
            }
        }

        if (megaEvolutionSanity) {
            List<MegaEvolution> allMegaEvos = getMegaEvolutions();
            for (MegaEvolution megaEvo : allMegaEvos) {
                if (megaEvo.from.megaEvolutionsFrom.size() > 1 || assignEvoStatsRandomly) {
                    megaEvo.to.assignNewStatsForEvolution(megaEvo.from, this.random);
                } else {
                    megaEvo.to.copyRandomizedStatsUpEvolution(megaEvo.from);
                }
            }
        }
    }

    @Override
    public void updatePokemonStats(Settings settings) {
        int generation = settings.getUpdateBaseStatsToGeneration();

        List<Pokemon> pokes = getPokemonInclFormes();

        for (int gen = 6; gen <= generation; gen++) {
            Map<Integer, StatChange> statChanges = getUpdatedPokemonStats(gen);

            for (int i = 1; i < pokes.size(); i++) {
                StatChange changedStats = statChanges.get(i);
                if (changedStats != null) {
                    int statNum = 0;
                    if ((changedStats.stat & Stat.HP.val) != 0) {
                        pokes.get(i).hp = changedStats.values[statNum];
                        statNum++;
                    }
                    if ((changedStats.stat & Stat.ATK.val) != 0) {
                        pokes.get(i).attack = changedStats.values[statNum];
                        statNum++;
                    }
                    if ((changedStats.stat & Stat.DEF.val) != 0) {
                        pokes.get(i).defense = changedStats.values[statNum];
                        statNum++;
                    }
                    if ((changedStats.stat & Stat.SPATK.val) != 0) {
                        if (generationOfPokemon() != 1) {
                            pokes.get(i).spatk = changedStats.values[statNum];
                        }
                        statNum++;
                    }
                    if ((changedStats.stat & Stat.SPDEF.val) != 0) {
                        if (generationOfPokemon() != 1) {
                            pokes.get(i).spdef = changedStats.values[statNum];
                        }
                        statNum++;
                    }
                    if ((changedStats.stat & Stat.SPEED.val) != 0) {
                        pokes.get(i).speed = changedStats.values[statNum];
                        statNum++;
                    }
                    if ((changedStats.stat & Stat.SPECIAL.val) != 0) {
                        pokes.get(i).special = changedStats.values[statNum];
                    }
                }
            }
        }
    }

    public enum CustomStatMode {
        additive,
        multiplicative
    }

    protected void customPokemonStats() {
        // TODO: make this configurable later
        CustomStatMode mode = CustomStatMode.additive;

        List<Pokemon> pokes = getPokemonInclFormes();

        Map<Pokemon, int[]> formeStatChanges = new HashMap<>();
        for (int i = 1; i < pokes.size(); i++) {
            Pokemon pk = pokes.get(i);
            if (pk.baseForme == null)
                continue;

            int hpDiff = pk.hp - pk.baseForme.hp;
            int attackDiff = pk.attack - pk.baseForme.attack;
            int defenseDiff = pk.defense - pk.baseForme.defense;
            int spatkDiff = pk.spatk - pk.baseForme.spatk;
            int spdefDiff = pk.spdef - pk.baseForme.spdef;
            int speedDiff = pk.speed - pk.baseForme.speed;

            formeStatChanges.put(pk, new int[]{hpDiff, attackDiff, defenseDiff, spatkDiff, spdefDiff, speedDiff});
        }

        for (int i = 1; i < pokes.size(); i++) {
            Pokemon pk = pokes.get(i);

            if (formeStatChanges.containsKey(pk)) {
                int[] statChanges = formeStatChanges.get(pk);

                int baseBst = pk.baseForme.bst();
                int sum = Arrays.stream(statChanges).sum();
                int newBst = baseBst + sum;

                boolean mega = pk.formeSuffix.startsWith("-Mega");
                boolean primal = pk.formeSuffix.equals("-P");

                if (mega || primal) {
                    for (int j = 1; j < 6; j++) {
                        statChanges[j] -= 10; // We only want an increase of 50 instead of 100
                    }
                } else if (newBst > 600) {
                    int offset = (int) Math.round((600.0 - newBst) / 10.0);

                    for (int j = 1; j < 6; j++) {
                        statChanges[j] += offset;
                    }
                }

                Pokemon baseForme = pk.baseForme;

                pk.hp = baseForme.hp + statChanges[0];
                pk.attack = baseForme.attack + statChanges[1];
                pk.defense = baseForme.defense + statChanges[2];
                pk.spatk = baseForme.spatk + statChanges[3];
                pk.spdef = baseForme.spdef + statChanges[4];
                pk.speed = baseForme.speed + statChanges[5];

                continue;
            }

            boolean isFullyEvolved = (pk.baseForme == null && pk.evolutionsFrom.isEmpty()) || (pk.baseForme != null && pk.baseForme.evolutionsFrom.isEmpty());

            double targetBst = (double) pk.bst() / 2.0 + 300.0;
            if (isFullyEvolved) {
                targetBst = Math.max(500, targetBst);
            }

            int maxStatValue = 130;
            AdjustCustomStat(mode, pk, isFullyEvolved, targetBst, maxStatValue, 0);
        }
    }

    private void AdjustCustomStat(CustomStatMode mode, Pokemon pk, boolean isFullyEvolved, double targetBst, int maxStatValue, int depth) {
        targetBst = Math.min(targetBst, 600);

        int maxHp = Math.max(pk.hp, maxStatValue);
        int maxAtk = Math.max(pk.attack, maxStatValue);
        int maxDef = Math.max(pk.defense, maxStatValue);
        int maxSpA = Math.max(pk.spatk, maxStatValue);
        int maxSpD = Math.max(pk.spdef, maxStatValue);
        int maxSpe = Math.max(pk.speed, maxStatValue);

        if (mode == CustomStatMode.additive || pk.bst() > 600) {
            // Additive; always used to lower BST down to 600

            int statDiff = targetBst > pk.bst() ? 1 : -1;
            int previousBst = 0;
            while (pk.bst() != previousBst && (targetBst - pk.bst()) * statDiff > 0) {
                previousBst = pk.bst();

                pk.hp = pk.hp == 1 ? pk.hp : Math.min(pk.hp + statDiff, maxHp);
                pk.attack = Math.min(pk.attack + statDiff, maxAtk);
                pk.defense = pk.hp == 1 ? pk.defense : Math.min(pk.defense + statDiff, maxDef);
                pk.spatk = Math.min(pk.spatk + statDiff, maxSpA);
                pk.spdef = pk.hp == 1 ? pk.spdef : Math.min(pk.spdef + statDiff, maxSpD);
                pk.speed = Math.min(pk.speed + statDiff, maxSpe);
            }
        } else {
            // Multiplicative
            double[] stats = new double[]{pk.hp, pk.attack, pk.defense, pk.spatk, pk.spdef, pk.speed};
            boolean[] maxedStats = new boolean[]{pk.hp == 255, pk.attack == 255, pk.defense == 255, pk.spatk == 255, pk.spdef == 255, pk.speed == 255};
            int totalMaxedStats = 0;
            for (int stat = 0; stat < 6; stat++) {
                if (maxedStats[stat])
                    totalMaxedStats++;
            }
            double bst = pk.bst();
            while (Math.abs(bst - targetBst) > 1.0) {
                double maxedStatReduction = totalMaxedStats * 255.0;
                double bstRatio = (targetBst - maxedStatReduction) / (bst - maxedStatReduction);
                for (int stat = 0; stat < 6; stat++) {
                    double statValue = stats[stat];
                    bst -= statValue;
                    stats[stat] = (stat == 0 && stats[0] == 1) ? 1 : Math.min(statValue * bstRatio, 255.0);
                    bst += stats[stat];

                    if ((int) Math.round(stats[stat]) == 255 && !maxedStats[stat]) {
                        maxedStats[stat] = true;
                        totalMaxedStats++;
                    }
                }
            }
            pk.hp = (int) Math.round(stats[0]);
            pk.attack = (int) Math.round(stats[1]);
            pk.defense = (int) Math.round(stats[2]);
            pk.spatk = (int) Math.round(stats[3]);
            pk.spdef = (int) Math.round(stats[4]);
            pk.speed = (int) Math.round(stats[5]);
        }

        if (depth > 0)
            return;

        int minDefensiveBulk = 6000;
        int emergencyBreak = 0;
        boolean changed = false;
        while (emergencyBreak < 100 && (isFullyEvolved && pk.hp > 1)
                && (pk.hp * pk.defense < minDefensiveBulk || pk.hp * pk.spdef < minDefensiveBulk)) {
            changed = true;

            if (pk.hp < maxHp)
                ++pk.hp;

            if (pk.defense < maxDef)
                ++pk.defense;

            if (pk.spdef < maxSpD)
                ++pk.spdef;

            ++emergencyBreak;
        }

        // Recurse
        if (changed)
            AdjustCustomStat(mode, pk, true, targetBst, maxStatValue, depth + 1);
    }

    protected void customPokemonTypes() {
        List<Pokemon> pokes = getPokemonInclFormes();

        pokes.get(Species.charmander).secondaryType = Type.DRAGON;
        pokes.get(Species.charmeleon).secondaryType = Type.DRAGON;
        pokes.get(Species.charizard).secondaryType = Type.DRAGON;
        pokes.get(Species.ninetales).secondaryType = Type.PSYCHIC;
        pokes.get(Species.psyduck).secondaryType = Type.PSYCHIC;
        pokes.get(Species.golduck).secondaryType = Type.PSYCHIC;
        pokes.get(Species.growlithe).primaryType = Type.NORMAL;
        pokes.get(Species.growlithe).secondaryType = Type.FIRE;
        pokes.get(Species.arcanine).primaryType = Type.NORMAL;
        pokes.get(Species.arcanine).secondaryType = Type.FIRE;
        pokes.get(Species.farfetchd).primaryType = Type.FIGHTING;
        pokes.get(Species.doduo).primaryType = Type.FIGHTING;
        pokes.get(Species.doduo).secondaryType = null;
        pokes.get(Species.dodrio).primaryType = Type.FIGHTING;
        pokes.get(Species.dodrio).secondaryType = null;
        pokes.get(Species.marowak).secondaryType = Type.FIGHTING;
        pokes.get(Species.kangaskhan).secondaryType = Type.GROUND;
        pokes.get(Species.pinsir).secondaryType = Type.GROUND;
        pokes.get(Species.gyarados).secondaryType = Type.DRAGON;
        pokes.get(Species.aerodactyl).secondaryType = Type.DRAGON;
        if (typeInGame(Type.STEEL)) {
            pokes.get(Species.blastoise).secondaryType = Type.STEEL;
        }
        if (typeInGame(Type.DARK)) {
            pokes.get(Species.arbok).secondaryType = Type.DARK;
            pokes.get(Species.dodrio).secondaryType = Type.DARK;
        }
        if (typeInGame(Type.FAIRY)) {
            pokes.get(Species.diglett).secondaryType = Type.FAIRY;
            pokes.get(Species.dugtrio).secondaryType = Type.FAIRY;
            pokes.get(Species.rapidash).secondaryType = Type.FAIRY;
        }
        if (generationOfPokemon() <= 1)
            return;

        pokes.get(Species.croconaw).secondaryType = Type.DARK;
        pokes.get(Species.feraligatr).secondaryType = Type.DARK;
        pokes.get(Species.hoothoot).primaryType = Type.PSYCHIC;
        pokes.get(Species.noctowl).primaryType = Type.PSYCHIC;
        pokes.get(Species.ampharos).secondaryType = Type.DRAGON;
        pokes.get(Species.girafarig).primaryType = Type.PSYCHIC;
        pokes.get(Species.girafarig).secondaryType = Type.DARK;
        pokes.get(Species.dunsparce).secondaryType = Type.DRAGON;
        pokes.get(Species.snubbull).secondaryType = Type.FIGHTING;
        pokes.get(Species.granbull).secondaryType = Type.FIGHTING;
        pokes.get(Species.ursaring).secondaryType = Type.FIGHTING;
        pokes.get(Species.octillery).secondaryType = Type.FIRE;
        if (typeInGame(Type.FAIRY)) {
            pokes.get(Species.meganium).secondaryType = Type.FAIRY;
            pokes.get(Species.misdreavus).secondaryType = Type.FAIRY;
        }
        if (generationOfPokemon() <= 2)
            return;

        pokes.get(Species.treecko).secondaryType = Type.DRAGON;
        pokes.get(Species.grovyle).secondaryType = Type.DRAGON;
        pokes.get(Species.sceptile).secondaryType = Type.DRAGON;
        pokes.get(Species.masquerain).secondaryType = Type.WATER;
        pokes.get(Species.aron).secondaryType = null;
        pokes.get(Species.lairon).secondaryType = null;
        pokes.get(Species.aggron).secondaryType = null;
        pokes.get(Species.grumpig).secondaryType = Type.POISON;
        pokes.get(Species.trapinch).primaryType = Type.BUG;
        pokes.get(Species.trapinch).secondaryType = Type.GROUND;
        pokes.get(Species.vibrava).primaryType = Type.BUG;
        pokes.get(Species.flygon).primaryType = Type.BUG;
        pokes.get(Species.seviper).secondaryType = Type.STEEL;
        pokes.get(Species.kecleon).secondaryType = Type.GHOST;
        pokes.get(Species.banette).secondaryType = Type.STEEL;
        pokes.get(Species.chimecho).secondaryType = Type.GHOST;
        pokes.get(Species.glalie).secondaryType = Type.ROCK;
        if (typeInGame(Type.FAIRY)) {
            pokes.get(Species.volbeat).secondaryType = Type.FAIRY;
            pokes.get(Species.illumise).secondaryType = Type.DARK;
            pokes.get(Species.swablu).primaryType = Type.FAIRY;
            pokes.get(Species.altaria).primaryType = Type.FAIRY;
            pokes.get(Species.altaria).secondaryType = Type.DRAGON;
            pokes.get(Species.milotic).secondaryType = Type.FAIRY;
            pokes.get(Species.absol).secondaryType = Type.FAIRY;
            pokes.get(Species.luvdisc).secondaryType = Type.FAIRY;
        }
        if (generationOfPokemon() <= 3)
            return;

        pokes.get(Species.luxray).secondaryType = Type.DARK;
        pokes.get(Species.vespiquen).secondaryType = Type.PSYCHIC;
        pokes.get(Species.cherrim).secondaryType = Type.FIRE;
        pokes.get(Species.lopunny).secondaryType = Type.FIGHTING;
        pokes.get(Species.chingling).secondaryType = Type.GHOST;
        pokes.get(Species.drapion).secondaryType = Type.DRAGON;
        pokes.get(Species.electivire).secondaryType = Type.FIGHTING;
        pokes.get(Species.probopass).secondaryType = Type.ELECTRIC;
        pokes.get(Species.dusknoir).secondaryType = Type.FIGHTING;
        if (typeInGame(Type.FAIRY)) {
            pokes.get(Species.pachirisu).secondaryType = Type.FAIRY;
            pokes.get(Species.mismagius).secondaryType = Type.FAIRY;
        }
        if (generationOfPokemon() <= 4)
            return;

        pokes.get(Species.snivy).secondaryType = Type.ELECTRIC;
        pokes.get(Species.servine).secondaryType = Type.ELECTRIC;
        pokes.get(Species.serperior).secondaryType = Type.ELECTRIC;
        pokes.get(Species.pignite).secondaryType = Type.DARK;
        pokes.get(Species.emboar).secondaryType = Type.DARK;
        pokes.get(Species.dewott).secondaryType = Type.FIGHTING;
        pokes.get(Species.samurott).secondaryType = Type.FIGHTING;
        pokes.get(Species.blitzle).secondaryType = Type.NORMAL;
        pokes.get(Species.zebstrika).secondaryType = Type.NORMAL;
        pokes.get(Species.throh).secondaryType = Type.ROCK;
        pokes.get(Species.sawk).secondaryType = Type.ROCK;
        pokes.get(Species.maractus).secondaryType = Type.FIRE;
        pokes.get(Species.yamask).secondaryType = Type.ROCK;
        pokes.get(Species.cofagrigus).secondaryType = Type.ROCK;
        pokes.get(Species.solosis).secondaryType = Type.ELECTRIC;
        pokes.get(Species.duosion).secondaryType = Type.ELECTRIC;
        pokes.get(Species.reuniclus).secondaryType = Type.ELECTRIC;
        pokes.get(Species.karrablast).secondaryType = Type.DARK;
        pokes.get(Species.eelektrik).secondaryType = Type.POISON;
        pokes.get(Species.eelektross).secondaryType = Type.POISON;
        pokes.get(Species.haxorus).secondaryType = Type.STEEL;
        pokes.get(Species.beartic).secondaryType = Type.FIGHTING;
        pokes.get(Species.shelmet).secondaryType = Type.STEEL;
        pokes.get(Species.accelgor).secondaryType = Type.DARK;
        pokes.get(Species.druddigon).secondaryType = Type.FIGHTING;
        if (typeInGame(Type.FAIRY)) {
            pokes.get(Species.audino).secondaryType = Type.FAIRY;
            pokes.get(Species.vanillite).secondaryType = Type.FAIRY;
            pokes.get(Species.vanillish).secondaryType = Type.FAIRY;
            pokes.get(Species.vanilluxe).secondaryType = Type.FAIRY;
        }
        if (generationOfPokemon() <= 5)
            return;

        pokes.get(Species.flabébé).secondaryType = Type.GRASS;
        pokes.get(Species.floette).secondaryType = Type.GRASS;
        pokes.get(Species.florges).secondaryType = Type.GRASS;
        pokes.get(Species.goomy).secondaryType = Type.WATER;
        pokes.get(Species.sliggoo).secondaryType = Type.WATER;
        pokes.get(Species.goodra).secondaryType = Type.WATER;
        pokes.get(Species.Gen6Formes.kangaskhanMega).secondaryType = Type.GROUND;
        pokes.get(Species.Gen6Formes.gyaradosMega).secondaryType = Type.DRAGON;
        pokes.get(Species.Gen6Formes.aerodactylMega).secondaryType = Type.DRAGON;
        pokes.get(Species.Gen6Formes.banetteMega).secondaryType = Type.STEEL;
        pokes.get(Species.Gen6Formes.absolMega).secondaryType = Type.FAIRY;
        pokes.get(Species.Gen6Formes.altariaMega).primaryType = Type.FAIRY;
        pokes.get(Species.Gen6Formes.altariaMega).secondaryType = Type.DRAGON;
        pokes.get(Species.Gen6Formes.glalieMega).secondaryType = Type.ROCK;
    }

    public Pokemon randomPokemon() {
        checkPokemonRestrictions();
        return mainPokemonList.get(this.random.nextInt(mainPokemonList.size()));
    }

    @Override
    public Pokemon randomPokemonInclFormes() {
        checkPokemonRestrictions();
        return mainPokemonListInclFormes.get(this.random.nextInt(mainPokemonListInclFormes.size()));
    }

    @Override
    public Pokemon randomNonLegendaryPokemon() {
        checkPokemonRestrictions();
        return noLegendaryList.get(this.random.nextInt(noLegendaryList.size()));
    }

    private Pokemon randomNonLegendaryPokemonInclFormes() {
        checkPokemonRestrictions();
        return noLegendaryListInclFormes.get(this.random.nextInt(noLegendaryListInclFormes.size()));
    }

    @Override
    public Pokemon randomLegendaryPokemon() {
        checkPokemonRestrictions();
        return onlyLegendaryList.get(this.random.nextInt(onlyLegendaryList.size()));
    }

    private List<Pokemon> fullyEvolvedPokes;

    @Override
    public Pokemon random2EvosPokemon(boolean allowAltFormes) {
        if (fullyEvolvedPokes == null) {
            // Prepare the list
            fullyEvolvedPokes = new ArrayList<>();
            List<Pokemon> allPokes =
                    allowAltFormes ?
                            this.getPokemonInclFormes()
                                    .stream()
                                    .filter(pk -> pk == null || !pk.actuallyCosmetic)
                                    .collect(Collectors.toList()) :
                            this.getPokemon();
            for (Pokemon pk : allPokes) {
                if (pk != null && pk.evolutionsTo.size() == 0 && pk.evolutionsFrom.size() > 0) {
                    // Potential candidate
                    for (Evolution ev : pk.evolutionsFrom) {
                        // If any of the targets here evolve, the original
                        // Pokemon has 2+ stages.
                        if (ev.to.evolutionsFrom.size() > 0) {
                            fullyEvolvedPokes.add(pk);
                            break;
                        }
                    }
                }
            }
        }
        return fullyEvolvedPokes.get(this.random.nextInt(fullyEvolvedPokes.size()));
    }

    @Override
    public Pokemon randomFullyEvolvedPokemon(boolean allowAltFormes) {
        if (fullyEvolvedPokes == null) {
            // Prepare the list
            fullyEvolvedPokes = new ArrayList<>();
            List<Pokemon> allPokes =
                    allowAltFormes ?
                            this.getPokemonInclFormes()
                                    .stream()
                                    .filter(pk -> pk == null || !pk.actuallyCosmetic)
                                    .collect(Collectors.toList()) :
                            this.getPokemon();
            for (Pokemon pk : allPokes) {
                if (pk != null && pk.evolutionsFrom.isEmpty()) {
                    fullyEvolvedPokes.add(pk);
                }
            }
        }

        return fullyEvolvedPokes.get(this.random.nextInt(fullyEvolvedPokes.size()));
    }

    @Override
    public Type randomType() {
        Type t = Type.randomType(this.random);
        while (!typeInGame(t)) {
            t = Type.randomType(this.random);
        }
        return t;
    }

    @Override
    public void customTypes() {

    }

    @Override
    public void randomizePokemonTypes(Settings settings) {
        boolean evolutionSanity = settings.getTypesMod() == Settings.TypesMod.RANDOM_FOLLOW_EVOLUTIONS;
        boolean megaEvolutionSanity = settings.isTypesFollowMegaEvolutions();
        boolean dualTypeOnly = settings.isDualTypeOnly();

        List<Pokemon> allPokes = this.getPokemonInclFormes();
        if (evolutionSanity) {
            // Type randomization with evolution sanity
            copyUpEvolutionsHelper(pk -> {
                // Step 1: Basic or Excluded From Copying Pokemon
                // A Basic/EFC pokemon has a 35% chance of a second type if
                // it has an evolution that copies type/stats, a 50% chance
                // otherwise
                pk.primaryType = randomType();
                pk.secondaryType = null;
                if (pk.evolutionsFrom.size() == 1 && pk.evolutionsFrom.get(0).carryStats) {
                    if (AbstractRomHandler.this.random.nextDouble() < 0.35 || dualTypeOnly) {
                        pk.secondaryType = randomType();
                        while (pk.secondaryType == pk.primaryType) {
                            pk.secondaryType = randomType();
                        }
                    }
                } else {
                    if (AbstractRomHandler.this.random.nextDouble() < 0.5 || dualTypeOnly) {
                        pk.secondaryType = randomType();
                        while (pk.secondaryType == pk.primaryType) {
                            pk.secondaryType = randomType();
                        }
                    }
                }
            }, (evFrom, evTo, toMonIsFinalEvo) -> {
                evTo.primaryType = evFrom.primaryType;
                evTo.secondaryType = evFrom.secondaryType;

                if (evTo.secondaryType == null) {
                    double chance = toMonIsFinalEvo ? 0.25 : 0.15;
                    if (AbstractRomHandler.this.random.nextDouble() < chance || dualTypeOnly) {
                        evTo.secondaryType = randomType();
                        while (evTo.secondaryType == evTo.primaryType) {
                            evTo.secondaryType = randomType();
                        }
                    }
                }
            });
        } else {
            // Entirely random types
            for (Pokemon pkmn : allPokes) {
                if (pkmn != null) {
                    pkmn.primaryType = randomType();
                    pkmn.secondaryType = null;
                    if (this.random.nextDouble() < 0.5 || settings.isDualTypeOnly()) {
                        pkmn.secondaryType = randomType();
                        while (pkmn.secondaryType == pkmn.primaryType) {
                            pkmn.secondaryType = randomType();
                        }
                    }
                }
            }
        }

        for (Pokemon pk : allPokes) {
            if (pk != null && pk.actuallyCosmetic) {
                pk.primaryType = pk.baseForme.primaryType;
                pk.secondaryType = pk.baseForme.secondaryType;
            }
        }

        if (megaEvolutionSanity) {
            List<MegaEvolution> allMegaEvos = getMegaEvolutions();
            for (MegaEvolution megaEvo : allMegaEvos) {
                if (megaEvo.from.megaEvolutionsFrom.size() > 1) continue;
                megaEvo.to.primaryType = megaEvo.from.primaryType;
                megaEvo.to.secondaryType = megaEvo.from.secondaryType;

                if (megaEvo.to.secondaryType == null) {
                    if (this.random.nextDouble() < 0.25) {
                        megaEvo.to.secondaryType = randomType();
                        while (megaEvo.to.secondaryType == megaEvo.to.primaryType) {
                            megaEvo.to.secondaryType = randomType();
                        }
                    }
                }
            }
        }
    }

    @Override
    public Set<Integer> getAvailableAbilities(Settings settings) {
        int maxAbility = highestAbilityIndex(settings);
        Set<Integer> availableAbilities = new HashSet<>(maxAbility);
        for (int i = 1; i <= maxAbility; ++i) {
            availableAbilities.add(i);
        }

        return availableAbilities;
    }

    @Override
    public void randomizeAbilities(Settings settings) {
        boolean evolutionSanity = settings.isAbilitiesFollowEvolutions();
        boolean allowWonderGuard = settings.isAllowWonderGuard();
        boolean banTrappingAbilities = settings.isBanTrappingAbilities();
        boolean banNegativeAbilities = settings.isBanNegativeAbilities();
        boolean banBadAbilities = settings.isBanBadAbilities();
        boolean megaEvolutionSanity = settings.isAbilitiesFollowMegaEvolutions();
        boolean weighDuplicatesTogether = settings.isWeighDuplicateAbilitiesTogether();
        boolean ensureTwoAbilities = settings.isEnsureTwoAbilities();
        boolean doubleBattleMode = settings.isDoubleBattleMode();

        // Abilities don't exist in some games...
        if (this.abilitiesPerPokemon() == 0) {
            return;
        }

        final boolean hasDWAbilities = (this.abilitiesPerPokemon() == 3);

        final List<Integer> bannedAbilities = this.getUselessAbilities();

        if (!allowWonderGuard) {
            bannedAbilities.add(Abilities.wonderGuard);
        }

        if (banTrappingAbilities) {
            bannedAbilities.addAll(GlobalConstants.battleTrappingAbilities);
        }

        if (banNegativeAbilities) {
            bannedAbilities.addAll(GlobalConstants.negativeAbilities);
        }

        if (banBadAbilities) {
            bannedAbilities.addAll(GlobalConstants.badAbilities);
            if (!doubleBattleMode) {
                bannedAbilities.addAll(GlobalConstants.doubleBattleAbilities);
            }
        }

        if (weighDuplicatesTogether) {
            bannedAbilities.addAll(GlobalConstants.duplicateAbilities);
            if (generationOfPokemon() == 3) {
                bannedAbilities.add(Gen3Constants.airLockIndex); // Special case for Air Lock in Gen 3
            }
        }

        final Set<Integer> availableAbilities = this.getAvailableAbilities(settings);

        if (evolutionSanity) {
            // copy abilities straight up evolution lines
            // still keep WG as an exception, though

            copyUpEvolutionsHelper(pk -> {
                if (settings.isEnsureRelevantAbilities()
                        || (pk.ability1 != Abilities.wonderGuard
                        && pk.ability2 != Abilities.wonderGuard
                        && pk.ability3 != Abilities.wonderGuard)) {
                    // Pick first ability
                    pk.ability1 = pickRandomAbility(pk, settings, availableAbilities, bannedAbilities, weighDuplicatesTogether);

                    // Second ability?
                    if (ensureTwoAbilities || AbstractRomHandler.this.random.nextDouble() < 0.5) {
                        // Yes, second ability
                        pk.ability2 = pickRandomAbility(pk, settings, availableAbilities, bannedAbilities, weighDuplicatesTogether,
                                pk.ability1);
                    } else {
                        // Nope
                        pk.ability2 = 0;
                    }

                    // Third ability?
                    if (hasDWAbilities) {
                        pk.ability3 = pickRandomAbility(pk, settings, availableAbilities, bannedAbilities, weighDuplicatesTogether,
                                pk.ability1, pk.ability2);
                    }
                }
            }, (evFrom, evTo, toMonIsFinalEvo) -> {
                if (evTo.ability1 != Abilities.wonderGuard
                        && evTo.ability2 != Abilities.wonderGuard
                        && evTo.ability3 != Abilities.wonderGuard) {
                    evTo.ability1 = evFrom.ability1;
                    evTo.ability2 = evFrom.ability2;
                    evTo.ability3 = evFrom.ability3;
                }
            });
        } else {
            List<Pokemon> allPokes = this.getPokemonInclFormes();
            for (Pokemon pk : allPokes) {
                if (pk == null) {
                    continue;
                }

//                // Don't remove WG if already in place.
//                if (pk.ability1 == Abilities.wonderGuard
//                        || pk.ability2 == Abilities.wonderGuard
//                        || pk.ability3 == Abilities.wonderGuard)
//                    continue;

                // Pick first ability
                pk.ability1 = this.pickRandomAbility(pk, settings, availableAbilities, bannedAbilities, weighDuplicatesTogether);

                // Second ability?
                if (ensureTwoAbilities || this.random.nextDouble() < 0.5) {
                    // Yes, second ability
                    pk.ability2 = this.pickRandomAbility(pk, settings, availableAbilities, bannedAbilities,
                            weighDuplicatesTogether, pk.ability1);
                } else {
                    // Nope
                    pk.ability2 = 0;
                }

                // Third ability?
                if (hasDWAbilities) {
                    pk.ability3 = pickRandomAbility(pk, settings, availableAbilities, bannedAbilities,
                            weighDuplicatesTogether, pk.ability1, pk.ability2);
                }
            }
        }

        List<Pokemon> allPokes = this.getPokemonInclFormes();
        for (Pokemon pk : allPokes) {
            if (pk != null && pk.actuallyCosmetic) {
                pk.copyBaseFormeAbilities(pk.baseForme);
            }
        }

        if (megaEvolutionSanity) {
            List<MegaEvolution> allMegaEvos = this.getMegaEvolutions();
            for (MegaEvolution megaEvo : allMegaEvos) {
                if (megaEvo.from.megaEvolutionsFrom.size() > 1) continue;
                megaEvo.to.ability1 = megaEvo.from.ability1;
                megaEvo.to.ability2 = megaEvo.from.ability2;
                megaEvo.to.ability3 = megaEvo.from.ability3;
            }
        }
    }

    private int pickRandomAbilityVariation(Settings settings, int selectedAbility, int... alreadySetAbilities) {
        int newAbility = selectedAbility;

        while (true) {
            Map<Integer, List<Integer>> abilityVariations = getAbilityVariations(settings);
            for (int baseAbility : abilityVariations.keySet()) {
                if (selectedAbility == baseAbility) {
                    List<Integer> variationsForThisAbility = abilityVariations.get(selectedAbility);
                    newAbility = variationsForThisAbility.get(this.random.nextInt(variationsForThisAbility.size()));
                    break;
                }
            }

            boolean repeat = false;
            for (int alreadySetAbility : alreadySetAbilities) {
                if (alreadySetAbility == newAbility) {
                    repeat = true;
                    break;
                }
            }

            if (!repeat) {
                break;
            }


        }

        return newAbility;
    }

    private int pickRandomAbility(Pokemon pk, Settings settings, Set<Integer> availableAbilities, List<Integer> bannedAbilities, boolean useVariations,
                                  int... alreadySetAbilities) {
        int newAbility = -1;

        boolean isParagonLite = (settings.getCurrentMiscTweaks() & MiscTweak.PARAGON_LITE.getValue()) != 0L;

        boolean lowAtk = pk.attack <= 85;
        boolean highAtk = pk.attack > 110;

        boolean lowSpA = pk.spatk <= 85;
        boolean highSpA = pk.spatk > 110;

        boolean highSpeed = pk.speed > 110;
        boolean mediumSpeed = (pk.speed >= 85) && (pk.speed <= 110);
        boolean lowSpeed = pk.speed < 85;

        double avgHpBuffFromIvs = (60.0 + 75.0) / 2.0;
        double avgStatBuffFromIvs = (5.0 + 20.0) / 2.0;

        int physicalBulk = (int) Math.sqrt((pk.hp + avgHpBuffFromIvs) * (pk.defense + avgStatBuffFromIvs));
        boolean highPhysBulk = physicalBulk > 135;
        boolean lowPhysBulk = physicalBulk <= 120;

        int specialBulk = (int) Math.sqrt((pk.hp + avgHpBuffFromIvs) * (pk.spdef + avgStatBuffFromIvs));

        int bulk = Math.min(physicalBulk, specialBulk);
        boolean lowBulk = bulk <= 115;

        boolean higherOrEqualAttack = pk.attack >= pk.spatk; // minAttack >= maxSpAtk;
        boolean higherOrEqualSpAtk = pk.spatk >= pk.attack; // minSpAtk >= maxAttack;

        long miscTweaks = settings.getCurrentMiscTweaks();
        boolean isCustomTypeEffectiveness = (miscTweaks & MiscTweak.CUSTOM_TYPE_EFFECTIVENESS.getValue()) == MiscTweak.CUSTOM_TYPE_EFFECTIVENESS.getValue();

        Map<Type, Effectiveness> against = Effectiveness.against(pk.primaryType, pk.secondaryType, generationOfPokemon(), true, isCustomTypeEffectiveness, typeInGame(Type.FAIRY));

        boolean isNormal = pk.primaryType == Type.NORMAL || pk.secondaryType == Type.NORMAL;

        boolean isFighting = pk.primaryType == Type.FIGHTING || pk.secondaryType == Type.FIGHTING;

        boolean isFlying = pk.primaryType == Type.FLYING || pk.secondaryType == Type.FLYING;

        boolean isPoison = pk.primaryType == Type.POISON || pk.secondaryType == Type.POISON;
        boolean resistsPoison = against.get(Type.POISON) == Effectiveness.HALF || against.get(Type.POISON) == Effectiveness.QUARTER || against.get(Type.POISON) == Effectiveness.ZERO;

        boolean isGround = pk.primaryType == Type.GROUND || pk.secondaryType == Type.GROUND;
        boolean resistsGround = against.get(Type.GROUND) == Effectiveness.HALF || against.get(Type.GROUND) == Effectiveness.QUARTER || against.get(Type.GROUND) == Effectiveness.ZERO;

        boolean isRock = pk.primaryType == Type.ROCK || pk.secondaryType == Type.ROCK;

        boolean isBug = pk.primaryType == Type.BUG || pk.secondaryType == Type.BUG;
        boolean resistsBug = against.get(Type.BUG) == Effectiveness.HALF || against.get(Type.BUG) == Effectiveness.QUARTER || against.get(Type.BUG) == Effectiveness.ZERO;
        boolean weakToBug = against.get(Type.BUG) == Effectiveness.DOUBLE || against.get(Type.BUG) == Effectiveness.QUADRUPLE;

        boolean weakToGhost = against.get(Type.GHOST) == Effectiveness.DOUBLE || against.get(Type.GHOST) == Effectiveness.QUADRUPLE;

        boolean isSteel = pk.primaryType == Type.STEEL || pk.secondaryType == Type.STEEL;

        boolean isFire = pk.primaryType == Type.FIRE || pk.secondaryType == Type.FIRE;
        boolean resistsFire = against.get(Type.FIRE) == Effectiveness.HALF || against.get(Type.FIRE) == Effectiveness.QUARTER || against.get(Type.FIRE) == Effectiveness.ZERO;
        boolean weakToFire = against.get(Type.FIRE) == Effectiveness.DOUBLE || against.get(Type.FIRE) == Effectiveness.QUADRUPLE;

        boolean isWater = pk.primaryType == Type.WATER || pk.secondaryType == Type.WATER;
        boolean resistsWater = against.get(Type.WATER) == Effectiveness.HALF || against.get(Type.WATER) == Effectiveness.QUARTER || against.get(Type.WATER) == Effectiveness.ZERO;
        boolean weakToWater = against.get(Type.WATER) == Effectiveness.DOUBLE || against.get(Type.WATER) == Effectiveness.QUADRUPLE;

        boolean isGrass = pk.primaryType == Type.GRASS || pk.secondaryType == Type.GRASS;
        boolean resistsGrass = against.get(Type.GRASS) == Effectiveness.HALF || against.get(Type.GRASS) == Effectiveness.QUARTER || against.get(Type.GRASS) == Effectiveness.ZERO;

        boolean isElectric = pk.primaryType == Type.ELECTRIC || pk.secondaryType == Type.ELECTRIC;
        boolean resistsElectric = against.get(Type.ELECTRIC) == Effectiveness.HALF || against.get(Type.ELECTRIC) == Effectiveness.QUARTER || against.get(Type.ELECTRIC) == Effectiveness.ZERO;

        boolean resistsPsychic = against.get(Type.PSYCHIC) == Effectiveness.HALF || against.get(Type.PSYCHIC) == Effectiveness.QUARTER || against.get(Type.PSYCHIC) == Effectiveness.ZERO;

        boolean isIce = pk.primaryType == Type.ICE || pk.secondaryType == Type.ICE;
        boolean resistsIce = against.get(Type.ICE) == Effectiveness.HALF || against.get(Type.ICE) == Effectiveness.QUARTER || against.get(Type.ICE) == Effectiveness.ZERO;

        boolean isDark = pk.primaryType == Type.DARK || pk.secondaryType == Type.DARK;
        boolean weakToDark = against.get(Type.DARK) == Effectiveness.DOUBLE || against.get(Type.DARK) == Effectiveness.QUADRUPLE;
        boolean resistsDark = against.get(Type.DARK) == Effectiveness.HALF || against.get(Type.DARK) == Effectiveness.QUARTER || against.get(Type.ICE) == Effectiveness.ZERO;

        boolean isFairy = pk.primaryType == Type.FAIRY || pk.secondaryType == Type.FAIRY;

        int weaknesses = 0;
        for (Effectiveness e : against.values()) {
            weaknesses += (e == Effectiveness.DOUBLE || e == Effectiveness.QUADRUPLE) ? 1 : 0;
        }

        List<Move> moves = this.getMoves();

        List<MoveLearnt> moveset = this.getMovesLearnt().get(pk.number);
        int tutorMoveCount = getMoveTutorMainGameCount();
        List<Integer> tutorMoves = this.getMoveTutorMoves();
        boolean[] tutorMoveCompatibility = this.getMoveTutorCompatibility().get(pk);

        List<Integer> tmMoves = this.getTMMoves();
        boolean[] tmCompatibility = this.getTMHMCompatibility().get(pk);
        boolean[] tmsAvailable = this.getTMsAvailableInMainGame();

        Set<Integer> technicianMoves = new HashSet<>();
        int technicianMovesFromLevel = 0;
        Set<Integer> multiStrikeMoves = new HashSet<>();
        int multiStrikeMovesFromLevel = 0;
        Set<Integer> punchMoves = new HashSet<>();
        int punchMovesFromLevel = 0;
        Set<Integer> soundMoves = new HashSet<>();
        int soundMovesFromLevel = 0;
        Set<Integer> kickMoves = new HashSet<>();
        int kickMovesFromLevel = 0;
        Set<Integer> biteMoves = new HashSet<>();
        int biteMovesFromLevel = 0;
        Set<Integer> sliceMoves = new HashSet<>();
        int sliceMovesFromLevel = 0;
        Set<Integer> windMoves = new HashSet<>();
        int windMovesFromLevel = 0;
        Set<Integer> ballBombPulseMoves = new HashSet<>();
        int ballBombPulseMovesFromLevel = 0;
        Set<Integer> offensiveSunMoves = new HashSet<>();
        Set<Integer> supportSunMoves = new HashSet<>();
        Set<Integer> rainMoves = new HashSet<>();

        for (MoveLearnt ml : moveset) {
            Move m = moves.get(ml.move);

            if (m.power > 0 && (m.power == 60 || m.minHits > 1)) {
                technicianMoves.add(m.number);
                technicianMovesFromLevel++;
            }

            if (m.maxHits == 5) {
                multiStrikeMoves.add(m.number);
                multiStrikeMovesFromLevel++;
            }

            if (!m.isGoodDamaging(generationOfPokemon()))
                continue;

            if (m.isPunchMove) {
                punchMoves.add(m.number);
                punchMovesFromLevel++;
            }

            if (m.isSoundMove) {
                soundMoves.add(m.number);
                soundMovesFromLevel++;
            }

            if (m.isCustomKickMove) {
                kickMoves.add(m.number);
                kickMovesFromLevel++;
            }

            if (m.isCustomBiteMove) {
                biteMoves.add(m.number);
                biteMovesFromLevel++;
            }

            if (m.isCustomSliceMove) {
                sliceMoves.add(m.number);
                sliceMovesFromLevel++;
            }

            if (m.isCustomWindMove) {
                windMoves.add(m.number);
                windMovesFromLevel++;
            }

            if (m.isCustomBallBombMove || m.isCustomPulseMove) {
                ballBombPulseMoves.add(m.number);
                ballBombPulseMovesFromLevel++;
            }

            if (m.type == Type.FIRE || m.effect == MoveEffect.SOLAR_BEAM || m.effect == MoveEffect.WEATHER_BALL)
                offensiveSunMoves.add(m.number);

            if (m.effect == MoveEffect.RECOVER_HP_50_WEATHER || m.effect == MoveEffect.GROWTH)
                supportSunMoves.add(m.number);

            if (m.type == Type.WATER || m.effect == MoveEffect.THUNDER || m.effect == MoveEffect.HURRICANE || m.effect == MoveEffect.WEATHER_BALL)
                rainMoves.add(m.number);
        }

        for (int tmIdx = 0; tmIdx < tmMoves.size(); tmIdx++) {
            if (!tmCompatibility[tmIdx] || !tmsAvailable[tmIdx])
                continue;

            Move m = moves.get(tmMoves.get(tmIdx));

            if (m.power > 0 && (m.power == 60 || m.minHits > 1))
                technicianMoves.add(m.number);

            if (m.maxHits == 5)
                multiStrikeMoves.add(m.number);

            if (!m.isGoodDamaging(generationOfPokemon()))
                continue;

            if (m.isPunchMove)
                punchMoves.add(m.number);

            if (m.isSoundMove)
                soundMoves.add(m.number);

            if (m.isCustomKickMove)
                kickMoves.add(m.number);

            if (m.isCustomBiteMove)
                biteMoves.add(m.number);

            if (m.isCustomSliceMove)
                sliceMoves.add(m.number);

            if (m.isCustomWindMove)
                windMoves.add(m.number);

            if (m.isCustomBallBombMove || m.isCustomPulseMove)
                ballBombPulseMoves.add(m.number);

            if (m.type == Type.FIRE || m.effect == MoveEffect.SOLAR_BEAM || m.effect == MoveEffect.WEATHER_BALL)
                offensiveSunMoves.add(m.number);

            if (m.effect == MoveEffect.RECOVER_HP_50_WEATHER || m.effect == MoveEffect.GROWTH)
                supportSunMoves.add(m.number);

            if (m.type == Type.WATER || m.effect == MoveEffect.THUNDER || m.effect == MoveEffect.HURRICANE || m.effect == MoveEffect.WEATHER_BALL)
                rainMoves.add(m.number);
        }

        for (int tutorMoveIdx = 0; tutorMoveIdx < tutorMoveCount; tutorMoveIdx++) {
            if (!tutorMoveCompatibility[tutorMoveIdx])
                continue;

            Move m = moves.get(tutorMoves.get(tutorMoveIdx));

            if (m.power > 0 && (m.power == 60 || m.minHits > 1))
                technicianMoves.add(m.number);

            if (m.maxHits == 5)
                multiStrikeMoves.add(m.number);

            if (!m.isGoodDamaging(generationOfPokemon()))
                continue;

            if (m.isPunchMove)
                punchMoves.add(m.number);

            if (m.isSoundMove)
                soundMoves.add(m.number);

            if (m.isCustomKickMove)
                kickMoves.add(m.number);

            if (m.isCustomBiteMove)
                biteMoves.add(m.number);

            if (m.isCustomSliceMove)
                sliceMoves.add(m.number);

            if (m.isCustomWindMove)
                windMoves.add(m.number);

            if (m.isCustomBallBombMove || m.isCustomPulseMove)
                ballBombPulseMoves.add(m.number);

            if (m.type == Type.FIRE || m.effect == MoveEffect.SOLAR_BEAM || m.effect == MoveEffect.WEATHER_BALL)
                offensiveSunMoves.add(m.number);

            if (m.effect == MoveEffect.RECOVER_HP_50_WEATHER || m.effect == MoveEffect.GROWTH)
                supportSunMoves.add(m.number);

            if (m.type == Type.WATER || m.effect == MoveEffect.THUNDER || m.effect == MoveEffect.HURRICANE || m.effect == MoveEffect.WEATHER_BALL)
                rainMoves.add(m.number);
        }

        HashSet<Integer> irrelevantAbilities = new HashSet<>();

        if (settings.isEnsureRelevantAbilities()) {
            if (!settings.isDoubleBattleMode()) {
                irrelevantAbilities.add(Abilities.healer); // 131
                irrelevantAbilities.add(Abilities.friendGuard); // 132
                irrelevantAbilities.add(Abilities.telepathy); // 140
                irrelevantAbilities.add(Abilities.symbiosis); // 180
            }

            // No in-battle effect
            irrelevantAbilities.add(Abilities.illuminate); // 035
            irrelevantAbilities.add(Abilities.pickup); // 053
            irrelevantAbilities.add(Abilities.honeyGather); // 118

            // Useless in deathless random
            irrelevantAbilities.add(Abilities.aftermath); // 106

            // Bad
            irrelevantAbilities.add(Abilities.pressure); // 046
            irrelevantAbilities.add(Abilities.truant); // 054
            irrelevantAbilities.add(Abilities.normalize); // 096
            irrelevantAbilities.add(Abilities.klutz); // 103
            irrelevantAbilities.add(Abilities.stall); // 100
            irrelevantAbilities.add(Abilities.slowStart); // 112
            irrelevantAbilities.add(Abilities.defeatist); // 129
            irrelevantAbilities.add(Abilities.weakArmor); // 133
            if (generationOfPokemon() < 6)
                irrelevantAbilities.add(Abilities.infiltrator); // 151

            // Too niche
//            irrelevantAbilities.add(Abilities.damp); // 006
            irrelevantAbilities.add(Abilities.oblivious); // 012
            irrelevantAbilities.add(Abilities.suctionCups); // 021
            irrelevantAbilities.add(Abilities.runAway); // 050
            irrelevantAbilities.add(Abilities.stickyHold); // 060
            irrelevantAbilities.add(Abilities.tangledFeet); // 077
            irrelevantAbilities.add(Abilities.badDreams); // 123
            irrelevantAbilities.add(Abilities.pickpocket); // 124
            if (generationOfPokemon() >= 6) {
                irrelevantAbilities.add(Abilities.magician); // 170
                irrelevantAbilities.add(Abilities.auraBreak); // 170
            }

            // Weather
            irrelevantAbilities.add(Abilities.sandVeil); // 008
            irrelevantAbilities.add(Abilities.snowCloak); // 081
            if (generationOfPokemon() >= 6) {
                irrelevantAbilities.add(Abilities.primordialSea); // 189
                irrelevantAbilities.add(Abilities.desolateLand); // 190
                irrelevantAbilities.add(Abilities.deltaStream); // 191
            }

            // Overpowered or too volatile
            irrelevantAbilities.add(Abilities.shadowTag); // 023
            irrelevantAbilities.add(Abilities.wonderGuard); // 025
            irrelevantAbilities.add(Abilities.magnetPull); // 042
            irrelevantAbilities.add(Abilities.arenaTrap); // 071
            irrelevantAbilities.add(Abilities.moody); // 141
            if (generationOfPokemon() >= 6) {
                irrelevantAbilities.add(Abilities.parentalBond); // 141
            }

            // Forme-changing
            irrelevantAbilities.add(Abilities.forecast); // 059
            irrelevantAbilities.add(Abilities.multitype); // 121
            irrelevantAbilities.add(Abilities.flowerGift); // 122
            irrelevantAbilities.add(Abilities.zenMode); // 161
            if (generationOfPokemon() >= 6) {
                irrelevantAbilities.add(Abilities.stanceChange); // 176
            }

            if (weaknesses < 2) {
                irrelevantAbilities.add(Abilities.solidRock); // 116
                irrelevantAbilities.add(Abilities.filter); // 111
            }

            if (lowSpeed)
                irrelevantAbilities.add(Abilities.stench);

            if (!isWater)
                irrelevantAbilities.add(Abilities.torrent);

            if (isFire || weakToWater) {
                irrelevantAbilities.add(Abilities.drizzle);
            }

            if (highSpeed)
                irrelevantAbilities.add(Abilities.speedBoost);

            if (pk.hp == 1) {
                irrelevantAbilities.add(Abilities.battleArmor); // 004
                irrelevantAbilities.add(Abilities.sturdy); // 005
                irrelevantAbilities.add(Abilities.colorChange); // 016
                irrelevantAbilities.add(Abilities.shieldDust); //019
                irrelevantAbilities.add(Abilities.synchronize); // 028
                irrelevantAbilities.add(Abilities.naturalCure); // 030
                irrelevantAbilities.add(Abilities.trace); // 036
                irrelevantAbilities.add(Abilities.innerFocus); // 039
                irrelevantAbilities.add(Abilities.thickFat); //047
                irrelevantAbilities.add(Abilities.guts); // 062
                irrelevantAbilities.add(Abilities.marvelScale); // 063
                irrelevantAbilities.add(Abilities.liquidOoze); // 064
                irrelevantAbilities.add(Abilities.shellArmor); // 075
                irrelevantAbilities.add(Abilities.steadfast); // 080
                irrelevantAbilities.add(Abilities.drySkin); // 087
                irrelevantAbilities.add(Abilities.filter); // 111
                irrelevantAbilities.add(Abilities.solidRock); // 116
                irrelevantAbilities.add(Abilities.cursedBody); // 130
                irrelevantAbilities.add(Abilities.multiscale); // 136
                irrelevantAbilities.add(Abilities.toxicBoost); // 137
                irrelevantAbilities.add(Abilities.harvest); // 139
                irrelevantAbilities.add(Abilities.regenerator); // 144
                irrelevantAbilities.add(Abilities.rattled); // 155
                irrelevantAbilities.add(Abilities.furCoat); // 169
                irrelevantAbilities.add(Abilities.gooey); // 183
                irrelevantAbilities.add(Abilities.stamina); // 192
                irrelevantAbilities.add(Abilities.wimpOut); // 193
                irrelevantAbilities.add(Abilities.emergencyExit); // 194
                irrelevantAbilities.add(Abilities.waterCompaction); // 195
                irrelevantAbilities.add(Abilities.innardsOut); // 215
                irrelevantAbilities.add(Abilities.fluffy); // 218
                irrelevantAbilities.add(Abilities.shadowShield); // 231
                irrelevantAbilities.add(Abilities.prismArmor); // 232
                irrelevantAbilities.add(Abilities.cottonDown); // 238
                irrelevantAbilities.add(Abilities.mirrorArmor); // 240
                irrelevantAbilities.add(Abilities.steamEngine); // 243
                irrelevantAbilities.add(Abilities.sandSpit); // 245
                irrelevantAbilities.add(Abilities.iceScales); // 246
                irrelevantAbilities.add(Abilities.perishBody); // 253
                irrelevantAbilities.add(Abilities.seedSower); // 269
                irrelevantAbilities.add(Abilities.thermalExchange); // 270
                irrelevantAbilities.add(Abilities.angerShell); // 271
                irrelevantAbilities.add(Abilities.electromorphosis); // 280
                irrelevantAbilities.add(Abilities.toxicDebris); // 295

                if (isParagonLite) {
                    irrelevantAbilities.add(Abilities.innerFocus);
                    irrelevantAbilities.add(ParagonLiteAbilities.colossal);
                }
            }

            if (pk.bst() > 550)
                irrelevantAbilities.add(Abilities.magicGuard); // Magic Guard is far too powerful

            if (!(pk.primaryType == Type.NORMAL && pk.secondaryType == null)) {
                irrelevantAbilities.add(Abilities.colorChange);
                irrelevantAbilities.add(Abilities.imposter);
            }

            if (isElectric || isGround)
                irrelevantAbilities.add(Abilities.limber);

            // Avoid Flying-type STAB Gale Wings in Gen VI
            // Otherwise, avoid giving it to non-Flying-types 
            if ((generationOfPokemon() == 6) == isFlying)
                irrelevantAbilities.add(Abilities.galeWings);

            // Protean and Libero are OP until Gen IX nerf
            if (generationOfPokemon() >= 6 && generationOfPokemon() < 9) {
                irrelevantAbilities.add(Abilities.protean);
                irrelevantAbilities.add(Abilities.libero);
            }

            if (generationOfPokemon() == 5 && (highAtk || highSpA)) {
                irrelevantAbilities.add(Abilities.protean);
            }

            // Abilities that activate on contact
            if (lowPhysBulk) {
                irrelevantAbilities.add(Abilities.staticTheAbilityNotTheKeyword);
                irrelevantAbilities.add(Abilities.roughSkin);
                irrelevantAbilities.add(Abilities.effectSpore);
                irrelevantAbilities.add(Abilities.poisonPoint);
                irrelevantAbilities.add(Abilities.flameBody);
                irrelevantAbilities.add(Abilities.cuteCharm);
                irrelevantAbilities.add(Abilities.mummy);
                irrelevantAbilities.add(Abilities.ironBarbs);
            }

            if (!isGrass)
                if (generationOfPokemon() >= 6) {
                    irrelevantAbilities.add(Abilities.flowerVeil); // 166
                }

            if (isFlying || isGround)
                if (generationOfPokemon() >= 6) {
                    irrelevantAbilities.add(Abilities.grassPelt);
                }

            if (resistsElectric)
                irrelevantAbilities.add(Abilities.voltAbsorb);

            if (resistsWater)
                irrelevantAbilities.add(Abilities.waterAbsorb);

            if (isPoison || isSteel) {
                irrelevantAbilities.add(Abilities.immunity);
                irrelevantAbilities.add(Abilities.poisonHeal);
            }

            if (resistsPoison)
                irrelevantAbilities.add(Abilities.immunity);

            if (!isFire && resistsFire)
                irrelevantAbilities.add(Abilities.flashFire);

            if (resistsGround)
                irrelevantAbilities.add(Abilities.levitate);

            if (isGround || (resistsElectric && !higherOrEqualSpAtk))
                irrelevantAbilities.add(Abilities.lightningRod);

            double hugePowerAtk = (pk.attack + 12.5) * 1.5 - 12.5;
            if (hugePowerAtk > 160 || pk.bst() - pk.attack + hugePowerAtk > 650 || hugePowerAtk < pk.spatk) {
                irrelevantAbilities.add(Abilities.hugePower);

                if (!isParagonLite)
                    irrelevantAbilities.add(Abilities.purePower);

            }

            double purePowerSpa = (pk.spatk + 12.5) * 1.5 - 12.5;
            if (isParagonLite && (purePowerSpa > 160 || pk.bst() - pk.spatk + purePowerSpa > 650 || purePowerSpa < pk.attack)) {
                irrelevantAbilities.add(Abilities.purePower);
            }

            if (isRock || isGround || isSteel || isIce)
                irrelevantAbilities.add(Abilities.overcoat);

            if (isFire)
                irrelevantAbilities.add(Abilities.waterVeil);

            if (!resistsWater || (isFire && !isWater))
                irrelevantAbilities.add(Abilities.rainDish);

            if (resistsFire && resistsIce)
                irrelevantAbilities.add(Abilities.thickFat);

            // only benefits physical attackers
            if (!higherOrEqualAttack) {
                irrelevantAbilities.add(Abilities.hyperCutter);
                irrelevantAbilities.add(Abilities.guts);
                irrelevantAbilities.add(Abilities.rockHead);
                irrelevantAbilities.add(Abilities.ironFist);
                irrelevantAbilities.add(Abilities.skillLink);
                irrelevantAbilities.add(Abilities.defiant);
                irrelevantAbilities.add(Abilities.poisonTouch);
                irrelevantAbilities.add(Abilities.moxie);
                irrelevantAbilities.add(Abilities.strongJaw);
                irrelevantAbilities.add(Abilities.toughClaws);
                irrelevantAbilities.add(Abilities.sharpness);
            }

            // only benefits special attackers
            if (!higherOrEqualSpAtk) {
                irrelevantAbilities.add(Abilities.competitive);
                irrelevantAbilities.add(Abilities.megaLauncher);
            }

            if (lowBulk)
                irrelevantAbilities.add(Abilities.marvelScale);

            if (!isGrass)
                irrelevantAbilities.add(Abilities.overgrow);

            if (!isFire) {
                irrelevantAbilities.add(Abilities.blaze);
            }

            if (isWater || weakToFire)
                irrelevantAbilities.add(Abilities.drought);

            if (!isBug)
                irrelevantAbilities.add(Abilities.swarm);

            if (isGround || !mediumSpeed)
                irrelevantAbilities.add(Abilities.motorDrive);

            if (resistsFire)
                irrelevantAbilities.add(Abilities.heatproof);

            if (resistsWater || weakToFire)
                irrelevantAbilities.add(Abilities.drySkin);

            if (!resistsWater || isFire)
                irrelevantAbilities.add(Abilities.hydration);

            if (!resistsFire || !higherOrEqualSpAtk)
                irrelevantAbilities.add(Abilities.solarPower);

            if (!mediumSpeed || !higherOrEqualSpAtk)
                irrelevantAbilities.add(Abilities.quickFeet);

            if (!(isNormal || isFighting))
                irrelevantAbilities.add(Abilities.scrappy);

            if (multiStrikeMoves.size() < 2 || multiStrikeMovesFromLevel == 0)
                irrelevantAbilities.add(Abilities.skillLink);

            if (resistsWater && !higherOrEqualSpAtk)
                irrelevantAbilities.add(Abilities.stormDrain);

            if (lowBulk || !higherOrEqualAttack)
                irrelevantAbilities.add(Abilities.reckless);

            if (!lowAtk || !lowSpA)
                irrelevantAbilities.add(Abilities.contrary);

            if (highAtk || highSpA)
                irrelevantAbilities.add(Abilities.download);

            if (isPoison || isSteel || !higherOrEqualAttack)
                irrelevantAbilities.add(Abilities.toxicBoost);

            if (isFire || !higherOrEqualSpAtk)
                irrelevantAbilities.add(Abilities.flareBoost);

            if (!highPhysBulk)
                irrelevantAbilities.add(Abilities.bigPecks);

            if (!mediumSpeed)
                irrelevantAbilities.add(Abilities.unburden);

            if (!lowSpeed)
                irrelevantAbilities.add(Abilities.analytic);

            if (pk.hp < 85 || pk.hp > 100)
                irrelevantAbilities.add(Abilities.imposter);

            if (!higherOrEqualAttack && !isParagonLite)
                irrelevantAbilities.add(Abilities.justified);

            if (weakToBug || weakToGhost || weakToDark || !mediumSpeed)
                irrelevantAbilities.add(Abilities.rattled);

            if (resistsGrass && !higherOrEqualAttack)
                irrelevantAbilities.add(Abilities.sapSipper);

            if (highSpeed) {
                irrelevantAbilities.add(Abilities.steadfast);
                irrelevantAbilities.add(Abilities.innerFocus);
                irrelevantAbilities.add(Abilities.lightMetal);
                irrelevantAbilities.add(Abilities.prankster);
            }

            if (pk.genderRatio == 255)
                irrelevantAbilities.add(Abilities.rivalry);

            if (highPhysBulk)
                irrelevantAbilities.add(Abilities.marvelScale);

            setTypeChangeAbilityIrrelevant(pk, Abilities.refrigerate, Type.ICE, isCustomTypeEffectiveness, against, higherOrEqualAttack, irrelevantAbilities);
            setTypeChangeAbilityIrrelevant(pk, Abilities.pixilate, Type.FAIRY, isCustomTypeEffectiveness, against, higherOrEqualAttack, irrelevantAbilities);
            setTypeChangeAbilityIrrelevant(pk, Abilities.aerilate, Type.FLYING, isCustomTypeEffectiveness, against, higherOrEqualAttack, irrelevantAbilities);
            setTypeChangeAbilityIrrelevant(pk, Abilities.galvanize, Type.ELECTRIC, isCustomTypeEffectiveness, against, higherOrEqualAttack, irrelevantAbilities);

            if (lowSpeed || highSpeed)
                irrelevantAbilities.add(Abilities.slushRush);

            if (highSpeed)
                irrelevantAbilities.add(Abilities.triage);

            if (technicianMovesFromLevel == 0 || technicianMoves.size() < 2)
                irrelevantAbilities.add(Abilities.technician);

            if (multiStrikeMovesFromLevel == 0 || multiStrikeMoves.size() < 2)
                irrelevantAbilities.add(Abilities.skillLink);

            if (punchMovesFromLevel == 0 || punchMoves.size() < 2)
                irrelevantAbilities.add(Abilities.ironFist);

            if (biteMovesFromLevel == 0 || biteMoves.size() < 2)
                irrelevantAbilities.add(Abilities.strongJaw);

            if (sliceMovesFromLevel == 0 || sliceMoves.size() < 2)
                irrelevantAbilities.add(Abilities.sharpness);

            if (ballBombPulseMovesFromLevel == 0 || ballBombPulseMoves.size() < 2)
                irrelevantAbilities.add(Abilities.megaLauncher);
            
            if (isParagonLite) {
                if ((isWater && (!lowAtk || !lowSpA)) || rainMoves.size() < 2)
                    irrelevantAbilities.add(Abilities.damp);

                if (resistsGround && resistsWater)
                    irrelevantAbilities.add(Abilities.magmaArmor);

                if (!higherOrEqualAttack && resistsDark)
                    irrelevantAbilities.add(Abilities.justified);

                if (isFlying)
                    irrelevantAbilities.add(ParagonLiteAbilities.heavyWing);

                if (resistsBug)
                    irrelevantAbilities.add(ParagonLiteAbilities.insectivore);

                if (!higherOrEqualSpAtk)
                    irrelevantAbilities.add(ParagonLiteAbilities.prestige);

                if (!higherOrEqualAttack || kickMovesFromLevel == 0 || kickMoves.size() < 2)
                    irrelevantAbilities.add(ParagonLiteAbilities.luckyFoot);

                if (resistsPsychic)
                    irrelevantAbilities.add(ParagonLiteAbilities.assimilate);

                if (!higherOrEqualSpAtk || soundMovesFromLevel == 0 || soundMoves.size() < 2)
                    irrelevantAbilities.add(ParagonLiteAbilities.cacophony);

                if (windMovesFromLevel == 0 || windMoves.size() < 2)
                    irrelevantAbilities.add(ParagonLiteAbilities.windWhipper);

                if (resistsWater && resistsGround)
                    irrelevantAbilities.add(ParagonLiteAbilities.glazeware);

                if ((isFire && (!lowAtk || !lowSpA)) || ((highAtk || highSpA) && isGrass && !offensiveSunMoves.isEmpty()) || offensiveSunMoves.size() < (supportSunMoves.isEmpty() ? 2 : 1))
                    irrelevantAbilities.add(ParagonLiteAbilities.sunSoaked);
            }
        }

        List<Integer> availableAbilitiesList = new ArrayList<>(availableAbilities);
        while (!availableAbilitiesList.isEmpty()) {
            int randomIndex = this.random.nextInt(availableAbilitiesList.size());
            newAbility = availableAbilitiesList.get(randomIndex);

            if (bannedAbilities.contains(newAbility) || irrelevantAbilities.contains(newAbility)) {
                // swap and remove
                int lastIndex = availableAbilitiesList.size() - 1;
                availableAbilitiesList.set(randomIndex, availableAbilitiesList.get(lastIndex));
                availableAbilitiesList.remove(lastIndex);
                continue;
            }

            boolean repeat = false;
            for (int alreadySetAbility : alreadySetAbilities) {
                if (alreadySetAbility == newAbility) {
                    repeat = true;
                    break;
                }
            }

            if (!repeat) {
                if (useVariations) {
                    newAbility = pickRandomAbilityVariation(settings, newAbility, alreadySetAbilities);
                }
                break;
            }
        }

        if (newAbility == -1)
            throw new RandomizationException("Failed to get ability");

        return newAbility;
    }

    private void setTypeChangeAbilityIrrelevant(Pokemon pk, int ability, Type type, boolean isCustomTypeEffectiveness, Map<Type, Effectiveness> against,
                                                boolean higherOrEqualAttack, HashSet<Integer> irrelevantAbilities) {
        if (!higherOrEqualAttack || pk.primaryType == Type.NORMAL || pk.secondaryType == Type.NORMAL) {
            irrelevantAbilities.add(ability);
            return;
        }

        if (irrelevantAbilities.contains(ability) || pk.primaryType == type || pk.secondaryType == type)
            return;

        boolean shouldInclude = false;
        List<Type> superEffectiveTypes = Effectiveness.superEffective(type, generationOfPokemon(), true, isCustomTypeEffectiveness, typeInGame(Type.FAIRY));
        for (Type superEffectiveType : superEffectiveTypes) {
            if (against.get(superEffectiveType) == Effectiveness.DOUBLE || against.get(superEffectiveType) == Effectiveness.QUADRUPLE)
                continue;

            Map<Type, Effectiveness> effectivenessMap = Effectiveness.against(superEffectiveType, null, generationOfPokemon(), true, isCustomTypeEffectiveness, typeInGame(Type.FAIRY));
            assert effectivenessMap != null;

            Effectiveness primaryEffectiveness = effectivenessMap.get(pk.primaryType);
            Effectiveness secondaryEffectiveness = pk.secondaryType == null ? null : effectivenessMap.get(pk.secondaryType);

            if ((primaryEffectiveness.ordinal() > Effectiveness.NEUTRAL.ordinal()) || (secondaryEffectiveness != null && secondaryEffectiveness.ordinal() > Effectiveness.NEUTRAL.ordinal()))
                continue;

            if (against.get(superEffectiveType).ordinal() <= Effectiveness.NEUTRAL.ordinal()) {
                shouldInclude = true;
                break;
            }
        }
        if (!shouldInclude)
            irrelevantAbilities.add(ability);
    }

    @Override
    public void randomEncounters(Settings settings) {
        boolean useTimeOfDay = settings.isUseTimeBasedEncounters();
        boolean catchEmAll = settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.CATCH_EM_ALL;
        boolean typeThemed = settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.TYPE_THEME_AREAS;
        boolean usePowerLevels = settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.SIMILAR_STRENGTH;
        boolean noLegendaries = settings.isBlockWildLegendaries();
        boolean balanceShakingGrass = settings.isBalanceShakingGrass();
        int levelModifier = settings.isWildLevelsModified() ? settings.getWildLevelModifier() : 0;
        boolean allowAltFormes = settings.isAllowWildAltFormes();
        boolean banIrregularAltFormes = settings.isBanIrregularAltFormes();
        boolean abilitiesAreRandomized = settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE;

        List<EncounterSet> currentEncounters = this.getEncounters(useTimeOfDay);

        if (isORAS) {
            List<EncounterSet> collapsedEncounters = collapseAreasORAS(currentEncounters);
            area1to1EncountersImpl(collapsedEncounters, settings);
            enhanceRandomEncountersORAS(collapsedEncounters, settings);
            setEncounters(useTimeOfDay, currentEncounters);
            return;
        }

        checkPokemonRestrictions();

        // New: randomize the order encounter sets are randomized in.
        // Leads to less predictable results for various modifiers.
        // Need to keep the original ordering around for saving though.
        List<EncounterSet> scrambledEncounters = new ArrayList<>(currentEncounters);
        Collections.shuffle(scrambledEncounters, this.random);

        List<Pokemon> banned = this.bannedForWildEncounters();
        banned.addAll(this.getBannedFormesForPlayerPokemon());
        if (!abilitiesAreRandomized) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        if (banIrregularAltFormes) {
            banned.addAll(getIrregularFormes());
        }
        // Assume EITHER catch em all OR type themed OR match strength for now
        if (catchEmAll) {
            List<Pokemon> allPokes;
            if (allowAltFormes) {
                allPokes = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes) : new ArrayList<>(
                        mainPokemonListInclFormes);
                allPokes.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
            } else {
                allPokes = noLegendaries ? new ArrayList<>(noLegendaryList) : new ArrayList<>(
                        mainPokemonList);
            }
            allPokes.removeAll(banned);

            for (EncounterSet area : scrambledEncounters) {
                List<Pokemon> pickablePokemon = allPokes;
                if (area.bannedPokemon.size() > 0) {
                    pickablePokemon = new ArrayList<>(allPokes);
                    pickablePokemon.removeAll(area.bannedPokemon);
                }
                for (Encounter enc : area.encounters) {
                    // In Catch 'Em All mode, don't randomize encounters for Pokemon that are banned for
                    // wild encounters. Otherwise, it may be impossible to obtain this Pokemon unless it
                    // randomly appears as a static or unless it becomes a random evolution.
                    if (banned.contains(enc.pokemon)) {
                        continue;
                    }

                    // Pick a random pokemon
                    if (pickablePokemon.size() == 0) {
                        // Only banned pokes are left, ignore them and pick
                        // something else for now.
                        List<Pokemon> tempPickable;
                        if (allowAltFormes) {
                            tempPickable = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes) : new ArrayList<>(
                                    mainPokemonListInclFormes);
                            tempPickable.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
                        } else {
                            tempPickable = noLegendaries ? new ArrayList<>(noLegendaryList) : new ArrayList<>(
                                    mainPokemonList);
                        }
                        tempPickable.removeAll(banned);
                        tempPickable.removeAll(area.bannedPokemon);
                        if (tempPickable.size() == 0) {
                            throw new RandomizationException("ERROR: Couldn't replace a wild Pokemon!");
                        }
                        int picked = this.random.nextInt(tempPickable.size());
                        enc.pokemon = tempPickable.get(picked);
                        setFormeForEncounter(enc, enc.pokemon);
                    } else {
                        // Picked this Pokemon, remove it
                        int picked = this.random.nextInt(pickablePokemon.size());
                        enc.pokemon = pickablePokemon.get(picked);
                        pickablePokemon.remove(picked);
                        if (allPokes != pickablePokemon) {
                            allPokes.remove(enc.pokemon);
                        }
                        setFormeForEncounter(enc, enc.pokemon);
                        if (allPokes.size() == 0) {
                            // Start again
                            if (allowAltFormes) {
                                allPokes.addAll(noLegendaries ? noLegendaryListInclFormes : mainPokemonListInclFormes);
                                allPokes.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
                            } else {
                                allPokes.addAll(noLegendaries ? noLegendaryList : mainPokemonList);
                            }
                            allPokes.removeAll(banned);
                            if (pickablePokemon != allPokes) {
                                pickablePokemon.addAll(allPokes);
                                pickablePokemon.removeAll(area.bannedPokemon);
                            }
                        }
                    }
                }
            }
        } else if (typeThemed) {
            Map<Type, List<Pokemon>> cachedPokeLists = new TreeMap<>();
            for (EncounterSet area : scrambledEncounters) {
                List<Pokemon> possiblePokemon = null;
                int iterLoops = 0;
                while (possiblePokemon == null && iterLoops < 10000) {
                    Type areaTheme = randomType();
                    if (!cachedPokeLists.containsKey(areaTheme)) {
                        List<Pokemon> pType = allowAltFormes ? pokemonOfTypeInclFormes(areaTheme, noLegendaries) :
                                pokemonOfType(areaTheme, noLegendaries);
                        pType.removeAll(banned);
                        cachedPokeLists.put(areaTheme, pType);
                    }
                    possiblePokemon = cachedPokeLists.get(areaTheme);
                    if (area.bannedPokemon.size() > 0) {
                        possiblePokemon = new ArrayList<>(possiblePokemon);
                        possiblePokemon.removeAll(area.bannedPokemon);
                    }
                    if (possiblePokemon.size() == 0) {
                        // Can't use this type for this area
                        possiblePokemon = null;
                    }
                    iterLoops++;
                }
                if (possiblePokemon == null) {
                    throw new RandomizationException("Could not randomize an area in a reasonable amount of attempts.");
                }
                for (Encounter enc : area.encounters) {
                    // Pick a random themed pokemon
                    enc.pokemon = possiblePokemon.get(this.random.nextInt(possiblePokemon.size()));
                    while (enc.pokemon.actuallyCosmetic) {
                        enc.pokemon = possiblePokemon.get(this.random.nextInt(possiblePokemon.size()));
                    }
                    setFormeForEncounter(enc, enc.pokemon);
                }
            }
        } else if (usePowerLevels) {
            List<Pokemon> allowedPokes;
            if (allowAltFormes) {
                allowedPokes = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes)
                        : new ArrayList<>(mainPokemonListInclFormes);
            } else {
                allowedPokes = noLegendaries ? new ArrayList<>(noLegendaryList)
                        : new ArrayList<>(mainPokemonList);
            }
            allowedPokes.removeAll(banned);
            for (EncounterSet area : scrambledEncounters) {
                List<Pokemon> localAllowed = allowedPokes;
                if (area.bannedPokemon.size() > 0) {
                    localAllowed = new ArrayList<>(allowedPokes);
                    localAllowed.removeAll(area.bannedPokemon);
                }
                for (Encounter enc : area.encounters) {
                    if (balanceShakingGrass) {
                        if (area.displayName.contains("Shaking")) {
                            enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, (enc.level + enc.maxLevel) / 2);
                            while (enc.pokemon.actuallyCosmetic) {
                                enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, (enc.level + enc.maxLevel) / 2);
                            }
                            setFormeForEncounter(enc, enc.pokemon);
                        } else {
                            enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, 100);
                            while (enc.pokemon.actuallyCosmetic) {
                                enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, 100);
                            }
                            setFormeForEncounter(enc, enc.pokemon);
                        }
                    } else {
                        enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, 100);
                        while (enc.pokemon.actuallyCosmetic) {
                            enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, 100);
                        }
                        setFormeForEncounter(enc, enc.pokemon);
                    }
                }
            }
        } else {
            // Entirely random
            for (EncounterSet area : scrambledEncounters) {
                for (Encounter enc : area.encounters) {
                    enc.pokemon = pickEntirelyRandomPokemon(allowAltFormes, noLegendaries, area, banned);
                    setFormeForEncounter(enc, enc.pokemon);
                }
            }
        }
        if (levelModifier != 0) {
            for (EncounterSet area : currentEncounters) {
                for (Encounter enc : area.encounters) {
                    enc.level = Math.min(100, (int) Math.round(enc.level * (1 + levelModifier / 100.0)));
                    enc.maxLevel = Math.min(100, (int) Math.round(enc.maxLevel * (1 + levelModifier / 100.0)));
                }
            }
        }

        setEncounters(useTimeOfDay, currentEncounters);
    }

    @Override
    public void area1to1Encounters(Settings settings) {
        boolean useTimeOfDay = settings.isUseTimeBasedEncounters();

        List<EncounterSet> currentEncounters = this.getEncounters(useTimeOfDay);
        if (isORAS) {
            List<EncounterSet> collapsedEncounters = collapseAreasORAS(currentEncounters);
            area1to1EncountersImpl(collapsedEncounters, settings);
            setEncounters(useTimeOfDay, currentEncounters);
            return;
        } else {
            area1to1EncountersImpl(currentEncounters, settings);
            setEncounters(useTimeOfDay, currentEncounters);
        }
    }

    private void area1to1EncountersImpl(List<EncounterSet> currentEncounters, Settings settings) {
        boolean catchEmAll = settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.CATCH_EM_ALL;
        boolean typeThemed = settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.TYPE_THEME_AREAS;
        boolean usePowerLevels = settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.SIMILAR_STRENGTH;
        boolean noLegendaries = settings.isBlockWildLegendaries();
        int levelModifier = settings.isWildLevelsModified() ? settings.getWildLevelModifier() : 0;
        boolean allowAltFormes = settings.isAllowWildAltFormes();
        boolean banIrregularAltFormes = settings.isBanIrregularAltFormes();
        boolean abilitiesAreRandomized = settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE;

        checkPokemonRestrictions();
        List<Pokemon> banned = this.bannedForWildEncounters();
        banned.addAll(this.getBannedFormesForPlayerPokemon());
        if (!abilitiesAreRandomized) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        if (banIrregularAltFormes) {
            banned.addAll(getIrregularFormes());
        }

        // New: randomize the order encounter sets are randomized in.
        // Leads to less predictable results for various modifiers.
        // Need to keep the original ordering around for saving though.
        List<EncounterSet> scrambledEncounters = new ArrayList<>(currentEncounters);
        Collections.shuffle(scrambledEncounters, this.random);

        // Assume EITHER catch em all OR type themed for now
        if (catchEmAll) {
            List<Pokemon> allPokes;
            if (allowAltFormes) {
                allPokes = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes) : new ArrayList<>(
                        mainPokemonListInclFormes);
                allPokes.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
            } else {
                allPokes = noLegendaries ? new ArrayList<>(noLegendaryList) : new ArrayList<>(
                        mainPokemonList);
            }
            allPokes.removeAll(banned);
            for (EncounterSet area : scrambledEncounters) {
                Set<Pokemon> inArea = pokemonInArea(area);
                // Build area map using catch em all
                Map<Pokemon, Pokemon> areaMap = new TreeMap<>();
                List<Pokemon> pickablePokemon = allPokes;
                if (area.bannedPokemon.size() > 0) {
                    pickablePokemon = new ArrayList<>(allPokes);
                    pickablePokemon.removeAll(area.bannedPokemon);
                }
                for (Pokemon areaPk : inArea) {
                    if (pickablePokemon.size() == 0) {
                        // No more pickable pokes left, take a random one
                        List<Pokemon> tempPickable;
                        if (allowAltFormes) {
                            tempPickable = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes) : new ArrayList<>(
                                    mainPokemonListInclFormes);
                            tempPickable.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
                        } else {
                            tempPickable = noLegendaries ? new ArrayList<>(noLegendaryList) : new ArrayList<>(
                                    mainPokemonList);
                        }
                        tempPickable.removeAll(banned);
                        tempPickable.removeAll(area.bannedPokemon);
                        if (tempPickable.size() == 0) {
                            throw new RandomizationException("ERROR: Couldn't replace a wild Pokemon!");
                        }
                        int picked = this.random.nextInt(tempPickable.size());
                        Pokemon pickedMN = tempPickable.get(picked);
                        areaMap.put(areaPk, pickedMN);
                    } else {
                        int picked = this.random.nextInt(allPokes.size());
                        Pokemon pickedMN = allPokes.get(picked);
                        areaMap.put(areaPk, pickedMN);
                        pickablePokemon.remove(pickedMN);
                        if (allPokes != pickablePokemon) {
                            allPokes.remove(pickedMN);
                        }
                        if (allPokes.size() == 0) {
                            // Start again
                            if (allowAltFormes) {
                                allPokes.addAll(noLegendaries ? noLegendaryListInclFormes : mainPokemonListInclFormes);
                                allPokes.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
                            } else {
                                allPokes.addAll(noLegendaries ? noLegendaryList : mainPokemonList);
                            }
                            allPokes.removeAll(banned);
                            if (pickablePokemon != allPokes) {
                                pickablePokemon.addAll(allPokes);
                                pickablePokemon.removeAll(area.bannedPokemon);
                            }
                        }
                    }
                }
                for (Encounter enc : area.encounters) {
                    // In Catch 'Em All mode, don't randomize encounters for Pokemon that are banned for
                    // wild encounters. Otherwise, it may be impossible to obtain this Pokemon unless it
                    // randomly appears as a static or unless it becomes a random evolution.
                    if (banned.contains(enc.pokemon)) {
                        continue;
                    }
                    // Apply the map
                    enc.pokemon = areaMap.get(enc.pokemon);
                    setFormeForEncounter(enc, enc.pokemon);
                }
            }
        } else if (typeThemed) {
            Map<Type, List<Pokemon>> cachedPokeLists = new TreeMap<>();
            for (EncounterSet area : scrambledEncounters) {
                // Poke-set
                Set<Pokemon> inArea = pokemonInArea(area);
                List<Pokemon> possiblePokemon = null;
                int iterLoops = 0;
                while (possiblePokemon == null && iterLoops < 10000) {
                    Type areaTheme = randomType();
                    if (!cachedPokeLists.containsKey(areaTheme)) {
                        List<Pokemon> pType = allowAltFormes ? pokemonOfTypeInclFormes(areaTheme, noLegendaries) :
                                pokemonOfType(areaTheme, noLegendaries);
                        pType.removeAll(banned);
                        cachedPokeLists.put(areaTheme, pType);
                    }
                    possiblePokemon = new ArrayList<>(cachedPokeLists.get(areaTheme));
                    if (area.bannedPokemon.size() > 0) {
                        possiblePokemon.removeAll(area.bannedPokemon);
                    }
                    if (possiblePokemon.size() < inArea.size()) {
                        // Can't use this type for this area
                        possiblePokemon = null;
                    }
                    iterLoops++;
                }
                if (possiblePokemon == null) {
                    throw new RandomizationException("Could not randomize an area in a reasonable amount of attempts.");
                }

                // Build area map using type theme.
                Map<Pokemon, Pokemon> areaMap = new TreeMap<>();
                for (Pokemon areaPk : inArea) {
                    int picked = this.random.nextInt(possiblePokemon.size());
                    Pokemon pickedMN = possiblePokemon.get(picked);
                    while (pickedMN.actuallyCosmetic) {
                        picked = this.random.nextInt(possiblePokemon.size());
                        pickedMN = possiblePokemon.get(picked);
                    }
                    areaMap.put(areaPk, pickedMN);
                    possiblePokemon.remove(picked);
                }
                for (Encounter enc : area.encounters) {
                    // Apply the map
                    enc.pokemon = areaMap.get(enc.pokemon);
                    setFormeForEncounter(enc, enc.pokemon);
                }
            }
        } else if (usePowerLevels) {
            List<Pokemon> allowedPokes;
            if (allowAltFormes) {
                allowedPokes = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes)
                        : new ArrayList<>(mainPokemonListInclFormes);
            } else {
                allowedPokes = noLegendaries ? new ArrayList<>(noLegendaryList)
                        : new ArrayList<>(mainPokemonList);
            }
            allowedPokes.removeAll(banned);
            for (EncounterSet area : scrambledEncounters) {
                // Poke-set
                Set<Pokemon> inArea = pokemonInArea(area);
                // Build area map using randoms
                Map<Pokemon, Pokemon> areaMap = new TreeMap<>();
                List<Pokemon> usedPks = new ArrayList<>();
                List<Pokemon> localAllowed = allowedPokes;
                if (area.bannedPokemon.size() > 0) {
                    localAllowed = new ArrayList<>(allowedPokes);
                    localAllowed.removeAll(area.bannedPokemon);
                }
                for (Pokemon areaPk : inArea) {
                    Pokemon picked = pickWildPowerLvlReplacement(localAllowed, areaPk, false, usedPks, 100);
                    while (picked.actuallyCosmetic) {
                        picked = pickWildPowerLvlReplacement(localAllowed, areaPk, false, usedPks, 100);
                    }
                    areaMap.put(areaPk, picked);
                    usedPks.add(picked);
                }
                for (Encounter enc : area.encounters) {
                    // Apply the map
                    enc.pokemon = areaMap.get(enc.pokemon);
                    setFormeForEncounter(enc, enc.pokemon);
                }
            }
        } else {
            // Entirely random
            for (EncounterSet area : scrambledEncounters) {
                // Poke-set
                Set<Pokemon> inArea = pokemonInArea(area);
                // Build area map using randoms
                Map<Pokemon, Pokemon> areaMap = new TreeMap<>();
                for (Pokemon areaPk : inArea) {
                    Pokemon picked = pickEntirelyRandomPokemon(allowAltFormes, noLegendaries, area, banned);
                    while (areaMap.containsValue(picked)) {
                        picked = pickEntirelyRandomPokemon(allowAltFormes, noLegendaries, area, banned);
                    }
                    areaMap.put(areaPk, picked);
                }
                for (Encounter enc : area.encounters) {
                    // Apply the map
                    enc.pokemon = areaMap.get(enc.pokemon);
                    setFormeForEncounter(enc, enc.pokemon);
                }
            }
        }

        if (levelModifier != 0) {
            for (EncounterSet area : currentEncounters) {
                for (Encounter enc : area.encounters) {
                    enc.level = Math.min(100, (int) Math.round(enc.level * (1 + levelModifier / 100.0)));
                    enc.maxLevel = Math.min(100, (int) Math.round(enc.maxLevel * (1 + levelModifier / 100.0)));
                }
            }
        }
    }

    @Override
    public void game1to1Encounters(Settings settings) {
        boolean useTimeOfDay = settings.isUseTimeBasedEncounters();
        boolean usePowerLevels = settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.SIMILAR_STRENGTH;
        boolean noLegendaries = settings.isBlockWildLegendaries();
        int levelModifier = settings.isWildLevelsModified() ? settings.getWildLevelModifier() : 0;
        boolean allowAltFormes = settings.isAllowWildAltFormes();
        boolean banIrregularAltFormes = settings.isBanIrregularAltFormes();
        boolean abilitiesAreRandomized = settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE;

        checkPokemonRestrictions();
        // Build the full 1-to-1 map
        Map<Pokemon, Pokemon> translateMap = new TreeMap<>();
        List<Pokemon> remainingLeft = allPokemonInclFormesWithoutNull();
        remainingLeft.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
        List<Pokemon> remainingRight;
        if (allowAltFormes) {
            remainingRight = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes)
                    : new ArrayList<>(mainPokemonListInclFormes);
            remainingRight.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
        } else {
            remainingRight = noLegendaries ? new ArrayList<>(noLegendaryList)
                    : new ArrayList<>(mainPokemonList);
        }
        List<Pokemon> banned = this.bannedForWildEncounters();
        banned.addAll(this.getBannedFormesForPlayerPokemon());
        if (!abilitiesAreRandomized) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        if (banIrregularAltFormes) {
            banned.addAll(getIrregularFormes());
        }
        // Banned pokemon should be mapped to themselves
        for (Pokemon bannedPK : banned) {
            translateMap.put(bannedPK, bannedPK);
            remainingLeft.remove(bannedPK);
            remainingRight.remove(bannedPK);
        }
        while (!remainingLeft.isEmpty()) {
            if (usePowerLevels) {
                int pickedLeft = this.random.nextInt(remainingLeft.size());
                Pokemon pickedLeftP = remainingLeft.remove(pickedLeft);
                Pokemon pickedRightP;
                if (remainingRight.size() == 1) {
                    // pick this (it may or may not be the same poke)
                    pickedRightP = remainingRight.get(0);
                } else {
                    // pick on power level with the current one blocked
                    pickedRightP = pickWildPowerLvlReplacement(remainingRight, pickedLeftP, true, null, 100);
                }
                remainingRight.remove(pickedRightP);
                translateMap.put(pickedLeftP, pickedRightP);
            } else {
                int pickedLeft = this.random.nextInt(remainingLeft.size());
                int pickedRight = this.random.nextInt(remainingRight.size());
                Pokemon pickedLeftP = remainingLeft.remove(pickedLeft);
                Pokemon pickedRightP = remainingRight.get(pickedRight);
                while (pickedLeftP.number == pickedRightP.number && remainingRight.size() != 1) {
                    // Reroll for a different pokemon if at all possible
                    pickedRight = this.random.nextInt(remainingRight.size());
                    pickedRightP = remainingRight.get(pickedRight);
                }
                remainingRight.remove(pickedRight);
                translateMap.put(pickedLeftP, pickedRightP);
            }
            if (remainingRight.size() == 0) {
                // restart
                if (allowAltFormes) {
                    remainingRight.addAll(noLegendaries ? noLegendaryListInclFormes : mainPokemonListInclFormes);
                    remainingRight.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
                } else {
                    remainingRight.addAll(noLegendaries ? noLegendaryList : mainPokemonList);
                }
                remainingRight.removeAll(banned);
            }
        }

        // Map remaining to themselves just in case
        List<Pokemon> allPokes = allPokemonInclFormesWithoutNull();
        for (Pokemon poke : allPokes) {
            if (!translateMap.containsKey(poke)) {
                translateMap.put(poke, poke);
            }
        }

        List<EncounterSet> currentEncounters = this.getEncounters(useTimeOfDay);

        for (EncounterSet area : currentEncounters) {
            for (Encounter enc : area.encounters) {
                // Apply the map
                enc.pokemon = translateMap.get(enc.pokemon);
                if (area.bannedPokemon.contains(enc.pokemon)) {
                    // Ignore the map and put a random non-banned poke
                    List<Pokemon> tempPickable;
                    if (allowAltFormes) {
                        tempPickable = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes)
                                : new ArrayList<>(mainPokemonListInclFormes);
                        tempPickable.removeIf(o -> ((Pokemon) o).actuallyCosmetic);
                    } else {
                        tempPickable = noLegendaries ? new ArrayList<>(noLegendaryList)
                                : new ArrayList<>(mainPokemonList);
                    }
                    tempPickable.removeAll(banned);
                    tempPickable.removeAll(area.bannedPokemon);
                    if (tempPickable.size() == 0) {
                        throw new RandomizationException("ERROR: Couldn't replace a wild Pokemon!");
                    }
                    if (usePowerLevels) {
                        enc.pokemon = pickWildPowerLvlReplacement(tempPickable, enc.pokemon, false, null, 100);
                    } else {
                        int picked = this.random.nextInt(tempPickable.size());
                        enc.pokemon = tempPickable.get(picked);
                    }
                }
                setFormeForEncounter(enc, enc.pokemon);
            }
        }
        if (levelModifier != 0) {
            for (EncounterSet area : currentEncounters) {
                for (Encounter enc : area.encounters) {
                    enc.level = Math.min(100, (int) Math.round(enc.level * (1 + levelModifier / 100.0)));
                    enc.maxLevel = Math.min(100, (int) Math.round(enc.maxLevel * (1 + levelModifier / 100.0)));
                }
            }
        }

        setEncounters(useTimeOfDay, currentEncounters);

    }

    @Override
    public void onlyChangeWildLevels(Settings settings) {
        int levelModifier = settings.getWildLevelModifier();

        List<EncounterSet> currentEncounters = this.getEncounters(true);

        if (levelModifier != 0) {
            for (EncounterSet area : currentEncounters) {
                for (Encounter enc : area.encounters) {
                    enc.level = Math.min(100, (int) Math.round(enc.level * (1 + levelModifier / 100.0)));
                    enc.maxLevel = Math.min(100, (int) Math.round(enc.maxLevel * (1 + levelModifier / 100.0)));
                }
            }
            setEncounters(true, currentEncounters);
        }
    }

    private void enhanceRandomEncountersORAS(List<EncounterSet> collapsedEncounters, Settings settings) {
        boolean catchEmAll = settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.CATCH_EM_ALL;
        boolean typeThemed = settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.TYPE_THEME_AREAS;
        boolean usePowerLevels = settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.SIMILAR_STRENGTH;
        boolean noLegendaries = settings.isBlockWildLegendaries();
        boolean allowAltFormes = settings.isAllowWildAltFormes();
        boolean banIrregularAltFormes = settings.isBanIrregularAltFormes();
        boolean abilitiesAreRandomized = settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE;

        List<Pokemon> banned = this.bannedForWildEncounters();
        if (!abilitiesAreRandomized) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        if (banIrregularAltFormes) {
            banned.addAll(getIrregularFormes());
        }
        Map<Integer, List<EncounterSet>> zonesToEncounters = mapZonesToEncounters(collapsedEncounters);
        Map<Type, List<Pokemon>> cachedPokeLists = new TreeMap<>();
        for (List<EncounterSet> encountersInZone : zonesToEncounters.values()) {
            int currentAreaIndex = -1;
            List<EncounterSet> nonRockSmashAreas = new ArrayList<>();
            Map<Integer, List<Integer>> areasAndEncountersToRandomize = new TreeMap<>();
            // Since Rock Smash Pokemon do not show up on DexNav, they can be fully randomized
            for (EncounterSet area : encountersInZone) {
                if (area.displayName.contains("Rock Smash")) {
                    // Assume EITHER catch em all OR type themed OR match strength for now
                    if (catchEmAll) {
                        for (Encounter enc : area.encounters) {
                            boolean shouldRandomize = doesAnotherEncounterWithSamePokemonExistInArea(enc, area);
                            if (shouldRandomize) {
                                enc.pokemon = pickEntirelyRandomPokemon(allowAltFormes, noLegendaries, area, banned);
                                setFormeForEncounter(enc, enc.pokemon);
                            }
                        }
                    } else if (typeThemed) {
                        List<Pokemon> possiblePokemon = null;
                        int iterLoops = 0;
                        while (possiblePokemon == null && iterLoops < 10000) {
                            Type areaTheme = randomType();
                            if (!cachedPokeLists.containsKey(areaTheme)) {
                                List<Pokemon> pType = allowAltFormes ? pokemonOfTypeInclFormes(areaTheme, noLegendaries) :
                                        pokemonOfType(areaTheme, noLegendaries);
                                pType.removeAll(banned);
                                cachedPokeLists.put(areaTheme, pType);
                            }
                            possiblePokemon = cachedPokeLists.get(areaTheme);
                            if (area.bannedPokemon.size() > 0) {
                                possiblePokemon = new ArrayList<>(possiblePokemon);
                                possiblePokemon.removeAll(area.bannedPokemon);
                            }
                            if (possiblePokemon.size() == 0) {
                                // Can't use this type for this area
                                possiblePokemon = null;
                            }
                            iterLoops++;
                        }
                        if (possiblePokemon == null) {
                            throw new RandomizationException("Could not randomize an area in a reasonable amount of attempts.");
                        }
                        for (Encounter enc : area.encounters) {
                            // Pick a random themed pokemon
                            enc.pokemon = possiblePokemon.get(this.random.nextInt(possiblePokemon.size()));
                            while (enc.pokemon.actuallyCosmetic) {
                                enc.pokemon = possiblePokemon.get(this.random.nextInt(possiblePokemon.size()));
                            }
                            setFormeForEncounter(enc, enc.pokemon);
                        }
                    } else if (usePowerLevels) {
                        List<Pokemon> allowedPokes;
                        if (allowAltFormes) {
                            allowedPokes = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes)
                                    : new ArrayList<>(mainPokemonListInclFormes);
                        } else {
                            allowedPokes = noLegendaries ? new ArrayList<>(noLegendaryList)
                                    : new ArrayList<>(mainPokemonList);
                        }
                        allowedPokes.removeAll(banned);
                        List<Pokemon> localAllowed = allowedPokes;
                        if (area.bannedPokemon.size() > 0) {
                            localAllowed = new ArrayList<>(allowedPokes);
                            localAllowed.removeAll(area.bannedPokemon);
                        }
                        for (Encounter enc : area.encounters) {
                            enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, 100);
                            while (enc.pokemon.actuallyCosmetic) {
                                enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, 100);
                            }
                            setFormeForEncounter(enc, enc.pokemon);
                        }
                    } else {
                        // Entirely random
                        for (Encounter enc : area.encounters) {
                            enc.pokemon = pickEntirelyRandomPokemon(allowAltFormes, noLegendaries, area, banned);
                            setFormeForEncounter(enc, enc.pokemon);
                        }
                    }
                } else {
                    currentAreaIndex++;
                    nonRockSmashAreas.add(area);
                    List<Integer> encounterIndices = new ArrayList<>();
                    for (int i = 0; i < area.encounters.size(); i++) {
                        encounterIndices.add(i);
                    }
                    areasAndEncountersToRandomize.put(currentAreaIndex, encounterIndices);
                }
            }

            // Now, randomize non-Rock Smash Pokemon until we hit the threshold for DexNav
            int crashThreshold = computeDexNavCrashThreshold(encountersInZone);
            while (crashThreshold < 18 && areasAndEncountersToRandomize.size() > 0) {
                Set<Integer> areaIndices = areasAndEncountersToRandomize.keySet();
                int areaIndex = areaIndices.stream().skip(this.random.nextInt(areaIndices.size())).findFirst().orElse(-1);
                List<Integer> encounterIndices = areasAndEncountersToRandomize.get(areaIndex);
                int indexInListOfEncounterIndices = this.random.nextInt(encounterIndices.size());
                int randomEncounterIndex = encounterIndices.get(indexInListOfEncounterIndices);
                EncounterSet area = nonRockSmashAreas.get(areaIndex);
                Encounter enc = area.encounters.get(randomEncounterIndex);
                // Assume EITHER catch em all OR type themed OR match strength for now
                if (catchEmAll) {
                    boolean shouldRandomize = doesAnotherEncounterWithSamePokemonExistInArea(enc, area);
                    if (shouldRandomize) {
                        enc.pokemon = pickEntirelyRandomPokemon(allowAltFormes, noLegendaries, area, banned);
                        setFormeForEncounter(enc, enc.pokemon);
                    }
                } else if (typeThemed) {
                    List<Pokemon> possiblePokemon = null;
                    Type areaTheme = getTypeForArea(area);
                    if (!cachedPokeLists.containsKey(areaTheme)) {
                        List<Pokemon> pType = allowAltFormes ? pokemonOfTypeInclFormes(areaTheme, noLegendaries) :
                                pokemonOfType(areaTheme, noLegendaries);
                        pType.removeAll(banned);
                        cachedPokeLists.put(areaTheme, pType);
                    }
                    possiblePokemon = cachedPokeLists.get(areaTheme);
                    if (area.bannedPokemon.size() > 0) {
                        possiblePokemon = new ArrayList<>(possiblePokemon);
                        possiblePokemon.removeAll(area.bannedPokemon);
                    }
                    if (possiblePokemon.size() == 0) {
                        // Can't use this type for this area
                        throw new RandomizationException("Could not find a possible Pokemon of the correct type.");
                    }
                    // Pick a random themed pokemon
                    enc.pokemon = possiblePokemon.get(this.random.nextInt(possiblePokemon.size()));
                    while (enc.pokemon.actuallyCosmetic) {
                        enc.pokemon = possiblePokemon.get(this.random.nextInt(possiblePokemon.size()));
                    }
                    setFormeForEncounter(enc, enc.pokemon);
                } else if (usePowerLevels) {
                    List<Pokemon> allowedPokes;
                    if (allowAltFormes) {
                        allowedPokes = noLegendaries ? new ArrayList<>(noLegendaryListInclFormes)
                                : new ArrayList<>(mainPokemonListInclFormes);
                    } else {
                        allowedPokes = noLegendaries ? new ArrayList<>(noLegendaryList)
                                : new ArrayList<>(mainPokemonList);
                    }
                    allowedPokes.removeAll(banned);
                    List<Pokemon> localAllowed = allowedPokes;
                    if (area.bannedPokemon.size() > 0) {
                        localAllowed = new ArrayList<>(allowedPokes);
                        localAllowed.removeAll(area.bannedPokemon);
                    }
                    enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, 100);
                    while (enc.pokemon.actuallyCosmetic) {
                        enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null, 100);
                    }
                    setFormeForEncounter(enc, enc.pokemon);
                } else {
                    // Entirely random
                    enc.pokemon = pickEntirelyRandomPokemon(allowAltFormes, noLegendaries, area, banned);
                    setFormeForEncounter(enc, enc.pokemon);
                }
                crashThreshold = computeDexNavCrashThreshold(encountersInZone);
                encounterIndices.remove(indexInListOfEncounterIndices);
                if (encounterIndices.size() == 0) {
                    areasAndEncountersToRandomize.remove(areaIndex);
                }
            }
        }
    }

    private Type getTypeForArea(EncounterSet area) {
        Pokemon firstPokemon = area.encounters.get(0).pokemon;
        if (area.encounters.get(0).formeNumber != 0) {
            firstPokemon = getAltFormeOfPokemon(firstPokemon, area.encounters.get(0).formeNumber);
        }
        Type primaryType = firstPokemon.primaryType;
        int primaryCount = 1;
        Type secondaryType = null;
        int secondaryCount = 0;
        if (firstPokemon.secondaryType != null) {
            secondaryType = firstPokemon.secondaryType;
            secondaryCount = 1;
        }
        for (int i = 1; i < area.encounters.size(); i++) {
            Pokemon pokemon = area.encounters.get(i).pokemon;
            if (area.encounters.get(i).formeNumber != 0) {
                pokemon = getAltFormeOfPokemon(pokemon, area.encounters.get(i).formeNumber);
            }
            if (pokemon.primaryType == primaryType || pokemon.secondaryType == primaryType) {
                primaryCount++;
            }
            if (pokemon.primaryType == secondaryType || pokemon.secondaryType == secondaryType) {
                secondaryCount++;
            }
        }
        return primaryCount > secondaryCount ? primaryType : secondaryType;
    }

    private boolean doesAnotherEncounterWithSamePokemonExistInArea(Encounter enc, EncounterSet area) {
        for (Encounter encounterToCheck : area.encounters) {
            if (enc != encounterToCheck && enc.pokemon == encounterToCheck.pokemon) {
                return true;
            }
        }
        return false;
    }

    private List<EncounterSet> collapseAreasORAS(List<EncounterSet> currentEncounters) {
        List<EncounterSet> output = new ArrayList<>();
        Map<Integer, List<EncounterSet>> zonesToEncounters = mapZonesToEncounters(currentEncounters);
        for (Integer zone : zonesToEncounters.keySet()) {
            List<EncounterSet> encountersInZone = zonesToEncounters.get(zone);
            int crashThreshold = computeDexNavCrashThreshold(encountersInZone);
            if (crashThreshold <= 18) {
                output.addAll(encountersInZone);
                continue;
            }

            // Naive Area 1-to-1 randomization will crash the game, so let's start collapsing areas to prevent this.
            // Start with combining all the fishing rod encounters, since it's a little less noticeable when they've
            // been collapsed.
            List<EncounterSet> collapsedEncounters = new ArrayList<>(encountersInZone);
            EncounterSet rodGroup = new EncounterSet();
            rodGroup.offset = zone;
            rodGroup.displayName = "Rod Group";
            for (EncounterSet area : encountersInZone) {
                if (area.displayName.contains("Old Rod") || area.displayName.contains("Good Rod") || area.displayName.contains("Super Rod")) {
                    collapsedEncounters.remove(area);
                    rodGroup.encounters.addAll(area.encounters);
                }
            }
            if (rodGroup.encounters.size() > 0) {
                collapsedEncounters.add(rodGroup);
            }
            crashThreshold = computeDexNavCrashThreshold(collapsedEncounters);
            if (crashThreshold <= 18) {
                output.addAll(collapsedEncounters);
                continue;
            }

            // Even after combining all the fishing rod encounters, we're still not below the threshold to prevent
            // DexNav from crashing the game. Combine all the grass encounters now to drop us below the threshold;
            // we've combined everything that DexNav normally combines, so at this point, we're *guaranteed* not
            // to crash the game.
            EncounterSet grassGroup = new EncounterSet();
            grassGroup.offset = zone;
            grassGroup.displayName = "Grass Group";
            for (EncounterSet area : encountersInZone) {
                if (area.displayName.contains("Grass/Cave") || area.displayName.contains("Long Grass") || area.displayName.contains("Horde")) {
                    collapsedEncounters.remove(area);
                    grassGroup.encounters.addAll(area.encounters);
                }
            }
            if (grassGroup.encounters.size() > 0) {
                collapsedEncounters.add(grassGroup);
            }

            output.addAll(collapsedEncounters);
        }
        return output;
    }

    private int computeDexNavCrashThreshold(List<EncounterSet> encountersInZone) {
        int crashThreshold = 0;
        for (EncounterSet area : encountersInZone) {
            if (area.displayName.contains("Rock Smash")) {
                continue; // Rock Smash Pokemon don't display on DexNav
            }
            Set<Pokemon> uniquePokemonInArea = new HashSet<>();
            for (Encounter enc : area.encounters) {
                if (enc.pokemon.baseForme != null) { // DexNav treats different forms as one Pokemon
                    uniquePokemonInArea.add(enc.pokemon.baseForme);
                } else {
                    uniquePokemonInArea.add(enc.pokemon);
                }
            }
            crashThreshold += uniquePokemonInArea.size();
        }
        return crashThreshold;
    }

    private void setEvoChainAsIllegal(Pokemon newPK, List<Pokemon> illegalList, boolean willForceEvolve) {
        // set pre-evos as illegal
        setIllegalPreEvos(newPK, illegalList);

        // if the placed Pokemon will be forced fully evolved, set its evolutions as illegal
        if (willForceEvolve) {
            setIllegalEvos(newPK, illegalList);
        }
    }

    private void setIllegalPreEvos(Pokemon pk, List<Pokemon> illegalList) {
        for (Evolution evo : pk.evolutionsTo) {
            pk = evo.from;
            illegalList.add(pk);
            setIllegalPreEvos(pk, illegalList);
        }
    }

    private void setIllegalEvos(Pokemon pk, List<Pokemon> illegalList) {
        for (Evolution evo : pk.evolutionsFrom) {
            pk = evo.to;
            illegalList.add(pk);
            setIllegalEvos(pk, illegalList);
        }
    }

    private List<Pokemon> getFinalEvos(Pokemon pk) {
        List<Pokemon> finalEvos = new ArrayList<>();
        traverseEvolutions(pk, finalEvos);
        return finalEvos;
    }

    private void traverseEvolutions(Pokemon pk, List<Pokemon> finalEvos) {
        if (!pk.evolutionsFrom.isEmpty()) {
            for (Evolution evo : pk.evolutionsFrom) {
                pk = evo.to;
                traverseEvolutions(pk, finalEvos);
            }
        } else {
            finalEvos.add(pk);
        }
    }

    private void setFormeForTrainerPokemon(TrainerPokemon tp, Pokemon pk) {
        boolean checkCosmetics = true;
        tp.formeSuffix = "";
        tp.forme = 0;
        if (pk.formeNumber > 0) {
            tp.forme = pk.formeNumber;
            tp.formeSuffix = pk.formeSuffix;
            tp.pokemon = pk.baseForme;
            checkCosmetics = false;
        }
        if (checkCosmetics && tp.pokemon.cosmeticForms > 0) {
            tp.forme = tp.pokemon.getCosmeticFormNumber(this.random.nextInt(tp.pokemon.cosmeticForms));
        } else if (!checkCosmetics && pk.cosmeticForms > 0) {
            tp.forme += pk.getCosmeticFormNumber(this.random.nextInt(pk.cosmeticForms));
        }
    }

    private void applyLevelModifierToTrainerPokemon(Trainer trainer, int levelModifier) {
        if (levelModifier != 0) {
            for (TrainerPokemon tp : trainer.pokemon) {
                tp.level = Math.min(100, (int) Math.round(tp.level * (1 + levelModifier / 100.0)));
            }
        }
    }

    @Override
    public void randomizeTrainerPokes(Settings settings) {
        boolean usePowerLevels = settings.isTrainersUsePokemonOfSimilarStrength();
        boolean weightByFrequency = settings.isTrainersMatchTypingDistribution();
        boolean noLegendaries = settings.isTrainersBlockLegendaries();
        boolean noEarlyWonderGuard = settings.isTrainersBlockEarlyWonderGuard();
        int levelModifier = settings.isTrainersLevelModified() ? settings.getTrainersLevelModifier() : 0;
        boolean isTypeThemed = settings.getTrainersMod() == Settings.TrainersMod.TYPE_THEMED;
        boolean isTypeThemedEliteFourGymOnly = settings.getTrainersMod() == Settings.TrainersMod.TYPE_THEMED_ELITE4_GYMS;
        boolean distributionSetting = settings.getTrainersMod() == Settings.TrainersMod.DISTRIBUTED;
        boolean mainPlaythroughSetting = settings.getTrainersMod() == Settings.TrainersMod.MAINPLAYTHROUGH;
        boolean includeFormes = settings.isAllowTrainerAlternateFormes();
        boolean banIrregularAltFormes = settings.isBanIrregularAltFormes();
        boolean swapMegaEvos = settings.isSwapTrainerMegaEvos();
        boolean shinyChance = settings.isShinyChance();
        boolean abilitiesAreRandomized = settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE;
        int eliteFourUniquePokemonNumber = settings.getEliteFourUniquePokemonNumber();
        boolean forceFullyEvolved = settings.isTrainersForceFullyEvolved();
        int forceFullyEvolvedLevel = settings.getTrainersForceFullyEvolvedLevel();
        boolean forceChallengeMode = (settings.getCurrentMiscTweaks() & MiscTweak.FORCE_CHALLENGE_MODE.getValue()) > 0;
        boolean rivalCarriesStarter = settings.isRivalCarriesStarterThroughout();
        boolean allSmart = (settings.getCurrentMiscTweaks() & MiscTweak.NPC_SMART_AI.getValue()) > 0;

        checkPokemonRestrictions();

        // Set up Pokemon pool
        cachedReplacementLists = new TreeMap<>();
        cachedAllList = noLegendaries ? new ArrayList<>(noLegendaryList) : new ArrayList<>(
                mainPokemonList);
        if (includeFormes) {
            if (noLegendaries) {
                cachedAllList.addAll(noLegendaryAltsList);
            } else {
                cachedAllList.addAll(altFormesList);
            }
        }
        cachedAllList =
                cachedAllList
                        .stream()
                        .filter(pk -> !pk.actuallyCosmetic)
                        .collect(Collectors.toList());

        List<Pokemon> banned = this.getBannedFormesForTrainerPokemon();
        if (!abilitiesAreRandomized) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        if (banIrregularAltFormes) {
            banned.addAll(getIrregularFormes());
        }
        cachedAllList.removeAll(banned);

        List<Trainer> currentTrainers = this.getTrainers();

        // Type Themed related
        Map<Trainer, Type> trainerTypes = new TreeMap<>();
        Set<Type> usedUberTypes = new TreeSet<>();
        if (isTypeThemed || isTypeThemedEliteFourGymOnly) {
            typeWeightings = new TreeMap<>();
            totalTypeWeighting = 0;
            // Construct groupings for types
            // Anything starting with GYM or ELITE or CHAMPION is a group
            Map<String, List<Trainer>> groups = new TreeMap<>();
            for (Trainer t : currentTrainers) {
                if (t.tag != null && t.tag.equals("IRIVAL")) {
                    // This is the first rival in Yellow. His Pokemon is used to determine the non-player
                    // starter, so we can't change it here. Just skip it.
                    continue;
                }
                String group = t.tag == null ? "" : t.tag;
                if (group.contains("-")) {
                    group = group.substring(0, group.indexOf('-'));
                }
                if (group.startsWith("GYM") || group.startsWith("ELITE") ||
                        ((group.startsWith("CHAMPION") || group.startsWith("THEMED")) && !isTypeThemedEliteFourGymOnly)) {
                    // Yep this is a group
                    if (!groups.containsKey(group)) {
                        groups.put(group, new ArrayList<>());
                    }
                    groups.get(group).add(t);
                } else if (group.startsWith("GIO")) {
                    // Giovanni has same grouping as his gym, gym 8
                    if (!groups.containsKey("GYM8")) {
                        groups.put("GYM8", new ArrayList<>());
                    }
                    groups.get("GYM8").add(t);
                }
            }

            // Give a type to each group
            // Gym & elite types have to be unique
            // So do uber types, including the type we pick for champion
            Set<Type> usedGymTypes = new TreeSet<>();
            Set<Type> usedEliteTypes = new TreeSet<>();
            for (String group : groups.keySet()) {
                List<Trainer> trainersInGroup = groups.get(group);
                // Shuffle ordering within group to promote randomness
                Collections.shuffle(trainersInGroup, random);
                Type typeForGroup = pickType(weightByFrequency, noLegendaries, includeFormes);
                if (group.startsWith("GYM")) {
                    while (usedGymTypes.contains(typeForGroup)) {
                        typeForGroup = pickType(weightByFrequency, noLegendaries, includeFormes);
                    }
                    usedGymTypes.add(typeForGroup);
                }
                if (group.startsWith("ELITE")) {
                    while (usedEliteTypes.contains(typeForGroup)) {
                        typeForGroup = pickType(weightByFrequency, noLegendaries, includeFormes);
                    }
                    usedEliteTypes.add(typeForGroup);
                }
                if (group.equals("CHAMPION")) {
                    usedUberTypes.add(typeForGroup);
                }

                for (Trainer t : trainersInGroup) {
                    trainerTypes.put(t, typeForGroup);
                }
            }
        }

        // Randomize the order trainers are randomized in.
        // Leads to less predictable results for various modifiers.
        // Need to keep the original ordering around for saving though.
        List<Trainer> scrambledTrainers = new ArrayList<>(currentTrainers);
        Collections.shuffle(scrambledTrainers, this.random);

        // Elite Four Unique Pokemon related
        boolean eliteFourUniquePokemon = eliteFourUniquePokemonNumber > 0;
        List<Pokemon> illegalIfEvolvedList = new ArrayList<>();
        List<Pokemon> bannedFromUniqueList = new ArrayList<>();
        boolean illegalEvoChains = false;
        List<Integer> eliteFourIndices = getEliteFourTrainers(forceChallengeMode);
        if (eliteFourUniquePokemon) {
            // Sort Elite Four Trainers to the start of the list
            scrambledTrainers.sort((t1, t2) ->
                    Boolean.compare(eliteFourIndices.contains(currentTrainers.indexOf(t2) + 1), eliteFourIndices.contains(currentTrainers.indexOf(t1) + 1)));
            illegalEvoChains = forceFullyEvolved;
            if (rivalCarriesStarter) {
                List<Pokemon> starterList = getStarters().subList(0, 3);
                for (Pokemon starter : starterList) {
                    // If rival/friend carries starter, the starters cannot be set as unique
                    bannedFromUniqueList.add(starter);
                    setEvoChainAsIllegal(starter, bannedFromUniqueList, true);

                    // If the final boss is a rival/friend, the fully evolved starters will be unique
                    if (hasRivalFinalBattle()) {
                        cachedAllList.removeAll(getFinalEvos(starter));
                        if (illegalEvoChains) {
                            illegalIfEvolvedList.add(starter);
                            setEvoChainAsIllegal(starter, illegalIfEvolvedList, true);
                        }
                    }
                }
            }
        }

        List<Integer> mainPlaythroughTrainers = getMainPlaythroughTrainers();

        // Randomize Trainer Pokemon
        // The result after this is done will not be final if "Force Fully Evolved" or "Rival Carries Starter"
        // are used, as they are applied later
        for (Trainer t : scrambledTrainers) {
            applyLevelModifierToTrainerPokemon(t, levelModifier);
            if (t.tag != null && t.tag.equals("IRIVAL")) {
                // This is the first rival in Yellow. His Pokemon is used to determine the non-player
                // starter, so we can't change it here. Just skip it.
                continue;
            }

            // If type themed, give a type to each unassigned trainer
            Type typeForTrainer = trainerTypes.get(t);
            if (typeForTrainer == null && isTypeThemed) {
                typeForTrainer = pickType(weightByFrequency, noLegendaries, includeFormes);
                // Ubers: can't have the same type as each other
                if (t.tag != null && t.tag.equals("UBER")) {
                    while (usedUberTypes.contains(typeForTrainer)) {
                        typeForTrainer = pickType(weightByFrequency, noLegendaries, includeFormes);
                    }
                    usedUberTypes.add(typeForTrainer);
                }
            }

            List<Pokemon> evolvesIntoTheWrongType = new ArrayList<>();
            if (typeForTrainer != null) {
                List<Pokemon> pokemonOfType = includeFormes ? pokemonOfTypeInclFormes(typeForTrainer, noLegendaries) :
                        pokemonOfType(typeForTrainer, noLegendaries);
                for (Pokemon pk : pokemonOfType) {
                    if (!pokemonOfType.contains(fullyEvolve(pk, t.index))) {
                        evolvesIntoTheWrongType.add(pk);
                    }
                }
            }

            List<TrainerPokemon> trainerPokemonList = new ArrayList<>(t.pokemon);

            // Elite Four Unique Pokemon related
            boolean eliteFourTrackPokemon = false;
            boolean eliteFourRival = false;
            if (eliteFourUniquePokemon && eliteFourIndices.contains(t.index)) {
                eliteFourTrackPokemon = true;

                // Sort Pokemon list back to front, and then put highest level Pokemon first
                // (Only while randomizing, does not affect order in game)
                Collections.reverse(trainerPokemonList);
                trainerPokemonList.sort((tp1, tp2) -> Integer.compare(tp2.level, tp1.level));
                if (rivalCarriesStarter && (t.tag.contains("RIVAL") || t.tag.contains("FRIEND"))) {
                    eliteFourRival = true;
                }
            }

            for (TrainerPokemon tp : trainerPokemonList) {
                boolean swapThisMegaEvo = swapMegaEvos && tp.canMegaEvolve();
                boolean wgAllowed = (!noEarlyWonderGuard) || tp.level >= 20;
                boolean eliteFourSetUniquePokemon =
                        eliteFourTrackPokemon && eliteFourUniquePokemonNumber > trainerPokemonList.indexOf(tp);
                boolean willForceEvolve = forceFullyEvolved && tp.level >= forceFullyEvolvedLevel;

                Pokemon oldPK = tp.pokemon;
                if (tp.forme > 0) {
                    oldPK = getAltFormeOfPokemon(oldPK, tp.forme);
                }

                bannedList = new ArrayList<>();
                bannedList.addAll(usedAsUniqueList);
                if (illegalEvoChains && willForceEvolve) {
                    bannedList.addAll(illegalIfEvolvedList);
                }
                if (eliteFourSetUniquePokemon) {
                    bannedList.addAll(bannedFromUniqueList);
                }
                if (willForceEvolve) {
                    bannedList.addAll(evolvesIntoTheWrongType);
                }

                Pokemon newPK = pickTrainerPokeReplacement(
                        oldPK,
                        usePowerLevels,
                        typeForTrainer,
                        noLegendaries,
                        wgAllowed,
                        distributionSetting || (mainPlaythroughSetting && mainPlaythroughTrainers.contains(t.index)),
                        swapThisMegaEvo,
                        abilitiesAreRandomized,
                        includeFormes,
                        banIrregularAltFormes
                );

                // Chosen Pokemon is locked in past here
                if (distributionSetting || (mainPlaythroughSetting && mainPlaythroughTrainers.contains(t.index))) {
                    setPlacementHistory(newPK);
                }
                tp.pokemon = newPK;

                setFormeForTrainerPokemon(tp, newPK);
                tp.abilitySlot = getRandomAbilitySlot(newPK);
                tp.resetMoves = true;

                if (!eliteFourRival) {
                    if (eliteFourSetUniquePokemon) {
                        List<Pokemon> actualPKList;
                        if (willForceEvolve) {
                            actualPKList = getFinalEvos(newPK);
                        } else {
                            actualPKList = new ArrayList<>();
                            actualPKList.add(newPK);
                        }
                        // If the unique Pokemon will evolve, we have to set all its potential evolutions as unique
                        for (Pokemon actualPK : actualPKList) {
                            usedAsUniqueList.add(actualPK);
                            if (illegalEvoChains) {
                                setEvoChainAsIllegal(actualPK, illegalIfEvolvedList, willForceEvolve);
                            }
                        }
                    }
                    if (eliteFourTrackPokemon) {
                        bannedFromUniqueList.add(newPK);
                        if (illegalEvoChains) {
                            setEvoChainAsIllegal(newPK, bannedFromUniqueList, willForceEvolve);
                        }
                    }
                } else {
                    // If the champion is a rival, the first Pokemon will be skipped - it's already
                    // set as unique since it's a starter
                    eliteFourRival = false;
                }

                if (swapThisMegaEvo) {
                    tp.heldItem = newPK
                            .megaEvolutionsFrom
                            .get(this.random.nextInt(newPK.megaEvolutionsFrom.size()))
                            .argument;
                }

                if (shinyChance) {
                    if (this.random.nextInt(256) == 0) {
                        tp.IVs |= (1 << 30);
                    }
                }
            }
        }

        // Save it all up
        this.setTrainers(currentTrainers, false, allSmart);
    }

    @Override
    public void randomizeTrainerHeldItems(Settings settings) {
        boolean giveToBossPokemon = settings.isRandomizeHeldItemsForBossTrainerPokemon();
        boolean giveToImportantPokemon = settings.isRandomizeHeldItemsForImportantTrainerPokemon();
        boolean giveToRegularPokemon = settings.isRandomizeHeldItemsForRegularTrainerPokemon();
        boolean highestLevelOnly = settings.isHighestLevelGetsItemsForTrainers();
        boolean customTypeChart = (settings.getCurrentMiscTweaks() & MiscTweak.CUSTOM_TYPE_EFFECTIVENESS.getValue()) > 0;
        boolean allSmart = (settings.getCurrentMiscTweaks() & MiscTweak.NPC_SMART_AI.getValue()) > 0;

        Map<Weather, Type> weatherBoostTypes = new HashMap<>();
        weatherBoostTypes.put(Weather.Sun, Type.FIRE);
        weatherBoostTypes.put(Weather.Rain, Type.WATER);

        Map<Weather, Type> weatherWeakTypes = new HashMap<>();
        weatherBoostTypes.put(Weather.Sun, Type.WATER);
        weatherBoostTypes.put(Weather.Rain, Type.FIRE);

        Map<Weather, Set<Type>> weatherImmuneTypes = new HashMap<>();
        weatherImmuneTypes.put(Weather.Hail, Set.of(Type.ICE, Type.FIGHTING, Type.STEEL));
        weatherImmuneTypes.put(Weather.Sand, Set.of(Type.GROUND, Type.ROCK, Type.STEEL));

        Set<Integer> sunBoostMoves = new HashSet<>();
        Set<Integer> rainBoostMoves = new HashSet<>();
        Set<Integer> hailBoostMoves = new HashSet<>();
        Set<Integer> sandBoostMoves = new HashSet<>();

        Set<Integer> sunWeakMoves = new HashSet<>();
        Set<Integer> rainWeakMoves = new HashSet<>();
        Set<Integer> hailWeakMoves = new HashSet<>();
        Set<Integer> sandWeakMoves = new HashSet<>();

        for (Move move : getMoves()) {
            // Sun
            if (move.type == Type.FIRE || move.effect == MoveEffect.WEATHER_BALL
                    || move.effect == MoveEffect.SOLAR_BEAM || move.effect == MoveEffect.GROWTH || move.effect == MoveEffect.RECOVER_HP_50_WEATHER)
                sunBoostMoves.add(move.number);

            if (move.type == Type.WATER
                    || move.effect == MoveEffect.THUNDER || move.effect == MoveEffect.HURRICANE)
                sunWeakMoves.add(move.number);

            // Rain
            if (move.type == Type.WATER || move.effect == MoveEffect.WEATHER_BALL
                    || move.effect == MoveEffect.THUNDER || move.effect == MoveEffect.HURRICANE)
                rainBoostMoves.add(move.number);

            if (move.type == Type.FIRE
                    || move.effect == MoveEffect.SOLAR_BEAM || move.effect == MoveEffect.GROWTH || move.effect == MoveEffect.RECOVER_HP_50_WEATHER)
                rainWeakMoves.add(move.number);

            // Hail
            if (move.effect == MoveEffect.WEATHER_BALL || move.effect == MoveEffect.BLIZZARD || move.number == Moves.auroraVeil)
                hailBoostMoves.add(move.number);

            if (move.effect == MoveEffect.SOLAR_BEAM || move.effect == MoveEffect.GROWTH || move.effect == MoveEffect.RECOVER_HP_50_WEATHER)
                hailWeakMoves.add(move.number);

            // Sand
            if (move.effect == MoveEffect.WEATHER_BALL)
                sandBoostMoves.add(move.number);

            if (move.effect == MoveEffect.SOLAR_BEAM || move.effect == MoveEffect.GROWTH || move.effect == MoveEffect.RECOVER_HP_50_WEATHER)
                sandWeakMoves.add(move.number);
        }

        Map<Weather, Set<Integer>> weatherBoostMoves = new HashMap<>();
        weatherBoostMoves.put(Weather.Sun, sunBoostMoves);
        weatherBoostMoves.put(Weather.Rain, rainBoostMoves);
        weatherBoostMoves.put(Weather.Hail, hailBoostMoves);
        weatherBoostMoves.put(Weather.Sand, sandBoostMoves);

        Map<Weather, Set<Integer>> weatherWeakMoves = new HashMap<>();
        weatherWeakMoves.put(Weather.Sun, sunWeakMoves);
        weatherWeakMoves.put(Weather.Rain, rainWeakMoves);
        weatherWeakMoves.put(Weather.Hail, hailWeakMoves);
        weatherWeakMoves.put(Weather.Sand, sandWeakMoves);

        Map<Weather, Set<Integer>> weatherBoostAbilities = new HashMap<>();
        weatherBoostAbilities.put(Weather.Sun, Set.of(
                Abilities.chlorophyll, // #034
                Abilities.forecast, // #059
                Abilities.solarPower, // #094
                Abilities.leafGuard, // #102
                Abilities.flowerGift // #122
        ));
        weatherBoostAbilities.put(Weather.Rain, Set.of(
                Abilities.swiftSwim, // #033
                Abilities.rainDish, // #044
                Abilities.forecast, // #059
                Abilities.drySkin, // #087
                Abilities.hydration // #093
        ));
        weatherBoostAbilities.put(Weather.Hail, Set.of(
                Abilities.forecast, // #059
                Abilities.snowCloak, // #081
                Abilities.iceBody, // #115
                Abilities.slushRush // #202
        ));
        weatherBoostAbilities.put(Weather.Sand, Set.of(
                Abilities.sandVeil, // #008
                Abilities.sandRush, // #146
                Abilities.sandForce // #159
        ));

        Map<Weather, Set<Integer>> weatherWeakAbilities = new HashMap<>();
        weatherWeakAbilities.put(Weather.Sun, Set.of(
                Abilities.drizzle, // #002
                Abilities.sandStream, // #045
                Abilities.drySkin, // #087
                Abilities.snowWarning // #117
        ));
        weatherWeakAbilities.put(Weather.Rain, Set.of(
                Abilities.sandStream, // #045
                Abilities.drought, // #070
                Abilities.snowWarning // #117
        ));
        weatherWeakAbilities.put(Weather.Hail, Set.of(
                Abilities.drizzle, // #117
                Abilities.sandStream, // #045
                Abilities.drought // #070
        ));
        weatherWeakAbilities.put(Weather.Sand, Set.of(
                Abilities.drizzle, // #117
                Abilities.drought, // #070
                Abilities.snowWarning // #117
        ));

        List<Move> moves = this.getMoves();
        Map<Integer, List<MoveLearnt>> movesets = this.getMovesLearnt();
        List<Trainer> currentTrainers = this.getTrainers();
        for (Trainer t : currentTrainers) {
            if (trainerShouldNotGetBuffs(t)) {
                continue;
            }
            if (!giveToRegularPokemon && (!t.isImportant() && !t.isBoss())) {
                continue;
            }
            if (!giveToImportantPokemon && t.isImportant()) {
                continue;
            }
            if (!giveToBossPokemon && t.isBoss()) {
                continue;
            }

            Map<Weather, Double> baseWeatherFrequencies = new HashMap<>();

            t.setPokemonHaveItems(true);
            if (highestLevelOnly) {
                int maxLevel = -1;
                TrainerPokemon highestLevelPoke = null;
                for (TrainerPokemon tp : t.pokemon) {
                    if (tp.level > maxLevel) {
                        highestLevelPoke = tp;
                        maxLevel = tp.level;
                    }
                }
                if (highestLevelPoke == null) {
                    continue; // should never happen - trainer had zero pokes
                }
                int[] moveset = highestLevelPoke.resetMoves ?
                        RomFunctions.getMovesAtLevel(getAltFormeOfPokemon(
                                        highestLevelPoke.pokemon, highestLevelPoke.forme).number,
                                movesets,
                                highestLevelPoke.level) :
                        highestLevelPoke.moves;
                randomizeHeldItem(highestLevelPoke, settings, moves, moveset, baseWeatherFrequencies);
            } else {
                for (Weather w : Weather.values()) {
                    baseWeatherFrequencies.put(w, 0.0);
                }

                for (TrainerPokemon tp : t.pokemon) {

                    Pokemon forme = getAltFormeOfPokemon(tp.pokemon, tp.forme);

                    if (tp.resetMoves) {
                        tp.moves = RomFunctions.getMovesAtLevel(forme.number, movesets, tp.level);
                    }
                    int[] moveset = tp.moves;

                    Map<Type, Effectiveness> against = Effectiveness.against(forme.primaryType, forme.secondaryType, generationOfPokemon(), true, customTypeChart, typeInGame(Type.FAIRY));
                    if (against == null)
                        against = new HashMap<>();

                    for (Weather w : Weather.values()) {
                        double frequency = baseWeatherFrequencies.get(w);

                        Type weatherBoostType = weatherBoostTypes.get(w);
                        switch (against.getOrDefault(weatherBoostType, Effectiveness.NEUTRAL)) {
                            case DOUBLE -> frequency -= 1.5;
                            case QUADRUPLE -> frequency -= 2.0;
                        }

                        Type weatherWeakType = weatherBoostTypes.get(w);
                        switch (against.getOrDefault(weatherWeakType, Effectiveness.NEUTRAL)) {
                            case DOUBLE -> frequency += 1.5;
                            case QUADRUPLE -> frequency += 2.0;
                        }

                        if (weatherImmuneTypes.containsKey(w) && forme.primaryType != null) {
                            if (weatherImmuneTypes.get(w).contains(forme.primaryType) || (forme.secondaryType != null && weatherImmuneTypes.get(w).contains(forme.secondaryType)))
                                frequency += 0.25;
                            else
                                frequency -= 0.25;
                        }

                        if (tp.abilitySlot != 0 && weatherBoostAbilities.getOrDefault(w, Set.of()).contains(forme.getAbilityBySlot(tp.abilitySlot)))
                            frequency += 1.0;
                        if (tp.abilitySlot != 0 && weatherWeakAbilities.getOrDefault(w, Set.of()).contains(forme.getAbilityBySlot(tp.abilitySlot)))
                            frequency -= 1.0;

                        for (int m : moveset) {
                            if (weatherBoostMoves.getOrDefault(w, Set.of()).contains(m))
                                frequency += 0.5;
                            if (weatherWeakMoves.getOrDefault(w, Set.of()).contains(m))
                                frequency -= 0.5;
                        }

                        baseWeatherFrequencies.put(w, frequency);
                    }
                }

                for (Weather w : Weather.values()) {
                    double frequency = baseWeatherFrequencies.getOrDefault(w, 0.0);
                    baseWeatherFrequencies.put(w, Math.max(0.0, frequency / t.pokemon.size()));
                }

                for (TrainerPokemon tp : t.pokemon) {
                    if (tp.resetMoves) {
                        Pokemon altForme = getAltFormeOfPokemon(tp.pokemon, tp.forme);
                        tp.moves = RomFunctions.getMovesAtLevel(altForme.number, movesets, tp.level);
                    }
                    int[] moveset = tp.moves;

                    do {
                        randomizeHeldItem(tp, settings, moves, moveset, baseWeatherFrequencies);
                    } while (t.requiresUniqueHeldItems && !t.pokemonHaveUniqueHeldItems());
                }
            }
        }
        this.setTrainers(currentTrainers, false, allSmart);
    }

    private void randomizeHeldItem(TrainerPokemon tp, Settings settings, List<Move> moves, int[] moveset, Map<RomHandler.Weather, Double> weatherFrequencies) {
        boolean sensibleItemsOnly = settings.isSensibleItemsOnlyForTrainers();
        boolean consumableItemsOnly = settings.isConsumableItemsOnlyForTrainers();
        boolean swapMegaEvolutions = settings.isSwapTrainerMegaEvos();
        if (tp.hasZCrystal) {
            return; // Don't overwrite existing Z Crystals.
        }
        if (tp.hasMegaStone && swapMegaEvolutions) {
            return; // Don't overwrite mega stones if another setting handled that.
        }
        List<Integer> toChooseFrom;
        if (sensibleItemsOnly) {
            toChooseFrom = getSensibleHeldItemsFor(tp, settings, consumableItemsOnly, moves, moveset, weatherFrequencies);
        } else if (consumableItemsOnly) {
            toChooseFrom = getAllConsumableHeldItems();
        } else {
            toChooseFrom = getAllHeldItems();
        }

        tp.heldItem = toChooseFrom.get(random.nextInt(toChooseFrom.size()));
    }

    @Override
    public void rivalCarriesStarter(Settings settings) {
        boolean smart = (settings.getCurrentMiscTweaks() & MiscTweak.NPC_SMART_AI.getValue()) > 0;

        checkPokemonRestrictions();
        List<Trainer> currentTrainers = this.getTrainers();
        rivalCarriesStarterUpdate(currentTrainers, "RIVAL", isORAS ? 0 : 1);
        rivalCarriesStarterUpdate(currentTrainers, "FRIEND", 2);
        this.setTrainers(currentTrainers, false, smart);
    }

    @Override
    public boolean hasRivalFinalBattle() {
        return false;
    }

    @Override
    public void forceFullyEvolvedTrainerPokes(Settings settings) {
        int minLevel = settings.getTrainersForceFullyEvolvedLevel();
        boolean allSmart = (settings.getCurrentMiscTweaks() & MiscTweak.NPC_SMART_AI.getValue()) > 0;

        checkPokemonRestrictions();
        List<Trainer> currentTrainers = this.getTrainers();
        for (Trainer t : currentTrainers) {
            for (TrainerPokemon tp : t.pokemon) {
                if (tp.level >= minLevel) {
                    Pokemon newPokemon = fullyEvolve(tp.pokemon, t.index);
                    if (newPokemon != tp.pokemon) {
                        tp.pokemon = newPokemon;
                        setFormeForTrainerPokemon(tp, newPokemon);
                        tp.abilitySlot = getValidAbilitySlotFromOriginal(newPokemon, tp.abilitySlot);
                        tp.resetMoves = true;
                    }
                }
            }
        }
        this.setTrainers(currentTrainers, false, allSmart);
    }

    @Override
    public void onlyChangeTrainerLevels(Settings settings) {
        int levelModifier = settings.getTrainersLevelModifier();
        boolean allSmart = (settings.getCurrentMiscTweaks() & MiscTweak.NPC_SMART_AI.getValue()) != 0;

        List<Trainer> currentTrainers = this.getTrainers();
        for (Trainer t : currentTrainers) {
            applyLevelModifierToTrainerPokemon(t, levelModifier);
        }
        this.setTrainers(currentTrainers, false, allSmart);
    }

    @Override
    public void addTrainerPokemon(Settings settings) {
        int additionalNormal = settings.getAdditionalRegularTrainerPokemon();
        int additionalImportant = settings.getAdditionalImportantTrainerPokemon();
        int additionalBoss = settings.getAdditionalBossTrainerPokemon();
        boolean allSmart = (settings.getCurrentMiscTweaks() & MiscTweak.NPC_SMART_AI.getValue()) > 0;

        List<Trainer> currentTrainers = this.getTrainers();

        // TODO Move this?
        // Lock difficulty of trainers
        for (Trainer t : currentTrainers) {
//            int minIV = t.isBoss() || t.isImportant() ? 6 : 0;
//            int maxIV = t.isBoss() || t.isImportant() ? 18 : 12;
//            int minStrength = t.isBoss() || t.isImportant() ? 50 : 0;
//            int maxStrength = t.isBoss() || t.isImportant() ? 150 : 100;
//            for (TrainerPokemon tpk : t.pokemon) {
//                tpk.IVs = Math.max(minIV, Math.min(tpk.IVs, maxIV));
//                tpk.strength = Math.max(minStrength, Math.min(tpk.strength, maxStrength));
//            }
        }

        // Fix Tate & Liza
        if (isORAS) {
            int additional = additionalBoss == 1 ? 1 : 2;

            Trainer tr = currentTrainers.get(551);
            for (TrainerPokemon tpk : tr.pokemon) {
                tpk.level = 44;
                tpk.strength = 160;
                tpk.IVs = 19;
            }

            int oldNum = tr.pokemon.size();
            for (int i = 0; i < additional; ++i) {
                TrainerPokemon tpk = tr.pokemon.get(i + i % oldNum).copy();
                tpk.level = 42;
                tr.pokemon.add(i, tpk);
            }
        }

        for (Trainer t : currentTrainers) {
            int additional;
            if (t.isBoss()) {
                additional = additionalBoss;
            } else if (t.isImportant()) {
                if (t.skipImportant()) continue;
                additional = additionalImportant;
            } else {
                additional = additionalNormal;
            }

            if (additional == 0) {
                continue;
            }

            int lowest = 100;
            List<TrainerPokemon> potentialPokes = new ArrayList<>();

            // First pass: find lowest level
            for (TrainerPokemon tpk : t.pokemon) {
                if (tpk.level < lowest) {
                    lowest = tpk.level;
                }
            }

            // Second pass: find all Pokemon at lowest level
            for (TrainerPokemon tpk : t.pokemon) {
                if (tpk.level == lowest) {
                    potentialPokes.add(tpk);
                }
            }

            // If a trainer can appear in a Multi Battle (i.e., a Double Battle where the enemy consists
            // of two independent trainers), we want to be aware of that so we don't give them a team of
            // six Pokemon and have a 6v12 battle
            int maxPokemon = t.multiBattleStatus != Trainer.MultiBattleStatus.NEVER ? 3 : 6;
            for (int i = 0; i < additional; i++) {
                if (t.pokemon.size() >= maxPokemon) break;

                // We want to preserve the original last Pokemon because the order is sometimes used to
                // determine the rival's starter
                int secondToLastIndex = t.pokemon.size() - 1;
                TrainerPokemon newPokemon = potentialPokes.get(i % potentialPokes.size()).copy();

                // Clear out the held item because we only want one Pokemon with a mega stone if we're
                // swapping mega evolvables
                newPokemon.heldItem = 0;
                t.pokemon.add(secondToLastIndex, newPokemon);
            }
        }
        this.setTrainers(currentTrainers, false, allSmart);
    }

    @Override
    public void doubleBattleMode(Settings settings) {
        boolean allSmart = (settings.getCurrentMiscTweaks() & MiscTweak.NPC_SMART_AI.getValue()) > 0;

        List<Trainer> currentTrainers = this.getTrainers();
        for (Trainer t : currentTrainers) {
            if (t.pokemon.size() != 1 || t.multiBattleStatus == Trainer.MultiBattleStatus.ALWAYS || this.trainerShouldNotGetBuffs(t)) {
                continue;
            }
            t.pokemon.add(t.pokemon.get(0).copy());
        }
        this.setTrainers(currentTrainers, true, allSmart);
    }

    private Map<Integer, List<MoveLearnt>> allLevelUpMoves;
    private Map<Integer, List<Integer>> allEggMoves;
    private Map<Pokemon, boolean[]> allTMCompat, allTutorCompat;
    private List<Integer> allTMMoves, allTutorMoves;

    @Override
    public List<Move> getMoveSelectionPoolAtLevel(TrainerPokemon tp, boolean cyclicEvolutions) {

        List<Move> moves = getMoves();
        double eggMoveProbability = 0.1;
        double preEvoMoveProbability = 0.5;
        double tmMoveProbability = 0.6;
        double tutorMoveProbability = 0.6;

        if (allLevelUpMoves == null) {
            allLevelUpMoves = getMovesLearnt();
        }

        if (allEggMoves == null) {
            allEggMoves = getEggMoves();
        }

        if (allTMCompat == null) {
            allTMCompat = getTMHMCompatibility();
        }

        if (allTMMoves == null) {
            allTMMoves = getTMMoves();
        }

        if (allTutorCompat == null && hasMoveTutors()) {
            allTutorCompat = getMoveTutorCompatibility();
        }

        if (allTutorMoves == null) {
            allTutorMoves = getMoveTutorMoves();
        }

        // Level-up Moves
        List<Move> moveSelectionPoolAtLevel = allLevelUpMoves.get(getAltFormeOfPokemon(tp.pokemon, tp.forme).number)
                .stream()
                .filter(ml -> (ml.level <= tp.level && ml.level != 0) || (ml.level == 0 && tp.level >= 30))
                .map(ml -> moves.get(ml.move))
                .distinct()
                .collect(Collectors.toList());

        // Pre-Evo Moves
        if (!cyclicEvolutions) {
            Pokemon preEvo;
            if (altFormesCanHaveDifferentEvolutions()) {
                preEvo = getAltFormeOfPokemon(tp.pokemon, tp.forme);
            } else {
                preEvo = tp.pokemon;
            }
            while (!preEvo.evolutionsTo.isEmpty()) {
                preEvo = preEvo.evolutionsTo.get(0).from;
                moveSelectionPoolAtLevel.addAll(allLevelUpMoves.get(preEvo.number)
                        .stream()
                        .filter(ml -> ml.level <= tp.level)
                        .filter(ml -> this.random.nextDouble() < preEvoMoveProbability)
                        .map(ml -> moves.get(ml.move))
                        .distinct()
                        .collect(Collectors.toList()));
            }
        }

        // TM Moves
        boolean[] tmCompat = allTMCompat.get(getAltFormeOfPokemon(tp.pokemon, tp.forme));
        for (int tmMove : allTMMoves) {
            if (tmCompat[allTMMoves.indexOf(tmMove) + 1]) {
                Move thisMove = moves.get(tmMove);
                if (thisMove.power > 1 && tp.level * 3 > thisMove.power * thisMove.getHitCount(generationOfPokemon()) &&
                        this.random.nextDouble() < tmMoveProbability) {
                    moveSelectionPoolAtLevel.add(thisMove);
                } else if ((thisMove.power <= 1 && this.random.nextInt(100) < tp.level) ||
                        this.random.nextInt(200) < tp.level) {
                    moveSelectionPoolAtLevel.add(thisMove);
                }
            }
        }

        // Move Tutor Moves
        if (hasMoveTutors()) {
            boolean[] tutorCompat = allTutorCompat.get(getAltFormeOfPokemon(tp.pokemon, tp.forme));
            for (int tutorMove : allTutorMoves) {
                if (tutorCompat[allTutorMoves.indexOf(tutorMove) + 1]) {
                    Move thisMove = moves.get(tutorMove);
                    if (thisMove.power > 1 && tp.level * 3 > thisMove.power * thisMove.getHitCount(generationOfPokemon()) &&
                            this.random.nextDouble() < tutorMoveProbability) {
                        moveSelectionPoolAtLevel.add(thisMove);
                    } else if ((thisMove.power <= 1 && this.random.nextInt(100) < tp.level) ||
                            this.random.nextInt(200) < tp.level) {
                        moveSelectionPoolAtLevel.add(thisMove);
                    }
                }
            }
        }

        // Egg Moves
        if (!cyclicEvolutions) {
            Pokemon firstEvo;
            if (altFormesCanHaveDifferentEvolutions()) {
                firstEvo = getAltFormeOfPokemon(tp.pokemon, tp.forme);
            } else {
                firstEvo = tp.pokemon;
            }
            while (!firstEvo.evolutionsTo.isEmpty()) {
                firstEvo = firstEvo.evolutionsTo.get(0).from;
            }
            if (allEggMoves.get(firstEvo.number) != null) {
                moveSelectionPoolAtLevel.addAll(allEggMoves.get(firstEvo.number)
                        .stream()
                        .filter(egm -> this.random.nextDouble() < eggMoveProbability)
                        .map(moves::get)
                        .collect(Collectors.toList()));
            }
        }


        return moveSelectionPoolAtLevel.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public void pickTrainerMovesets(Settings settings) {
        boolean isCyclicEvolutions = settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM_EVERY_LEVEL;
        boolean doubleBattleMode = settings.isDoubleBattleMode();
        boolean allSmart = (settings.getCurrentMiscTweaks() & MiscTweak.NPC_SMART_AI.getValue()) > 0;

        List<Trainer> trainers = getTrainers();

        for (Trainer t : trainers) {
            t.setPokemonHaveCustomMoves(true);

            for (TrainerPokemon tp : t.pokemon) {
                tp.resetMoves = false;

                List<Move> movesAtLevel = getMoveSelectionPoolAtLevel(tp, isCyclicEvolutions);

                movesAtLevel = trimMoveList(tp, movesAtLevel, doubleBattleMode);

                if (movesAtLevel.isEmpty()) {
                    continue;
                }

                double trainerTypeModifier = 1;
                if (t.isImportant()) {
                    trainerTypeModifier = 1.5;
                } else if (t.isBoss()) {
                    trainerTypeModifier = 2;
                }
                double movePoolSizeModifier = movesAtLevel.size() / 10.0;
                double bonusModifier = trainerTypeModifier * movePoolSizeModifier;

                double atkSpatkRatioModifier = 0.75;
                double stabMoveBias = 0.25 * bonusModifier;
                double hardAbilityMoveBias = 1 * bonusModifier;
                double softAbilityMoveBias = 0.5 * bonusModifier;
                double statBias = 0.5 * bonusModifier;
                double softMoveBias = 0.25 * bonusModifier;
                double hardMoveBias = 1 * bonusModifier;
                double softMoveAntiBias = 0.5;

                // Add bias for STAB

                Pokemon pk = getAltFormeOfPokemon(tp.pokemon, tp.forme);

                List<Move> stabMoves = new ArrayList<>(movesAtLevel)
                        .stream()
                        .filter(mv -> mv.type == pk.primaryType && mv.category != MoveCategory.STATUS)
                        .collect(Collectors.toList());
                Collections.shuffle(stabMoves, this.random);

                for (int i = 0; i < stabMoveBias * stabMoves.size(); i++) {
                    int j = i % stabMoves.size();
                    movesAtLevel.add(stabMoves.get(j));
                }

                if (pk.secondaryType != null) {
                    stabMoves = new ArrayList<>(movesAtLevel)
                            .stream()
                            .filter(mv -> mv.type == pk.secondaryType && mv.category != MoveCategory.STATUS)
                            .collect(Collectors.toList());
                    Collections.shuffle(stabMoves, this.random);

                    for (int i = 0; i < stabMoveBias * stabMoves.size(); i++) {
                        int j = i % stabMoves.size();
                        movesAtLevel.add(stabMoves.get(j));
                    }
                }

                // Hard ability/move synergy

                List<Move> abilityMoveSynergyList = MoveSynergy.getHardAbilityMoveSynergy(
                        getAbilityForTrainerPokemon(tp),
                        pk.primaryType,
                        pk.secondaryType,
                        movesAtLevel,
                        generationOfPokemon());
                Collections.shuffle(abilityMoveSynergyList, this.random);
                for (int i = 0; i < hardAbilityMoveBias * abilityMoveSynergyList.size(); i++) {
                    int j = i % abilityMoveSynergyList.size();
                    movesAtLevel.add(abilityMoveSynergyList.get(j));
                }

                // Soft ability/move synergy

                List<Move> softAbilityMoveSynergyList = MoveSynergy.getSoftAbilityMoveSynergy(
                        getAbilityForTrainerPokemon(tp),
                        movesAtLevel,
                        pk.primaryType,
                        pk.secondaryType);

                Collections.shuffle(softAbilityMoveSynergyList, this.random);
                for (int i = 0; i < softAbilityMoveBias * softAbilityMoveSynergyList.size(); i++) {
                    int j = i % softAbilityMoveSynergyList.size();
                    movesAtLevel.add(softAbilityMoveSynergyList.get(j));
                }

                // Soft ability/move anti-synergy

                List<Move> softAbilityMoveAntiSynergyList = MoveSynergy.getSoftAbilityMoveAntiSynergy(
                        getAbilityForTrainerPokemon(tp), movesAtLevel);
                List<Move> withoutSoftAntiSynergy = new ArrayList<>(movesAtLevel);
                for (Move mv : softAbilityMoveAntiSynergyList) {
                    withoutSoftAntiSynergy.remove(mv);
                }
                if (withoutSoftAntiSynergy.size() > 0) {
                    movesAtLevel = withoutSoftAntiSynergy;
                }

                List<Move> distinctMoveList = movesAtLevel.stream().distinct().collect(Collectors.toList());
                int movesLeft = distinctMoveList.size();

                if (movesLeft <= 4) {

                    for (int i = 0; i < 4; i++) {
                        if (i < movesLeft) {
                            tp.moves[i] = distinctMoveList.get(i).number;
                        } else {
                            tp.moves[i] = 0;
                        }
                    }
                    continue;
                }

                // Stat/move synergy

                List<Move> statSynergyList = MoveSynergy.getStatMoveSynergy(pk, movesAtLevel);
                Collections.shuffle(statSynergyList, this.random);
                for (int i = 0; i < statBias * statSynergyList.size(); i++) {
                    int j = i % statSynergyList.size();
                    movesAtLevel.add(statSynergyList.get(j));
                }

                // Stat/move anti-synergy

                List<Move> statAntiSynergyList = MoveSynergy.getStatMoveAntiSynergy(pk, movesAtLevel);
                List<Move> withoutStatAntiSynergy = new ArrayList<>(movesAtLevel);
                for (Move mv : statAntiSynergyList) {
                    withoutStatAntiSynergy.remove(mv);
                }
                if (withoutStatAntiSynergy.size() > 0) {
                    movesAtLevel = withoutStatAntiSynergy;
                }

                distinctMoveList = movesAtLevel.stream().distinct().collect(Collectors.toList());
                movesLeft = distinctMoveList.size();

                if (movesLeft <= 4) {

                    for (int i = 0; i < 4; i++) {
                        if (i < movesLeft) {
                            tp.moves[i] = distinctMoveList.get(i).number;
                        } else {
                            tp.moves[i] = 0;
                        }
                    }
                    continue;
                }

                // Add bias for atk/spatk ratio

                double atkSpatkRatio = (double) pk.attack / (double) pk.spatk;
                switch (getAbilityForTrainerPokemon(tp)) {
                    case Abilities.hugePower:
                    case Abilities.purePower:
                        atkSpatkRatio *= 2;
                        break;
                    case Abilities.hustle:
                    case Abilities.gorillaTactics:
                        atkSpatkRatio *= 1.5;
                        break;
                    case Abilities.moxie:
                        atkSpatkRatio *= 1.1;
                        break;
                    case Abilities.soulHeart:
                        atkSpatkRatio *= 0.9;
                        break;
                }

                List<Move> physicalMoves = new ArrayList<>(movesAtLevel)
                        .stream()
                        .filter(mv -> mv.category == MoveCategory.PHYSICAL)
                        .collect(Collectors.toList());
                List<Move> specialMoves = new ArrayList<>(movesAtLevel)
                        .stream()
                        .filter(mv -> mv.category == MoveCategory.SPECIAL)
                        .collect(Collectors.toList());

                if (atkSpatkRatio < 1 && specialMoves.size() > 0) {
                    atkSpatkRatio = 1 / atkSpatkRatio;
                    double acceptedRatio = atkSpatkRatioModifier * atkSpatkRatio;
                    int additionalMoves = (int) (physicalMoves.size() * acceptedRatio) - specialMoves.size();
                    for (int i = 0; i < additionalMoves; i++) {
                        Move mv = specialMoves.get(this.random.nextInt(specialMoves.size()));
                        movesAtLevel.add(mv);
                    }
                } else if (physicalMoves.size() > 0) {
                    double acceptedRatio = atkSpatkRatioModifier * atkSpatkRatio;
                    int additionalMoves = (int) (specialMoves.size() * acceptedRatio) - physicalMoves.size();
                    for (int i = 0; i < additionalMoves; i++) {
                        Move mv = physicalMoves.get(this.random.nextInt(physicalMoves.size()));
                        movesAtLevel.add(mv);
                    }
                }

                // Pick moves

                List<Move> pickedMoves = new ArrayList<>();

                for (int i = 1; i <= 4; i++) {
                    Move move;
                    List<Move> pickFrom;

                    if (i == 1) {
                        pickFrom = movesAtLevel
                                .stream()
                                .filter(mv -> mv.isGoodDamaging(generationOfPokemon()))
                                .collect(Collectors.toList());
                        if (pickFrom.isEmpty()) {
                            pickFrom = movesAtLevel;
                        }
                    } else {
                        pickFrom = movesAtLevel;
                    }

                    if (i == 4) {
                        List<Move> requiresOtherMove = movesAtLevel
                                .stream()
                                .filter(mv -> GlobalConstants.requiresOtherMove.contains(mv.number))
                                .distinct()
                                .collect(Collectors.toList());

                        for (Move dependentMove : requiresOtherMove) {
                            boolean hasRequiredMove = false;
                            for (Move requiredMove : MoveSynergy.requiresOtherMove(dependentMove, movesAtLevel)) {
                                if (pickedMoves.contains(requiredMove)) {
                                    hasRequiredMove = true;
                                    break;
                                }
                            }
                            if (!hasRequiredMove) {
                                movesAtLevel.removeAll(Collections.singletonList(dependentMove));
                            }
                        }
                    }

                    move = pickFrom.get(this.random.nextInt(pickFrom.size()));
                    pickedMoves.add(move);

                    if (i == 4) {
                        break;
                    }

                    movesAtLevel.removeAll(Collections.singletonList(move));

                    movesAtLevel.removeAll(MoveSynergy.getHardMoveAntiSynergy(move, movesAtLevel));

                    distinctMoveList = movesAtLevel.stream().distinct().collect(Collectors.toList());
                    movesLeft = distinctMoveList.size();

                    if (movesLeft <= (4 - i)) {
                        pickedMoves.addAll(distinctMoveList);
                        break;
                    }

                    List<Move> hardMoveSynergyList = MoveSynergy.getMoveSynergy(
                            move,
                            movesAtLevel);
                    Collections.shuffle(hardMoveSynergyList, this.random);
                    for (int j = 0; j < hardMoveBias * hardMoveSynergyList.size(); j++) {
                        int k = j % hardMoveSynergyList.size();
                        movesAtLevel.add(hardMoveSynergyList.get(k));
                    }

                    boolean customTypeEffectiveness = (settings.getCurrentMiscTweaks() & MiscTweak.CUSTOM_TYPE_EFFECTIVENESS.getValue()) == MiscTweak.CUSTOM_TYPE_EFFECTIVENESS.getValue();
                    boolean addFairy = (settings.getCurrentMiscTweaks() & MiscTweak.CUSTOM_ADD_FAIRY.getValue()) == MiscTweak.CUSTOM_ADD_FAIRY.getValue();
                    List<Move> softMoveSynergyList = MoveSynergy.getSoftMoveSynergy(
                            move,
                            movesAtLevel,
                            generationOfPokemon(),
                            isEffectivenessUpdated(),
                            customTypeEffectiveness,
                            addFairy
                    );
                    Collections.shuffle(softMoveSynergyList, this.random);
                    for (int j = 0; j < softMoveBias * softMoveSynergyList.size(); j++) {
                        int k = j % softMoveSynergyList.size();
                        movesAtLevel.add(softMoveSynergyList.get(k));
                    }

                    List<Move> softMoveAntiSynergyList = MoveSynergy.getSoftMoveAntiSynergy(move, movesAtLevel);
                    Collections.shuffle(softMoveAntiSynergyList, this.random);
                    for (int j = 0; j < softMoveAntiBias * softMoveAntiSynergyList.size(); j++) {
                        distinctMoveList = movesAtLevel.stream().distinct().collect(Collectors.toList());
                        if (distinctMoveList.size() <= (4 - i)) {
                            break;
                        }
                        int k = j % softMoveAntiSynergyList.size();
                        movesAtLevel.remove(softMoveAntiSynergyList.get(k));
                    }

                    distinctMoveList = movesAtLevel.stream().distinct().collect(Collectors.toList());
                    movesLeft = distinctMoveList.size();

                    if (movesLeft <= (4 - i)) {
                        pickedMoves.addAll(distinctMoveList);
                        break;
                    }
                }

                int movesPicked = pickedMoves.size();

                for (int i = 0; i < 4; i++) {
                    if (i < movesPicked) {
                        tp.moves[i] = pickedMoves.get(i).number;
                    } else {
                        tp.moves[i] = 0;
                    }
                }
            }
        }
        setTrainers(trainers, false, allSmart);
    }

    private List<Move> trimMoveList(TrainerPokemon tp, List<Move> movesAtLevel, boolean doubleBattleMode) {
        int movesLeft = movesAtLevel.size();

        if (movesLeft <= 4) {
            for (int i = 0; i < 4; i++) {
                if (i < movesLeft) {
                    tp.moves[i] = movesAtLevel.get(i).number;
                } else {
                    tp.moves[i] = 0;
                }
            }
            return new ArrayList<>();
        }

        movesAtLevel = movesAtLevel
                .stream()
                .filter(mv -> !GlobalConstants.uselessMoves.contains(mv.number) &&
                        (doubleBattleMode || !GlobalConstants.doubleBattleMoves.contains(mv.number)))
                .collect(Collectors.toList());

        movesLeft = movesAtLevel.size();

        if (movesLeft <= 4) {
            for (int i = 0; i < 4; i++) {
                if (i < movesLeft) {
                    tp.moves[i] = movesAtLevel.get(i).number;
                } else {
                    tp.moves[i] = 0;
                }
            }
            return new ArrayList<>();
        }

        List<Move> obsoletedMoves = getObsoleteMoves(movesAtLevel);

        // Remove obsoleted moves

        movesAtLevel.removeAll(obsoletedMoves);

        movesLeft = movesAtLevel.size();

        if (movesLeft <= 4) {
            for (int i = 0; i < 4; i++) {
                if (i < movesLeft) {
                    tp.moves[i] = movesAtLevel.get(i).number;
                } else {
                    tp.moves[i] = 0;
                }
            }
            return new ArrayList<>();
        }

        List<Move> requiresOtherMove = movesAtLevel
                .stream()
                .filter(mv -> GlobalConstants.requiresOtherMove.contains(mv.number))
                .collect(Collectors.toList());

        for (Move dependentMove : requiresOtherMove) {
            if (MoveSynergy.requiresOtherMove(dependentMove, movesAtLevel).isEmpty()) {
                movesAtLevel.remove(dependentMove);
            }
        }

        movesLeft = movesAtLevel.size();

        if (movesLeft <= 4) {
            for (int i = 0; i < 4; i++) {
                if (i < movesLeft) {
                    tp.moves[i] = movesAtLevel.get(i).number;
                } else {
                    tp.moves[i] = 0;
                }
            }
            return new ArrayList<>();
        }

        // Remove hard ability anti-synergy moves

        List<Move> withoutHardAntiSynergy = new ArrayList<>(movesAtLevel);
        withoutHardAntiSynergy.removeAll(MoveSynergy.getHardAbilityMoveAntiSynergy(
                getAbilityForTrainerPokemon(tp),
                movesAtLevel));

        if (withoutHardAntiSynergy.size() > 0) {
            movesAtLevel = withoutHardAntiSynergy;
        }

        movesLeft = movesAtLevel.size();

        if (movesLeft <= 4) {
            for (int i = 0; i < 4; i++) {
                if (i < movesLeft) {
                    tp.moves[i] = movesAtLevel.get(i).number;
                } else {
                    tp.moves[i] = 0;
                }
            }
            return new ArrayList<>();
        }
        return movesAtLevel;
    }

    private List<Move> getObsoleteMoves(List<Move> movesAtLevel) {
        List<Move> obsoletedMoves = new ArrayList<>();
        for (Move mv : movesAtLevel) {
            if (GlobalConstants.cannotObsoleteMoves.contains(mv.number)) {
                continue;
            }
            if (mv.power > 0) {
                List<Move> obsoleteThis = movesAtLevel
                        .stream()
                        .filter(mv2 -> !GlobalConstants.cannotBeObsoletedMoves.contains(mv2.number) &&
                                mv.type == mv2.type &&
                                ((((mv.getStatChangeMoveType() == mv2.getStatChangeMoveType() &&
                                        mv.statChanges[0].equals(mv2.statChanges[0])) ||
                                        (mv2.getStatChangeMoveType() == StatChangeMoveType.NONE_OR_UNKNOWN &&
                                                mv.hasBeneficialStatChange())) &&
                                        mv.getAbsorbPercent() >= mv2.getAbsorbPercent() &&
                                        !mv.isChargeMove &&
                                        !mv.isRechargeMove) ||
                                        mv2.power * mv2.getHitCount(generationOfPokemon()) <= 30) &&
                                mv.accuracy >= mv2.accuracy &&
                                mv.category == mv2.category &&
                                mv.priority >= mv2.priority &&
                                mv2.power > 0 &&
                                mv.power * mv.getHitCount(generationOfPokemon()) > mv2.power * mv2.getHitCount(generationOfPokemon()))
                        .collect(Collectors.toList());
                for (Move obsoleted : obsoleteThis) {
                    //System.out.println(obsoleted.name + " obsoleted by " + mv.name);
                }
                obsoletedMoves.addAll(obsoleteThis);
            } else if (mv.getStatChangeMoveType() == StatChangeMoveType.NO_DAMAGE_USER ||
                    mv.getStatChangeMoveType() == StatChangeMoveType.NO_DAMAGE_TARGET) {
                List<Move> obsoleteThis = new ArrayList<>();
                List<Move.StatChange> statChanges1 = new ArrayList<>();
                for (Move.StatChange sc : mv.statChanges) {
                    if (sc.type != StatChangeType.NONE) {
                        statChanges1.add(sc);
                    }
                }
                for (Move mv2 : movesAtLevel
                        .stream()
                        .filter(otherMv -> !otherMv.equals(mv) &&
                                otherMv.power <= 0 &&
                                otherMv.getStatChangeMoveType() == mv.getStatChangeMoveType() &&
                                (otherMv.statusType == mv.statusType ||
                                        otherMv.statusType == MoveStatusType.NONE))
                        .collect(Collectors.toList())) {
                    List<Move.StatChange> statChanges2 = new ArrayList<>();
                    for (Move.StatChange sc : mv2.statChanges) {
                        if (sc.type != StatChangeType.NONE) {
                            statChanges2.add(sc);
                        }
                    }
                    if (statChanges2.size() > statChanges1.size()) {
                        continue;
                    }
                    List<Move.StatChange> statChanges1Filtered = statChanges1
                            .stream()
                            .filter(sc -> !statChanges2.contains(sc))
                            .collect(Collectors.toList());
                    statChanges2.removeAll(statChanges1);
                    if (!statChanges1Filtered.isEmpty() && statChanges2.isEmpty()) {
                        if (!GlobalConstants.cannotBeObsoletedMoves.contains(mv2.number)) {
                            obsoleteThis.add(mv2);
                        }
                        continue;
                    }
                    if (statChanges1Filtered.isEmpty() && statChanges2.isEmpty()) {
                        continue;
                    }
                    boolean maybeBetter = false;
                    for (Move.StatChange sc1 : statChanges1Filtered) {
                        boolean canStillBeBetter = false;
                        for (Move.StatChange sc2 : statChanges2) {
                            if (sc1.type == sc2.type) {
                                canStillBeBetter = true;
                                if ((mv.getStatChangeMoveType() == StatChangeMoveType.NO_DAMAGE_USER && sc1.stages > sc2.stages) ||
                                        (mv.getStatChangeMoveType() == StatChangeMoveType.NO_DAMAGE_TARGET && sc1.stages < sc2.stages)) {
                                    maybeBetter = true;
                                } else {
                                    canStillBeBetter = false;
                                }
                            }
                        }
                        if (!canStillBeBetter) {
                            maybeBetter = false;
                            break;
                        }
                    }
                    if (maybeBetter) {
                        if (!GlobalConstants.cannotBeObsoletedMoves.contains(mv2.number)) {
                            obsoleteThis.add(mv2);
                        }
                    }
                }
                for (Move obsoleted : obsoleteThis) {
                    //System.out.println(obsoleted.name + " obsoleted by " + mv.name);
                }
                obsoletedMoves.addAll(obsoleteThis);
            }
        }

        return obsoletedMoves.stream().distinct().collect(Collectors.toList());
    }

    private boolean trainerShouldNotGetBuffs(Trainer t) {
        return t.tag != null && (t.tag.startsWith("RIVAL1-") || t.tag.startsWith("FRIEND1-") || t.tag.endsWith("NOTSTRONG"));
    }

    public int getRandomAbilitySlot(Pokemon pokemon) {
        if (abilitiesPerPokemon() == 0) {
            return 0;
        }
        List<Integer> abilitiesList = Arrays.asList(pokemon.ability1, pokemon.ability2, pokemon.ability3);
        int slot = random.nextInt(this.abilitiesPerPokemon());
        while (abilitiesList.get(slot) == 0) {
            slot = random.nextInt(this.abilitiesPerPokemon());
        }
        return slot + 1;
    }

    public int getValidAbilitySlotFromOriginal(Pokemon pokemon, int originalAbilitySlot) {
        // This is used in cases where one Trainer Pokemon evolves into another. If the unevolved Pokemon
        // is using slot 2, but the evolved Pokemon doesn't actually have a second ability, then we
        // want the evolved Pokemon to use slot 1 for safety's sake.
        if (originalAbilitySlot == 2 && pokemon.ability2 == 0) {
            return 1;
        }
        return originalAbilitySlot;
    }

    // MOVE DATA
    // All randomizers don't touch move ID 165 (Struggle)
    // They also have other exclusions where necessary to stop things glitching.

    @Override
    public void randomizeMovePowers() {
        List<Move> moves = this.getMoves();
        for (Move mv : moves) {
            if (mv != null && mv.internalId != Moves.struggle && mv.power >= 10) {
                // "Generic" damaging move to randomize power
                if (random.nextInt(3) != 2) {
                    // "Regular" move
                    mv.power = random.nextInt(11) * 5 + 50; // 50 ... 100
                } else {
                    // "Extreme" move
                    mv.power = random.nextInt(27) * 5 + 20; // 20 ... 150
                }
                // Tiny chance for massive power jumps
                for (int i = 0; i < 2; i++) {
                    if (random.nextInt(100) == 0) {
                        mv.power += 50;
                    }
                }

                if (mv.getHitCount(generationOfPokemon()) != 1) {
                    // Divide randomized power by average hit count, round to
                    // nearest 5
                    mv.power = (int) (Math.round(mv.power / mv.getHitCount(generationOfPokemon()) / 5) * 5);
                    if (mv.power == 0) {
                        mv.power = 5;
                    }
                }
            }
        }
    }

    @Override
    public void randomizeMovePPs() {
        List<Move> moves = this.getMoves();
        for (Move mv : moves) {
            if (mv != null && mv.internalId != Moves.struggle) {
                if (random.nextInt(3) != 2) {
                    // "average" PP: 15-25
                    mv.pp = random.nextInt(3) * 5 + 15;
                } else {
                    // "extreme" PP: 5-40
                    mv.pp = random.nextInt(8) * 5 + 5;
                }
            }
        }
    }

    @Override
    public void randomizeMoveAccuracies() {
        List<Move> moves = this.getMoves();
        for (Move mv : moves) {
            if (mv != null && mv.internalId != Moves.struggle && mv.accuracy >= 5) {
                // "Sane" accuracy randomization
                // Broken into three tiers based on original accuracy
                // Designed to limit the chances of 100% accurate OHKO moves and
                // keep a decent base of 100% accurate regular moves.

                if (mv.accuracy <= 50) {
                    // lowest tier (acc <= 50)
                    // new accuracy = rand(20...50) inclusive
                    // with a 10% chance to increase by 50%
                    mv.accuracy = random.nextInt(7) * 5 + 20;
                    if (random.nextInt(10) == 0) {
                        mv.accuracy = (mv.accuracy * 3 / 2) / 5 * 5;
                    }
                } else if (mv.accuracy < 90) {
                    // middle tier (50 < acc < 90)
                    // count down from 100% to 20% in 5% increments with 20%
                    // chance to "stop" and use the current accuracy at each
                    // increment
                    // gives decent-but-not-100% accuracy most of the time
                    mv.accuracy = 100;
                    while (mv.accuracy > 20) {
                        if (random.nextInt(10) < 2) {
                            break;
                        }
                        mv.accuracy -= 5;
                    }
                } else {
                    // highest tier (90 <= acc <= 100)
                    // count down from 100% to 20% in 5% increments with 40%
                    // chance to "stop" and use the current accuracy at each
                    // increment
                    // gives high accuracy most of the time
                    mv.accuracy = 100;
                    while (mv.accuracy > 20) {
                        if (random.nextInt(10) < 4) {
                            break;
                        }
                        mv.accuracy -= 5;
                    }
                }
            }
        }
    }

    @Override
    public void randomizeMoveTypes() {
        List<Move> moves = this.getMoves();
        for (Move mv : moves) {
            if (mv != null && mv.internalId != Moves.struggle && mv.type != null) {
                mv.type = randomType();
            }
        }
    }

    @Override
    public void randomizeMoveCategory() {
        if (!this.hasPhysicalSpecialSplit()) {
            return;
        }
        List<Move> moves = this.getMoves();
        for (Move mv : moves) {
            if (mv != null && mv.internalId != Moves.struggle && mv.category != MoveCategory.STATUS) {
                if (random.nextInt(2) == 0) {
                    mv.category = (mv.category == MoveCategory.PHYSICAL) ? MoveCategory.SPECIAL : MoveCategory.PHYSICAL;
                }
            }
        }

    }

    @Override
    public void updateMoves(Settings settings) {
        int generation = settings.getUpdateMovesToGeneration();

        List<Move> moves = this.getMoves();

        if (generation >= 2 && generationOfPokemon() < 2) {
            // gen1
            // Karate Chop => FIGHTING (gen1)
            updateMoveType(moves, Moves.karateChop, Type.FIGHTING);
            // Gust => FLYING (gen1)
            updateMoveType(moves, Moves.gust, Type.FLYING);
            // Wing Attack => 60 power (gen1)
            updateMovePower(moves, Moves.wingAttack, 60);
            // Whirlwind => 100 accuracy (gen1)
            updateMoveAccuracy(moves, Moves.whirlwind, 100);
            // Sand Attack => GROUND (gen1)
            updateMoveType(moves, Moves.sandAttack, Type.GROUND);
            // Double-Edge => 120 power (gen1)
            updateMovePower(moves, Moves.doubleEdge, 120);
            // Move 44, Bite, becomes dark (but doesn't exist anyway)
            // Blizzard => 70% accuracy (gen1)
            updateMoveAccuracy(moves, Moves.blizzard, 70);
            // Rock Throw => 90% accuracy (gen1)
            updateMoveAccuracy(moves, Moves.rockThrow, 90);
            // Hypnosis => 60% accuracy (gen1)
            updateMoveAccuracy(moves, Moves.hypnosis, 60);
            // SelfDestruct => 200power (gen1)
            updateMovePower(moves, Moves.selfDestruct, 200);
            // Explosion => 250 power (gen1)
            updateMovePower(moves, Moves.explosion, 250);
            // Dig => 60 power (gen1)
            updateMovePower(moves, Moves.dig, 60);
        }

        if (generation >= 3 && generationOfPokemon() < 3) {
            // Razor Wind => 100% accuracy (gen1/2)
            updateMoveAccuracy(moves, Moves.razorWind, 100);
            // Move 67, Low Kick, has weight-based power in gen3+
            // Low Kick => 100% accuracy (gen1)
            updateMoveAccuracy(moves, Moves.lowKick, 100);
        }

        if (generation >= 4 && generationOfPokemon() < 4) {
            // Fly => 90 power (gen1/2/3)
            updateMovePower(moves, Moves.fly, 90);
            // Vine Whip => 15 pp (gen1/2/3)
            updateMovePP(moves, Moves.vineWhip, 15);
            // Absorb => 25pp (gen1/2/3)
            updateMovePP(moves, Moves.absorb, 25);
            // Mega Drain => 15pp (gen1/2/3)
            updateMovePP(moves, Moves.megaDrain, 15);
            // Dig => 80 power (gen1/2/3)
            updateMovePower(moves, Moves.dig, 80);
            // Recover => 10pp (gen1/2/3)
            updateMovePP(moves, Moves.recover, 10);
            // Flash => 100% acc (gen1/2/3)
            updateMoveAccuracy(moves, Moves.flash, 100);
            // Petal Dance => 90 power (gen1/2/3)
            updateMovePower(moves, Moves.petalDance, 90);
            // Disable => 100% accuracy (gen1-4)
            updateMoveAccuracy(moves, Moves.disable, 80);
            // Jump Kick => 85 power
            updateMovePower(moves, Moves.jumpKick, 85);
            // Hi Jump Kick => 100 power
            updateMovePower(moves, Moves.highJumpKick, 100);

            if (generationOfPokemon() >= 2) {
                // Zap Cannon => 120 power (gen2-3)
                updateMovePower(moves, Moves.zapCannon, 120);
                // Outrage => 120 power (gen2-3)
                updateMovePower(moves, Moves.outrage, 120);
                updateMovePP(moves, Moves.outrage, 10);
                // Giga Drain => 10pp (gen2-3)
                updateMovePP(moves, Moves.gigaDrain, 10);
                // Rock Smash => 40 power (gen2-3)
                updateMovePower(moves, Moves.rockSmash, 40);
            }

            if (generationOfPokemon() == 3) {
                // Stockpile => 20 pp
                updateMovePP(moves, Moves.stockpile, 20);
                // Dive => 80 power
                updateMovePower(moves, Moves.dive, 80);
                // Leaf Blade => 90 power
                updateMovePower(moves, Moves.leafBlade, 90);
            }
        }

        if (generation >= 5 && generationOfPokemon() < 5) {
            // Bind => 85% accuracy (gen1-4)
            updateMoveAccuracy(moves, Moves.bind, 85);
            // Jump Kick => 10 pp, 100 power (gen1-4)
            updateMovePP(moves, Moves.jumpKick, 10);
            updateMovePower(moves, Moves.jumpKick, 100);
            // Tackle => 50 power, 100% accuracy , gen1-4
            updateMovePower(moves, Moves.tackle, 50);
            updateMoveAccuracy(moves, Moves.tackle, 100);
            // Wrap => 90% accuracy (gen1-4)
            updateMoveAccuracy(moves, Moves.wrap, 90);
            // Thrash => 120 power, 10pp (gen1-4)
            updateMovePP(moves, Moves.thrash, 10);
            updateMovePower(moves, Moves.thrash, 120);
            // Disable => 100% accuracy (gen1-4)
            updateMoveAccuracy(moves, Moves.disable, 100);
            // Petal Dance => 120power, 10pp (gen1-4)
            updateMovePP(moves, Moves.petalDance, 10);
            updateMovePower(moves, Moves.petalDance, 120);
            // Fire Spin => 35 power, 85% acc (gen1-4)
            updateMoveAccuracy(moves, Moves.fireSpin, 85);
            updateMovePower(moves, Moves.fireSpin, 35);
            // Toxic => 90% accuracy (gen1-4)
            updateMoveAccuracy(moves, Moves.toxic, 90);
            // Clamp => 15pp, 85% acc (gen1-4)
            updateMoveAccuracy(moves, Moves.clamp, 85);
            updateMovePP(moves, Moves.clamp, 15);
            // HJKick => 130 power, 10pp (gen1-4)
            updateMovePP(moves, Moves.highJumpKick, 10);
            updateMovePower(moves, Moves.highJumpKick, 130);
            // Glare => 90% acc (gen1-4)
            updateMoveAccuracy(moves, Moves.glare, 90);
            // Poison Gas => 80% acc (gen1-4)
            updateMoveAccuracy(moves, Moves.poisonGas, 80);
            // Crabhammer => 90% acc (gen1-4)
            updateMoveAccuracy(moves, Moves.crabhammer, 90);

            if (generationOfPokemon() >= 2) {
                // Curse => GHOST (gen2-4)
                updateMoveType(moves, Moves.curse, Type.GHOST);
                // Cotton Spore => 100% acc (gen2-4)
                updateMoveAccuracy(moves, Moves.cottonSpore, 100);
                // Scary Face => 100% acc (gen2-4)
                updateMoveAccuracy(moves, Moves.scaryFace, 100);
                // Bone Rush => 90% acc (gen2-4)
                updateMoveAccuracy(moves, Moves.boneRush, 90);
                // Giga Drain => 75 power (gen2-4)
                updateMovePower(moves, Moves.gigaDrain, 75);
                // Fury Cutter => 20 power (gen2-4)
                updateMovePower(moves, Moves.furyCutter, 20);
                // Future Sight => 10 pp, 100 power, 100% acc (gen2-4)
                updateMovePP(moves, Moves.futureSight, 10);
                updateMovePower(moves, Moves.futureSight, 100);
                updateMoveAccuracy(moves, Moves.futureSight, 100);
                // Whirlpool => 35 pow, 85% acc (gen2-4)
                updateMovePower(moves, Moves.whirlpool, 35);
                updateMoveAccuracy(moves, Moves.whirlpool, 85);
            }

            if (generationOfPokemon() >= 3) {
                // Uproar => 90 power (gen3-4)
                updateMovePower(moves, Moves.uproar, 90);
                // Sand Tomb => 35 pow, 85% acc (gen3-4)
                updateMovePower(moves, Moves.sandTomb, 35);
                updateMoveAccuracy(moves, Moves.sandTomb, 85);
                // Bullet Seed => 25 power (gen3-4)
                updateMovePower(moves, Moves.bulletSeed, 25);
                // Icicle Spear => 25 power (gen3-4)
                updateMovePower(moves, Moves.icicleSpear, 25);
                // Covet => 60 power (gen3-4)
                updateMovePower(moves, Moves.covet, 60);
                // Rock Blast => 90% acc (gen3-4)
                updateMoveAccuracy(moves, Moves.rockBlast, 90);
                // Doom Desire => 140 pow, 100% acc, gen3-4
                updateMovePower(moves, Moves.doomDesire, 140);
                updateMoveAccuracy(moves, Moves.doomDesire, 100);
            }

            if (generationOfPokemon() == 4) {
                // Feint => 30 pow
                updateMovePower(moves, Moves.feint, 30);
                // Last Resort => 140 pow
                updateMovePower(moves, Moves.lastResort, 140);
                // Drain Punch => 10 pp, 75 pow
                updateMovePP(moves, Moves.drainPunch, 10);
                updateMovePower(moves, Moves.drainPunch, 75);
                // Magma Storm => 75% acc
                updateMoveAccuracy(moves, Moves.magmaStorm, 75);
            }
        }

        if (generation >= 6 && generationOfPokemon() < 6) {
            // gen 1
            // Swords Dance 20 PP
            updateMovePP(moves, Moves.swordsDance, 20);
            // Whirlwind can't miss
            updateMoveAccuracy(moves, Moves.whirlwind, Move.getPerfectAccuracy());
            // Vine Whip 25 PP, 45 Power
            updateMovePP(moves, Moves.vineWhip, 25);
            updateMovePower(moves, Moves.vineWhip, 45);
            // Pin Missile 25 Power, 95% Accuracy
            updateMovePower(moves, Moves.pinMissile, 25);
            updateMoveAccuracy(moves, Moves.pinMissile, 95);
            // Flamethrower 90 Power
            updateMovePower(moves, Moves.flamethrower, 90);
            // Hydro Pump 110 Power
            updateMovePower(moves, Moves.hydroPump, 110);
            // Surf 90 Power
            updateMovePower(moves, Moves.surf, 90);
            // Ice Beam 90 Power
            updateMovePower(moves, Moves.iceBeam, 90);
            // Blizzard 110 Power
            updateMovePower(moves, Moves.blizzard, 110);
            // Growth 20 PP
            updateMovePP(moves, Moves.growth, 20);
            // String Shot Speed -2
            moves.get(Moves.stringShot).effect = MoveEffect.TRGT_SPE_MINUS_2;
            moves.get(Moves.stringShot).statChanges[0].stages = -2;
            if (generationOfPokemon() >= 5)
                moves.get(Moves.stringShot).description = removeLineBreaks(moves.get(Moves.stringShot).description)
                        .replace("reduce", "harshly reduce")
                        .replace("lower", "harshly lower");
            // Thunderbolt 90 Power
            updateMovePower(moves, Moves.thunderbolt, 90);
            // Thunder 110 Power
            updateMovePower(moves, Moves.thunder, 110);
            // Minimize 10 PP
            updateMovePP(moves, Moves.minimize, 10);
            // Barrier 20 PP
            updateMovePP(moves, Moves.barrier, 20);
            // Lick 30 Power
            updateMovePower(moves, Moves.lick, 30);
            // Smog 30 Power
            updateMovePower(moves, Moves.smog, 30);
            // Fire Blast 110 Power
            updateMovePower(moves, Moves.fireBlast, 110);
            // Skull Bash 10 PP, 130 Power
            updateMovePP(moves, Moves.skullBash, 10);
            updateMovePower(moves, Moves.skullBash, 130);
            // Glare 100% Accuracy
            updateMoveAccuracy(moves, Moves.glare, 100);
            // Poison Gas 90% Accuracy
            updateMoveAccuracy(moves, Moves.poisonGas, 90);
            // Bubble 40 Power
            updateMovePower(moves, Moves.bubble, 40);
            // Psywave 100% Accuracy
            updateMoveAccuracy(moves, Moves.psywave, 100);
            // Acid Armor 20 PP
            updateMovePP(moves, Moves.acidArmor, 20);
            // Crabhammer 100 Power
            updateMovePower(moves, Moves.crabhammer, 100);

            if (generationOfPokemon() >= 2) {
                // Thief 25 PP, 60 Power
                updateMovePP(moves, Moves.thief, 25);
                updateMovePower(moves, Moves.thief, 60);
                // Snore 50 Power
                updateMovePower(moves, Moves.snore, 50);
                // Fury Cutter 40 Power
                updateMovePower(moves, Moves.furyCutter, 40);
                // Future Sight 120 Power
                updateMovePower(moves, Moves.futureSight, 120);
            }

            if (generationOfPokemon() >= 3) {
                // Heat Wave 95 Power
                updateMovePower(moves, Moves.heatWave, 95);
                // Will-o-Wisp 85% Accuracy
                updateMoveAccuracy(moves, Moves.willOWisp, 85);
                // Smellingsalt 70 Power
                updateMovePower(moves, Moves.smellingSalts, 70);
                // Knock off 65 Power
                updateMovePower(moves, Moves.knockOff, 65);
                // Poison Fang 50% chance to badly poison
                moves.get(Moves.poisonFang).statusPercentChance = 50;
                // Meteor Mash 90 Power, 90% Accuracy
                updateMovePower(moves, Moves.meteorMash, 90);
                updateMoveAccuracy(moves, Moves.meteorMash, 90);
                // Air Cutter 60 Power
                updateMovePower(moves, Moves.airCutter, 60);
                // Overheat 130 Power
                updateMovePower(moves, Moves.overheat, 130);
                // Rock Tomb 15 PP, 60 Power, 95% Accuracy
                updateMovePP(moves, Moves.rockTomb, 15);
                updateMovePower(moves, Moves.rockTomb, 60);
                updateMoveAccuracy(moves, Moves.rockTomb, 95);
                // Extrasensory 20 PP
                updateMovePP(moves, Moves.extrasensory, 20);
                // Muddy Water 90 Power
                updateMovePower(moves, Moves.muddyWater, 90);
                // Covet 25 PP
                updateMovePP(moves, Moves.covet, 25);
            }

            if (generationOfPokemon() >= 4) {
                // Wake-Up Slap 70 Power
                updateMovePower(moves, Moves.wakeUpSlap, 70);
                // Tailwind 15 PP
                updateMovePP(moves, Moves.tailwind, 15);
                // Assurance 60 Power
                updateMovePower(moves, Moves.assurance, 60);
                // Psycho Shift 100% Accuracy
                updateMoveAccuracy(moves, Moves.psychoShift, 100);
                // Aura Sphere 80 Power
                updateMovePower(moves, Moves.auraSphere, 80);
                // Air Slash 15 PP
                updateMovePP(moves, Moves.airSlash, 15);
                // Dragon Pulse 85 Power
                updateMovePower(moves, Moves.dragonPulse, 85);
                // Power Gem 80 Power
                updateMovePower(moves, Moves.powerGem, 80);
                // Energy Ball 90 Power
                updateMovePower(moves, Moves.energyBall, 90);
                // Draco Meteor 130 Power
                updateMovePower(moves, Moves.dracoMeteor, 130);
                // Leaf Storm 130 Power
                updateMovePower(moves, Moves.leafStorm, 130);
                // Gunk Shot 80% Accuracy
                updateMoveAccuracy(moves, Moves.gunkShot, 80);
                // Chatter 65 Power
                updateMovePower(moves, Moves.chatter, 65);
                // Magma Storm 100 Power
                updateMovePower(moves, Moves.magmaStorm, 100);
            }

            if (generationOfPokemon() == 5) {
                // Synchronoise 120 Power
                updateMovePower(moves, Moves.synchronoise, 120);
                // Low Sweep 65 Power
                updateMovePower(moves, Moves.lowSweep, 65);
                // Hex 65 Power
                updateMovePower(moves, Moves.hex, 65);
                // Incinerate 60 Power
                updateMovePower(moves, Moves.incinerate, 60);
                // Pledges 80 Power
                updateMovePower(moves, Moves.waterPledge, 80);
                updateMovePower(moves, Moves.firePledge, 80);
                updateMovePower(moves, Moves.grassPledge, 80);
                // Struggle Bug 50 Power
                updateMovePower(moves, Moves.struggleBug, 50);
                // Frost Breath and Storm Throw 45 Power
                // Crits are 2x in these games, so we need to multiply BP by 3/4
                // Storm Throw was also updated to have a base BP of 60
                int critMovePower = (settings.getCurrentMiscTweaks() & MiscTweak.MODERNIZE_CRIT.getValue()) == 0 ? 45 : 60;
                updateMovePower(moves, Moves.frostBreath, critMovePower);
                updateMovePower(moves, Moves.stormThrow, critMovePower);
                // Sacred Sword 15 PP
                updateMovePP(moves, Moves.sacredSword, 15);
                // Hurricane 110 Power
                updateMovePower(moves, Moves.hurricane, 110);
                // Techno Blast 120 Power
                updateMovePower(moves, Moves.technoBlast, 120);
            }
        }

        if (generation >= 7 && generationOfPokemon() < 7) {
            // Leech Life 80 Power, 10 PP
            updateMovePower(moves, Moves.leechLife, 80);
            updateMovePP(moves, Moves.leechLife, 10);
            // Submission 20 PP
            updateMovePP(moves, Moves.submission, 20);
            // Tackle 40 Power
            updateMovePower(moves, Moves.tackle, 40);
            // Thunder Wave 90% Accuracy
            updateMoveAccuracy(moves, Moves.thunderWave, 90);

            if (generationOfPokemon() >= 2) {
                // Swagger 85% Accuracy
                updateMoveAccuracy(moves, Moves.swagger, 85);
            }

            if (generationOfPokemon() >= 3) {
                // Knock Off 20 PP
                updateMovePP(moves, Moves.knockOff, 20);
            }

            if (generationOfPokemon() >= 4) {
                // Dark Void 50% Accuracy
                updateMoveAccuracy(moves, Moves.darkVoid, 50);
                // Sucker Punch 70 Power
                updateMovePower(moves, Moves.suckerPunch, 70);
            }

            if (generationOfPokemon() == 6) {
                // Aromatic Mist can't miss
                updateMoveAccuracy(moves, Moves.aromaticMist, Move.getPerfectAccuracy());
                // Fell Stinger 50 Power
                updateMovePower(moves, Moves.fellStinger, 50);
                // Flying Press 100 Power
                updateMovePower(moves, Moves.flyingPress, 100);
                // Mat Block 10 PP
                updateMovePP(moves, Moves.matBlock, 10);
                // Mystical Fire 75 Power
                updateMovePower(moves, Moves.mysticalFire, 75);
                // Parabolic Charge 65 Power
                updateMovePower(moves, Moves.parabolicCharge, 65);
                // Topsy-Turvy can't miss
                updateMoveAccuracy(moves, Moves.topsyTurvy, Move.getPerfectAccuracy());
                // Water Shuriken Special
                updateMoveCategory(moves, Moves.waterShuriken, MoveCategory.SPECIAL);
            }
        }

        if (generation >= 8 && generationOfPokemon() < 8) {
            if (generationOfPokemon() >= 2) {
                // Rapid Spin 50 Power
                updateMovePower(moves, Moves.rapidSpin, 50);
            }

            if (generationOfPokemon() == 7) {
                // Multi-Attack 120 Power
                updateMovePower(moves, Moves.multiAttack, 120);
            }
        }

        if (generation >= 9 && generationOfPokemon() < 9) {
            // Gen 1
            // Recover 5 PP
            updateMovePP(moves, Moves.recover, 5);
            // Soft-Boiled 5 PP
            updateMovePP(moves, Moves.softBoiled, 5);
            // Rest 5 PP
            updateMovePP(moves, Moves.rest, 5);

            if (generationOfPokemon() >= 2) {
                // Milk Drink 5 PP
                updateMovePP(moves, Moves.milkDrink, 5);
            }

            if (generationOfPokemon() >= 3) {
                // Slack Off 5 PP
                updateMovePP(moves, Moves.slackOff, 5);
            }

            if (generationOfPokemon() >= 4) {
                // Roost 5 PP
                updateMovePP(moves, Moves.roost, 5);
            }

            if (generationOfPokemon() >= 7) {
                // Shore Up 5 PP
                updateMovePP(moves, Moves.shoreUp, 5);
            }

            if (generationOfPokemon() >= 8) {
                // Grassy Glide 60 Power
                updateMovePower(moves, Moves.grassyGlide, 60);
                // Wicked Blow 75 Power
                updateMovePower(moves, Moves.wickedBlow, 75);
                // Glacial Lance 120 Power
                updateMovePower(moves, Moves.glacialLance, 120);
            }
        }
    }

    protected void customMoveChanges(Settings settings) {
        boolean doubles = settings.isDoubleBattleMode();

        List<Move> moves = this.getMoves();

        // 003 Double Slap
        if (generationOfPokemon() >= 3)
            moves.get(Moves.doubleSlap).name = "Double Slap";
        if (typeInGame(Type.FAIRY))
            updateMoveType(moves, Moves.doubleSlap, Type.FAIRY);
        updateMoveAccuracy(moves, Moves.doubleSlap, 100);

        // 004 Comet Punch
        if (typeInGame(Type.FAIRY))
            updateMoveType(moves, Moves.cometPunch, Type.FAIRY);
        updateMovePower(moves, Moves.cometPunch, 20);
        updateMoveAccuracy(moves, Moves.cometPunch, 100);

        // 005 Mega Punch
        updateMovePower(moves, Moves.megaPunch, 100);
        updateMoveAccuracy(moves, Moves.megaPunch, 90);

        // 006 Pay Day
        updateMoveType(moves, Moves.payDay, Type.STEEL);
        updateMovePower(moves, Moves.payDay, 60);

        // 011 Vise Grip
        if (generationOfPokemon() <= 8)
            moves.get(Moves.viseGrip).name = "Vise Grip";

//        // 012 Guillotine
//        updateMoveType(moves, Moves.guillotine, Type.STEEL);
//        moves.get(Moves.guillotine).qualities = MoveQualities.DAMAGE;
//        updateMovePower(moves, Moves.guillotine, 100);
//        updateMoveAccuracy(moves, Moves.guillotine, Move.getPerfectAccuracy(MoveCategory.PHYSICAL, generationOfPokemon()));
//        updateMovePP(moves, Moves.guillotine, 10);
//        moves.get(Moves.guillotine).priority = -1;
//        moves.get(Moves.guillotine).effect = MoveEffect.DMG_DCR_PRIORITY;
//        moves.get(Moves.guillotine).description = moves.get(Moves.vitalThrow).description;

//        // 013 Razor Wind
//        moves.get(Moves.razorWind).qualities = MoveQualities.DAMAGE_USER_STAT_CHANGE;
//        updateMovePower(moves, Moves.razorWind, 140);
//        updateMoveAccuracy(moves, Moves.razorWind, 90);
//        updateMovePP(moves, Moves.razorWind, 5);
//        moves.get(Moves.razorWind).criticalChance = generationOfPokemon() < 5 ? CriticalChance.NONE : CriticalChance.NORMAL;
//        moves.get(Moves.razorWind).effect = MoveEffect.DMG_USER_SPA_MINUS_2;
//        moves.get(Moves.razorWind).target = MoveTarget.ANY_ADJACENT;
//        moves.get(Moves.razorWind).statChanges[0].type = StatChangeType.SPECIAL_ATTACK;
//        moves.get(Moves.razorWind).statChanges[0].stages = -2;
//        moves.get(Moves.razorWind).statChanges[0].percentChance = 100;
//        moves.get(Moves.razorWind).isChargeMove = false;
//        moves.get(Moves.razorWind).description = moves.get(Moves.overheat).description;

        // 015 Cut
        updateMoveType(moves, Moves.cut, Type.GRASS);
        updateMovePower(moves, Moves.cut, generationOfPokemon() >= 5 ? moves.get(Moves.stormThrow).power : 45); // May be different due to crit settings
        updateMoveAccuracy(moves, Moves.cut, 100);
        updateMovePP(moves, Moves.cut, 10);
        moves.get(Moves.cut).criticalChance = CriticalChance.GUARANTEED;
        moves.get(Moves.cut).effect = generationOfPokemon() >= 5 ? MoveEffect.DMG_ALWAYS_CRIT : MoveEffect.DMG_INCR_CRIT;
        switch (generationOfPokemon()) {
            case 2:
            case 3:
                moves.get(Moves.cut).description = "Always lands a critical hit.";
                break;
            case 4:
            case 5:
            case 6:
                moves.get(Moves.cut).description = "It always results in a critical hit and can also be used to cut down thin trees.";
                break;
            default:
                moves.get(Moves.cut).description = "The target is cut with scythe or claw. It always results in a critical hit.";
                break;
        }

//        // 017 Tail Whip
//        if (typeInGame(Type.FAIRY))
//            updateMoveType(moves, Moves.tailWhip, Type.FAIRY);

        // 019 Fly
        updateMoveAccuracy(moves, Moves.fly, 100);

        // 027 Rolling Kick
        updateMoveAccuracy(moves, Moves.rollingKick, 95);

        // 028 Sand Attack
        moves.get(Moves.sandAttack).name = "Sand Attack";

        // 031 Fury Attack
        updateMoveType(moves, Moves.furyAttack, Type.DARK);
        updateMoveAccuracy(moves, Moves.furyAttack, 100);

//        // 032 Horn Drill
//        moves.get(Moves.hornDrill).qualities = MoveQualities.DAMAGE_USER_STAT_CHANGE;
//        updateMovePower(moves, Moves.hornDrill, 130);
//        updateMoveAccuracy(moves, Moves.hornDrill, 100);
//        moves.get(Moves.hornDrill).effect = MoveEffect.DMG_USER_DEF_SPD_MINUS_1;
//        moves.get(Moves.hornDrill).statChanges[0] = new Move.StatChange(StatChangeType.DEFENSE, -1, 100.0);
//        moves.get(Moves.hornDrill).statChanges[1] = new Move.StatChange(StatChangeType.SPECIAL_DEFENSE, -1, 100.0);
//        moves.get(Moves.hornDrill).description = "The user stabs the target with a horn that rotates like a drill. It lowers the user's Defense and Sp. Def stats.";

        // 042 Pin Missile
        updateMoveAccuracy(moves, Moves.pinMissile, 100);

        // 049 Sonic Boom
        if (generationOfPokemon() <= 5)
            moves.get(Moves.sonicBoom).name = "Sonic Boom";

        // 051 Acid
        moves.get(Moves.acid).name = "Corrosion";
        updateMovePower(moves, Moves.acid, 30);
        moves.get(Moves.acid).statChanges[0].percentChance = 100.0;
        moves.get(Moves.acid).description = removeLineBreaks(moves.get(Moves.acid).description)
                .replace("may lower", "also lowers")
                .replace("may also lower", "also lowers");

        // 057 Surf
        moves.get(Moves.surf).target = MoveTarget.ALL_ADJACENT_FOES;
        moves.get(Moves.surf).accuracy = 90;

        // 061 Bubble Beam
        if (generationOfPokemon() >= 3)
            moves.get(Moves.bubbleBeam).name = "Bubble Beam";

        // 062 Aurora Beam
        updateMovePower(moves, Moves.auroraBeam, 70);
        moves.get(Moves.auroraBeam).statChanges[0].percentChance = 100.0;
        moves.get(Moves.auroraBeam).description = removeLineBreaks(moves.get(Moves.auroraBeam).description)
                .replace("may lower", "lowers")
                .replace("may also lower", "also lowers");

        // 063 Hyper Beam
        moves.get(Moves.hyperBeam).qualities = MoveQualities.DAMAGE_USER_STAT_CHANGE;
        updateMovePower(moves, Moves.hyperBeam, 130);
        updateMoveAccuracy(moves, Moves.hyperBeam, 100);
        moves.get(Moves.hyperBeam).effect = MoveEffect.DMG_USER_DEF_SPD_MINUS_1;
        moves.get(Moves.hyperBeam).statChanges[0] = new Move.StatChange(StatChangeType.DEFENSE, -1, 100);
        moves.get(Moves.hyperBeam).statChanges[1] = new Move.StatChange(StatChangeType.SPECIAL_DEFENSE, -1, 100);
        moves.get(Moves.hyperBeam).isRechargeMove = false;
        moves.get(Moves.hyperBeam).description = "The target is attacked with a powerful beam. It lowers the user’s Defense and Sp. Def stats.";

        // 065 Drill Peck
        updateMovePower(moves, Moves.drillPeck, 85);

        // 066 Submission
        if (generationOfPokemon() >= 6 || !typeInGame(Type.FAIRY)) {
            updateMovePower(moves, Moves.submission, 70);
            updateMoveAccuracy(moves, Moves.submission, 100);
            moves.get(Moves.submission).qualities = MoveQualities.DAMAGE_TARGET_STAT_CHANGE;
            moves.get(Moves.submission).effect = MoveEffect.DMG_TRGT_ATK_MINUS_1;
            moves.get(Moves.submission).recoil = 0;
            moves.get(Moves.submission).statChanges[0] = new Move.StatChange(StatChangeType.ATTACK, -1, 100);
            moves.get(Moves.submission).description = "The target is thrown using the power of gravity. It also lowers the target's Attack stat.";
        }

        // 070 Strength
        updateMoveType(moves, Moves.strength, Type.ROCK);
        moves.get(Moves.strength).flinchPercentChance = 30.0;
        moves.get(Moves.strength).effect = MoveEffect.DMG_FLINCH;
        moves.get(Moves.strength).description = "The user slugs the target at max power and may make it flinch. It can also be used to move heavy boulders.";

        // 073 Leech Seed
        updateMoveAccuracy(moves, Moves.leechSeed, 100);

        // 075 Razor Leaf
        if (!doubles)
            updateMoveAccuracy(moves, Moves.razorLeaf, 100);

        // 076 Solar Beam
        if (generationOfPokemon() <= 5)
            moves.get(Moves.solarBeam).name = "Solar Beam";

        // 077 Poison Powder
        updateMoveAccuracy(moves, Moves.poisonPowder, 100);

        // 078 Stun Spore
        updateMoveAccuracy(moves, Moves.stunSpore, 100);

//        // 082 Dragon Rage
//        moves.get(Moves.dragonRage).qualities = MoveQualities.DAMAGE_TARGET_STAT_CHANGE;
//        updateMovePower(moves, Moves.dragonRage, 60);
//        updateMovePP(moves, Moves.dragonRage, 15);
//        moves.get(Moves.dragonRage).effect = MoveEffect.DMG_TRGT_ATK_MINUS_1;
//        moves.get(Moves.dragonRage).statChanges[0] = new Move.StatChange(StatChangeType.ATTACK, -1, 100.0);
//        moves.get(Moves.dragonRage).description = "This attack hits the target with a shock wave of pure rage. It also lowers the target's Attack stat.";

        // 083 Fire Spin
        updateMoveAccuracy(moves, Moves.fireSpin, 100);

        // 086 Thunder Wave
        updateMoveAccuracy(moves, Moves.thunderWave, 100);

        // 088 Rock Throw
        updateMovePower(moves, Moves.rockThrow, 55);
        updateMoveAccuracy(moves, Moves.rockThrow, 95);

//        // 090 Fissure
//        moves.get(Moves.fissure).qualities = MoveQualities.DAMAGE_USER_STAT_CHANGE;
//        updateMovePower(moves, Moves.fissure, 120);
//        updateMoveAccuracy(moves, Moves.fissure, 100);
//        moves.get(Moves.fissure).effect = MoveEffect.DMG_USER_ATK_DEF_MINUS_1;
//        moves.get(Moves.fissure).statChanges[0] = new Move.StatChange(StatChangeType.ATTACK, -1, 100.0);
//        moves.get(Moves.fissure).statChanges[1] = new Move.StatChange(StatChangeType.DEFENSE, -1, 100.0);
//        moves.get(Moves.fissure).description = "The user opens up a fissure in the ground and drops the target in. It also lowers the user's Attack and Defense.";

        // 096 Meditate
        updateMovePP(moves, Moves.meditate, 30);
        moves.get(Moves.meditate).effect = MoveEffect.USER_ATK_SPA_PLUS_1;
        moves.get(Moves.meditate).statChanges[1] = new Move.StatChange(StatChangeType.SPECIAL_ATTACK, 1, 0.0);
        moves.get(Moves.meditate).description = "The user meditates to awaken the power deep within its body and raise its Attack and Sp. Atk stats.";

//        // 101 Night Shade
//        moves.get(Moves.nightShade).qualities = MoveQualities.DAMAGE_USER_STAT_CHANGE;
//        updateMovePower(moves, Moves.nightShade, 50);
//        updateMovePP(moves, Moves.nightShade, 10);
//        moves.get(Moves.nightShade).effect = MoveEffect.DMG_USER_SPA_PLUS_1;
//        moves.get(Moves.nightShade).statChanges[0] = new Move.StatChange(StatChangeType.SPECIAL_ATTACK, 1, 100.0);
//        moves.get(Moves.nightShade).description = "The user makes the target see a frightening mirage. It inflicts damage matching the user’s level and raises the user's Sp. Atk stat.";

        // 103 Screech
        updateMoveAccuracy(moves, Moves.screech, 100);

        // 104 Double Team
        updateMovePP(moves, Moves.doubleTeam, 30);
        moves.get(Moves.doubleTeam).effect = MoveEffect.USER_SPE_PLUS_2;
        moves.get(Moves.doubleTeam).statChanges[0] = new Move.StatChange(StatChangeType.SPEED, 1, 0.0);
        moves.get(Moves.doubleTeam).description = removeLineBreaks(moves.get(Moves.doubleTeam).description).replace("evasiveness", "Speed stat");

        // 107 Minimize
        moves.get(Moves.minimize).statChanges[0].stages = 1;

        // 108 Smokescreen
        if (generationOfPokemon() <= 5)
            moves.get(Moves.smokescreen).name = "Smokescreen";

        // 120 Self-Destruct
        if (generationOfPokemon() <= 5)
            moves.get(Moves.selfDestruct).name = "Self-Destruct";

        // 121 Egg Bomb
        updateMoveCategory(moves, Moves.eggBomb, MoveCategory.SPECIAL);
        updateMovePower(moves, Moves.eggBomb, 110);
        updateMoveAccuracy(moves, Moves.eggBomb, 90);
        // TODO: This doesn't work (at least in Gen V) :(
        // moves.get(Moves.eggBomb).effect = MoveEffect.PHYSICAL_DMG;
        // moves.get(Moves.eggBomb).description = "An explosive egg is hurled with maximum force at the foe. This attack does physical damage.";

        // 122 Lick
        moves.get(Moves.lick).statusPercentChance = 100.0;
        moves.get(Moves.lick).description = "The target is licked with a long tongue, causing damage. It will also leave the target with paralysis.";

        // 123 Smog
        updateMoveAccuracy(moves, Moves.smog, 100);
        moves.get(Moves.smog).statusPercentChance = 100.0;
        moves.get(Moves.smog).description = "The target is attacked with a discharge of filthy gases. It also poisons the target.";

        // 124 Sludge
        updateMovePower(moves, Moves.sludge, 70);

        // 125 Bone Club
        updateMoveAccuracy(moves, Moves.boneClub, 100);

        // 128 Clamp
        updateMoveAccuracy(moves, Moves.clamp, 100);

        // 131 Spike Cannon
        updateMoveType(moves, Moves.spikeCannon, Type.STEEL);

        // 134 Kinesis
        moves.get(Moves.kinesis).qualities = MoveQualities.NO_DAMAGE_STATUS;
        updateMoveAccuracy(moves, Moves.kinesis, 100);
        updateMovePP(moves, Moves.kinesis, 10);
        moves.get(Moves.kinesis).statusType = MoveStatusType.CONFUSION;
        moves.get(Moves.kinesis).effect = MoveEffect.NO_DMG_CNF;
        moves.get(Moves.kinesis).description = "The user distracts the target by bending a spoon which causes confusion.";

        // 135 Soft-Boiled
        if (generationOfPokemon() <= 2)
            moves.get(Moves.softBoiled).name = "SoftBoiled";
        else
            moves.get(Moves.softBoiled).name = "Soft-Boiled";

        // 139 Poison Gas
        if (!doubles)
            updateMoveAccuracy(moves, Moves.poisonGas, 100);

        // 140 Barrage
        updateMovePower(moves, Moves.barrage, 25);
        updateMoveAccuracy(moves, Moves.barrage, 95);

        // 151 Acid Armor
        updateMoveType(moves, Moves.acidArmor, Type.WATER);
        moves.get(Moves.acidArmor).name = "Liquefy";

        // 154 Fury Swipes
        updateMovePower(moves, Moves.furySwipes, 20);
        updateMoveAccuracy(moves, Moves.furySwipes, 100);

        if (!doubles)
            updateMoveAccuracy(moves, Moves.rockSlide, 95);

        // 158 Hyper Fang
        moves.get(Moves.hyperFang).flinchPercentChance = 30.0;

        // 162 Super Fang
        updateMoveAccuracy(moves, Moves.superFang, 95);

        // Gen II
        if (generationOfPokemon() < 2)
            return;

        // 167 Triple Kick
        updateMoveAccuracy(moves, Moves.tripleKick, 95);

        // 172 Flame Wheel
        moves.get(Moves.flameWheel).statusPercentChance = 50.0;

        // 185 Feint Attack
        moves.get(Moves.feintAttack).qualities = MoveQualities.DAMAGE_TARGET_STAT_CHANGE;
        updateMovePower(moves, Moves.feintAttack, 65);
        updateMoveAccuracy(moves, Moves.feintAttack, 100);
        updateMovePP(moves, Moves.feintAttack, 15);
        moves.get(Moves.feintAttack).effect = MoveEffect.DMG_TRGT_DEF_MINUS_1;
        moves.get(Moves.feintAttack).statChanges[0] = new Move.StatChange(StatChangeType.DEFENSE, -1, 100.0);
        if (generationOfPokemon() >= 3)
            moves.get(Moves.feintAttack).name = "FeintAttack";
        else
            moves.get(Moves.feintAttack).name = "Feint Attack";
        moves.get(Moves.feintAttack).description = "The user approaches the target disarmingly, then throws a sucker punch. It also lowers the target's Defense stat.";

        // 189 Mud-Slap
        updateMovePower(moves, Moves.mudSlap, 40);
        moves.get(Moves.mudSlap).statChanges[0] = new Move.StatChange(StatChangeType.ATTACK, -2, 100.0);
        moves.get(Moves.mudSlap).description = "The user hurls mud in the target to inflict damage and harshly lower its Attack stat.";

        // 190 Octazooka
        updateMovePower(moves, Moves.octazooka, 75);
        updateMoveAccuracy(moves, Moves.octazooka, 100);
        moves.get(Moves.octazooka).effect = MoveEffect.DMG_TRGT_SPA_MINUS_1;
        moves.get(Moves.octazooka).statChanges[0].type = StatChangeType.SPECIAL_ATTACK;
        moves.get(Moves.octazooka).description = "The user attacks by spraying ink in the target's face or eyes. It also lowers the target's Sp. Atk stat.";

        // 192 Zap Cannon
        moves.get(Moves.zapCannon).qualities = MoveQualities.DAMAGE_USER_STAT_CHANGE;
        updateMovePower(moves, Moves.zapCannon, 130);
        updateMoveAccuracy(moves, Moves.zapCannon, 90);
        moves.get(Moves.zapCannon).statusType = MoveStatusType.NONE;
        moves.get(Moves.zapCannon).statusPercentChance = 0.0;
        moves.get(Moves.zapCannon).effect = MoveEffect.DMG_USER_SPA_MINUS_2;
        moves.get(Moves.zapCannon).statChanges[0] = new Move.StatChange(StatChangeType.SPECIAL_ATTACK, -2, 100.0);
        moves.get(Moves.zapCannon).description = "The user fires an electric blast like a cannon. This harshly reduces the user's Sp. Atk stat.";

        // 194 Destiny Bond
        moves.get(Moves.destinyBond).qualities = MoveQualities.NO_DAMAGE_STAT_CHANGE;
        updateMovePP(moves, Moves.destinyBond, 20);
        moves.get(Moves.destinyBond).effect = MoveEffect.USER_ATK_DEF_ACC_PLUS_1;
        moves.get(Moves.destinyBond).statChanges[0] = new Move.StatChange(StatChangeType.ATTACK, 1, 0.0);
        moves.get(Moves.destinyBond).statChanges[1] = new Move.StatChange(StatChangeType.DEFENSE, 1, 0.0);
        moves.get(Moves.destinyBond).statChanges[2] = new Move.StatChange(StatChangeType.ACCURACY, 1, 0.0);
        moves.get(Moves.destinyBond).isStolenBySnatch = true;
        moves.get(Moves.destinyBond).bypassesSubstitute = false;
        moves.get(Moves.destinyBond).name = "Manifest";
        moves.get(Moves.destinyBond).description = "The user manifests itself in a more physical form. This raises its Attack and Defense stats as well as its accuracy.";

        // 196 Icy Wind
        updateMovePower(moves, Moves.icyWind, 60);

        // 198 Bone Rush
        updateMoveAccuracy(moves, Moves.boneRush, 100);

        // 205 Rollout
        updateMoveAccuracy(moves, Moves.rollout, 95);

        // 210 Fury Cutter
        updateMovePower(moves, Moves.furyCutter, 50);
        updateMoveAccuracy(moves, Moves.furyCutter, 100);
        updateMovePP(moves, Moves.furyCutter, 10);
        moves.get(Moves.furyCutter).qualities = MoveQualities.DAMAGE_USER_STAT_CHANGE;
        moves.get(Moves.furyCutter).effect = MoveEffect.DMG_USER_ATK_PLUS_1;
        moves.get(Moves.furyCutter).statChanges[0].type = StatChangeType.ATTACK;
        moves.get(Moves.furyCutter).statChanges[0].stages = 1;
        moves.get(Moves.furyCutter).statChanges[0].percentChance = 100.0;
        moves.get(Moves.furyCutter).description = "The target is slashed with scythes or claws. It also raises the user's Attack stat.";

        // 211 Steel Wing
        updateMoveAccuracy(moves, Moves.steelWing, 100);
        moves.get(Moves.steelWing).statChanges[0].percentChance = 100.0;
        moves.get(Moves.steelWing).description = "The target is hit with wings of steel. It also raises the user's Defense stat.";

//        // 219 Safeguard
//        if (typeInGame(Type.FAIRY))
//            updateMoveType(moves, Moves.safeguard, Type.FAIRY);

        // 220 Pain Split
        updateMoveType(moves, Moves.painSplit, Type.GHOST);

        // 224 Megahorn
        moves.get(Moves.megahorn).qualities = MoveQualities.DAMAGE_USER_STAT_CHANGE;
        updateMoveAccuracy(moves, Moves.megahorn, 100);
        updateMovePP(moves, Moves.megahorn, 5);
        moves.get(Moves.megahorn).effect = MoveEffect.DMG_USER_DEF_SPD_MINUS_1;
        moves.get(Moves.megahorn).statChanges[0] = new Move.StatChange(StatChangeType.DEFENSE, -1, 100);
        moves.get(Moves.megahorn).statChanges[1] = new Move.StatChange(StatChangeType.SPECIAL_DEFENSE, -1, 100);
        moves.get(Moves.megahorn).description = "Using its tough and impressive horn, the user rams into the target. It lowers the user’s Defense and Sp. Def stats.";

        // 225 Dragon Breath
        updateMovePower(moves, Moves.dragonBreath, 70);

//        // 227 Encore
//        if (typeInGame(Type.FAIRY))
//            updateMoveType(moves, Moves.encore, Type.FAIRY);

        // 231 Iron Tail
        updateMovePower(moves, Moves.ironTail, 65);
        updateMoveAccuracy(moves, Moves.ironTail, 100);
        moves.get(Moves.ironTail).statChanges[0].percentChance = 100.0;
        moves.get(Moves.ironTail).description = "The target is slammed with a steel-hard tail. It also lowers the target's Defense stat.";

        // 232 Metal Claw
        updateMovePower(moves, Moves.metalClaw, 60);
        updateMoveAccuracy(moves, Moves.metalClaw, 100);
        updateMovePP(moves, Moves.metalClaw, 10);
        moves.get(Moves.metalClaw).statChanges[0].percentChance = 100.0;
        moves.get(Moves.metalClaw).description = "The target is raked with steel claws. It also raises the user's Attack stat.";

        // 233 Vital Throw
        updateMovePower(moves, Moves.vitalThrow, 100);

        // 239 Twister
        if (doubles) {
            updateMovePower(moves, Moves.twister, 75);
            updateMoveAccuracy(moves, Moves.twister, 90);
        }
        moves.get(Moves.twister).flinchPercentChance = 30.0;

//        // 243 Mirror Coat
//        updateMoveType(moves, Moves.mirrorCoat, Type.STEEL);
//        updateMovePower(moves, Moves.mirrorCoat, 150);
//        updateMovePP(moves, Moves.mirrorCoat, 5);
//        moves.get(Moves.mirrorCoat).priority = 0;
//        moves.get(Moves.mirrorCoat).effect = MoveEffect.DMG_HIGH_USER_HP;
//        moves.get(Moves.mirrorCoat).target = MoveTarget.ALL_ADJACENT_FOES;
//        moves.get(Moves.mirrorCoat).isCopiedByMirrorMove = true;
//        moves.get(Moves.mirrorCoat).description = "The user blasts the targets with a radiant sheen. The lower the user's HP, the less powerful it becomes.";

        // 244 Psych Up
        if (typeInGame(Type.FAIRY))
            updateMoveType(moves, Moves.psychUp, Type.FAIRY);

        // 249 Rock Smash
        if (generationOfPokemon() == 6) {
            updateMovePower(moves, Moves.rockSmash, 75);
        } else {
            moves.get(Moves.rockSmash).statChanges[0].percentChance = 100.0;
            moves.get(Moves.rockSmash).description = removeLineBreaks(moves.get(Moves.rockSmash).description)
                    .replace("may lower", "harshly lowers")
                    .replace("may also cut", "also harshly cuts")
                    .replace("may also lower", "also harshly lowers");
        }

        // 250 Whirlpool
        updateMoveAccuracy(moves, Moves.whirlpool, 100);

        // Gen III
        if (generationOfPokemon() < 3)
            return;

        // 260 Flatter
        if (typeInGame(Type.FAIRY))
            updateMoveType(moves, Moves.flatter, Type.FAIRY);

        // 261 Will-O-Wisp
        updateMoveAccuracy(moves, Moves.willOWisp, 100);
        updateMoveType(moves, Moves.willOWisp, Type.GHOST);

        // 262 Memento
        if (generationOfPokemon() == 5) {
            // There's a bug where this only seems to work in Gen V...
            // Tested in ORAS, there's a cosmetic bug where HP bar doesn't visually drop

            updateMoveCategory(moves, Moves.memento, MoveCategory.SPECIAL);
            updateMovePower(moves, Moves.memento, 70);
            updateMovePP(moves, Moves.memento, 15);
            moves.get(Moves.memento).qualities = MoveQualities.DAMAGE_TARGET_STAT_CHANGE;
            moves.get(Moves.memento).effect = MoveEffect.DMG_TRGT_ATK_MINUS_1;
            moves.get(Moves.memento).statChanges[0].type = StatChangeType.ATTACK;
            moves.get(Moves.memento).statChanges[0].stages = -1;
            moves.get(Moves.memento).statChanges[0].percentChance = 100;
            moves.get(Moves.memento).description = "The user reveals a dark energy and damages the target. It also lowers the target's Attack stat.";
        }

//        // 266 Follow Me
//        if (typeInGame(Type.FAIRY))
//            updateMoveType(moves, Moves.followMe, Type.FAIRY);

//        // 270 Helping Hand
//        if (typeInGame(Type.FAIRY))
//            updateMoveType(moves, Moves.helpingHand, Type.FAIRY);

        // 273 Wish
        if (typeInGame(Type.FAIRY))
            updateMoveType(moves, Moves.wish, Type.FAIRY);

        // 292 Arm Thrust
        updateMovePower(moves, Moves.armThrust, 25);

        // 295 Luster Purge
        if (generationOfPokemon() >= 6 || !typeInGame(Type.FAIRY)) {
            updateMovePower(moves, Moves.lusterPurge, 65);
            updateMovePP(moves, Moves.lusterPurge, 10);
            moves.get(Moves.lusterPurge).statChanges[0].percentChance = 100.0;
            moves.get(Moves.lusterPurge).description = "The user lets loose a damaging burst of light. It also lowers the target's Sp. Def stat.";
        }

        // 296 Mist Ball
        if (generationOfPokemon() >= 6 || !typeInGame(Type.FAIRY)) {
            updateMovePower(moves, Moves.mistBall, 75);
            updateMovePP(moves, Moves.mistBall, 10);
            moves.get(Moves.mistBall).statChanges[0].percentChance = 100.0;
            moves.get(Moves.mistBall).description = "A mistlike flurry of down envelops and damages the target. It also lowers the target's Sp. Atk.";
        }

        // 299 Blaze Kick
        updateMoveAccuracy(moves, Moves.blazeKick, 95);

        // 300 Ice Ball
        updateMoveAccuracy(moves, Moves.iceBall, 95);

        // 304 Hyper Voice
        moves.get(Moves.hyperVoice).flinchPercentChance = 10.0;
        moves.get(Moves.hyperVoice).effect = MoveEffect.DMG_FLINCH;
        moves.get(Moves.hyperVoice).description = "The user lets loose a horribly echoing shout with the power to inflict damage. It may also make the target flinch.";

        // 306 Crush Claw
        updateMoveAccuracy(moves, Moves.crushClaw, 100);

        // 308 Hydro Cannon
        moves.get(Moves.hydroCannon).qualities = MoveQualities.DAMAGE_USER_STAT_CHANGE;
        updateMovePower(moves, Moves.hydroCannon, 120);
        updateMoveAccuracy(moves, Moves.hydroCannon, 100);
        moves.get(Moves.hydroCannon).effect = MoveEffect.DMG_USER_SPA_MINUS_2;
        moves.get(Moves.hydroCannon).statChanges[0] = new Move.StatChange(StatChangeType.SPECIAL_ATTACK, -1, 100);
        moves.get(Moves.hydroCannon).isRechargeMove = false;
        moves.get(Moves.hydroCannon).description = "The target is hit with a watery blast. It harshly lowers the user's Sp. Atk stat.";

        // 310 Astonish
        // TODO Test this in gen 6... doesn't work in Gen V!
        if (generationOfPokemon() != 5) {
            updateMovePower(moves, Moves.astonish, 40);
            updateMovePP(moves, Moves.astonish, 10);
            moves.get(Moves.astonish).priority = 3;
            moves.get(Moves.astonish).flinchPercentChance = 100;
            moves.get(Moves.astonish).effect = MoveEffect.FAKE_OUT;
            moves.get(Moves.astonish).description = "An attack that hits first and makes the target flinch. It only works the first turn the user is in battle.";
        }

        // 314 Air Cutter
        updateMovePower(moves, Moves.airCutter, 75);
        updateMoveAccuracy(moves, Moves.airCutter, 100);
        updateMovePP(moves, Moves.airCutter, 15);

        // 317 Rock Tomb
        updateMovePower(moves, Moves.rockTomb, 65);
        updateMoveAccuracy(moves, Moves.rockTomb, 100);

        // 319 Metal Sound
        updateMoveAccuracy(moves, Moves.metalSound, 100);

        // 321 Tickle
        if (typeInGame(Type.FAIRY))
            updateMoveType(moves, Moves.tickle, Type.FAIRY);

        // 324 Signal Beam
        moves.get(Moves.signalBeam).statusPercentChance = 30.0;

        // 325 Shadow Punch
        updateMovePower(moves, Moves.shadowPunch, 75);

        // 326 Extrasensory
        updateMovePower(moves, Moves.extrasensory, 70);
        moves.get(Moves.extrasensory).flinchPercentChance = 30.0;

        // 327 Sky Uppercut
        updateMovePower(moves, Moves.skyUppercut, 80);
        updateMoveAccuracy(moves, Moves.skyUppercut, 100);

        // 328 Sand Tomb
        updateMoveAccuracy(moves, Moves.sandTomb, 100);

//        // 329 Sheer Cold
//        moves.get(Moves.sheerCold).qualities = MoveQualities.DAMAGE_USER_STAT_CHANGE;
//        updateMovePower(moves, Moves.sheerCold, 130);
//        updateMoveAccuracy(moves, Moves.sheerCold, 90);
//        moves.get(Moves.sheerCold).effect = MoveEffect.DMG_USER_SPA_MINUS_2;
//        moves.get(Moves.sheerCold).statChanges[0] = new Move.StatChange(StatChangeType.SPECIAL_ATTACK, -2, 100.0);
//        moves.get(Moves.sheerCold).description = "The target is attacked with a blast of absolute-zero cold. This harshly reduces the user's Sp. Atk stat.";

        // 330 Muddy Water
        if (doubles)
            updateMovePower(moves, Moves.muddyWater, 65);
        updateMoveAccuracy(moves, Moves.muddyWater, 95);
        moves.get(Moves.muddyWater).effect = MoveEffect.DMG_TRGT_SPE_MINUS_1;
        moves.get(Moves.muddyWater).statChanges[0].type = StatChangeType.SPEED;
        moves.get(Moves.muddyWater).statChanges[0].percentChance = 100.0;
        moves.get(Moves.muddyWater).description = "The user attacks by shooting muddy water at the opposing team. It also lowers their Speed stats.";

        // 338 Frenzy Plant
        moves.get(Moves.frenzyPlant).qualities = MoveQualities.DAMAGE_USER_STAT_CHANGE;
        updateMovePower(moves, Moves.frenzyPlant, 120);
        updateMoveAccuracy(moves, Moves.frenzyPlant, 100);
        moves.get(Moves.frenzyPlant).effect = MoveEffect.DMG_USER_DEF_SPD_MINUS_1;
        moves.get(Moves.frenzyPlant).statChanges[0] = new Move.StatChange(StatChangeType.DEFENSE, -1, 100);
        moves.get(Moves.frenzyPlant).statChanges[1] = new Move.StatChange(StatChangeType.SPECIAL_DEFENSE, -1, 100);
        moves.get(Moves.frenzyPlant).isRechargeMove = false;
        moves.get(Moves.frenzyPlant).description = "The user slams the target with an enormous tree. It lowers the user’s Defense and Sp. Def stats.";

        // 340 Bounce
        updateMovePower(moves, Moves.bounce, 95);
        updateMoveAccuracy(moves, Moves.bounce, 100);

        // 341 Mud Shot
        updateMovePower(moves, Moves.mudShot, 60);
        updateMoveAccuracy(moves, Moves.mudShot, 100);

        // 342 Poison Tail
        updateMovePower(moves, Moves.poisonTail, 60);
        moves.get(Moves.poisonTail).statusPercentChance = 30;

        // 350 Rock Blast
        updateMoveAccuracy(moves, Moves.rockBlast, 100);

        // 354 Psycho Boost
        updateMovePower(moves, Moves.psychoBoost, 130);

        // Gen IV
        if (generationOfPokemon() < 4)
            return;

        // 358 Wake-Up Slap
        if (typeInGame(Type.FAIRY))
            updateMoveType(moves, Moves.wakeUpSlap, Type.FAIRY);

        // 364 Feint
        updateMovePower(moves, Moves.feint, 50);

//        // 368 Metal Burst
//        updateMovePower(moves, Moves.metalBurst, 150);
//        updateMovePP(moves, Moves.metalBurst, 5);
//        moves.get(Moves.metalBurst).effect = MoveEffect.DMG_HIGH_USER_HP;
//        moves.get(Moves.metalBurst).target = MoveTarget.ALL_ADJACENT_FOES;
//        moves.get(Moves.metalBurst).description = "The user explodes chunks of metal toward the targets. The lower the user's HP, the less powerful it becomes.";

        // 381 Lucky Chant
        if (typeInGame(Type.FAIRY))
            updateMoveType(moves, Moves.luckyChant, Type.FAIRY);
        moves.get(Moves.luckyChant).qualities = MoveQualities.OTHER;
        moves.get(Moves.luckyChant).effect = MoveEffect.CRIT_RATIO_PLUS_2;
        moves.get(Moves.luckyChant).target = MoveTarget.USER;
        moves.get(Moves.luckyChant).description = "The user chants an incantation toward the sky so that critical hits land more easily.";

        // 382 Me First
        if (typeInGame(Type.FAIRY))
            updateMoveType(moves, Moves.meFirst, Type.FAIRY);

        // 383 Copycat
        if (typeInGame(Type.FAIRY))
            updateMoveType(moves, Moves.copycat, Type.FAIRY);

        // 403 Air Slash
        updateMoveAccuracy(moves, Moves.airSlash, 100);

        // 405 Bug Buzz
        moves.get(Moves.bugBuzz).statChanges[0].percentChance = 30.0;

        // 407 Dragon Rush
        updateMovePower(moves, Moves.dragonRush, 40);
        updateMoveAccuracy(moves, Moves.dragonRush, 100);
        updateMovePP(moves, Moves.dragonRush, 30);
        moves.get(Moves.dragonRush).priority = 1;
        moves.get(Moves.dragonRush).flinchPercentChance = 0.0;
        moves.get(Moves.dragonRush).effect = MoveEffect.DMG_INCR_PRIO;
        moves.get(Moves.dragonRush).description = "The user tackles the target while exhibiting overwhelming menace. This move always goes first.";

        // 416 Giga Impact
        moves.get(Moves.gigaImpact).qualities = MoveQualities.DAMAGE_USER_STAT_CHANGE;
        updateMovePower(moves, Moves.gigaImpact, 130);
        updateMoveAccuracy(moves, Moves.gigaImpact, 100);
        moves.get(Moves.gigaImpact).effect = MoveEffect.DMG_USER_DEF_SPD_MINUS_1;
        moves.get(Moves.gigaImpact).statChanges[0] = new Move.StatChange(StatChangeType.DEFENSE, -1, 100);
        moves.get(Moves.gigaImpact).statChanges[1] = new Move.StatChange(StatChangeType.SPECIAL_DEFENSE, -1, 100);
        moves.get(Moves.gigaImpact).isRechargeMove = false;
        moves.get(Moves.gigaImpact).description = "The user charges at the target using every bit of its power. It lowers the user’s Defense and Sp. Def stats.";

        // 422 Thunder Fang
        updateMoveAccuracy(moves, Moves.thunderFang, 100);
        moves.get(Moves.thunderFang).statusPercentChance = 30.0;
        moves.get(Moves.thunderFang).flinchPercentChance = 30.0;

        // 423 Ice Fang
        updateMoveAccuracy(moves, Moves.iceFang, 100);
        moves.get(Moves.iceFang).flinchPercentChance = 30.0;

        // 424 Fire Fang
        updateMoveAccuracy(moves, Moves.fireFang, 100);
        moves.get(Moves.fireFang).statusPercentChance = 30.0;
        moves.get(Moves.fireFang).flinchPercentChance = 30.0;

        // 426 Mud Bomb
        updateMoveAccuracy(moves, Moves.mudBomb, 100);
        moves.get(Moves.mudBomb).effect = MoveEffect.DMG_TRGT_SPD_MINUS_1;
        moves.get(Moves.mudBomb).statChanges[0].type = StatChangeType.SPECIAL_DEFENSE;
        moves.get(Moves.mudBomb).statChanges[0].percentChance = 100.0;
        moves.get(Moves.mudBomb).description = "The user launches a hard-packed mud ball to attack. It also lowers the target's Sp. Def stat.";

        // 429 Mirror Shot
        updateMovePower(moves, Moves.mirrorShot, 70);
        updateMoveAccuracy(moves, Moves.mirrorShot, 100);
        moves.get(Moves.mirrorShot).effect = MoveEffect.DMG_TRGT_SPA_MINUS_1;
        moves.get(Moves.mirrorShot).statChanges[0].type = StatChangeType.SPECIAL_ATTACK;
        moves.get(Moves.mirrorShot).statChanges[0].percentChance = 100.0;
        moves.get(Moves.mirrorShot).description = "The user looses a flash of energy at the target from its polished body. It also lowers the target's Sp. Atk stat.";

        // 431 Rock Climb
        updateMoveAccuracy(moves, Moves.rockClimb, 100);
        moves.get(Moves.rockClimb).statusPercentChance = 30.0;

        // 439 Rock Wrecker
        moves.get(Moves.rockWrecker).qualities = MoveQualities.DAMAGE_USER_STAT_CHANGE;
        updateMovePower(moves, Moves.rockWrecker, 130);
        updateMoveAccuracy(moves, Moves.rockWrecker, 100);
        moves.get(Moves.rockWrecker).effect = MoveEffect.DMG_USER_DEF_SPD_MINUS_1;
        moves.get(Moves.rockWrecker).statChanges[0] = new Move.StatChange(StatChangeType.DEFENSE, -1, 100);
        moves.get(Moves.rockWrecker).statChanges[1] = new Move.StatChange(StatChangeType.SPECIAL_DEFENSE, -1, 100);
        moves.get(Moves.rockWrecker).isRechargeMove = false;
        moves.get(Moves.rockWrecker).description = "The user launches a huge boulder at the target to attack. It lowers the user’s Defense and Sp. Def stats.";

        // 443 Magnet Bomb
        updateMoveCategory(moves, Moves.magnetBomb, MoveCategory.SPECIAL);

        // 445 Captivate
        if (typeInGame(Type.FAIRY))
            updateMoveType(moves, Moves.captivate, Type.FAIRY);

        // 451 Charge Beam
        updateMoveAccuracy(moves, Moves.chargeBeam, 100);
        moves.get(Moves.chargeBeam).statChanges[0].percentChance = 100.0;
        moves.get(Moves.chargeBeam).description = "The user attacks with an electric charge. The user then uses the remaining electricity to raise its Sp. Atk stat.";

        // 457 Head Smash
        updateMovePower(moves, Moves.headSmash, 120);
        updateMoveAccuracy(moves, Moves.headSmash, 95);
        moves.get(Moves.headSmash).effect = MoveEffect.DMG_RECOIL_33;
        moves.get(Moves.headSmash).recoil = -33;
        moves.get(Moves.headSmash).description = "The user attacks the target with a hazardous, full-power headbutt. The user also takes serious damage.";

        // 463 Magma Storm
        updateMoveAccuracy(moves, Moves.magmaStorm, 85);

        // Gen V
        if (generationOfPokemon() < 5)
            return;

        // 494 Entrainment
        if (typeInGame(Type.FAIRY))
            updateMoveType(moves, Moves.entrainment, Type.FAIRY);

        // 495 After You
        if (typeInGame(Type.FAIRY))
            updateMoveType(moves, Moves.afterYou, Type.FAIRY);

        // 499 Clear Smog
        updateMovePower(moves, Moves.clearSmog, 60);

        // 505 Heal Pulse
        if (typeInGame(Type.FAIRY))
            updateMoveType(moves, Moves.healPulse, Type.FAIRY);

        // 509 Circle Throw
        updateMoveAccuracy(moves, Moves.circleThrow, 100);

        // 516 Bestow
        if (typeInGame(Type.FAIRY))
            updateMoveType(moves, Moves.bestow, Type.FAIRY);

        // 522 Struggle Bug
        updateMovePower(moves, Moves.struggleBug, 60);

        // 524 Frost Breath
        updateMoveAccuracy(moves, Moves.frostBreath, 100);

        // 525 Dragon Tail
        updateMoveAccuracy(moves, Moves.dragonTail, 100);

        // 527 Electroweb
        updateMovePower(moves, Moves.electroweb, 55);
        updateMoveAccuracy(moves, Moves.electroweb, 100);

        // 530 Dual Chop
        updateMoveAccuracy(moves, Moves.dualChop, 95);

        // 534 Razor Shell
        updateMoveAccuracy(moves, Moves.razorShell, 100);

        // 536 Leaf Tornado
        updateMovePower(moves, Moves.leafTornado, 70);
        updateMoveAccuracy(moves, Moves.leafTornado, 100);
        moves.get(Moves.leafTornado).effect = MoveEffect.DMG_TRGT_SPA_MINUS_1;
        moves.get(Moves.leafTornado).statChanges[0].type = StatChangeType.SPECIAL_ATTACK;
        moves.get(Moves.leafTornado).statChanges[0].percentChance = 100.0;
        moves.get(Moves.leafTornado).description = "The user attacks its target by encircling it in sharp leaves. This attack also lowers the target's Sp. Atk stat.";

        // 539 Night Daze
        moves.get(Moves.nightDaze).qualities = MoveQualities.DAMAGE_TARGET_STATUS;
        updateMoveAccuracy(moves, Moves.nightDaze, 100);
        moves.get(Moves.nightDaze).statusType = MoveStatusType.CONFUSION;
        moves.get(Moves.nightDaze).statusPercentChance = 20.0;
        moves.get(Moves.nightDaze).effect = MoveEffect.DMG_CNF;
        moves.get(Moves.nightDaze).statChanges[0] = new Move.StatChange();
        moves.get(Moves.nightDaze).description = "The user lets loose a pitch-black shock wave at its target. It may also confuse the target.";

        // 541 TailSlap
        updateMoveAccuracy(moves, Moves.tailSlap, 100);

        // 544 Gear Grind
        updateMoveAccuracy(moves, Moves.gearGrind, 95);

        // 555 Snarl
        updateMovePower(moves, Moves.snarl, 60);
        updateMoveAccuracy(moves, Moves.snarl, 100);

        // 556 Icicle Crash
        updateMoveAccuracy(moves, Moves.icicleCrash, 100);

        // 557 V-create
        updateMovePower(moves, Moves.vCreate, 120);
        updateMoveAccuracy(moves, Moves.vCreate, 100);
        moves.get(Moves.vCreate).effect = MoveEffect.DMG_USER_DEF_SPD_MINUS_1;
        moves.get(Moves.vCreate).statChanges[0] = new Move.StatChange(StatChangeType.DEFENSE, -1, 100);
        moves.get(Moves.vCreate).statChanges[1] = new Move.StatChange(StatChangeType.SPECIAL_DEFENSE, -1, 100);
        moves.get(Moves.vCreate).statChanges[2] = new Move.StatChange();
        moves.get(Moves.vCreate).description = "With a hot flame on its forehead, the user hurls itself at its target. It lowers the user’s Defense and Sp. Def stats.";

        // 459 Roar of Time
        moves.get(Moves.roarOfTime).qualities = MoveQualities.DAMAGE_USER_STAT_CHANGE;
        updateMovePower(moves, Moves.roarOfTime, 120);
        updateMoveAccuracy(moves, Moves.roarOfTime, 100);
        moves.get(Moves.roarOfTime).effect = MoveEffect.DMG_USER_DEF_SPD_MINUS_1;
        moves.get(Moves.roarOfTime).statChanges[0] = new Move.StatChange(StatChangeType.DEFENSE, -1, 100);
        moves.get(Moves.roarOfTime).statChanges[1] = new Move.StatChange(StatChangeType.SPECIAL_DEFENSE, -1, 100);
        moves.get(Moves.roarOfTime).isRechargeMove = false;
        moves.get(Moves.roarOfTime).description = "The user blasts the target with power that distorts even time. It lowers the user’s Defense and Sp. Def stats.";

        // 550 Bolt Strike
        moves.get(Moves.boltStrike).qualities = MoveQualities.DAMAGE_USER_STAT_CHANGE;
        updateMovePower(moves, Moves.boltStrike, 120);
        updateMoveAccuracy(moves, Moves.boltStrike, 100);
        moves.get(Moves.boltStrike).statusType = MoveStatusType.NONE;
        moves.get(Moves.boltStrike).statusPercentChance = 0.0;
        moves.get(Moves.boltStrike).effect = MoveEffect.DMG_USER_DEF_SPD_MINUS_1;
        moves.get(Moves.boltStrike).statChanges[0] = new Move.StatChange(StatChangeType.DEFENSE, -1, 100);
        moves.get(Moves.boltStrike).statChanges[1] = new Move.StatChange(StatChangeType.SPECIAL_DEFENSE, -1, 100);
        moves.get(Moves.boltStrike).description = "The user charges its target, surrounding itself with a electricity. It lowers the user’s Defense and Sp. Def stats.";

        // Gen 6
        if (generationOfPokemon() < 6)
            return;

//        TODO: Let's maybe keep Play Rough as a solid option
//        // 583 Play Rough
//        moves.get(Moves.playRough).power = 75;
//        moves.get(Moves.playRough).statChanges[0].percentChance = 100;
//        moves.get(Moves.playRough).description = removeLineBreaks(moves.get(Moves.playRough).description)
//                .replace("may also lower", "also lowers");

        // 584 Fairy Wind
        updateMovePower(moves, Moves.fairyWind, 60);
        updateMoveAccuracy(moves, Moves.fairyWind, Move.getPerfectAccuracy());

        // 591 Diamond Storm
        updateMovePower(moves, Moves.diamondStorm, 65);
        updateMoveCategory(moves, Moves.diamondStorm, MoveCategory.SPECIAL);

        // 592 Steam Eruption
        updateMovePower(moves, Moves.steamEruption, 90);
        updateMoveAccuracy(moves, Moves.steamEruption, 90);

        // 595 Mystical Fire
        updateMovePower(moves, Moves.mysticalFire, 70);

        // 617 Light of Ruin
        updateMovePower(moves, Moves.lightOfRuin, 120);
        updateMoveAccuracy(moves, Moves.lightOfRuin, 95);
        moves.get(Moves.lightOfRuin).effect = MoveEffect.DMG_RECOIL_33;
        moves.get(Moves.lightOfRuin).recoil = -33;
        moves.get(Moves.lightOfRuin).description = "Drawing power from the Eternal Flower, the user fires a powerful beam of light. The user also takes serious damage.";

        // 613 Oblivion Wing
        updateMovePower(moves, Moves.oblivionWing, 65);

        // 614 Thousand Arrows
        updateMovePower(moves, Moves.thousandArrows, 75);
        if (doubles)
            updateMoveAccuracy(moves, Moves.thousandArrows, 90);

        // 615 Thousand Waves
        updateMovePower(moves, Moves.thousandWaves, 75);
        if (doubles)
            updateMoveAccuracy(moves, Moves.thousandWaves, 90);
        updateMoveCategory(moves, Moves.thousandWaves, MoveCategory.SPECIAL);

        // 616 Land's Wrath
        updateMovePower(moves, Moves.landsWrath, 80);
        if (doubles)
            updateMoveAccuracy(moves, Moves.landsWrath, 90);

        // 618 Origin Pulse
        updateMovePower(moves, Moves.originPulse, 95);
        updateMoveAccuracy(moves, Moves.originPulse, 90);

        // 619 Precipice Blades
        updateMovePower(moves, Moves.precipiceBlades, 95);
        updateMoveAccuracy(moves, Moves.precipiceBlades, 90);

        // 621 Hyperspace Fury
        updateMovePower(moves, Moves.hyperspaceFury, 80);
    }

    private Map<Integer, boolean[]> moveUpdates;

    @Override
    public void initMoveUpdates() {
        moveUpdates = new TreeMap<>();
    }

    @Override
    public Map<Integer, boolean[]> getMoveUpdates() {
        return moveUpdates;
    }

    protected void customTypeEffectiveness(Type[] typeTable, List<TypeRelationship> typeEffectiveness) {
        Effectiveness[][] typeEffectivenessTable = new Effectiveness[18][];

        for (int i = 0; i < typeEffectivenessTable.length; ++i) {
            typeEffectivenessTable[i] = new Effectiveness[typeEffectivenessTable.length];
        }

        for (int i = 0; i < typeEffectivenessTable.length; ++i) {
            for (int j = 0; j < typeEffectivenessTable.length; ++j) {
                typeEffectivenessTable[i][j] = Effectiveness.NEUTRAL;
            }
        }

        for (TypeRelationship relationship : typeEffectiveness) {
            int attacker = Gen4Constants.typeToByte(relationship.attacker);
            int defender = Gen4Constants.typeToByte(relationship.defender);

            if (attacker < 0 || attacker > 17 || defender < 0 || defender > 17)
                throw new RuntimeException();

            typeEffectivenessTable[attacker][defender] = relationship.effectiveness;
        }

        customTypeEffectiveness(typeTable, typeEffectivenessTable);

        typeEffectiveness.clear();
        for (int i = 0; i < typeEffectivenessTable.length; ++i) {
            Type attacker = Gen4Constants.typeTable[i];
            for (int j = 0; j < typeEffectivenessTable.length; ++j) {
                Effectiveness effectiveness = typeEffectivenessTable[i][j];
                if (effectiveness == Effectiveness.NEUTRAL)
                    continue;

                Type defender = Gen4Constants.typeTable[j];
                typeEffectiveness.add(new TypeRelationship(attacker, defender, effectiveness));
            }
        }
    }

    protected void customTypeEffectiveness(Type[] typeTable, Effectiveness[][] typeEffectivenessTable) {
        Map<Effectiveness, String> effectivenessStringMap = new HashMap<Effectiveness, String>() {{
            put(Effectiveness.HALF, " not very effective");
            put(Effectiveness.NEUTRAL, " neutral");
            put(Effectiveness.DOUBLE, " super effective");
        }};

        log("--Custom Type Effectiveness--");

        List<String> logStrings = new ArrayList<>();

        for (int defendIndex = 0; defendIndex < typeEffectivenessTable.length; defendIndex++) {
            Type defendType = typeTable[defendIndex];
            Map<Type, Effectiveness> against = Effectiveness.against(defendType, null,
                    generationOfPokemon(), true, true, true);

            for (int attackIndex = 0; attackIndex < typeEffectivenessTable.length; attackIndex++) {
                Type attackType = typeTable[attackIndex];
                Effectiveness oldEffectiveness = typeEffectivenessTable[attackIndex][defendIndex];
                Effectiveness newEffectiveness = against.get(attackType);

                if (oldEffectiveness == newEffectiveness)
                    continue;

                typeEffectivenessTable[attackIndex][defendIndex] = newEffectiveness;
                logStrings.add(attackType + effectivenessStringMap.get(oldEffectiveness) + " vs " + defendType
                        + " => " + attackType + effectivenessStringMap.get(newEffectiveness) + " vs " + defendType);
            }
        }

        Collections.sort(logStrings);
        for (String str : logStrings) {
            log("Replaced: " + str);
        }

        logBlankLine();
    }

    public String sortText(String inText, int maxLines, int maxLinePixels, boolean throwOnOverflow) {
        return sortText(inText, maxLines, maxLinePixels, 7, throwOnOverflow);
    }

    public String sortText(String inText, int maxLines, int maxLinePixels, int sentenceLineFlags, boolean throwOnOverflow) {
        String[] inLines = inText.split(getLineBreakStringRegex());
        if (validateTextLines(inLines, maxLines, maxLinePixels))
            return inText; // Already valid

        String[] words = inText.replace(getLineBreakString(), " ").split(" ");
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < words.length - 1; i++) {
            if (words[i].equals("Sp.")) {
                words[i] = String.join(" ", words[i], words[i + 1]);
                words[i + 1] = "";
            }
        }

        int word = 0;
        int linePixels = 0;
        StringBuilder stringBuilder = new StringBuilder();

        boolean isNewSentence = false;
        int sentenceIndex = 0;
        while (word < words.length) {
            if (words[word].isEmpty()) {
                word++;
                continue;
            }

            int spacePixels = linePixels == 0 ? 0 : getTextCharPixels(' ');
            int wordPixels = getTextLineCharPixels(words[word]);

            boolean shiftSentence = sentenceIndex > 0 && (sentenceLineFlags & (1 << (sentenceIndex - 1))) != 0;

            if (linePixels + spacePixels + wordPixels > maxLinePixels || (isNewSentence && shiftSentence)) {
                // Next Line
                linePixels = 0;
                lines.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
                isNewSentence = false;
                continue;
            }

            if (words[word].endsWith(".")) {
                isNewSentence = true;
                ++sentenceIndex;
            }

            if (linePixels > 0)
                stringBuilder.append(' ');
            stringBuilder.append(words[word]);
            linePixels += spacePixels + wordPixels;
            word++;
        }

        lines.add(stringBuilder.toString());

        if (lines.size() > maxLines) {
            if (sentenceLineFlags > 0) {
                int mask = (1 << (sentenceIndex - 1)) - 1;
                sentenceLineFlags = mask & (sentenceLineFlags - 1);

                return sortText(inText, maxLines, maxLinePixels, sentenceLineFlags, throwOnOverflow);
            }

            // TOO LONG! May happen with many TMs
            if (throwOnOverflow)
                throw new RuntimeException("Text was too long: " + inText);

            return String.join(getLineBreakString(), lines);
        }

        return String.join(getLineBreakString(), lines);
    }

    private String removeLineBreaks(String string) {
        return string.replaceAll(getLineBreakStringRegex(), " ");
    }

    private boolean validateTextLines(String[] lines, int maxLines, int maxLinePixels) {
        if (lines.length > maxLines)
            return false;

        for (String line : lines) {
            if (getTextLineCharPixels(line) > maxLinePixels)
                return false;
        }

        return true;
    }

    private int getTextLineCharPixels(String line) {
        int pixelCount = 0;

        char[] characters = line.toCharArray();
        for (char character : characters) {
            pixelCount += getTextCharPixels(character);
        }

        return pixelCount;
    }

    public int getLongestLinePixels(String inText) {
        String[] inLines = inText.split(getLineBreakStringRegex());

        int longestLinePixels = 0;
        for (String string : inLines) {
            int linePixels = getTextLineCharPixels(string);
            if (linePixels > longestLinePixels)
                longestLinePixels = linePixels;
        }

        return longestLinePixels;
    }

    @Override
    public int getTextCharPixels(char character) {
        return switch (character) {
            case 'i' -> 3;
            case ' ', 'l' -> 4;
            case 'f', ',', '.', '\'', '\"', ':', ';' -> 5;
            default -> 6; // All other characters are 6 pixels
        };
    }

    @Override
    public String getLineBreakString() {
        return "\\n";
    }

    @Override
    public String getLineBreakStringRegex() {
        return "\\\\n";
    }

    @Override
    public void randomizeMovesLearnt(Settings settings) {
        boolean typeThemed = settings.getMovesetsMod() == Settings.MovesetsMod.RANDOM_PREFER_SAME_TYPE;
        boolean noBroken = settings.isBlockBrokenMovesetMoves();
        boolean forceStartingMoves = supportsFourStartingMoves() && settings.isStartWithGuaranteedMoves();
        int forceStartingMoveCount = settings.getGuaranteedMoveCount();
        double goodDamagingPercentage =
                settings.isMovesetsForceGoodDamaging() ? settings.getMovesetsGoodDamagingPercent() / 100.0 : 0;
        boolean evolutionMovesForAll = settings.isEvolutionMovesForAll();

        // Get current sets
        Map<Integer, List<MoveLearnt>> movesets = this.getMovesLearnt();

        // Build sets of moves
        List<Move> validMoves = new ArrayList<>();
        List<Move> validDamagingMoves = new ArrayList<>();
        Map<Type, List<Move>> validTypeMoves = new HashMap<>();
        Map<Type, List<Move>> validTypeDamagingMoves = new HashMap<>();
        createSetsOfMoves(settings, noBroken, validMoves, validDamagingMoves, validTypeMoves, validTypeDamagingMoves);

        int totalCount = 0;
        int minFinalMoveLevel = 100;
        int maxFinalMoveLevel = 0;
        double averageFinalMoveLevel = 0.0;

        List<Integer> eliteFourTrainers = getEliteFourTrainers(isChallengeMode());
        int minEliteFourAceLevel = 100;
        for (int e : eliteFourTrainers) {
            Trainer tr = getTrainers().get(e - 1);
            int aceLevel = tr.getAceLevel();

            if (aceLevel < minEliteFourAceLevel) {
                minEliteFourAceLevel = aceLevel;
            }
        }

        if (settings.isTrainersLevelModified()) {
            double mod = 1.0 + settings.getTrainersLevelModifier() / 100.0;
            minEliteFourAceLevel = (int) Math.round(minEliteFourAceLevel * mod);
        }

        if (isChallengeMode()) {
            minEliteFourAceLevel += 4;
        }

        for (Integer pkmnNum : movesets.keySet()) {
            List<Integer> learnt = new ArrayList<>();
            // List<MoveLearnt> moves = movesets.get(pkmnNum);
            int lv1AttackingMove = 0;
            Pokemon pkmn = findPokemonInPoolWithSpeciesID(mainPokemonListInclFormes, pkmnNum);
            if (pkmn == null) {
                continue;
            }

            List<MoveLearnt> moves = movesets.get(pkmnNum);
            moves.clear();

            if (pkmn.actuallyCosmetic) {
                List<MoveLearnt> baseFormeMoves = movesets.get(pkmn.baseForme.number);
                for (int i = 0; i < baseFormeMoves.size(); i++) {
                    moves.add(baseFormeMoves.get(i));
                }
                continue;
            }

            // TODO: Move these back to level 1
            int level = 2;

            double atkSpAtkRatio = pkmn.getAttackSpecialAttackRatio();

            // TODO: Move these back to level 1
            // 4 starting moves?
            if (forceStartingMoves) {
                for (int i = 0; i < forceStartingMoveCount; i++) {
                    MoveLearnt level1Move = new MoveLearnt();
                    level1Move.level = 2;
                    level1Move.move = 0;
                    moves.add(0, level1Move);
                }

                level = 6 + random.nextInt(5); // 6-10
            }

            int lastDouble = -1;
            while (level <= minEliteFourAceLevel && moves.size() < 22) {
                MoveLearnt move = new MoveLearnt();
                move.level = level;
                move.move = 0;
                moves.add(move);

                int minNext = level < 25 ? 2 : level < 40 ? 3 : 4;
                int maxNext = level < 25 ? 4 : level < 40 ? 5 : 6;

                if (lastDouble != level && random.nextDouble() < 0.005) {
                    // small chance to not raise level (i.e., two moves on the same level)
                    lastDouble = level;
                    continue;
                }

                level += minNext + random.nextInt(maxNext - minNext);
            }

            if (evolutionMovesForAll) {
                if (moves.get(0).level != 0) {
                    MoveLearnt fakeEvoMove = new MoveLearnt();
                    fakeEvoMove.level = 0;
                    fakeEvoMove.move = 0;
                    moves.add(0, fakeEvoMove);
                }
            }

            int finalLevel = moves.get(moves.size() - 1).level;
            minFinalMoveLevel = Math.min(minFinalMoveLevel, finalLevel);
            maxFinalMoveLevel = Math.max(maxFinalMoveLevel, finalLevel);
            averageFinalMoveLevel = ((averageFinalMoveLevel * totalCount) + finalLevel) / (totalCount + 1);
            totalCount++;

            // Find last lv1 move
            // lv1index ends up as the index of the first non-lv1 move
            int lv1index = moves.get(0).level == 1 ? 0 : 1; // Evolution move handling (level 0 = evo move)
            while (lv1index < moves.size() && moves.get(lv1index).level == 1) {
                lv1index++;
            }

            // last lv1 move is 1 before lv1index
            if (lv1index != 0) {
                lv1index--;
            }

            // Force a certain amount of good damaging moves depending on the percentage
            int goodDamagingLeft = (int) Math.round(goodDamagingPercentage * moves.size());

            // Replace moves as needed
            for (int i = 0; i < moves.size(); i++) {
                // should this move be forced damaging?
                boolean attemptDamaging = i == lv1index || goodDamagingLeft > 0;

                // type themed?
                Type typeOfMove = null;
                if (typeThemed) {
                    double picked = random.nextDouble();
                    if ((pkmn.primaryType == Type.NORMAL && pkmn.secondaryType != null) ||
                            (pkmn.secondaryType == Type.NORMAL)) {

                        Type otherType = pkmn.primaryType == Type.NORMAL ? pkmn.secondaryType : pkmn.primaryType;

                        // Normal/OTHER: 10% normal, 30% other, 60% random
                        if (picked < 0.1) {
                            typeOfMove = Type.NORMAL;
                        } else if (picked < 0.4) {
                            typeOfMove = otherType;
                        }
                        // else random
                    } else if (pkmn.secondaryType != null) {
                        // Primary/Secondary: 20% primary, 20% secondary, 60% random
                        if (picked < 0.2) {
                            typeOfMove = pkmn.primaryType;
                        } else if (picked < 0.4) {
                            typeOfMove = pkmn.secondaryType;
                        }
                        // else random
                    } else {
                        // Primary/None: 40% primary, 60% random
                        if (picked < 0.4) {
                            typeOfMove = pkmn.primaryType;
                        }
                        // else random
                    }
                }

                // select a list to pick a move from that has at least one free
                List<Move> pickList = validMoves;
                if (attemptDamaging) {
                    if (typeOfMove != null) {
                        if (validTypeDamagingMoves.containsKey(typeOfMove)
                                && checkForUnusedMove(validTypeDamagingMoves.get(typeOfMove), learnt)) {
                            pickList = validTypeDamagingMoves.get(typeOfMove);
                        } else if (checkForUnusedMove(validDamagingMoves, learnt)) {
                            pickList = validDamagingMoves;
                        }
                    } else if (checkForUnusedMove(validDamagingMoves, learnt)) {
                        pickList = validDamagingMoves;
                    }
                    MoveCategory forcedCategory = random.nextDouble() < atkSpAtkRatio ? MoveCategory.PHYSICAL : MoveCategory.SPECIAL;
                    List<Move> filteredList = pickList.stream().filter(mv -> mv.category == forcedCategory).collect(Collectors.toList());
                    if (!filteredList.isEmpty() && checkForUnusedMove(filteredList, learnt)) {
                        pickList = filteredList;
                    }
                } else if (typeOfMove != null) {
                    if (validTypeMoves.containsKey(typeOfMove)
                            && checkForUnusedMove(validTypeMoves.get(typeOfMove), learnt)) {
                        pickList = validTypeMoves.get(typeOfMove);
                    }
                }

                // now pick a move until we get a valid one
                Move mv = pickList.get(random.nextInt(pickList.size()));
                while (learnt.contains(mv.number)) {
                    mv = pickList.get(random.nextInt(pickList.size()));
                }

                if (i == lv1index) {
                    lv1AttackingMove = mv.number;
                } else {
                    goodDamagingLeft--;
                }
                learnt.add(mv.number);

            }

            Collections.shuffle(learnt, random);
            if (learnt.get(lv1index) != lv1AttackingMove) {
                for (int i = 0; i < learnt.size(); i++) {
                    if (learnt.get(i) == lv1AttackingMove) {
                        learnt.set(i, learnt.get(lv1index));
                        learnt.set(lv1index, lv1AttackingMove);
                        break;
                    }
                }
            }

            // write all moves for the pokemon
            for (int i = 0; i < learnt.size(); i++) {
                moves.get(i).move = learnt.get(i);
                if (i == lv1index) {
                    // just in case, set this to lv1
                    moves.get(i).level = 2; // TODO: Set this back to 1 later
                }
            }
        }

        // Done, save
        this.setMovesLearnt(movesets);
    }

    @Override
    public void randomizeEggMoves(Settings settings) {
        boolean typeThemed = settings.getMovesetsMod() == Settings.MovesetsMod.RANDOM_PREFER_SAME_TYPE;
        boolean noBroken = settings.isBlockBrokenMovesetMoves();
        double goodDamagingPercentage =
                settings.isMovesetsForceGoodDamaging() ? settings.getMovesetsGoodDamagingPercent() / 100.0 : 0;

        // Get current sets
        Map<Integer, List<Integer>> movesets = this.getEggMoves();

        // Build sets of moves
        List<Move> validMoves = new ArrayList<>();
        List<Move> validDamagingMoves = new ArrayList<>();
        Map<Type, List<Move>> validTypeMoves = new HashMap<>();
        Map<Type, List<Move>> validTypeDamagingMoves = new HashMap<>();
        createSetsOfMoves(settings, noBroken, validMoves, validDamagingMoves, validTypeMoves, validTypeDamagingMoves);

        for (Integer pkmnNum : movesets.keySet()) {
            List<Integer> learnt = new ArrayList<>();
            List<Integer> moves = movesets.get(pkmnNum);
            Pokemon pkmn = findPokemonInPoolWithSpeciesID(mainPokemonListInclFormes, pkmnNum);
            if (pkmn == null) {
                continue;
            }

            double atkSpAtkRatio = pkmn.getAttackSpecialAttackRatio();

            if (pkmn.actuallyCosmetic) {
                for (int i = 0; i < moves.size(); i++) {
                    moves.set(i, movesets.get(pkmn.baseForme.number).get(i));
                }
                continue;
            }

            // Force a certain amount of good damaging moves depending on the percentage
            int goodDamagingLeft = (int) Math.round(goodDamagingPercentage * moves.size());

            // Replace moves as needed
            for (int i = 0; i < moves.size(); i++) {
                // should this move be forced damaging?
                boolean attemptDamaging = goodDamagingLeft > 0;

                // type themed?
                Type typeOfMove = null;
                if (typeThemed) {
                    double picked = random.nextDouble();
                    if ((pkmn.primaryType == Type.NORMAL && pkmn.secondaryType != null) ||
                            (pkmn.secondaryType == Type.NORMAL)) {

                        Type otherType = pkmn.primaryType == Type.NORMAL ? pkmn.secondaryType : pkmn.primaryType;

                        // Normal/OTHER: 10% normal, 30% other, 60% random
                        if (picked < 0.1) {
                            typeOfMove = Type.NORMAL;
                        } else if (picked < 0.4) {
                            typeOfMove = otherType;
                        }
                        // else random
                    } else if (pkmn.secondaryType != null) {
                        // Primary/Secondary: 20% primary, 20% secondary, 60% random
                        if (picked < 0.2) {
                            typeOfMove = pkmn.primaryType;
                        } else if (picked < 0.4) {
                            typeOfMove = pkmn.secondaryType;
                        }
                        // else random
                    } else {
                        // Primary/None: 40% primary, 60% random
                        if (picked < 0.4) {
                            typeOfMove = pkmn.primaryType;
                        }
                        // else random
                    }
                }

                // select a list to pick a move from that has at least one free
                List<Move> pickList = validMoves;
                if (attemptDamaging) {
                    if (typeOfMove != null) {
                        if (validTypeDamagingMoves.containsKey(typeOfMove)
                                && checkForUnusedMove(validTypeDamagingMoves.get(typeOfMove), learnt)) {
                            pickList = validTypeDamagingMoves.get(typeOfMove);
                        } else if (checkForUnusedMove(validDamagingMoves, learnt)) {
                            pickList = validDamagingMoves;
                        }
                    } else if (checkForUnusedMove(validDamagingMoves, learnt)) {
                        pickList = validDamagingMoves;
                    }
                    MoveCategory forcedCategory = random.nextDouble() < atkSpAtkRatio ? MoveCategory.PHYSICAL : MoveCategory.SPECIAL;
                    List<Move> filteredList = pickList.stream().filter(mv -> mv.category == forcedCategory).collect(Collectors.toList());
                    if (!filteredList.isEmpty() && checkForUnusedMove(filteredList, learnt)) {
                        pickList = filteredList;
                    }
                } else if (typeOfMove != null) {
                    if (validTypeMoves.containsKey(typeOfMove)
                            && checkForUnusedMove(validTypeMoves.get(typeOfMove), learnt)) {
                        pickList = validTypeMoves.get(typeOfMove);
                    }
                }

                // now pick a move until we get a valid one
                Move mv = pickList.get(random.nextInt(pickList.size()));
                while (learnt.contains(mv.number)) {
                    mv = pickList.get(random.nextInt(pickList.size()));
                }

                goodDamagingLeft--;
                learnt.add(mv.number);
            }

            // write all moves for the pokemon
            Collections.shuffle(learnt, random);
            for (int i = 0; i < learnt.size(); i++) {
                moves.set(i, learnt.get(i));
            }
        }
        // Done, save
        this.setEggMoves(movesets);
    }

    private void createSetsOfMoves(Settings settings, boolean noBroken, List<Move> validMoves, List<Move> validDamagingMoves,
                                   Map<Type, List<Move>> validTypeMoves, Map<Type, List<Move>> validTypeDamagingMoves) {
        List<Move> allMoves = this.getMoves();
        List<Integer> hms = this.getHMMoves();
        Set<Integer> allBanned = new HashSet<Integer>(noBroken ? this.getGameBreakingMoves() : Collections.EMPTY_SET);
        allBanned.addAll(hms);
        allBanned.addAll(this.getMovesBannedFromLevelup());
        allBanned.addAll(GlobalConstants.zMoves);
        allBanned.addAll(this.getIllegalMoves());

        for (Move mv : allMoves) {
            if (mv == null || mv.number == Moves.struggle)
                continue;

            if (allBanned.contains(mv.number) || isBannedRandomMove(settings, mv))
                continue;

            validMoves.add(mv);
            if (mv.type != null) {
                if (!validTypeMoves.containsKey(mv.type)) {
                    validTypeMoves.put(mv.type, new ArrayList<>());
                }
                validTypeMoves.get(mv.type).add(mv);
            }

            if (!mv.canBeDamagingMove(generationOfPokemon()) || !mv.isGoodDamaging(generationOfPokemon()))
                continue;

            validDamagingMoves.add(mv);
            if (mv.type == null)
                continue;

            if (!validTypeDamagingMoves.containsKey(mv.type)) {
                validTypeDamagingMoves.put(mv.type, new ArrayList<>());
            }
            validTypeDamagingMoves.get(mv.type).add(mv);
        }

        Map<Type, Double> avgTypePowers = new TreeMap<>();
        double totalAvgPower = 0;

        for (Type type : validTypeMoves.keySet()) {
            List<Move> typeMoves = validTypeMoves.get(type);
            int attackingSum = 0;
            for (Move typeMove : typeMoves) {
                if (typeMove.power > 0) {
                    attackingSum += (typeMove.power * typeMove.getHitCount(generationOfPokemon()));
                }
            }
            double avgTypePower = (double) attackingSum / (double) typeMoves.size();
            avgTypePowers.put(type, avgTypePower);
            totalAvgPower += (avgTypePower);
        }

        totalAvgPower /= (double) validTypeMoves.keySet().size();

        // Want the average power of each type to be within 25% both directions
        double minAvg = totalAvgPower * 0.75;
        double maxAvg = totalAvgPower * 1.25;

        // Add extra moves to type lists outside of the range to balance the average power of each type

        for (Type type : avgTypePowers.keySet()) {
            double avgPowerForType = avgTypePowers.get(type);
            List<Move> typeMoves = validTypeMoves.get(type);
            List<Move> alreadyPicked = new ArrayList<>();
            int iterLoops = 0;
            while (avgPowerForType < minAvg && iterLoops < 10000) {
                final double finalAvgPowerForType = avgPowerForType;
                List<Move> strongerThanAvgTypeMoves = typeMoves
                        .stream()
                        .filter(mv -> mv.power * mv.getHitCount(generationOfPokemon()) > finalAvgPowerForType)
                        .collect(Collectors.toList());
                if (strongerThanAvgTypeMoves.isEmpty()) break;
                if (alreadyPicked.containsAll(strongerThanAvgTypeMoves)) {
                    alreadyPicked = new ArrayList<>();
                } else {
                    strongerThanAvgTypeMoves.removeAll(alreadyPicked);
                }
                Move extraMove = strongerThanAvgTypeMoves.get(random.nextInt(strongerThanAvgTypeMoves.size()));
                avgPowerForType = (avgPowerForType * typeMoves.size() + extraMove.power * extraMove.getHitCount(generationOfPokemon()))
                        / (typeMoves.size() + 1);
                typeMoves.add(extraMove);
                alreadyPicked.add(extraMove);
                iterLoops++;
            }
            iterLoops = 0;
            while (avgPowerForType > maxAvg && iterLoops < 10000) {
                final double finalAvgPowerForType = avgPowerForType;
                List<Move> weakerThanAvgTypeMoves = typeMoves
                        .stream()
                        .filter(mv -> mv.power * mv.getHitCount(generationOfPokemon()) < finalAvgPowerForType)
                        .collect(Collectors.toList());
                if (weakerThanAvgTypeMoves.isEmpty()) break;
                if (alreadyPicked.containsAll(weakerThanAvgTypeMoves)) {
                    alreadyPicked = new ArrayList<>();
                } else {
                    weakerThanAvgTypeMoves.removeAll(alreadyPicked);
                }
                Move extraMove = weakerThanAvgTypeMoves.get(random.nextInt(weakerThanAvgTypeMoves.size()));
                avgPowerForType = (avgPowerForType * typeMoves.size() + extraMove.power * extraMove.getHitCount(generationOfPokemon()))
                        / (typeMoves.size() + 1);
                typeMoves.add(extraMove);
                alreadyPicked.add(extraMove);
                iterLoops++;
            }
        }
    }

    @Override
    public void orderDamagingMovesByDamage() {
        Map<Integer, List<MoveLearnt>> movesets = this.getMovesLearnt();
        List<Move> allMoves = this.getMoves();
        for (Integer pkmn : movesets.keySet()) {
            List<MoveLearnt> moves = movesets.get(pkmn);

            // Build up a list of damaging moves and their positions
            List<Integer> damagingMoveIndices = new ArrayList<>();
            List<Move> damagingMoves = new ArrayList<>();
            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).level == 0) continue; // Don't reorder evolution move
                Move mv = allMoves.get(moves.get(i).move);
                if (mv != null && mv.power > 1) {
                    // considered a damaging move for this purpose
                    damagingMoveIndices.add(i);
                    damagingMoves.add(mv);
                }
            }

            // Ties should be sorted randomly, so shuffle the list first.
            Collections.shuffle(damagingMoves, random);

            // Sort the damaging moves by power
            damagingMoves.sort(Comparator.comparingDouble(m -> m.power * m.getHitCount(generationOfPokemon())));

            // Reassign damaging moves in the ordered positions
            for (int i = 0; i < damagingMoves.size(); i++) {
                moves.get(damagingMoveIndices.get(i)).move = damagingMoves.get(i).number;
            }
        }

        // Done, save
        this.setMovesLearnt(movesets);
    }

    @Override
    public void metronomeOnlyMode(Settings settings) {
        // movesets
        Map<Integer, List<MoveLearnt>> movesets = this.getMovesLearnt();

        MoveLearnt metronomeML = new MoveLearnt();
        metronomeML.level = 1;
        metronomeML.move = Moves.metronome;

        for (List<MoveLearnt> ms : movesets.values()) {
            if (ms != null && ms.size() > 0) {
                ms.clear();
                ms.add(metronomeML);
            }
        }

        this.setMovesLearnt(movesets);

        // trainers
        // run this to remove all custom non-Metronome moves
        List<Trainer> trainers = this.getTrainers();

        for (Trainer t : trainers) {
            for (TrainerPokemon tpk : t.pokemon) {
                tpk.resetMoves = true;
            }
        }

        this.setTrainers(trainers, false, false);

        // tms
        List<Integer> tmMoves = this.getTMMoves();

        for (int i = 0; i < tmMoves.size(); i++) {
            tmMoves.set(i, Moves.metronome);
        }

        this.setTMMoves(settings, tmMoves);

        // movetutors
        if (this.hasMoveTutors()) {
            List<Integer> mtMoves = this.getMoveTutorMoves();

            for (int i = 0; i < mtMoves.size(); i++) {
                mtMoves.set(i, Moves.metronome);
            }

            this.setMoveTutorMoves(mtMoves);
        }

        // move tweaks
        List<Move> moveData = this.getMoves();

        Move metronome = moveData.get(Moves.metronome);

        metronome.pp = 40;

        List<Integer> hms = this.getHMMoves();

        for (int hm : hms) {
            Move thisHM = moveData.get(hm);
            thisHM.pp = 0;
        }
    }

    @Override
    public void customStarters(Settings settings) {
        boolean abilitiesUnchanged = settings.getAbilitiesMod() == Settings.AbilitiesMod.UNCHANGED;
        int[] customStarters = settings.getCustomStarters();
        boolean allowAltFormes = settings.isAllowStarterAltFormes();
        boolean banIrregularAltFormes = settings.isBanIrregularAltFormes();

        List<Pokemon> romPokemon = getPokemonInclFormes()
                .stream()
                .filter(pk -> pk == null || !pk.actuallyCosmetic)
                .collect(Collectors.toList());

        List<Pokemon> banned = getBannedFormesForPlayerPokemon();
        pickedStarters = new ArrayList<>();
        if (abilitiesUnchanged) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        if (banIrregularAltFormes) {
            banned.addAll(getIrregularFormes());
        }
        // loop to add chosen pokemon to banned, preventing it from being a random option.
        for (int i = 0; i < customStarters.length; i = i + 1) {
            if (!(customStarters[i] - 1 == 0)) {
                banned.add(romPokemon.get(customStarters[i] - 1));
            }
        }
        if (customStarters[0] - 1 == 0) {
            Pokemon pkmn = allowAltFormes ? randomPokemonInclFormes() : randomPokemon();
            while (pickedStarters.contains(pkmn) || banned.contains(pkmn) || pkmn.actuallyCosmetic) {
                pkmn = allowAltFormes ? randomPokemonInclFormes() : randomPokemon();
            }
            pickedStarters.add(pkmn);
        } else {
            Pokemon pkmn1 = romPokemon.get(customStarters[0] - 1);
            pickedStarters.add(pkmn1);
        }
        if (customStarters[1] - 1 == 0) {
            Pokemon pkmn = allowAltFormes ? randomPokemonInclFormes() : randomPokemon();
            while (pickedStarters.contains(pkmn) || banned.contains(pkmn) || pkmn.actuallyCosmetic) {
                pkmn = allowAltFormes ? randomPokemonInclFormes() : randomPokemon();
            }
            pickedStarters.add(pkmn);
        } else {
            Pokemon pkmn2 = romPokemon.get(customStarters[1] - 1);
            pickedStarters.add(pkmn2);
        }

        if (isYellow()) {
            setStarters(pickedStarters);
        } else {
            if (customStarters[2] - 1 == 0) {
                Pokemon pkmn = allowAltFormes ? randomPokemonInclFormes() : randomPokemon();
                while (pickedStarters.contains(pkmn) || banned.contains(pkmn) || pkmn.actuallyCosmetic) {
                    pkmn = allowAltFormes ? randomPokemonInclFormes() : randomPokemon();
                }
                pickedStarters.add(pkmn);
            } else {
                Pokemon pkmn3 = romPokemon.get(customStarters[2] - 1);
                pickedStarters.add(pkmn3);
            }
            if (starterCount() > 3) {
                for (int i = 3; i < starterCount(); i++) {
                    Pokemon pkmn = random2EvosPokemon(allowAltFormes);
                    while (pickedStarters.contains(pkmn)) {
                        pkmn = random2EvosPokemon(allowAltFormes);
                    }
                    pickedStarters.add(pkmn);
                }
                setStarters(pickedStarters);
            } else {
                setStarters(pickedStarters);
            }
        }
    }

    @Override
    public void randomizeStarters(Settings settings) {
        boolean abilitiesUnchanged = settings.getAbilitiesMod() == Settings.AbilitiesMod.UNCHANGED;
        boolean allowAltFormes = settings.isAllowStarterAltFormes();
        boolean banIrregularAltFormes = settings.isBanIrregularAltFormes();

        int starterCount = starterCount();
        pickedStarters = new ArrayList<>();
        List<Pokemon> banned = getBannedFormesForPlayerPokemon();
        if (abilitiesUnchanged) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        if (banIrregularAltFormes) {
            banned.addAll(getIrregularFormes());
        }
        for (int i = 0; i < starterCount; i++) {
            Pokemon pkmn = allowAltFormes ? randomPokemonInclFormes() : randomPokemon();
            while (pickedStarters.contains(pkmn) || banned.contains(pkmn) || pkmn.actuallyCosmetic) {
                pkmn = allowAltFormes ? randomPokemonInclFormes() : randomPokemon();
            }
            pickedStarters.add(pkmn);
        }
        setStarters(pickedStarters);
    }

    @Override
    public void randomizeBasicTwoEvosStarters(Settings settings) {
        boolean abilitiesUnchanged = settings.getAbilitiesMod() == Settings.AbilitiesMod.UNCHANGED;
        boolean allowAltFormes = settings.isAllowStarterAltFormes();
        boolean banIrregularAltFormes = settings.isBanIrregularAltFormes();

        int starterCount = starterCount();
        pickedStarters = new ArrayList<>();
        List<Pokemon> banned = getBannedFormesForPlayerPokemon();
        if (abilitiesUnchanged) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        if (banIrregularAltFormes) {
            banned.addAll(getIrregularFormes());
        }
        for (int i = 0; i < starterCount; i++) {
            Pokemon pkmn = random2EvosPokemon(allowAltFormes);
            while (pickedStarters.contains(pkmn) || banned.contains(pkmn)) {
                pkmn = random2EvosPokemon(allowAltFormes);
            }
            pickedStarters.add(pkmn);
        }
        setStarters(pickedStarters);
    }

    @Override
    public void randomizeFullyEvolvedStarters(Settings settings) {
        boolean abilitiesUnchanged = settings.getAbilitiesMod() == Settings.AbilitiesMod.UNCHANGED;
        boolean allowAltFormes = settings.isAllowStarterAltFormes();
        boolean banIrregularAltFormes = settings.isBanIrregularAltFormes();

        int starterCount = starterCount();
        pickedStarters = new ArrayList<>();
        List<Pokemon> banned = getBannedFormesForPlayerPokemon();
        if (abilitiesUnchanged) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        if (banIrregularAltFormes) {
            banned.addAll(getIrregularFormes());
        }
        for (int i = 0; i < starterCount; i++) {
            Pokemon pkmn = randomFullyEvolvedPokemon(allowAltFormes);
            while (pickedStarters.contains(pkmn) || banned.contains(pkmn)) {
                pkmn = randomFullyEvolvedPokemon(allowAltFormes);
            }
            pickedStarters.add(pkmn);
        }
        setStarters(pickedStarters);
    }

    @Override
    public List<Pokemon> getPickedStarters() {
        return pickedStarters;
    }


    @Override
    public void randomizeStaticPokemon(Settings settings) {
        boolean swapLegendaries = settings.getStaticPokemonMod() == Settings.StaticPokemonMod.RANDOM_MATCHING;
        boolean similarStrength = settings.getStaticPokemonMod() == Settings.StaticPokemonMod.SIMILAR_STRENGTH;
        boolean limitMainGameLegendaries = settings.isLimitMainGameLegendaries();
        boolean limit600 = settings.isLimit600();
        boolean allowAltFormes = settings.isAllowStaticAltFormes();
        boolean banIrregularAltFormes = settings.isBanIrregularAltFormes();
        boolean swapMegaEvos = settings.isSwapStaticMegaEvos();
        boolean abilitiesAreRandomized = settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE;
        int levelModifier = settings.isStaticLevelModified() ? settings.getStaticLevelModifier() : 0;
        boolean correctStaticMusic = settings.isCorrectStaticMusic();

        // Load
        checkPokemonRestrictions();
        List<StaticEncounter> currentStaticPokemon = this.getStaticPokemon();
        List<StaticEncounter> replacements = new ArrayList<>();
        List<Pokemon> banned = this.bannedForStaticPokemon();
        banned.addAll(this.getBannedFormesForPlayerPokemon());
        if (!abilitiesAreRandomized) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        if (banIrregularAltFormes) {
            banned.addAll(getIrregularFormes());
        }
        boolean reallySwapMegaEvos = forceSwapStaticMegaEvos() || swapMegaEvos;

        Map<Integer, Integer> specialMusicStaticChanges = new HashMap<>();
        List<Integer> changeMusicStatics = new ArrayList<>();
        if (correctStaticMusic) {
            changeMusicStatics = getSpecialMusicStatics();
        }

        if (swapLegendaries) {
            List<Pokemon> legendariesLeft = new ArrayList<>(onlyLegendaryList);
            if (allowAltFormes) {
                legendariesLeft.addAll(onlyLegendaryAltsList);
                legendariesLeft =
                        legendariesLeft
                                .stream()
                                .filter(pk -> !pk.actuallyCosmetic)
                                .collect(Collectors.toList());
            }
            List<Pokemon> nonlegsLeft = new ArrayList<>(noLegendaryList);
            if (allowAltFormes) {
                nonlegsLeft.addAll(noLegendaryAltsList);
                nonlegsLeft =
                        nonlegsLeft
                                .stream()
                                .filter(pk -> !pk.actuallyCosmetic)
                                .collect(Collectors.toList());
            }
            List<Pokemon> ultraBeastsLeft = new ArrayList<>(ultraBeastList);
            legendariesLeft.removeAll(banned);
            nonlegsLeft.removeAll(banned);
            ultraBeastsLeft.removeAll(banned);

            // Full pools for easier refilling later
            List<Pokemon> legendariesPool = new ArrayList<>(legendariesLeft);
            List<Pokemon> nonlegsPool = new ArrayList<>(nonlegsLeft);
            List<Pokemon> ultraBeastsPool = new ArrayList<>(ultraBeastsLeft);

            for (StaticEncounter old : currentStaticPokemon) {
                StaticEncounter newStatic = cloneStaticEncounter(old);
                Pokemon newPK;
                if (old.pkmn.isLegendary()) {
                    if (reallySwapMegaEvos && old.canMegaEvolve()) {
                        newPK = getMegaEvoPokemon(onlyLegendaryList, legendariesLeft, newStatic);
                    } else {
                        if (old.restrictedPool) {
                            newPK = getRestrictedPokemon(legendariesPool, legendariesLeft, old);
                        } else {
                            newPK = legendariesLeft.remove(this.random.nextInt(legendariesLeft.size()));
                        }
                    }

                    setPokemonAndFormeForStaticEncounter(newStatic, newPK);

                    if (legendariesLeft.size() == 0) {
                        legendariesLeft.addAll(legendariesPool);
                    }
                } else if (ultraBeastList.contains(old.pkmn)) {
                    if (old.restrictedPool) {
                        newPK = getRestrictedPokemon(ultraBeastsPool, ultraBeastsLeft, old);
                    } else {
                        newPK = ultraBeastsLeft.remove(this.random.nextInt(ultraBeastsLeft.size()));
                    }

                    setPokemonAndFormeForStaticEncounter(newStatic, newPK);

                    if (ultraBeastsLeft.size() == 0) {
                        ultraBeastsLeft.addAll(ultraBeastsPool);
                    }
                } else {
                    if (reallySwapMegaEvos && old.canMegaEvolve()) {
                        newPK = getMegaEvoPokemon(noLegendaryList, nonlegsLeft, newStatic);
                    } else {
                        if (old.restrictedPool) {
                            newPK = getRestrictedPokemon(nonlegsPool, nonlegsLeft, old);
                        } else {
                            newPK = nonlegsLeft.remove(this.random.nextInt(nonlegsLeft.size()));
                        }
                    }
                    setPokemonAndFormeForStaticEncounter(newStatic, newPK);

                    if (nonlegsLeft.size() == 0) {
                        nonlegsLeft.addAll(nonlegsPool);
                    }
                }
                replacements.add(newStatic);
                if (changeMusicStatics.contains(old.pkmn.number)) {
                    specialMusicStaticChanges.put(old.pkmn.number, newPK.number);
                }
            }
        } else if (similarStrength) {
            List<Pokemon> listInclFormesExclCosmetics =
                    mainPokemonListInclFormes
                            .stream()
                            .filter(pk -> !pk.actuallyCosmetic)
                            .collect(Collectors.toList());
            List<Pokemon> pokemonLeft = new ArrayList<>(!allowAltFormes ? mainPokemonList : listInclFormesExclCosmetics);
            pokemonLeft.removeAll(banned);

            List<Pokemon> pokemonPool = new ArrayList<>(pokemonLeft);

            List<Integer> mainGameLegendaries = getMainGameLegendaries();
            for (StaticEncounter old : currentStaticPokemon) {
                StaticEncounter newStatic = cloneStaticEncounter(old);
                Pokemon newPK;
                Pokemon oldPK = old.pkmn;
                if (old.forme > 0) {
                    oldPK = getAltFormeOfPokemon(oldPK, old.forme);
                }
                Integer oldBST = oldPK.bstForPowerLevels();
                if (oldBST >= 600 && limit600) {
                    if (reallySwapMegaEvos && old.canMegaEvolve()) {
                        newPK = getMegaEvoPokemon(mainPokemonList, pokemonLeft, newStatic);
                    } else {
                        if (old.restrictedPool) {
                            newPK = getRestrictedPokemon(pokemonPool, pokemonLeft, old);
                        } else {
                            newPK = pokemonLeft.remove(this.random.nextInt(pokemonLeft.size()));
                        }
                    }
                    setPokemonAndFormeForStaticEncounter(newStatic, newPK);
                } else {
                    boolean limitBST = oldPK.baseForme == null ?
                            limitMainGameLegendaries && mainGameLegendaries.contains(oldPK.number) :
                            limitMainGameLegendaries && mainGameLegendaries.contains(oldPK.baseForme.number);
                    if (reallySwapMegaEvos && old.canMegaEvolve()) {
                        List<Pokemon> megaEvoPokemonLeft =
                                megaEvolutionsList
                                        .stream()
                                        .filter(mega -> mega.method == 1)
                                        .map(mega -> mega.from)
                                        .distinct()
                                        .filter(pokemonLeft::contains)
                                        .collect(Collectors.toList());
                        if (megaEvoPokemonLeft.isEmpty()) {
                            megaEvoPokemonLeft =
                                    megaEvolutionsList
                                            .stream()
                                            .filter(mega -> mega.method == 1)
                                            .map(mega -> mega.from)
                                            .distinct()
                                            .filter(mainPokemonList::contains)
                                            .collect(Collectors.toList());
                        }
                        newPK = pickStaticPowerLvlReplacement(
                                megaEvoPokemonLeft,
                                oldPK,
                                true,
                                limitBST);
                        newStatic.heldItem = newPK
                                .megaEvolutionsFrom
                                .get(this.random.nextInt(newPK.megaEvolutionsFrom.size()))
                                .argument;
                    } else {
                        if (old.restrictedPool) {
                            List<Pokemon> restrictedPool = pokemonLeft
                                    .stream()
                                    .filter(pk -> old.restrictedList.contains(pk))
                                    .collect(Collectors.toList());
                            if (restrictedPool.isEmpty()) {
                                restrictedPool = pokemonPool
                                        .stream()
                                        .filter(pk -> old.restrictedList.contains(pk))
                                        .collect(Collectors.toList());
                            }
                            newPK = pickStaticPowerLvlReplacement(
                                    restrictedPool,
                                    oldPK,
                                    false, // Allow same Pokemon just in case
                                    limitBST);
                        } else {
                            newPK = pickStaticPowerLvlReplacement(
                                    pokemonLeft,
                                    oldPK,
                                    true,
                                    limitBST);
                        }
                    }
                    pokemonLeft.remove(newPK);
                    setPokemonAndFormeForStaticEncounter(newStatic, newPK);
                }

                if (pokemonLeft.size() == 0) {
                    pokemonLeft.addAll(pokemonPool);
                }
                replacements.add(newStatic);
                if (changeMusicStatics.contains(old.pkmn.number)) {
                    specialMusicStaticChanges.put(old.pkmn.number, newPK.number);
                }
            }
        } else { // Completely random
            List<Pokemon> listInclFormesExclCosmetics =
                    mainPokemonListInclFormes
                            .stream()
                            .filter(pk -> !pk.actuallyCosmetic)
                            .collect(Collectors.toList());
            List<Pokemon> pokemonLeft = new ArrayList<>(!allowAltFormes ? mainPokemonList : listInclFormesExclCosmetics);
            pokemonLeft.removeAll(banned);

            List<Pokemon> pokemonPool = new ArrayList<>(pokemonLeft);

            for (StaticEncounter old : currentStaticPokemon) {
                StaticEncounter newStatic = cloneStaticEncounter(old);
                Pokemon newPK;
                if (reallySwapMegaEvos && old.canMegaEvolve()) {
                    newPK = getMegaEvoPokemon(mainPokemonList, pokemonLeft, newStatic);
                } else {
                    if (old.restrictedPool) {
                        newPK = getRestrictedPokemon(pokemonPool, pokemonLeft, old);
                    } else {
                        newPK = pokemonLeft.remove(this.random.nextInt(pokemonLeft.size()));
                    }
                }
                pokemonLeft.remove(newPK);
                setPokemonAndFormeForStaticEncounter(newStatic, newPK);
                if (pokemonLeft.size() == 0) {
                    pokemonLeft.addAll(pokemonPool);
                }
                replacements.add(newStatic);
                if (changeMusicStatics.contains(old.pkmn.number)) {
                    specialMusicStaticChanges.put(old.pkmn.number, newPK.number);
                }
            }
        }

        if (levelModifier != 0) {
            for (StaticEncounter se : replacements) {
                if (!se.isEgg) {
                    se.level = Math.min(100, (int) Math.round(se.level * (1 + levelModifier / 100.0)));
                    se.maxLevel = Math.min(100, (int) Math.round(se.maxLevel * (1 + levelModifier / 100.0)));
                    for (StaticEncounter linkedStatic : se.linkedEncounters) {
                        if (!linkedStatic.isEgg) {
                            linkedStatic.level = Math.min(100, (int) Math.round(linkedStatic.level * (1 + levelModifier / 100.0)));
                            linkedStatic.maxLevel = Math.min(100, (int) Math.round(linkedStatic.maxLevel * (1 + levelModifier / 100.0)));
                        }
                    }
                }
            }
        }

        if (specialMusicStaticChanges.size() > 0) {
            applyCorrectStaticMusic(specialMusicStaticChanges);
        }

        // Save
        this.setStaticPokemon(replacements);
    }

    private Pokemon getRestrictedPokemon(List<Pokemon> fullList, List<Pokemon> pokemonLeft, StaticEncounter old) {
        Pokemon newPK;
        List<Pokemon> restrictedPool = pokemonLeft.stream().filter(pk -> old.restrictedList.contains(pk)).collect(Collectors.toList());
        if (restrictedPool.isEmpty()) {
            restrictedPool = fullList
                    .stream()
                    .filter(pk -> old.restrictedList.contains(pk))
                    .collect(Collectors.toList());
        }
        newPK = restrictedPool.remove(this.random.nextInt(restrictedPool.size()));
        pokemonLeft.remove(newPK);
        return newPK;
    }

    @Override
    public void onlyChangeStaticLevels(Settings settings) {
        int levelModifier = settings.getStaticLevelModifier();

        List<StaticEncounter> currentStaticPokemon = this.getStaticPokemon();
        for (StaticEncounter se : currentStaticPokemon) {
            if (!se.isEgg) {
                se.level = Math.min(100, (int) Math.round(se.level * (1 + levelModifier / 100.0)));
                for (StaticEncounter linkedStatic : se.linkedEncounters) {
                    if (!linkedStatic.isEgg) {
                        linkedStatic.level = Math.min(100, (int) Math.round(linkedStatic.level * (1 + levelModifier / 100.0)));
                    }
                }
            }
            setPokemonAndFormeForStaticEncounter(se, se.pkmn);
        }
        this.setStaticPokemon(currentStaticPokemon);
    }

    private StaticEncounter cloneStaticEncounter(StaticEncounter old) {
        StaticEncounter newStatic = new StaticEncounter();
        newStatic.pkmn = old.pkmn;
        newStatic.level = old.level;
        newStatic.maxLevel = old.maxLevel;
        newStatic.heldItem = old.heldItem;
        newStatic.isEgg = old.isEgg;
        newStatic.resetMoves = true;
        for (StaticEncounter oldLinked : old.linkedEncounters) {
            StaticEncounter newLinked = new StaticEncounter();
            newLinked.pkmn = oldLinked.pkmn;
            newLinked.level = oldLinked.level;
            newLinked.maxLevel = oldLinked.maxLevel;
            newLinked.heldItem = oldLinked.heldItem;
            newLinked.isEgg = oldLinked.isEgg;
            newLinked.resetMoves = true;
            newStatic.linkedEncounters.add(newLinked);
        }
        return newStatic;
    }

    private void setPokemonAndFormeForStaticEncounter(StaticEncounter newStatic, Pokemon pk) {
        boolean checkCosmetics = true;
        Pokemon newPK = pk;
        int newForme = 0;
        if (pk.formeNumber > 0) {
            newForme = pk.formeNumber;
            newPK = pk.baseForme;
            checkCosmetics = false;
        }
        if (checkCosmetics && pk.cosmeticForms > 0) {
            newForme = pk.getCosmeticFormNumber(this.random.nextInt(pk.cosmeticForms));
        } else if (!checkCosmetics && pk.cosmeticForms > 0) {
            newForme += pk.getCosmeticFormNumber(this.random.nextInt(pk.cosmeticForms));
        }
        newStatic.pkmn = newPK;
        newStatic.forme = newForme;
        for (StaticEncounter linked : newStatic.linkedEncounters) {
            linked.pkmn = newPK;
            linked.forme = newForme;
        }
    }

    private void setFormeForStaticEncounter(StaticEncounter newStatic, Pokemon pk) {
        boolean checkCosmetics = true;
        newStatic.forme = 0;
        if (pk.formeNumber > 0) {
            newStatic.forme = pk.formeNumber;
            newStatic.pkmn = pk.baseForme;
            checkCosmetics = false;
        }
        if (checkCosmetics && newStatic.pkmn.cosmeticForms > 0) {
            newStatic.forme = newStatic.pkmn.getCosmeticFormNumber(this.random.nextInt(newStatic.pkmn.cosmeticForms));
        } else if (!checkCosmetics && pk.cosmeticForms > 0) {
            newStatic.forme += pk.getCosmeticFormNumber(this.random.nextInt(pk.cosmeticForms));
        }
    }

    private Pokemon getMegaEvoPokemon(List<Pokemon> fullList, List<Pokemon> pokemonLeft, StaticEncounter newStatic) {
        List<MegaEvolution> megaEvos = megaEvolutionsList;
        List<Pokemon> megaEvoPokemon =
                megaEvos
                        .stream()
                        .filter(mega -> mega.method == 1)
                        .map(mega -> mega.from)
                        .distinct()
                        .collect(Collectors.toList());
        Pokemon newPK;
        List<Pokemon> megaEvoPokemonLeft =
                megaEvoPokemon
                        .stream()
                        .filter(pokemonLeft::contains)
                        .collect(Collectors.toList());
        if (megaEvoPokemonLeft.isEmpty()) {
            megaEvoPokemonLeft = megaEvoPokemon
                    .stream()
                    .filter(fullList::contains)
                    .collect(Collectors.toList());
        }
        newPK = megaEvoPokemonLeft.remove(this.random.nextInt(megaEvoPokemonLeft.size()));
        pokemonLeft.remove(newPK);
        newStatic.heldItem = newPK
                .megaEvolutionsFrom
                .get(this.random.nextInt(newPK.megaEvolutionsFrom.size()))
                .argument;
        return newPK;
    }

    @Override
    public void randomizeTMMoves(Settings settings) {
        boolean noBroken = settings.isBlockBrokenTMMoves();
        boolean preserveField = settings.isKeepFieldMoveTMs();
        double goodDamagingPercentage = settings.isTmsForceGoodDamaging() ? settings.getTmsGoodDamagingPercent() / 100.0 : 0;

        // Pick some random TM moves.
        int tmCount = this.getTMCount();
        List<Move> allMoves = this.getMoves();
        List<Integer> hms = this.getHMMoves();
        List<Integer> oldTMs = this.getTMMoves();
        @SuppressWarnings("unchecked")
        List<Integer> banned = new ArrayList<Integer>(noBroken ? this.getGameBreakingMoves() : Collections.EMPTY_LIST);
        banned.addAll(getMovesBannedFromLevelup());
        banned.addAll(this.getIllegalMoves());
        banned.addAll(GlobalConstants.tmBannedMoves);
        banned.addAll(GlobalConstants.bannedMoves);
        // field moves?
        List<Integer> fieldMoves = this.getFieldMoves();
        int preservedFieldMoveCount = 0;

        if (preserveField) {
            List<Integer> banExistingField = new ArrayList<>(oldTMs);
            banExistingField.retainAll(fieldMoves);
            preservedFieldMoveCount = banExistingField.size();
            banned.addAll(banExistingField);
        }

        // Determine which moves are pickable
        List<Move> usableMoves = new ArrayList<>(allMoves);
        usableMoves.remove(0); // remove null entry
        Set<Move> unusableMoves = new HashSet<>();
        Set<Move> unusableDamagingMoves = new HashSet<>();

        for (Move mv : usableMoves) {
            if (isBannedRandomMove(settings, mv) || GlobalConstants.zMoves.contains(mv.number) ||
                    hms.contains(mv.number) || banned.contains(mv.number)) {
                unusableMoves.add(mv);
            } else if (!mv.canBeDamagingMove(generationOfPokemon()) || !mv.isGoodDamaging(generationOfPokemon())) {
                unusableDamagingMoves.add(mv);
            }
        }

        usableMoves.removeAll(unusableMoves);
        List<Move> usableDamagingMoves = new ArrayList<>(usableMoves);
        usableDamagingMoves.removeAll(unusableDamagingMoves);

        // pick (tmCount - preservedFieldMoveCount) moves
        List<Integer> pickedMoves = new ArrayList<>();

        // Force a certain amount of good damaging moves depending on the percentage
        int goodDamagingLeft = (int) Math.round(goodDamagingPercentage * (tmCount - preservedFieldMoveCount));

        for (int i = 0; i < tmCount - preservedFieldMoveCount; i++) {
            Move chosenMove;
            if (goodDamagingLeft > 0 && usableDamagingMoves.size() > 0) {
                chosenMove = usableDamagingMoves.get(random.nextInt(usableDamagingMoves.size()));
            } else {
                chosenMove = usableMoves.get(random.nextInt(usableMoves.size()));
            }
            pickedMoves.add(chosenMove.number);
            usableMoves.remove(chosenMove);
            usableDamagingMoves.remove(chosenMove);
            goodDamagingLeft--;
        }

        // shuffle the picked moves because high goodDamagingPercentage
        // will bias them towards early numbers otherwise

        Collections.shuffle(pickedMoves, random);

        // finally, distribute them as tms
        int pickedMoveIndex = 0;
        List<Integer> newTMs = new ArrayList<>();

        for (int i = 0; i < tmCount; i++) {
            if (preserveField && fieldMoves.contains(oldTMs.get(i))) {
                newTMs.add(oldTMs.get(i));
            } else {
                newTMs.add(pickedMoves.get(pickedMoveIndex++));
            }
        }

        this.setTMMoves(settings, newTMs);
    }

    @Override
    public void randomizeTMHMCompatibility(Settings settings) {
        boolean preferSameType = settings.getTmsHmsCompatibilityMod() == Settings.TMsHMsCompatibilityMod.RANDOM_PREFER_TYPE;
        boolean followEvolutions = settings.isTmsFollowEvolutions();

        // Get current compatibility
        // increase HM chances if required early on
        List<Integer> requiredEarlyOn = this.getEarlyRequiredHMMoves();
        Map<Pokemon, boolean[]> compat = this.getTMHMCompatibility();
        List<Integer> tmHMs = new ArrayList<>(this.getTMMoves());
        tmHMs.addAll(this.getHMMoves());

        if (followEvolutions) {
            copyUpEvolutionsHelper(pk -> randomizePokemonMoveCompatibility(
                            pk, compat.get(pk), tmHMs, requiredEarlyOn, preferSameType),
                    (evFrom, evTo, toMonIsFinalEvo) -> copyPokemonMoveCompatibilityUpEvolutions(
                            evFrom, evTo, compat.get(evFrom), compat.get(evTo), tmHMs, preferSameType
                    ), null, true);
        } else {
            for (Map.Entry<Pokemon, boolean[]> compatEntry : compat.entrySet()) {
                randomizePokemonMoveCompatibility(compatEntry.getKey(), compatEntry.getValue(), tmHMs,
                        requiredEarlyOn, preferSameType);
            }
        }

        // Set the new compatibility
        this.setTMHMCompatibility(compat);
    }

    private void randomizePokemonMoveCompatibility(Pokemon pkmn, boolean[] moveCompatibilityFlags,
                                                   List<Integer> moveIDs, List<Integer> prioritizedMoves,
                                                   boolean preferSameType) {
        List<Move> moveData = this.getMoves();
        for (int i = 1; i <= moveIDs.size(); i++) {
            int move = moveIDs.get(i - 1);
            Move mv = moveData.get(move);
            double probability = getMoveCompatibilityProbability(
                    pkmn,
                    mv,
                    prioritizedMoves.contains(move),
                    preferSameType
            );
            moveCompatibilityFlags[i] = (this.random.nextDouble() < probability);
        }
    }

    private void copyPokemonMoveCompatibilityUpEvolutions(Pokemon evFrom, Pokemon evTo, boolean[] prevCompatibilityFlags,
                                                          boolean[] toCompatibilityFlags, List<Integer> moveIDs,
                                                          boolean preferSameType) {
        List<Move> moveData = this.getMoves();
        for (int i = 1; i <= moveIDs.size(); i++) {
            if (!prevCompatibilityFlags[i]) {
                // Slight chance to gain TM/HM compatibility for a move if not learned by an earlier evolution step
                // Without prefer same type: 25% chance
                // With prefer same type:    10% chance, 90% chance for a type new to this evolution
                int move = moveIDs.get(i - 1);
                Move mv = moveData.get(move);
                double probability = 0.25;
                if (preferSameType) {
                    probability = 0.1;
                    if (evTo.primaryType.equals(mv.type)
                            && !evTo.primaryType.equals(evFrom.primaryType) && !evTo.primaryType.equals(evFrom.secondaryType)
                            || evTo.secondaryType != null && evTo.secondaryType.equals(mv.type)
                            && !evTo.secondaryType.equals(evFrom.secondaryType) && !evTo.secondaryType.equals(evFrom.primaryType)) {
                        probability = 0.9;
                    }
                }
                toCompatibilityFlags[i] = (this.random.nextDouble() < probability);
            } else {
                toCompatibilityFlags[i] = prevCompatibilityFlags[i];
            }
        }
    }

    private double getMoveCompatibilityProbability(Pokemon pkmn, Move mv, boolean requiredEarlyOn,
                                                   boolean preferSameType) {
        double probability = 0.5;
        if (preferSameType) {
            if (pkmn.primaryType.equals(mv.type)
                    || (pkmn.secondaryType != null && pkmn.secondaryType.equals(mv.type))) {
                probability = 0.9;
            } else if (mv.type != null && mv.type.equals(Type.NORMAL)) {
                probability = 0.5;
            } else {
                probability = 0.25;
            }
        }
        if (requiredEarlyOn) {
            probability = Math.min(1.0, probability * 1.8);
        }
        return probability;
    }

    @Override
    public void fullTMHMCompatibility() {
        Map<Pokemon, boolean[]> compat = this.getTMHMCompatibility();
        for (Map.Entry<Pokemon, boolean[]> compatEntry : compat.entrySet()) {
            boolean[] flags = compatEntry.getValue();
            for (int i = 1; i < flags.length; i++) {
                flags[i] = true;
            }
        }
        this.setTMHMCompatibility(compat);
    }

    @Override
    public void ensureTMCompatSanity() {
        // if a pokemon learns a move in its moveset
        // and there is a TM of that move, make sure
        // that TM can be learned.
        Map<Pokemon, boolean[]> compat = this.getTMHMCompatibility();
        Map<Integer, List<MoveLearnt>> movesets = this.getMovesLearnt();
        List<Integer> tmMoves = this.getTMMoves();
        for (Pokemon pkmn : compat.keySet()) {
            List<MoveLearnt> moveset = movesets.get(pkmn.number);
            boolean[] pkmnCompat = compat.get(pkmn);
            for (MoveLearnt ml : moveset) {
                if (tmMoves.contains(ml.move)) {
                    int tmIndex = tmMoves.indexOf(ml.move);
                    pkmnCompat[tmIndex + 1] = true;
                }
            }
        }
        this.setTMHMCompatibility(compat);
    }

    @Override
    public void ensureTMEvolutionSanity() {
        Map<Pokemon, boolean[]> compat = this.getTMHMCompatibility();
        // Don't do anything with the base, just copy upwards to ensure later evolutions retain learn compatibility
        copyUpEvolutionsHelper(pk -> {
        }, ((evFrom, evTo, toMonIsFinalEvo) -> {
            boolean[] fromCompat = compat.get(evFrom);
            boolean[] toCompat = compat.get(evTo);
            for (int i = 1; i < toCompat.length; i++) {
                toCompat[i] |= fromCompat[i];
            }
        }), null, true);
        this.setTMHMCompatibility(compat);
    }

    @Override
    public void fullHMCompatibility() {
        Map<Pokemon, boolean[]> compat = this.getTMHMCompatibility();
        int tmCount = this.getTMCount();
        for (boolean[] flags : compat.values()) {
            for (int i = tmCount + 1; i < flags.length; i++) {
                flags[i] = true;
            }
        }

        // Set the new compatibility
        this.setTMHMCompatibility(compat);
    }

    @Override
    public void copyTMCompatibilityToCosmeticFormes() {
        Map<Pokemon, boolean[]> compat = this.getTMHMCompatibility();

        for (Map.Entry<Pokemon, boolean[]> compatEntry : compat.entrySet()) {
            Pokemon pkmn = compatEntry.getKey();
            boolean[] flags = compatEntry.getValue();
            if (pkmn.actuallyCosmetic) {
                boolean[] baseFlags = compat.get(pkmn.baseForme);
                for (int i = 1; i < flags.length; i++) {
                    flags[i] = baseFlags[i];
                }
            }
        }

        this.setTMHMCompatibility(compat);
    }

    @Override
    public void randomizeMoveTutorMoves(Settings settings) {
        boolean noBroken = settings.isBlockBrokenTutorMoves();
        boolean preserveField = settings.isKeepFieldMoveTutors();
        double goodDamagingPercentage = settings.isTutorsForceGoodDamaging() ? settings.getTutorsGoodDamagingPercent() / 100.0 : 0;

        if (!this.hasMoveTutors()) {
            return;
        }

        // Pick some random Move Tutor moves, excluding TMs.
        List<Move> allMoves = this.getMoves();
        List<Integer> tms = this.getTMMoves();
        List<Integer> oldMTs = this.getMoveTutorMoves();
        int mtCount = oldMTs.size();
        List<Integer> hms = this.getHMMoves();
        @SuppressWarnings("unchecked")
        List<Integer> banned = new ArrayList<Integer>(noBroken ? this.getGameBreakingMoves() : Collections.EMPTY_LIST);
        banned.addAll(getMovesBannedFromLevelup());
        banned.addAll(this.getIllegalMoves());

        // field moves?
        List<Integer> fieldMoves = this.getFieldMoves();
        int preservedFieldMoveCount = 0;
        if (preserveField) {
            List<Integer> banExistingField = new ArrayList<>(oldMTs);
            banExistingField.retainAll(fieldMoves);
            preservedFieldMoveCount = banExistingField.size();
            banned.addAll(banExistingField);
        }

        // Determine which moves are pickable
        List<Move> usableMoves = new ArrayList<>(allMoves);
        usableMoves.remove(0); // remove null entry
        Set<Move> unusableMoves = new HashSet<>();
        Set<Move> unusableDamagingMoves = new HashSet<>();

        for (Move mv : usableMoves) {
            if (isBannedRandomMove(settings, mv) || tms.contains(mv.number) || hms.contains(mv.number)
                    || banned.contains(mv.number) || GlobalConstants.zMoves.contains(mv.number)) {
                unusableMoves.add(mv);
            } else if (!mv.canBeDamagingMove(generationOfPokemon()) || !mv.isGoodDamaging(generationOfPokemon())) {
                unusableDamagingMoves.add(mv);
            }
        }

        usableMoves.removeAll(unusableMoves);
        List<Move> usableDamagingMoves = new ArrayList<>(usableMoves);
        usableDamagingMoves.removeAll(unusableDamagingMoves);

        // pick (tmCount - preservedFieldMoveCount) moves
        List<Integer> pickedMoves = new ArrayList<>();

        // Force a certain amount of good damaging moves depending on the percentage
        int goodDamagingLeft = (int) Math.round(goodDamagingPercentage * (mtCount - preservedFieldMoveCount));

        for (int i = 0; i < mtCount - preservedFieldMoveCount; i++) {
            Move chosenMove;
            if (goodDamagingLeft > 0 && usableDamagingMoves.size() > 0) {
                chosenMove = usableDamagingMoves.get(random.nextInt(usableDamagingMoves.size()));
            } else {
                chosenMove = usableMoves.get(random.nextInt(usableMoves.size()));
            }
            pickedMoves.add(chosenMove.number);
            usableMoves.remove(chosenMove);
            usableDamagingMoves.remove(chosenMove);
            goodDamagingLeft--;
        }

        // shuffle the picked moves because high goodDamagingPercentage
        // will bias them towards early numbers otherwise

        Collections.shuffle(pickedMoves, random);

        // finally, distribute them as tutors
        int pickedMoveIndex = 0;
        List<Integer> newMTs = new ArrayList<>();

        for (Integer oldMT : oldMTs) {
            if (preserveField && fieldMoves.contains(oldMT)) {
                newMTs.add(oldMT);
            } else {
                newMTs.add(pickedMoves.get(pickedMoveIndex++));
            }
        }

        this.setMoveTutorMoves(newMTs);
    }

    private boolean isBannedRandomMove(Settings settings, Move mv) {
        if ((settings.isNoCounter() && mv.isCounterMove()))
            return true;
        if (settings.isNoSelfDestruct() && mv.isExplosionMove())
            return true;
        if (settings.isNoRechargeMoves() && mv.isRechargeMove)
            return true;
        if (settings.isNoDirectDamageMoves() && mv.isDirectDamageMove())
            return true;
        if (settings.isNoMetronome() && mv.number == Moves.metronome)
            return true;
        if (settings.isNoMagnitude() && mv.number == Moves.magnitude)
            return true;
        if (settings.isNoOHKOMoves() && mv.isOHKOMove())
            return true;

        return false;
    }

    @Override
    public void randomizeMoveTutorCompatibility(Settings settings) {
        boolean preferSameType = settings.getMoveTutorsCompatibilityMod() == Settings.MoveTutorsCompatibilityMod.RANDOM_PREFER_TYPE;
        boolean followEvolutions = settings.isTutorFollowEvolutions();

        if (!this.hasMoveTutors()) {
            return;
        }
        // Get current compatibility
        Map<Pokemon, boolean[]> compat = this.getMoveTutorCompatibility();
        List<Integer> mts = this.getMoveTutorMoves();

        // Empty list
        List<Integer> priorityTutors = new ArrayList<Integer>();

        if (followEvolutions) {
            copyUpEvolutionsHelper(pk -> randomizePokemonMoveCompatibility(
                            pk, compat.get(pk), mts, priorityTutors, preferSameType),
                    (evFrom, evTo, toMonIsFinalEvo) -> copyPokemonMoveCompatibilityUpEvolutions(
                            evFrom, evTo, compat.get(evFrom), compat.get(evTo), mts, preferSameType
                    ), null, true);
        } else {
            for (Map.Entry<Pokemon, boolean[]> compatEntry : compat.entrySet()) {
                randomizePokemonMoveCompatibility(compatEntry.getKey(), compatEntry.getValue(), mts, priorityTutors, preferSameType);
            }
        }

        // Set the new compatibility
        this.setMoveTutorCompatibility(compat);
    }

    @Override
    public void fullMoveTutorCompatibility() {
        if (!this.hasMoveTutors()) {
            return;
        }
        Map<Pokemon, boolean[]> compat = this.getMoveTutorCompatibility();
        for (Map.Entry<Pokemon, boolean[]> compatEntry : compat.entrySet()) {
            boolean[] flags = compatEntry.getValue();
            for (int i = 1; i < flags.length; i++) {
                flags[i] = true;
            }
        }
        this.setMoveTutorCompatibility(compat);
    }

    @Override
    public void ensureMoveTutorCompatSanity() {
        if (!this.hasMoveTutors()) {
            return;
        }
        // if a pokemon learns a move in its moveset
        // and there is a tutor of that move, make sure
        // that tutor can be learned.
        Map<Pokemon, boolean[]> compat = this.getMoveTutorCompatibility();
        Map<Integer, List<MoveLearnt>> movesets = this.getMovesLearnt();
        List<Integer> mtMoves = this.getMoveTutorMoves();
        for (Pokemon pkmn : compat.keySet()) {
            List<MoveLearnt> moveset = movesets.get(pkmn.number);
            boolean[] pkmnCompat = compat.get(pkmn);
            for (MoveLearnt ml : moveset) {
                if (mtMoves.contains(ml.move)) {
                    int mtIndex = mtMoves.indexOf(ml.move);
                    pkmnCompat[mtIndex + 1] = true;
                }
            }
        }
        this.setMoveTutorCompatibility(compat);
    }

    @Override
    public void ensureMoveTutorEvolutionSanity() {
        if (!this.hasMoveTutors()) {
            return;
        }
        Map<Pokemon, boolean[]> compat = this.getMoveTutorCompatibility();
        // Don't do anything with the base, just copy upwards to ensure later evolutions retain learn compatibility
        copyUpEvolutionsHelper(pk -> {
        }, ((evFrom, evTo, toMonIsFinalEvo) -> {
            boolean[] fromCompat = compat.get(evFrom);
            boolean[] toCompat = compat.get(evTo);
            for (int i = 1; i < toCompat.length; i++) {
                toCompat[i] |= fromCompat[i];
            }
        }), null, true);
        this.setMoveTutorCompatibility(compat);
    }

    @Override
    public void copyMoveTutorCompatibilityToCosmeticFormes() {
        Map<Pokemon, boolean[]> compat = this.getMoveTutorCompatibility();

        for (Map.Entry<Pokemon, boolean[]> compatEntry : compat.entrySet()) {
            Pokemon pkmn = compatEntry.getKey();
            boolean[] flags = compatEntry.getValue();
            if (pkmn.actuallyCosmetic) {
                boolean[] baseFlags = compat.get(pkmn.baseForme);
                for (int i = 1; i < flags.length; i++) {
                    flags[i] = baseFlags[i];
                }
            }
        }

        this.setMoveTutorCompatibility(compat);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void randomizeTrainerNames(Settings settings) {
        CustomNamesSet customNames = settings.getCustomNames();

        if (!this.canChangeTrainerText()) {
            return;
        }

        // index 0 = singles, 1 = doubles
        List<String>[] allTrainerNames = new List[]{new ArrayList<String>(), new ArrayList<String>()};
        Map<Integer, List<String>> trainerNamesByLength[] = new Map[]{new TreeMap<Integer, List<String>>(),
                new TreeMap<Integer, List<String>>()};

        List<String> repeatedTrainerNames = Arrays.asList(new String[]{"GRUNT", "EXECUTIVE", "SHADOW", "ADMIN", "GOON", "EMPLOYEE"});

        // Read name lists
        for (String trainername : customNames.getTrainerNames()) {
            int len = this.internalStringLength(trainername);
            if (len <= 10) {
                allTrainerNames[0].add(trainername);
                if (trainerNamesByLength[0].containsKey(len)) {
                    trainerNamesByLength[0].get(len).add(trainername);
                } else {
                    List<String> namesOfThisLength = new ArrayList<>();
                    namesOfThisLength.add(trainername);
                    trainerNamesByLength[0].put(len, namesOfThisLength);
                }
            }
        }

        for (String trainername : customNames.getDoublesTrainerNames()) {
            int len = this.internalStringLength(trainername);
            if (len <= 10) {
                allTrainerNames[1].add(trainername);
                if (trainerNamesByLength[1].containsKey(len)) {
                    trainerNamesByLength[1].get(len).add(trainername);
                } else {
                    List<String> namesOfThisLength = new ArrayList<>();
                    namesOfThisLength.add(trainername);
                    trainerNamesByLength[1].put(len, namesOfThisLength);
                }
            }
        }

        // Get the current trainer names data
        List<String> currentTrainerNames = this.getTrainerNames();
        if (currentTrainerNames.size() == 0) {
            // RBY have no trainer names
            return;
        }
        TrainerNameMode mode = this.trainerNameMode();
        int maxLength = this.maxTrainerNameLength();
        int totalMaxLength = this.maxSumOfTrainerNameLengths();

        boolean success = false;
        int tries = 0;

        // Init the translation map and new list
        Map<String, String> translation = new HashMap<>();
        List<String> newTrainerNames = new ArrayList<>();
        List<Integer> tcNameLengths = this.getTCNameLengthsByTrainer();

        // loop until we successfully pick names that fit
        // should always succeed first attempt except for gen2.
        while (!success && tries < 10000) {
            success = true;
            translation.clear();
            newTrainerNames.clear();
            int totalLength = 0;

            // Start choosing
            int tnIndex = -1;
            for (String trainerName : currentTrainerNames) {
                tnIndex++;
                if (translation.containsKey(trainerName) && !repeatedTrainerNames.contains(trainerName.toUpperCase())) {
                    // use an already picked translation
                    newTrainerNames.add(translation.get(trainerName));
                    totalLength += this.internalStringLength(translation.get(trainerName));
                } else {
                    int idx = trainerName.contains("&") ? 1 : 0;
                    List<String> pickFrom = allTrainerNames[idx];
                    int intStrLen = this.internalStringLength(trainerName);
                    if (mode == TrainerNameMode.SAME_LENGTH) {
                        pickFrom = trainerNamesByLength[idx].get(intStrLen);
                    }
                    String changeTo = trainerName;
                    int ctl = intStrLen;
                    if (pickFrom != null && pickFrom.size() > 0 && intStrLen > 0) {
                        int innerTries = 0;
                        changeTo = pickFrom.get(this.cosmeticRandom.nextInt(pickFrom.size()));
                        ctl = this.internalStringLength(changeTo);
                        while ((mode == TrainerNameMode.MAX_LENGTH && ctl > maxLength)
                                || (mode == TrainerNameMode.MAX_LENGTH_WITH_CLASS && ctl + tcNameLengths.get(tnIndex) > maxLength)) {
                            innerTries++;
                            if (innerTries == 100) {
                                changeTo = trainerName;
                                ctl = intStrLen;
                                break;
                            }
                            changeTo = pickFrom.get(this.cosmeticRandom.nextInt(pickFrom.size()));
                            ctl = this.internalStringLength(changeTo);
                        }
                    }
                    translation.put(trainerName, changeTo);
                    newTrainerNames.add(changeTo);
                    totalLength += ctl;
                }

                if (totalLength > totalMaxLength) {
                    success = false;
                    tries++;
                    break;
                }
            }
        }

        if (!success) {
            throw new RandomizationException("Could not randomize trainer names in a reasonable amount of attempts."
                    + "\nPlease add some shorter names to your custom trainer names.");
        }

        // Done choosing, save
        this.setTrainerNames(newTrainerNames);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void randomizeTrainerClassNames(Settings settings) {
        CustomNamesSet customNames = settings.getCustomNames();

        if (!this.canChangeTrainerText()) {
            return;
        }

        // index 0 = singles, index 1 = doubles
        List<String> allTrainerClasses[] = new List[]{new ArrayList<String>(), new ArrayList<String>()};
        Map<Integer, List<String>> trainerClassesByLength[] = new Map[]{new HashMap<Integer, List<String>>(),
                new HashMap<Integer, List<String>>()};

        // Read names data
        for (String trainerClassName : customNames.getTrainerClasses()) {
            allTrainerClasses[0].add(trainerClassName);
            int len = this.internalStringLength(trainerClassName);
            if (trainerClassesByLength[0].containsKey(len)) {
                trainerClassesByLength[0].get(len).add(trainerClassName);
            } else {
                List<String> namesOfThisLength = new ArrayList<>();
                namesOfThisLength.add(trainerClassName);
                trainerClassesByLength[0].put(len, namesOfThisLength);
            }
        }

        for (String trainerClassName : customNames.getDoublesTrainerClasses()) {
            allTrainerClasses[1].add(trainerClassName);
            int len = this.internalStringLength(trainerClassName);
            if (trainerClassesByLength[1].containsKey(len)) {
                trainerClassesByLength[1].get(len).add(trainerClassName);
            } else {
                List<String> namesOfThisLength = new ArrayList<>();
                namesOfThisLength.add(trainerClassName);
                trainerClassesByLength[1].put(len, namesOfThisLength);
            }
        }

        // Get the current trainer names data
        List<String> currentClassNames = this.getTrainerClassNames();
        boolean mustBeSameLength = this.fixedTrainerClassNamesLength();
        int maxLength = this.maxTrainerClassNameLength();

        // Init the translation map and new list
        Map<String, String> translation = new HashMap<>();
        List<String> newClassNames = new ArrayList<>();

        int numTrainerClasses = currentClassNames.size();
        List<Integer> doublesClasses = this.getDoublesTrainerClasses();

        // Start choosing
        for (int i = 0; i < numTrainerClasses; i++) {
            String trainerClassName = currentClassNames.get(i);
            if (translation.containsKey(trainerClassName)) {
                // use an already picked translation
                newClassNames.add(translation.get(trainerClassName));
            } else {
                int idx = doublesClasses.contains(i) ? 1 : 0;
                List<String> pickFrom = allTrainerClasses[idx];
                int intStrLen = this.internalStringLength(trainerClassName);
                if (mustBeSameLength) {
                    pickFrom = trainerClassesByLength[idx].get(intStrLen);
                }
                String changeTo = trainerClassName;
                if (pickFrom != null && pickFrom.size() > 0) {
                    changeTo = pickFrom.get(this.cosmeticRandom.nextInt(pickFrom.size()));
                    while (changeTo.length() > maxLength) {
                        changeTo = pickFrom.get(this.cosmeticRandom.nextInt(pickFrom.size()));
                    }
                }
                translation.put(trainerClassName, changeTo);
                newClassNames.add(changeTo);
            }
        }

        // Done choosing, save
        this.setTrainerClassNames(newClassNames);
    }

    @Override
    public void randomizeWildHeldItems(Settings settings) {
        boolean banBadItems = settings.isBanBadRandomWildPokemonHeldItems();

        List<Pokemon> pokemon = allPokemonInclFormesWithoutNull();
        ItemList possibleItems = banBadItems ? this.getNonBadItems() : this.getAllowedItems();
        for (Pokemon pk : pokemon) {
            if (pk.guaranteedHeldItem == -1 && pk.commonHeldItem == -1 && pk.rareHeldItem == -1
                    && pk.darkGrassHeldItem == -1) {
                // No held items at all, abort
                return;
            }
            boolean canHaveDarkGrass = pk.darkGrassHeldItem != -1;
            if (pk.guaranteedHeldItem != -1) {
                // Guaranteed held items are supported.
                if (pk.guaranteedHeldItem > 0) {
                    // Currently have a guaranteed item
                    double decision = this.random.nextDouble();
                    if (decision < 0.9) {
                        // Stay as guaranteed
                        canHaveDarkGrass = false;
                        pk.guaranteedHeldItem = possibleItems.randomItem(this.random);
                    } else {
                        // Change to 25% or 55% chance
                        pk.guaranteedHeldItem = 0;
                        pk.commonHeldItem = possibleItems.randomItem(this.random);
                        pk.rareHeldItem = possibleItems.randomItem(this.random);
                        while (pk.rareHeldItem == pk.commonHeldItem) {
                            pk.rareHeldItem = possibleItems.randomItem(this.random);
                        }
                    }
                } else {
                    // No guaranteed item atm
                    double decision = this.random.nextDouble();
                    if (decision < 0.5) {
                        // No held item at all
                        pk.commonHeldItem = 0;
                        pk.rareHeldItem = 0;
                    } else if (decision < 0.65) {
                        // Just a rare item
                        pk.commonHeldItem = 0;
                        pk.rareHeldItem = possibleItems.randomItem(this.random);
                    } else if (decision < 0.8) {
                        // Just a common item
                        pk.commonHeldItem = possibleItems.randomItem(this.random);
                        pk.rareHeldItem = 0;
                    } else if (decision < 0.95) {
                        // Both a common and rare item
                        pk.commonHeldItem = possibleItems.randomItem(this.random);
                        pk.rareHeldItem = possibleItems.randomItem(this.random);
                        while (pk.rareHeldItem == pk.commonHeldItem) {
                            pk.rareHeldItem = possibleItems.randomItem(this.random);
                        }
                    } else {
                        // Guaranteed item
                        canHaveDarkGrass = false;
                        pk.guaranteedHeldItem = possibleItems.randomItem(this.random);
                        pk.commonHeldItem = 0;
                        pk.rareHeldItem = 0;
                    }
                }
            } else {
                // Code for no guaranteed items
                double decision = this.random.nextDouble();
                if (decision < 0.5) {
                    // No held item at all
                    pk.commonHeldItem = 0;
                    pk.rareHeldItem = 0;
                } else if (decision < 0.65) {
                    // Just a rare item
                    pk.commonHeldItem = 0;
                    pk.rareHeldItem = possibleItems.randomItem(this.random);
                } else if (decision < 0.8) {
                    // Just a common item
                    pk.commonHeldItem = possibleItems.randomItem(this.random);
                    pk.rareHeldItem = 0;
                } else {
                    // Both a common and rare item
                    pk.commonHeldItem = possibleItems.randomItem(this.random);
                    pk.rareHeldItem = possibleItems.randomItem(this.random);
                    while (pk.rareHeldItem == pk.commonHeldItem) {
                        pk.rareHeldItem = possibleItems.randomItem(this.random);
                    }
                }
            }

            if (canHaveDarkGrass) {
                double dgDecision = this.random.nextDouble();
                if (dgDecision < 0.5) {
                    // Yes, dark grass item
                    pk.darkGrassHeldItem = possibleItems.randomItem(this.random);
                } else {
                    pk.darkGrassHeldItem = 0;
                }
            } else if (pk.darkGrassHeldItem != -1) {
                pk.darkGrassHeldItem = 0;
            }
        }

    }

    @Override
    public void randomizeStarterHeldItems(Settings settings) {
        boolean banBadItems = settings.isBanBadRandomStarterHeldItems();

        List<Integer> oldHeldItems = this.getStarterHeldItems();
        List<Integer> newHeldItems = new ArrayList<>();
        ItemList possibleItems = banBadItems ? this.getNonBadItems() : this.getAllowedItems();
        for (int i = 0; i < oldHeldItems.size(); i++) {
            newHeldItems.add(possibleItems.randomItem(this.random));
        }
        this.setStarterHeldItems(newHeldItems);
    }

    @Override
    public void shuffleFieldItems() {
        List<Integer> currentItems = this.getRegularFieldItems();
        List<Integer> currentTMs = this.getCurrentFieldTMs();

        Collections.shuffle(currentItems, this.random);
        Collections.shuffle(currentTMs, this.random);

        this.setRegularFieldItems(currentItems);
        this.setFieldTMs(currentTMs);
    }

    @Override
    public void randomizeFieldItems(Settings settings) {
        boolean banBadItems = settings.isBanBadRandomFieldItems();
        boolean distributeItemsControl = settings.getFieldItemsMod() == Settings.FieldItemsMod.RANDOM_EVEN;
        boolean uniqueItems = !settings.isBalanceShopPrices();

        ItemList possibleItems = banBadItems ? this.getNonBadItems().copy() : this.getAllowedItems().copy();
        List<Integer> currentItems = this.getRegularFieldItems();
        List<Integer> currentTMs = this.getCurrentFieldTMs();
        List<Integer> requiredTMs = this.getRequiredFieldTMs();
        List<Integer> uniqueNoSellItems = this.getUniqueNoSellItems();
        // System.out.println("distributeItemsControl: "+ distributeItemsControl);

        int fieldItemCount = currentItems.size();
        int fieldTMCount = currentTMs.size();
        int reqTMCount = requiredTMs.size();
        int totalTMCount = this.getTMCount();

        List<Integer> newItems = new ArrayList<>();
        List<Integer> newTMs = new ArrayList<>(requiredTMs);

        // List<Integer> chosenItems = new ArrayList<Integer>(); // collecting chosenItems for later process

        if (distributeItemsControl) {
            for (int i = 0; i < fieldItemCount; i++) {
                int chosenItem = possibleItems.randomNonTM(this.random);
                int iterNum = 0;
                while ((this.getItemPlacementHistory(chosenItem) > this.getItemPlacementAverage()) && iterNum < 100) {
                    chosenItem = possibleItems.randomNonTM(this.random);
                    iterNum += 1;
                }
                newItems.add(chosenItem);
                if (uniqueItems && uniqueNoSellItems.contains(chosenItem)) {
                    possibleItems.banSingles(chosenItem);
                } else {
                    this.setItemPlacementHistory(chosenItem);
                }
            }
        } else {
            for (int i = 0; i < fieldItemCount; i++) {
                int chosenItem = possibleItems.randomNonTM(this.random);
                newItems.add(chosenItem);
                if (uniqueItems && uniqueNoSellItems.contains(chosenItem)) {
                    possibleItems.banSingles(chosenItem);
                }
            }
        }

        for (int i = reqTMCount; i < fieldTMCount; i++) {
            while (true) {
                int tm = this.random.nextInt(totalTMCount) + 1;
                if (!newTMs.contains(tm)) {
                    newTMs.add(tm);
                    break;
                }
            }
        }


        Collections.shuffle(newItems, this.random);
        Collections.shuffle(newTMs, this.random);

        this.setRegularFieldItems(newItems);
        this.setFieldTMs(newTMs);
    }

    @Override
    public void randomizeIngameTrades(Settings settings) {
        boolean randomizeRequest = settings.getInGameTradesMod() == Settings.InGameTradesMod.RANDOMIZE_GIVEN_AND_REQUESTED;
        boolean randomNickname = settings.isRandomizeInGameTradesNicknames();
        boolean randomOT = settings.isRandomizeInGameTradesOTs();
        boolean randomStats = settings.isRandomizeInGameTradesIVs();
        boolean randomItem = settings.isRandomizeInGameTradesItems();
        CustomNamesSet customNames = settings.getCustomNames();

        checkPokemonRestrictions();
        // Process trainer names
        List<String> trainerNames = new ArrayList<>();
        // Check for the file
        if (randomOT) {
            int maxOT = this.maxTradeOTNameLength();
            for (String trainername : customNames.getTrainerNames()) {
                int len = this.internalStringLength(trainername);
                if (len <= maxOT && !trainerNames.contains(trainername)) {
                    trainerNames.add(trainername);
                }
            }
        }

        // Process nicknames
        List<String> nicknames = new ArrayList<>();
        // Check for the file
        if (randomNickname) {
            int maxNN = this.maxTradeNicknameLength();
            for (String nickname : customNames.getPokemonNicknames()) {
                int len = this.internalStringLength(nickname);
                if (len <= maxNN && !nicknames.contains(nickname)) {
                    nicknames.add(nickname);
                }
            }
        }

        // get old trades
        List<IngameTrade> trades = this.getIngameTrades();
        List<Pokemon> usedRequests = new ArrayList<>();
        List<Pokemon> usedGivens = new ArrayList<>();
        List<String> usedOTs = new ArrayList<>();
        List<String> usedNicknames = new ArrayList<>();
        ItemList possibleItems = this.getAllowedItems();

        int nickCount = nicknames.size();
        int trnameCount = trainerNames.size();

        for (IngameTrade trade : trades) {
            // pick new given pokemon
            Pokemon oldgiven = trade.givenPokemon;
            Pokemon given = this.randomPokemon();
            while (usedGivens.contains(given)) {
                given = this.randomPokemon();
            }
            usedGivens.add(given);
            trade.givenPokemon = given;

            // requested pokemon?
            if (oldgiven == trade.requestedPokemon) {
                // preserve trades for the same pokemon
                trade.requestedPokemon = given;
            } else if (randomizeRequest) {
                if (trade.requestedPokemon != null) {
                    Pokemon request = this.randomPokemon();
                    while (usedRequests.contains(request) || request == given) {
                        request = this.randomPokemon();
                    }
                    usedRequests.add(request);
                    trade.requestedPokemon = request;
                }
            }

            // nickname?
            if (randomNickname && nickCount > usedNicknames.size()) {
                String nickname = nicknames.get(this.random.nextInt(nickCount));
                while (usedNicknames.contains(nickname)) {
                    nickname = nicknames.get(this.random.nextInt(nickCount));
                }
                usedNicknames.add(nickname);
                trade.nickname = nickname;
            } else if (trade.nickname.equalsIgnoreCase(oldgiven.name)) {
                // change the name for sanity
                trade.nickname = trade.givenPokemon.name;
            }

            if (randomOT && trnameCount > usedOTs.size()) {
                String ot = trainerNames.get(this.random.nextInt(trnameCount));
                while (usedOTs.contains(ot)) {
                    ot = trainerNames.get(this.random.nextInt(trnameCount));
                }
                usedOTs.add(ot);
                trade.otName = ot;
                trade.otId = this.random.nextInt(65536);
            }

            if (randomStats) {
                int maxIV = this.hasDVs() ? 16 : 32;
                for (int i = 0; i < trade.ivs.length; i++) {
                    trade.ivs[i] = this.random.nextInt(maxIV);
                }
            }

            if (randomItem) {
                trade.item = possibleItems.randomItem(this.random);
            }
        }

        // things that the game doesn't support should just be ignored
        this.setIngameTrades(trades);
    }

    @Override
    public void condenseLevelEvolutions() {
        List<Pokemon> allPokemon = this.getPokemon();
        // search for level evolutions
        for (Pokemon pk : allPokemon) {
            if (pk != null) {
                for (Evolution checkEvo : pk.evolutionsFrom) {
                    if (checkEvo.type.usesLevel()) {
                        checkEvo.extraInfo = Math.min(checkEvo.extraInfo, (int) Math.round(pk.bst() / 8.0) - 32);
                        addEvoUpdateCondensed(easierEvolutionUpdates, checkEvo, false);
                    }
                    if (checkEvo.type == EvolutionType.LEVEL_UPSIDE_DOWN) {
                        checkEvo.type = EvolutionType.LEVEL;
                        addEvoUpdateCondensed(easierEvolutionUpdates, checkEvo, false);
                    }
                }
            }
        }

    }

    @Override
    public Set<EvolutionUpdate> getImpossibleEvoUpdates() {
        return impossibleEvolutionUpdates;
    }

    @Override
    public Set<EvolutionUpdate> getEasierEvoUpdates() {
        return easierEvolutionUpdates;
    }

    @Override
    public Set<EvolutionUpdate> getTimeBasedEvoUpdates() {
        return timeBasedEvolutionUpdates;
    }

    @Override
    public void randomizeEvolutions(Settings settings) {
        boolean similarStrength = settings.isEvosSimilarStrength();
        boolean sameType = settings.isEvosSameTyping();
        boolean limitToThreeStages = settings.isEvosMaxThreeStages();
        boolean forceChange = settings.isEvosForceChange();
        boolean allowAltFormes = settings.isEvosAllowAltFormes();
        boolean banIrregularAltFormes = settings.isBanIrregularAltFormes();
        boolean abilitiesAreRandomized = settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE;

        checkPokemonRestrictions();
        List<Pokemon> pokemonPool;
        if (this.altFormesCanHaveDifferentEvolutions()) {
            pokemonPool = new ArrayList<>(mainPokemonListInclFormes);
        } else {
            pokemonPool = new ArrayList<>(mainPokemonList);
        }
        List<Pokemon> actuallyCosmeticPokemonPool = new ArrayList<>();
        int stageLimit = limitToThreeStages ? 3 : 10;

        List<Pokemon> banned = this.getBannedFormesForPlayerPokemon();
        if (!abilitiesAreRandomized) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        if (banIrregularAltFormes) {
            banned.addAll(getIrregularFormes());
        }

        for (int i = 0; i < pokemonPool.size(); i++) {
            Pokemon pk = pokemonPool.get(i);
            if (pk.actuallyCosmetic) {
                pokemonPool.remove(pk);
                i--;
                actuallyCosmeticPokemonPool.add(pk);
            }
        }

        // Cache old evolutions for data later
        Map<Pokemon, List<Evolution>> originalEvos = new HashMap<>();
        for (Pokemon pk : pokemonPool) {
            originalEvos.put(pk, new ArrayList<>(pk.evolutionsFrom));
        }

        Set<EvolutionPair> newEvoPairs = new HashSet<>();
        Set<EvolutionPair> oldEvoPairs = new HashSet<>();

        if (forceChange) {
            for (Pokemon pk : pokemonPool) {
                for (Evolution ev : pk.evolutionsFrom) {
                    oldEvoPairs.add(new EvolutionPair(ev.from, ev.to));
                    if (generationOfPokemon() >= 7 && ev.from.number == Species.cosmoem) { // Special case for Cosmoem to add Lunala/Solgaleo since we remove the split evo
                        int oppositeVersionLegendary = ev.to.number == Species.solgaleo ? Species.lunala : Species.solgaleo;
                        Pokemon toPkmn = findPokemonInPoolWithSpeciesID(pokemonPool, oppositeVersionLegendary);
                        if (toPkmn != null) {
                            oldEvoPairs.add(new EvolutionPair(ev.from, toPkmn));
                        }
                    }
                }
            }
        }

        List<Pokemon> replacements = new ArrayList<>();

        int loops = 0;
        while (loops < 1) {
            // Setup for this loop.
            boolean hadError = false;
            for (Pokemon pk : pokemonPool) {
                pk.evolutionsFrom.clear();
                pk.evolutionsTo.clear();
            }
            newEvoPairs.clear();

            // Shuffle pokemon list so the results aren't overly predictable.
            Collections.shuffle(pokemonPool, this.random);

            for (Pokemon fromPK : pokemonPool) {
                List<Evolution> oldEvos = originalEvos.get(fromPK);
                for (Evolution ev : oldEvos) {
                    // Pick a Pokemon as replacement
                    replacements.clear();

                    List<Pokemon> chosenList =
                            allowAltFormes ?
                                    mainPokemonListInclFormes
                                            .stream()
                                            .filter(pk -> !pk.actuallyCosmetic)
                                            .collect(Collectors.toList()) :
                                    mainPokemonList;
                    // Step 1: base filters
                    for (Pokemon pk : chosenList) {
                        // Prevent evolving into oneself (mandatory)
                        if (pk == fromPK) {
                            continue;
                        }

                        // Force same EXP curve (mandatory)
                        if (pk.growthCurve != fromPK.growthCurve) {
                            continue;
                        }

                        // Prevent evolving into banned Pokemon (mandatory)
                        if (banned.contains(pk)) {
                            continue;
                        }

                        EvolutionPair ep = new EvolutionPair(fromPK, pk);
                        // Prevent split evos choosing the same Pokemon
                        // (mandatory)
                        if (newEvoPairs.contains(ep)) {
                            continue;
                        }

                        // Prevent evolving into old thing if flagged
                        if (forceChange && oldEvoPairs.contains(ep)) {
                            continue;
                        }

                        // Prevent evolution that causes cycle (mandatory)
                        if (evoCycleCheck(fromPK, pk)) {
                            continue;
                        }

                        // Prevent evolution that exceeds stage limit
                        Evolution tempEvo = new Evolution(fromPK, pk, false, EvolutionType.NONE, 0);
                        fromPK.evolutionsFrom.add(tempEvo);
                        pk.evolutionsTo.add(tempEvo);
                        boolean exceededLimit = false;

                        Set<Pokemon> related = relatedPokemon(fromPK);

                        for (Pokemon pk2 : related) {
                            int numPreEvos = numPreEvolutions(pk2, stageLimit);
                            if (numPreEvos >= stageLimit) {
                                exceededLimit = true;
                                break;
                            } else if (numPreEvos == stageLimit - 1 && pk2.evolutionsFrom.size() == 0
                                    && originalEvos.get(pk2).size() > 0) {
                                exceededLimit = true;
                                break;
                            }
                        }

                        fromPK.evolutionsFrom.remove(tempEvo);
                        pk.evolutionsTo.remove(tempEvo);

                        if (exceededLimit) {
                            continue;
                        }

                        // Passes everything, add as a candidate.
                        replacements.add(pk);
                    }

                    // If we don't have any candidates after Step 1, severe
                    // failure
                    // exit out of this loop and try again from scratch
                    if (replacements.size() == 0) {
                        hadError = true;
                        break;
                    }

                    // Step 2: filter by type, if needed
                    if (replacements.size() > 1 && sameType) {
                        Set<Pokemon> includeType = new HashSet<>();
                        for (Pokemon pk : replacements) {
                            // Special case for Eevee
                            if (fromPK.number == Species.eevee) {
                                if (pk.primaryType == ev.to.primaryType
                                        || (pk.secondaryType != null) && pk.secondaryType == ev.to.primaryType) {
                                    includeType.add(pk);
                                }
                            } else if (pk.primaryType == fromPK.primaryType
                                    || (fromPK.secondaryType != null && pk.primaryType == fromPK.secondaryType)
                                    || (pk.secondaryType != null && pk.secondaryType == fromPK.primaryType)
                                    || (fromPK.secondaryType != null && pk.secondaryType != null && pk.secondaryType == fromPK.secondaryType)) {
                                includeType.add(pk);
                            }
                        }

                        if (includeType.size() != 0) {
                            replacements.retainAll(includeType);
                        }
                    }

                    if (!alreadyPicked.containsAll(replacements) && !similarStrength) {
                        replacements.removeAll(alreadyPicked);
                    }

                    // Step 3: pick - by similar strength or otherwise
                    Pokemon picked;

                    if (replacements.size() == 1) {
                        // Foregone conclusion.
                        picked = replacements.get(0);
                        alreadyPicked.add(picked);
                    } else if (similarStrength) {
                        picked = pickEvoPowerLvlReplacement(replacements, ev.to);
                        alreadyPicked.add(picked);
                    } else {
                        picked = replacements.get(this.random.nextInt(replacements.size()));
                        alreadyPicked.add(picked);
                    }

                    // Step 4: add it to the new evos pool
                    Evolution newEvo = new Evolution(fromPK, picked, ev.carryStats, ev.type, ev.extraInfo);
                    boolean checkCosmetics = true;
                    if (picked.formeNumber > 0) {
                        newEvo.forme = picked.formeNumber;
                        newEvo.formeSuffix = picked.formeSuffix;
                        checkCosmetics = false;
                    }
                    if (checkCosmetics && newEvo.to.cosmeticForms > 0) {
                        newEvo.forme = newEvo.to.getCosmeticFormNumber(this.random.nextInt(newEvo.to.cosmeticForms));
                    } else if (!checkCosmetics && picked.cosmeticForms > 0) {
                        newEvo.forme += picked.getCosmeticFormNumber(this.random.nextInt(picked.cosmeticForms));
                    }
                    if (newEvo.type == EvolutionType.LEVEL_FEMALE_ESPURR) {
                        newEvo.type = EvolutionType.LEVEL_FEMALE_ONLY;
                    }
                    fromPK.evolutionsFrom.add(newEvo);
                    picked.evolutionsTo.add(newEvo);
                    newEvoPairs.add(new EvolutionPair(fromPK, picked));
                }

                if (hadError) {
                    // No need to check the other Pokemon if we already errored
                    break;
                }
            }

            // If no error, done and return
            if (!hadError) {
                for (Pokemon pk : actuallyCosmeticPokemonPool) {
                    pk.copyBaseFormeEvolutions(pk.baseForme);
                }
                return;
            } else {
                loops++;
            }
        }

        // If we made it out of the loop, we weren't able to randomize evos.
        throw new RandomizationException("Not able to randomize evolutions in a sane amount of retries.");
    }

    @Override
    public void randomizeEvolutionsEveryLevel(Settings settings) {
        boolean sameType = settings.isEvosSameTyping();
        boolean forceChange = settings.isEvosForceChange();
        boolean allowAltFormes = settings.isEvosAllowAltFormes();
        boolean abilitiesAreRandomized = settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE;

        checkPokemonRestrictions();
        List<Pokemon> pokemonPool;
        if (this.altFormesCanHaveDifferentEvolutions()) {
            pokemonPool = new ArrayList<>(mainPokemonListInclFormes);
        } else {
            pokemonPool = new ArrayList<>(mainPokemonList);
        }
        List<Pokemon> actuallyCosmeticPokemonPool = new ArrayList<>();

        List<Pokemon> banned = this.getBannedFormesForPlayerPokemon();
        if (!abilitiesAreRandomized) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }

        for (int i = 0; i < pokemonPool.size(); i++) {
            Pokemon pk = pokemonPool.get(i);
            if (pk.actuallyCosmetic) {
                pokemonPool.remove(pk);
                i--;
                actuallyCosmeticPokemonPool.add(pk);
            }
        }

        Set<EvolutionPair> oldEvoPairs = new HashSet<>();

        if (forceChange) {
            for (Pokemon pk : pokemonPool) {
                for (Evolution ev : pk.evolutionsFrom) {
                    oldEvoPairs.add(new EvolutionPair(ev.from, ev.to));
                    if (generationOfPokemon() >= 7 && ev.from.number == Species.cosmoem) { // Special case for Cosmoem to add Lunala/Solgaleo since we remove the split evo
                        int oppositeVersionLegendary = ev.to.number == Species.solgaleo ? Species.lunala : Species.solgaleo;
                        Pokemon toPkmn = findPokemonInPoolWithSpeciesID(pokemonPool, oppositeVersionLegendary);
                        if (toPkmn != null) {
                            oldEvoPairs.add(new EvolutionPair(ev.from, toPkmn));
                        }
                    }
                }
            }
        }

        List<Pokemon> replacements = new ArrayList<>();

        int loops = 0;
        while (loops < 1) {
            // Setup for this loop.
            boolean hadError = false;
            for (Pokemon pk : pokemonPool) {
                pk.evolutionsFrom.clear();
                pk.evolutionsTo.clear();
            }

            // Shuffle pokemon list so the results aren't overly predictable.
            Collections.shuffle(pokemonPool, this.random);

            for (Pokemon fromPK : pokemonPool) {
                // Pick a Pokemon as replacement
                replacements.clear();

                List<Pokemon> chosenList =
                        allowAltFormes ?
                                mainPokemonListInclFormes
                                        .stream()
                                        .filter(pk -> !pk.actuallyCosmetic)
                                        .collect(Collectors.toList()) :
                                mainPokemonList;
                // Step 1: base filters
                for (Pokemon pk : chosenList) {
                    // Prevent evolving into oneself (mandatory)
                    if (pk == fromPK) {
                        continue;
                    }

                    // Force same EXP curve (mandatory)
                    if (pk.growthCurve != fromPK.growthCurve) {
                        continue;
                    }

                    // Prevent evolving into banned Pokemon (mandatory)
                    if (banned.contains(pk)) {
                        continue;
                    }

                    // Prevent evolving into old thing if flagged
                    EvolutionPair ep = new EvolutionPair(fromPK, pk);
                    if (forceChange && oldEvoPairs.contains(ep)) {
                        continue;
                    }

                    // Passes everything, add as a candidate.
                    replacements.add(pk);
                }

                // If we don't have any candidates after Step 1, severe failure
                // exit out of this loop and try again from scratch
                if (replacements.size() == 0) {
                    hadError = true;
                    break;
                }

                // Step 2: filter by type, if needed
                if (replacements.size() > 1 && sameType) {
                    Set<Pokemon> includeType = new HashSet<>();
                    for (Pokemon pk : replacements) {
                        if (pk.primaryType == fromPK.primaryType
                                || (fromPK.secondaryType != null && pk.primaryType == fromPK.secondaryType)
                                || (pk.secondaryType != null && pk.secondaryType == fromPK.primaryType)
                                || (pk.secondaryType != null && pk.secondaryType == fromPK.secondaryType)) {
                            includeType.add(pk);
                        }
                    }

                    if (includeType.size() != 0) {
                        replacements.retainAll(includeType);
                    }
                }

                // Step 3: pick - by similar strength or otherwise
                Pokemon picked;

                if (replacements.size() == 1) {
                    // Foregone conclusion.
                    picked = replacements.get(0);
                } else {
                    picked = replacements.get(this.random.nextInt(replacements.size()));
                }

                // Step 4: create new level 1 evo and add it to the new evos pool
                Evolution newEvo = new Evolution(fromPK, picked, false, EvolutionType.LEVEL, 1);
                newEvo.level = 1;
                boolean checkCosmetics = true;
                if (picked.formeNumber > 0) {
                    newEvo.forme = picked.formeNumber;
                    newEvo.formeSuffix = picked.formeSuffix;
                    checkCosmetics = false;
                }
                if (checkCosmetics && newEvo.to.cosmeticForms > 0) {
                    newEvo.forme = newEvo.to.getCosmeticFormNumber(this.random.nextInt(newEvo.to.cosmeticForms));
                } else if (!checkCosmetics && picked.cosmeticForms > 0) {
                    newEvo.forme += picked.getCosmeticFormNumber(this.random.nextInt(picked.cosmeticForms));
                }
                fromPK.evolutionsFrom.add(newEvo);
                picked.evolutionsTo.add(newEvo);
            }

            // If no error, done and return
            if (!hadError) {
                for (Pokemon pk : actuallyCosmeticPokemonPool) {
                    pk.copyBaseFormeEvolutions(pk.baseForme);
                }
                return;
            } else {
                loops++;
            }
        }

        // If we made it out of the loop, we weren't able to randomize evos.
        throw new RandomizationException("Not able to randomize evolutions in a sane amount of retries.");
    }

    @Override
    public void changeCatchRates(Settings settings) {
        int minimumCatchRateLevel = settings.getMinimumCatchRateLevel();

        if (minimumCatchRateLevel == 5) {
            enableGuaranteedPokemonCatching();
        } else {
            int normalMin, legendaryMin;
            switch (minimumCatchRateLevel) {
                case 1:
                default:
                    normalMin = 75;
                    legendaryMin = 37;
                    break;
                case 2:
                    normalMin = 128;
                    legendaryMin = 64;
                    break;
                case 3:
                    normalMin = 200;
                    legendaryMin = 100;
                    break;
                case 4:
                    normalMin = legendaryMin = 255;
                    break;
            }
            minimumCatchRate(normalMin, legendaryMin);
        }
    }

    @Override
    public void shuffleShopItems(Settings settings) {
        int badges = getBadgesAtForceEvoLevel(settings);
        Map<Integer, Shop> currentItems = this.getShopItems(badges);
        if (currentItems == null) return;
        List<Integer> itemList = new ArrayList<>();
        for (Shop shop : currentItems.values()) {
            itemList.addAll(shop.items);
        }
        Collections.shuffle(itemList, this.random);

        Iterator<Integer> itemListIter = itemList.iterator();

        for (Shop shop : currentItems.values()) {
            for (int i = 0; i < shop.items.size(); i++) {
                shop.items.remove(i);
                shop.items.add(i, itemListIter.next());
            }
        }

        this.setShopItems(currentItems);
    }

    // Note: If you use this on a game where the amount of randomizable shop items is greater than the amount of
    // possible items, you will get owned by the while loop
    @Override
    public void randomizeShopItems(Settings settings) {
        boolean banBadItems = settings.isBanBadRandomShopItems();
        boolean banRegularShopItems = settings.isBanRegularShopItems();
        boolean banOPShopItems = settings.isBanOPShopItems();
        boolean balancePrices = settings.isBalanceShopPrices();
        boolean placeEvoItems = settings.isGuaranteeEvolutionItems();
        boolean placeXItems = settings.isGuaranteeXItems();

        int maxBadgesForEvoItem = getBadgesAtForceEvoLevel(settings);

        if (this.getShopItems(maxBadgesForEvoItem) == null) return;
        ItemList possibleItems = banBadItems ? this.getNonBadItems() : this.getAllowedItems();
        if (banRegularShopItems) {
            possibleItems.banSingles(this.getRegularShopItems().stream().mapToInt(Integer::intValue).toArray());
        }
        if (banOPShopItems) {
            possibleItems.banSingles(this.getOPShopItems().stream().mapToInt(Integer::intValue).toArray());
        }
        Map<Integer, Shop> currentItems = this.getShopItems(maxBadgesForEvoItem);

        int shopItemCount = currentItems.values().stream().mapToInt(s -> s.items.size()).sum();

        List<Integer> newItems = new ArrayList<>();
        Map<Integer, Shop> newItemsMap = new TreeMap<>();
        int newItem;
        List<Integer> guaranteedEvoItems = getEvolutionItems();
        List<Integer> guaranteedXItems = getXItems();

        if (placeEvoItems || placeXItems) {
            if (placeEvoItems)
                newItems.addAll(guaranteedEvoItems);
            if (placeXItems)
                newItems.addAll(guaranteedXItems);
            shopItemCount = shopItemCount - newItems.size();

            // Find a random item that's not one of the guarantees for each guaranteed item
            for (int i = 0; i < shopItemCount; i++) {
                newItem = possibleItems.randomNonTM(this.random, newItems);

                // If we ran out of items, allow duplicates
                if (newItem == -1)
                    newItem = possibleItems.randomNonTM(this.random);

                newItems.add(newItem);
            }

            // Guarantee main-game
            int firstShop;
            List<Integer> primaryShops = new ArrayList<>();
            List<Integer> mainGameShops = new ArrayList<>();
            List<Integer> nfeMainGameShops = new ArrayList<>();
            List<Integer> nonMainGameShops = new ArrayList<>();
            for (int i : currentItems.keySet()) {
                if (currentItems.get(i).isMainGame) {
                    if (placeEvoItems && currentItems.get(i).isBeforeFullyEvolved)
                        nfeMainGameShops.add(i);
                    else
                        mainGameShops.add(i);
                } else {
                    nonMainGameShops.add(i);
                }
            }

            // Place items in non-main-game shops; skip over guaranteed items
            Collections.shuffle(newItems, this.random);
            for (int i : nonMainGameShops) {
                int j = 0;
                List<Integer> newShopItems = new ArrayList<>();
                Shop oldShop = currentItems.get(i);
                for (Integer ignored : oldShop.items) {
                    Integer item = newItems.get(j);
                    while ((placeXItems && guaranteedXItems.contains(item))
                            || (placeEvoItems && guaranteedEvoItems.contains(item))) {
                        j++;
                        item = newItems.get(j);
                    }
                    newShopItems.add(item);
                    newItems.remove(item);
                }
                Shop shop = new Shop(oldShop);
                shop.items = newShopItems;
                newItemsMap.put(i, shop);
            }

            // Place items in fe-main-game shops; skip over guaranteed evo items
            Collections.shuffle(newItems, this.random);
            for (int i : mainGameShops) {
                int j = 0;
                List<Integer> newShopItems = new ArrayList<>();
                Shop oldShop = currentItems.get(i);
                for (Integer ignored : oldShop.items) {
                    Integer item;
                    if (j < newItems.size())
                        item = newItems.get(j);
                    else
                        item = possibleItems.randomNonTM(this.random, newItems);

                    while (placeEvoItems && guaranteedEvoItems.contains(item)) {
                        j++;

                        if (j < newItems.size())
                            item = newItems.get(j);
                        else
                            item = possibleItems.randomNonTM(this.random, newItems);
                    }
                    newShopItems.add(item);
                    newItems.remove(item);
                }
                Shop shop = new Shop(oldShop);
                shop.items = newShopItems;
                newItemsMap.put(i, shop);
            }

            // Place items in nfe-main-game shops
            Collections.shuffle(newItems, this.random);
            for (int i : nfeMainGameShops) {
                List<Integer> newShopItems = new ArrayList<>();
                Shop oldShop = currentItems.get(i);
                for (Integer ignored : oldShop.items) {
                    Integer item = newItems.get(0);
                    newShopItems.add(item);
                    newItems.remove(0);
                }
                Shop shop = new Shop(oldShop);
                shop.items = newShopItems;
                newItemsMap.put(i, shop);
            }


        } else {
            for (int i = 0; i < shopItemCount; i++) {
                int j = 0;
                do {
                    newItem = possibleItems.randomNonTM(this.random);

                    if (j > 100)
                        break;

                    ++j;
                }
                while (newItems.contains(newItem));

                newItems.add(newItem);
            }

            Iterator<Integer> newItemsIter = newItems.iterator();

            for (int i : currentItems.keySet()) {
                List<Integer> newShopItems = new ArrayList<>();
                Shop oldShop = currentItems.get(i);
                for (Integer ignored : oldShop.items) {
                    newShopItems.add(newItemsIter.next());
                }
                Shop shop = new Shop(oldShop);
                shop.items = newShopItems;
                newItemsMap.put(i, shop);
            }
        }

        this.setShopItems(newItemsMap);
        if (balancePrices) {
            this.setShopPrices();
        }
    }

    @Override
    public int getBadgesAtForceEvoLevel(Settings settings) {
        List<Integer> gymLeaders = getGymLeaders();
        List<Integer> aceLevels = new ArrayList<>();
        List<Trainer> trainers = getTrainers();

        boolean isChallengeMode = isChallengeMode();

        int badges = 0;
        for (int i : gymLeaders) {
            // compensates for challenge mode
            int challengeAdd = isChallengeMode ? badges / 2 + 1 : 0;

            Trainer gymLeader = trainers.get(i);
            aceLevels.add(gymLeader.getAceLevel() + challengeAdd);

            badges++;
        }

        int level = 100;
        if (settings.isTrainersForceFullyEvolved()) {
            level = settings.getTrainersForceFullyEvolvedLevel();
        }

        badges = 0;
        for (int i : aceLevels) {
            if (i < level)
                badges++;
        }

        return badges;
    }

    @Override
    public boolean isChallengeMode() {
        return false;
    }

    List<Integer> getGymLeaders() {
        List<Trainer> trainers = getTrainers();
        List<Integer> mainPlaythroughTrainers = getMainPlaythroughTrainers();
        List<Integer> leaders = new ArrayList<>();

        for (Trainer trainer : trainers) {
            int index = trainer.index - 1;
            if (trainer.isLeader() && (mainPlaythroughTrainers.isEmpty() || mainPlaythroughTrainers.contains(index))) {
                leaders.add(index);
            }
        }

        boolean isChallengeMode = isChallengeMode();
        int start = isChallengeMode ? 8 : 0;
        int end = isChallengeMode ? 16 : 8;

        return leaders.subList(start, end);
    }

    @Override
    public void randomizePickupItems(Settings settings) {
        boolean banBadItems = settings.isBanBadRandomPickupItems();

        ItemList possibleItems = banBadItems ? this.getNonBadItems() : this.getAllowedItems();
        List<PickupItem> currentItems = this.getPickupItems();
        List<PickupItem> newItems = new ArrayList<>();
        for (int i = 0; i < currentItems.size(); i++) {
            int item;
            if (this.generationOfPokemon() == 3 || this.generationOfPokemon() == 4) {
                // Allow TMs in Gen 3/4 since they aren't infinite (and you get TMs from Pickup in the vanilla game)
                item = possibleItems.randomItem(this.random);
            } else {
                item = possibleItems.randomNonTM(this.random);
            }
            PickupItem pickupItem = new PickupItem(item);
            pickupItem.probabilities = Arrays.copyOf(currentItems.get(i).probabilities, currentItems.size());
            newItems.add(pickupItem);
        }

        this.setPickupItems(newItems);
    }

    @Override
    public void minimumCatchRate(int rateNonLegendary, int rateLegendary) {
        List<Pokemon> pokes = getPokemonInclFormes();
        for (Pokemon pkmn : pokes) {
            if (pkmn == null) {
                continue;
            }
            int minCatchRate = pkmn.isLegendary() ? rateLegendary : rateNonLegendary;
            pkmn.catchRate = Math.max(pkmn.catchRate, minCatchRate);
        }

    }

    @Override
    public void standardizeEXPCurves(Settings settings) {
        Settings.ExpCurveMod mod = settings.getExpCurveMod();
        ExpCurve expCurve = settings.getSelectedEXPCurve();

        List<Pokemon> pokes = getPokemonInclFormes();
        switch (mod) {
            case LEGENDARIES:
                for (Pokemon pkmn : pokes) {
                    if (pkmn == null) {
                        continue;
                    }
                    pkmn.growthCurve = pkmn.isLegendary() ? ExpCurve.SLOW : expCurve;
                }
                break;
            case STRONG_LEGENDARIES:
                for (Pokemon pkmn : pokes) {
                    if (pkmn == null) {
                        continue;
                    }
                    pkmn.growthCurve = pkmn.isStrongLegendary() ? ExpCurve.SLOW : expCurve;
                }
                break;
            case ALL:
                for (Pokemon pkmn : pokes) {
                    if (pkmn == null) {
                        continue;
                    }
                    pkmn.growthCurve = expCurve;
                }
                break;
        }
    }

    /* Private methods/structs used internally by the above methods */

    private void updateMovePower(List<Move> moves, int moveNum, int power) {
        Move mv = moves.get(moveNum);
        if (mv.power != power) {
            mv.power = power;
            addMoveUpdate(moveNum, 0);
        }
    }

    private void updateMovePP(List<Move> moves, int moveNum, int pp) {
        Move mv = moves.get(moveNum);
        if (mv.pp != pp) {
            mv.pp = pp;
            addMoveUpdate(moveNum, 1);
        }
    }

    private void updateMoveAccuracy(List<Move> moves, int moveNum, int accuracy) {
        Move mv = moves.get(moveNum);
        if (Math.abs(mv.accuracy - accuracy) >= 1) {
            mv.accuracy = accuracy;
            addMoveUpdate(moveNum, 2);
        }
    }

    private void updateMoveType(List<Move> moves, int moveNum, Type type) {
        Move mv = moves.get(moveNum);
        if (mv.type != type) {
            mv.type = type;
            addMoveUpdate(moveNum, 3);
        }
    }

    private void updateMoveCategory(List<Move> moves, int moveNum, MoveCategory category) {
        Move mv = moves.get(moveNum);
        if (mv.category != category) {
            mv.category = category;
            addMoveUpdate(moveNum, 4);
        }
    }

    private void addMoveUpdate(int moveNum, int updateType) {
        if (moveUpdates == null) {
            return;
        }

        if (!moveUpdates.containsKey(moveNum)) {
            boolean[] updateField = new boolean[5];
            updateField[updateType] = true;
            moveUpdates.put(moveNum, updateField);
        } else {
            moveUpdates.get(moveNum)[updateType] = true;
        }
    }

    protected Set<EvolutionUpdate> impossibleEvolutionUpdates = new TreeSet<>();
    protected Set<EvolutionUpdate> timeBasedEvolutionUpdates = new TreeSet<>();
    protected Set<EvolutionUpdate> easierEvolutionUpdates = new TreeSet<>();

    protected void addEvoUpdateLevel(Set<EvolutionUpdate> evolutionUpdates, Evolution evo) {
        Pokemon pkFrom = evo.from;
        Pokemon pkTo = evo.to;
        int level = evo.extraInfo;
        evolutionUpdates.add(new EvolutionUpdate(pkFrom, pkTo, EvolutionType.LEVEL, String.valueOf(level),
                false, false));
    }

    protected void addEvoUpdateStone(Set<EvolutionUpdate> evolutionUpdates, Evolution evo, String item) {
        Pokemon pkFrom = evo.from;
        Pokemon pkTo = evo.to;
        evolutionUpdates.add(new EvolutionUpdate(pkFrom, pkTo, EvolutionType.STONE, item,
                false, false));
    }

    protected void addEvoUpdateHappiness(Set<EvolutionUpdate> evolutionUpdates, Evolution evo) {
        Pokemon pkFrom = evo.from;
        Pokemon pkTo = evo.to;
        evolutionUpdates.add(new EvolutionUpdate(pkFrom, pkTo, EvolutionType.HAPPINESS, "",
                false, false));
    }

    protected void addEvoUpdateHeldItem(Set<EvolutionUpdate> evolutionUpdates, Evolution evo, String item) {
        Pokemon pkFrom = evo.from;
        Pokemon pkTo = evo.to;
        evolutionUpdates.add(new EvolutionUpdate(pkFrom, pkTo, EvolutionType.LEVEL_ITEM_DAY, item,
                false, false));
    }

    protected void addEvoUpdateParty(Set<EvolutionUpdate> evolutionUpdates, Evolution evo, String otherPk) {
        Pokemon pkFrom = evo.from;
        Pokemon pkTo = evo.to;
        evolutionUpdates.add(new EvolutionUpdate(pkFrom, pkTo, EvolutionType.LEVEL_WITH_OTHER, otherPk,
                false, false));
    }

    protected void addEvoUpdateCondensed(Set<EvolutionUpdate> evolutionUpdates, Evolution evo, boolean additional) {
        Pokemon pkFrom = evo.from;
        Pokemon pkTo = evo.to;
        int level = evo.extraInfo;
        evolutionUpdates.add(new EvolutionUpdate(pkFrom, pkTo, EvolutionType.LEVEL, String.valueOf(level),
                true, additional));
    }

    private Pokemon pickEvoPowerLvlReplacement(List<Pokemon> pokemonPool, Pokemon current) {
        // start with within 10% and add 5% either direction till we find
        // something
        int currentBST = current.bstForPowerLevels();
        int minTarget = currentBST - currentBST / 10;
        int maxTarget = currentBST + currentBST / 10;
        List<Pokemon> canPick = new ArrayList<>();
        List<Pokemon> emergencyPick = new ArrayList<>();
        int expandRounds = 0;
        while (canPick.isEmpty() || (canPick.size() < 3 && expandRounds < 3)) {
            for (Pokemon pk : pokemonPool) {
                if (pk.bstForPowerLevels() >= minTarget && pk.bstForPowerLevels() <= maxTarget && !canPick.contains(pk) && !emergencyPick.contains(pk)) {
                    if (alreadyPicked.contains(pk)) {
                        emergencyPick.add(pk);
                    } else {
                        canPick.add(pk);
                    }
                }
            }
            if (expandRounds >= 2 && canPick.isEmpty()) {
                canPick.addAll(emergencyPick);
            }
            minTarget -= currentBST / 20;
            maxTarget += currentBST / 20;
            expandRounds++;
        }
        return canPick.get(this.random.nextInt(canPick.size()));
    }

    // Note that this is slow and somewhat hacky.
    private Pokemon findPokemonInPoolWithSpeciesID(List<Pokemon> pokemonPool, int speciesID) {
        for (int i = 0; i < pokemonPool.size(); i++) {
            if (pokemonPool.get(i).number == speciesID) {
                return pokemonPool.get(i);
            }
        }
        return null;
    }

    private List<Pokemon> getEvolutionaryRelatives(Pokemon pk) {
        List<Pokemon> evolutionaryRelatives = new ArrayList<>();
        for (Evolution ev : pk.evolutionsFrom) {
            if (!evolutionaryRelatives.contains(ev.to)) {
                Pokemon evo = ev.to;
                evolutionaryRelatives.add(evo);
                Queue<Evolution> evolutionsList = new LinkedList<>();
                evolutionsList.addAll(evo.evolutionsFrom);
                while (evolutionsList.size() > 0) {
                    evo = evolutionsList.remove().to;
                    if (!evolutionaryRelatives.contains(evo)) {
                        evolutionaryRelatives.add(evo);
                        evolutionsList.addAll(evo.evolutionsFrom);
                    }
                }
            }
        }

        for (Evolution ev : pk.evolutionsTo) {
            if (!evolutionaryRelatives.contains(ev.from)) {
                Pokemon preEvo = ev.from;
                evolutionaryRelatives.add(preEvo);

                // At this point, preEvo is basically the "parent" of pk. Run
                // getEvolutionaryRelatives on preEvo in order to get pk's
                // "sibling" evolutions too. For example, if pk is Espeon, then
                // preEvo here will be Eevee, and this will add all the other
                // eeveelutions to the relatives list.
                List<Pokemon> relativesForPreEvo = getEvolutionaryRelatives(preEvo);
                for (Pokemon preEvoRelative : relativesForPreEvo) {
                    if (!evolutionaryRelatives.contains(preEvoRelative)) {
                        evolutionaryRelatives.add(preEvoRelative);
                    }
                }

                while (preEvo.evolutionsTo.size() > 0) {
                    preEvo = preEvo.evolutionsTo.get(0).from;
                    if (!evolutionaryRelatives.contains(preEvo)) {
                        evolutionaryRelatives.add(preEvo);

                        // Similar to above, get the "sibling" evolutions here too.
                        relativesForPreEvo = getEvolutionaryRelatives(preEvo);
                        for (Pokemon preEvoRelative : relativesForPreEvo) {
                            if (!evolutionaryRelatives.contains(preEvoRelative)) {
                                evolutionaryRelatives.add(preEvoRelative);
                            }
                        }
                    }
                }
            }
        }

        return evolutionaryRelatives;
    }

    private static class EvolutionPair {
        private Pokemon from;
        private Pokemon to;

        EvolutionPair(Pokemon from, Pokemon to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((from == null) ? 0 : from.hashCode());
            result = prime * result + ((to == null) ? 0 : to.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            EvolutionPair other = (EvolutionPair) obj;
            if (from == null) {
                if (other.from != null)
                    return false;
            } else if (!from.equals(other.from))
                return false;
            if (to == null) {
                return other.to == null;
            } else return to.equals(other.to);
        }
    }

    /**
     * Check whether adding an evolution from one Pokemon to another will cause
     * an evolution cycle.
     *
     * @param from Pokemon that is evolving
     * @param to   Pokemon to evolve to
     * @return True if there is an evolution cycle, else false
     */
    private boolean evoCycleCheck(Pokemon from, Pokemon to) {
        Evolution tempEvo = new Evolution(from, to, false, EvolutionType.NONE, 0);
        from.evolutionsFrom.add(tempEvo);
        Set<Pokemon> visited = new HashSet<>();
        Set<Pokemon> recStack = new HashSet<>();
        boolean recur = isCyclic(from, visited, recStack);
        from.evolutionsFrom.remove(tempEvo);
        return recur;
    }

    private boolean isCyclic(Pokemon pk, Set<Pokemon> visited, Set<Pokemon> recStack) {
        if (!visited.contains(pk)) {
            visited.add(pk);
            recStack.add(pk);
            for (Evolution ev : pk.evolutionsFrom) {
                if (!visited.contains(ev.to) && isCyclic(ev.to, visited, recStack)) {
                    return true;
                } else if (recStack.contains(ev.to)) {
                    return true;
                }
            }
        }
        recStack.remove(pk);
        return false;
    }

    private interface BasePokemonAction {
        void applyTo(Pokemon pk);
    }

    private interface EvolvedPokemonAction {
        void applyTo(Pokemon evFrom, Pokemon evTo, boolean toMonIsFinalEvo);
    }

    private interface CosmeticFormAction {
        void applyTo(Pokemon pk, Pokemon baseForme);
    }

    /**
     * Universal implementation for things that have "copy X up evolutions"
     * support.
     *
     * @param bpAction      Method to run on all base or no-copy Pokemon
     * @param epAction      Method to run on all evolved Pokemon with a linear chain of
     * @param copySplitEvos If true, treat split evolutions the same way as base Pokemon
     */
    private void copyUpEvolutionsHelper(BasePokemonAction bpAction, EvolvedPokemonAction epAction,
                                        EvolvedPokemonAction splitAction, boolean copySplitEvos) {
        List<Pokemon> allPokes = this.getPokemonInclFormes();
        for (Pokemon pk : allPokes) {
            if (pk != null) {
                pk.temporaryFlag = false;
            }
        }

        // Get evolution data.
        Set<Pokemon> basicPokes = RomFunctions.getBasicPokemon(this);
        Set<Pokemon> splitEvos = RomFunctions.getSplitEvolutions(this);
        Set<Pokemon> middleEvos = RomFunctions.getMiddleEvolutions(this, copySplitEvos);

        for (Pokemon pk : basicPokes) {
            bpAction.applyTo(pk);
            pk.temporaryFlag = true;
        }

        if (!copySplitEvos) {
            for (Pokemon pk : splitEvos) {
                bpAction.applyTo(pk);
                pk.temporaryFlag = true;
            }
        }

        // go "up" evolutions looking for pre-evos to do first
        for (Pokemon pk : allPokes) {
            if (pk != null && !pk.temporaryFlag) {

                // Non-randomized pokes at this point must have
                // a linear chain of single evolutions down to
                // a randomized poke.
                Stack<Evolution> currentStack = new Stack<>();
                Evolution ev = pk.evolutionsTo.get(0);
                while (!ev.from.temporaryFlag) {
                    currentStack.push(ev);
                    ev = ev.from.evolutionsTo.get(0);
                }

                // Now "ev" is set to an evolution from a Pokemon that has had
                // the base action done on it to one that hasn't.
                // Do the evolution action for everything left on the stack.

                if (copySplitEvos && splitAction != null && splitEvos.contains(ev.to)) {
                    splitAction.applyTo(ev.from, ev.to, !middleEvos.contains(ev.to));
                } else {
                    epAction.applyTo(ev.from, ev.to, !middleEvos.contains(ev.to));
                }
                ev.to.temporaryFlag = true;
                while (!currentStack.isEmpty()) {
                    ev = currentStack.pop();
                    if (copySplitEvos && splitAction != null && splitEvos.contains(pk)) {
                        splitAction.applyTo(ev.from, ev.to, !middleEvos.contains(ev.to));
                    } else {
                        epAction.applyTo(ev.from, ev.to, !middleEvos.contains(ev.to));
                    }
                    ev.to.temporaryFlag = true;
                }

            }
        }
    }

    private void copyUpEvolutionsHelper(BasePokemonAction bpAction, EvolvedPokemonAction epAction) {
        copyUpEvolutionsHelper(bpAction, epAction, null, false);
    }

    private boolean checkForUnusedMove(List<Move> potentialList, List<Integer> alreadyUsed) {
        for (Move mv : potentialList) {
            if (!alreadyUsed.contains(mv.number)) {
                return true;
            }
        }
        return false;
    }

    private List<Pokemon> pokemonOfType(Type type, boolean noLegendaries) {
        List<Pokemon> typedPokes = new ArrayList<>();
        for (Pokemon pk : mainPokemonList) {
            if (pk != null && (!noLegendaries || !pk.isLegendary()) && !pk.actuallyCosmetic) {
                if (pk.primaryType == type || pk.secondaryType == type) {
                    typedPokes.add(pk);
                }
            }
        }
        return typedPokes;
    }

    private List<Pokemon> pokemonOfTypeInclFormes(Type type, boolean noLegendaries) {
        List<Pokemon> typedPokes = new ArrayList<>();
        for (Pokemon pk : mainPokemonListInclFormes) {
            if (pk != null && !pk.actuallyCosmetic && (!noLegendaries || !pk.isLegendary())) {
                if (pk.primaryType == type || pk.secondaryType == type) {
                    typedPokes.add(pk);
                }
            }
        }
        return typedPokes;
    }

    private List<Pokemon> allPokemonWithoutNull() {
        List<Pokemon> allPokes = new ArrayList<>(this.getPokemon());
        allPokes.remove(0);
        return allPokes;
    }

    private List<Pokemon> allPokemonInclFormesWithoutNull() {
        List<Pokemon> allPokes = new ArrayList<>(this.getPokemonInclFormes());
        allPokes.remove(0);
        return allPokes;
    }

    private Set<Pokemon> pokemonInArea(EncounterSet area) {
        Set<Pokemon> inArea = new TreeSet<>();
        for (Encounter enc : area.encounters) {
            inArea.add(enc.pokemon);
        }
        return inArea;
    }

    private Map<Type, Integer> typeWeightings;
    private int totalTypeWeighting;

    private Type pickType(boolean weightByFrequency, boolean noLegendaries, boolean allowAltFormes) {
        if (totalTypeWeighting == 0) {
            // Determine weightings
            for (Type t : Type.values()) {
                if (typeInGame(t)) {
                    List<Pokemon> pokemonOfType = allowAltFormes ? pokemonOfTypeInclFormes(t, noLegendaries) :
                            pokemonOfType(t, noLegendaries);
                    int pkWithTyping = pokemonOfType.size();
                    typeWeightings.put(t, pkWithTyping);
                    totalTypeWeighting += pkWithTyping;
                }
            }
        }

        if (weightByFrequency) {
            int typePick = this.random.nextInt(totalTypeWeighting);
            int typePos = 0;
            for (Type t : typeWeightings.keySet()) {
                int weight = typeWeightings.get(t);
                if (typePos + weight > typePick) {
                    return t;
                }
                typePos += weight;
            }
            return null;
        } else {
            return randomType();
        }
    }

    private void rivalCarriesStarterUpdate(List<Trainer> currentTrainers, String prefix, int pokemonOffset) {
        // Find the highest rival battle #
        int highestRivalNum = 0;
        for (Trainer t : currentTrainers) {
            if (t.tag != null && t.tag.startsWith(prefix)) {
                highestRivalNum = Math.max(highestRivalNum,
                        Integer.parseInt(t.tag.substring(prefix.length(), t.tag.indexOf('-'))));
            }
        }

        if (highestRivalNum == 0) {
            // This rival type not used in this game
            return;
        }

        // Get the starters
        // us 0 1 2 => them 0+n 1+n 2+n
        List<Pokemon> starters = this.getStarters();

        // Yellow needs its own case, unfortunately.
        if (isYellow()) {
            // The rival's starter is index 1
            Pokemon rivalStarter = starters.get(1);
            int timesEvolves = numEvolutions(rivalStarter, 2);
            // Yellow does not have abilities
            int abilitySlot = 0;
            // Apply evolutions as appropriate
            if (timesEvolves == 0) {
                for (int j = 1; j <= 3; j++) {
                    changeStarterWithTag(currentTrainers, prefix + j + "-0", rivalStarter, abilitySlot);
                }
                for (int j = 4; j <= 7; j++) {
                    for (int i = 0; i < 3; i++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, rivalStarter, abilitySlot);
                    }
                }
            } else if (timesEvolves == 1) {
                for (int j = 1; j <= 3; j++) {
                    changeStarterWithTag(currentTrainers, prefix + j + "-0", rivalStarter, abilitySlot);
                }
                rivalStarter = pickRandomEvolutionOf(rivalStarter, false);
                for (int j = 4; j <= 7; j++) {
                    for (int i = 0; i < 3; i++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, rivalStarter, abilitySlot);
                    }
                }
            } else if (timesEvolves == 2) {
                for (int j = 1; j <= 2; j++) {
                    changeStarterWithTag(currentTrainers, prefix + j + "-" + 0, rivalStarter, abilitySlot);
                }
                rivalStarter = pickRandomEvolutionOf(rivalStarter, true);
                changeStarterWithTag(currentTrainers, prefix + "3-0", rivalStarter, abilitySlot);
                for (int i = 0; i < 3; i++) {
                    changeStarterWithTag(currentTrainers, prefix + "4-" + i, rivalStarter, abilitySlot);
                }
                rivalStarter = pickRandomEvolutionOf(rivalStarter, false);
                for (int j = 5; j <= 7; j++) {
                    for (int i = 0; i < 3; i++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, rivalStarter, abilitySlot);
                    }
                }
            }
        } else {
            // Replace each starter as appropriate
            // Use level to determine when to evolve, not number anymore
            for (int i = 0; i < 3; i++) {
                // Rival's starters are pokemonOffset over from each of ours
                int starterToUse = (i + pokemonOffset) % 3;
                Pokemon thisStarter = starters.get(starterToUse);
                int timesEvolves = numEvolutions(thisStarter, 2);
                int abilitySlot = getRandomAbilitySlot(thisStarter);
                while (abilitySlot == 3) {
                    // Since starters never have hidden abilities, the rival's starter shouldn't either
                    abilitySlot = getRandomAbilitySlot(thisStarter);
                }
                // If a fully evolved pokemon, use throughout
                // Otherwise split by evolutions as appropriate
                if (timesEvolves == 0) {
                    for (int j = 1; j <= highestRivalNum; j++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter, abilitySlot);
                    }
                } else if (timesEvolves == 1) {
                    int j = 1;
                    for (; j <= highestRivalNum / 2; j++) {
                        if (getLevelOfStarter(currentTrainers, prefix + j + "-" + i) >= 30) {
                            break;
                        }
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter, abilitySlot);
                    }
                    thisStarter = pickRandomEvolutionOf(thisStarter, false);
                    int evolvedAbilitySlot = getValidAbilitySlotFromOriginal(thisStarter, abilitySlot);
                    for (; j <= highestRivalNum; j++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter, evolvedAbilitySlot);
                    }
                } else if (timesEvolves == 2) {
                    int j = 1;
                    for (; j <= highestRivalNum; j++) {
                        if (getLevelOfStarter(currentTrainers, prefix + j + "-" + i) >= 16) {
                            break;
                        }
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter, abilitySlot);
                    }
                    thisStarter = pickRandomEvolutionOf(thisStarter, true);
                    int evolvedAbilitySlot = getValidAbilitySlotFromOriginal(thisStarter, abilitySlot);
                    for (; j <= highestRivalNum; j++) {
                        if (getLevelOfStarter(currentTrainers, prefix + j + "-" + i) >= 36) {
                            break;
                        }
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter, evolvedAbilitySlot);
                    }
                    thisStarter = pickRandomEvolutionOf(thisStarter, false);
                    evolvedAbilitySlot = getValidAbilitySlotFromOriginal(thisStarter, abilitySlot);
                    for (; j <= highestRivalNum; j++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter, evolvedAbilitySlot);
                    }
                }
            }
        }

    }

    private Pokemon pickRandomEvolutionOf(Pokemon base, boolean mustEvolveItself) {
        // Used for "rival carries starter"
        // Pick a random evolution of base Pokemon, subject to
        // "must evolve itself" if appropriate.
        List<Pokemon> candidates = new ArrayList<>();
        for (Evolution ev : base.evolutionsFrom) {
            if (!mustEvolveItself || ev.to.evolutionsFrom.size() > 0) {
                candidates.add(ev.to);
            }
        }

        if (candidates.size() == 0) {
            throw new RandomizationException("Random evolution called on a Pokemon without any usable evolutions.");
        }

        return candidates.get(random.nextInt(candidates.size()));
    }

    private int getLevelOfStarter(List<Trainer> currentTrainers, String tag) {
        for (Trainer t : currentTrainers) {
            if (t.tag != null && t.tag.equals(tag)) {
                // Bingo, get highest level
                // last pokemon is given priority +2 but equal priority
                // = first pokemon wins, so its effectively +1
                // If it's tagged the same we can assume it's the same team
                // just the opposite gender or something like that...
                // So no need to check other trainers with same tag.
                int highestLevel = t.pokemon.get(0).level;
                int trainerPkmnCount = t.pokemon.size();
                for (int i = 1; i < trainerPkmnCount; i++) {
                    int levelBonus = (i == trainerPkmnCount - 1) ? 2 : 0;
                    if (t.pokemon.get(i).level + levelBonus > highestLevel) {
                        highestLevel = t.pokemon.get(i).level;
                    }
                }
                return highestLevel;
            }
        }
        return 0;
    }

    private void changeStarterWithTag(List<Trainer> currentTrainers, String tag, Pokemon starter, int abilitySlot) {
        for (Trainer t : currentTrainers) {
            if (t.tag != null && t.tag.equals(tag)) {

                // Bingo
                TrainerPokemon bestPoke = t.pokemon.get(0);

                if (t.forceStarterPosition >= 0) {
                    bestPoke = t.pokemon.get(t.forceStarterPosition);
                } else {
                    // Change the highest level pokemon, not the last.
                    // BUT: last gets +2 lvl priority (effectively +1)
                    // same as above, equal priority = earlier wins
                    int trainerPkmnCount = t.pokemon.size();
                    for (int i = 1; i < trainerPkmnCount; i++) {
                        int levelBonus = (i == trainerPkmnCount - 1) ? 2 : 0;
                        if (t.pokemon.get(i).level + levelBonus > bestPoke.level) {
                            bestPoke = t.pokemon.get(i);
                        }
                    }
                }
                bestPoke.pokemon = starter;
                setFormeForTrainerPokemon(bestPoke, starter);
                bestPoke.resetMoves = true;
                bestPoke.abilitySlot = abilitySlot;
            }
        }

    }

    // Return the max depth of pre-evolutions a Pokemon has
    private int numPreEvolutions(Pokemon pk, int maxInterested) {
        return numPreEvolutions(pk, 0, maxInterested);
    }

    private int numPreEvolutions(Pokemon pk, int depth, int maxInterested) {
        if (pk.evolutionsTo.size() == 0) {
            return 0;
        } else {
            if (depth == maxInterested - 1) {
                return 1;
            } else {
                int maxPreEvos = 0;
                for (Evolution ev : pk.evolutionsTo) {
                    maxPreEvos = Math.max(maxPreEvos, numPreEvolutions(ev.from, depth + 1, maxInterested) + 1);
                }
                return maxPreEvos;
            }
        }
    }

    private int numEvolutions(Pokemon pk, int maxInterested) {
        return numEvolutions(pk, 0, maxInterested);
    }

    private int numEvolutions(Pokemon pk, int depth, int maxInterested) {
        if (pk.evolutionsFrom.size() == 0) {
            return 0;
        } else {
            if (depth == maxInterested - 1) {
                return 1;
            } else {
                int maxEvos = 0;
                for (Evolution ev : pk.evolutionsFrom) {
                    maxEvos = Math.max(maxEvos, numEvolutions(ev.to, depth + 1, maxInterested) + 1);
                }
                return maxEvos;
            }
        }
    }

    private Pokemon fullyEvolve(Pokemon pokemon, int trainerIndex) {
        // If the fullyEvolvedRandomSeed hasn't been set yet, set it here.
        if (this.fullyEvolvedRandomSeed == -1) {
            this.fullyEvolvedRandomSeed = random.nextInt(GlobalConstants.LARGEST_NUMBER_OF_SPLIT_EVOS);
        }

        Set<Pokemon> seenMons = new HashSet<>();
        seenMons.add(pokemon);

        while (true) {
            if (pokemon.evolutionsFrom.size() == 0) {
                // fully evolved
                break;
            }

            // check for cyclic evolutions from what we've already seen
            boolean cyclic = false;
            for (Evolution ev : pokemon.evolutionsFrom) {
                if (seenMons.contains(ev.to)) {
                    // cyclic evolution detected - bail now
                    cyclic = true;
                    break;
                }
            }

            if (cyclic) {
                break;
            }

            // We want to make split evolutions deterministic, but still random on a seed-to-seed basis.
            // Therefore, we take a random value (which is generated once per seed) and add it to the trainer's
            // index to get a pseudorandom number that can be used to decide which split to take.
            int evolutionIndex = (this.fullyEvolvedRandomSeed + trainerIndex) % pokemon.evolutionsFrom.size();
            pokemon = pokemon.evolutionsFrom.get(evolutionIndex).to;
            seenMons.add(pokemon);
        }

        return pokemon;
    }

    private Set<Pokemon> relatedPokemon(Pokemon original) {
        Set<Pokemon> results = new HashSet<>();
        results.add(original);
        Queue<Pokemon> toCheck = new LinkedList<>();
        toCheck.add(original);
        while (!toCheck.isEmpty()) {
            Pokemon check = toCheck.poll();
            for (Evolution ev : check.evolutionsFrom) {
                if (!results.contains(ev.to)) {
                    results.add(ev.to);
                    toCheck.add(ev.to);
                }
            }
            for (Evolution ev : check.evolutionsTo) {
                if (!results.contains(ev.from)) {
                    results.add(ev.from);
                    toCheck.add(ev.from);
                }
            }
        }
        return results;
    }

    private Map<Type, List<Pokemon>> cachedReplacementLists;
    private List<Pokemon> cachedAllList;
    private List<Pokemon> bannedList = new ArrayList<>();
    private List<Pokemon> usedAsUniqueList = new ArrayList<>();


    private Pokemon pickTrainerPokeReplacement(Pokemon current, boolean usePowerLevels, Type type,
                                               boolean noLegendaries, boolean wonderGuardAllowed,
                                               boolean usePlacementHistory, boolean swapMegaEvos,
                                               boolean abilitiesAreRandomized, boolean allowAltFormes,
                                               boolean banIrregularAltFormes) {
        List<Pokemon> pickFrom;
        List<Pokemon> withoutBannedPokemon;

        if (swapMegaEvos) {
            pickFrom = megaEvolutionsList
                    .stream()
                    .filter(mega -> mega.method == 1)
                    .map(mega -> mega.from)
                    .distinct()
                    .collect(Collectors.toList());
        } else {
            pickFrom = cachedAllList;
        }

        if (usePlacementHistory) {
            // "Distributed" settings
            double placementAverage = getPlacementAverage();
            pickFrom = pickFrom
                    .stream()
                    .filter(pk -> getPlacementHistory(pk) < placementAverage * 2)
                    .collect(Collectors.toList());
            if (pickFrom.isEmpty()) {
                pickFrom = cachedAllList;
            }
        } else if (type != null && cachedReplacementLists != null) {
            // "Type Themed" settings
            if (!cachedReplacementLists.containsKey(type)) {
                List<Pokemon> pokemonOfType = allowAltFormes ? pokemonOfTypeInclFormes(type, noLegendaries) :
                        pokemonOfType(type, noLegendaries);
                pokemonOfType.removeAll(this.getBannedFormesForPlayerPokemon());
                if (!abilitiesAreRandomized) {
                    List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
                    pokemonOfType.removeAll(abilityDependentFormes);
                }
                if (banIrregularAltFormes) {
                    pokemonOfType.removeAll(getIrregularFormes());
                }
                cachedReplacementLists.put(type, pokemonOfType);
            }
            if (swapMegaEvos) {
                pickFrom = cachedReplacementLists.get(type)
                        .stream()
                        .filter(pickFrom::contains)
                        .collect(Collectors.toList());
                if (pickFrom.isEmpty()) {
                    pickFrom = cachedReplacementLists.get(type);
                }
            } else {
                pickFrom = cachedReplacementLists.get(type);
            }
        }

        withoutBannedPokemon = pickFrom.stream().filter(pk -> !bannedList.contains(pk)).collect(Collectors.toList());
        if (!withoutBannedPokemon.isEmpty()) {
            pickFrom = withoutBannedPokemon;
        }

        if (usePowerLevels) {
            // start with within 10% and add 5% either direction till we find
            // something
            int currentBST = current.bstForPowerLevels();
            int minTarget = currentBST - currentBST / 10;
            int maxTarget = currentBST + currentBST / 10;
            List<Pokemon> canPick = new ArrayList<>();
            int expandRounds = 0;
            while (canPick.isEmpty() || (canPick.size() < 3 && expandRounds < 2)) {
                for (Pokemon pk : pickFrom) {
                    if (pk.bstForPowerLevels() >= minTarget
                            && pk.bstForPowerLevels() <= maxTarget
                            && (wonderGuardAllowed || (pk.ability1 != Abilities.wonderGuard
                            && pk.ability2 != Abilities.wonderGuard && pk.ability3 != Abilities.wonderGuard))) {
                        canPick.add(pk);
                    }
                }
                minTarget -= currentBST / 20;
                maxTarget += currentBST / 20;
                expandRounds++;
            }
            // If usePlacementHistory is True, then we need to do some
            // extra checking to make sure the randomly chosen pokemon
            // is actually below the current average placement
            // if not, re-roll

            Pokemon chosenPokemon = canPick.get(this.random.nextInt(canPick.size()));
            if (usePlacementHistory) {
                double placementAverage = getPlacementAverage();
                List<Pokemon> filteredPickList = canPick
                        .stream()
                        .filter(pk -> getPlacementHistory(pk) < placementAverage)
                        .collect(Collectors.toList());
                if (filteredPickList.isEmpty()) {
                    filteredPickList = canPick;
                }
                chosenPokemon = filteredPickList.get(this.random.nextInt(filteredPickList.size()));
            }
            return chosenPokemon;
        } else {
            if (wonderGuardAllowed) {
                return pickFrom.get(this.random.nextInt(pickFrom.size()));
            } else {
                Pokemon pk = pickFrom.get(this.random.nextInt(pickFrom.size()));
                while (pk.ability1 == Abilities.wonderGuard
                        || pk.ability2 == Abilities.wonderGuard
                        || pk.ability3 == Abilities.wonderGuard) {
                    pk = pickFrom.get(this.random.nextInt(pickFrom.size()));
                }
                return pk;
            }
        }
    }

    private Pokemon pickWildPowerLvlReplacement(List<Pokemon> pokemonPool, Pokemon current, boolean banSamePokemon,
                                                List<Pokemon> usedUp, int bstBalanceLevel) {
        // start with within 10% and add 5% either direction till we find
        // something
        int balancedBST = bstBalanceLevel * 10 + 250;
        int currentBST = Math.min(current.bstForPowerLevels(), balancedBST);
        int minTarget = currentBST - currentBST / 10;
        int maxTarget = currentBST + currentBST / 10;
        List<Pokemon> canPick = new ArrayList<>();
        int expandRounds = 0;
        while (canPick.isEmpty() || (canPick.size() < 3 && expandRounds < 3)) {
            for (Pokemon pk : pokemonPool) {
                if (pk.bstForPowerLevels() >= minTarget && pk.bstForPowerLevels() <= maxTarget
                        && (!banSamePokemon || pk != current) && (usedUp == null || !usedUp.contains(pk))
                        && !canPick.contains(pk)) {
                    canPick.add(pk);
                }
            }
            minTarget -= currentBST / 20;
            maxTarget += currentBST / 20;
            expandRounds++;
        }
        return canPick.get(this.random.nextInt(canPick.size()));
    }

    private void setFormeForEncounter(Encounter enc, Pokemon pk) {
        boolean checkCosmetics = true;
        enc.formeNumber = 0;
        if (enc.pokemon.formeNumber > 0) {
            enc.formeNumber = enc.pokemon.formeNumber;
            enc.pokemon = enc.pokemon.baseForme;
            checkCosmetics = false;
        }
        if (checkCosmetics && enc.pokemon.cosmeticForms > 0) {
            enc.formeNumber = enc.pokemon.getCosmeticFormNumber(this.random.nextInt(enc.pokemon.cosmeticForms));
        } else if (!checkCosmetics && pk.cosmeticForms > 0) {
            enc.formeNumber += pk.getCosmeticFormNumber(this.random.nextInt(pk.cosmeticForms));
        }
    }

    private Map<Integer, List<EncounterSet>> mapZonesToEncounters(List<EncounterSet> encountersForAreas) {
        Map<Integer, List<EncounterSet>> zonesToEncounters = new TreeMap<>();
        for (EncounterSet encountersInArea : encountersForAreas) {
            if (zonesToEncounters.containsKey(encountersInArea.offset)) {
                zonesToEncounters.get(encountersInArea.offset).add(encountersInArea);
            } else {
                List<EncounterSet> encountersForZone = new ArrayList<>();
                encountersForZone.add(encountersInArea);
                zonesToEncounters.put(encountersInArea.offset, encountersForZone);
            }
        }
        return zonesToEncounters;
    }

    public Pokemon pickEntirelyRandomPokemon(boolean includeFormes, boolean noLegendaries, EncounterSet area, List<Pokemon> banned) {
        Pokemon result;
        Pokemon randomNonLegendaryPokemon = includeFormes ? randomNonLegendaryPokemonInclFormes() : randomNonLegendaryPokemon();
        Pokemon randomPokemon = includeFormes ? randomPokemonInclFormes() : randomPokemon();
        result = noLegendaries ? randomNonLegendaryPokemon : randomPokemon;
        while (result.actuallyCosmetic) {
            randomNonLegendaryPokemon = includeFormes ? randomNonLegendaryPokemonInclFormes() : randomNonLegendaryPokemon();
            randomPokemon = includeFormes ? randomPokemonInclFormes() : randomPokemon();
            result = noLegendaries ? randomNonLegendaryPokemon : randomPokemon;
        }
        while (banned.contains(result) || area.bannedPokemon.contains(result)) {
            randomNonLegendaryPokemon = includeFormes ? randomNonLegendaryPokemonInclFormes() : randomNonLegendaryPokemon();
            randomPokemon = includeFormes ? randomPokemonInclFormes() : randomPokemon();
            result = noLegendaries ? randomNonLegendaryPokemon : randomPokemon;
            while (result.actuallyCosmetic) {
                randomNonLegendaryPokemon = includeFormes ? randomNonLegendaryPokemonInclFormes() : randomNonLegendaryPokemon();
                randomPokemon = includeFormes ? randomPokemonInclFormes() : randomPokemon();
                result = noLegendaries ? randomNonLegendaryPokemon : randomPokemon;
            }
        }
        return result;
    }

    private Pokemon pickStaticPowerLvlReplacement(List<Pokemon> pokemonPool, Pokemon current, boolean banSamePokemon,
                                                  boolean limitBST) {
        // start with within 10% and add 5% either direction till we find
        // something
        int currentBST = current.bstForPowerLevels();
        int minTarget = limitBST ? currentBST - currentBST / 5 : currentBST - currentBST / 10;
        int maxTarget = limitBST ? currentBST : currentBST + currentBST / 10;
        List<Pokemon> canPick = new ArrayList<>();
        int expandRounds = 0;
        while (canPick.isEmpty() || (canPick.size() < 3 && expandRounds < 3)) {
            for (Pokemon pk : pokemonPool) {
                if (pk.bstForPowerLevels() >= minTarget && pk.bstForPowerLevels() <= maxTarget
                        && (!banSamePokemon || pk != current) && !canPick.contains(pk)) {
                    canPick.add(pk);
                }
            }
            minTarget -= currentBST / 20;
            maxTarget += currentBST / 20;
            expandRounds++;
        }
        return canPick.get(this.random.nextInt(canPick.size()));
    }

    @Override
    public List<Pokemon> getAbilityDependentFormes() {
        List<Pokemon> abilityDependentFormes = new ArrayList<>();
        for (int i = 0; i < mainPokemonListInclFormes.size(); i++) {
            Pokemon pokemon = mainPokemonListInclFormes.get(i);
            if (pokemon.baseForme != null) {
                if (pokemon.baseForme.number == Species.castform) {
                    // All alternate Castform formes
                    abilityDependentFormes.add(pokemon);
                } else if (pokemon.baseForme.number == Species.darmanitan && pokemon.formeNumber == 1) {
                    // Damanitan-Z
                    abilityDependentFormes.add(pokemon);
                } else if (pokemon.baseForme.number == Species.aegislash) {
                    // Aegislash-B
                    abilityDependentFormes.add(pokemon);
                } else if (pokemon.baseForme.number == Species.wishiwashi) {
                    // Wishiwashi-S
                    abilityDependentFormes.add(pokemon);
                }
            }
        }
        return abilityDependentFormes;
    }

    @Override
    public List<Pokemon> getBannedFormesForPlayerPokemon() {
        List<Pokemon> bannedFormes = new ArrayList<>();
        for (int i = 0; i < mainPokemonListInclFormes.size(); i++) {
            Pokemon pokemon = mainPokemonListInclFormes.get(i);
            if (pokemon.baseForme != null) {
                if (pokemon.baseForme.number == Species.giratina) {
                    // Giratina-O is banned because it reverts back to Altered Forme if
                    // equipped with any item that isn't the Griseous Orb.
                    bannedFormes.add(pokemon);
                } else if (pokemon.baseForme.number == Species.shaymin) {
                    // Shaymin-S is banned because it reverts back to its original forme
                    // under a variety of circumstances, and can only be changed back
                    // with the Gracidea.
                    bannedFormes.add(pokemon);
                }
            }
        }
        return bannedFormes;
    }

    @Override
    public void randomizeTotemPokemon(Settings settings) {
        boolean randomizeTotem =
                settings.getTotemPokemonMod() == Settings.TotemPokemonMod.RANDOM ||
                        settings.getTotemPokemonMod() == Settings.TotemPokemonMod.SIMILAR_STRENGTH;
        boolean randomizeAllies =
                settings.getAllyPokemonMod() == Settings.AllyPokemonMod.RANDOM ||
                        settings.getAllyPokemonMod() == Settings.AllyPokemonMod.SIMILAR_STRENGTH;
        boolean randomizeAuras =
                settings.getAuraMod() == Settings.AuraMod.RANDOM ||
                        settings.getAuraMod() == Settings.AuraMod.SAME_STRENGTH;
        boolean similarStrengthTotem = settings.getTotemPokemonMod() == Settings.TotemPokemonMod.SIMILAR_STRENGTH;
        boolean similarStrengthAllies = settings.getAllyPokemonMod() == Settings.AllyPokemonMod.SIMILAR_STRENGTH;
        boolean similarStrengthAuras = settings.getAuraMod() == Settings.AuraMod.SAME_STRENGTH;
        boolean randomizeHeldItems = settings.isRandomizeTotemHeldItems();
        int levelModifier = settings.isTotemLevelsModified() ? settings.getTotemLevelModifier() : 0;
        boolean allowAltFormes = settings.isAllowTotemAltFormes();
        boolean banIrregularAltFormes = settings.isBanIrregularAltFormes();
        boolean abilitiesAreRandomized = settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE;

        checkPokemonRestrictions();
        List<TotemPokemon> currentTotemPokemon = this.getTotemPokemon();
        List<TotemPokemon> replacements = new ArrayList<>();
        List<Pokemon> banned = this.bannedForStaticPokemon();
        if (!abilitiesAreRandomized) {
            List<Pokemon> abilityDependentFormes = getAbilityDependentFormes();
            banned.addAll(abilityDependentFormes);
        }
        if (banIrregularAltFormes) {
            banned.addAll(getIrregularFormes());
        }
        List<Pokemon> listInclFormesExclCosmetics =
                mainPokemonListInclFormes
                        .stream()
                        .filter(pk -> !pk.actuallyCosmetic)
                        .collect(Collectors.toList());
        List<Pokemon> pokemonLeft = new ArrayList<>(!allowAltFormes ? mainPokemonList : listInclFormesExclCosmetics);
        pokemonLeft.removeAll(banned);
        for (TotemPokemon old : currentTotemPokemon) {
            TotemPokemon newTotem = new TotemPokemon();
            newTotem.heldItem = old.heldItem;
            if (randomizeTotem) {
                Pokemon newPK;
                Pokemon oldPK = old.pkmn;
                if (old.forme > 0) {
                    oldPK = getAltFormeOfPokemon(oldPK, old.forme);
                }

                if (similarStrengthTotem) {
                    newPK = pickStaticPowerLvlReplacement(
                            pokemonLeft,
                            oldPK,
                            true,
                            false);
                } else {
                    newPK = pokemonLeft.remove(this.random.nextInt(pokemonLeft.size()));
                }

                pokemonLeft.remove(newPK);
                newTotem.pkmn = newPK;
                setFormeForStaticEncounter(newTotem, newPK);
                newTotem.resetMoves = true;
                newTotem.level = old.level;

                if (levelModifier != 0) {
                    newTotem.level = Math.min(100, (int) Math.round(newTotem.level * (1 + levelModifier / 100.0)));
                }
                if (pokemonLeft.size() == 0) {
                    pokemonLeft.addAll(!allowAltFormes ? mainPokemonList : listInclFormesExclCosmetics);
                    pokemonLeft.removeAll(banned);
                }
            } else {
                newTotem.pkmn = old.pkmn;
                newTotem.level = old.level;
                if (levelModifier != 0) {
                    newTotem.level = Math.min(100, (int) Math.round(newTotem.level * (1 + levelModifier / 100.0)));
                }
                setFormeForStaticEncounter(newTotem, newTotem.pkmn);
            }

            if (randomizeAllies) {
                for (Integer oldAllyIndex : old.allies.keySet()) {
                    StaticEncounter oldAlly = old.allies.get(oldAllyIndex);
                    StaticEncounter newAlly = new StaticEncounter();
                    Pokemon newAllyPK;
                    Pokemon oldAllyPK = oldAlly.pkmn;
                    if (oldAlly.forme > 0) {
                        oldAllyPK = getAltFormeOfPokemon(oldAllyPK, oldAlly.forme);
                    }
                    if (similarStrengthAllies) {
                        newAllyPK = pickStaticPowerLvlReplacement(
                                pokemonLeft,
                                oldAllyPK,
                                true,
                                false);
                    } else {
                        newAllyPK = pokemonLeft.remove(this.random.nextInt(pokemonLeft.size()));
                    }

                    pokemonLeft.remove(newAllyPK);
                    newAlly.pkmn = newAllyPK;
                    setFormeForStaticEncounter(newAlly, newAllyPK);
                    newAlly.resetMoves = true;
                    newAlly.level = oldAlly.level;
                    if (levelModifier != 0) {
                        newAlly.level = Math.min(100, (int) Math.round(newAlly.level * (1 + levelModifier / 100.0)));
                    }

                    newTotem.allies.put(oldAllyIndex, newAlly);
                    if (pokemonLeft.size() == 0) {
                        pokemonLeft.addAll(!allowAltFormes ? mainPokemonList : listInclFormesExclCosmetics);
                        pokemonLeft.removeAll(banned);
                    }
                }
            } else {
                newTotem.allies = old.allies;
                for (StaticEncounter ally : newTotem.allies.values()) {
                    if (levelModifier != 0) {
                        ally.level = Math.min(100, (int) Math.round(ally.level * (1 + levelModifier / 100.0)));
                        setFormeForStaticEncounter(ally, ally.pkmn);
                    }
                }
            }

            if (randomizeAuras) {
                if (similarStrengthAuras) {
                    newTotem.aura = Aura.randomAuraSimilarStrength(this.random, old.aura);
                } else {
                    newTotem.aura = Aura.randomAura(this.random);
                }
            } else {
                newTotem.aura = old.aura;
            }

            if (randomizeHeldItems) {
                if (old.heldItem != 0) {
                    List<Integer> consumableList = getAllConsumableHeldItems();
                    newTotem.heldItem = consumableList.get(this.random.nextInt(consumableList.size()));
                }
            }

            replacements.add(newTotem);
        }

        // Save
        this.setTotemPokemon(replacements);
    }

    /* Helper methods used by subclasses and/or this class */

    void checkPokemonRestrictions() {
        if (!restrictionsSet) {
            setPokemonPool(null);
        }
    }

    protected void applyCamelCaseNames() {
        List<Pokemon> pokes = getPokemon();
        for (Pokemon pkmn : pokes) {
            if (pkmn == null) {
                continue;
            }
            pkmn.name = RomFunctions.camelCase(pkmn.name);
        }

    }

    private void setPlacementHistory(Pokemon newPK) {
        Integer history = getPlacementHistory(newPK);
        placementHistory.put(newPK, history + 1);
    }

    private int getPlacementHistory(Pokemon newPK) {
        return placementHistory.getOrDefault(newPK, 0);
    }

    private double getPlacementAverage() {
        return placementHistory.values().stream().mapToInt(e -> e).average().orElse(0);
    }


    private List<Pokemon> getBelowAveragePlacements() {
        // This method will return a PK if the number of times a pokemon has been
        // placed is less than average of all placed pokemon's appearances
        // E.g., Charmander's been placed once, but the average for all pokemon is 2.2
        // So add to list and return 

        List<Pokemon> toPlacePK = new ArrayList<>();
        List<Pokemon> placedPK = new ArrayList<>(placementHistory.keySet());
        List<Pokemon> allPK = cachedAllList;
        int placedPKNum = 0;
        for (Pokemon p : placedPK) {
            placedPKNum += placementHistory.get(p);
        }
        float placedAverage = Math.round((float) placedPKNum / (float) placedPK.size());


        if (placedAverage != placedAverage) { // this is checking for NaN, should only happen on first call
            placedAverage = 1;
        }

        // now we've got placement average, iterate all pokemon and see if they qualify to be placed

        for (Pokemon newPK : allPK) {
            if (placedPK.contains(newPK)) { // if it's in the list of previously placed, then check its viability 
                if (placementHistory.get(newPK) <= placedAverage) {
                    toPlacePK.add(newPK);
                }
            } else {
                toPlacePK.add(newPK); // if not placed at all, automatically flag true for placing

            }
        }

        return toPlacePK;

    }

    @Override
    public void renderPlacementHistory() {
        List<Pokemon> placedPK = new ArrayList<>(placementHistory.keySet());
        for (Pokemon p : placedPK) {
            System.out.println(p.name + ": " + placementHistory.get(p));
        }
    }

    ///// Item functions
    private void setItemPlacementHistory(int newItem) {
        Integer history = getItemPlacementHistory(newItem);
        // System.out.println("Current history: " + newPK.name + " : " + history);
        itemPlacementHistory.put(newItem, history + 1);
    }

    private int getItemPlacementHistory(int newItem) {
        List<Integer> placedItem = new ArrayList<>(itemPlacementHistory.keySet());
        if (placedItem.contains(newItem)) {
            return itemPlacementHistory.get(newItem);
        } else {
            return 0;
        }
    }

    private float getItemPlacementAverage() {
        // This method will return an integer of average for itemPlacementHistory
        // placed is less than average of all placed pokemon's appearances
        // E.g., Charmander's been placed once, but the average for all pokemon is 2.2
        // So add to list and return 

        List<Integer> placedPK = new ArrayList<>(itemPlacementHistory.keySet());
        int placedPKNum = 0;
        for (Integer p : placedPK) {
            placedPKNum += itemPlacementHistory.get(p);
        }
        return (float) placedPKNum / (float) placedPK.size();
    }

    private void reportItemHistory() {
        String[] itemNames = this.getItemNames();
        List<Integer> placedItem = new ArrayList<>(itemPlacementHistory.keySet());
        for (Integer p : placedItem) {
            System.out.println(itemNames[p] + ": " + itemPlacementHistory.get(p));
        }
    }

    protected void log(String log) {
        if (logStream != null) {
            logStream.println(log);
        }
    }

    protected void logBlankLine() {
        if (logStream != null) {
            logStream.println();
        }
    }

    /* Default Implementations */
    /* Used when a subclass doesn't override */
    /*
     * The implication here is that these WILL be overridden by at least one
     * subclass.
     */
    @Override
    public boolean typeInGame(Type type) {
        return !type.isHackOnly && !(type == Type.FAIRY && generationOfPokemon() < 6);
    }

    @Override
    public String abilityName(int number) {
        return "";
    }

    @Override
    public List<Integer> getUselessAbilities() {
        return new ArrayList<>();
    }

    @Override
    public int getAbilityForTrainerPokemon(TrainerPokemon tp) {
        return 0;
    }

    @Override
    public boolean hasTimeBasedEncounters() {
        // DEFAULT: no
        return false;
    }

    @Override
    public List<Pokemon> bannedForWildEncounters() {
        return new ArrayList<>();
    }

    @Override
    public List<Integer> getMovesBannedFromLevelup() {
        return GlobalConstants.bannedMoves.stream().toList();
    }

    @Override
    public List<Pokemon> bannedForStaticPokemon() {
        return new ArrayList<>();
    }

    @Override
    public boolean forceSwapStaticMegaEvos() {
        return false;
    }

    @Override
    public int maxTrainerNameLength() {
        // default: no real limit
        return Integer.MAX_VALUE;
    }

    @Override
    public int maxSumOfTrainerNameLengths() {
        // default: no real limit
        return Integer.MAX_VALUE;
    }

    @Override
    public int maxTrainerClassNameLength() {
        // default: no real limit
        return Integer.MAX_VALUE;
    }

    @Override
    public int maxTradeNicknameLength() {
        return 10;
    }

    @Override
    public int maxTradeOTNameLength() {
        return 7;
    }

    @Override
    public boolean altFormesCanHaveDifferentEvolutions() {
        return false;
    }

    @Override
    public List<Integer> getGameBreakingMoves() {
        return Arrays.asList(Moves.sonicBoom, Moves.dragonRage, Moves.sketch);
    }

    @Override
    public List<Integer> getIllegalMoves() {
        return Collections.singletonList(Moves.struggle);
    }

    @Override
    public boolean isYellow() {
        return false;
    }

    @Override
    public void writeCheckValueToROM(int value) {
        // do nothing
    }

    @Override
    public long miscTweaksAvailable() {
        // default: none
        return 0;
    }

    @Override
    public void applyMiscTweaks(Settings settings) {
        long codeTweaksAvailable = miscTweaksAvailable();
        List<MiscTweak> tweaksToApply = new ArrayList<>();

        for (MiscTweak mt : MiscTweak.allTweaks) {
            if ((codeTweaksAvailable & mt.getValue()) != 0L) {
                tweaksToApply.add(mt);
            }
        }

        // Sort so priority is respected in tweak ordering.
        Collections.sort(tweaksToApply);

        // Now apply in order.
        for (MiscTweak mt : tweaksToApply) {
            if ((settings.getCurrentMiscTweaks() & mt.getValue()) != 0L)
                applyMiscTweak(settings, mt);
        }
    }

    @Override
    public void applyMiscTweak(Settings settings, MiscTweak tweak) {
        // default: do nothing
    }

    @Override
    public List<Integer> getXItems() {
        return GlobalConstants.xItems;
    }

    @Override
    public List<Integer> getSensibleHeldItemsFor(TrainerPokemon tp, Settings settings, boolean consumableOnly, List<Move> moves, int[] pokeMoves, Map<Weather, Double> weatherFrequencies) {
        return Arrays.asList(0);
    }

    @Override
    public List<Integer> getAllConsumableHeldItems() {
        return Arrays.asList(0);
    }

    @Override
    public List<Integer> getAllHeldItems() {
        return Arrays.asList(0);
    }

    @Override
    public List<Pokemon> getBannedFormesForTrainerPokemon() {
        return new ArrayList<>();
    }

    @Override
    public List<PickupItem> getPickupItems() {
        return new ArrayList<>();
    }

    @Override
    public void setPickupItems(List<PickupItem> pickupItems) {
        // do nothing
    }
}
