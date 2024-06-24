package splat;

public abstract class SplatException extends Exception {

	private int line;
	private int column;
	
	public SplatException(String msg, int line, int column) {
		super(msg);
		this.line = line;
		this.column = column;
	}
	
	public int getLine() {
		return line;
	}
	
	public int getColumn() {
		return column;
	}
	
	public String toString() {
		return super.getMessage() + " Error at line " 
						+ line + ", column " + column;
	}
	
}
