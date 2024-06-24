package splat.parser.elements;
import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;

import javax.swing.plaf.nimbus.State;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.Map;
import java.util.List;

public class ExprReturn_Statement extends Statement {

    private Expression expr;

    public ExprReturn_Statement(Token token, Expression expr) {
        super(token);
        this.expr = expr;
    }

    public String toString() {
        return "return " + expr + " ;\n";
    }
    public Expression getExpr() { return this.expr;}

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

        Type exprType = this.expr.analyzeAndGetType(funcMap, varAndParamMap);
        Type returnType = varAndParamMap.get("0result");

        if (returnType == null){
            throw new SemanticAnalysisException("Problem with return type definition", expr.getLine(), expr.getColumn());
        }
        // check for return and expression type match
        if (!returnType.toString().equals(exprType.toString())) {
            throw new SemanticAnalysisException("Return and expression types mismatch", this.expr.getLine(), this.expr.getColumn());
        }

    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {

        Value val = expr.evaluate(funcMap, varAndParamMap);
        throw new ReturnFromCall(val);
    }
}
