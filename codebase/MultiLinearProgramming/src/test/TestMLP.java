package test;

import mlp.MLPConstant;
import mlp.MLPEnv;
import mlp.MLPModel;
import mlp.MLPMultiLinExpr;
import mlp.MLPVar;

/**
 * 
 * This example formulates and solves following simple MLP model:
 * 
 * minimize    20 - 6 r - 4 s1 - 7 s2 - 4 t + 2 s1 t + 2 s2 t 
 * subject to  s1 <= r 
 *             r + s2 <= 2
 *             0 <= r, s1, s2, t <= 2 
 *             r, s1, s2, t integers
 * 
 * @author Somdeb Sarkhel
 * 
 */
public class TestMLP {
	
	public static void main(String[] args) {
		MLPEnv env = new MLPEnv("mlp.log");
		MLPModel model = new MLPModel(env);
		
		MLPVar r = model.addVar(0, 2, "R");
		MLPVar s1 = model.addVar(0, 2, "S1");
		MLPVar s2 = model.addVar(0, 2, "S2");
		MLPVar t = model.addVar(0, 2, "T");
		
		// 20 - 6r - 4s1 - 7s2 - 4t + 2s1t + 2s2t
		MLPMultiLinExpr  expr = new MLPMultiLinExpr();
		expr.addConstant(20.0);
		expr.addTerm(-6.0, r);
		expr.addTerm(-4.0, s1);
		expr.addTerm(-7.0, s2);
		expr.addTerm(-4.0, t);
		expr.addTerm(2.0, s1, t);
		expr.addTerm(2.0, s2, t);
		
		model.setObjective(expr, MLPConstant.MINIMIZE);
		
		// s1 <= r
		expr = new MLPMultiLinExpr();
		expr.addTerm(-1.0, r);
		expr.addTerm(1.0, s1);
		
		model.addConstr(expr, MLPConstant.LESS_EQUAL, 0.0, "c1");
		
		// r + s2 <= 2
		expr = new MLPMultiLinExpr();
		expr.addTerm(1.0, r);
		expr.addTerm(1.0, s2);
		
		model.addConstr(expr, MLPConstant.LESS_EQUAL, 2.0, "c2");
		
		model.optimize();

		System.out.println("Objective Value is " + model.getObjectiveValue());
		for (MLPVar var : model.getVars()) {
			System.out.println("Value of variable "+var.getName()+" is "+var.value());
		}

		model.dispose();
	}

}
