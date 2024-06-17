package pptxt;

/*----------------------------------------------------------------------------*/
/*--  PPTxtHandler.java - handles generation 5 games text encoding          --*/
/*--  Code derived from "PPTXT", copyright (C) SCV?                         --*/
/*--  Ported to Java and bugfixed/customized by Dabomstew                   --*/
/*----------------------------------------------------------------------------*/

import java.io.FileNotFoundException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dabomstew.pkrandom.FileFunctions;

public class PPTxtHandler {

    private static Map<String, String> pokeToText = new HashMap<>();
    private static Map<String, String> textToPoke = new HashMap<>();

    private static Pattern pokeToTextPattern, textToPokePattern;

    static {
        try {
            Scanner sc = new Scanner(FileFunctions.openConfig("Generation5.tbl"), "UTF-8");
            while (sc.hasNextLine()) {
                String q = sc.nextLine();
                if (!q.trim().isEmpty()) {
                    String[] r = q.split("=", 2);
                    if (r[1].endsWith("\r\n")) {
                        r[1] = r[1].substring(0, r[1].length() - 2);
                    }
                    pokeToText.put(Character.toString((char) Integer.parseInt(r[0], 16)), r[1].replace("\\", "\\\\")
                            .replace("$", "\\$"));
                    textToPoke.put(r[1], "\\\\x" + r[0]);
                }
            }
            sc.close();
            pokeToTextPattern = makePattern(pokeToText.keySet());
            textToPokePattern = makePattern(textToPoke.keySet());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Pattern makePattern(Iterable<String> tokens) {
        String patternStr = "("
                + implode(tokens, "|").replace("\\", "\\\\").replace("[", "\\[").replace("]", "\\]")
                .replace("(", "\\(").replace(")", "\\)") + ")";
        return Pattern.compile(patternStr);
    }

    private static String implode(Iterable<String> tokens, String sep) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String token : tokens) {
            if (!first) {
                sb.append(sep);
            }
            sb.append(token);
            first = false;
        }
        return sb.toString();
    }

    /**
     * Decompress the words given into chars according to 9bits per char format
     * Based off poketext's implementation of the same in gen4, but uses all 16
     * bits per word as opposed to 15
     *
     * @param chars List of words, beginning with [F100] which is skipped.
     * @return Decompressed list of integers corresponding to characters
     */
    private static List<Integer> decompress(List<Integer> chars) {
        List<Integer> uncomp = new ArrayList<>();
        int j = 1;
        int shift1 = 0;
        int trans = 0;
        while (true) {
            int tmp = chars.get(j);
            tmp = tmp >> shift1;
            int tmp1 = tmp;
            if (shift1 >= 0x10) {
                shift1 -= 0x10;
                if (shift1 > 0) {
                    tmp1 = (trans | ((chars.get(j) << (9 - shift1)) & 0x1FF));
                    if ((tmp1 & 0xFF) == 0xFF) {
                        break;
                    }
                    if (tmp1 != 0x0 && tmp1 != 0x1) {
                        uncomp.add(tmp1);
                    }
                }
            } else {
                tmp1 = ((chars.get(j) >> shift1) & 0x1FF);
                if ((tmp1 & 0xFF) == 0xFF) {
                    break;
                }
                if (tmp1 != 0x0 && tmp1 != 0x1) {
                    uncomp.add(tmp1);
                }
                shift1 += 9;
                if (shift1 < 0x10) {
                    trans = ((chars.get(j) >> shift1) & 0x1FF);
                    shift1 += 9;
                }
                j += 1;
            }
        }
        return uncomp;
    }

    /**
     * Take a byte-array corresponding to a NARC entry and build a list of
     * strings against the gen5 text encryption. Decompresses as appropriate.
     *
     * @param ds The data from this msg.narc entry
     * @return The list of strings
     */

    public static List<String> readTexts(byte[] ds) {
        int pos = 0;
        List<String> strings = new ArrayList<>();
        int numSections, numEntries, tmpCharCount, tmpUnknown, tmpChar;
        int tmpOffset;
        int sectionOffset = 0;
        List<Integer> tableOffsets = new ArrayList<>();
        List<Integer> characterCount = new ArrayList<>();
        List<List<Integer>> encText = new ArrayList<>();
        StringBuilder sb;
        int key;

        numSections = readWord(ds, 0);
        numEntries = readWord(ds, 2);
        pos += 12;
        if (numSections > 0) {
            for (int z = 0; z < numSections; z++) {
                sectionOffset = readLong(ds, pos);
                pos += 4;
            }
            pos = sectionOffset;
            pos += 4;
            for (int j = 0; j < numEntries; j++) {
                tmpOffset = readLong(ds, pos);
                pos += 4;
                tmpCharCount = readWord(ds, pos);
                pos += 4;
                tableOffsets.add(tmpOffset);
                characterCount.add(tmpCharCount);
            }

            for (int j = 0; j < numEntries; j++) {
                tmpCharCount = characterCount.get(j);

                List<Integer> tmpEncChars = new ArrayList<>();
                pos = sectionOffset + tableOffsets.get(j);
                for (int k = 0; k < characterCount.get(j); k++) {
                    tmpChar = readWord(ds, pos);
                    pos += 2;
                    tmpEncChars.add(tmpChar);
                }
                encText.add(tmpEncChars);
                key = encText.get(j).get(characterCount.get(j) - 1) ^ 0xFFFF;
                for (int k = tmpCharCount - 1; k >= 0; k--) {
                    encText.get(j).set(k, (encText.get(j).get(k)) ^ key);
                    key = ((key >>> 3) | (key << 13)) & 0xffff;
                }
                if (encText.get(j).get(0) == 0xF100) {
                    encText.set(j, decompress(encText.get(j)));
                    characterCount.set(j, encText.get(j).size());
                }
                List<String> chars = new ArrayList<>();
                sb = new StringBuilder();
                for (int k = 0; k < characterCount.get(j); k++) {
                    if (encText.get(j).get(k) == 0xFFFF) {
                        chars.add("\\xFFFF");
                    } else {
                        if (encText.get(j).get(k) > 20 && encText.get(j).get(k) <= 0xFFF0
                                && Character.UnicodeBlock.of(encText.get(j).get(k)) != null) {
                            chars.add("" + ((char) encText.get(j).get(k).intValue()));
                        } else {
                            String num = String.format("%04X", encText.get(j).get(k));
                            chars.add("\\x" + num);
                        }
                        sb.append(chars.get(k));
                    }
                }
                strings.add(sb.toString());
            }
        }

        // Parse strings against the table
        strings.replaceAll(string -> bulkReplace(string, pokeToTextPattern, pokeToText));
        return strings;
    }

    private static String bulkReplace(String string, Pattern pattern, Map<String, String> replacements) {
        if (string == null)
            string = "";
        
        Matcher matcher = pattern.matcher(string);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String group = matcher.group(1);
            String replacement = replacements.get(group);
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * Write newStrings to the text datafile originalData, as language 0 (the
     * only one in most releases BUT japanese). Return the resulting binary as a
     * byte-array. Will never use the [F100] compression, even if the original
     * file used it.
     *
     * @param text         The new data.
     * @return The file to write back to the NARC.
     */
    public static byte[] saveEntry(List<String> text) {

        // Parse strings against the reverse table
        text.replaceAll(string -> bulkReplace(string, textToPokePattern, textToPoke));

        int sectionOffset = 16;
        byte[] newSection = makeSection(text);

        byte[] newData = new byte[sectionOffset + newSection.length];
        writeWord(newData, 0, 0x0001); // Sections
        writeWord(newData, 2, text.size()); // Entry Count
        writeLong(newData, 4, newSection.length); // Section Size
        writeLong(newData, 12, sectionOffset); // Section Start
        System.arraycopy(newSection, 0, newData, sectionOffset, newSection.length);
        return newData;
    }

    private static byte[] makeSection(List<String> strings) {
        List<List<Integer>> data = new ArrayList<>();
        int size = 0;
        int offset = 4 + 8 * strings.size();
        int charCount;

        for (int i = 0; i < strings.size(); i++) {
            data.add(parseString(strings.get(i), i));
            size += (data.get(i).size() * 2);
        }

        if (size % 4 == 2) {
            size += 2;
            int tmpKey = ((strings.size() + 2) * 0x2983) & 0xFFFF;
            for (int i = 0; i < data.get(strings.size() - 1).size(); i++) {
                tmpKey = ((tmpKey << 3) | (tmpKey >> 13)) & 0xFFFF;
            }
            data.get(strings.size() - 1).add(0xFFFF ^ tmpKey);
        }

        size += offset;
        byte[] section = new byte[size];
        int pos = 0;
        writeLong(section, pos, size);
        pos += 4;

        for (int i = 0; i < strings.size(); i++) {
            charCount = data.get(i).size();
            writeLong(section, pos, offset);
            pos += 4;
            writeWord(section, pos, charCount);
            pos += 2;
            writeWord(section, pos, 0);
            pos += 2;
            offset += (charCount * 2);
        }

        for (int i = 0; i < strings.size(); i++) {
            for (int word : data.get(i)) {
                writeWord(section, pos, word);
                pos += 2;
            }
        }
        return section;
    }

    private static List<Integer> parseString(String string, int entry_id) {
        List<Integer> chars = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != '\\') {
                chars.add((int) string.charAt(i));
            } else {
                if (((i + 2) < string.length()) && string.charAt(i + 2) == '{') {
                    chars.add((int) string.charAt(i));
                } else {
                    chars.add(Integer.parseInt(string.substring(i + 2, i + 6), 16));
                    i += 5;
                }
            }
        }
        chars.add(0xFFFF);
        int key = ((entry_id + 3) * 0x2983) & 0xFFFF;
        for (int i = 0; i < chars.size(); i++) {
            chars.set(i, (chars.get(i) ^ key) & 0xFFFF);
            key = ((key << 3) | (key >>> 13)) & 0xFFFF;
        }
        return chars;
    }

    private static int readWord(byte[] data, int offset) {
        return (data[offset] & 0xFF) + ((data[offset + 1] & 0xFF) << 8);
    }

    private static int readLong(byte[] data, int offset) {
        return (data[offset] & 0xFF) + ((data[offset + 1] & 0xFF) << 8) + ((data[offset + 2] & 0xFF) << 16)
                + ((data[offset + 3] & 0xFF) << 24);
    }

    protected static void writeWord(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }

    private static void writeLong(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
        data[offset + 2] = (byte) ((value >> 16) & 0xFF);
        data[offset + 3] = (byte) ((value >> 24) & 0xFF);
    }
}
