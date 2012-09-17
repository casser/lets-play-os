package os.letsplay.utils.reflection.definitions;

import os.letsplay.utils.reflection.Definition;
import os.letsplay.utils.reflection.Definitions;

public class InternalDefinition extends Definition {
	
	public InternalDefinition(Class<?> javaType) {
		super(javaType);
	}
	
	@Override
	public Definitions type() {
		return Definitions.INTERNAL;
	}
	
}
