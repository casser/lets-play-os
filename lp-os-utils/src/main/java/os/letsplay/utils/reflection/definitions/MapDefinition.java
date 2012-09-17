package os.letsplay.utils.reflection.definitions;

import java.lang.reflect.ParameterizedType;

import os.letsplay.utils.reflection.Definition;
import os.letsplay.utils.reflection.Definitions;

public class MapDefinition extends Definition {
	
	private static Class<?> determineMapItemType(Class<?> clazz){
		ParameterizedType parameterizedType = getParameterizedType(clazz);
		if(parameterizedType!=null && parameterizedType.getActualTypeArguments().length==2){
			try{
				return (Class<?>) parameterizedType.getActualTypeArguments()[1];
			}catch(ClassCastException ex){}
		}
		return Object.class;
	}
	
	private static Class<?> determineMapKeyType(Class<?> clazz){
		
		ParameterizedType parameterizedType = getParameterizedType(clazz);
		if(parameterizedType!=null && parameterizedType.getActualTypeArguments().length==2){
			try{
				return (Class<?>) parameterizedType.getActualTypeArguments()[0];
			}catch(ClassCastException ex){}
		}
		return String.class;
	}
	
	private static ParameterizedType getParameterizedType(Class<?> clazz){
		java.lang.reflect.Type superClass = clazz;
		try{
			do{
				superClass = ((Class<?>)superClass).getGenericSuperclass();
			}while(!(superClass instanceof ParameterizedType));
			return (ParameterizedType) superClass;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	private Class<?>	valueClazz;
	private Class<?> 	keyClazz;
	
	public MapDefinition(Class<?> clazz) {
		super(clazz);
		valueClazz 	= determineMapItemType(clazz());
		keyClazz 	= determineMapKeyType(clazz());
	}
	
	@Override
	public Definitions type() {
		return Definitions.MAP;
	}
	
	
	public Class<?> keyClazz() {
		return keyClazz;
	}
	
	public Class<?> valueClazz() {
		return valueClazz;
	}
}
