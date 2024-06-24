package splat.lexer;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class Lexer {

	private File progFile;

	public Lexer(File progFile) {
		this.progFile = progFile;
	}

	private boolean isSign(char ch) {
		return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '(' || ch == ')' || ch == ';'
				|| ch == '!' || ch == '"' || ch == ',';
	}

	private void checkClosedString(String line, int line_num) throws LexException{
		boolean isClosed = true;
		int col_num = 1;
		int error_col_num = -1;

		for (int i = 0; i < line.length(); i++) {
			char ch = line.charAt(i);

			if (ch == '"' && isClosed) {
				isClosed = false;
				error_col_num = col_num;
			} else if (ch == '"' && !isClosed) {
				isClosed = true;
			}
			col_num++;
		}

		if (!isClosed){
			throw new LexException("Unfinished string!", line_num, error_col_num);
		}

	}

	private static void checkExclamationMark(String line, int line_num) throws LexException {
		boolean insideQuotes = false;

		for (int i = 0; i < line.length(); i++) {
			char ch = line.charAt(i);

			if (ch == '"') {
				insideQuotes = !insideQuotes; // Toggle insideQuotes flag when encountering a quote
			} else if (ch == '!' && !insideQuotes) {
				throw new LexException("Exclamation mark usage outside quotes", line_num, i + 1);
			}
		}
	}

	private void checkQuestionMark(char ch, int line_num, int col_num) throws LexException{
		if (ch == '?'){
			throw new LexException("Question mark!", line_num, col_num);
		}
	}

	private void checkNextChar(char nextChar, char next2nextChar,int line_num, int col_num) throws LexException{
		if (nextChar == '=' && next2nextChar != '='){
			throw new LexException("Unacceptable expression", line_num, col_num);
		}
	}

	private void checkBackSlash(char ch, int line_num, int col_num) throws LexException{
		if (ch == '\\'){
			throw new LexException("Back slash usage", line_num, col_num);
		}
	}

	private void checkPlusMinus(char ch, int line_num, int col_num) throws LexException{
		if (ch == '±'){
			throw new LexException("Plus or minus usage", line_num, col_num);
		}
	}

	private void checkSingleQuote(char ch, int line_num, int col_num) throws LexException{
		if (ch == '\''){
			throw new LexException("Single quote usage", line_num, col_num);
		}
	}

	private void checkExclamationMark(char ch, int line_num, int col_num) throws LexException{
		if (ch == '!'){
			throw new LexException("Exclamation mark usage", line_num, col_num);
		}
	}




public List<Token> tokenize() throws LexException, IOException {

		List<Token> token_lst = new ArrayList<>();

		if (progFile.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(this.progFile));

			String line;
			int lineNum = 1;
			boolean isClosed = true;
			// Read line-by-line
			while ((line = reader.readLine()) != null) {
				int column = 1;
				String token = "";

				// Check for closed brackets
				checkClosedString(line, lineNum);
				checkExclamationMark(line, lineNum);

				// Read char-by-char in a line and add token to the list
				for (int i = 0; i < line.length(); i++) {
					char ch = line.charAt(i);

					checkQuestionMark(ch, lineNum, column);
					checkBackSlash(ch, lineNum, column);
					checkPlusMinus(ch, lineNum, column);
					checkSingleQuote(ch, lineNum, column);

					if (ch == '"' && !isClosed) {
						isClosed = true;
						token = token + '"';
						continue;
					} else if (ch == '"') {
						isClosed = false;
					}


					if (isClosed) {


						if (!Character.isWhitespace(ch) ) {

							if (isSign(ch) || ch == ':') {
								if (!token.isEmpty()) {
									token_lst.add(new Token(token, lineNum, column - token.length()));
									token = "";
								}

								if (i < line.length() - 1 && line.charAt(i + 1) == '=' && ch == ':') {
									token_lst.add(new Token(":=", lineNum, column));
									i++; // Skip the '=' character
								} else {
									token_lst.add(new Token(String.valueOf(ch), lineNum, column));
								}
							}

							else if (ch == '>' || ch == '<' || ch == '=') {
								if (i < line.length() - 1 && line.charAt(i + 1) == '=') {
									if (!token.isEmpty()) {
										token_lst.add(new Token(token, lineNum, column - token.length()));
										token = "";
									}
									token_lst.add(new Token(String.valueOf(ch) + "=", lineNum, column));
									checkNextChar(line.charAt(i + 2), line.charAt(i + 3) ,lineNum, column);
									i++; // Skip the '=' character
								} else {
									if (!token.isEmpty()) {
										token_lst.add(new Token(token, lineNum, column - token.length()));
										token = "";
									}
									token_lst.add(new Token(String.valueOf(ch), lineNum, column));
								}
							} else {
								token = token + ch;
							}


						} else { // in case if we met whitespace

							if (!token.isEmpty()) {
								token_lst.add(new Token(token, lineNum, column - token.length()));
								token = "";
							}
						}
					} else {
						token = token + ch;
					}

					column++;
				}

				// Check if there's a token left after the loop
				if (!token.isEmpty()) {
					token_lst.add(new Token(token, lineNum, column - token.length()));
					token = "";
				}

				lineNum++;
			}

			}
//	System.out.println(token_lst);
	return token_lst;
	}


}