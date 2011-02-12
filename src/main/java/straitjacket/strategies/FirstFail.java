package straitjacket.strategies;

import java.util.*;

import straitjacket.ConstraintSet;
import straitjacket.Variable;


public class FirstFail extends Strategy {

	private final HashSet<Variable> candidates;
	
	private class FFComparator implements java.util.Comparator<Variable> {

		public int compare(Variable arg0, Variable arg1) {
			return ((Integer)arg0.getDomain().cardinality()).compareTo(arg1.getDomain().cardinality());
		}
		
	}
	/**
	 * Builds an iterator for a first fail strategy.
	 * @param cs
	 */
	public FirstFail(ConstraintSet cs) {
        this.candidates = new HashSet<Variable>(cs.getVariables());
		this.dequeued = new Stack<Variable>();
	}
	
	/**
	 * @see Strategy#next()
	 */
	public Variable next() {
		Variable minVar = null;
		int minSize = Integer.MAX_VALUE;
		
		for ( Variable v : this.candidates ) {
			int size = v.getDomain().cardinality();
			if ( size == 1 ) {
				// abort immediately if size=1
				this.dequeued.push(v);
				this.candidates.remove(v);
				return v;
			}
			else if ( size < minSize ) {
				minSize = size;
				minVar = v;
			}
		}
		
		this.dequeued.push(minVar);
		this.candidates.remove(minVar);
		return minVar;
	}

	/**
	 * @see Strategy#previous()
	 */
	public Variable previous() {
		// remove the current variable from the stack
		// and reenque it 
		this.candidates.add(this.dequeued.pop());
		// the top of the stack is now the previous variable
		// which we want to return
		return this.dequeued.empty() ? null : this.dequeued.peek();
	}
	
	/**
	 * @see Strategy#hasNext()
	 */
	public boolean hasNext() {
		return ! this.candidates.isEmpty();
	}
	
}
