package straitjacket.constraints;

import straitjacket.Constraint;


/**
 * A repesentation for all problems of the type f(x) > a, f(x) < a ,f(x) <> a,f(x) = a
 * or in the terms of this programm lhs=f(variables) sign rhs
 */
public abstract class EquationLikeConstraint extends Constraint {

	/**
	 * the value for the rhs of the equaltion
	 */
    int rhs;
	
	/** 
	 * General constructor for a EquationLikeConstraint, that'll 
	 * associate the Constraint with the given name and define the rhs value 
	 * @param name a name for the Constraint
	 * @param rhs the value for the rhs
	 */
    EquationLikeConstraint(String name, int rhs) {
		super(name);
		
		this.rhs  = rhs;
		
	}
	/**
	 * each EqualitionConstrain has a sign, this methode tells us which sign this constrain has  
	 * @return a String representing the Sign for the equalition
	 */
	protected abstract String operatorSign();
	
	/**
	 * this methode validates the statement lhs=f(variables) sign rhs 
	 * @return boolean (lhs sign rhs)
	 */
	protected abstract boolean operator(int lhs, int rhs);
}
