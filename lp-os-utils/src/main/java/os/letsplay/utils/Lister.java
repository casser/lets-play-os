package os.letsplay.utils;

import java.util.ArrayList;

public class Lister extends ArrayList<Object> {
	private static final long serialVersionUID = 870071214100060751L;

	public Lister insert(Object value){
		this.add(value);
		return this;
	}
	
	public static Lister create(Object value){
		return new Lister().insert(value);
	}
}
