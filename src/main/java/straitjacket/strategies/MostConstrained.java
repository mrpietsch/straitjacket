package straitjacket.strategies;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;

import straitjacket.Constraint;
import straitjacket.ConstraintSet;
import straitjacket.Variable;


public class MostConstrained extends Strategy {
	private LinkedList<Variable> candidates;
	
	class MCComparator implements java.util.Comparator<Variable> {

		Map<Variable,Integer> constraintCountByVariable;
		
		public MCComparator(ConstraintSet cs) {
			Map<Variable,Collection<Constraint>> cBV = cs.getConstraintsByVariables();
			this.constraintCountByVariable = new HashMap<Variable,Integer>();
			for ( Variable v : cs.getVariables() ) {
				this.constraintCountByVariable.put(v,cBV.get(v).size());
			}
		}
		public int compare(Variable arg0, Variable arg1) {
			return (this.constraintCountByVariable.get(arg1)).compareTo(this.constraintCountByVariable.get(arg0));
		}
		
	}
	
	public MostConstrained(ConstraintSet cs) {
		PriorityQueue<Variable> q = new PriorityQueue<Variable>(cs.getVariables().size(),new MCComparator(cs));
		for ( Variable v : cs.getVariables() ) q.add(v);
		this.candidates = new LinkedList<Variable>(q);
		this.dequeued = new Stack<Variable>();
	}
	
	public Variable next() {
		Variable v = this.candidates.removeFirst();
		this.dequeued.push(v);
		return v;
	}

	public Variable previous() {
		// remove the current variable from the stack
		// and reenque it 
		this.candidates.addFirst(this.dequeued.pop());
		// the top of the stack is now the previous variable
		// which we want to return
		return this.dequeued.empty() ? null : this.dequeued.peek();
	}
	
	public boolean hasNext() {
		return ! this.candidates.isEmpty();
	}

}
