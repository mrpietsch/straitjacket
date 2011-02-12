package straitjacket.constraints;

import java.util.ArrayList;

import straitjacket.Variable;
/**
 * A repesentation for all problems of the type 
 * a_n x_n+... a_1 x_1 <> a,
 * or in terms of the program
 * variables*coefficients != rhs
 */
public class NeqConstraint extends LinearEquationLikeConstraint {
	
	/**
	 * Creates a NeqConstraint with the given parameter
	 * @param name a name for the Constraint
	 * @param variables list of the involved variables
	 * @param coefficients list of the coefficients for the variables
	 * @param rhs the value for the rhs
	 */	
	public NeqConstraint(String name, ArrayList<Variable> variables, ArrayList<Integer> coefficients, int rhs) {
		super(name, variables, coefficients, rhs);
	}

	/**
	 * The operator for the Constraint. A NeqConstraint means <>
	 * @return the sign representing the NeqConstraint: "<>" 
	 */
	@Override
	public String operatorSign() {
		return "<>";
	}

	/**
	 * verifies the operation (lhs != rhs). lhs will be constucted in a method of LinearEquationLikeConstraint
	 * @return true if lhs does not takes the same value as rhs, false otherwise 
	 */	
	@Override
	public boolean operator(int lhs, int rhs) {
		return lhs != rhs;
	}
	
}
