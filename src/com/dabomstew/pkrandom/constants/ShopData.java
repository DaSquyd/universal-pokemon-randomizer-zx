package com.dabomstew.pkrandom.constants;

public class ShopData {
    public String name;
    public int badges;
    public boolean isPrimary;
    public boolean isMain;

    ShopData(String name, int badges, boolean isPrimary, boolean isMain) {
        this.name = name;
        this.badges = badges;
        this.isPrimary = isPrimary;
        this.isMain = isMain;
    }
}
