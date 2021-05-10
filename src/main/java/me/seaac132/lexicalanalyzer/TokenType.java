package me.seaac132.lexicalanalyzer;

public enum TokenType {

    IDENTIFIER("identifier"),
    OPERATOR("operator"),
    LITERAL("literal"),
    BRACKETS("bracket"),
    SEPARATORS("separator"),

    ASSIGN_OP("assign_operator"),
    KEYWORD("keyword");

    private String name;

    TokenType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

}
