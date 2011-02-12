package straitjacket;

import java.util.BitSet;
import java.util.Stack;

/** 
 * A representation for a domain ( Set of possible values for a Variable)
 */
public class Domain {

	private static final long serialVersionUID = 1L;
	
	/** 
	 * A bitset which actually repesents the domain (as set of possible values)
	 * They are organised in a Stack, so that we can save a domain in the step of the 
	 * backtracking algorith and cutting the domain on a deeper level without loosing the 
	 * old domain 
	 */	
	private final Stack<BitSet> domainStack = new Stack<BitSet>();
	
	/** 
	 * Creates a new empty Domain. So a Variable with this Domain has no possible allocation.  
	 */
	public Domain () {
		domainStack.push(new BitSet()); 
	}
	
	/** 
	 * Creates a new Domain with the values of elements.
	 * @param elements the list of possible values 
	 */	
	public Domain (int[] elements) {
		domainStack.push(new BitSet());
		for (int i : elements) {
			this.domainStack.peek().set(i);
		}
	}
	
	/**
	 * Define the domain as a range containing value from [lb, ub]
	 * @param lb lower bound of the range
	 * @param ub upper bound of the range
	 */
	public Domain (int lb, int ub) {
		domainStack.push(new BitSet());
		domainStack.peek().set(lb,ub+1);
	}
	
	/**
	 * return the highest valid value in this domain 
	 * @return the highest valid value in this domain
	 */
	public int getMax() {
		//TODO wenn ich das letzte rausnehmen, wirde der bitset gleich wieder gekuerzt
		// sonst muss man doch nachsehen ob dieses max ueberhaupt noch true ist
		return this.domainStack.peek().length();
	}
	
	/**
	 * Gets the cardinality of the set.
	 * @returnhow number of elements the set contains
	 */
	public int cardinality() {
		return this.getSet().cardinality();
	}
	
	/**
	 * Copys the bitset of possible values and push then atop of the domainstack 
	 */	
	public void push() {
		domainStack.push((BitSet)domainStack.peek().clone());
	}
	
	/**
	 * push an emptySet atop of the domainstack 
	 * (so no possible values are available on this backtracking level) 
	 */	
	public void pushEmpty() {
		domainStack.push(new BitSet());
	}
	
	/**
	 * pop the bitset of possible values for the top of the stack
	 * (this happen when we move a step towards the root in the backtracking)
	 */
	public void pop() {
		domainStack.pop();
	}
	
	/**
	 * return the BitSet of possible values 
	 * @return the BitSet of possible values
	 */
	public BitSet getSet() {
		return domainStack.peek();
	}

	/**
	 * returns a array of all possible values in the for ov in int array 
	 * @return athe BitSet of possible values
	 */

	public int[] validToArray() {
		int[] output=new int[domainStack.peek().cardinality()];
		
		int i=0;
		if (domainStack.peek().get(0)) {output[0]=0; i++;} 
		int tmp=0;
		
		for(;i<output.length;i++)
		{
			tmp=domainStack.peek().nextSetBit(tmp+1);
			output[i]=tmp;
		}
		return output;
	}

	/**
	 * Return a representation of the BitSet of possible values, by listing all possible values 
	 * @return a String representation of the Domain
	 */
	public String toString() {
		StringBuffer stringRepr = new StringBuffer();
		stringRepr.append("{");
		boolean first = true;
		for (int i=domainStack.peek().nextSetBit(0);i>=0;i=domainStack.peek().nextSetBit(i+1)) {
			if (!first) stringRepr.append(",");
			else first = false;
			stringRepr.append(i);
		}
		stringRepr.append("}");
		return stringRepr.toString();
	}
	
}
