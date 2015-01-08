package test;

/**
 * 
 * @author Somdeb Sarkhel
 * 
 */
import mlp.MLPConstant;
import mlp.MLPEnv;
import mlp.MLPLinExpr;
import mlp.MLPModel;
import mlp.MLPMultiLinExpr;
import mlp.MLPVar;

public class TestMultiplication {
	
	public static void main(String[] args) {
		MLPEnv env = new MLPEnv("mlp.log");
		MLPModel model = new MLPModel(env);
		
		MLPVar r = model.addVar(0, 2, "R");
		MLPVar s = model.addVar(0, 1, "S");
		MLPVar t = model.addVar(0, 2, "T");
		
		MLPMultiLinExpr  expr = new MLPMultiLinExpr();
		expr.addConstant(3.0 * 2.0);
		
		MLPLinExpr linExpr = new MLPLinExpr();
		linExpr.addConstant(2);
		linExpr.addTerm(-1, r);
		
		expr = linExpr.multiply(expr);

		linExpr = new MLPLinExpr();
		linExpr.addConstant(1);
		linExpr.addTerm(-1, s);
		
		expr = linExpr.multiply(expr);
		
		linExpr = new MLPLinExpr();
		linExpr.addConstant(2);
		linExpr.addTerm(-1, t);
		
		expr = linExpr.multiply(expr);
		
		expr.addTerm(4, r);
		expr.addTerm(4, s);
		expr.addTerm(8, t);
		

		model.setObjective(expr, MLPConstant.MINIMIZE);
		
		model.optimize();

		System.out.println("Objective Value is " + model.getObjectiveValue());
		for (MLPVar var : model.getVars()) {
			System.out.println("Value of variable "+var.getName()+" is "+var.value());
		}

		model.dispose();

	}

}
