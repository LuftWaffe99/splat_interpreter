package splat.parser.elements;
import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.Map;
import java.util.List;

public class Assignment_Statement extends Statement{

    private Label label;
    private Expression expr;

    public Assignment_Statement(Token token, Label label, Expression expr) {
        super(token);
        this.label = label;
        this.expr = expr;
    }

    public String getLabel() {
        return this.label.toString();
    }
    /*
    <stmt>  ::= <label>   :=    <expr>   ;
     */
    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
        Type exprType = this.expr.analyzeAndGetType(funcMap, varAndParamMap);
        String labelStr = this.label.toString();

        Type labelType = varAndParamMap.get(labelStr);
        // New label declaration without defining its type before
        if (labelType == null) {
            throw new SemanticAnalysisException("Label type is not defined before", label.getLine(), label.getColumn());
        }

        if (!labelType.toString().equals(exprType.toString())){
            throw new SemanticAnalysisException("Type mismatch between label and expression", label.getLine(), label.getColumn());
        }

        if (!varAndParamMap.containsKey(labelStr)) {
            throw new SemanticAnalysisException("Label name is not defined", label.getLine(), label.getColumn());
        }

        if (funcMap.containsKey(labelStr)) {
            throw new SemanticAnalysisException("Label is assigned with another function name", label.getLine(), label.getColumn());
        }

    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {
        Value exprValue = expr.evaluate(funcMap, varAndParamMap);
        varAndParamMap.put(label.toString(), exprValue);

    }
}
