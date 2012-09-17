package os.letsplay.utils.reflection;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;

import os.letsplay.utils.StringUtils;

public class Annotations extends LinkedHashMap<String,Annotation> {
	private static final long serialVersionUID = 902728003050732126L;
	public boolean containsKey(String key) {
		return super.containsKey(StringUtils.toUnderscoredNotation(key));
	}
	public Annotation get(String key) {
		return super.get(key);
	}
	public Annotation put(Annotation value) {
		return super.put(value.annotationType().getName(), value);
	}
}
