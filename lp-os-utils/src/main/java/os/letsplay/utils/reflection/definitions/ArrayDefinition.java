package os.letsplay.utils.reflection.definitions;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;

import os.letsplay.utils.reflection.Definition;
import os.letsplay.utils.reflection.Definitions;
import os.letsplay.utils.reflection.exceptions.ReflectionException;

public class ArrayDefinition extends Definition {
	
	private static Class<?> determineArrayItemType(Class<?> clazz){
		if(clazz.isArray()){
			return clazz.getComponentType();
		}
		ParameterizedType parameterizedType = getParameterizedType(clazz);
		if(parameterizedType!=null && parameterizedType.getActualTypeArguments().length==1){
			try{
				return (Class<?>) parameterizedType.getActualTypeArguments()[0];
			}catch(ClassCastException ex){}
		}
		return Object.class;
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
	
	Class<?> valueClazz;
	
	public ArrayDefinition(Class<?> clazz) {
		super(clazz);
		this.valueClazz = determineArrayItemType(clazz());
	}
	
	@Override
	public Definitions type() {
		return Definitions.ARRAY;
	}

	public Class<?> valueClazz() {
		return valueClazz;
	}
	
	@SuppressWarnings("unchecked")
	public void add(Object target, Object value) throws ReflectionException{
		if(List.class.isAssignableFrom(target.getClass())){
			List<Object> list = (List<Object>) target;
			list.add(value);
		}else
		if(Set.class.isAssignableFrom(target.getClass())){
			Set<Object> list = (Set<Object>) target;
			list.add(value);
		}
		else throw new ReflectionException("Unknown array type <"+target.getClass().getName()+">");
	}
	
}
