package straitjacket.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Extends the ArrayList and add the feature that no multiple entries are allowed.
 *
 * @param <T>
 */
public class OrderedHashSet<T> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;

	//TODO wir wollen alles doppelt speichern ! sicher?
	/**
	 * We save all enties of the list in an HashSet
	 */
    private final HashSet<T> elementHash;
	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	public OrderedHashSet () {
		super();
		elementHash = new HashSet<T>();
	}
	/**
	 * Constructs a list containing the elements of the specified collection, 
	 * in the order they are returned by the collection's iterator. 
	 * The ArrayList instance has an initial capacity of 110% the size of the specified collection.
	 * @param c the collection whose elements are to be placed into this list.
	 * @throws NullPointerException - if the specified collection is null.
	 */
	public OrderedHashSet (Collection<? extends T> c) {
		super(c);
		elementHash = new HashSet<T>(c);
	}
	
	/**
	 * Constructs an empty list with the specified initial capacity.
	 * @param initialCapacity - the initial capacity of the list.
	 * @throws IllegalArgumentException - if the specified initial capacity is negative
	 */
	public OrderedHashSet (int initialCapacity) {
		super(initialCapacity);
		elementHash = new HashSet<T>(initialCapacity);
	}
	
	/**
	 * Inserts the specified element at the specified position in this list.
	 * Shifts the element currently at that position (if any) and any subsequent 
	 * elements to the right (adds one to their indices). 
	 * If the element is already in the list nothing will be done.
	 * @param index index at which the specified element is to be inserted.
	 * @param element element to be inserted.
	 * @throws IndexOutOfBoundsException - if index is out of range (index < 0 || index > size()).
	 */
	@Override
	public void add(int index, T element) {
		if (elementHash.contains(element)) return;
		elementHash.add(element);
		super.add(index, element);
	}

	/**
	 * Appends the specified element to the end of this list. 
	 * If the element is already in the list nothing will be done.  
	 * @param o element to be appended to this list.
	 * @return true (as per the general contract of Collection.add).
	 */
	@Override
	public boolean add(T o) {
		if (elementHash.contains(o)) return false;
		elementHash.add(o);
		return super.add(o);
	}
	/**
	 * Replaces the element at the specified position in this list with the specified element.
	 * If the element is already in the list nothing will be done.
	 * 
	 * @param index index of element to replace.
	 * @param element element to be inserted.
	 * @return the element previously at the specified position.
	 * @throws IndexOutOfBoundsException - if index out of range (index < 0 || index >= size()).
	 */
	@Override
	public T set(int index, T element) {
		if (!elementHash.contains(element)) {
			// remove the old index element from the Hashtable and add the new one
			elementHash.remove(this.get(index));
			elementHash.add(element);
			return super.set(index, element);
		} else return null;
	}
	
	/**
	 * Removes the element at the specified position in this list.
	 * Shifts any subsequent elements to the left (subtracts one from their indices).
	 * @param index the index of the element to removed.
	 * @return the element that was removed from the list.
	 * @throws IndexOutOfBoundsException - if index out of range (index < 0 || index >= size()).
	 */
	@Override
	public T remove(int index) {
		elementHash.remove(this.get(index));
		return super.remove(index);
	}

	/**
	 * Removes the specified element from the list
	 * @param o to be removed from this list, if present.
	 * @return true if the collection contained the specified element.
	 */
	@Override
	public boolean remove(Object o) {
		elementHash.remove(o);
		return super.remove(o);
	}

	/**
	 * Returns true if this list contains the specified element.
	 * @param elem element whose presence in this List is to be tested.
	 * @return true if the specified element is present; false otherwise.
	 */
	@Override
	public boolean contains(Object elem) {
		return elementHash.contains(elem);
	}
	/**
	 * Appends all of the elements in the specified Collection to the end of this list, 
	 * in the order that they are returned by the specified Collection's Iterator. 
	 * The behavior of this operation is undefined if the specified Collection is modified 
	 * while the operation is in progress. (This implies that the behavior of this call is 
	 * undefined if the specified Collection is this list, and this list is nonempty.)
	 * If an element of c is already in the list, false is return and nothing happens.
	 * 
	 * @param c the elements to be inserted into this list.
	 * @return true if this list changed as a result of the call.
	 * @throws NullPointerException - if the specified collection is null.
	 */
	@Override
	public boolean addAll(Collection<? extends T> c) {
		for (T t : c) if (elementHash.contains(t)) return false;
		elementHash.addAll(c);
		return super.addAll(c);
	}

	/**
	 * Inserts all of the elements in the specified Collection into this list, 
	 * starting at the specified position. Shifts the element currently at that position 
	 * (if any) and any subsequent elements to the right (increases their indices). 
	 * The new elements will appear in the list in the order that they are returned by
	 * the specified Collection's iterator.
	 * If an element of c is already in the list, false is return and nothing happens.
	 * @param index index at which to insert first element from the specified collection.
	 * @param c elements to be inserted into this list.
	 * @return true if this list changed as a result of the call.
	 * @throws IndexOutOfBoundsException - if index out of range (index < 0 || index > size()).
	 * @throws NullPointerException - if the specified Collection is null.
	 */
	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		for (T t : c) if (elementHash.contains(t)) return false;
		elementHash.addAll(c);
		return super.addAll(index, c);
	}
	/**
	 * Removes from this List all of the elements whose index is between fromIndex, 
	 * inclusive and toIndex, exclusive. Shifts any succeeding elements to the left
	 *  (reduces their index). This call shortens the list by (toIndex - fromIndex) 
	 *  elements. (If toIndex==fromIndex, this operation has no effect.)
	 *  @param fromIndex index of first element to be removed.
	 *  @param toIndex index after last element to be removed.
	 */
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		for (int i=fromIndex;i<toIndex;i++) elementHash.remove(this.get(i));
		super.removeRange(fromIndex, toIndex);
	}

	/**
	 * Returns true if this list contains all of the elements of the specified collection.
	 *  @param c collection to be checked for containment in this set.
	 *  @return true if this set contains all of the elements of the specified collection.
	 *  @throws ClassCastException - if the types of one or more elements in the specified collection are incompatible with this set (optional).
	 *  @throws NullPointerException - if the specified collection contains one or more null elements and this set does not support null elements (optional).
	 *  @throws NullPointerException - if the specified collection is null.
	 * 
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return elementHash.containsAll(c);
	}

	/**
	 * Removes from this list all of elements that are contained in the specified collection.
	 * @param c the elements to be removed
	 * @return true if this collection changed as a result of the call.
	 * @throws NullPointerException - if the specified collection is null.
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		elementHash.removeAll(c);
		return super.removeAll(c);
	}

	/**
	 * Retains only the elements in this collection that are contained in the specified collection
	 * (optional operation). In other words, removes from this collection all of its elements that 
	 * are not contained in the specified collection.
	 * @param c elements to be retained in this collection.
	 * @return true if this collection changed as a result of the call.
	 * @throws NullPointerException - if the specified collection is null.
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		elementHash.retainAll(c);
		return super.retainAll(c);
	}

}
