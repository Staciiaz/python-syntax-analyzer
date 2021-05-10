package me.seaac132.lexicalanalyzer;

public class Token {

    private TokenType type;
    private String data;
    private int row;
    private int column;

    public Object tag;
    public boolean index;

    public Token(TokenType type, String data, int row, int column) {
        this.type = type;
        this.data = data;
        this.row = row;
        this.column = column;

        this.index = false;
    }

    public TokenType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public String toString() {
        return data;
    }

}
