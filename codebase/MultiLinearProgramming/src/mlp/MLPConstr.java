package mlp;

/**
 * 
 * @author Somdeb Sarkhel
 * 
 */
public class MLPConstr {
	
	MLPMultiLinExpr expr; 
	
	char sense; 
	
	double rhs; 
	
	String name;
	
	public MLPConstr(MLPMultiLinExpr expr, char sense, double rhs, String name) {
		this.expr = expr;
		this.sense = sense;
		this.rhs = rhs;
		this.name = name;
	}

}
