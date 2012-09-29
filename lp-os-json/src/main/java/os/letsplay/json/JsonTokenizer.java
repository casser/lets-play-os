
package os.letsplay.json;

public class JsonTokenizer {
	
	
	
	private String jsonString;
	
	
	private int loc;
	private int line;
	
	private int pos;
	private int level;
	private char ch;
	
	private JsonToken token = null;
	
	

	private int 		markedLoc;
	private int			markedLine;
	private int 		markedPos;
	private int 		markedLevel;
	private char		markedChar;
	private JsonToken 	markedToken;
	
	/**
	 * Constructs a new JSONDecoder to parse a JSON string
	 * into a native object.
	 *
	 * @param s The JSON string to be converted
	 *		into a native object
	 */
	public JsonTokenizer( String s) {
		jsonString = s;
		loc 	= 0;
		line 	= 1;
		pos  	= -1;
		level	= 0;
		nextChar();
	}
	
	public void mark(){
		markedLoc 		= loc;
		markedLine 		= line;
		markedPos 		= pos;
		markedLevel 	= level;
		markedChar 		= ch;
		markedToken 	= token;
	}
	
	public void reset(){
		loc		= markedLoc;
		line	= markedLine;
		pos  	= markedPos;
		level  	= markedLevel;
		ch  	= markedChar;
		token  	= markedToken;
	}
	
	/**
	 * Gets the next token in the input sting and advances
	 * the character to the next character after the token
	 * @throws JsonParseError 
	 */
	public JsonToken nextToken() throws JsonParseError {
		String comment = skipIgnored();
		int sLoc  = loc;
		int sPos  = pos;
		int sLine = line;
		
		switch (ch) {
			case '{':
				token = JsonToken.create( JsonToken.Type.OBJECT_START);
				token.level(level++);
				nextChar();
			break;
			case '}':
				token = JsonToken.create( JsonToken.Type.OBJECT_END);
				nextChar();
				token.level(--level);
			break;
			case '[':
				token = JsonToken.create( JsonToken.Type.ARRAY_START);
				token.level(level++);
				nextChar();
			break;
			case ']':
				token = JsonToken.create( JsonToken.Type.ARRAY_END);
				token.level(--level);				
				nextChar();
			break;
			case ',':
				token = JsonToken.create( JsonToken.Type.COMMA);
				token.level(level);
				nextChar();
			break;
			case ':':
				token = JsonToken.create( JsonToken.Type.COLON);
				token.level(level);
				nextChar();
			break;
			case '\"': 
			case '\'': 
				token = readString();
				token.level(level);
			break;
			default:
				if ( ch == 0 ){
					token = null;
				}else{
					token = readUnknownString();
					token.level(level);
				}
		}
		int eLoc  = loc;
		int ePos  = pos;
		int eLine = line;
		
		if(token!=null){
			token.positions(sLoc,eLoc,sLine,eLine,sPos,ePos);
			token.comment(comment);
		}
		return token;
	}
	
	private JsonToken readUnknownString() throws JsonParseError {
		StringBuffer string = new StringBuffer();
		string.append(ch);
		while(isKeyChar(nextChar())){
			string.append(ch);
		}
		return JsonToken.create(JsonToken.Type.STRING,string.toString().trim());
	}
	
	private JsonToken readString() throws JsonParseError{	
		int quoteIndex = loc;
		do {
			// Find the next quote in the input stream
			quoteIndex = jsonString.indexOf( ch, quoteIndex );
			if ( quoteIndex >= 0 ){
				// We found the next double quote character in the string, but we need
				// to make sure it is not part of an escape sequence.
				// Keep looping backwards while the previous character is a backslash
				int backspaceCount = 0;
				int backspaceIndex = quoteIndex - 1;
				while ( jsonString.charAt( backspaceIndex ) == '\\' ){
					backspaceCount++;
					backspaceIndex--;
				}
				// If we have an even number of backslashes, that means this is the ending quote 
				if ( ( backspaceCount & 1 ) == 0 ){
					break;
				}
				// At this point, the quote was determined to be part of an escape sequence
				// so we need to move past the quote index to look for the next one
				quoteIndex++;
			}else{ // There are no more quotes in the string and we haven't found the end yet
				parseError( "Unterminated string literal" );
			}
		} while ( true );
		// Unescape the string
		// the token for the string we'll try to read
		JsonToken token = JsonToken.create( 
			JsonToken.Type.STRING,
			unescapeString( jsonString.substring( loc, quoteIndex) ) 
		);
		// Move past the closing quote in the input string.  This updates the next
		// character in the input stream to be the character one after the closing quote
		while (loc < quoteIndex + 2) {
			nextChar();
		}		
		return token;
	}
	
	public String unescapeString( String input ) throws JsonParseError{
		String result = "";
		int backslashIndex = 0;
		int nextSubstringStartPosition = 0;
		int len = input.length();
		do{
			// Find the next backslash in the input
			backslashIndex = input.indexOf( '\\', nextSubstringStartPosition );
			if ( backslashIndex >= 0 ){
				result += input.substring( nextSubstringStartPosition, backslashIndex );
				// Move past the backslash and next character (all escape sequences are
				// two characters, except for \\u, which will advance this further)
				nextSubstringStartPosition = backslashIndex + 2;
				// Check the next character so we know what to escape
				char escapedChar = input.charAt( backslashIndex + 1 );
				switch ( escapedChar ){
					// Try to list the most common expected cases first to improve performance
					case '"':
						result += escapedChar;
						break; // quotation mark
					case '\\':
						result += escapedChar;
						break; // reverse solidus	
					case 'n':
						result += '\n';
						break; // newline
					case 'r':
						result += '\r';
						break; // carriage return
					case 't':
						result += '\t';
					break; // horizontal tab	
					// Convert a unicode escape sequence to it's character value
					case 'u':
						// Save the characters as a string we'll convert to an int
						String hexValue = "";
						int unicodeEndPosition = nextSubstringStartPosition + 4;
						// Make sure there are enough characters in the string leftover
						if ( unicodeEndPosition > len ){
							parseError( "Unexpected end of input.  Expecting 4 hex digits after \\u." );
						}
						// Try to find 4 hex characters
						for ( int i = nextSubstringStartPosition; i < unicodeEndPosition; i++ )	{
							// get the next character and determine
							// if it's a valid hex digit or not
							char possibleHexChar = input.charAt( i );
							if ( !isHexDigit( possibleHexChar ) ){
								parseError( "Excepted a hex digit, but found: " + possibleHexChar );
							}
							// Valid hex digit, add it to the value
							hexValue += possibleHexChar;
						}
						// Convert hexValue to an integer, and use that
						// integer value to create a character to add
						// to our string.
						result += String.valueOf((char)Integer.parseInt(hexValue, 16 ));
						// Move past the 4 hex digits that we just read
						nextSubstringStartPosition = unicodeEndPosition;
					break;
					case 'f':
						result += '\f';
					break; // form feed
					case '/':
						result += '/';
					break; // solidus
					case 'b':
						result += '\b';
					break; // bell
					default:
						result += '\\' + escapedChar; // Couldn't unescape the sequence, so just pass it through
				}
			}else{
				result += input.substring( nextSubstringStartPosition );
				break;
			}
		} while ( nextSubstringStartPosition < len );
		return result;
	}
	
	
	private char nextChar() {
		try{
			ch = jsonString.charAt( loc++ );
			if(ch=='\n'){
				line++; pos=-1;
			}else{
				pos++;
			}
			return ch;
		}catch(IndexOutOfBoundsException ex){
			return ch = 0;
		}
	}
	
	private String skipIgnored() throws JsonParseError {
		StringBuffer comments = new StringBuffer();
		int originalLoc;
		do {
			originalLoc = loc;
			skipWhite();
			comments.append(skipComments());
		} while ( originalLoc != loc );
		return comments.toString();
	}
	
	private String skipComments() throws JsonParseError {
		StringBuffer sf = new StringBuffer();
		if ( ch == '/' ) {
			nextChar();
			switch ( ch ){
				case '/':
					do{
						sf.append(nextChar());
					} while ( ch != '\n' && ch != 0 );
					sf.append(nextChar());
				break;
				case '*': 
					sf.append(nextChar());
					while ( true ){
						if ( ch == '*' ){
							nextChar();
							if ( ch == '/' ){
								sf.append(nextChar());
								break;
							}
						}else{
							sf.append(nextChar());
						}
						if ( ch == 0 ){
							parseError( "Multi-line comment not closed" );
						}
					}
				break;
				default:
					parseError( "Unexpected " + ch + " encountered (expecting '/' or '*' )" );
			}
		}
		if(sf.length()>1){
			String res  = sf.toString();
			if(res.length()>0){
				res = res.substring(0, res.length()-2);
			}
			return res+"\n";
		}else{
			return "";
		}
	}
	
	private void skipWhite(){
		while (isWhiteSpace(ch)){
			nextChar();
		}
	}
	
	private Boolean isKeyChar(char ch) {
		char[] excludes = new char[]{
			JsonToken.Type.COLON.character,
			JsonToken.Type.COMMA.character,
			JsonToken.Type.OBJECT_START.character,
			JsonToken.Type.OBJECT_END.character,
			JsonToken.Type.ARRAY_START.character,
			JsonToken.Type.ARRAY_END.character,
			'\'','"',(char)0
		};
		for(char c:excludes){
			if(ch==c){
				return false;
			}
		}
		return true;
	}
	
	private Boolean isWhiteSpace(char ch) {
		if ( ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r' ){
			return true;
		}
		return false;
	}
	
	private Boolean isDigit( char ch){
		return ( ch >= '0' && ch <= '9' );
	}
	
	private Boolean isHexDigit( char ch ) {
		return ( isDigit( ch ) || ( ch >= 'A' && ch <= 'F' ) || ( ch >= 'a' && ch <= 'f' ) );
	}
	
	public void parseError(String message) throws JsonParseError{
		throw new JsonParseError( message, loc, jsonString );
	}
	
	/*
	public String getObjectString() throws JsonParseError {
		int d=0;
		String str = "";
		try{
			if(token.type().equals(JsonToken.Type.ARRAY_START) || token.type().equals(JsonToken.Type.OBJECT_START)){
				switch(token.type()){
					case LEFT_BRACE: {
						do{
							if(token==null){
								break;
							}else
							if(token.type.equals(JsonToken.Type.OBJECT_END)){
								d--;
							}else 
							if(token.type.equals(JsonToken.Type.OBJECT_START)){
								d++;
							}
							
							str+=token.escapedValue();
							if(d>0){
								nextToken();	
							}
						}while(d>0);
						break;
					}
					case LEFT_BRACKET:{
						do{
							if(token==null){
								break;
							}else
							if(token.type.equals(JsonToken.Type.ARRAY_END)){
								d--;
							}else 
							if(token.type.equals(JsonToken.Type.ARRAY_START)){
								d++;
							}
							
							str+=token.escapedValue();
							if(d>0){
								nextToken();	
							}
						}while(d>0);
						break;
					}
				}
			}
		}catch(Exception ex){
			
		}
		str = str.length()==0?token.value().toString():str;
		return str;
	}
	*/
}


