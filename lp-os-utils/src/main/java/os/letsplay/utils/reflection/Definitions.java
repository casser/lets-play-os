package os.letsplay.utils.reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import os.letsplay.utils.reflection.definitions.ArrayDefinition;
import os.letsplay.utils.reflection.definitions.BeanDefinition;
import os.letsplay.utils.reflection.definitions.EnumDefinition;
import os.letsplay.utils.reflection.definitions.InternalDefinition;
import os.letsplay.utils.reflection.definitions.MapDefinition;
import os.letsplay.utils.reflection.definitions.SimpleDefinition;
import os.letsplay.utils.reflection.exceptions.ReflectionException;

public enum Definitions {
	
	INTERNAL {
		Class<? extends Definition> eval(Class<?> cls){
			if(Object.class.equals(cls)){
				return InternalDefinition.class;
			}
			for(Class<?> c : INTERNAL_CLASSES){
				if(c.isAssignableFrom(cls)){
					return InternalDefinition.class;
				}
			}
			return null;
		}
	},
	SIMPLE {
		Class<? extends Definition> eval(Class<?> cls){
			if(cls.isPrimitive()){
				return SimpleDefinition.class;
			}else{
				for(Class<?> c : SIMPLE_CLASSES){
					if(c.isAssignableFrom(cls)){
						return SimpleDefinition.class;
					}
				}
			}
			return null;
		}
	},
	ARRAY {
		Class<? extends Definition> eval(Class<?> cls) {
			if(cls.isArray()){
				return ArrayDefinition.class;
			}else{
				for(Class<?> c : ARRAY_CLASSES){
					if(c.isAssignableFrom(cls)){
						return ArrayDefinition.class;
					}
				}
			}
			return null;
		}
	},
	MAP {
		Class<? extends Definition> eval(Class<?> cls) {
			for(Class<?> c : MAP_CLASSES){
				if(c.isAssignableFrom(cls)){
					return MapDefinition.class;
				}
			}
			return null;
		}
	},
	ENUM {
		Class<? extends Definition> eval(Class<?> cls){
			if(cls.isEnum()){
				return EnumDefinition.class;
			}
			return null;
		}
	},
	BEAN {
		Class<? extends Definition> eval(Class<?> cls){
			return BeanDefinition.class;
		}
	};
	
	
	abstract Class<? extends Definition> eval(Class<?> clazz);
	
	public static final Class<?>[] MAP_CLASSES 		= {Map.class};
	public static final Class<?>[] INTERNAL_CLASSES = {Definitions.class,Class.class};
	public static final Class<?>[] SIMPLE_CLASSES 	= {
		Simple.class,String.class,Character.class,Integer.class,
		Long.class,Double.class,Float.class,Boolean.class,
		Date.class
	};
	public static final Class<?>[] ARRAY_CLASSES 	= {
		List.class,Set.class,Collection.class
	};
	
	public static final Map<Class<?>, Class<?>> 	IMPLEMENTATIONS = new LinkedHashMap<Class<?>, Class<?>>();
	static{
		IMPLEMENTATIONS.put(List.class, ArrayList.class);
		IMPLEMENTATIONS.put(Map.class, HashMap.class);
	}
	
	public static final Map<Class<?>,Definition> 	DEFINITIONS 	= new ConcurrentHashMap<Class<?>,Definition>();
	
	public static Definition create(Class<?> clazz) throws ReflectionException{
		if(clazz.isInterface()){
			if(IMPLEMENTATIONS.containsKey(clazz)){
				clazz = IMPLEMENTATIONS.get(clazz);
			}else{
				throw new ReflectionException("No default implementation found for interface <"+clazz.getName()+">");
			}
		}
		for (Definitions dt:Definitions.values()) {
			Class<? extends Definition> dfc = dt.eval(clazz);
			if(dfc!=null){
				try {
					return dfc
						.getConstructor(new Class[]{Class.class})
						.newInstance(clazz)
					;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static Definition get(Class<?> clazz) throws ReflectionException{
		if(!DEFINITIONS.containsKey(clazz)){
			DEFINITIONS.put(clazz, create(clazz));
		}
		return DEFINITIONS.get(clazz);
	}
	
	public static List<Definition> list(){
		Definition[] definitions = new Definition[DEFINITIONS.size()];
		DEFINITIONS.values().toArray(definitions);
		return Arrays.asList(definitions);
	}
	
	public static void clean(){
		DEFINITIONS.clear();
	}
}
