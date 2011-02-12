package straitjacket.constraints;

import java.util.ArrayList;

import straitjacket.constraints.PolynomElement;


/**
 * Representing a Bigger Constrains for polynome functions, so all Constrain of the form f(x) > rhs, 
 * where f(x) is a polynom 
 */
public class BiggerPolynomeConstraint extends PolynomeConstraint {
	
	/**
	 * Creates a BiggerPolynomeConstraint, so a constraint of the form f(x) > rhs, 
	 * where f(x) is a polynom 
	 */
	public BiggerPolynomeConstraint(String name, ArrayList<PolynomElement> elems, int rhs ) {
		super(name, rhs, elems);
	}
	
	/**
	 * The operator for the Constrain. A BiggerPolynomeConstraint means >
	 * @return the sign representing the BiggerPolynomeConstraint: ">" 
	 */
	@Override
	public String operatorSign() {
		return ">";
	}
	
	/**
	 * verifies the operation (lhs > rhs), were lhs is constucted in a method of PolynomeConstraint
	 * @return true if lhs is bigger then rhs false otherwise 
	 */	
	public boolean operator(int lhs, int rhs) {
		return lhs > rhs;
	}
}
