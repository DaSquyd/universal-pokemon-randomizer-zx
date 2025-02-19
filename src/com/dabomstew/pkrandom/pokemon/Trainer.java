package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Trainer.java - represents a Trainer's pokemon set/other details.      --*/
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Trainer implements Comparable<Trainer> {
    public enum BattleType {
        SingleBattle,
        DoubleBattle,
        TripleBattle,
        RotationBattle
    }

    public enum PoolId {
        A, B, C, D, E, F
    }

    public static class PartySlot {
        public PoolId poolId;
        public int ivs;
        public int level;
        
        public PartySlot(PoolId poolId, int ivs, int level) {
            this.poolId = poolId;
            this.ivs = ivs;
            this.level = level;
        }
        
        public int toPacked() {
            return (poolId.ordinal() & 0x07) | ((ivs & 0x1F) << 3) | ((level & 0xFF) << 8);
        }
    }

    public int offset;
    public int index;
    public List<List<TrainerPokemon>> pools = new ArrayList<>(6);
    public String tag;
    public boolean importantTrainer;
    // This value has some flags about the trainer's pokemon (e.g. if they have items or custom moves)
    public int partyFlags;
    public String name;
    public int trainerclass;
    public BattleType battleType;
    public String fullDisplayName;
    public MultiBattleStatus multiBattleStatus = MultiBattleStatus.NEVER;
    public int forceStarterPosition = -1;
    // Certain trainers (e.g., trainers in the PWT in BW2) require unique held items for all of their Pokemon to prevent a game crash.
    public boolean requiresUniqueHeldItems;
    public int[] items = new int[4];
    public int aiFlags;
    public boolean isHealer;
    public byte payoutScale; // Actual reward money is this scale * ace Pokémon level * 100
    public int rewardItem;
    public List<PartySlot> partySlots = new ArrayList<>(6);
    public boolean uniqueSpecies = false;
    public boolean uniqueItems = false;
    public boolean pokesHaveNatures = false;
    public boolean pokesHaveIVsEVs = false;
    public boolean ppMax = false;
    public boolean isPooled = false;
    public boolean plMode = false;
    
    public int getPartySize() {
        if (pools.isEmpty())
            return 0;
        
        if (!isPooled)
            return pools.get(0).size();
        
        return partySlots.size();
    }
    
    public List<TrainerPokemon> getStandardPokePool() {
        if (isPooled)
            throw new RuntimeException("Trainer is pooled");
        
        if (pools.size() > 1)
            throw new RuntimeException("Too many pools");
        
        if (pools.isEmpty())
            pools.add(new ArrayList<>());
        
        List<TrainerPokemon> standardPokes = pools.get(0);
        if (standardPokes.size() > 6)
            throw new RuntimeException("Standard Pokes is too large");
        
        return pools.get(0);
    }
    
    public List<TrainerPokemon> getAllPokesInPools() {
        List<TrainerPokemon> pokes = new ArrayList<>();
        for (List<TrainerPokemon> pool : pools)
            pokes.addAll(pool);
        
        return pokes;
    }
    
    public int getTotalPokes() {
        int count = 0;
        for (List<TrainerPokemon> pool : pools)
            count += pool.size();
        
        return count;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        if (fullDisplayName != null) {
            sb.append(fullDisplayName).append(" ");
        } else if (name != null) {
            sb.append(name).append(" ");
        }
        if (trainerclass != 0) {
            sb.append("(").append(trainerclass).append(") - ");
        }
        if (offset > 0) {
            sb.append(String.format("%x", offset));
        }
        sb.append(" => ");
        boolean first = true;
        if (isPooled) {
            Set<Integer> usedSpecies = new HashSet<>();
            Set<Integer> usedItems = new HashSet<>();
            
            for (PartySlot s : partySlots) {
                if (!first)
                    sb.append(',');
                
                List<TrainerPokemon> pool = pools.get(s.poolId.ordinal());
                TrainerPokemon poke = null;
                for (TrainerPokemon pk : pool) {
                    if (uniqueSpecies && usedSpecies.contains(pk.pokemon.number))
                        continue;
                    
                    if (uniqueItems && usedItems.contains(pk.heldItem))
                        continue;
                    
                    usedSpecies.add(pk.pokemon.number);
                    usedItems.add(pk.heldItem);
                    poke = pk;
                    break;
                }

                if (poke == null)
                    throw new RuntimeException();

                sb.append(poke.pokemon.name).append(" Lv").append(poke.level);
                
                first = false;
            }
        } else {
            for (TrainerPokemon p : pools.get(0)) {
                if (!first)
                    sb.append(',');
                    
                sb.append(p.pokemon.name).append(" Lv").append(p.level);
                first = false;
            }
        }
        
        sb.append(']');
        if (tag != null) {
            sb.append(" (").append(tag).append(")");
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
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
        Trainer other = (Trainer) obj;
        return index == other.index;
    }

    @Override
    public int compareTo(Trainer o) {
        return index - o.index;
    }

    public boolean isBoss() {
        return tag != null && (tag.startsWith("ELITE") || tag.startsWith("CHAMPION")
                || tag.startsWith("UBER") || tag.endsWith("LEADER"));
    }

    public boolean isLeader() {
        return tag != null && !tag.startsWith("THEMED") && tag.endsWith("LEADER");
    }

    public int getAceLevel() {
        int highestLevel = 0;
        
        if (!isPooled) {
            var pokes = getStandardPokePool();
            for (var poke : pokes) {
                highestLevel = Math.max(highestLevel, poke.level);
            }
            
            return highestLevel;
        }
        
        for (var partySlot : partySlots) {
            highestLevel = Math.max(highestLevel, partySlot.level);
        }

        return highestLevel;
    }

    public boolean isImportant() {
        return tag != null && (tag.startsWith("RIVAL") || tag.startsWith("FRIEND") || tag.endsWith("STRONG"));
    }

    public boolean skipImportant() {
        return ((tag != null) && (tag.startsWith("RIVAL1-") || tag.startsWith("FRIEND1-") || tag.endsWith("NOTSTRONG")));
    }

    public void setPokemonHaveItems(boolean haveItems) {
        if (haveItems) {
            this.partyFlags |= 2;
        } else {
            this.partyFlags = partyFlags & ~2;
        }
    }

    public boolean pokemonHaveItems() {
        // This flag seems consistent for all gens
        return (this.partyFlags & 2) == 2;
    }

    public void setPokemonHaveCustomMoves(boolean haveCustomMoves) {
        if (haveCustomMoves) {
            this.partyFlags |= 1;
        } else {
            this.partyFlags = partyFlags & ~1;
        }
    }

    public boolean pokemonHaveCustomMoves() {
        // This flag seems consistent for all gens
        return (this.partyFlags & 1) == 1;
    }

    public boolean pokemonHaveUniqueHeldItems() {
        if (isPooled)
            throw new RuntimeException();
        
        List<Integer> heldItemsForThisTrainer = new ArrayList<>();
        for (TrainerPokemon poke : pools.get(0)) {
            if (poke.heldItem > 0) {
                if (heldItemsForThisTrainer.contains(poke.heldItem)) {
                    return false;
                } else {
                    heldItemsForThisTrainer.add(poke.heldItem);
                }
            }
        }
        return true;
    }

    public enum MultiBattleStatus {
        NEVER, POTENTIAL, ALWAYS
    }
}
