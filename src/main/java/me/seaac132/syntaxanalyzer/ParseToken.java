package me.seaac132.syntaxanalyzer;

import me.seaac132.lexicalanalyzer.Token;
import me.seaac132.lexicalanalyzer.TokenType;

public class ParseToken {

    public static Token IDENTIFIER = new Token(TokenType.IDENTIFIER, "IDENTIFIER", -1, -1);
    public static Token OPERATOR = new Token(TokenType.OPERATOR, "OPERATOR", -1, -1);
    public static Token ASSIGN_OP = new Token(TokenType.OPERATOR, "ASSIGN_OP", -1, -1);
    public static Token LITERAL = new Token(TokenType.IDENTIFIER, "LITERAL", -1, -1);

    public static boolean compareToken(Token token, Token parseToken) {
        if (token.getType() == TokenType.OPERATOR) {
            String value = token.getData();
            if (value.equals("=") && parseToken == ASSIGN_OP) {
                return true;
            } else if (parseToken == OPERATOR) {
                return true;
            }
        } else if (token.getType() == TokenType.IDENTIFIER) {
            if (parseToken == IDENTIFIER) {
                return true;
            }
        } else if (token.getType() == TokenType.LITERAL) {
            if (parseToken == LITERAL) {
                return true;
            }
        }
        return false;
    }

}

