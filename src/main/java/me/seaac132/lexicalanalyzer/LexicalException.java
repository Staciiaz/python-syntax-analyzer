package me.seaac132.lexicalanalyzer;

import java.util.List;

public class LexicalException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String data;
    private int row;
    private int column;

    private List<LexicalException> list;

    public LexicalException(String message, String data, int row, int column) {
        super(message);
        this.data = data;
        this.row = row;
        this.column = column;
    }

    public LexicalException(List<LexicalException> list) {
        this.list = list;
    }

    public LexicalException(String message, Token token) {
        this(message, token.getData(), token.getRow(), token.getColumn());
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

    public List<LexicalException> getList() {
        return list;
    }

    public String toString() {
        return super.getMessage() + " on row " + row + " column " + column + ". '" + data + "'";
    }

}
