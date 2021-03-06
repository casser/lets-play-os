package os.letsplay.json;

public class JsonParseError extends Exception {
	
	private static final long serialVersionUID = -3872954761146946303L;
	
	private int location;
	private String text;
	
	public JsonParseError(String message) {
		super(message);
	}
	public JsonParseError(String message,Throwable ex) {
		super(message,ex);
	}
	public JsonParseError( String message, int location, String text) {
		super( message );
		this.location = location;
		this.text = text;
	}
	
	public int getLocation(){
		return location;
	}
	
	public String getText(){
		return text;
	}
	
}

