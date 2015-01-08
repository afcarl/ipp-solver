package mlp.internal;


/**
 * 
 * @author Somdeb Sarkhel
 * 
 */
public class BinLinConstr {

	BinLinExpr expr; 
	
	char sense; 
	
	double rhs; 
	
	String name;
	
	public BinLinConstr(BinLinExpr expr, char sense, double rhs, String name) {
		this.expr = expr;
		this.sense = sense;
		this.rhs = rhs;
		this.name = name;
	}
	
	public BinLinExpr getExpr() {
		return expr;
	}
	
	public String getName() {
		return name;
	}
	
	public double getRhs() {
		return rhs;
	}
	
	public char getSense() {
		return sense;
	}
}
