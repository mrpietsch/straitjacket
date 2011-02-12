package straitjacket.strategies;

import straitjacket.ConstraintSet;

public class StrategyFactory {
	
	public static enum AvailableStrategies { 
		ARBITRARY("arbitrary",Arbitrary.class),
		FIRST_FAIL("first fail",FirstFail.class),
		MOST_CONSTRAINED("most constrained",MostConstrained.class);
		
		private String name;
		private Class reprClass;
		
		static Class[] parameterTypes = {ConstraintSet.class}; 
		
		AvailableStrategies(String name, Class c) {
			this.name = name;
			this.reprClass = c;
		}
		
		public String toString() {
			return this.name;
		}
		
		public Strategy instanciate(ConstraintSet cs) {
			Object[] params = {cs};
			try {
				return (Strategy)this.reprClass.getConstructor(parameterTypes).newInstance(params);
			} catch ( Exception e ) {
				System.out.println("Exception that cannot occur: " + e);
				return null;
			}
		
		}
	}
}
