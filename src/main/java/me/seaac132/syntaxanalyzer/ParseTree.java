package me.seaac132.syntaxanalyzer;

import me.seaac132.lexicalanalyzer.Token;
import me.seaac132.lexicalanalyzer.TokenType;

import java.util.*;

public class ParseTree {

    abstract class AbstractParseTreeNodeData {

        private Token value;

    }

    protected class ParseTreeNodeValue extends AbstractParseTreeNodeData {

        private ParseTreeNodeValue(Token value) {
            super.value = value;
        }

        protected Token getValue() {
            return super.value;
        }

        public String toString() {
            Token token = super.value;
            return token.getData();
        }

    }

    protected enum ParseTreeNodeTag {

        PROGRAM,
        STATEMENT,
        TERM

    }

    protected class ParseTreeNode extends AbstractParseTreeNodeData {

        private List<AbstractParseTreeNodeData> children;
        private ParseTreeNodeTag tag;

        ParseTreeNode(ParseTreeNodeTag tag) {
            super();
            this.children = new LinkedList<>();
            this.tag = tag;
        }

        ParseTreeNodeTag getTag() {
            return tag;
        }

        boolean hasChild() {
            return !children.isEmpty();
        }

        int getChildrenCount() {
            return children.size();
        }

        AbstractParseTreeNodeData getChild(int index) {
            return children.get(index);
        }

        List<AbstractParseTreeNodeData> getChildren() {
            return children;
        }

        ParseTreeNodeValue addChild(Token value) {
            ParseTreeNodeValue child = new ParseTreeNodeValue(value);
            children.add(child);
            return child;
        }

        ParseTreeNode addChild(ParseTreeNode node) {
            children.add(node);
            return node;
        }

        void removeChild(int index) {
            children.remove(index);
        }

        public String toString() {
            return hasChild() ? children.toString() : tag.toString();
        }

    }

    private ParseTreeNode root;

    public ParseTree() {
        root = new ParseTreeNode(ParseTreeNodeTag.PROGRAM);
    }

    public ParseTreeNode getRoot() {
        return root;
    }

    public ParseTreeNodeValue addChild(Token child) {
        return root.addChild(child);
    }

    public AbstractParseTreeNodeData getChild(int index) {
        if (index < root.children.size() && index >= 0) {
            return root.getChild(index);
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setChild(int index, AbstractParseTreeNodeData child) {
        if (index < root.children.size() && index >= 0) {
            root.children.set(index, child);
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void removeChild(int index) {
        if (index < root.children.size() && index >= 0) {
            root.removeChild(index);
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public int getChildrenCount() {
        return root.children.size();
    }

    public List<AbstractParseTreeNodeData> getChildren() {
        return root.children;
    }

    private void bufferList(List<String> list, ParseTreeNode root) {
        if (root.hasChild()) {
            for (AbstractParseTreeNodeData child : root.children) {
                if (child instanceof ParseTreeNode) {
                    bufferList(list, (ParseTreeNode) child);
                } else if (child instanceof ParseTreeNodeValue) {
                    ParseTreeNodeValue value = (ParseTreeNodeValue) child;
                    list.add(value.getValue().getData());
                }
            }
        } else {
            list.add(root.tag.toString());
        }
    }

    public List<String> toList() {
        List<String> list = new ArrayList<>();
        bufferList(list, root);
        return list;
    }

    public String toString() {
        return root.toString();
    }

}
