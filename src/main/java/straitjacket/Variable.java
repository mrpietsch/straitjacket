package straitjacket;

import straitjacket.ConstraintSet;
import straitjacket.Domain;
import straitjacket.VariableNameExistsException;

/**
 * Repesents a Variable, with name and a Domain of possible values 
 */
public class Variable {
	
	/**
	 * the name of the Variable 
	 */	
	private final String name;
	/**
	 * the domain for the Variable 
	 */
	private Domain domain;
	/**
	 * the value the Variable is tied to, if it ist null the variable is untied
	 */
    private Integer tiedToValue = null;
		
	/**
	 * Allocates a new Variable with the given name, the domain should be given later.
	 * This constructor should only be called by a factory taking care of duplicate names
	 * @param name the name for the new Variable
	 * @throws VariableNameExistsException is thrown when there already exist a Variable with that name,
	 * @see ConstraintSet#addVariable(String)
	 */
    Variable(String name) {
		this.name = name;
		this.domain = new Domain();
	}
	
	/**
	 * Allocates a new Variable with the given name and with a Domain of the interval from lb to ub.
	 * This constructor should only be called by a factory taking care of duplicate names.
	 * @param name the name for the new Variable
	 * @param lb the lower bound for the interval that should built the Domain
	 * @param ub the upper bound for the interval that should built the Domain
	 * @throws VariableNameExistsException is thrown when there already exist a Variable with that name,
	 * @see ConstraintSet#addVariable(String, int, int)
	 */
    Variable(String name, int lb, int ub) {
		this.name = name;
		this.domain = new Domain(lb,ub);
	}
	
	/**
	 * Allocates a new Variable with the given name and with the Domain given by the int array.
	 * This constructor should only be called by a factory taking care of duplicate names.
	 * @param name the name for the new Variable
	 * @param elements a list of all values the variable can adopt
	 * @throws VariableNameExistsException is thrown when there already exist a Variable with that name,
	 * @see ConstraintSet#addVariable(String, int[])
	 */
    Variable(String name, int[] elements) {
		this.name = name;
		this.domain = new Domain(elements);
	}
	
	/**
	 * Redefines the domain of the variable with the given elements
	 * @param elements
	 */
	public void setDomain(int ... elements) {
		this.domain = new Domain(elements);
	}

	/**
	 * Redefines the domain of the variable with the given lower
	 * and upper bound
	 * @param lb lower bound of the range
	 * @param ub upper bound of the range
	 */
	public void setDomain(int lb, int ub) {
		this.domain = new Domain(lb,ub);
	}
	
	/**
	 * Return the value the Variable is tied to
	 * @return the value the Variable is tied to
	 */
	public Integer getTiedValue() {
		return tiedToValue;
	}
	
	//TODO we might want to throw something like an IllegalValueException here, when value is not in domain
	/**
	 * Ties the Variable to the given value
	 * @param value the value we want the Variable to tie to
	 */
	public void tieToValue(Integer value) {
		tiedToValue = value;
	}
	
	/**
	 * check whether the Variable is tied or not
	 * @return true if the Variable is tied, false otherwise
	 */
	public boolean isTiedToValue() {
		return tiedToValue != null;
	}
	
	/**
	 * unties the Variable
	 */
	public void untie() {
		tiedToValue = null;
	}
	
	/**
	 * Ties the given variable to the next possible value 
	 * That means that the methode tie the Variable to the next value that is
	 * bigger (and valid) then the value the Variable was tied before starting the method.
	 */
	public void tieToNextValue() {
		if (!this.isTiedToValue()) {
			// the variable is not yet tied to any value
			this.tieToValue(this.getDomain().getSet().nextSetBit(0));
		} else {
			// we still have values left, so set the next one
			int currentValue = this.getTiedValue();
			this.tieToValue(this.getDomain().getSet().nextSetBit(currentValue+1));
		}
	}
	
	/**
	 * Checks whether there are still values to check for a variable.
	 * So this method checks whether there is a value left in the domain, which is 
	 * bigger than the value the variable is tie at the moment
	 * @return true, if there are still values
	 */
	public boolean variableHasValuesLeft() {
		if (this.isTiedToValue()) {
			return this.getDomain().getSet().length() > this.getTiedValue() + 1;
		} else {
			return this.getDomain().getSet().cardinality() > 0;
		}
	}
	
	//TODO clear von domain ueberscheiben
	
	/**
	 * Returns the domain of the Variable
	 * @return the domain of the Variable
	 */
	public Domain getDomain() {
		return domain;
	}
	
	/**
	 * Returns the name of the Variable
	 * @return the name of the Variable
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a String representation of the Variable, if the Variable is untied
	 * we return just the name of the Variable, if the Variable is tied we return
	 *  a String of the kind:  name (=TiedValue) 
	 * @return the name of the Variable
	 */
	public String toString() {
		if (isTiedToValue()) return name + "(=" + getTiedValue() + ")";
		else return name;
	}
	
}
