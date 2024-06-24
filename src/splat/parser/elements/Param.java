package splat.parser.elements;
import splat.lexer.Token;

public class Param extends ASTElement {

    private Type type;
    private Label label;


    public Param(Token token, Label label, Type type) {
        super(token);
        this.label = label;
        this.type = type;
    }

    public Label getLabel() {return label;}
    public String toString() {
        return label + " : " + type;
    }
    public Type getType() {
        return type;
    }



}
