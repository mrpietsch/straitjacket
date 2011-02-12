package straitjacket;

import java.util.Collection;
import java.util.Iterator;

import straitjacket.util.OrderedHashSet;



//import sun.security.x509.IssuerAlternativeNameExtension;

/**
 * A collection of methodes for forward checks, that means by checking ArcConsistency and NodeConsistency  
 * we remove value for the Variable Domains to downsize the backtrackingtree. 
 */
public class ArcConsistency {

	//TODO die Klasse sollte ac3 heissen und diese methode ArcConstistency ... 
	// Sorry aber das ist totaler Quatsch...
	
	/**
	 * AC3 algorithm to establish arc consistency. The method processes all constraints with
	 * two-digit constraints. If you provide a variable list, only those constraints are processed
	 * that contain the variable. This could make sense when doing forward checks in your backtracking,
	 * where you just want to recheck the variables you just have tied to a distinct value.
	 * To point out is here that tied (fixed) variables are considered to be constants.
	 * So two-digit constraints means that there are two untied variables left.
	 * @param cs the ConstraintSet to check
	 * @param initialVars optional variable list for the initial worklist
	 * @return FALSE if one constraint is dissatisfied, DELAY otherwise
	 * 		   (this method won't ever return TRUE; this should be checked in backtracking)
	 */
	public static Constraint.satisfaction ac3(ConstraintSet cs, Variable ... initialVars) {
		// first construct the list of all binary constraints
		OrderedHashSet<Constraint> worklist = new OrderedHashSet<Constraint>();

		// build up the initial worklist
		if ( initialVars != null && initialVars.length > 0 ) {
			// if called with a list of variables, only append
			// constraints that contain this variable and one other
            for (Variable initialVar : initialVars) worklist.addAll(cs.getConstraintsByVariables().get(initialVar));
		}
		else {
			// if called without a variable list, add all constraints with two variables
			worklist.addAll(cs);
		}
		
		// the algorithm
		while ( ! worklist.isEmpty() ) {			
			Constraint c = worklist.remove(0);
			// creating arcConsistency for this Constraint
			Collection<Variable> chvars=c.makeArcConsistent();
			
			// if some vars changed we have to check the other again
			if ( chvars!= null && chvars.size()>0 ) {
                for (Variable chvar : chvars) {
                    // has a domain tun out of values? so we failt (or succced by eleminating a subtree)
                    if (chvar.getDomain().getSet().cardinality() == 0) return Constraint.satisfaction.FALSE;
                    // enqueue all two-digit constraints where cVar is involved except
                    // the constraint that has been processed above
                    Collection<Constraint> toBeAdded = cs.getConstraintsByVariables().get(chvar);
                    toBeAdded.remove(c);
                    worklist.addAll(toBeAdded);
                }
			}
		}//end of while
		
		return Constraint.satisfaction.DELAY;
	}
	
	/**
	 * Algorithm to establish node consistency. It processed every one-digit constraint.
	 * To point out is here that tied (fixed) variables are considered to be constants,
	 * So constrain.getNumberOfVariables() could be bigger than 1, but thereby some 
	 * variables are fixed we will check these constraints here. 
	 * 
	 * @param cs the ConstraintSet
	 * @param initialVars optional variable list for the initial worklist
	 * @return FALSE if one constraint is dissatisfied, DELAY otherwise
	 * 		   (this method won't ever return TRUE; this should be checked in backtracking)
	 */
	public static Constraint.satisfaction nodeConsistency(ConstraintSet cs, Variable ... initialVars) {
		// first construct the list of all unary constraints		
		OrderedHashSet<Constraint> unaryConstraints = new OrderedHashSet<Constraint>();
		
		if ( initialVars != null && initialVars.length > 0 ) {
			for (Constraint c : cs) {
				if (c.getNumberOfFreeVariables() == 1)
                    for (Variable initialVar : initialVars) if (c.containsVariable(initialVar)) unaryConstraints.add(c);
			}
		}
		else	for (Constraint c : cs) if (c.getNumberOfFreeVariables() == 1) unaryConstraints.add(c);
		
		// now enfore node consistency
		for (Constraint c : unaryConstraints) {
			if (!c.makeNodeConsistent()) return Constraint.satisfaction.FALSE; //we found a constrain we cant fulfil anymore
		}
		// if we reach this point every constrain was at least satisfied for a possible value of the variables
		return Constraint.satisfaction.DELAY;
	}
	
	//TODO unused
	/**
	 * This method checks only if the Constraints with only fixed variables are fulfilled,
	 * so what we do here is to check if we did a mistake so far.
	 * @param cs the ConstraintSet
	 * @return FALSE if one constraint is dissatisfied, true if all Constraints are satisfied
	 */
	public static boolean holdsForFixed(ConstraintSet cs) {
		
		for (Constraint c : cs)
			if (c.getNumberOfFreeVariables() == 0)
				if (c.holds()==Constraint.satisfaction.FALSE) return false;
		
		return true;
	}
	
	
	
}
