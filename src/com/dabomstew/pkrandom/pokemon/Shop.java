package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Shop.java - represents a shop with a list of purchasable items.       --*/
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

import java.util.List;

public class Shop {
    public String name;
    public List<Integer> items;
    public boolean isPrimary;
    public boolean isMainGame;
    public boolean isBeforeFullyEvolved;

    public Shop(String name, List<Integer> items, boolean isPrimary, boolean isMainGame, boolean isBeforeFullyEvolved) {
        this.name = name;
        this.items = items;
        this.isPrimary = isPrimary;
        this.isMainGame = isMainGame;
        this.isBeforeFullyEvolved = isBeforeFullyEvolved;
    }

    public Shop(Shop otherShop) {
        this.name = otherShop.name;
        this.items = otherShop.items;
        this.isPrimary = otherShop.isPrimary;
        this.isMainGame = otherShop.isMainGame;
        this.isBeforeFullyEvolved = otherShop.isBeforeFullyEvolved;
    }
}
