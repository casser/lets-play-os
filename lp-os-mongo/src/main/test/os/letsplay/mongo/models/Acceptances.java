package os.letsplay.mongo.models;

import java.util.HashMap;

public class Acceptances extends HashMap<Profile, Acceptance> {
	public Acceptances add(String profile,Acceptance acceptance){
		put(new Profile(profile), acceptance);
		return this;
	}
}