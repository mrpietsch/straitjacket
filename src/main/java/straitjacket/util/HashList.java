package straitjacket.util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class HashList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;
	protected HashSet<T> elementHash = new HashSet<T>();
	
	@Override
	public void add(int index, T element) {
		if (elementHash.contains(element)) return;
		elementHash.add(element);
		super.add(index, element);
	}

	@Override
	public boolean add(T o) {
		if (elementHash.contains(o)) return false;
		elementHash.add(o);
		return super.add(o);
	}

	@Override
	public T set(int index, T element) {
		if (!elementHash.contains(element)) {
			// remove the old index element from the Hashtable and add the new one
			elementHash.remove(this.get(index));
			elementHash.add(element);
			return super.set(index, element);
		} else return null;
	}

	@Override
	public T remove(int index) {
		elementHash.remove(this.get(index));
		return super.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		elementHash.remove(o);
		return super.remove(o);
	}

	@Override
	public boolean contains(Object elem) {
		return elementHash.contains(elem);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		for (T t : c) if (elementHash.contains(t)) return false;
		elementHash.addAll(c);
		return super.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		for (T t : c) if (elementHash.contains(t)) return false;
		elementHash.addAll(c);
		return super.addAll(index, c);
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		for (int i=fromIndex;i<toIndex;i++) elementHash.remove(this.get(i));
		super.removeRange(fromIndex, toIndex);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return elementHash.containsAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		elementHash.removeAll(c);
		return super.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		elementHash.retainAll(c);
		return super.retainAll(c);
	}
	
	
	
}
