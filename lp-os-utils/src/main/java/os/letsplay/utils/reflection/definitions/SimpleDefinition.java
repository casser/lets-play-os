package os.letsplay.utils.reflection.definitions;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import os.letsplay.utils.reflection.Definition;
import os.letsplay.utils.reflection.Definitions;
import os.letsplay.utils.reflection.exceptions.ReflectionException;

public class SimpleDefinition extends Definition {
	
	private static String[] DATE_FORMATS = new String[]{
		"MM/dd/yyyy",
		"yyyy-MM-dd'T'HH:mm:ssZ",
		"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"		
	};
	
	public SimpleDefinition(Class<?> javaType) {
		super(javaType);
	}
	
	@Override
	public Definitions type() {
		return Definitions.SIMPLE;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T newInstance(Object param) throws ReflectionException {
		try{
			if(param==null){
				return null;
			}else if(param.getClass().isPrimitive()){
				return (T) param;
			}else
			if(clazz().equals(param.getClass())){
				return (T) param;
			}else
			if(clazz().equals(String.class)){
				return (T) param.toString();
			}if(clazz().equals(Date.class)){
				String ds = param.toString();
				if(ds.matches("\\d{10}")){
					return (T) new Date(Long.parseLong(ds)*1000);
				}else{
					for(String df:DATE_FORMATS){
						try{
							return (T) new SimpleDateFormat(df).parse(ds);
						}catch(Exception ex){}
					}
				}
				return null;
			}else{
				Method m = clazz().getMethod("valueOf", new Class[]{String.class});
				return (T) m.invoke(clazz(), param.toString());
			}
		}catch(Exception ex){
			return (T)param;
		}
	}
}
