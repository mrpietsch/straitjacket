package straitjacket;
import java.util.ArrayList;
import java.util.Arrays;

import straitjacket.constraints.AllDifferentConstraint;
import straitjacket.constraints.EqualsConstraint;
import straitjacket.constraints.NeqConstraint;


public class CSPGenerator {
	
	// static enum for the factory
	public static enum CSPs {
		SMM("SMM") { public ConstraintSet getCS() { return sendMoreMoney(); } },
		SMM2("SMM (carries)") { public ConstraintSet getCS() { return sendMoreMoneyDC(); } },
		QUEENS8("Queens 8x8") { public ConstraintSet getCS() { return nQueens(8,false); } },
		QUEENS10("Superqueens 10x10") { public ConstraintSet getCS() { return nQueens(10,true); } },
		SUDOKU3EMPTY("Sudoku 3x3 (empty)") { public ConstraintSet getCS() { return sudokuEmpty(3,false); } },
		//SUDOKU4EMPTY("Sudoku 4x4 (empty)") { public ConstraintSet getCS() { return sudokuEmpty(4,false); } },
		SUDOKU3("Sudoku 3x3 (example)") { public ConstraintSet getCS() { return sudokuExample3x3(); } },
		SUDOKU4("Sudoku 4x4 (example)") { public ConstraintSet getCS() { return sudokuExample4x4(); } };
		
		private final String name;
		
		CSPs(String name) {
			this.name = name;
		}
		
		public abstract ConstraintSet getCS();
		
		public String toString() {
			return this.name;
		}
	}
	
	/**
	 * Generates a constraint set representing the SEND MORE MONEY problem.
	 * SEND + MORE = MONEY
	 * In contrast to politics leading zeros are not allowed.
	 */
	private static ConstraintSet sendMoreMoney() {
		
		ConstraintSet cs = new ConstraintSet();
		
		try {
			
			Variable s = cs.addVariable("s",1,9);
			Variable e = cs.addVariable("e",0,9);
			Variable n = cs.addVariable("n",0,9);
			Variable d = cs.addVariable("d",0,9);
			Variable m = cs.addVariable("m",1,9);
			Variable o = cs.addVariable("o",0,9);
			Variable r = cs.addVariable("r",0,9);
			Variable y = cs.addVariable("y",0,9);
			
			ArrayList<Variable> c1Vars = new ArrayList<Variable>();
			ArrayList<Integer> c1Coeffs = new ArrayList<Integer>();
			
			c1Vars.add(0,s);
			c1Coeffs.add(0,1000);
			c1Vars.add(1,e);
			c1Coeffs.add(1,91);
			c1Vars.add(2,n);
			c1Coeffs.add(2,-90);
			c1Vars.add(3,d);
			c1Coeffs.add(3,1);
			
			c1Vars.add(4,m);
			c1Coeffs.add(4,-9000);
			c1Vars.add(5,o);
			c1Coeffs.add(5,-900);
			c1Vars.add(6,r);
			c1Coeffs.add(6,10);
			
			c1Vars.add(7,y);
			c1Coeffs.add(7,-1);
			
			cs.add( new EqualsConstraint("SMM",c1Vars,c1Coeffs,0) );
			cs.add( new AllDifferentConstraint(s,e,n,d,m,o,r,y) );
									
		} catch (VariableNameExistsException e) {
			System.out.println("A Variable with that name already exists!");
		}
		
		return cs;
	}
	
	/**
	 * Generates a constraint set representing the SEND MORE MONEY problem.
	 * SEND + MORE = MONEY
	 * In contrast to politics leading zeros are not allowed.
	 */
	private static ConstraintSet sendMoreMoneyDC() {
		
		ConstraintSet cs = new ConstraintSet();
		
		try {
			
			// variables
			Variable s = cs.addVariable("s",1,9);
			Variable e = cs.addVariable("e",0,9);
			Variable n = cs.addVariable("n",0,9);
			Variable d = cs.addVariable("d",0,9);
			Variable m = cs.addVariable("m",1,9);
			Variable o = cs.addVariable("o",0,9);
			Variable r = cs.addVariable("r",0,9);
			Variable y = cs.addVariable("y",0,9);
			
			// carries
			Variable c1 = cs.addVariable("c1",0,1);
			Variable c2 = cs.addVariable("c2",0,1);
			Variable c3 = cs.addVariable("c3",0,1);
			Variable c4 = cs.addVariable("c4",0,1);
			
			Variable[][] cv = {
					{m,c4},
					{o,c4,m,s,c3},
					{n,c3,o,e,c2},
					{e,c2,r,n,c1},
					{y,c1,e,d}
			};
			Integer[][] cc = {
					{1,-1},
					{1,10,-1,-1,-1},
					{1,10,-1,-1,-1},
					{1,10,-1,-1,-1},
					{1,10,-1,-1}					
			};
			
			for ( int x=0; x<cv.length; x++ ) {
				ArrayList<Variable> cVars = new ArrayList<Variable>();
				ArrayList<Integer> cCoeffs = new ArrayList<Integer>();

                cVars.addAll(Arrays.asList(cv[x]));
                cCoeffs.addAll(Arrays.asList(cc[x]));
				
				cs.add( new EqualsConstraint("SMM_"+x,cVars,cCoeffs,0 ) );	
			}
			
			cs.add( new AllDifferentConstraint(s,e,n,d,m,o,r,y) );
									
		} catch (VariableNameExistsException e) {
			System.out.println("A Variable with that name already exists!");
		}
		
		return cs;
	}
	
	/**
	 * Generates a set representing the n-queens problem.
	 * @param numberOfQueens number of queens
	 * @param isAmazon if set to true, the queen may hit like a knight (aka amazon, super queen, maharadscha). Won't find solutions with <code>numberOfQueens</code> < 10 
	 */
	private static ConstraintSet nQueens(int numberOfQueens, boolean isAmazon) {
		ConstraintSet cs = new ConstraintSet();
		
		try {
			
			Variable[] queens = new Variable[numberOfQueens];
			
			for (int i = 0; i<numberOfQueens; i++) {
				queens[i] = cs.addVariable("q"+(i),0,numberOfQueens-1);
			}
			
			for ( int i = 0; i<numberOfQueens; i++ ) {
				for ( int j=i+1; j<numberOfQueens; j++ ) {
					ArrayList<Variable> cVars = new ArrayList<Variable>();
					ArrayList<Integer> cCoeffs = new ArrayList<Integer>();
					
					cVars.add(0,queens[i]);
					cCoeffs.add(0,-1);
					cVars.add(1,queens[j]);
					cCoeffs.add(1,1);
					
					cs.add( new NeqConstraint("q_"+i+"_"+j+"_diagup", cVars,cCoeffs,j-i) );
					cs.add( new NeqConstraint("q_"+i+"_"+j+"_diagdown", cVars,cCoeffs,i-j) );
				}
				
				if ( isAmazon ) {
					int j = i + 1;    // next column
					int k = i + 2;    // next but one
					
					if ( j < numberOfQueens ) {
						ArrayList<Variable> cVars = new ArrayList<Variable>();
						ArrayList<Integer> cCoeffs = new ArrayList<Integer>();
						
						cVars.add(0,queens[i]);
						cCoeffs.add(0,-1);
						cVars.add(1,queens[j]);
						cCoeffs.add(1,1);
						cs.add( new NeqConstraint("q_"+i+"_"+j+"_knightUp", cVars,cCoeffs,2) );
						cs.add( new NeqConstraint("q_"+i+"_"+j+"_knighDown", cVars,cCoeffs,-2) );
					}
					
					if ( k < numberOfQueens ) {
						ArrayList<Variable> cVars = new ArrayList<Variable>();
						ArrayList<Integer> cCoeffs = new ArrayList<Integer>();
						
						cVars.add(0,queens[i]);
						cCoeffs.add(0,-1);
						cVars.add(1,queens[k]);
						cCoeffs.add(1,1);
						cs.add( new NeqConstraint("q_"+i+"_"+k+"_knightUp", cVars,cCoeffs,1) );
						cs.add( new NeqConstraint("q_"+i+"_"+k+"_knighDown", cVars,cCoeffs,-1) );
					}
				}
			}
			cs.add( new AllDifferentConstraint(queens) );
			
		} catch (VariableNameExistsException e) {
			System.out.println("A Variable with that name already exists!");
		}
		
		return cs;
	}
	
	/**
	 * Generates a constraint set representing an empty sudoku field. The field
	 * will be empty i.e. there are no fixed cells.
	 * @param blockSize height and width of one of the blocks, e.g. blockSize=3 would generate a 9x9 field
	 * @param constrainDiags set true if you want alldifferent constraints over the diagonals, too
	 * @return the constraint set containing all neccessary alldifferent constraints
	 * @see #sudokuExample3x3()
	 */
	private static ConstraintSet sudokuEmpty(int blockSize, boolean constrainDiags) {
		ConstraintSet cs = new ConstraintSet();
		
		int fieldSize = blockSize*blockSize;
		
		Variable[][] cells = new Variable[fieldSize][fieldSize];
		
		// initialize the variables with their domains
		try {					
			for (int i = 0; i<fieldSize; i++) {
				for (int j = 0; j<fieldSize; j++){
					cells[i][j] = cs.addVariable("s["+i+"]["+j+"]",1,fieldSize);
				}
			}
		} catch (VariableNameExistsException e) {
			System.out.println("A Variable with that name already exists!");
		}
		
		// set up the alldifferent constraints for the rows and columns
		for (int x = 0; x<fieldSize; x++) {
			// alldifferent for row
			cs.add( new AllDifferentConstraint(cells[x]) );
			
			// alldifferent for column
			Variable[] col = new Variable[fieldSize];
			for ( int i=0; i<fieldSize; i++ ) {
				col[i] = cells[i][x];
			}	
			cs.add( new AllDifferentConstraint(col) );
			
		}
		
		// set up the alldifferent constraints for the blocks
		for ( int a = 0; a<blockSize; a++ ) {
			for ( int b = 0; b<blockSize; b++ ) {
				Variable[] block = new Variable[fieldSize];
				for ( int i = 0; i<blockSize; i++ )
                    System.arraycopy(cells[a * blockSize + i], b * blockSize, block, i * blockSize, blockSize);
				cs.add( new AllDifferentConstraint(block) );
			}
		}
		
		// constrain the diagonals, too if wanted
		if (constrainDiags) {
			Variable[] diag1 = new Variable[fieldSize];
			Variable[] diag2 = new Variable[fieldSize];
			
			for ( int d=0; d<fieldSize; d++) {
				diag1[d] = cells[d][d];
				diag2[d] = cells[d][fieldSize-d-1];
			}

			cs.add( new AllDifferentConstraint(diag1) );
			cs.add( new AllDifferentConstraint(diag2) );
		}
		
		return cs;
	}
	
	/**
	 * Generates a constraint set representing a distinct 4x4 sudoku problem.
	 * 30 cells are already fixed that is that their domains have cardinality 1.
	 * @return the constraints set containing all neccessary alldifferent constraints
	 * @see #sudokuEmpty(int, boolean)
	 */
	private static ConstraintSet sudokuExample4x4() {
		ConstraintSet cs = new ConstraintSet();
		
		int n = 4;
		int size = n*n;
		
		Variable[][] fields = new Variable[size][size];

		// set fixed values
		int fixed[][] = {
				{0,14,0,0,9,0,0,5,11,0,0,8,0,4,6,0},
				{0,5,16,11,0,0,0,0,0,0,6,0,13,15,14,0},
				{15,0,4,0,0,12,6,0,0,9,10,0,0,7,0,5},
				{0,0,10,0,1,14,7,0,0,4,5,13,0,9,0,0},
				{0,0,0,12,0,6,13,0,0,5,11,0,2,0,0,0},
				{8,9,0,0,11,3,16,0,0,10,15,2,0,0,12,14},
				{13,0,0,2,8,0,1,10,3,0,0,12,9,0,0,7},
				{0,1,11,10,0,0,0,9,7,0,0,0,0,3,4,0},
				{0,4,8,15,0,0,0,6,2,0,0,0,5,12,10,0},
				{2,0,0,5,7,0,4,16,0,0,0,6,1,0,0,3},
				{12,10,0,0,5,11,8,0,0,3,16,1,0,0,15,0},
				{0,0,0,7,0,10,2,0,0,8,14,0,4,0,0,0},
				{0,0,15,0,14,7,11,0,0,13,9,10,0,2,0,0},
				{4,0,12,0,0,13,10,0,0,16,2,0,0,5,0,15},
				{0,6,1,3,0,2,0,0,0,0,7,0,14,16,13,0},
				{0,0,13,0,0,0,0,15,14,0,0,4,0,10,7,0}
		};
		
		// initialize the variables with their domains
		try {					
			for (int i = 0; i<size; i++) {
				for (int j = 0; j<size; j++){
					if ( fixed[i][j] != 0 ) {
						fields[i][j] = cs.addVariable("s["+i+"]["+j+"]",fixed[i][j],fixed[i][j]);
					} else {
						fields[i][j] = cs.addVariable("s["+i+"]["+j+"]",1,size);
					}
				}
			}
		} catch (VariableNameExistsException e) {
			System.out.println("A Variable with that name already exists!");
		}
		
		// set up the alldifferent constraints for the rows and columns
		for (int x = 0; x<size; x++) {
			// alldifferent for row
			cs.add( new AllDifferentConstraint(fields[x]) );
			// alldifferent for column
			Variable[] col = new Variable[size];
			for ( int i=0; i<size; i++ ) {
				col[i] = fields[i][x];
			}
			cs.add( new AllDifferentConstraint(col) );
		}
		
		// set up the alldifferent constraints for the blocks
		for ( int a = 0; a<n; a++ ) {
			for ( int b = 0; b<n; b++ ) {
				Variable[] block = new Variable[size];
				for ( int i = 0; i<n; i++ )
                    System.arraycopy(fields[a * n + i], b * n, block, i * n, n);
				cs.add( new AllDifferentConstraint(block) );
			}
		}
		
		return cs;
	}

	/**
	 * Generates a constraint set representing a distinct 3x3 sudoku problem.
	 * 30 cells are already fixed that is that their domains have cardinality 1.
	 * @return the constraints set containing all neccessary alldifferent constraints
	 * @see #sudokuEmpty(int, boolean)
	 */
	private static ConstraintSet sudokuExample3x3() {
		ConstraintSet cs = new ConstraintSet();
		
		int n = 3;
		int size = n*n;
		
		Variable[][] fields = new Variable[size][size];
		
		// set fixed values
		int fixed[][] = {
				{5,3,0,0,7,0,0,0,0},
				{6,0,0,1,9,5,0,0,0},
				{0,9,8,0,0,0,0,6,0},
				{8,0,0,0,6,0,0,0,3},
				{4,0,0,8,0,3,0,0,1},
				{7,0,0,0,2,0,0,0,6},
				{0,6,0,0,0,0,2,8,0},
				{0,0,0,4,1,9,0,0,5},
				{0,0,0,0,8,0,0,7,9}
		};
		
		// initialize the variables with their domains
		try {					
			for (int i = 0; i<size; i++) {
				for (int j = 0; j<size; j++){
					if ( fixed[i][j] != 0 ) {
						fields[i][j] = cs.addVariable("s["+i+"]["+j+"]",fixed[i][j],fixed[i][j]);
					} else {
						fields[i][j] = cs.addVariable("s["+i+"]["+j+"]",1,size);
					}
				}
			}
		} catch (VariableNameExistsException e) {
			System.out.println("A Variable with that name already exists!");
		}
		
		// set up the alldifferent constraints for the rows and columns
		for (int x = 0; x<size; x++) {
			// alldifferent for row
			cs.add( new AllDifferentConstraint(fields[x]) );
			// alldifferent for column
			Variable[] col = new Variable[size];
			for ( int i=0; i<size; i++ ) {
				col[i] = fields[i][x];
			}
			cs.add( new AllDifferentConstraint(col) );
		}
		
		// set up the alldifferent constraints for the blocks
		for ( int a = 0; a<n; a++ ) {
			for ( int b = 0; b<n; b++ ) {
				Variable[] block = new Variable[size];
				for ( int i = 0; i<n; i++ )
                    System.arraycopy(fields[a * n + i], b * n, block, i * n, n);
				cs.add( new AllDifferentConstraint(block) );
			}
		}
		
		return cs;
	}
	
	/**
	 * Solves the AlldifferentProblem presented in the course 
	 */
	public static ConstraintSet courseAllDifferent() {
		
		ConstraintSet cs = new ConstraintSet();
		
		try {
			
			Variable[] vars = new Variable[6];
			vars[0] = cs.addVariable("x1",1,2);
			vars[1] = cs.addVariable("x2",2,3);
			int[] a1=new int[2]; a1[0]=1;	a1[1]=3;
			vars[2] = cs.addVariable("x3", a1 );
			int[] a2=new int[2]; a2[0]=2;	a2[1]=4;
			vars[3] = cs.addVariable("x4", a2 );
			vars[4] = cs.addVariable("x5",3,6);
			vars[5] = cs.addVariable("x6",6,7);
			
						
			cs.add( new AllDifferentConstraint(vars) );
			
		} catch (VariableNameExistsException e) {
			System.out.println("A Variable with that name already exists!");
		}
		
		return cs;
	}
}
