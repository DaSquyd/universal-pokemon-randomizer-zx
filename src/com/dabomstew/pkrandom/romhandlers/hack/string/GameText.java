package com.dabomstew.pkrandom.romhandlers.hack.string;

import java.util.ArrayList;
import java.util.List;

public abstract class GameText {
    List<String> lines = new ArrayList<>();

    // TODO: validation for size
    
    @Override
    public String toString() {
        return String.join("\\xFFFE", lines); 
    }
}
