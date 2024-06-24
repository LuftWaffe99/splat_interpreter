package splat.parser.elements;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.List;
import java.util.Map;

public class FunctionDecl extends Declaration {

	private List<Param> params;
	private Type retType;
	private List<VariableDecl> locVarDecls;
	private List<Statement> stmts;

	public FunctionDecl(Token tok, Label label,  List<Param> params, Type retType,
						List<VariableDecl> locVarDecls, List<Statement> stmts) {
		super(tok, label);
		this.params = params;
		this.retType = retType;
		this.locVarDecls = locVarDecls;
		this.stmts = stmts;
	}


	public boolean checkReturnStmts(Map<String, FunctionDecl> funcMap,
									Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

		Statement lastStmt = stmts.get(stmts.size()-1);
		if (lastStmt instanceof  ExprReturn_Statement) {
			return true;
		}

		if (lastStmt instanceof  IfThenElse_Statement){
			IfThenElse_Statement castedStmt = (IfThenElse_Statement) lastStmt;
			boolean recursionResult = castedStmt.returnExprTerminated(retType, funcMap, varAndParamMap);
			return recursionResult;
		}

		return false;
	}

	public List<Param> getParams() {
		return params;
	}

	public Type getRetType() {
		return retType;
	}

	public List<VariableDecl> getLocVarDecls() {
		return locVarDecls;
	}

	public List<Statement> getStmts() {
		return stmts;
	}

	@Override
	public String toString() {
		return "FunctionDecl{" +
				"label=" + getLabel() +
				", params=" + params +
				", retType=" + retType +
				", locVarDecls=" + locVarDecls +
				", stmts=" + stmts +
				'}';
	}
}
