package splat.parser.elements;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import javax.swing.plaf.nimbus.State;
import java.util.Map;
import splat.executor.boolValue;

public class Return_Statement extends Statement {

    private Token token;
    public Return_Statement(Token token) {

        super(token);
        this.token = token;
    }

    public String toString() {
        return "return ;\n";
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

        Type returnType = varAndParamMap.get("0result");

        if(returnType == null) {
            throw new SemanticAnalysisException("Problem with return type definition", super.getLine(), super.getColumn());
        }

        if(!returnType.getValue().equals("void")) {
            throw new SemanticAnalysisException("The function return type should be void", super.getLine(), super.getColumn());
        }


    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ReturnFromCall {
        throw new ReturnFromCall(new boolValue(new Type(token,"Boolean")));
    }
}
