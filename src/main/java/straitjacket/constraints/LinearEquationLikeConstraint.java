package straitjacket.constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import straitjacket.Variable;
/**
 * A repesentation for all problems of the type 
 * a_n x_n+... a_1 x_1 < a,  a_n x_n+... a_1 x_1 > a,
 * a_n x_n+... a_1 x_1 <> a, a_n x_n+... a_1 x_1 = a
 * or in terms of the program
 * variables*coefficients sign rhs
 */
public abstract class LinearEquationLikeConstraint extends EquationLikeConstraint {

	/**
	 * A hashmap for all variableCoeffs
	 */
    private final HashMap<Variable,Integer> variableCoeffs;
	
	/**
	 * Creates a LinearEquationLikeConstraint with the given parameter
	 * @param name a name for the constraint
	 * @param variables list of the involved variables
	 * @param coefficients list of the coefficients for the variabeles
	 * @param rhs the value for the rhs
	 */
    LinearEquationLikeConstraint(String name, ArrayList<Variable> variables, ArrayList<Integer> coefficients, int rhs) {
		super(name,rhs);
		
		this.rhs  = rhs;
		this.variableCoeffs = new HashMap<Variable,Integer>(Math.min(variables.size(),coefficients.size()));
		this.variables = new HashSet<Variable>(Math.min(variables.size(),coefficients.size()));
		
		Iterator<Variable> varIter = variables.iterator();
		Iterator<Integer> coeffIter = coefficients.iterator();
		while (varIter.hasNext() && coeffIter.hasNext()) {
			Variable var = varIter.next();
			Integer coeff = coeffIter.next();
			variableCoeffs.put(var,coeff);
			this.variables.add(var);
		}
	}	
	
	/**
	 * Decides wether the constraint is still satified with respect to the 
	 * given concrete values of the variables. Take care to provide the 
	 * correct variables in the parameter (they're compared by reference, not by name)
	 * @param valuations a HashMap with variable assignments. 
	 * @return one of TRUE (satisfied), FALSE (dissatisfied), DELAYED (not determinable yet)
	 */
	@Override
	public satisfaction holdsFor(HashMap<Variable,Integer> valuations) {
		
		int lhs = 0;
		for (Variable var : variables) {
			// we respect the explicit valuations
			if ( valuations.containsKey( var ) ) {
				lhs += variableCoeffs.get(var) * valuations.get(var);
				continue;
			}
			
			// we respect wether a variable is tied to a value here or not
			if (var.isTiedToValue()) {
				lhs += variableCoeffs.get(var) * var.getTiedValue();
			} else {
				// if we don't have a tied variable here, look at the actual domains of the variable
				switch (var.getDomain().getSet().size()) {
					case 0: return satisfaction.FALSE;
					case 1: lhs += variableCoeffs.get(var) * var.getDomain().getMax(); break;
					default: return satisfaction.DELAY;
				}
			}
		}
		// if the function got this far we can actually evaluate wether the constraint holds or not
		return operator(lhs,rhs) ? satisfaction.TRUE : satisfaction.FALSE;
	}
	/**
	 * Decides wether the constraint is still satified with respect to the 
	 * current domains of the involved variables.
	 * @return one of TRUE (satisfied), FALSE (dissatisfied), DELAYED (not determinable yet)
	 */
	@Override
	public satisfaction holds() {
		int lhs = 0;
		for (Variable var : variables) {
			// we respect wether a variable is tied to a value here or not
			if (var.isTiedToValue()) {
				lhs += variableCoeffs.get(var) * var.getTiedValue();
			} else {
				// if we don't have a tied variable here, look at the actual domains of the variable
				switch (var.getDomain().getSet().size()) {
					case 0: return satisfaction.FALSE;
					case 1: lhs += variableCoeffs.get(var) * var.getDomain().getMax(); break;
					default: return satisfaction.DELAY;
				}
			}
		}
		// if the function got this far we can actually evaluate wether the constraint holds or not
		return operator(lhs,rhs) ? satisfaction.TRUE : satisfaction.FALSE;
	}

	/**
	 * Returns a string representation of this Constrain
	 * @return a string representation of this Constrain
	 */
	public String toString() {
		String out = this.getName() + ": ";
		boolean first = true;
		for ( Variable x : this.variableCoeffs.keySet() ) {
			if ( first ) first = false;
			else out += " + ";
			out +=  this.variableCoeffs.get(x) + x.toString();
		}
		
		out += " " + this.operatorSign() + " " + this.rhs; 
		
		return out;
	}
	
		
}
