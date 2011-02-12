package straitjacket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


/**
 * Generell representation for constraints
 */
public abstract class Constraint {
	
	/**
	 * A counter of the autotitling, so that the constraintnames are unique
	 */
	static private int counter = 0;  
	
	/** Enumeration for the state of a constraint. It may be satisfied (TRUE), dissatisfied (FALSE)
	 * or not determinable (DELAYED).
	 */
	public enum satisfaction {TRUE, FALSE, DELAY};
	
	/**
	 * A name for our Constraint
	 */
	protected String name;
	
	/**
	 * A list of the involved variables
	 */
	protected HashSet<Variable> variables;
	
	/**
	 * Default-Constructor that will generate a name with an
	 * underscore and an auto-incremented value like "_42"
	 */
	public Constraint() {
		this.name = "_" + counter++;
	}
	
	/** General constructor for a constraint object, that'll 
	 * associate the constraint with the given name. 
	 * @param name a name for the Constraint 
	 */
	public Constraint(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the name attribute
	 * @return the name of the Constraint
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Change the name attribute. You may also set this name
	 * at construction time.
	 * @param name the new name for the Constraint
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns a string representation of the Constraint
	 * 
	 * @return a string representation of the Constraint
	 */	
	public String toString() {
		return this.name;
	}
	
	/**
	 * Returns the number of involved variables
	 * @return number of involved variables
	 */
	public int getNumberOfVariables() {
		return variables.size();
	}
	
	/**
	 * Returns the number of notied involved variables
	 * @return number of notied involved variables
	 */
	public int getNumberOfFreeVariables() {
		return getFreeVariables().size();
	}

	
	/**
	 * 
	 * Returns all involved untied variables
	 * @return all involved untied variables as Collection
	 */
	public Collection<Variable> getFreeVariables() {
		ArrayList<Variable> freevars=new ArrayList<Variable> ();
		Iterator <Variable> varIt=variables.iterator();		
		while (varIt.hasNext()){
			Variable currentvar=varIt.next();
			if (!currentvar.isTiedToValue()) freevars.add(currentvar);
		}
		return freevars;
	}
	
	/**
	 * 
	 * Returns all involved variables
	 * @return all involved variables as Collection
	 */
	public Collection<Variable> getVariables() {
		return variables;
	}
	
	/**
	 * Checks whether a given variable is involved in the constraint.
	 * The check is done on the reference, not on the name of the variable!
	 * @param variable the variable to check
	 * @return true, if the variable is involved in the constraint
	 */
	public boolean containsVariable(Variable variable) {
		return variables.contains(variable);
	}
	
	/**
	 * Decides whether the constraint is still satified with respect to the 
	 * current domains of the involved variables. This default method just returns
	 * FALSE. You might want to override this behaviour.
	 * @return one of TRUE (satisfied), FALSE (dissatisfied), DELAYED (not determinable yet)
	 */
	public satisfaction holds() {
		return satisfaction.FALSE;
	}
	
	/**
	 * Decides whether the constraint is still satified with respect to the 
	 * given concrete values of the variables. Take care to provide the 
	 * correct variables in the parameter (they're compared by reference, not by name)
	 * @param valuations a HashMap with variable assignments. 
	 * @return one of TRUE (satisfied), FALSE (dissatisfied), DELAYED (not determinable yet)
	 */
	public satisfaction holdsFor(HashMap<Variable,Integer> valuations) {
		return satisfaction.FALSE;
	}	
	
	/**
            * Makes this constraint arc consistent, e.g. modifies the respective domains, so that for every value
            * of one domain, there exists at least one value in the other domain(s) so that the constraint still holds.
            * If it turns out, that this constraint cannot be satisfied, all domains will be empty after calling this method.
            * The semantics of this method seem to limit its application to binary constraints. However, the general
            * arc consistency algorithm will not enforce this (e.g. it will not test wether the constraint is binary
            * before calling makeArcConsistent). This enables the use of more complex notions of arc consistency,
            * such as the arc consistency of a global constraint, in a well defined way.
            * @return a collection of variables which domains have changed making the constraint arc consistent.
            * When no variable's domain was changed, it may return either an empty Collection or null
        */
	public Collection<Variable> makeArcConsistent(){
		
		ArrayList<Variable> changedVars=new ArrayList<Variable>();
		if (getNumberOfFreeVariables() == 2){
			Iterator<Variable> varIt = getFreeVariables().iterator();
	
			// we know that the Iterator points only to two variables
			Variable cVar=varIt.next();		//CurrentVariable
			Variable oVar=varIt.next();		//OtherVariable
			// and we know that both are untied
			
			// the next code we will do twice, the second time we will swap cVar an oVar
			for ( int i=1;i<=2;i++ ) {
				
				boolean changed = false;
					
				while (cVar.variableHasValuesLeft()) {
					// we take a value
					cVar.tieToNextValue();
					// and search if find a valid value for the other variable to comply the constrain  
					boolean satisfiable = false; 
					while (oVar.variableHasValuesLeft()) {
						oVar.tieToNextValue();
						if ( ! this.holds().equals(Constraint.satisfaction.FALSE) ) {
							satisfiable = true;
						}
					}
					oVar.untie();
						
					if ( ! satisfiable ) {
						// found one binding for cVar, such that the constraint is not satisfiable
						// remove from domain set
						changed = true;
						// System.out.println("AC3: throwing out " + cVar );
						cVar.getDomain().getSet().clear(cVar.getTiedValue());
						
					}
				}
	
				cVar.untie();
				if ( changed ) {
					changedVars.add(cVar);
				}
				//we tried all values for cvar now switch cVar und oVar (if it is the first iteration)
				if (i==1)	{	Variable tmp=cVar; cVar=oVar; oVar=tmp; 	}
			}//end of for-loop 
		}//end of if free==2
		return changedVars;
	}//end of arcCons
	
	/**
	 * creates NodeConsistency
	 * @return bollean whether we found at lease one value to fit the constraint 
	 */
	public boolean makeNodeConsistent(){
		boolean isSatisfiable = false;
		if (getNumberOfFreeVariables() == 1){
			Iterator<Variable> it= getFreeVariables().iterator(); // getFreeVariables() return only one variable ..
			if(it.hasNext()){
				Variable cVar=it.next();				
				while (cVar.variableHasValuesLeft()) {
					cVar.tieToNextValue();
					if (holds().equals(Constraint.satisfaction.FALSE)) {
						cVar.getDomain().getSet().clear(cVar.getTiedValue());
					}
					else isSatisfiable = true;
				}
				cVar.untie();
			}
		}
		return isSatisfiable;	
	}
}
