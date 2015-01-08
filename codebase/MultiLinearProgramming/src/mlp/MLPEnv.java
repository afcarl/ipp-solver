package mlp;

/**
 * 
 * @author Somdeb Sarkhel
 * 
 */
public class MLPEnv {
	
	String logFileName;
	
	String gurobiLogFileName;
	
	int timeOut;
	
	public MLPEnv(String logFileName) {
		this.logFileName = logFileName;
		this.gurobiLogFileName = "grb."+logFileName;
	}
	
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	
	public int getTimeOut() {
		return timeOut;
	}

}
