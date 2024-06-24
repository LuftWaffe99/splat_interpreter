package splat.lexer;

public class Token {

    public String value;
    public int line;
    public int column;

    public Token(String value, int line, int column) {
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public String getValue(){
        return this.value;
    }

    public int getLine(){
        return this.line;
    }

    public int getColumn(){
        return this.column;
    }


    public String toString() {
        return "Value: " + this.getValue() +
                " Line: " + this.getLine() +
                " Column: " + this.getColumn() +
                "\n";
    }
}
