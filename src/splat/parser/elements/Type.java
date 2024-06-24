package splat.parser.elements;

import splat.lexer.Token;

public class Type extends ASTElement{

    private String typeValue; // could be Boolean/Integer/String

    public Type(Token token, String value) {
        super(token);
        this.typeValue = value;
    }

    public String toString() {return typeValue;}
    public String getValue() {
        return typeValue;
    }
}
