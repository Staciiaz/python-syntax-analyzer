package me.seaac132.syntaxanalyzer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Recursive {

    private String string;
    private int stringPointer;

    public Recursive(String string) {
        this.string = string;
        this.stringPointer = 0;
    }

    private char getChar() {
        if (stringPointer < string.length()) {
            return string.charAt(stringPointer);
        } else {
            return 0;
        }
    }

    private List<String> getRules(String context) {
        if (context.equals("S")) {
            return Arrays.asList("ABC", "DEF", "GHI");
        } else if (context.equals("A")) {
            return Arrays.asList("ab", "gh", "m");
        } else if (context.equals("B")) {
            return Arrays.asList("cd", "ij", "n");
        } else if (context.equals("C")) {
            return Arrays.asList("ef", "kl", "o");
        } else if (context.equals("D")) {
            return Arrays.asList("a");
        } else if (context.equals("E")) {
            return Arrays.asList("b");
        } else if (context.equals("G")) {
            return Arrays.asList("d");
        } else if (context.equals("F")) {
            return Arrays.asList("d");
        } else if (context.equals("H")) {
            return Arrays.asList("e");
        } else if (context.equals("I")) {
            return Arrays.asList("f");
        }
        return Collections.emptyList();
    }

    private boolean checkContext(String context) {
        List<String> rules = getRules(context);
        for (int i = 0; i < rules.size(); i++) {
            String rule = rules.get(i);
            for (int j = 0; j < rule.length(); j++) {
                char c = rule.charAt(j);
                if (Character.isUpperCase(c)) {
                    checkContext(Character.toString(c));
                }
                else if (c == getChar()) {
                    stringPointer++;
                }
                else {
                    break;
                }
            }
            if (stringPointer == string.length()) {
                return true;
            }
        }
        return false;
    }

    public void execute() {
        boolean result = checkContext("S");
        if (result) {
            System.out.println("String accepted");
        } else {
            System.out.println("Syntax error");
        }
    }

}
