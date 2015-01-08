package mlp.internal;

import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBVar;


/**
 * 
 * @author Somdeb Sarkhel
 * 
 */
public class BinVar implements Comparable<BinVar> {
	
	String name;
	
	private GRBVar backedVar;
	
	public BinVar(String name) {
		this.name = name;
	}

	public GRBVar getBackedVar() {
		return backedVar;
	}
	
	public void setBackedVar(GRBVar backedVar) {
		this.backedVar = backedVar;
	}

	public Double value() {
		try {
			return backedVar.get(GRB.DoubleAttr.X);
		} catch (GRBException e) {
			return null;
		}
	}
	
	@Override
	public int compareTo(BinVar o) {
		return name.compareTo(o.name);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BinVar) {
			BinVar anotherVar = (BinVar) obj;
			return name.equals(anotherVar.name);
		}
		return super.equals(obj);
	}

}
