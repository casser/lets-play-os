package os.letsplay.bson;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface BsonEncodable {
	
	public byte[] encodeBson(); 
}
