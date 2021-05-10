package me.seaac132.lexicalanalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LexicalAnalyzer {

    private LinkedList<Token> identifiers = new LinkedList<>();
    private LinkedList<Token> operators = new LinkedList<>();
    private LinkedList<Token> literals = new LinkedList<>();
    private LinkedList<Token> brackets = new LinkedList<>();
    private LinkedList<Token> separators = new LinkedList<>();
    private LinkedList<Token> reserve_words = new LinkedList<>();

    private HashMap<String, Object> identifier_types = new HashMap<>();
    private HashSet<String> functions = new HashSet<>();

    private me.seaac132.lexicalanalyzer.Python python = new me.seaac132.lexicalanalyzer.Python();

    public LexicalAnalyzer() {

    }

    public List<Token> getIdentifiers() {
        return identifiers;
    }

    public List<Token> getOperators() {
        return operators;
    }

    public List<Token> getLiterals() {
        return literals;
    }

    public List<Token> getBrackets() {
        return brackets;
    }

    public List<Token> getSeparators() {
        return separators;
    }

    public List<Token> getReserveWords() {
        return reserve_words;
    }

    public List<Token> getAll() {
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.addAll(identifiers);
        tokens.addAll(operators);
        tokens.addAll(literals);
        tokens.addAll(brackets);
        tokens.addAll(separators);
        tokens.addAll(reserve_words);
        tokens.sort((t1, t2) -> {
            if (t1.getRow() != t2.getRow()) {
                return t1.getRow() - t2.getRow();
            } else {
                return t1.getColumn() - t2.getColumn();
            }
        });
        return tokens;
    }

    public String getType(Token identifier) {
        String name = identifier.getData();
        if (python.hasBuiltinFunction(name) || functions.contains(name)) {
            return "function";
        } else if (identifier_types.containsKey(name)) {
            return (String)identifier_types.get(name);
        }
        return "undefined";
    }

    private String currentValue = "";
    private char quoteChar = '\0';
    private int currentRow = 0;
    private int currentColumn = 0;
    private Stack<Token> bracketStack = new Stack<>();
    private Stack<Token> arrayBracketStack = new Stack<>();
    private Stack<Token> blockBracketStack = new Stack<>();
    private char prevToken = '\0';

    private LinkedList<LexicalException> exceptions = new LinkedList<>();

    private boolean checkCurrentValue() {
        if (quoteChar != '\0') {
            //in quote
            Token token = new Token(TokenType.LITERAL, currentValue, currentRow, currentColumn);
            token.tag = "string";
            if (!arrayBracketStack.isEmpty()) {
                token.index = true;
            }
            identifier_types.put(currentValue, token.tag);
            literals.add(token);
            quoteChar = '\0';
            currentValue = "";
            return true;
        }
        if (currentValue.isEmpty()) {
            //current value empty
            return true;
        }
        if (python.hasOperator1(currentValue.charAt(0))) {
            //start with operator
            if (python.hasOperator2(currentValue)) {
                //is operator 2
                operators.add(new Token(TokenType.OPERATOR, currentValue, currentRow, currentColumn));
                currentValue = "";
                return true;
            } else if (currentValue.length() == 1) {
                //is operator 1
                operators.add(new Token(TokenType.OPERATOR, currentValue, currentRow, currentColumn));
                currentValue = "";
                return true;
            } else if (currentValue.charAt(0) != '-') {
                String data = currentValue;
                currentValue = "";
                exceptions.add(new LexicalException("Unknown operator", data, currentRow, currentColumn));
                return false;
            }
        }
        if (python.hasReserveWord(currentValue)) {
            //is reserve word
            reserve_words.add(new Token(TokenType.KEYWORD, currentValue, currentRow, currentColumn));
            currentValue = "";
            return true;
        }
        FloatingAnalyzer floatingAnalyzer = new FloatingAnalyzer(currentValue);
        try {
            byte data = floatingAnalyzer.analyze();
            //is number
            Token token = new Token(TokenType.LITERAL, currentValue, currentRow, currentColumn);
            token.tag = data == 1 ? "float" : "int";
            if (!arrayBracketStack.isEmpty()) {
                token.index = true;
            }
            literals.add(token);
            currentValue = "";
            return true;
        } catch (Exception ex) {}
        //otherwise
        if (currentValue.matches("[A-Za-z_]([A-Za-z_0-9]*)")) {
            identifiers.add(new Token(TokenType.IDENTIFIER, currentValue, currentRow, currentColumn));
            currentValue = "";
        } else {
            exceptions.add(new LexicalException("Identifier name is not allow", currentValue, currentRow, currentColumn));
            currentValue = "";
            return false;
        }
        return true;
    }

    public boolean checkAssignment(int row) {
        List<Token> operatorsOnRow = operators.stream()
                .filter(x -> x.getData().equals("=") && x.getRow() == row).collect(Collectors.toList());
        List<Token> defOnRow = reserve_words.stream()
                .filter(x -> x.getData().equals("def") && x.getRow() == row).collect(Collectors.toList());
        if (operatorsOnRow.size() > 0) {
            LinkedList<Token> tokens = new LinkedList<>();
            tokens.addAll(identifiers.stream().filter(x -> x.getRow() == row).collect(Collectors.toList()));
            tokens.addAll(operators.stream().filter(x -> x.getRow() == row).collect(Collectors.toList()));
            tokens.addAll(literals.stream().filter(x -> x.getRow() == row).collect(Collectors.toList()));
            Comparator<Token> tokenComparator = new Comparator<Token>() {
                @Override
                public int compare(Token token1, Token token2) {
                    return token1.getColumn() - token2.getColumn();
                }
            };
            Token assignOp = tokens.stream()
                    .filter(x -> x.getType() == TokenType.OPERATOR && x.getData().equals("=")).max(tokenComparator).get();
            Optional<Token> _beforeAssign = tokens.stream()
                    .filter(x -> x.getType() == TokenType.OPERATOR && !x.getData().equals("=") && x.getColumn() < assignOp.getColumn()).findAny();
            if (_beforeAssign.isPresent()) {
                exceptions.add(new LexicalException("Operation before assignment operator", _beforeAssign.get()));
                return false;
            }
			/*
			Optional<Token> _undefined = tokens.stream()
					.filter(x -> x.getType() == TokenType.IDENTIFIER && x.getColumn() > assignOp.getColumn() && !identifier_types.containsKey(x.getData()))
					.findFirst();
			Optional<Token> _literalBefore = tokens.stream()
					.filter(x -> x.getType() == TokenType.LITERAL && x.getColumn() < assignOp.getColumn() && !x.index).findAny();
			if (_undefined.isPresent()) {
				exceptions.add(new LexicalException("Identifier not defined", _undefined.get()));
				return false;
			}
			else if (_literalBefore.isPresent()) {
				exceptions.add(new LexicalException("Literal before assignment operator", _literalBefore.get()));
				return false;
			}
			*/
            Optional<Token> _lastLiteral = tokens.stream()
                    .filter(x -> x.getType() == TokenType.LITERAL && x.getColumn() > assignOp.getColumn()).findFirst();
            if (_lastLiteral.isPresent()) {
                Token lastLiteral = _lastLiteral.get();
                for (Token identifier : tokens.stream()
                        .filter(x -> x.getType() == TokenType.IDENTIFIER && x.getColumn() < assignOp.getColumn()).collect(Collectors.toList())) {
                    identifier.tag = lastLiteral.tag;
                    identifier_types.put(identifier.getData(), identifier.tag);
                }
            }
        }
        if (defOnRow.size() > 0) {
            Optional<Token> _openBracket = brackets.stream().filter(x -> x.getRow() == row && x.getData().equals("(")).findFirst();
            if (_openBracket.isPresent()) {
                Token openBracket = _openBracket.get();
                List<Token> identifiersOnRow = identifiers.stream()
                        .filter(x -> x.getRow() == row && x.getColumn() < openBracket.getColumn()).collect(Collectors.toList());
                for (Token identifier : identifiersOnRow) {
                    functions.add(identifier.getData());
                }
            }
        }
        return true;
    }

    private char getChar() {
        return currentValue.charAt(currentValue.length() - 1);
    }

    public void analyze(String[] lines) throws LexicalException {
        for (String line : lines) {
            for (int i = 0; i < line.length(); i++) {
                char token = line.charAt(i);
                if (token == '#') {
                    //handle if comment
                    break;
                }
                else if (token == '"') {
                    //handle if is quote
                    if (quoteChar == '"') {
                        checkCurrentValue();
                        currentColumn = i;
                        quoteChar = '\0';
                    } else {
                        checkCurrentValue();
                        currentColumn = i;
                        quoteChar = '"';
                    }
                }
                else if (token == '\'') {
                    if (quoteChar == '\'') {
                        checkCurrentValue();
                        currentColumn = i;
                        quoteChar = '\0';
                    } else {
                        checkCurrentValue();
                        currentColumn = i;
                        quoteChar = '\'';
                    }
                }
                else if (token == '(' && quoteChar == '\0') {
                    //handle if is open bracket
                    Token _token = new Token(TokenType.BRACKETS, Character.toString(token), currentRow, i);
                    brackets.add(_token);
                    checkCurrentValue();
                    currentColumn = i + 1;
                    bracketStack.push(_token);
                }
                else if (token == ')' && quoteChar == '\0') {
                    //handle if is close bracket
                    Token _token = new Token(TokenType.BRACKETS, Character.toString(token), currentRow, i);
                    brackets.add(_token);
                    checkCurrentValue();
                    currentColumn = i;
                    if (bracketStack.isEmpty()) {
                        exceptions.add(new LexicalException("Close bracket on no open bracket", _token));
                        continue;
                    }
                    bracketStack.pop();
                }
                else if (token == '[' && quoteChar == '\0') {
                    //handle if is open array bracket
                    Token _token = new Token(TokenType.BRACKETS, Character.toString(token), currentRow, i);
                    brackets.add(_token);
                    checkCurrentValue();
                    currentColumn = i + 1;
                    arrayBracketStack.push(_token);
                }
                else if (token == ']' && quoteChar == '\0') {
                    //handle if is close array bracket
                    Token _token = new Token(TokenType.BRACKETS, Character.toString(token), currentRow, i);
                    brackets.add(_token);
                    checkCurrentValue();
                    currentColumn = i;
                    if (arrayBracketStack.isEmpty()) {
                        exceptions.add(new LexicalException("Close bracket on no open bracket", _token));
                        continue;
                    }
                    arrayBracketStack.pop();
                }
                else if (token == '{' && quoteChar == '\0') {
                    //handle if is open array bracket
                    Token _token = new Token(TokenType.BRACKETS, Character.toString(token), currentRow, i);
                    brackets.add(_token);
                    checkCurrentValue();
                    currentColumn = i + 1;
                    blockBracketStack.push(_token);
                }
                else if (token == '}' && quoteChar == '\0') {
                    //handle if is close block bracket
                    Token _token = new Token(TokenType.BRACKETS, Character.toString(token), currentRow, i);
                    brackets.add(_token);
                    checkCurrentValue();
                    currentColumn = i;
                    if (blockBracketStack.isEmpty()) {
                        exceptions.add(new LexicalException("Close bracket on no open bracket", _token));
                        continue;
                    }
                    blockBracketStack.pop();
                }
                else if (token == '\\' && quoteChar == '\0') {
                    //handle if is colon
                    separators.add(new Token(TokenType.SEPARATORS, Character.toString(token), currentRow, i));
                    checkCurrentValue();
                    currentColumn = i;
                }
                else if (token == ':' && quoteChar == '\0') {
                    //handle if is colon
                    separators.add(new Token(TokenType.SEPARATORS, Character.toString(token), currentRow, i));
                    checkCurrentValue();
                    currentColumn = i;
                }
                else if (token == ',' && quoteChar == '\0') {
                    //handle if is comma
                    separators.add(new Token(TokenType.SEPARATORS, Character.toString(token), currentRow, i));
                    checkCurrentValue();
                    currentColumn = i + 1;
                }
                else if (python.hasOperator1(token) && quoteChar == '\0') {
                    //handle if is operator
                    if (prevToken == ',') {
                        exceptions.add(new LexicalException("Unknown operator", prevToken + Character.toString(token), currentRow, currentColumn));
                        continue;
                    }
                    else if (currentValue.isEmpty() || !python.hasOperator1(currentValue.charAt(0))) {
                        checkCurrentValue();
                        currentColumn = i;
                    }
                    else if (Character.toUpperCase(getChar()) != 'E') {
                        //currentColumn = i;
                    }
                    currentValue += token;
                }
                else if (token == ' ' || token == '\n') {
                    //handle if is space
                    if (quoteChar != '\0') {
                        currentValue += token;
                    } else {
                        checkCurrentValue();
                        currentColumn = i + 1;
                    }
                }
                else if (token == '.' && quoteChar == '\0') {
                    if (!currentValue.isEmpty() && FloatingAnalyzer.tryAnalyze(currentValue) != null) {
                        currentValue += token;
                    }
                    else {
                        separators.add(new Token(TokenType.SEPARATORS, Character.toString(token), currentRow, i));
                        checkCurrentValue();
                        currentColumn = i + 1;
                    }
                }
                else if (Character.isLetter(token)) {
                    //handle if is letter
                    if (Character.toUpperCase(token) == 'E' && !currentValue.isEmpty() && FloatingAnalyzer.tryAnalyze(currentValue) != null) {
                        currentValue += token;
                    }
                    else {
                        if (!currentValue.isEmpty() && (python.hasOperator1(currentValue.charAt(0)) || python.hasOperator2(currentValue))) {
                            //found another character but currentValue is operator
                            checkCurrentValue();
                            currentColumn = i;
                        }
                        currentValue += token;
                    }
                }
                else if (Character.isDigit(token)) {
                    //handle if is digit
                    if (!currentValue.isEmpty()) {
                        if ((python.hasOperator1(currentValue.charAt(0)) || python.hasOperator2(currentValue))
                                && currentValue.charAt(0) != '-') {
                            //found digit but currentValue is operator
                            checkCurrentValue();
                            currentColumn = i;
                        }
                    }
                    currentValue += token;
                }
                else if (quoteChar != '\0') {
                    //handle if is in quote
                    currentValue += token;
                }
                else if (token == '_') {
                    currentValue += token;
                }
                else {
                    //otherwise
                    exceptions.add(new LexicalException("Unknown character", Character.toString(token), currentRow, i));
                    continue;
                }
                prevToken = token;
            }
            checkCurrentValue();
            //checkAssignment(currentRow);
            currentRow++;
            currentColumn = 0;
        }
        System.out.println("Analyze " + currentRow + " lines of code complete.");
        /*
        System.out.println("Checking bracket ...");
        if (bracketStack.size() > 0) {
            while (!bracketStack.isEmpty()) {
                Token bracketToken = bracketStack.pop();
                exceptions.add(new LexicalException("Not enough close bracket", bracketToken));
            }
        }
        if (arrayBracketStack.size() > 0) {
            while (!arrayBracketStack.isEmpty()) {
                Token arrayBracketToken = arrayBracketStack.pop();
                exceptions.add(new LexicalException("Not enough close bracket", arrayBracketToken));
            }
        }
         */
        if (exceptions.size() > 0) {
            throw new LexicalException(exceptions);
        }
    }

    public void printResult() {
        System.out.println("Identifiers: " + identifiers.size());
        System.out.println("Operators: " + operators.size());
        System.out.println("Literals: " + literals.size());
        System.out.println("Brackets: " + brackets.size());
        System.out.println("Separators: " + separators.size());
        System.out.println("Reserve Words: " + reserve_words.size());
    }

    public void write(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            BufferedWriter buffer = new BufferedWriter(writer);
            System.out.println("Write result into csv file ...");
            buffer.write("Token,Type,Row,Column\n");
            for (Token token : getAll()) {
                String line = "\"" + token.getData() + "\"," + token.getType() + "," + token.getRow() + "," + token.getColumn() + "\n";
                buffer.write(line);
            }
            System.out.println("Write result complete.");
            buffer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeError(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            BufferedWriter buffer = new BufferedWriter(writer);
            for (LexicalException exception : exceptions) {
                buffer.write(exception + "\n");
            }
            System.out.println("Error! See error.txt for more detail.");
            buffer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
