package straitjacket.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import straitjacket.Constraint;
import straitjacket.Variable;

/**
 * A repesentation for all problems of the type
 *  a_n x_n+... + a_1 x_1 > a, or in
 * terms of the program variables*coefficients > rhs
 */
public class BiggerConstraint extends LinearEquationLikeConstraint {

	/**
	 * Creates a BiggerConstraint with the given parameter
	 * 
	 * @param name
	 *            a name for the Constraint
	 * @param variables
	 *            list of the involved variables
	 * @param coefficients
	 *            list of the coefficients for the variables
	 * @param rhs
	 *            the value for the rhs
	 */
	public BiggerConstraint(String name, ArrayList<Variable> variables,
			ArrayList<Integer> coefficients, int rhs) {
		super(name, variables, coefficients, rhs);
	}

	/**
	 * The operator for the Constraint. A BiggerConstraint means >
	 * 
	 * @return the sign representing the BiggerConstraint: ">"
	 */
	@Override
	public String operatorSign() {
		return ">";
	}

	/**
	 * verifies the operation (lhs > rhs). lhs will be constucted in a method of
	 * LinearEquationLikeConstraint
	 * 
	 * @return true if lhs is bigger then rhs false otherwise
	 */
	public boolean operator(int lhs, int rhs) {
		return lhs > rhs;
	}

	/**
	 * TODO doc muss abgeaendert werden. 2 free var boundconsistency wegen der
	 * linearitaet von biggerconst
	 * 
	 * @return a collection of variables which domains have changed making the
	 *         constraint arc consistent. When no variable's domain was changed,
	 *         it may return either an empty Collection or null
	 *//*
	public Collection<Variable> makeArcConsistent() {
		ArrayList<Variable> chList = new ArrayList<Variable>();
		if (getNumberOfFreeVariables() == 2) {
			Iterator<Variable> varIt = getFreeVariables().iterator();

			// we know that the Iterator points only to two variables
			Variable var1 = varIt.next(); // CurrentVariable
			Variable var2 = varIt.next(); // OtherVariable
			// and that bother are untied, tie then to the min
			var1.tieToNextValue();
			var2.tieToNextValue();
			Constraint.satisfaction llStatus = holds();
			// the one to the min the other to the maximum
			var2.tieToValue(var2.getDomain().getMax() - 1);
			Constraint.satisfaction luStatus = holds();
			// the one to the maximum the other to the min
			var1.tieToValue(var1.getDomain().getMax() - 1);
			var2.untie();
			var2.tieToNextValue();
			Constraint.satisfaction ulStatus = holds();

			// and then both to the maximum
			var2.tieToValue(var2.getDomain().getMax() - 1);
			Constraint.satisfaction uuStatus = holds();
			// if all 4 are TRUE all combination are possible
			if ((Constraint.satisfaction.TRUE.equals(llStatus))
					&& (Constraint.satisfaction.TRUE.equals(luStatus))
					&& (Constraint.satisfaction.TRUE.equals(ulStatus))
					&& (Constraint.satisfaction.TRUE.equals(uuStatus)))
				return chList; // no changes, so an empty list
			// if all 4 are FALSE no combination is possible
			if ((Constraint.satisfaction.FALSE.equals(llStatus))
					&& (Constraint.satisfaction.FALSE.equals(luStatus))
					&& (Constraint.satisfaction.FALSE.equals(ulStatus))
					&& (Constraint.satisfaction.FALSE.equals(uuStatus))) {
				var1.untie();
				var1.getDomain().getSet().clear();
				var2.untie();
				var2.getDomain().getSet().clear();
				chList.add(var1);
				chList.add(var2);
				return chList;
			}
		}// end of if free==2
		// if we get here the simplificated algorith was useless, and we use the
		// orignal one
		return super.makeArcConsistent();
	}// end of arcCons
*/
	
	/**
	 * creates NodeConsistency, for BiggerConstrain we can check bounds
	 * consistency
	 * 
	 * @return boolean whether we found at lease one value to fit the constraint
	 */
	@Override
	public boolean makeNodeConsistent() {

		boolean isSatisfiable = false;
		if (getNumberOfFreeVariables() == 1) {
			Iterator<Variable> it = getFreeVariables().iterator();
			// getFreeVariables() return only one variable ..
			Constraint.satisfaction lowerStatus;
			Constraint.satisfaction upperStatus;
			if (it.hasNext()) {
				Variable cVar = it.next();
				// cVar was untied and then we tied it to the smallest possible
				// value
				cVar.tieToNextValue();
				lowerStatus = holds();
				cVar.tieToValue(cVar.getDomain().getMax() - 1);
				upperStatus = holds();
				cVar.untie();
				// both ends are not equal, so we have to check
				// it with the another makeNodeConsistent() method
				if (!lowerStatus.equals(upperStatus)) {
					return super.makeNodeConsistent();
				} else {
					if (upperStatus.equals(Constraint.satisfaction.TRUE))
						// both sides of the range of are valid, so every value
						// in the domain will be possible
						return true;
					else {
						// both sides of the range of are invalid and because of
						// the
						// linearity (monotony), no value of the domain is valid
						cVar.getDomain().getSet().clear();
						return false;
					}
				}
			}
		}
		return isSatisfiable;
	}
}
