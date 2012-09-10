package os.letsplay.bson.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BsonDocument {
	int version() default 1;
	String collection() default "";
	byte model()  default 0;
}