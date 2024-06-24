package splat.parser.elements;

import splat.executor.*;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class FunctionCall_Statement extends Statement {

    private Label label;
    private Args args;

    public FunctionCall_Statement(Token token, Label label, Args args) {
        super(token);
        this.label = label;
        this.args = args;
    }

    public String toString() {
        return label + " ( " + args + " ) ;\n";
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

        String labelStr = this.label.toString();
        // Check for function name
        if (!funcMap.containsKey(labelStr)) {
            throw new SemanticAnalysisException("There is no such function defined before", this.label.getLine(), this.label.getColumn());
        }

        List<Param> paramList = funcMap.get(labelStr).getParams();
        List<Expression> exprList = this.args.getExprs();

        if (paramList.size() != exprList.size()){
            throw new SemanticAnalysisException("Parameter and argument sizes are not matched", this.args.getLine(), this.args.getColumn());
        }

        for (int ind = 0; ind < paramList.size() ; ind++) {
            Type exprType = exprList.get(ind).analyzeAndGetType(funcMap, varAndParamMap);
            Type paramType = paramList.get(ind).getType();

            if (!exprType.toString().equals(paramType.toString())) {
                throw new SemanticAnalysisException("Parameter and argument types are mismatched", paramType.getLine(), paramType.getColumn());
            }
        }

        Type returnType = funcMap.get(labelStr).getRetType();

        if (!returnType.toString().equals("void")) {
            throw new SemanticAnalysisException("Return type should be void", returnType.getLine(), returnType.getColumn());
        }

    }

    @Override
    public  void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ReturnFromCall , ExecutionException {

        Map<String, Value> VarAndParamMap = new HashMap<>();
        FunctionDecl funcDecl = funcMap.get(label.toString());
        // Map <var_name, value>
        for (Declaration decl: funcDecl.getLocVarDecls()){

            VariableDecl var_decl = (VariableDecl)decl;
            Label label = var_decl.getLabel();
            Type varType = var_decl.getType();
            Value val;
            // initialize Value with default values (0, false, "")
            if (varType.toString().equals("Integer")) {val = new intValue(varType);}
            else if (varType.toString().equals("String")) {val = new strValue(varType);}
            else {val = new boolValue(varType);}

            VarAndParamMap.put(label.toString(), val);
        }

        List<Value> arg_list= new ArrayList<>(); //Collecting values for args

        for (Expression expr: this.args.getExprs()){
            Value arg_value = expr.evaluate(funcMap, varAndParamMap);
            arg_list.add(arg_value);
        }

        for (int i=0; i<funcDecl.getParams().size(); i++){ // Adding params to Map
            Param param = funcDecl.getParams().get(i);
            Value value = arg_list.get(i);
            Label label = param.getLabel();
            Type type = param.getType();
            Value val;

            if (value instanceof intValue valueCasted) {
                val = new intValue(type, valueCasted.convValue());
            }
            else if (value instanceof strValue valueCasted) {
                val = new strValue(type, valueCasted.getValue());
            }
            else {
                boolValue valueCasted = (boolValue)value;
                val = new boolValue(type, valueCasted.convValue());
            }

            VarAndParamMap.put(label.toString(), val);
        }

        List<Statement> funcStmts = funcMap.get(label.toString()).getStmts();

        try {
            for (Statement stmt : funcStmts) {
                stmt.execute(funcMap, VarAndParamMap);
            }

        } catch (ReturnFromCall ex) {
            //System.out.println(label.toString() + " function returned value (stmt): " +  ex.getReturnVal().getValue());
        }

    }
}
