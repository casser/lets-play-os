package os.letsplay.utils.reflection.exceptions;

public class ReflectionException extends Exception {
	private static final long serialVersionUID = 6993012222350447416L;
	public ReflectionException() {
		this("Unknown Reflection Error",null);
	}
	public ReflectionException(String message) {
		this(message,null);
	}
	public ReflectionException(String message,Exception cause) {
		super(message,cause);
	}
}
