package mlp.internal;

import gurobi.GRBLinExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Somdeb Sarkhel
 * 
 */
public class BinLinExpr {

	List<Double> coefficients = new ArrayList<Double>();
	
	List<BinVar> vars = new ArrayList<BinVar>();
	
	double constant;

	public List<BinVar> getVars() {
		return vars;
	}
	
	public List<Double> getCoefficients() {
		return coefficients;
	}
	
	public void addTerm(double coefficient, BinVar var){
		coefficients.add(coefficient);
		vars.add(var);
	}
	
	public void addConstant(double constant) {
		this.constant =  constant;
	}
	
	/**
	 * Add another linear expression with this expression 
	 * @param expr
	 */
	public void add(BinLinExpr expr) {
		coefficients.addAll(expr.coefficients);
		vars.addAll(expr.vars);
		constant += expr.constant;
	}
	
	/**
	 * Multiply the expression with constants to create a new expression. 
	 * Equivalent to  multiplying all the coefficients by the constant.
	 * 
	 * @param constant
	 */
	public BinLinExpr multiply(double constant) {
		BinLinExpr result = new BinLinExpr();
		for (int i = 0; i < coefficients.size(); i++) {
			result.coefficients.add(coefficients.get(i) * constant);
			result.vars.add(vars.get(i));
		}
		result.constant = this.constant * constant;
		return result;
	}

	/**
	 * Multiply with another linear expression. Result is a multi-linear expression
	 * 
	 * @param expr
	 * @return
	 */
	public BinMltLinExpr multiply(BinLinExpr expr) {
		
		BinMltLinExpr result = new BinMltLinExpr();
		
		// Multiply variables together
		for (int i = 0; i < vars.size(); i++) {
			for (int j = 0; j < expr.vars.size(); j++) {
				double newCoeff = coefficients.get(i) * expr.coefficients.get(j);
				result.addTerm(newCoeff, vars.get(i), expr.vars.get(j));
			}
		}
		
		// Multiply variables with constants
		for (int i = 0; i < vars.size(); i++) {
			double newCoeff = coefficients.get(i) * expr.constant;
			result.addTerm(newCoeff, vars.get(i));
		}
		
		for (int i = 0; i < expr.vars.size(); i++) {
			double newCoeff = expr.coefficients.get(i) * constant;
			result.addTerm(newCoeff, expr.vars.get(i));
		}
		
		result.addConstant(constant*expr.constant);
		
		return result;
	}

	/**
	 * Multiply with a multi-linear expression. Result is a multi-linear expression
	 * 
	 * @param expr
	 * @return
	 */
	public BinMltLinExpr multiply(BinMltLinExpr expr) {
		
		BinMltLinExpr result = new BinMltLinExpr();
		
		// Multiply variables together
		for (int i = 0; i < vars.size(); i++) {
			for (int j = 0; j < expr.termVars.size(); j++) {
				
				double newCoeff = coefficients.get(i) * expr.coefficients.get(j);
				List<BinVar> newVars = new ArrayList<BinVar>();
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
	
	/**
	 * Convert the expression to a Gurobi friendly format.
	 * 
	 * @return
	 */
	public GRBLinExpr convertForGRB() {
		GRBLinExpr convertedExpr = new GRBLinExpr();
		for (int i = 0; i < vars.size(); i++) {
			convertedExpr.addTerm(coefficients.get(i), vars.get(i).getBackedVar());
		}
		convertedExpr.addConstant(constant);
		return convertedExpr;
	}
	
	/**
	 * Evaluate the expression, with values associated with variables
	 * 
	 * @return
	 */
	public Double evaluate() {
		double result = 0.0;
		for (int i = 0; i < vars.size(); i++) {
			Double value = vars.get(i).value();

			if(value == null) {
				return null;
			}
			
			result += value * coefficients.get(i);
		}
		result += constant;
		return result;
	}
}