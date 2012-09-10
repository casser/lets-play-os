package os.letsplay.bson;


public interface BsonBinary {

	byte getType();
	void setType(byte value);
	byte[] getData();
	void setData(byte[] value);
}
