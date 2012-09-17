package os.letsplay.utils.reflection.definitions;

import os.letsplay.utils.reflection.Definition;
import os.letsplay.utils.reflection.Definitions;

public class EnumDefinition extends Definition {
	
	public EnumDefinition(Class<?> clazz) {
		super(clazz);
	}
	
	@Override
	public Definitions type() {
		return Definitions.ENUM;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T newInstance(Object param) {
		Object[] list = clazz().getEnumConstants();
		for(Object item:list){
			Enum<?> en = (Enum<?>)item;
			if(en.name().toUpperCase().equals(param.toString().toUpperCase())){
				return (T) en;
			}
		}
		return null;
	}
}
