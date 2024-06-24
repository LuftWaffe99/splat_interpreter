package splat.parser.elements;
import splat.lexer.Token;
public class Label extends ASTElement{

    private String label ;

    public Label(Token token) {
        super(token);
        this.label = token.getValue();
    }

    public String toString() {
        return label;
    }


}
