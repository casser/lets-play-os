package os.letsplay.utils.reflection.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface Property {
	int index() default 0;
	String name() default "";
	String description() default "";
	String setter() default "";
	String getter() default "";
	String[] scope() default {"any"};
	boolean ignore() default false;
}