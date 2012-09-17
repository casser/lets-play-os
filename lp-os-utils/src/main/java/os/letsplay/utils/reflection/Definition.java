package os.letsplay.utils.reflection;

import java.lang.reflect.Constructor;

import os.letsplay.utils.reflection.exceptions.ReflectionException;

public abstract class Definition {
	
	private Class<?> 		clazz;
	private DefinitionProperties  	properties;
	private Annotations 	annotations;
	
	public Definition(Class<?> javaType){
		this.properties  	= new DefinitionProperties();
		this.annotations 	= new Annotations();
		this.clazz 			= javaType;
	}
	
	abstract public Definitions type();
	
	public Class<?> clazz() {
		return clazz;
	}
	
	public DefinitionProperties properties() {
		return properties;
	}
	
	public Annotations annotations() {
		return annotations;
	}
		
	public <T> T newInstance() throws ReflectionException {
		return newInstance(null,null);
	}
	
	public <T> T newInstance(Object param) throws ReflectionException {
		return newInstance(param,param.getClass());
	}
	
	@SuppressWarnings("unchecked")
	public <T> T newInstance(Object param, Class<?> cls) throws ReflectionException {
		try{
			if(param!=null){
				try{
					Constructor<?> constructor = clazz().getConstructor(new Class<?>[]{cls});
					if(constructor!=null){
						return (T) constructor.newInstance(param);
					}
				}catch(Exception e){}
			}
			return (T) clazz().newInstance();
		}catch(Exception e){
			throw new ReflectionException(e.getMessage(), e);
		}
	}
	
	public Object value(Object target, String key, Object value) {
		DefinitionProperty property = properties().get(key);
		if(property!=null){
			return property.invokeSetter(target, value);
		}
		return null;
	}
	
	public Object value(Object target, String key) {
		DefinitionProperty property = properties().get(key);
		if(property!=null){
			return property.invokeGetter(target);
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "Definition("+clazz().getName()+")";
	}
}

