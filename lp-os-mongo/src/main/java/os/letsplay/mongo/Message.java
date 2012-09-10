package os.letsplay.mongo;

public interface Message {
	public  Boolean isSafe();
	public  byte[] toBinaryMsg() throws Exception;
	public  void   fromBinaryMsg(byte[] mgs) throws Exception;
}
