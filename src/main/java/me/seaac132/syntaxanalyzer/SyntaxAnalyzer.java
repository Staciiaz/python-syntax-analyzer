package me.seaac132.syntaxanalyzer;

import me.seaac132.lexicalanalyzer.LexicalAnalyzer;
import me.seaac132.lexicalanalyzer.LexicalException;
import me.seaac132.lexicalanalyzer.Token;
import me.seaac132.lexicalanalyzer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class SyntaxAnalyzer extends ParseTree {

    private String statement;
    private List<Token> lexemes;

    public SyntaxAnalyzer(String statement) {
        this.statement = statement;
        this.lexemes = new ArrayList<>();
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
        String[] lines = new String[1];
        lines[0] = statement;
        try {
            lexicalAnalyzer.analyze(lines);
            lexemes.addAll(lexicalAnalyzer.getAll());
        } catch (LexicalException e) {
            e.printStackTrace();
        }
    }

    public String getStatement() {
        return statement;
    }

    public List<Token> getLexemes() {
        return lexemes;
    }

    private void _analyze(List<Token> lexemes, ParseTreeNode current) {
        if (current.getTag() == ParseTreeNodeTag.PROGRAM) {
            _analyze(lexemes, current.addChild(new ParseTreeNode(ParseTreeNodeTag.STATEMENT)));
            _analyze(lexemes, current.addChild(new ParseTreeNode(ParseTreeNodeTag.STATEMENT)));
        } else if (current.getTag() == ParseTreeNodeTag.STATEMENT) {
            _analyze(lexemes, current.addChild(new ParseTreeNode(ParseTreeNodeTag.TERM)));
            _analyze(lexemes, current.addChild(new ParseTreeNode(ParseTreeNodeTag.TERM)));
        }
    }

    public void analyze() {
        List<Token> lexemes = getLexemes();
        ParseTree tree = new ParseTree();
        _analyze(lexemes, tree.getRoot());
        System.out.println("Lexemes: " + lexemes);
        System.out.println("Tree: " + tree.toList());
    }

}
