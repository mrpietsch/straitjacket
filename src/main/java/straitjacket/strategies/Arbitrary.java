package straitjacket.strategies;

import java.util.Stack;

import straitjacket.ConstraintSet;
import straitjacket.Variable;


public class Arbitrary extends Strategy {

	private final Stack<Variable> candidates;
	
	public Arbitrary(ConstraintSet cs) {
		this.dequeued = new Stack<Variable>();
		this.candidates = new Stack<Variable>();
		for ( Variable v : cs.getVariables() ) {
			this.candidates.push(v);
		}
	}
	
	public boolean hasNext() {
		return ! this.candidates.isEmpty();
	}

	public Variable next() {
		Variable v = this.candidates.pop();
		this.dequeued.push(v);
		return v;
	}

	public Variable previous() {
		this.candidates.push(this.dequeued.pop());
		return this.dequeued.isEmpty() ? null : this.dequeued.peek();
	}
	
}
