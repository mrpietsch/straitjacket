package straitjacket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


/**
 * This class represents a problem to solve. 
 * It containts the set of all constraints and all variables. 
 *
 */
public class ConstraintSet extends HashSet<Constraint> {
	
	private static final long serialVersionUID = 1L;
	/**
	 * save for every variable in which constrain it is involved, so we do not have to search
	 * the hole list of constrains every time 
	 */
	private final HashMap<Variable,Collection<Constraint>> constraintsByVariables = new HashMap<Variable,Collection<Constraint>>();
	
	//TODO wieso kein orderedhashset ? dann koennen wir uns die dopplungspruefungen sparen
	/**
	 * the list of all variables in the problem 
	 */
	private final HashSet<Variable> variables = new HashSet<Variable>();
	
	/**
	 * a hash taking care, that there are no two variables with the same name
	 * potenially 'variablesByName' could contain more variables than 'variables' (unused variables)
	 * this would be a mistake of the using class
	 * TODO this could be joined with 'variables' to one member 
	 */
	private final HashMap<String,Variable> variablesByName = new HashMap<String,Variable>();
	
	/**
	 *  Found solutions are saved in form of a ArrayList of HashMap, where in each HashMap save to a Varialbe the 
	 *  valid values.
	 *  
	 *  We save do not only a value for the possblie allocation of a domain, as to be expected
	 *  by out implimentation of the given Constraint, with the objective to give possible extensions of this
	 *  program the possiblity to decide on there own how to handly the problem of return Satifaction.TRUE.
	 *  Out Constraints return Satifaction.TRUE only when all Variables are tied, but in some cases that should not
	 *  be necessary.
	 *   
	 */
	private final ArrayList< HashMap<Variable,HashSet<Integer>>> solutions = new ArrayList<HashMap<Variable,HashSet<Integer>> > ();
	
	/**
	 * Creates a new ConstraintSet. The inital ConstraintSet has no constraints. 
	 */	
	public ConstraintSet() {
		super();
	}
	
	/**
	 * Gets a new Variable with the given name, the domain should be given later 
	 * @param name the name for the new Variable
	 * @throws VariableNameExistsException is thrown when there already exist a Variable with that name,
	 * we want the names for the variables to be unique
	 */
	public Variable addVariable(String name) throws VariableNameExistsException {
		if (this.variablesByName.containsKey(name)) {
			throw new VariableNameExistsException("A Variable with the name \""
					+ name + "\" already exists.");
		} else {
			Variable v = new Variable(name);
			this.variablesByName.put(name, v);
			return v;
		}
	}

	/**
	 * Gets a new Variable with the given name and with a Domain of the interval from lb to ub 
	 * @param name the name for the new Variable
	 * @param lb the lower bound for the interval that should built the Domain
	 * @param ub the upper bound for the interval that should built the Domain
	 * @throws VariableNameExistsException is thrown when there already exist a Variable with that name,
	 * we want the names for the variables to be unique
	 */
	public Variable addVariable(String name, int lb, int ub)  throws VariableNameExistsException {
		if (this.variablesByName.containsKey(name)) {
			throw new VariableNameExistsException("A Variable with the name \""
					+ name + "\" already exists.");
		} else {
			Variable v = new Variable(name,lb,ub);
			this.variablesByName.put(name, v);
			return v;
		}
	}
	
	/**
	 * Gets a new Variable with the given name and the given Domain (as set of values) 
	 * @param name the name for the new Variable
	 * @param elements a set of integer values representing the domain of the variable
	 * @throws VariableNameExistsException is thrown when there already exist a Variable with that name,
	 * we want the names for the variables to be unique
	 */
	public Variable addVariable(String name, int[] elements)  throws VariableNameExistsException {
		if (this.variablesByName.containsKey(name)) {
			throw new VariableNameExistsException("A Variable with the name \""
					+ name + "\" already exists.");
		} else {
			Variable v = new Variable(name,elements);
			this.variablesByName.put(name, v);
			return v;
		}
	}
	
	/**
	 * Return the Variable with the given name
	 * @param name the name of the Variable we want to be returned
	 * @return the Variable with the given name or null if no Variable with what name was found
	 */
	public Variable getVariableByName(String name) {
		//TODO wird nie benutzt, die HashMap wird auch nie benutzt
		if (this.variablesByName.containsKey(name)) 
			return this.variablesByName.get(name);
		else return null;
	}
	
	
	/**
	 * Adds the specified Constraint element to this set if it is not already present.
	 * Variables from the specified Constraint, that are not already contained in the ConstraintSet
	 * will also be added.  
	 * @param c the Constrain to be added
	 * @return true if the set did not already contain the specified element.
	 */
	public boolean add(Constraint c) {
		Collection<Variable> vars = c.getVariables();
		boolean returning= super.add(c);
		if (returning){
			for (Variable var : vars) {
				// if we don't have a set of constraints for this variable, yet, create it
				if (!constraintsByVariables.containsKey(var)) constraintsByVariables.put(var,new HashSet<Constraint>());
				// add the constraint to the set of constraints containing this variable
				constraintsByVariables.get(var).add(c);
				// add the variable to the set of used variables
				variables.add(var);
			}
		}
		// finally add the constraint
		return super.add(c);
	}
	/**
	 * Removes the specified Constraint from this set if it is present.
	 * If a variable was only involved in this constrain and in no other Constrain of the
	 * ConstraintSet, it will be removed from the list of known Variables.
	 * @param c the Constraint to be removed
	 */
	public void remove(Constraint c) {
		if (contains(c)) {
			Collection<Variable> vars = c.getVariables();
			for (Variable var : vars) {
				// the variables to constraints map should definetely contain this key, actually
				if (constraintsByVariables.containsKey(var)) {
					
					constraintsByVariables.get(var).remove(c);
					if (constraintsByVariables.get(var).size() == 0) {
						// this constraint was the last one containing the variable var
						// so remove the respective set from the variables to constraints map
						constraintsByVariables.remove(var);
						// and remove the variable from the set of used variables
						variables.remove(var);
					}
				}
			}
			// finally remove the constraint
			super.remove(c);
		}
	}
	/**
	 * check whether the ConstrainSet is satified
	 * Constraint.satisfaction.TRUE: if all Constraints are satisfied for the given Variable allocation
	 * Constraint.satisfaction.DELAY: if not all nessecary Variables are tied, and no fault was found for the Variables that are already tied
	 * Constraint.satisfaction.FALSE: if a fault was found for the Variables that are already tied
	 * @return Constraint.satisfaction.TRUE if all Constraints are satisfied, FALSE if a Constraint failed, DELAY if no further statement can be made
	 */
	public Constraint.satisfaction isSatisfied() {
		Constraint.satisfaction sat = Constraint.satisfaction.TRUE;
		for (Constraint c : this) {
			Constraint.satisfaction cSatisfied = c.holds();
			if (cSatisfied.equals(Constraint.satisfaction.FALSE)) return Constraint.satisfaction.FALSE;
			if (cSatisfied.equals(Constraint.satisfaction.DELAY)) sat = Constraint.satisfaction.DELAY;
		}
		return sat;
	}
	/**
	 * gets the Hashmap, so we can grip the Constraints by there involved Variables  
	 * @return the local HashMap constraintsByVariables
	 */
	public HashMap<Variable, Collection<Constraint>> getConstraintsByVariables() {
		return constraintsByVariables;
	}

	/**
	 * returns all involved Variables
	 * @return the Variables contained in the ConstrainSet
	 */
	public HashSet<Variable> getVariables() {
		return variables;
	}
	
	/**
	 * push a copy of the current domains on the variable Domains, so 
	 * we can initalise a step in the backtracking without losing information of the old domain. 
	 */
	public void pushAllDomains() {
		for (Variable var : variables) {
			var.getDomain().push();
		}
	}
	/**
	 * pop the top of the saved domains from the domain-stack of each involved Variable.
	 * Importet when rising back in the backtracking algorith, where found restriction for 
	 * an subtree do not optain by rising back to the root. 
	 */
	public void popAllDomains() {
		for (Variable var : variables) {
			var.getDomain().pop();
		}
	}
	
	/**
	 * Return a String describing all Constaints and all Variables of the ConstraintSet
	 * @return returns a String representation of the ConstrainSet
	 */
	public String toString() {
		StringBuffer stringRepr = new StringBuffer();
		stringRepr.append("SET:" + "\n");
		for (Constraint c : this) {
            stringRepr.append(c).append("\n");
		}
		stringRepr.append("\n");
		for (Variable var : variables) {
            stringRepr.append(var).append(" in ").append(var.getDomain()).append("\n");
		}
		return stringRepr.toString();
	}

	/**
	 * Returns a String representation of the found solutions
	 * @return a String representation of the found solutions
	 */
	public String solutionsToString(){
		
		if (solutions.size()==0) return "no Solutions found";
		
		StringBuffer stringRepr = new StringBuffer();		
		stringRepr.append("Solutions: " );		
		Iterator< HashMap<Variable,HashSet<Integer>>> solutionIt= solutions.iterator();
		int i=0;
		while(solutionIt.hasNext()){
            stringRepr.append("\n\tSolution ").append(++i).append(":\n\t");
			HashMap<Variable,HashSet<Integer>> solution = solutionIt.next();
			for (Variable var : variables) {
				HashSet<Integer> value = solution.get(var);
                stringRepr.append(var.getName()).append(" in ").append(value.toString()).append("; ");
			}		
		}
		return stringRepr.toString();
	}
	
	/**
	 * Returns all found and saved solutions
	 * @return all found and saved solutions
	 */
	public ArrayList< HashMap<Variable,HashSet<Integer>>> getAllFoundSolutions(){
		return solutions;
	}

	/**
	 * Save the current allocation of the Variable as a solution
	 */
	public void saveVariableAllocationsAsSolution(){
		HashMap<Variable,HashSet<Integer>> solution = new HashMap<Variable,HashSet<Integer>>();
		
		for (Variable var : variables) {
			HashSet<Integer> domain = new HashSet<Integer>();
			if (var.isTiedToValue()) {
				domain.add(var.getTiedValue());
			} else {
				for (int bit=var.getDomain().getSet().nextSetBit(0);bit>=0;bit=var.getDomain().getSet().nextSetBit(bit+1)) 
					domain.add(bit);
			}
			solution.put(var,domain);
		}
		solutions.add(solution);
	}
	
	/**
	 * Returns a string that contains a representation of the current
	 * variable allocation.
	 * @return string containing the current variable allocation
	 */
	public String variableAllocationToString() {
		StringBuffer stringRepr = new StringBuffer();	
		
		for (Variable var : variables) {
			//int[] tmpdom = map.get(var);
			//StringBuffer dombuffer = new StringBuffer();
			//for(int j=0;j<tmpdom.length;j++) dombuffer.append(tmpdom[j]+" ");
			stringRepr.append(var.toString());
			stringRepr.append(" ");
		}
		
		return stringRepr.toString();
	}
}
