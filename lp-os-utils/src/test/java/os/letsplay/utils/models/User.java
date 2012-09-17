package os.letsplay.utils.models;

import java.util.ArrayList;

public class User extends Profile{
	
	private class Inbox extends ArrayList<Object>{
		private static final long serialVersionUID = -2827066120888154671L;
	}
	
	private Inbox inbox;
	
	
	public Inbox inbox(){
		return inbox;
	}
	public User inbox(Inbox value){
		this.inbox = value;
		return this;
	}
    
}