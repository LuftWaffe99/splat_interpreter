package splat.lexer;

import splat.SplatException;

public class LexException extends SplatException {

	public LexException(String msg, int line, int column) {
		super(msg, line, column);
	}
	
}
