package com.dabomstew.pkrandom.romhandlers;

import com.dabomstew.pkrandom.FileFunctions;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ParagonLiteLocalizer {
    Locale locale; // used primarily for sorting, Spanish uses English

    List<String[]> searchCharacters;

    public ParagonLiteLocalizer() {
    }

    public void setLocale(String localeName) {
        switch (localeName) {
            case "English", "Spanish":
                locale = Locale.ENGLISH;
                break;
            case "French":
                locale = Locale.FRENCH;
                break;
            case "German":
                locale = Locale.GERMAN;
                break;
            case "Italian":
                locale = Locale.ITALIAN;
                break;
            case "Japanese":
                locale = Locale.JAPANESE;
                break;
            case "Korean":
                locale = Locale.KOREAN;
                break;
            case "Chinese-Simplified":
                locale = Locale.SIMPLIFIED_CHINESE;
                break;
            case "Chinese-Traditional":
                locale = Locale.TRADITIONAL_CHINESE;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + localeName);
        }

        String searchCharactersFilename = "search_characters.ini";
        searchCharacters = ReadIni(searchCharactersFilename, localeName).get(localeName);
        if (searchCharacters.get(0)[0].equals("Base")) {
            String baseLocaleName = searchCharacters.get(0)[1];
            searchCharacters = ReadIni(searchCharactersFilename, baseLocaleName).get(baseLocaleName);
        }
    }

    private static Map<String, List<String[]>> ReadIni(String filename, String label) {
        List<String> lines = readLines(filename);

        // remove comments
        for (int i = 0; i < lines.size(); ++i) {
            String line = lines.get(i);
            line = line.replaceFirst("[#;].*", "");
            lines.set(i, line.trim());
        }

        Map<String, List<String[]>> returnValue = new HashMap<>();

        String currentLabel = "";
        List<String[]> currentMap = null;
        for (String line : lines) {
            if (line.isEmpty())
                continue;

            if (line.startsWith("[")) {
                if (!line.endsWith("]"))
                    throw new RuntimeException();

                if (!label.isEmpty() && currentLabel.equals(label))
                    break;

                currentLabel = line.substring(1, line.length() - 1);
                if (!returnValue.containsKey(currentLabel))
                    returnValue.put(currentLabel, new ArrayList<>());
                currentMap = returnValue.get(currentLabel);
                continue;
            }

            if (currentMap == null)
                throw new RuntimeException();

            if (!label.isEmpty() && !currentLabel.equals(label))
                continue;

            String[] kvpStr = line.split("=", 2);
            if (kvpStr.length != 2)
                throw new RuntimeException();

            kvpStr[0] = kvpStr[0].trim();
            kvpStr[1] = kvpStr[1].trim();

            currentMap.add(kvpStr);
        }

        return returnValue;
    }

    private static List<String> readLines(String filename) {
        Scanner sc;
        try {
            filename = "paragonlite/localization" + filename;
            InputStream stream = FileFunctions.openConfig(filename);
            if (stream == null)
                throw new RuntimeException(String.format("Could not find file \"%s\"", filename));
            sc = new Scanner(stream, StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<String> lines = new ArrayList<>();
        while (sc.hasNextLine()) {
            lines.add(sc.nextLine());
        }

        return lines;
    }
}
