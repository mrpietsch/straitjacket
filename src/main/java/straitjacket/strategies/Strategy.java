package straitjacket.strategies;

import java.util.Iterator;
import java.util.Stack;

import straitjacket.Variable;

/**
 * Interface that defines the operations for all strategies.
 * For the descriptions of the methods we assume, that there is a pool
 * which contains all candidates for the next backtracking step and a stack
 * which contains the variables that have been taken from the pool already.
 * The top-most element of the stack is the variable, that nextVariable() has
 * returned as last, i.e. the variable of the current backtracking step.
 * (variable choose heuristics) have to implement
 * 
 */
public abstract class Strategy implements Iterator {
	
	Stack<Variable> dequeued;
	
	/**
	 * Gets one Variable from the pool and pushs it to the stack.
	 * @return one variable of the pool (which depends on the implementation)
	 */
	public abstract Variable next();
	
	/**
	 * Pops the current variable from the stack and adds it to the pool again.
	 * @return the variable, that is now on the top of the stack (after popping)
	 */
	public abstract Variable previous();
	
	/**
	 * Looks if there is at least one element in the pool.
	 * @return true iff there is at least one element in the pool
	 */
	public abstract boolean hasNext();
	
	/**
	 * Looks if there is an element beyond the top of the stack,
	 * i.e. if the stack has at least two elements 
	 * @return true iff the stack has more than one element
	 */
	public boolean hasPrevious() {
		return this.dequeued.size() > 1;
	}
	
	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		this.dequeued.pop();
	}

}