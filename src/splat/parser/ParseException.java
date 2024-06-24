package splat.parser;

import splat.SplatException;
import splat.lexer.Token;

public class ParseException extends SplatException {
	
	public ParseException(String msg, Token tok) {
		super(msg, tok.getLine(), tok.getColumn());
	}
	
	public ParseException(String msg, int line, int column) {
		super(msg, line, column);
	}
}
