package test;

import mlp.MLPConstant;
import mlp.MLPEnv;
import mlp.MLPModel;
import mlp.MLPMultiLinExpr;
import mlp.MLPVar;

/**
 * 
 * Class from Gurobi MIP example. For cross verification with Gurobi.
 * 
 * This example formulates and solves the following simple MIP model:
 * 
 * maximize    x + y + 2 z 
 * subject to  x + 2 y + 3 z <= 4 
 *             x + y >= 1 
 *             x, y, z binary
 * 
 * @author Somdeb Sarkhel
 * 
 */
public class TestMIP {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		MLPEnv env = new MLPEnv("mip1.log");
		MLPModel model = new MLPModel(env);

		// Create variables

		MLPVar x = model.addVar(0, 1, "x");
		MLPVar y = model.addVar(0, 1, "y");
		MLPVar z = model.addVar(0, 1, "z");

		// Set objective: maximize x + y + 2 z

		MLPMultiLinExpr expr = new MLPMultiLinExpr();
		expr.addTerm(1.0, x);
		expr.addTerm(1.0, y);
		expr.addTerm(2.0, z);
		model.setObjective(expr, MLPConstant.MAXIMIZE);

		// Add constraint: x + 2 y + 3 z <= 4

		expr = new MLPMultiLinExpr();
		expr.addTerm(1.0, x);
		expr.addTerm(2.0, y);
		expr.addTerm(3.0, z);
		model.addConstr(expr, MLPConstant.LESS_EQUAL, 4.0, "c0");

		// Add constraint: x + y >= 1

		expr = new MLPMultiLinExpr();
		expr.addTerm(1.0, x);
		expr.addTerm(1.0, y);
		model.addConstr(expr, MLPConstant.GREATER_EQUAL, 1.0, "c1");

		// Optimize model

		model.optimize();

		// Dispose of model and environment

		model.dispose();

	}

}
