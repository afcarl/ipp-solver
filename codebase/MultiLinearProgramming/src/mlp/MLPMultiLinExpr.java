package mlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mlp.internal.BinLinExpr;
import mlp.internal.BinMltLinExpr;

/**
 * 
 * @author Somdeb Sarkhel
 * 
 */
public class MLPMultiLinExpr {
	
	List<Double> coefficients = new ArrayList<Double>();
	
	List<List<MLPVar>> termVars = new ArrayList<List<MLPVar>>();
	
	double constant = 0.0d;
	
	BinMltLinExpr backedBinaryExpr = new BinMltLinExpr();
	
	public void addTerm(double coefficient, MLPVar... vars){
		addTerm(coefficient, Arrays.asList(vars));
	}
	
	public void addTerm(double coefficient, List<MLPVar> vars){
		coefficients.add(coefficient);
		
		ArrayList<MLPVar> termVar = new ArrayList<MLPVar>();
		termVar.addAll(vars);
		termVars.add(termVar);
		
		// Convert the added term to a binary multi-linear expression and add the
		// expression to the backed binary multi-linear expression.
		BinLinExpr binLinExpr0 = vars.get(0).backingExpr;
		if(vars.size() == 1) {
			// The term is actually a linear term
			// Retrieve the backed expression of the variable and multiply it with
			// the coeff and add it to the backed binary multi-linear expression.
			backedBinaryExpr.add(binLinExpr0.multiply(coefficient));
		} else {
			// In case length is greater than 1, (i.e.- an actual multi-linear term)
			// first multiply all the backed expression for each variable and then
			// multiply it with the coefficient. Finally add the resultant multi-
			// linear expression with the backed binary multi-linear expression.
			
			BinMltLinExpr termVarMltLinExpr = binLinExpr0.multiply(vars.get(1).backingExpr); 
			for (int i = 2; i < vars.size(); i++) {
				termVarMltLinExpr = termVarMltLinExpr.multiply(vars.get(i).backingExpr);
			}
			
			backedBinaryExpr.add(termVarMltLinExpr.multiply(coefficient));
		}
		
	}
	
	public void addConstant(double constant) {
		this.constant = constant;
		
		// Add the constant to the backed expression
		backedBinaryExpr.addConstant(constant);
	}
	
	public void add(MLPMultiLinExpr expr) {
		this.addConstant(constant + expr.constant);
		for (int i = 0; i < expr.coefficients.size(); i++) {
			this.addTerm(expr.coefficients.get(i), expr.termVars.get(i));
		}
	}

}
