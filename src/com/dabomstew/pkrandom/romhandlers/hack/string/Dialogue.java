package com.dabomstew.pkrandom.romhandlers.hack.string;

import java.util.Arrays;

public class Dialogue extends GameText {
    public static final int maxLinePixelCount = 264;
    public static final String clearLine = "";
    
    public Dialogue(String... lines) {
        this.lines.addAll(Arrays.asList(lines));
        
        // TODO: confirm line pixel count
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean firstLine = true; // false
        int indexInSection = 0; // 0

        String c = "\\xF000\\xBE01\\x0000";
        String r = "\\xF000\\xBE00\\x0000";
        String n = "\\xFFFE";

        for (String line : lines) {
            if (line.isBlank()) {
                stringBuilder.append(c);
                indexInSection = 0;
                continue;
            }

            if (indexInSection >= 2)
                stringBuilder.append(r); // \r

            if (!firstLine)
                stringBuilder.append(n); // \n

            stringBuilder.append(line);
            firstLine = false;
            indexInSection++;
        }

        stringBuilder.append(c);

        return stringBuilder.toString();
    }
}
