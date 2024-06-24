package splat.parser.elements;

import splat.executor.*;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.Map;


public class Print_Statement extends Statement {

    private Expression expr;

    public Print_Statement(Token token, Expression expr) {
        super(token);
        this.expr = expr;
    }

    public String toString() {
        return "print " + expr + " ;\n";
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap)
            throws SemanticAnalysisException {

        this.expr.analyzeAndGetType(funcMap, varAndParamMap);

    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException  {
        Value valExpr = expr.evaluate(funcMap, varAndParamMap);

        if (valExpr instanceof intValue castedVal) {
            System.out.print(castedVal.convValue());
        }
        else if (valExpr instanceof strValue castedVal) {
            System.out.print(castedVal.getValue());
        }
        else {
            boolValue castedVal = (boolValue) valExpr;
            System.out.print(castedVal.convValue());
        }
    }
}
