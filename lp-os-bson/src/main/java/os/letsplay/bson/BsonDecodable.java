package os.letsplay.bson;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public interface BsonDecodable {
	
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Factory {
	}
	
	public void   decodeBson(byte[] bson);
}
