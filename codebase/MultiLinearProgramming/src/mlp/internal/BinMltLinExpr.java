package mlp.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Somdeb Sarkhel
 * 
 */
public class BinMltLinExpr {

	List<Double> coefficients = new ArrayList<Double>();

	List<List<BinVar>> termVars = new ArrayList<List<BinVar>>();
	
	double constant;

	public void addTerm(double coefficient, BinVar... vars) {
		addTerm(coefficient, Arrays.asList(vars));
	}

	public void addTerm(double coefficient, List<BinVar> vars) {
		coefficients.add(coefficient);

		ArrayList<BinVar> termVar = new ArrayList<BinVar>();
		termVar.addAll(vars);
		termVars.add(termVar);
	}
	
	public void addConstant(double constant) {
		this.constant =  constant;
	}
	
	/**
	 * Add another multi-linear expression with this expression 
	 * @param expr
	 */
	public void add(BinMltLinExpr expr) {
		coefficients.addAll(expr.coefficients);
		termVars.addAll(expr.termVars);
		constant += expr.constant;
	}
	
	/**
	 * Add a linear expression with this expression
	 * @param expr
	 */
	public void add(BinLinExpr expr) {
		coefficients.addAll(expr.coefficients);
		for (BinVar var : expr.vars) {
			List<BinVar> termVar = new ArrayList<BinVar>();
			termVar.add(var);
			
			termVars.add(termVar);
		}
		constant += expr.constant;
	}
	
	/**
	 * Multiply the expression with constants to create a new expression. 
	 * Equivalent to  multiplying all the coefficients by the constant.
	 * 
	 * @param constant
	 */
	public BinMltLinExpr multiply(double constant) {
		BinMltLinExpr result = new BinMltLinExpr();
		for (int i = 0; i < coefficients.size(); i++) {
			result.coefficients.add(coefficients.get(i) * constant);
			result.termVars.add(termVars.get(i));
		}
		result.constant = this.constant * constant;
		return result;
	}

	/**
	 * Multiply with a linear expression. Result is a multi-linear expression
	 * 
	 * @param expr
	 * @return
	 */
	public BinMltLinExpr multiply(BinLinExpr expr) {
		
		BinMltLinExpr result = new BinMltLinExpr();
		
		// Multiply variables together
		for (int i = 0; i < expr.vars.size(); i++) {
			for (int j = 0; j < termVars.size(); j++) {
				
				double newCoeff = expr.coefficients.get(i) * coefficients.get(j);
				List<BinVar> newVars = new ArrayList<BinVar>();
				newVars.add(expr.vars.get(i));
				newVars.addAll(termVars.get(j));
				
				result.addTerm(newCoeff, newVars);
			}
		}

		// Multiply variables with constants
		for (int i = 0; i < termVars.size(); i++) {
			double newCoeff = coefficients.get(i) * expr.constant;
			result.addTerm(newCoeff, termVars.get(i));
		}
		
		for (int i = 0; i < expr.vars.size(); i++) {
			double newCoeff = expr.coefficients.get(i) * constant;
			result.addTerm(newCoeff, expr.vars.get(i));
		}
		
		result.addConstant(constant*expr.constant);
		
		return result;
	}

	/**
	 * Multiply with another multi-linear expression. Result is a multi-linear expression
	 * 
	 * @param expr
	 * @return
	 */
	public BinMltLinExpr multiply(BinMltLinExpr expr) {
		
		BinMltLinExpr result = new BinMltLinExpr();
		
		// Multiply variables together
		for (int i = 0; i < expr.termVars.size(); i++) {
			for (int j = 0; j < termVars.size(); j++) {
				
				double newCoeff = expr.coefficients.get(i) * coefficients.get(j);
				List<BinVar> newVars = new ArrayList<BinVar>();
				newVars.addAll(expr.termVars.get(i));
				newVars.addAll(termVars.get(j));
				
				result.addTerm(newCoeff, newVars);
			}
		}

		// Multiply variables with constants
		for (int i = 0; i < termVars.size(); i++) {
			double newCoeff = coefficients.get(i) * expr.constant;
			result.addTerm(newCoeff, termVars.get(i));
		}
		
		for (int i = 0; i < expr.termVars.size(); i++) {
			double newCoeff = expr.coefficients.get(i) * constant;
			result.addTerm(newCoeff, expr.termVars.get(i));
		}
		
		result.addConstant(constant*expr.constant);
		
		return result;
	}
	
	/**
	 * Convert a binary multi-linear expression into a binary linear expression
	 * by introducing new variables corresponding to multi-linear terms. These
	 * newly introduced variables are collected in the variable registry iff 
	 * they are not present in the registry already. In case the registry 
	 * contains a composite variable corresponding to some terms, that variable
	 * is used for substitution. Also if a term is linear term, it do not
	 * introduce newer variables.  
	 * 
	 * @param compositeVarRegistry
	 * @return
	 */
	public BinLinExpr convert(Map<String, BinCmpstVar> compositeVarRegistry) {
		BinLinExpr converted = new BinLinExpr();
		
		for (int i = 0; i < termVars.size(); i++) {
			List<BinVar> termVar = termVars.get(i);

			if( termVar.size() == 1) {
				// The term is actually a linear term
				converted.addTerm(coefficients.get(i), termVar.get(0));
				continue;
			}
			
			// Create a unique key for the composite variable
			Collections.sort(termVar);
			String binVarName = termVar.toString();
			
			// Look up key-wise, if not present create entry
			BinCmpstVar compositeVar;
			if(compositeVarRegistry.containsKey(binVarName)) {
				compositeVar = compositeVarRegistry.get(binVarName);
			} else {
				compositeVar = new BinCmpstVar(binVarName, termVar);
				compositeVarRegistry.put(binVarName, compositeVar);
			}
			
			// We are all done! add the term to converted linear expr
			converted.addTerm(coefficients.get(i), compositeVar);
		}
		
		converted.addConstant(constant);
		
		return converted;
	}
}
