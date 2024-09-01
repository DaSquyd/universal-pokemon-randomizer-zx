package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  StatusType.java - represents the different types of status effects    --*/
/*--                    that can be inflicted on a Pokemon.                 --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
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

public enum MoveStatusType {
    NONE(0, MoveStatusMode.NONE, 0, 0),
    MAGNET_RISE(0, MoveStatusMode.NONE, 5, 5),
    PARALYZE(1, MoveStatusMode.INDEFINITE, 0, 0),
    SLEEP(2, MoveStatusMode.SET_TURNS, 2, 4),
    FREEZE(3, MoveStatusMode.INDEFINITE, 0, 0),
    BURN(4, MoveStatusMode.INDEFINITE, 0, 0),
    POISON(5, MoveStatusMode.INDEFINITE, 0, 0),
    TOXIC_POISON(5, MoveStatusMode.INDEFINITE, 15, 15),
    CONFUSION(6, MoveStatusMode.SET_TURNS, 2, 5),
    INFATUATION(7, MoveStatusMode.INFATUATION, 0, 0),
    TRAP(8, MoveStatusMode.TRAP, 5, 6),
    NIGHTMARE(9, MoveStatusMode.INDEFINITE, 0, 0),
    TORMENT(12, MoveStatusMode.INDEFINITE, 0, 0),
    DISABLE(13, MoveStatusMode.SET_TURNS, 4, 4),
    DROWSY(14, MoveStatusMode.SET_TURNS, 2, 2),
    HEAL_BLOCK(15, MoveStatusMode.SET_TURNS, 5, 5),
    ACC_IGNORE(17, MoveStatusMode.INDEFINITE, 0, 0),
    LEECH_SEED(18, MoveStatusMode.INDEFINITE, 0, 0),
    EMBARGO(19, MoveStatusMode.SET_TURNS, 5, 5),
    PERISH_SONG(20, MoveStatusMode.SET_TURNS, 4, 4),
    INGRAIN(21, MoveStatusMode.INDEFINITE, 0, 0),
    TRI_ATTACK(-1, MoveStatusMode.INDEFINITE, 0, 0),
    TELEKINESIS(-1, MoveStatusMode.SET_TURNS, 3, 3),
    SMACK_DOWN(-1, MoveStatusMode.INDEFINITE, 0, 0);

    public final int index;
    public final MoveStatusMode mode;
    public final int minTurns;
    public final int maxTurns;

    MoveStatusType(int index, MoveStatusMode mode, int minTurns, int maxTurns) {
        this.index = index;
        this.mode = mode;
        this.minTurns = minTurns;
        this.maxTurns = maxTurns;
    }

    public static MoveStatusType fromValues(int index, MoveStatusMode mode, int minTurns, int maxTurns) {
        MoveStatusType match = null;

        for (MoveStatusType moveStatusType : values()) {
            if (moveStatusType.index == index && moveStatusType.mode == mode && (match == null || (moveStatusType.minTurns == minTurns && moveStatusType.maxTurns == maxTurns)))
                match = moveStatusType;
        }

        return match;
    }
}
