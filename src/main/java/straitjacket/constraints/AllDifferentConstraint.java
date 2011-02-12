package straitjacket.constraints;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import straitjacket.Constraint;
import straitjacket.Variable;
import straitjacket.util.Tuple2;

/**
 * A repesentation for the AllDifferentConstraint:
 * all variables involved in the contrain should adopt different values 
 * In this class we archive this through many pairwise constraints 
 * No statement about untied variables are made there.
 */

public class AllDifferentConstraint extends Constraint {

	/**
	 * the set of pairwise different Constraints (NeqConstraints) 
	 */
	//private Constraint[] permutedConstraints;
	
	/**
	 * Creates a AllDifferentConstraint for the given variables
	 * @param variables the list of all involved variables
	 */
	public AllDifferentConstraint(Variable ... variables ) {
		super();
		
		this.variables = new HashSet<Variable>();
		for (int i = 0; i < variables.length; i++) this.variables.add(variables[i]);
	}
	
	/**
	 * Creates a AllDifferentConstraint with the given name and for the given variables
	 * @param name the name for the new Constrain
	 * @param variables the list of all involved variables
	 */	
	public AllDifferentConstraint(String name, Variable ... variables ) {
		this(variables);
		setName(name);
	}
	
	/**
	 * Decides wether the constraint is still satified with respect to the 
	 * current domains of the involved variables. 
	 * @return one of TRUE (satisfied), FALSE (dissatisfied), DELAYED (not determinable yet)
	 */
	public satisfaction holds() {
		HashSet<Integer> values = new HashSet<Integer>();
		boolean hadNonFixVariable = false;
		for (Variable var : variables) {
			int value = 0;
			boolean isFixed = false;
			
			// see wether this variable has a fixed value
			if (var.isTiedToValue()) {
				value = var.getTiedValue();
				isFixed = true;
			} else if (var.getDomain().getSet().cardinality() == 1) {
				value = var.getDomain().getSet().nextSetBit(0);
				isFixed = true;
			}
			
			// if so, see wether we had that value before
			if (isFixed) {
				if (values.contains(value)) return satisfaction.FALSE;
				else values.add(value);
			} else hadNonFixVariable = true;
		}
		// if it made it until here, it can only be true or delay
		if (hadNonFixVariable) return satisfaction.DELAY;
		else return satisfaction.TRUE;
	}
	
	/**
	 * Decides wether the constraint is still satified with respect to the 
	 * given concrete values of the variables. Take care to provide the 
	 * correct variables in the parameter (they're compared by reference, not by name)
	 * @param valuations a HashMap with variable assignments. 
	 * @return one of TRUE (satisfied), FALSE (dissatisfied), DELAYED (not determinable yet)
	 */
	public satisfaction holdsFor(HashMap<Variable,Integer> valuations) {
		HashSet<Integer> values = new HashSet<Integer>();
		boolean hadNonFixVariable = false;
		for (Variable var : variables) {
			int value = 0;
			boolean isFixed = false;
			
			// see wether this variable has a value supplied with the argument or a fixed value anyways 
			if (valuations.containsKey(var)) {
				value = valuations.get(var);
				isFixed = true;
			} else if (var.isTiedToValue()) {
				value = var.getTiedValue();
				isFixed = true;
			} else if (var.getDomain().getSet().cardinality() == 1) {
				value = var.getDomain().getSet().nextSetBit(0);
				isFixed = true;
			}
			
			// if so, see wether we had that value before
			if (isFixed) {
				if (values.contains(value)) return satisfaction.FALSE;
				else values.add(value);
			} else hadNonFixVariable = true;
		}
		// if it made it until here, it can only be true or delay
		if (hadNonFixVariable) return satisfaction.DELAY;
		else return satisfaction.TRUE;
	}
	
	/**
	 * Returns a string representation of the Constraint,
	 * this string consists of the substing "alldifferent" to indicate the type of the constrain
	 * and the names of the involved variables
	 * 
	 * @return a string representation of the Constraint
	 */	
	public String toString() {
		StringBuffer b = new StringBuffer();
		boolean first = true;
		b.append(this.name);
		b.append(": alldifferent(");
		for ( Variable v : this.variables ) {
			if ( first ) first = false;
			else b.append(",");
			b.append(v);			
		}
		b.append(")");
		return b.toString();
	}
	
	
	public Collection<Variable> makeArcConsistent() {
		//if (true) return null;
		//System.out.println("Starting AllDifferent makeArcConsistent.");
		
		//long startTime = System.currentTimeMillis();
		
		// ok, to do this, we first have to create our bipartite variable value graph and
		// find a maximum cardinality matching in this graph
		
		HashMap<Object,HashSet<Object>> edges = new HashMap<Object,HashSet<Object>>();
		HashMap<Object,HashSet<Object>> reverseEdges = new HashMap<Object,HashSet<Object>>();
		HashSet<Object> nodes = new HashSet<Object>();
		
		HashSet<Integer> matchedValues = new HashSet<Integer>();
		int valueCount = 0;
		
		// now build the graph
		for (Variable var : variables) {
			
			boolean matchedVar = false;
			
			// add this to the list of nodes
			nodes.add(var);
			
			// construct the adjacency lists for each variable
			edges.put(var,new HashSet<Object>());
			// and the reverese adjacency lists for each variable
			reverseEdges.put(var,new HashSet<Object>());
			
			if (!var.isTiedToValue()) {
				BitSet currentBitSet = var.getDomain().getSet();
				
				for (int i = currentBitSet.nextSetBit(0);i>=0;i=currentBitSet.nextSetBit(i+1)) {
					
					// we eagerly create adjacency and reverse adjacency lists for all possible values
					if (!edges.containsKey(i)) {
						edges.put(i,new HashSet<Object>());
						reverseEdges.put(i,new HashSet<Object>());
						nodes.add(i);
						valueCount++;
					}
	
					// aaand, we create a first 'greedy' matching already
					if (!matchedVar && !matchedValues.contains(i)) {
						edges.get(i).add(var);
						reverseEdges.get(var).add(i);
						matchedVar = true;
						matchedValues.add(i);
					} else {
						edges.get(var).add(i);
						reverseEdges.get(i).add(var);
					}
	
				}
			} else { // the variable is tied, so it will only get one edge
				
				int i = var.getTiedValue();
				
				// we eagerly create adjacency and reverse adjacency lists for all possible values
				if (!edges.containsKey(i)) {
					edges.put(i,new HashSet<Object>());
					reverseEdges.put(i,new HashSet<Object>());
					nodes.add(i);
					valueCount++;
				}

				// aaand, we create a first 'greedy' matching already
				if (!matchedVar && !matchedValues.contains(i)) {
					edges.get(i).add(var);
					reverseEdges.get(var).add(i);
					matchedVar = true;
					matchedValues.add(i);
				} else {
					edges.get(var).add(i);
					reverseEdges.get(i).add(var);
				}
			}
		}
		
		//long time = System.currentTimeMillis() - startTime;
		
		//System.out.println("AllDifferent makeArcConsistent graph setup after " + time + " milliseconds.");
		//System.out.println("Including a first matching with " + matchedValues.size() + " edges");
		//System.out.println("The graph has " + valueCount + " value vertices and " + variables.size() + " variable vertices.");
		
		// now find out, wether this matching is actually already cardinality maximal
		if (valueCount < variables.size()) {
			// we can empty all domains, as this will never lead to a solution
			
			//System.out.println("Fewer values than variables, this constraint thus is unsatisfiable");
			
			for (Variable var : variables) var.getDomain().getSet().clear();
			return variables;
		} else if (matchedValues.size() < variables.size()) {
			// our matching is not yet cardinality maximal, so we need to run hopcroft & karp
			
			// System.out.println("First matching is not cardinality maximal, running Hopcroft & Karp now.");
			
			int augmentations = maxCardinalityMatching(edges, reverseEdges, variables);
			// if it turns out, that this matching does not match all variables, we can clear all domains as this constraint
			// can't be fulfilled then
			if (matchedValues.size() + augmentations < variables.size()) {
				
				//System.out.println("A maximum matching contains fewer edges than variables, this constraint thus is unsatisfiable");
				
				for (Variable var : variables) var.getDomain().getSet().clear();
				return variables;
			}
		} else {
			// our matching is cardinality maximal, so we only need to find alternating paths and circles to throw out
			// edges (e.g. variable value pairs) from now on
			//System.out.println("Found cardinality maximal matching heuristically, will mark unused edges now.");
		}
		
		/*
		// show the matching we found
		for (Object var : variables) {
			System.out.println(var + " matched to " + reverseEdges.get(var));
		}
		*/
		
		// ok, here we know the following: we have a cardinality maximal matching which covers all variables
		// now we try to find out which edges (and thus values) we can throw away from each domain
		
		// first we need to find the strongly connected components
		HashMap<Object, Integer> components = findStronglyConnectedComponents(edges, reverseEdges, nodes);
		HashMap<Object, HashSet<Object>> pathEdges = findEdgesOnAlternatingPaths(edges, reverseEdges, nodes);
		
		/*
		for (Object key : components.keySet()) {
			System.out.println(key + " is in component " + components.get(key));
		}
		*/
		
		// now that we have found all edges which could be part of any maximum cardinality matching, we can
		// remove all other edges (all edges except for: edges between two vertices of the same strongly connected component,
		// edges in the matching and edges in the pathEdges set)
		// the nice thing about this, is that we only have to iterate over edges originating from variables, as all
		// other edges are in the matching.
		int clearedValues = 0;
		for (Variable v : variables) {
			for (Object u : edges.get(v)) {
				// first see wether they are in the same component
				if (components.get(u) != components.get(v)) {
					// and now test wether this edge is on an alternating path
					if (!pathEdges.containsKey(v) || !pathEdges.get(v).contains(u)) {
						// ok, remove this value from the respective domain
						v.getDomain().getSet().clear((Integer)u);
						
						//System.out.println("for " + v + " cleared: " + u);
						
						clearedValues++;
					}
				}
			}
		}
		
		//System.out.println("AllDifferent arc consistency cleared " + clearedValues + " values!");
		
		/*
		System.out.println("");
		
		for (Object key : edges.keySet()) {
			System.out.println(key + " has edge to " + edges.get(key));
		}
		*/
		
		return null;
	}
	
	// this generates the set of edges which are on alternating paths in the maximum cardinality matched graph
	private HashMap<Object, HashSet<Object>> findEdgesOnAlternatingPaths(HashMap<Object,HashSet<Object>> edges, 
			HashMap<Object,HashSet<Object>> reverseEdges,
			HashSet<Object> nodes) {

		HashMap<Object, HashSet<Object>> pathEdges = new HashMap<Object, HashSet<Object>>();
		
		for (Object node : nodes) {
			if (!(node instanceof Variable) && edges.get(node).size() == 0 ) {
				LinkedList<Object> queue = new LinkedList<Object>();
				HashSet<Object> visited = new HashSet<Object>();
				
				queue.add(node);
				
				while (queue.size() > 0) {
					Object currentNode = queue.poll();
					if (!visited.contains(currentNode)) {
						visited.add(currentNode);
						for (Object v : reverseEdges.get(currentNode)) {
							if (!visited.contains(v)) {
								queue.add(v);
								// insert the used edge (but reversed, i.e. in the real orientation)
								if (!pathEdges.containsKey(v)) pathEdges.put(v,new HashSet<Object>());
								pathEdges.get(v).add(currentNode);
							}
						}
					}
				}
			}
		}
		
		return pathEdges; 
	}
	
	// this generates the set of edges which are on alternating paths in the maximum cardinality matched graph
	// this algorithm only produces reasonable output, if the matching inherent in the directed graph
	// covers all nodes in vars.
	// the set returned does not necessarily contain all mathcing edges!
	/*
	private HashMap<Object, HashSet<Object>> findEdgesOnAlternatingPaths(HashMap<Object,HashSet<Object>> edges, 
			HashMap<Object,HashSet<Object>> reverseEdges,
			Collection<Variable> vars) {
		
		HashMap<Object, HashSet<Object>> pathEdges = new HashMap<Object, HashSet<Object>>();
		
		// as we know that all veriable vertices are matched, we can start our paths at the vertices from which
		// the respective matching edge originates (and know that we can find all alternating paths, because
		// without loss of generalization all even alternating paths start with a matching edge)
		LinkedList<Object> path = new LinkedList<Object>();
		LinkedList<Object> lifo = new LinkedList<Object>();
		HashSet<Object> visited = new HashSet<Object>();
		
		for (Object v : vars) {
			visited.clear();
			path.clear();
			lifo.clear();
			
			// as we look at a matching edge first, we know that there can only be one, and because the variable has
			// to be matched, also at least one.
			
			if (reverseEdges.get(v).isEmpty()) System.out.println("This just can't happen!!!");
			
			Object start = reverseEdges.get(v).iterator().next();
			lifo.add(start);
			while (lifo.size() > 0) {
				Object currentNode = lifo.getLast();
				if (visited.contains(currentNode)) {
					
					//System.out.println(path + " | " + lifo);
					
					// we have visited this node before, so we can pop it off the lifo (and the path if they are the same!)
					if (path.getLast() == currentNode) path.removeLast();
					//if (path.removeLast() != currentNode) System.out.println("wrong last entry of path!!!");
					
					lifo.removeLast();
				} else {
					
					// mark it as visited
					visited.add(currentNode);
					
					// add the current node to the current path and its children - if not visited already - to the list
					// of nodes to explore
					path.add(currentNode);
					
					//System.out.println(path);
					
					if (edges.get(currentNode).size() == 0) {
						// this means we arrived at a free node (which also means that it must be a value node), and
						// thus found an alternating path. We will now add all edges of the current path to the set
						// of edges to be returned
						
						System.out.println("Found alternating path: " + path);
						
						Iterator<Object> pathIter = path.iterator();
						Object lastNode = null;
						while (pathIter.hasNext()) {
							if (lastNode == null) lastNode = pathIter.next();
							else {
								Object nextNode = pathIter.next();
								if (!pathEdges.containsKey(lastNode)) {
									pathEdges.put(lastNode, new HashSet<Object>());
								}
								pathEdges.get(lastNode).add(nextNode);
								lastNode = nextNode;
							}
						}
					} else {
						for (Object u : edges.get(currentNode)) {
							if (!visited.contains(u)) lifo.add(u);
						}
					}
				}
			}
		}
		
		return pathEdges;
	}
	*/
	
	// this finds the strongly connected components of the given graph
	// basically this consists of two DFSs, however with some strong variations
	// please see 'Combinatorial Optimization' - Korte, Vygen for the details
	private HashMap<Object, Integer> findStronglyConnectedComponents(HashMap<Object,HashSet<Object>> edges, 
			HashMap<Object,HashSet<Object>> reverseEdges,
			HashSet<Object> nodes) {
		
		// the component mapping to be returned
		HashMap<Object, Integer> components = new HashMap<Object, Integer>();
		
		// the phi mapping and its inverse (see 'Combinatorial Optimization' - Korte, Vygen)
		HashMap<Object, Integer> phi = new HashMap<Object, Integer>();
		HashMap<Integer, Object> phiInv = new HashMap<Integer, Object>();
		int currentPhi = 0;
		
		// the set of visited nodes
		HashSet<Object> visited = new HashSet<Object>();
		
		// the list in which we store the current search path
		// whenever a node is removed from the search path, it will be given a phi value
		LinkedList<Object> lifo = new LinkedList<Object>();
		
		for (Object node : nodes) {
			// see wether we visited that node already, or have to perform another dfs
			if (!visited.contains(node)) {
				// now do a DFS which creates our phi mapping while running
				lifo.add(node);
				while (lifo.size() > 0) {
					Object currentNode = lifo.getLast();
					
					// ok, if the last node in the lifo was not yet marked as visited,
					// we will pile its unvisisted neighbours onto the lifo, and continue exploration
					// however, if it had been marked as visited before, this means that we are now done with
					// exploring that branch and can finally assign it a phi value and remove it from the stack
					if (visited.contains(currentNode)) { // ok, this means we will now assign it a phi value	
						if (!phi.containsKey(currentNode)) {
							phi.put(currentNode, currentPhi);
							phiInv.put(currentPhi, currentNode);
							//System.out.println("assigned a phi of " + currentPhi + " to " + currentNode);
							currentPhi++;		
						}
						lifo.removeLast();
						
						//System.out.println("removing " + currentNode + " from stack, with phi=" + (currentPhi-1));
					
					} else { //  ok, so the node just made it onto the stack, so we first explore this whole branch
						
						//System.out.println("looking at " + currentNode + " and exploring branch:");
						
						visited.add(currentNode);
						
						// now add all previously unvisited nodes adjacent to currentNode to the lifo
						for (Object u : edges.get(currentNode)) {
							if (!visited.contains(u)) {
								lifo.add(u);
								
								//System.out.println("     piling " + u + " onto stack");
							
							}
						}
					}
				} 
				
			}
			
		}
		
		// now that we have our phi mapping, we can start building the anti-arboresences which indicate the components
		int currentComponent = 0;
		visited.clear();
		lifo.clear();
		for (currentPhi--;currentPhi>=0;currentPhi--) {
			Object currentNode = phiInv.get(currentPhi);
			// see wether we had already visited this node (i.e. assigned it a component already)
			if (!visited.contains(currentNode)) {
				// perform a DFS following flipped edges on this node
				lifo.add(currentNode);
				while (lifo.size() > 0) {
					Object v = lifo.removeLast();
					if (!visited.contains(v)) {
						visited.add(v);
						components.put(v,currentComponent);
						for (Object u : reverseEdges.get(v)) {
							if (!visited.contains(u)) {
								lifo.add(u);
							}
						}
					}
				}
				currentComponent++;
			}
			
		}
				
		return components;
	}
	
	/* the hopcroft & karp maximum cardinality matching algorithm */
	// this is the main hopcroft and karp algorithm providing us with a maximum cardinality matching
	// it returns the number of augmentations necessary to maximize the matching.
	private int maxCardinalityMatching(HashMap<Object,HashSet<Object>> edges, 
			HashMap<Object,HashSet<Object>> reverseEdges,
			Collection<Variable> vars) {
		
		int augmentations = 0;
		boolean maximal = false;
		
		while (!maximal) {
			//long startTime = System.currentTimeMillis();
			
			ArrayList<Variable> unmatchedVars = new ArrayList<Variable>();
			for (Variable var : vars) {
				if (reverseEdges.get(var).size() == 0) {
					// this variable node has no incoming edge, so it is not matched...
					unmatchedVars.add(var);
				}
			}
			Tuple2<HashMap<Object,Integer>,Integer> levels = getLevelLabels(edges, reverseEdges, unmatchedVars);
			
			//long time = System.currentTimeMillis() - startTime;
			//System.out.println("After " + time + "ms we know that the minimum length of an augmenting path is " + levels.second);
			if (levels.second == Integer.MAX_VALUE) {
				//System.out.println("i.e. there is no such augmenting path! (which happens iff the matching is cardinality maximal)");
				break;
			}
			//startTime = System.currentTimeMillis();
			
			HashSet<Object> visited = new HashSet<Object>();
			ArrayList<Object[]> paths = new ArrayList<Object[]>();
			for (Variable var : unmatchedVars) {
				Object[] path = getShortestAugmentingPath(edges, reverseEdges, levels.first, visited, levels.second, var);
				if (path != null) paths.add(path);
			}
			
			//time = System.currentTimeMillis() - startTime;
			//System.out.println("After " + time + "ms we found " + paths.size() + " augmenting paths");
			//startTime = System.currentTimeMillis();
			
			// now we need to augment our matching
			for (Object[] path : paths) {
				for (int i=0;i<path.length-1;i++) {
					flipEdge(edges,reverseEdges,path[i],path[i+1]);
				}
				augmentations++;
			}

			//time = System.currentTimeMillis() - startTime;
			//System.out.println("After " + time + "ms we augmented our matching with these paths.");
			
		}
		
		return augmentations;
		
	}
	
	
	/* this is essentially a BFS, to find the minimum length of a path from a free (i.e. unmatched) node in the variable partition
	 to a free node in the value partition
	 the minimum length of an augmenting path is Integer.MAX_VALUE, if the algorithm is applied to a graph which already contains a
	 maximum cardinality matching.
	 */
	private Tuple2<HashMap<Object,Integer>,Integer> getLevelLabels(HashMap<Object,HashSet<Object>> edges, 
			HashMap<Object,HashSet<Object>> reverseEdges,
			Collection<Variable> unmatchedVars) {
		
		// this will be our level function
		HashMap<Object,Integer> levels = new HashMap<Object, Integer>();
		
		// here's the queue for the dfs
		LinkedList<Object> queue = new LinkedList<Object>();
		
		// and this will be the maximum level
		int maxLevel = Integer.MAX_VALUE;
		
		// for now we populate the queue with all unmatched nodes (i.e. no incoming edge) from our variable partition
		for (Variable var : unmatchedVars) {
			queue.add(var);
			levels.put(var,0);
		}
		
		while (queue.size() > 0) {
			// get the node, mark it as visited and add its children while assigning them their correct levels
			Object node = queue.poll();
			// as the node made it into the queue it also has to have a level
			int childLevel = levels.get(node) + 1;
		
			if (childLevel <= maxLevel) { // see wether we should actually add these nodes
				for (Object child : edges.get(node)) {
					// see wether it has been visited before, if not add it, if so, fuggeddaboudid
					if (!levels.containsKey(child)) {
						levels.put(child, childLevel);
						queue.add(child);
						//System.out.println(child + " has level " + childLevel);
						// now find out wether we arrived at a not-matched vertex in the value partition
						// we don't have to care for unmatched nodes in the variable partition as we
						// already had all of them in our root set.
						if (childLevel % 2 != 0 && edges.get(child).size() == 0) {
							// ok, we arrived at a free value node, set the maxLevel accordingly
							maxLevel = childLevel;
						}
					}
				}
			}
		}
		
		return new Tuple2<HashMap<Object,Integer>,Integer>(levels,maxLevel);
	}
	
	
	/* this is essentially a DFS, finding a path using only unvisited nodes (i.e. nodes not in 'visited')
	 from the node unmatchedVar to an unmatched value node. The path must not be longer than maxPathLength
	 edges, and null is returned if no such path was found.
	 please note that the visited flags are changed in the run of this method and should be used in their changed state
	 in subsequent calls to this method while still forming the same maximal set of shortest augmenting paths.
	 we can ensure this to be well defined, as we only scan neighbours of nodes which are in the next level (so
	 a node can never be part of any augmenting path, if it wasn't part of an augmenting path in this run).
	 */
	private Object[] getShortestAugmentingPath(HashMap<Object,HashSet<Object>> edges, 
			HashMap<Object,HashSet<Object>> reverseEdges,
			HashMap<Object,Integer> levels,
			HashSet<Object> visited,
			int maxPathLength,
			Variable unmatchedVar) {
		
		Object[] path = new Object[maxPathLength+1];
		// please note, that we use this list as a lifo!
		LinkedList<Object> lifo = new LinkedList<Object>();
		// int currentLevel = 0;
		lifo.add(unmatchedVar);
		
		while (lifo.size() > 0) {
			
			Object currentNode = lifo.removeLast();
			if (!visited.contains(currentNode)) {
				// mark as visited
				visited.add(currentNode);
				
				int currentLevel = levels.get(currentNode);
				
				path[currentLevel] = currentNode;
				
				// now check wether this node is free/unmatched and the path has the right length
				if (currentLevel == maxPathLength) {
					// if this is true, we must be in the value partition (as all paths of the same length and starting
					// at the same partition end in the same partition, and we know that a path of length maxPathLength
					// from a variable node to a value node exists).
					// thus this node is not matched iff it does not have an outgoing edge
					if (edges.get(currentNode).size() == 0) return path;
				}
				
				// now add its 'children' (in regards to the level tree)
				int nextLevel = currentLevel+1;
				if (nextLevel <= maxPathLength) {
					for (Object u : edges.get(currentNode)) {
						if (levels.get(u) == nextLevel && !visited.contains(u)) {
							// add to lifo
							lifo.add(u);
						}
					}
				}
			}
		}
		
		return null;
	}
	
	
	private void addEdge(HashMap<Object,HashSet<Object>> edges, HashMap<Object,HashSet<Object>> reverseEdges, Object from, Object to) {
		edges.get(from).add(to);
		reverseEdges.get(to).add(from);
	}
	
	
	private void removeEdge(HashMap<Object,HashSet<Object>> edges, HashMap<Object,HashSet<Object>> reverseEdges, Object from, Object to) {
		edges.get(from).remove(to);
		reverseEdges.get(to).remove(from);
	}
	
	
	private void flipEdge(HashMap<Object,HashSet<Object>> edges, HashMap<Object,HashSet<Object>> reverseEdges, Object from, Object to) {
		removeEdge(edges,reverseEdges,from,to);
		addEdge(edges,reverseEdges,to,from);
	}
	
}
