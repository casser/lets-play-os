package os.letsplay.utils.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import os.letsplay.utils.StringUtils;
import os.letsplay.utils.reflection.annotations.Property;
import os.letsplay.utils.reflection.definitions.BeanDefinition;

public class DefinitionProperty{
	
	private String name;
	private String description;
	
	private Class<?> clazz;
	private BeanDefinition parent;

	private Integer index;
	private Set<Class<? extends Annotation>> annotationTypes;
	private Map<String,Annotation> annotations;
	
	private Field  field;
	private List<Method> setters;
	private List<Method> getters;
	private Set<String> scope;
	
	public String description() {
		return description;
	}
	public DefinitionProperty description(String description) {
		this.description = description;
		return this;
	}
	
	public String name() {
		return name==null?(field!=null?field.getName():"null"):name;
	}
	public DefinitionProperty name(String name) {
		this.name = name;
		return this;
	}
	
	public Boolean hasScope(String ...scopes) {
		if(this.scope.contains("any")){
			return true;
		}
		Boolean contain = true;
		for(String s:scopes){
			contain = contain && this.scope.contains(s.toLowerCase());
		}
		return contain;
	}
	
	
	public BeanDefinition parent() {
		return parent;
	}
	
	public Field field() {
		return field;
	}
	
	public DefinitionProperty field(Field field) {
		this.field = field;
		return this;
	}

	public Class<?> clazz() {
		return field!=null?field.getType():(clazz==null)?Object.class:clazz;
	}
	
	public DefinitionProperty clazz(Class<?> clazz) {
		this.clazz = clazz;
		return this;
	}
	
	public Integer index() {
		return index;
	}
	
	public DefinitionProperty index(Integer i) {
		index = i;
		return this;
	}
	
	public Map<String,Annotation> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(Map<String,Annotation> annotations) {
		this.annotations = annotations;
		annotationTypes = new HashSet<Class<? extends Annotation>>();
		for(Annotation an:annotations.values()){
			annotationTypes.add(an.annotationType());
		}
	}
	
	public DefinitionProperty(BeanDefinition parent,String name){
		this.parent 	= parent;
		this.name 		= StringUtils.toUnderscoredNotation(name);
		this.setters 	= new ArrayList<Method>();
		this.getters 	= new ArrayList<Method>();
		this.scope		= new HashSet<String>();
		this.scope.add("any");
	}
	
	public DefinitionProperty(BeanDefinition parent,Field field){
		this(parent,field.getName());
		this.field = field;
		if(field.isAnnotationPresent(Property.class)){
			Property info = field.getAnnotation(Property.class);
			this.index = info.index();
			if(info.name().length()>0){
				this.name  = StringUtils.toUnderscoredNotation(info.name());
			}
			if(info.setter().length()>0){
				addSetter(info.setter());
			}
			if(info.getter().length()>0 && !getters.contains(info.getter())){
				addGetter(info.getter());
			}
		}
	}
	
	public void addSetter(String setter){
		addSetter(parent().lookupMethod(setter, new Class[]{clazz()}));
	}
	public void addSetter(Method setter){
		 synchronized(setters) {
			if(!setters.contains(setter)){
				setters.add(setter);
			}
		 }
	}
	public void addGetter(String setter){
		addSetter(parent().lookupMethod(setter, new Class[]{}));
	}
	public void addGetter(Method setter){
		 synchronized(getters) {
			if(!getters.contains(setter)){
				getters.add(setter);
			}
		 }
	}
		
	public Object invokeSetter(Object data, Object value) {
		if(value==null){
			return null;
		}
		for(Method setter:setters){
			try {
				 return setter.invoke(data, value);     
			} catch (Exception e) {}
		}
		if(field!=null && field.isAccessible()){
			try {
				field.set(data, value);
			} catch (Exception e2){}
		}
		return null;
	}
	
	public Object invokeGetter(Object data) {
		
		for(Method getter:getters){
			try {
				return getter.invoke(data);
			} catch (Exception e) {}
		}
		
		if(field!=null && field.isAccessible()){
			try {
				field.get(data);
			} catch (Exception e2){}
		}
		return null;
	}
	
	@Override
	public String toString() {
		try{
			return "Property["+index+"] "+parent().clazz().getSimpleName()+"."+name()+":"+clazz().getSimpleName();
		}catch(Exception ex){
			ex.printStackTrace();
			return parent()+" "+name()+clazz();
		}
	}

	public boolean hasAnnotation(Class<? extends Annotation> annotation) {
		return annotations!= null && annotationTypes.contains(annotation);
	}
	
	public void info(Property info) {
		if(info==null){
			return;
		}
		if(info.description().length()>0){
			this.description(info.description());
		}
		if(info.index()>0){
			this.index(info.index());
		}
		if(info.setter().length()>0){
			this.addSetter(info.setter());
		}
		if(info.getter().length()>0){
			this.addGetter(info.getter());
		}
		this.scope = new HashSet<String>();
		for(String s:info.scope()){
			this.scope.add(s.toLowerCase());
		}
	}
	
	
}
