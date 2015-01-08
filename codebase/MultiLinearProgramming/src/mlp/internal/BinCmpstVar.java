package mlp.internal;

import java.util.List;

/**
 * 
 * @author Somdeb Sarkhel
 * 
 */
public class BinCmpstVar extends BinVar {
	
	List<BinVar> componentVars;

	public BinCmpstVar(String name, List<BinVar> componentVars) {
		super(name);
		this.componentVars = componentVars;
	}
	
	public List<BinVar> getComponentVars() {
		return componentVars;
	}

}
