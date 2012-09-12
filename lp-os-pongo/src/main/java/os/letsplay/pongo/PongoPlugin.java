package os.letsplay.pongo;

import os.letsplay.utils.Types;
import play.Application;
import play.Plugin;
public class PongoPlugin extends Plugin {
	
	public PongoPlugin(Application application) {
		
    }

    @Override
    public void onStart() {
    	System.out.println("Reloading Types");
    	System.out.println(Types.classMap.keySet());
    	Types.classMap.clear();
    	Pongo.restart();
    }

    @Override
    public void onStop() {
        Pongo.shutdown();
    }
    
}
