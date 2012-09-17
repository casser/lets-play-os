package os.letsplay.utils.reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import os.letsplay.utils.StringUtils;
import os.letsplay.utils.reflection.definitions.BeanDefinition;

public class DefinitionProperties extends ArrayList<DefinitionProperty> {
	private static final long serialVersionUID = 902728003050732126L;
	private static class PropertyComparator implements Comparator<DefinitionProperty>{
		public int compare(DefinitionProperty o1, DefinitionProperty o2) {
			return o1.index()-o2.index();
		}
		
	}
	public DefinitionProperty get(String key) {
		for(DefinitionProperty property:this){
			if(property.name().equals(StringUtils.toUnderscoredNotation(key))){
				return property;
			}
		}
		return null;
	}
	
	public DefinitionProperty get(BeanDefinition parent, String name) {
		DefinitionProperty property = get(name);
		if(property==null){
			add(property = new DefinitionProperty(parent, name));
			property.index(size());
		}
		
		return property;
	}
	
	public boolean has(String key) {
		return get(key)!=null;
	}
	
	public void print() {
		for(DefinitionProperty property:this){
			System.out.println(property);
		}
	}

	public void sort() {
		Collections.sort(this,new PropertyComparator());
	}

	
}
