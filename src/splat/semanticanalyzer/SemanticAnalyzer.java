package splat.semanticanalyzer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

import splat.parser.elements.*;

import javax.swing.plaf.nimbus.State;
import java.util.HashMap;


public class SemanticAnalyzer {

	private ProgramAST progAST;
	
	private Map<String, FunctionDecl> funcMap = new HashMap<>(); //Global function map
	private Map<String, Type> progVarMap = new HashMap<>();  //Global variable map
	
	public SemanticAnalyzer(ProgramAST progAST) {
		this.progAST = progAST;
	}


	public void analyze() throws SemanticAnalysisException {
		
		// Checks to make sure we don't use the same labels more than once
		// for our program functions and variables 
		checkNoDuplicateProgLabels();  // --> checks globally function and variable names for duplicate
		
		// This sets the maps that will be needed later when we need to
		// typecheck variable references and function calls in the 
		// program body
		setProgVarAndFuncMaps(); // --> Collect information
		
		// Perform semantic analysis on the functions
		for (FunctionDecl funcDecl : funcMap.values()) {	// --> iterate through every function and check for duplicate stmts
			analyzeFuncDecl(funcDecl);
		}
		
		// Perform semantic analysis on the program body
		for (Statement stmt : progAST.getStmts()) {
			stmt.analyze(funcMap, progVarMap);
		}
		
	}

	private void analyzeFuncDecl(FunctionDecl funcDecl) throws SemanticAnalysisException {
		
		// Checks to make sure we don't use the same labels more than once
		// among our function parameters, local variables, and function names
		checkNoDuplicateFuncLabels(funcDecl);
		
		// Get the types of the parameters and local variables
		Map<String, Type> varAndParamMap = getVarAndParamMap(funcDecl);
		
		// Perform semantic analysis on the function body
		for (Statement stmt : funcDecl.getStmts()) {
			stmt.analyze(funcMap, varAndParamMap);
		}

		Type retType = varAndParamMap.get("0result");
		if (retType == null){
			throw new SemanticAnalysisException("Return is expected for the function ", funcDecl.getLine(), funcDecl.getColumn());
		}

	}
	
	
	private Map<String, Type> getVarAndParamMap(FunctionDecl funcDecl) throws SemanticAnalysisException{
		// separation into funcMap and progVarMap , LOCALLY !
		// FIXME: Somewhat similar to setProgVarAndFuncMaps()
		Map<String, Type> VarAndParamMap = new HashMap<>();

		for (Declaration decl: funcDecl.getLocVarDecls()){ // Adding loc vars to Map
			VariableDecl var_decl = (VariableDecl)decl;
			Label label = var_decl.getLabel();
			Type type = var_decl.getType();
			VarAndParamMap.put(label.toString(), type);
		}

		for (Param param: funcDecl.getParams()){ // Adding params to Map
			Label label = param.getLabel();
			Type type = param.getType();
			VarAndParamMap.put(label.toString(), type);
		}
		// Return type checking
		Type returnType = funcDecl.getRetType();
		boolean noProblem = false;
		List<Statement> funcStmts = funcDecl.getStmts();
		Statement lastStmt = funcStmts.get(funcStmts.size()-1);

		// Non-void return type case
		if (!returnType.toString().equals("void")) {

			noProblem = funcDecl.checkReturnStmts(funcMap, VarAndParamMap);
//			System.out.println(noProblem);
		}
		else if (returnType.toString().equals("void")) {

			if ( lastStmt instanceof Return_Statement) {
				noProblem = true;
			} else if (!(lastStmt instanceof ExprReturn_Statement)) {
				noProblem = true;
			}

			if (lastStmt instanceof IfThenElse_Statement) {
				IfThenElse_Statement castedlastStmt = (IfThenElse_Statement) lastStmt;
				if (castedlastStmt.getElseStmts() != null){
					noProblem = false;
				}
			}

		}


		if (noProblem) {
			VarAndParamMap.put("0result", returnType);
		} else {
			VarAndParamMap.put("0result", null);
		}

		return VarAndParamMap;
	}

	private void checkNoDuplicateFuncLabels(FunctionDecl funcDecl) 
									throws SemanticAnalysisException {
		
		// FIXME: Similar to checkNoDuplicateProgLabels()
		Set<String> loc_labels = new HashSet<String>();
		Set<String> loc_params = new HashSet<String>();
		// Check for function duplicate local variables
		for (Declaration decl : funcDecl.getLocVarDecls()) {
			String loc_label = decl.getLabel().toString();

			if (loc_labels.contains(loc_label)) {
				throw new SemanticAnalysisException("Cannot have duplicate label "
						+ loc_label + "' in function body", decl);
			} else {
				loc_labels.add(loc_label);
			}

		}
		// Check for function duplicate params
		for (Param param : funcDecl.getParams()) {
			String loc_param = param.getLabel().toString();

			if (loc_params.contains(loc_param)) {
				throw new SemanticAnalysisException("Cannot have duplicate parameter "
						+ loc_param + "' in function body", param);
			} else {
				loc_params.add(loc_param);
			}
			// Redefinition of local parameter with existing parameter name
			if (loc_labels.contains(loc_param)) {
				throw new SemanticAnalysisException("Redefinition of parameter label with newly defined local variable "
						+ loc_param + "' in function body", param);
			}

		}



	}
	
	private void checkNoDuplicateProgLabels() throws SemanticAnalysisException {
		
		Set<String> labels = new HashSet<String>();
		// check only global function and variable labels
 		for (Declaration decl : progAST.getDecls()) {
 			String label = decl.getLabel().toString();
 			
			if (labels.contains(label)) {
				throw new SemanticAnalysisException("Cannot have duplicate label '"
						+ label + "' in program", decl);
			} else {
				labels.add(label);
			}
			
		}
	}

	private void setProgVarAndFuncMaps() {
		// separation into funcMap and progVarMap , GLOBALLY !
//		System.out.println(progAST.getDecls().size());
		for (Declaration decl : progAST.getDecls()) {

			String label = decl.getLabel().toString();
			
			if (decl instanceof FunctionDecl) {
				FunctionDecl funcDecl = (FunctionDecl)decl;
				funcMap.put(label, funcDecl);

			} else if (decl instanceof VariableDecl) {
				VariableDecl varDecl = (VariableDecl)decl;
				progVarMap.put(label, varDecl.getType());
			}
		}
	}
}
