package mlp;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Somdeb Sarkhel
 * 
 */
public class MLPLinExpr {

	List<Double> coefficients = new ArrayList<Double>();
	
	List<MLPVar> vars = new ArrayList<MLPVar>();
	
	double constant;
	
	public void addTerm(double coefficient, MLPVar var){
		coefficients.add(coefficient);
		vars.add(var);
	}

	public void addConstant(double constant) {
		this.constant = constant;
	}
	
	/**
	 * Multiply with a multi-linear expression. Result is a multi-linear expression
	 * 
	 * @param expr
	 * @return
	 */
	public MLPMultiLinExpr multiply(MLPMultiLinExpr expr) {
		
		MLPMultiLinExpr result = new MLPMultiLinExpr();
		
		// Multiply variables together
		for (int i = 0; i < vars.size(); i++) {
			for (int j = 0; j < expr.termVars.size(); j++) {
				
				double newCoeff = coefficients.get(i) * expr.coefficients.get(j);
				List<MLPVar> newVars = new ArrayList<MLPVar>();
				newVars.add(vars.get(i));
				newVars.addAll(expr.termVars.get(j));
				
				result.addTerm(newCoeff, newVars);
			}
		}
		
		// Multiply variables with constants
		for (int i = 0; i < vars.size(); i++) {
			double newCoeff = coefficients.get(i) * expr.constant;
			result.addTerm(newCoeff, vars.get(i));
		}
		
		for (int i = 0; i < expr.termVars.size(); i++) {
			double newCoeff = expr.coefficients.get(i) * constant;
			result.addTerm(newCoeff, expr.termVars.get(i));
		}
		
		result.addConstant(constant*expr.constant);
		
		return result;
	}


}
