package splat.parser.elements;
import splat.lexer.Token;
import java.util.List;

public class Args extends ASTElement{

    private List<Expression> exprs;

    public Args(Token token, List<Expression> exprs) {
        super(token);
        this.exprs = exprs;
    }

    public List<Expression> getExprs(){
        return exprs;
    }

    public String toString() {
        String expr2display = "";
        for(Expression expr: exprs) { expr2display += expr + " , ";}
        return expr2display;
    }


}
