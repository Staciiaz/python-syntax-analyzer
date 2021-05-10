package me.seaac132.lexicalanalyzer;

import java.util.HashSet;

public class Python {

    private HashSet<Character> python_ops1 = new HashSet<Character>();
    private HashSet<String> python_ops2 = new HashSet<String>();
    private HashSet<String> python_reserves = new HashSet<String>();
    private HashSet<String> python_builtin_func = new HashSet<String>();
    private HashSet<Character> integer_chars = new HashSet<Character>();

    public Python() {
        initOperators1();
        initOperators2();
        initReserveWords();
        initBuiltinFunctions();
        initIntCharacters();
    }

    public boolean hasOperator1(char operator) {
        return python_ops1.contains(operator);
    }

    public boolean hasOperator2(String operator) {
        return python_ops2.contains(operator);
    }

    public boolean hasReserveWord(String reserveWord) {
        return python_reserves.contains(reserveWord);
    }

    public boolean hasBuiltinFunction(String function) {
        return python_builtin_func.contains(function);
    }

    public boolean hasIntCharacter(char character) {
        return integer_chars.contains(character);
    }

    private void initOperators1() {
        python_ops1.add('+');
        python_ops1.add('-');
        python_ops1.add('*');
        python_ops1.add('/');
        python_ops1.add('%');
        python_ops1.add('=');
        python_ops1.add('!');
        python_ops1.add('>');
        python_ops1.add('<');
        python_ops1.add('^');
        python_ops1.add('&');
        python_ops1.add('|');
        python_ops1.add('~');
    }

    private void initOperators2() {
        python_ops2.add("**");
        python_ops2.add("//");
        python_ops2.add("+=");
        python_ops2.add("-=");
        python_ops2.add("*=");
        python_ops2.add("/=");
        python_ops2.add("%=");
        python_ops2.add("//=");
        python_ops2.add("**=");
        python_ops2.add("&=");
        python_ops2.add("|=");
        python_ops2.add("^=");
        python_ops2.add(">>=");
        python_ops2.add("<<=");
        python_ops2.add("==");
        python_ops2.add("!=");
        python_ops2.add(">=");
        python_ops2.add("<=");
        python_ops2.add(">>");
        python_ops2.add("<<");
    }

    private void initReserveWords() {
        python_reserves.add("and");
        python_reserves.add("assert");
        python_reserves.add("in");
        python_reserves.add("del");
        python_reserves.add("else");
        python_reserves.add("raise");
        python_reserves.add("from");
        python_reserves.add("if");
        python_reserves.add("continue");
        python_reserves.add("not");
        python_reserves.add("pass");
        python_reserves.add("finally");
        python_reserves.add("while");
        python_reserves.add("yield");
        python_reserves.add("is");
        python_reserves.add("as");
        python_reserves.add("break");
        python_reserves.add("return");
        python_reserves.add("elif");
        python_reserves.add("except");
        python_reserves.add("def");
        python_reserves.add("global");
        python_reserves.add("import");
        python_reserves.add("for");
        python_reserves.add("or");
        python_reserves.add("lambda");
        python_reserves.add("with");
        python_reserves.add("class");
        python_reserves.add("try");
        python_reserves.add("exec");
        python_reserves.add("False");
        python_reserves.add("True");
        python_reserves.add("None");
    }

    private void initBuiltinFunctions() {
        python_builtin_func.add("abs");
        python_builtin_func.add("all");
        python_builtin_func.add("any");
        python_builtin_func.add("ascii");
        python_builtin_func.add("bin");
        python_builtin_func.add("bool");
        python_builtin_func.add("breakpoint");
        python_builtin_func.add("bytearray");
        python_builtin_func.add("bytes");
        python_builtin_func.add("callable");
        python_builtin_func.add("chr");
        python_builtin_func.add("classmethod");
        python_builtin_func.add("compile");
        python_builtin_func.add("complex");
        python_builtin_func.add("delattr");
        python_builtin_func.add("dict");
        python_builtin_func.add("dir");
        python_builtin_func.add("divmod");
        python_builtin_func.add("enumerate");
        python_builtin_func.add("eval");
        python_builtin_func.add("exec");
        python_builtin_func.add("filter");
        python_builtin_func.add("float");
        python_builtin_func.add("format");
        python_builtin_func.add("frozenset");
        python_builtin_func.add("getattr");
        python_builtin_func.add("globals");
        python_builtin_func.add("hasattr");
        python_builtin_func.add("hash");
        python_builtin_func.add("help");
        python_builtin_func.add("hex");
        python_builtin_func.add("id");
        python_builtin_func.add("input");
        python_builtin_func.add("int");
        python_builtin_func.add("isinstance");
        python_builtin_func.add("issubclass");
        python_builtin_func.add("iter");
        python_builtin_func.add("len");
        python_builtin_func.add("list");
        python_builtin_func.add("locals");
        python_builtin_func.add("map");
        python_builtin_func.add("max");
        python_builtin_func.add("memoryview");
        python_builtin_func.add("min");
        python_builtin_func.add("next");
        python_builtin_func.add("object");
        python_builtin_func.add("oct");
        python_builtin_func.add("open");
        python_builtin_func.add("ord");
        python_builtin_func.add("pow");
        python_builtin_func.add("print");
        python_builtin_func.add("property");
        python_builtin_func.add("range");
        python_builtin_func.add("repr");
        python_builtin_func.add("reversed");
        python_builtin_func.add("round");
        python_builtin_func.add("set");
        python_builtin_func.add("setattr");
        python_builtin_func.add("slice");
        python_builtin_func.add("sorted");
        python_builtin_func.add("staticmethod");
        python_builtin_func.add("str");
        python_builtin_func.add("sum");
        python_builtin_func.add("super");
        python_builtin_func.add("tuple");
        python_builtin_func.add("type");
        python_builtin_func.add("vars");
        python_builtin_func.add("zip");
        python_builtin_func.add("__import__");
    }

    private void initIntCharacters() {
        for (char i = '0'; i <= '9'; i++) {
            integer_chars.add(i);
        }
        integer_chars.add('.');
    }

}
