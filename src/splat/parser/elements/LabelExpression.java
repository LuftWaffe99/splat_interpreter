package splat.parser.elements;
import splat.executor.Value;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import java.util.Map;
import splat.executor.*;

public class LabelExpression extends Expression{

    private Label label;

    public LabelExpression(Token token, Label label) {
        super(token);
        this.label = label;
    }

    public String toString() {
        return label.toString();
    }

    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

        if (!varAndParamMap.containsKey(label.toString())) {
            throw new SemanticAnalysisException("The variable is not declared", label.getLine(), label.getColumn());
        }

        return varAndParamMap.get(label.toString()); // Note VarAndParamMap.put(label_name, type) !, so we are returning label's initial type
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) {

        return varAndParamMap.get(this.label.toString()); // map stores child class of Value and not abstract class itself
    }
}
