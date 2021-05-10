package me.seaac132.lexicalanalyzer;

import java.util.HashSet;

public class FloatingAnalyzer {

    public FloatingAnalyzer(String line) {
        this.line = line;
    }

    private String line;
    private String currentValue = "";
    private int pointer;

    private void addChar() {
        currentValue += line.charAt(pointer);
    }

    private char getChar() {
        return line.charAt(pointer);
    }

    public static String tryAnalyze(String line) {
        FloatingAnalyzer floatingAnalyzer = new FloatingAnalyzer(line);
        try {
            floatingAnalyzer.analyze();
            return floatingAnalyzer.line;
        } catch (Exception ex) {
            return null;
        }
    }

    public byte analyze() throws Exception {
        HashSet<Character> set = new HashSet<>();
        for (pointer = 0; pointer < line.length(); pointer++) {
            char token = getChar();
            if (token == '.') {
                if (set.contains(token)) {
                    throw new Exception("Found 2 decimal point");
                }
                set.add(token);
                addChar();
            }
            else if (token == '_') {
                addChar();
            }
            else if (Character.isDigit(token)) {
                addChar();
            }
            else if (token == '+' || token == '-') {
                if (pointer != 0 && Character.toUpperCase(currentValue.charAt(currentValue.length() - 1)) != 'E') {
                    throw new Exception("Found +/- inside");
                }
                addChar();
            }
            else if (Character.toUpperCase(token) == 'E') {
                if (set.contains(Character.toUpperCase(token))) {
                    throw new Exception("Found 2 exponent");
                }
                set.add(Character.toUpperCase(token));
                addChar();
            }
            else {
                throw new Exception("Found another character '" + token + "'");
            }
        }
        line = line.replace("_", "");
        if (set.size() == 0) {
            return 0;
        }
        return 1;
    }

    public double getFloatingLiteral() {
        return Double.parseDouble(line);
    }

}
