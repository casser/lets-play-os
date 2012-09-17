package os.letsplay.utils.reflection.definitions;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import os.letsplay.utils.StringUtils;
import os.letsplay.utils.reflection.Definition;
import os.letsplay.utils.reflection.Definitions;
import os.letsplay.utils.reflection.DefinitionProperty;
import os.letsplay.utils.reflection.annotations.Property;
import os.letsplay.utils.reflection.annotations.Properties;

public class BeanDefinition extends Definition 	{
	
	public BeanDefinition(Class<?> clazz) {
		super(clazz);
		initFields();
		initAnnotations();
		properties().sort();
	}
	
	@Override
	public Definitions type() {
		return Definitions.BEAN;
	}
	
	private void initFields(){
		Class<?> cls  = clazz();
		do{
			List<Field> fields =  Arrays.asList(cls.getDeclaredFields());
			for(Field field:fields){
				if(!Modifier.isStatic(field.getModifiers())){
					initField(field);
				}
			}
			List<Method> methods = Arrays.asList(cls.getDeclaredMethods());
			for(Method method:methods){
				if(!Modifier.isStatic(method.getModifiers())){
					initMethod(method);
				}
			}
			cls = cls.getSuperclass();
			Collections.reverse(properties());
		}while(Object.class != cls);
		Collections.reverse(properties());
		
	}
		
	private void initAnnotations(){
		if(clazz().isAnnotationPresent(Properties.class)){
			Properties infos = clazz().getAnnotation(Properties.class);
			for(Property info:infos.value()){
				if(!info.ignore() && info.name().length()>0){
					DefinitionProperty property =properties().get(this,info.name());
					property.info(info);
				}
			}
		}
	}
	
	private void initMethod(Method method){
		Class<?>[] params = method.getParameterTypes();
		
		if(params.length>1){
			return;
		}else{
			Property info 	= null;
			String pName 		= method.getName();
			if(method.isAnnotationPresent(Property.class)){
				info = method.getAnnotation(Property.class);
				if(info.ignore()){
					return;
				}
				if(info.name().length()>0){
					pName=info.name();
				}
			}
			
			if(pName.indexOf("get")==0 || pName.indexOf("set")==0){
				pName = StringUtils.toUnderscoredNotation(method.getName().substring(3));
			}
			
			DefinitionProperty property = properties().get(this, pName);
			property.info(info);
			if(params.length==1){
				property.clazz(params[0]);
				property.addSetter(method);
			}
			if(params.length==0 && !method.getReturnType().equals(void.class)){
				property.clazz(method.getReturnType());
				property.addGetter(method);
			}
		}
	}
		
	private void initField(Field field){
		Property info 	= null;
		String pName 		= field.getName();
		if(field.isAnnotationPresent(Property.class)){
			info = field.getAnnotation(Property.class);
			if(info.ignore()){
				return;
			}
			if(info.name().length()>0){
				pName=info.name();
			}
		}
		DefinitionProperty property = properties().get(pName);
		if(property==null){
			property = properties().get(this, pName);
		}
		if(property!=null){
			property.info(info);
			property.field(field);
		}
	}
	
	public Method lookupMethod(String methodName, Class<?>[] paramTypes){
		try {
			return clazz().getMethod(methodName, paramTypes);
		} catch (Exception e) {
			return null;
		}
	}
}
