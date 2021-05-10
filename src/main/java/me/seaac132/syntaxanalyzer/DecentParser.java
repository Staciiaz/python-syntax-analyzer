package me.seaac132.syntaxanalyzer;

import me.seaac132.lexicalanalyzer.LexicalAnalyzer;
import me.seaac132.lexicalanalyzer.LexicalException;
import me.seaac132.lexicalanalyzer.Token;
import me.seaac132.lexicalanalyzer.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DecentParser extends ParseTree {

    private List<Token> lexemes;
    private int listPointer;

    public DecentParser(String src) {
        this.lexemes = new ArrayList<>();
        this.listPointer = 0;
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
        String[] lines = new String[1];
        lines[0] = src;
        try {
            lexicalAnalyzer.analyze(lines);
            lexemes.addAll(lexicalAnalyzer.getAll());
        } catch (LexicalException e) {
            e.printStackTrace();
            System.out.println("Lexical Error");
        }
    }

    private List<ParseTreeNode> getRule(ParseTreeNodeTag context) {
        if (context == ParseTreeNodeTag.PROGRAM) {
            List<ParseTreeNode> products = new ArrayList<>();
            ParseTreeNode product1 = new ParseTreeNode(ParseTreeNodeTag.PROGRAM); //program -> stmt = stmt
            product1.addChild(new ParseTreeNode(ParseTreeNodeTag.STATEMENT));
            product1.addChild(ParseToken.ASSIGN_OP);
            product1.addChild(new ParseTreeNode(ParseTreeNodeTag.STATEMENT));
            ParseTreeNode product2 = new ParseTreeNode(ParseTreeNodeTag.PROGRAM); //program -> stmt
            product2.addChild(new ParseTreeNode(ParseTreeNodeTag.STATEMENT));
            products.add(product1);
            products.add(product2);
            return products;
        } else if (context == ParseTreeNodeTag.STATEMENT) {
            List<ParseTreeNode> products = new ArrayList<>();
            ParseTreeNode product1 = new ParseTreeNode(ParseTreeNodeTag.STATEMENT); //stmt -> term
            product1.addChild(new ParseTreeNode(ParseTreeNodeTag.TERM));
            ParseTreeNode product2 = new ParseTreeNode(ParseTreeNodeTag.STATEMENT); //stmt -> term op term
            product2.addChild(new ParseTreeNode(ParseTreeNodeTag.TERM));
            product2.addChild(ParseToken.OPERATOR);
            product2.addChild(new ParseTreeNode(ParseTreeNodeTag.TERM));
            products.add(product1);
            products.add(product2);
            return products;
        } else if (context == ParseTreeNodeTag.TERM) {
            List<ParseTreeNode> products = new ArrayList<>();
            ParseTreeNode product1 = new ParseTreeNode(ParseTreeNodeTag.TERM); //term -> id
            product1.addChild(ParseToken.IDENTIFIER);
            ParseTreeNode product2 = new ParseTreeNode(ParseTreeNodeTag.TERM); //term -> id op id
            product2.addChild(ParseToken.IDENTIFIER);
            product2.addChild(ParseToken.OPERATOR);
            product2.addChild(ParseToken.IDENTIFIER);
            products.add(product1);
            products.add(product2);
            return products;
        }
        return Collections.emptyList();
    }

    private int _analyze(ParseTreeNodeTag context, int initial) {
        List<ParseTreeNode> rules = getRule(context);
        int pointer = initial;
        for (int i = 0; i < rules.size(); i++) {
            ParseTreeNode rule = rules.get(i);
            System.out.println("Rule " + rule);
            for (int j = 0; j < rule.getChildrenCount(); j++) {
                AbstractParseTreeNodeData c = rule.getChild(j);
                System.out.println("Select " + c);
                if (c instanceof ParseTreeNode) {
                    ParseTreeNode node = (ParseTreeNode) c;
                    int increment = _analyze(node.getTag(), pointer);
                    if (increment > 0) {
                        System.out.println("Match " + c);
                        pointer += increment;
                    } else {
                        System.out.println("Backtracking ...");
                        pointer = 0;
                        break;
                    }
                }
                else if (c instanceof ParseTreeNodeValue) {
                    ParseTreeNodeValue value = (ParseTreeNodeValue) c;
                    Token parseToken = value.getValue();
                    Token token = lexemes.get(pointer);
                    if (token != null && ParseToken.compareToken(token, parseToken)) {
                        System.out.println(token + " " + parseToken);
                        pointer++;
                    } else {
                        System.out.println("Backtracking ...");
                        break;
                    }
                }
            }
        }
        return pointer - initial;
    }

    public void analyze() {
        int increment = _analyze(ParseTreeNodeTag.PROGRAM, 0);
        if (increment > 0) {
            System.out.println("Accepted");
        } else {
            System.out.println("Syntax Error");
        }
    }

}
