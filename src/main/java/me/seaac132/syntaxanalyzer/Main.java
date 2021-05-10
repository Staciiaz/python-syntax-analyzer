package me.seaac132.syntaxanalyzer;

public class Main {

    public static void main(String[] args) {
        String src = "a = b";
        System.out.println("Source Code: " + src);
        DecentParser parser = new DecentParser(src);
        parser.analyze();
    }

}
