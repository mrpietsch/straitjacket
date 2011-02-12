package straitjacket;

import straitjacket.strategies.Strategy;
import straitjacket.strategies.StrategyFactory.AvailableStrategies;

/**
 * This class controlles the solving of a ConstraintSet
 *
 */
public class Backtracker {
	
	private static int iterations = 0;
	private static long time = 0;
	
	public static int  getIterations() {
		return iterations;
	}
	
	public static long getTime() {
		return time;
	}

	/**
	 * Solves a CSP using exhaustive search (full backtracking). The backtracking
	 * algorithmn is implemented non-recursive to avoid slow downs due to method
	 * calls...
	 * @param cs the ConstraintSet to solve
	 */
	public static void backtrackSolve(ConstraintSet cs, AvailableStrategies strategy) {
		backtrack( cs, strategy, false, false);
	}
	
	/**
	 * Finds all solution for a CSP using exhaustive search (full backtracking).
	 * @param cs the ConstraintSet to solve
	 */
	public static void allSolutionsCheck(ConstraintSet cs, AvailableStrategies strategy) {
		backtrack( cs, strategy,  false , true);
	}

	/**
	 * Solves a CSP using exhaustive search with forward checking.
	 * Forward checking means to call AC3 in each step.
	 * @param cs the ConstraintSet to solve
	 */
	public static void backtrackSolveForwardCheck(ConstraintSet cs, AvailableStrategies strategy) {
		backtrack( cs, strategy, true , false);
		
	}
	
	/**
	 * Finds all solution for a CSP using exhaustive search with forward checking,
	 * Forward checking means to call AC3 in each step.
	 * @param cs the ConstraintSet to solve
	 */
	public static void allSolutionsForwardCheck(ConstraintSet cs, AvailableStrategies strategy) {
		backtrack( cs, strategy,  true , true);
	}

	
	/**
	 * Controlles the Backtracking and finding solution for the CSP.
	 * @param cs the ConstraintSet to solve
	 * @param strategyType an enum element representing the strategy
	 * @param forward boolean whether forward checking (AC3 check in each step) is performed or not
	 * @param searchAllSolutions boolean whether all solution are search or the methode should simple break when finding a solution 
	 */
	private static void backtrack(ConstraintSet cs, AvailableStrategies strategyType, boolean forward, boolean searchAllSolutions)
	{
		// ok here we want to solve the ConstraintSet cs
		
		// first we need a queue in which order to fix the variables		
		Strategy strategy = strategyType.instanciate(cs);
		
		int currentLevel = 0;
		boolean exhausted = false;
		iterations = 0;
		time = 0;
		long startTime = System.currentTimeMillis();
		Variable currentVariable = strategy.next();
		
		// this loop runs until the complete domain of the first variable/level
		// has been tried (which implies that all combinations have been tried).
		// or the constraint set became satisfied (e.g. each domain only has cardinality one and every constraint is satisfied)
		while (!exhausted) {
			iterations++;
			
			// so the cs is not yet satisfied
			// we can't advance to the next variable if
			// - currentVariable is the last variable (i.e. there are no more variables to set) OR
			// - the cs is dissatisfied OR
			// - the next Variable does not have any elements to be tied to
			//	 this last condition is not really important for the current implementation
			//	 but is necessary if we want to forward check lateron
			
			// see wether we should backtrack, set the next variable or modify the current variable
			if ( currentVariable.variableHasValuesLeft() ) {			

				currentVariable.tieToNextValue();
			
				// see what that value does to our cs constraint
				Constraint.satisfaction csSat = cs.isSatisfied();
				
				if (csSat.equals(Constraint.satisfaction.TRUE)) {
					cs.saveVariableAllocationsAsSolution();
					if (!searchAllSolutions) break;
					if (forward) System.out.println("Found solution using exhaustive search (with forward checking) after " + iterations + " iterations");
					else System.out.println("Found solution using exhaustive search after " + iterations + " iterations");
					System.out.println(cs.variableAllocationToString());
				}
				else if (csSat.equals(Constraint.satisfaction.DELAY)) {
					// this can only happen if not all variables are set, this also implies that we still have at least one level to descend
					// ok, not yet satisfied. try to set next level
					currentLevel++;
					currentVariable = strategy.next();
					if (forward)
					{ // we will take a little look forward to see if it make sense to continue   
						cs.pushAllDomains(); // and by looking forward narrowing the domains, so we save the old
						ArcConsistency.ac3(cs,currentVariable);
						// if the cs is dissatisfied, we will see in the next loop wether we still have values to try in this level
					}
				}
				

				
			} else {
				// there are no further solutions possible by changing the current level
				// so backtrack
				// TODO currentLevel is not needed anymore
				currentLevel--;
				currentVariable.untie();
				currentVariable = strategy.previous();

				// if we ever reach level -1 again, we have tried all possible combinations
				if (currentLevel < 0) exhausted = true;
				// we tried the branch now we go back to the source and use the old uncutted Domain
				else if (forward) cs.popAllDomains();  
			}
			
		}
		
		time = System.currentTimeMillis() - startTime;
		Constraint.satisfaction csSat = cs.isSatisfied();
		if (csSat.equals(Constraint.satisfaction.TRUE)) {
			if (forward) System.out.println("Solved using exhaustive search (with forward checking) after " + iterations + " iterations in " + time + " milliseconds.");
			else System.out.println("Solved using exhaustive search after " + iterations + " iterations in " + time + " milliseconds.");
		}
		else
		{
			if (forward) System.out.println("No further solution found after exhaustive search (with forward checking) with " + iterations + " iterations in " + time + " milliseconds.");
			else System.out.println("No further solution found after exhaustive search with " + iterations + " iterations in " + time + " milliseconds.");
		}
		System.out.println();
		System.out.println(cs.solutionsToString());
	}	

}
