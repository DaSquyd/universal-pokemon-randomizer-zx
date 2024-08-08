package com.dabomstew.pkrandom.romhandlers;

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.SysConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ParagonLiteDocWriter {
    public static class Table {
        public static class Column {
            String title;
            int width;

            public Column(String title) {
                this.title = title;
                this.width = title.length();
            }

            public void updateWidth(String rowElement) {
                int elementLength = rowElement.length();
                if (elementLength > width)
                    width = elementLength;
            }
        }

        Column[] columns;
        List<String> rowTitles = new ArrayList<>();
        List<String[]> rows = new ArrayList<>();
        int maxRowTitleSize = 0;

        public Table(String... columnTitles) {
            this.columns = new Column[columnTitles.length];
            for (int i = 0; i < columnTitles.length; ++i) {
                this.columns[i] = new Column(columnTitles[i]);
            }
        }

        public int getWidth() {
            return columns.length;
        }

        public int getRowCount() {
            return rows.size();
        }

        public void addRow(String title, Collection<String> row) {
            String[] rowArray = new String[row.size()];
            row.toArray(rowArray);
            addRow(title, rowArray);
        }

        public void addRow(String title, String... row) {
            assert row.length == getWidth();

            rowTitles.add(title);
            rows.add(row);

            if (title.length() > maxRowTitleSize)
                maxRowTitleSize = title.length();

            for (int i = 0; i < getWidth(); ++i) {
                columns[i].updateWidth(row[i]);
            }
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("| ");
            stringBuilder.append(" ".repeat(maxRowTitleSize));
            stringBuilder.append(" |");
            for (Column column : columns) {
                stringBuilder.append(' ');
                stringBuilder.append(column.title);
                stringBuilder.append(" ".repeat(column.width - column.title.length()));
                stringBuilder.append(" |");
            }
            stringBuilder.append(System.lineSeparator());

            stringBuilder.append("|-");
            stringBuilder.append("-".repeat(maxRowTitleSize));
            stringBuilder.append("-|");
            for (Column column : columns) {
                stringBuilder.append('-');
                stringBuilder.append("-".repeat(column.width));
                stringBuilder.append("-|");
            }
            stringBuilder.append(System.lineSeparator());

            for (int i = 0; i < getRowCount(); ++i) {
                String title = rowTitles.get(i);
                stringBuilder.append("| ");
                stringBuilder.append(title);
                stringBuilder.append(" ".repeat(maxRowTitleSize - title.length()));
                stringBuilder.append(" |");
                String[] rowStrs = rows.get(i);
                for (int j = 0; j < rowStrs.length; ++j) {
                    int maxWidth = columns[j].width;
                    String rowStr = rowStrs[j];

                    stringBuilder.append(' ');
                    stringBuilder.append(rowStr);
                    stringBuilder.append(" ".repeat(maxWidth - rowStr.length()));
                    stringBuilder.append(" |");
                }
                stringBuilder.append(System.lineSeparator());
            }

            return stringBuilder.toString();
        }
    }

    StringBuilder sb = new StringBuilder();

    public void write(String filepath) {
        FileWriter myWriter;
        try {
            File file = new File(filepath + ".md");
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();

            myWriter = new FileWriter(file);
            myWriter.write(sb.toString());
            myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void paragraph(String str) {
        sb.append(str);
        sb.append(System.lineSeparator());
    }

    public void paragraph(String... strs) {
        for (String str : strs) {
            sb.append(str);
            sb.append(System.lineSeparator());
        }
    }

    public void lineBreak() {
        sb.append(System.lineSeparator());
    }

    public void h1(String str) {
        sb.append("# ");
        sb.append(str);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
    }

    public void h1(String str, String id) {
        sb.append("# ");
        sb.append(str);
        sb.append(" {#");
        sb.append(id);
        sb.append("}");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
    }

    public void h2(String str) {
        sb.append("## ");
        sb.append(str);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
    }

    public void h2(String str, String id) {
        sb.append("## ");
        sb.append(str);
        sb.append(" {#");
        sb.append(id);
        sb.append("}");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
    }

    public void h3(String str) {
        sb.append("### ");
        sb.append(str);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
    }

    public void h3(String str, String id) {
        sb.append("### ");
        sb.append(str);
        sb.append(" {#");
        sb.append(id);
        sb.append("}");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
    }

    public static String bold(String str) {
        return String.format("**%s**", str);
    }

    public static String italicized(String str) {
        return String.format("*%s*", str);
    }

    public void blockQuote(Collection<String> strs) {
        for (String str : strs) {
            String[] subStrs = str.split("(\r\n)|(\n)");
            for (String subStr : subStrs) {
                sb.append("> ");
                sb.append(subStr);
                sb.append(System.lineSeparator());
            }
        }
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
    }

    public void blockQuote(String... strs) {
        for (String str : strs) {
            String[] subStrs = str.split("(\r\n)|(\n)");
            for (String subStr : subStrs) {
                sb.append("> ");
                sb.append(subStr);
                sb.append(System.lineSeparator());
            }
        }
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
    }

    public void orderedList(Collection<String> strs) {
        int index = 1;
        for (String str : strs) {
            sb.append(index);
            sb.append(". ");
            sb.append(str);
            sb.append(System.lineSeparator());
            ++index;
        }
        sb.append(System.lineSeparator());
    }

    public void orderedList(String... strs) {
        int index = 1;
        for (String str : strs) {
            sb.append(index);
            sb.append(". ");
            sb.append(str);
            sb.append(System.lineSeparator());
            ++index;
        }
        sb.append(System.lineSeparator());
    }

    public void unorderedList(Collection<String> strs) {
        for (String str : strs) {
            sb.append("- ");
            sb.append(str);
            sb.append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
    }

    public void unorderedList(String... strs) {
        for (String str : strs) {
            sb.append("- ");
            sb.append(str);
            sb.append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
    }

    public static String code(String str) {
        return String.format("`%s`", str);
    }

    public void horizontalRule() {
        sb.append("---");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
    }

    public static String link(String title, String url) {
        return String.format("[%s](%s)", title, url);
    }

    public static String image(String altText, String url) {
        return String.format("![%s](%s)", altText, url);
    }

    public void table(Table table) {
        sb.append(table.toString());
        sb.append(System.lineSeparator());
    }

    public void codeBlock(String str) {
        sb.append("```");
        sb.append(System.lineSeparator());
        sb.append(str);
        sb.append(System.lineSeparator());
        sb.append("```");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
    }

    public void codeBlock(String... strs) {
        sb.append("```");
        sb.append(System.lineSeparator());
        for (String str : strs) {
            sb.append(str);
            sb.append(System.lineSeparator());
        }
        sb.append("```");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
    }

    public static String footnote(int number) {
        return String.format("[^%d]", number);
    }

    public void endnote(int footnoteNumber, String text) {
        sb.append("[^");
        sb.append(footnoteNumber);
        sb.append("]: ");
        sb.append(text);
        sb.append(System.lineSeparator());
    }

    public void definitionList(String term, String... definitions) {
        sb.append(term);
        sb.append(System.lineSeparator());

        for (String definition : definitions) {
            sb.append(": ");
            sb.append(definition);
            sb.append(System.lineSeparator());
        }

        sb.append(System.lineSeparator());
    }

    public static String strikethrough(String str) {
        return String.format("~~%s~~", str);
    }

    public static String highlight(String str) {
        return String.format("==%s==", str);
    }

    public static String subscript(String str) {
        return String.format("~%s~", str);
    }

    public static String superscript(String str) {
        return String.format("^%s^", str);
    }
}
