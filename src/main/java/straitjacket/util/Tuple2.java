package straitjacket.util;


/**
 * Generic Tuple class, for returning 2-tuples objects.
 * @author Jakob Uszkoreit
 *
 * @param <T1> the type of the first object
 * @param <T2> the type of the second object
 */
public class Tuple2<T1,T2> {
	public final T1 first;
	public final T2 second;
	
	public Tuple2(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}
}
