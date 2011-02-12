package straitjacket.constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import straitjacket.Variable;

/**
 * Repesents an element of an polynom:
 * C * x_n^P_n *...* x_1^P_1
 * 
 */
class PolynomElement {
	
	/**
	 * A list of the involved variables (x_i)
	 */
	public final HashSet<Variable> variables;
	
	/**
	 * A hashmap for all variablePowers (P_i)
	 */
    private final HashMap<Variable,Double> variablePower;
	
	/**
	 * the constant coefficient 
	 */
    private final double cnst;
	
	/**
	 * Allocates a new PolynomElement with the given parameters.
	 * @param c the Constant coefficient that will be mulitiplied with the element
	 * @param variables all involved variables 
	 * @param powers a HashMap that assign the powers to the variables  
	 */
	public PolynomElement(double c,ArrayList<Variable> variables, ArrayList<Double> powers){
		cnst=c;
		
		this.variablePower = new HashMap<Variable,Double>(Math.min(variables.size(),powers.size()));
		this.variables = new HashSet<Variable>(Math.min(variables.size(),powers.size()));
		
		Iterator<Variable> varIter = variables.iterator();
		Iterator<Double> powerIter = powers.iterator();
		while (varIter.hasNext() && powerIter.hasNext()) {
			Variable var = varIter.next();
			Double pow = powerIter.next();
			variablePower.put(var,pow);
			this.variables.add(var);
		}
	}
	/**
	 * Return the value of the element, this should only happen when all involved 
	 * Variables are fixed.
	 * 
	 * Every Methode that calls this methode should assure that all Variables are tied. 
	 * Because this method, if not all Variables are tied, a unannounce NullPointException will be thrown. 
	 * @return the value of the element
	 */
	public double getValue(){
		double sum=cnst;
		for ( Variable x : this.variablePower.keySet() ) {
			sum =sum*Math.pow(x.getTiedValue(), variablePower.get(x));
		}
		return sum;
	}
	
	/**
	 * Return the value of the element with respect to an alternative allocation of the
	 * variables given by valuations.
	 * 
	 * Every Methode that calls this methode should assure that all Variables are tied 
	 * or allocated in the parameter valuations.
	 * Because this method if not all Variables have fixed valus, a unannounce NullPointException will be thrown. 
	 * @return the value of the element
	 */
	public double getValueWithRespectTo(HashMap<Variable,Integer> valuations){
		double sum=cnst;
		
		for (Variable var : variables) {
			// we respect the explicit valuations
			if ( valuations.containsKey( var ) ) {
				sum =sum*Math.pow(valuations.get(var), variablePower.get(var));
				continue;
			}
			
			// we respect wether a variable is tied to a value here or not
			if (var.isTiedToValue()) {
				sum =sum*Math.pow(var.getTiedValue(), variablePower.get(var));
			} 
		}

		return sum;
	}
	
	/**
	 * Return a String representation of the element
	 * @return a String representation of the element
	 */
	public String toString(){
		String out = ""+cnst;
		
		for ( Variable x : this.variablePower.keySet() ) {
			out +=" *" + x.toString()+ "^" +this.variablePower.get(x);
		}
		return out;
	}
	
}
