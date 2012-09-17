package os.letsplay.utils.models;


public class Profile extends Entity {
	
	private String name;
	
	public String name(){
		return name;
	}
	public Profile name(String value){
		this.name = value;
		return this;
	}
	
	public String picture(){
		return "http://server.com/picture/"+id();
	}
	
}
