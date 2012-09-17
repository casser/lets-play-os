package os.letsplay.utils;

import java.util.LinkedHashMap;

public class Mapper extends LinkedHashMap<Object, Object> {
	private static final long serialVersionUID = -7350718202614113867L;
	public Mapper insert(Object key,Object value){
		put(key, value);
		return this;
	}
	public static Mapper create(Object key,Object value){
		return new Mapper().insert(key, value);
	}
}
