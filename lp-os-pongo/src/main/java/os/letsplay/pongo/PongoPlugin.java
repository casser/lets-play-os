package os.letsplay.pongo;

import play.Application;
import play.Play;
import play.Plugin;
public class PongoPlugin extends Plugin {
	
	public PongoPlugin(Application application) {
		
    }

    @Override
    public void onStart() {
    	Pongo.restart();
    }

    @Override
    public void onStop() {
        if (!Play.isTest()) {
        	Pongo.shutdown();
        }
    }
    
}
