package straitjacket.constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import straitjacket.Variable;

/**
 * Representing Polynomeconstrains, so all Constrain of the form f(x) > < = rhs, 
 * where f(x) is a polynom 
 */
public abstract class PolynomeConstraint extends EquationLikeConstraint {

	/**
	 * the list of elements that represents the polynom
	 */
    private final ArrayList<PolynomElement> elements;
	
	/**
	 * Create a PolynomeConstraint with the Name name, a function consisting of
	 * the elememts and with Rhs rhs  
	 *  
	 * @param name the name for the new Constraint
	 * @param elems a list of elements that should build the function 
	 * @param rhs the rhs for the new Constraint
	 */
    PolynomeConstraint(String name, int rhs, ArrayList<PolynomElement> elems) {
		super(name, rhs);
		elements=elems; 
		
		//search all involved from the elements
		Iterator<PolynomElement> elemIter = elements.iterator();
		variables=new HashSet<Variable>();				
		while (elemIter.hasNext()) {
            for (Variable variable : elemIter.next().variables) {
                if (!variables.contains(variable)) variables.add(variable);
            }
		}
	}
	
	/**
	 * Decides wether the constraint is still satified with respect to the values of the variables 
	 * given in valuations and by the tied values of variables. Take care to provide the correct 
	 * variables in the parameter (they're compared by reference, not by name)
	 * @param valuations a HashMap with variable assignments. 
	 * @return one of TRUE (satisfied), FALSE (dissatisfied), DELAYED (not determinable yet)
	 */
	@Override
	public satisfaction holdsFor(HashMap<Variable,Integer> valuations) {
		//fist we check whether all vars have a value 
		for (Variable var : variables)	 {
			// we respect the explicit valuations
			if ( !valuations.containsKey( var ) ) {
				// we respect wether a variable is tied to a value here or not
				if (!var.isTiedToValue()) {
					// if we don't have a tied variable here, look at the actual domains of the variable
					switch (var.getDomain().getSet().size()) {
						case 0: return satisfaction.FALSE;
						case 1: var.tieToValue(var.getDomain().getMax()); break; //so we fix now
						default: return satisfaction.DELAY;
					}
				}
			}
		}
		
			int lhs = 0;
        for (PolynomElement element : elements) {
            lhs += element.getValueWithRespectTo(valuations);
        }
			return operator(lhs,rhs) ? satisfaction.TRUE : satisfaction.FALSE; 
	}
	
	/**
	 * Decides whether the constraint is still satified with respect to the 
	 * current domains of the involved variables. 
	 * @return one of TRUE (satisfied), FALSE (dissatisfied), DELAYED (not determinable yet)
	 */
	@Override
	public satisfaction holds() {
		
		for (Variable var : variables) {
			// all vars have to be tied
			if (!var.isTiedToValue()) {
				// if we don't have a tied variable here, look at the actual domains of the variable
				switch (var.getDomain().getSet().size()) {
					case 0: return satisfaction.FALSE;//empty Domain
					case 1: var.tieToValue(var.getDomain().getMax()); break; //so we fix now
					default: return satisfaction.DELAY;
				}
			}
		}
		// now we now that each var ist tied, now we build the lhs
		int lhs = 0;
        for (PolynomElement element : elements) {
            lhs += element.getValue();
        }
		return operator(lhs,rhs) ? satisfaction.TRUE : satisfaction.FALSE; 
	}

	/**
	 * Return a string representation of the constraint
	 * 
	 * @return a string representation of the constraint
	 */
	@Override
	public String toString() {
		String out = this.getName() + ": ";
		boolean first = true;
        for (PolynomElement element : elements) {
            if (first) first = false;
            else out += " + ";
            out += element.toString() + " ";
        }
		out += " " + this.operatorSign() + " " + this.rhs; 		
		return out;
	}

}
