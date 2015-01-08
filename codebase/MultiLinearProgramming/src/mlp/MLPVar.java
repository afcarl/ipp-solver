package mlp;

import mlp.internal.BinLinExpr;
import mlp.internal.BinVar;

/**
 * 
 * @author Somdeb Sarkhel
 * 
 */
public class MLPVar {
	
	// Lower bound is ignored for now. It is assumed to be zero
	int lb;
	
	int ub;
	
	String name;
	
	BinLinExpr backingExpr = new BinLinExpr();
	
	public int getLb() {
		return lb;
	}

	public int getUb() {
		return ub;
	}

	public String getName() {
		return name;
	}

	public MLPVar(int lb, int ub, String name) {
		this.lb = lb;
		this.ub = ub;
		this.name = name;
		
		// Create backed binary variables
		int noOfDigits = 32 - Integer.numberOfLeadingZeros(ub);
		int coeff = 1;
		for (int i = 0; i < noOfDigits; i++) {
			String binaryVarName = name + "::" + i;
			BinVar bv  = new BinVar(binaryVarName);
			backingExpr.addTerm(coeff, bv);
			coeff *= 2;
		}
	}
	
	public Double value() {
		return backingExpr.evaluate();
	}

}
