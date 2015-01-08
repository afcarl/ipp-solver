package mlp;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mlp.internal.BinCmpstVar;
import mlp.internal.BinLinConstr;
import mlp.internal.BinLinExpr;
import mlp.internal.BinMltLinExpr;
import mlp.internal.BinVar;

/**
 * 
 * @author Somdeb Sarkhel
 * 
 */
public class MLPModel {

	private MLPEnv env;

	Map<String, MLPVar> vars = new HashMap<String, MLPVar>();

	Map<String, MLPConstr> constrs = new HashMap<String, MLPConstr>();

	List<BinVar> binaryVariables = new ArrayList<BinVar>();
	Map<String, BinCmpstVar> compositeVariables = new HashMap<String, BinCmpstVar>();

	List<BinLinConstr> binaryConstraints = new ArrayList<BinLinConstr>();

	BinLinExpr binObjective;
	int objectiveSense;
	MLPMultiLinExpr objective;

	GRBModel grbModel;
	GRBEnv grbEnv;

	Double objectiveValue;

	public MLPModel(MLPEnv env) {
		this.env = env;

		// Set gurobi env
		try {
			grbEnv   = new GRBEnv(env.gurobiLogFileName);
			grbModel = new GRBModel(grbEnv);
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}

	public MLPEnv getEnv() {
		return env;
	}

	public Double getObjectiveValue() {
		return objectiveValue;
	}

	public Collection<MLPVar> getVars() {
		return vars.values();
	}

	public MLPVar getVar(String name) {
		return vars.get(name);
	}

	public MLPVar addVar(int lb, int ub, String name) {
		MLPVar var = new MLPVar(lb, ub, name);
		vars.put(name, var);

		binaryVariables.addAll(var.backingExpr.getVars());

		// Add to gurobi
		for (BinVar binVar : var.backingExpr.getVars()) {
			try {
				GRBVar x = grbModel.addVar(0.0, 1.0, 0.0, GRB.BINARY, binVar.toString());
				binVar.setBackedVar(x);
			} catch (GRBException e) {
				System.out.println("Error code: " + e.getErrorCode() + ". " +
						e.getMessage());
			}
		}
		return var;
	}

	public MLPConstr addConstr(MLPMultiLinExpr expr, char sense, double rhs, String name) {
		MLPConstr constr = new MLPConstr(expr, sense, rhs, name);
		constrs.put(name, constr);

		// Access the backed multi-linear expresion
		BinMltLinExpr binMltLinExpr = expr.backedBinaryExpr;

		// Convert the binary multi-linear expression into a binary linear expression 
		// and collect the variables in the variable registry
		BinLinExpr binLinExpr = binMltLinExpr.convert(compositeVariables);
		BinLinConstr binLinConstr = new BinLinConstr(binLinExpr, sense, rhs, "bin@"+name);
		binaryConstraints.add(binLinConstr);

		return constr;
	}

	public void setObjective(MLPMultiLinExpr expr, int sense) {
		objective = expr;
		binObjective = expr.backedBinaryExpr.convert(compositeVariables);
		objectiveSense = sense;
	}
	
	public MLPMultiLinExpr getObjective() {
		return objective;
	}
	
	public int getObjectiveSense() {
		return objectiveSense;
	}

	public void optimize() {
		try {
			// Add composite variables in Gurobi
			for (BinVar binVar : compositeVariables.values()) {
				GRBVar x = grbModel.addVar(0.0, 1.0, 0.0, GRB.BINARY, binVar.toString());
				binVar.setBackedVar(x);
			}

			// Integrate new variables
			grbModel.update();

			// Add bound constraints for the variables
			for (MLPVar var : vars.values()) {
				grbModel.addConstr(var.backingExpr.convertForGRB(), MLPConstant.GREATER_EQUAL, var.lb, "lb@"+var.name);
				grbModel.addConstr(var.backingExpr.convertForGRB(), MLPConstant.LESS_EQUAL, var.ub, "ub@"+var.name);
			}

			// Add "Equivalent" hard constraints for composite variables 
			for (BinCmpstVar cmpstVar : compositeVariables.values()) {
				List<BinVar> componentVars = cmpstVar.getComponentVars();

				GRBLinExpr firstConstr = new GRBLinExpr();
				firstConstr.addTerm(-1.0, cmpstVar.getBackedVar());

				GRBLinExpr secondConstr = new GRBLinExpr();
				secondConstr.addTerm(componentVars.size(), cmpstVar.getBackedVar());

				for (BinVar componentVar : componentVars) {
					firstConstr.addTerm(1.0, componentVar.getBackedVar());
					secondConstr.addTerm(-1.0, componentVar.getBackedVar());
				}

				double firstConstrRhs = componentVars.size() - 1.0;
				grbModel.addConstr(firstConstr, GRB.LESS_EQUAL, firstConstrRhs, "grb.first@"+cmpstVar);
				grbModel.addConstr(secondConstr, GRB.LESS_EQUAL, 0, "grb.second@"+cmpstVar);
			}

			// Add all other constraints in Gurobi
			for (BinLinConstr constr : binaryConstraints) {
				grbModel.addConstr(constr.getExpr().convertForGRB(), constr.getSense(), constr.getRhs(), constr.getName());
			}

			// Set objective function in Gurobi
			grbModel.setObjective(binObjective.convertForGRB(), objectiveSense);
//			grbModel.getEnv().set(GRB.DoubleParam.TimeLimit, timeLimit);
			
			// Optimize Gurobi
			grbModel.optimize();

			objectiveValue = grbModel.get(GRB.DoubleAttr.ObjVal);

		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}

	public void dispose() {
		grbModel.dispose();
		try {
			grbEnv.dispose();
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}

}
