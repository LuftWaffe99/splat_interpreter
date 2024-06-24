package splat.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import splat.lexer.Token;
import splat.parser.elements.*;

public class Parser {

	private List<Token> tokens;
	
	public Parser(List<Token> tokens) {
		this.tokens = tokens;
	}

	/**
	 * Compares the next token to an expected value, and throws
	 * an exception if they don't match.  This removes the front-most
	 * (next) token  
	 * 
	 * @param expected value of the next token
	 * @throws ParseException if the actual token doesn't match what 
	 * 			was expected
	 */
	private void checkNext(String expected) throws ParseException {

		Token tok = tokens.remove(0);
		
		if (!tok.getValue().equals(expected)) {
			throw new ParseException("Expected '"+ expected + "', got '" 
					+ tok.getValue()+ "'.", tok);
		}
	}
	
	/**
	 * Returns a boolean indicating whether or not the next token matches
	 * the expected String value.  This does not remove the token from the
	 * token list.
	 * 
	 * @param expected value of the next token
	 * @return true iff the token value matches the expected string
	 */
	private boolean peekNext(String expected) {
		return tokens.get(0).getValue().equals(expected);
	}
	
	/**
	 * Returns a boolean indicating whether or not the token directly after
	 * the front most token matches the expected String value.  This does 
	 * not remove any tokens from the token list.
	 * 
	 * @param expected value of the token directly after the next token
	 * @return true iff the value matches the expected string
	 */
	private boolean peekTwoAhead(String expected) {
		return tokens.get(1).getValue().equals(expected);
	}



	/*
	 *  Additional functions for validation ;
	 */

	private boolean isValidBinOp(String checkingOp) {
		boolean isValid = false;
		List<String> validOps = List.of("and", "or", ">", "<", "==", ">=", "<=", "+", "-", "*", "/", "%");

		if (validOps.contains(checkingOp)){
			return isValid = true;
		}

		return isValid;
	}

	private boolean isValidUnaryOp(String checkingOp) {
		boolean isValid = false;
		List<String> validOps = List.of("not", "-");

		if (validOps.contains(checkingOp)){
			return isValid = true;
		}

		return isValid;
	}

	private boolean isValidType(String checkingToken) {
		boolean isValid = false;
		List<String> validTypes = List.of("Integer", "Boolean", "String", "void");

		if (validTypes.contains(checkingToken)){
			return isValid = true;
		}

		return isValid;
	}

	private boolean isNextLabel() {

		Set<String> reservedKeywords = new HashSet<>(List.of("if", "while", "else", "return",
				"program", "begin", "end", "print", "print_line", "and", "not", "true", "false",
				"or", "void", "Boolean", "Integer", "String", "do", "then", "is"));

		String nextToken = tokens.get(0).getValue();

		if (reservedKeywords.contains(nextToken.toLowerCase())) {
			return false;
		}

		if (Character.isDigit(nextToken.charAt(0))) {
			return false;
		}

		if (!nextToken.matches("[a-zA-Z0-9_]+")) {
			return false;
		}

		return true;
	}







	/*
	 *  <program> ::= program <decls> begin <stmts> end ;
	 */
	public ProgramAST parse() throws ParseException {
		
		try {
			// Needed for 'program' token position info
			Token startTok = tokens.get(0);

			checkNext("program");

			List<Declaration> decls = parseDecls();
//
			checkNext("begin");
			
			List<Statement> stmts = parseStmts();
			
			checkNext("end");
			checkNext(";");
	
			return new ProgramAST(decls, stmts, startTok);
			
		// This might happen if we do a tokens.get(), and nothing is there!
		} catch (IndexOutOfBoundsException ex) {
			
			throw new ParseException("Unexpectedly reached the end of file.", -1, -1);
		}
	}
	
	/*
	 *  <decls> ::= (  <decl>  )*
	 */
	private List<Declaration> parseDecls() throws ParseException {
		
		List<Declaration> decls = new ArrayList<Declaration>();

		while (!peekNext("begin")) {
			Declaration decl = parseDecl();
			decls.add(decl);
		}
		
		return decls;
	}
	
	/*
	 * <decl> ::= <var-decl> | <func-decl>
	 */
	private Declaration parseDecl() throws ParseException {

		if (peekTwoAhead(":")) {
			return parseVarDecl();
		} else if (peekTwoAhead("(")) {
			return parseFuncDecl();
		} else {
			Token tok = tokens.get(0);
			throw new ParseException("Declaration expected", tok);
		}
	}

	/*
	 * <var-decl> ::= <label> : <type> ;
	 */
	private VariableDecl parseVarDecl() throws ParseException {

		Token startToken = tokens.get(0);

		Label label = parse_Label();

		checkNext(":");

		Type type = parse_Type(); // remove type token

		checkNext(";");

		return new VariableDecl(startToken, label, type);

	}

	/*
	 * <type> ::= Integer | Boolean | String
	 *
	 */

	private Type parse_Type() throws ParseException {

		Token startToken = tokens.remove(0);
		String tokenType = startToken.getValue();

		if (!isValidType(tokenType))
		{
			throw new ParseException("Invalid type of variable is given", startToken);
		}

		return new Type(startToken, startToken.getValue());
	}

	
	/*
	 * <func-decl> ::= <label> ( <params> ) : <ret-type> is 
	 * 						<loc-var-decls> begin <stmts> end ;
	 */
	private FunctionDecl parseFuncDecl() throws ParseException {

		Token startToken = tokens.get(0);  // Take a reference point for error handling

		Label label = parse_Label();

		checkNext("(");

		List<Param> params = parseParams();

		checkNext(")");

		checkNext(":");

		Type retType = parseRetType();

		checkNext("is");

		List<VariableDecl> locVarDecls = parseLocVarDecls();

		checkNext("begin");

		List<Statement> stmts = parseStmts();

		checkNext("end");

		checkNext(";");

		return new FunctionDecl(startToken, label, params, retType, locVarDecls, stmts);

	}


	/*
	 * <loc-var-decls> ::= ( <var-decl> )*
	 *
	 */
	private List<VariableDecl> parseLocVarDecls() throws ParseException {

		List<VariableDecl> locVarDecls = new ArrayList<VariableDecl>();

		while (!peekNext("begin")) {
			VariableDecl local_var = parseVarDecl();
			locVarDecls.add(local_var);
		}
		return locVarDecls;

	}


	/*
	 * <ret-type> ::= <type> | void
	 *
	 */

	private Type parseRetType() throws ParseException {
		Token startToken = tokens.remove(0);
		String tokenType = startToken.getValue();

		if (!isValidType(tokenType)) {
			throw new ParseException("Invalid return type", startToken);
		}

		return new Type(startToken, tokenType);
	}

	/*
	 * <params> ::= <param> (, <param>)* | ɛ
	 */
	private List<Param> parseParams() throws ParseException {
		List<Param> params = new ArrayList<>();

		// Check for the epsilon case (empty parameters)
		if (!peekNext(")")) {
			// Parse the first parameter
			Param firstParam = parseParam();
			params.add(firstParam);

			// Check for additional parameters separated by commas
			while (peekNext(",")) {
				checkNext(",");  // Consume the comma
				Param additionalParam = parseParam();
				params.add(additionalParam);
			}
		}
		// in case of epsilon return empty param list
		return params;
	}

	/*
	 * <param> ::= <label> : <type>
	 */
	private Param parseParam() throws ParseException {
		Token startToken = tokens.get(0);

		Label label = parse_Label();

		checkNext(":");

		Type type = parse_Type();

		return new Param(startToken, label, type);
	}




	/*
	 * Statements handling methods
	 */
	private List<Statement> parseStmts() throws ParseException {
		List<Statement> statements = new ArrayList<>();

		while (!peekNext("end") && !peekNext("else")) {
			Statement stmt = parseStmt();
			statements.add(stmt);
		}

		return statements;
	}


	private Statement parseStmt() throws ParseException {


		if (peekNext("print")) {
			return parse_Print();}
		else if (peekNext("while")) {
			return parse_While();
		} else if (peekNext("if")) {
			return parse_IfThenElse();
		} else if (peekTwoAhead(":=")) {
			return parse_Assignment();
		} else if (peekNext("return")) {
			if (peekTwoAhead(";")){
				return parse_Return();
			} else {
				return parse_ExprReturn();
			}
		}else if (peekTwoAhead("(")) {
			return parse_FunctionCall();
		} else if (peekNext("print_line")) {
			return parse_PrintLine();
		} else {
			Token tok = tokens.get(0);
			throw new ParseException("Invalid statement", tok);
		}
	}

	private Assignment_Statement parse_Assignment() throws ParseException {

		Token startToken = tokens.get(0);

		Label label = parse_Label();

		checkNext(":=");

		Expression expr = parse_Expression();

		checkNext(";");

		return new Assignment_Statement(startToken, label, expr);

	}


	private While_Statement parse_While() throws ParseException{

		Token startToken = tokens.get(0);

		checkNext("while");

		Expression expr = parse_Expression();

		checkNext("do");

		List<Statement> stmts =  parseStmts();

		checkNext("end");

		checkNext("while");

		checkNext(";");

		return new While_Statement(startToken, expr, stmts);
	}


	private IfThenElse_Statement parse_IfThenElse() throws ParseException {

		Token startToken = tokens.get(0);

		checkNext("if");

		Expression expr = parse_Expression();

		checkNext("then");

		List<Statement> then_stmts =  parseStmts();

		if (peekNext("end")) {
			checkNext("end");
			checkNext("if");
			checkNext(";");

			return new IfThenElse_Statement(startToken, expr, then_stmts, null);

		} else if (peekNext("else")){
			checkNext("else");

			List<Statement> else_stmts =  parseStmts();

			checkNext("end");
			checkNext("if");
			checkNext(";");

			return new IfThenElse_Statement(startToken, expr, then_stmts, else_stmts);
		} else {
			throw new ParseException("Wrong IfThenElse statement ", startToken);
		}

	}

	private FunctionCall_Statement parse_FunctionCall() throws ParseException {

		Token startToken = tokens.get(0);

		Label label = parse_Label();

		checkNext("(");

		Args args = parseArgs();

		checkNext(")");

		checkNext(";");

		return new FunctionCall_Statement(startToken, label, args);

	}


	private Print_Statement parse_Print() throws ParseException {

		Token startToken = tokens.get(0);

		checkNext("print");

		Expression expr = parse_Expression();

		Print_Statement newPrintStmt = new Print_Statement(startToken, expr);

		checkNext(";");


		return newPrintStmt;
	}

	private PrintLine_Statement parse_PrintLine() throws ParseException {

		Token startToken = tokens.get(0);

		checkNext("print_line");
		checkNext(";");

		return new PrintLine_Statement(startToken);
	}



	/*
	 * return ;
	 */

	private Return_Statement parse_Return() throws ParseException {

		Token startToken = tokens.get(0);

		checkNext("return");

		checkNext(";");

		return new Return_Statement(startToken);
	}

	/*
	 * return <expr> ;
	 */
	private ExprReturn_Statement parse_ExprReturn() throws ParseException {

		Token startToken = tokens.get(0);

		checkNext("return");

		Expression expr = parse_Expression();

		checkNext(";");

		return new ExprReturn_Statement(startToken, expr);
	}







	/*
	 * <expr> ::= ( <expr> <bin-op> <expr> )
	 *	          | ( <unary-op> <expr> )
	 *            | <label> ( <args> )
	 *	          | <label>
	 *	          | <literal>
	 */
	private Expression parse_Expression() throws ParseException {

		Token startToken = tokens.get(0);

		if (peekNext("(")){
			if (peekTwoAhead("-") || peekTwoAhead("not")) {
				return parse_UnaryOpExpr();
			} else {
				return parse_BinaryOpExpr();
			}
		}
		else if (peekTwoAhead("(") ) {
			return parse_FunctionCallExpr();
		}
		else if (isNextLabel()) {
			Label label = parse_Label();
			return new LabelExpression(startToken, label);
		}
		else {
			Literal literal = parse_Literal();
			return new LiteralExpression(startToken, literal);
		}

	}

	/*
	 * <label> ( <args> )
	 */
	private FunctionCall_Expression parse_FunctionCallExpr() throws ParseException {

		Token startToken = tokens.get(0);

		Label label = parse_Label();

		checkNext("(");

		Args args = parseArgs();

		checkNext(")");

		return new FunctionCall_Expression(startToken, label, args);
	}

	/*
	 * <label> ::= …sequence of alphanumeric characters and underscore, not starting with a digit,
	 *		which are not keywords...
	 */
	private Label parse_Label() throws ParseException{

		Boolean isValid = isNextLabel();
		Token startToken = tokens.remove(0);

		if(!isValid) {
			throw new ParseException("Invalid label is given", startToken);
		}

		return new Label(startToken);

	}

	/*
	 * <args> ::= <expr> ( , <expr> )*
	 */

	private Args parseArgs() throws ParseException {

		Token startToken = tokens.get(0);

		List<Expression> expressions = new ArrayList<>();

		// Check if there is at least one expression
		if (!peekNext(")")) {
			// Parse the first expression
			Expression firstExpr = parse_Expression();
			expressions.add(firstExpr);

			// Check for additional expressions separated by commas
			while (peekNext(",")) {
				checkNext(",");  // Consume the comma
				Expression additionalExpr = parse_Expression();
				expressions.add(additionalExpr);
			}
		}

		return new Args(startToken, expressions);
	}





	/*
	 * <expr> ::= ( <expr> <bin-op> <expr> )
	 */
	private BinaryOpExpression parse_BinaryOpExpr() throws ParseException {
		Token startToken = tokens.get(0);

		checkNext("(");

		Expression before_expr = parse_Expression();

		BinaryOperator binaryOp = parse_BinaryOp();

		Expression after_expr = parse_Expression();

		checkNext(")");

		return new BinaryOpExpression(startToken, before_expr, binaryOp, after_expr);
	}


	/*
	 * <bin-op> ::= and | or | > | < | == | >= | <= | + | - | * | / | %
	 */
	private BinaryOperator parse_BinaryOp() throws ParseException {

		Token startToken = tokens.remove(0);

		if (!isValidBinOp(startToken.getValue())) {
			throw new ParseException("Incorrect binary operator ", startToken);
		}

		return new BinaryOperator(startToken);

	}

	/*
	 * <expr> ::= ( <unary-op> <expr> )
	 */

	private UnaryOpExpression parse_UnaryOpExpr() throws ParseException {
		Token startToken = tokens.get(0);

		checkNext("(");

		UnaryOperator unaryOp = parse_UnaryOp();

		Expression expr = parse_Expression();

		checkNext(")");

		return new UnaryOpExpression(startToken, unaryOp, expr);
	}

	/*
	 * <unary-op> ::= not | -
	 */
	private UnaryOperator parse_UnaryOp() throws ParseException {

		Token startToken = tokens.remove(0);

		if (!isValidUnaryOp(startToken.getValue())) {
			throw new ParseException("Incorrect unary operator ", startToken);
		}

		return new UnaryOperator(startToken);

	}


	/*
	 * <literal> ::= <int-literal> | <bool-literal> | <string-literal>
	 */

	private Literal parse_Literal() throws ParseException {

		String token_value = tokens.get(0).getValue();

		if (peekNext("false") || peekNext("true")){
			return parse_BooleanLiteral();
		} else if (token_value.charAt(0) == '"'){
			return parse_StringLiteral();
		} else {
			return parse_IntegerLiteral();
		}

	}


	/*
	 * <int-literal> ::= …sequence of decimal digits...
	 */

	private IntegerLiteral parse_IntegerLiteral() throws ParseException {
		Token startToken = tokens.remove(0);

		if (!startToken.getValue().matches("-?\\d+")) {
			throw new ParseException("Invalid integer literal", startToken);
		}

		return new IntegerLiteral(startToken);
	}


	/*
	 * <bool-literal> ::= true | false
	 */
	private BooleanLiteral parse_BooleanLiteral() throws ParseException {
		Token startToken = tokens.remove(0);

		if (!startToken.getValue().equals("false") && !startToken.getValue().equals("true")) {
			throw new ParseException("Invalid boolean literal", startToken);
		}

		return new BooleanLiteral(startToken);
	}

	/*
	 * <string-literal> ::= "…sequence of characters and space that do not contain double-quotes, backslashes,
	 *		or newlines... "
	 */

	private StringLiteral parse_StringLiteral() throws ParseException {
		Token startToken = tokens.remove(0); // Remove the opening " sign
		String result = startToken.getValue().replace("\"", "");

		return new StringLiteral(startToken, result);

	}





}



















