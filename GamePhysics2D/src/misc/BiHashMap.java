package misc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A bi-directional hash map.
 * 
 * A bi-directional map is like a standard map, except either of the two objects in the stored tuple
 * can be used as the "key". Like a standard map, you can look up value with key in constant time,
 * but you can also look up key with value in constant time. Note that this also means values must be unique.
 * 
 * Implemented using two java library HashMaps. Stored data is NOT duplicated.
 * 
 * @author Kelton Finch
 *
 * @param <A> First object type.
 * @param <B> Second object type.
 */
public class BiHashMap<A extends Object, B extends Object> {

	private Map<A,B> ab;
	private Map<B,A> ba;
	
	public BiHashMap(){
		ab = new HashMap<A,B>();
		ba = new HashMap<B,A>();
	}
	
	/**
	 * Returns the number of mappings in this map.
	 * If the map contains more than Integer.MAX_VALUE elements, returns Integer.MAX_VALUE.
	 * @return the number of mappings in this map.
	 */
	public int size(){
		return ab.size();
	}
	
	/**
	 * Returns true iff this map contains no mappings.
	 * @return true iff this map contains no mappings.
	 */
	public boolean isEmpty(){
		return ab.isEmpty();
	}
	
	/**
	 * Returns true if this map contains a mapping for the specified A value.
	 * More formally, returns true iff this map contains a mapping for an A key
	 * such that (key==null ? k==null : key.equals(k)). (There can be at most one such mapping.)
	 * @param key A-value whose presence in the map is being tested
	 * @return true iff the specified key is present in the map
	 */
	public boolean containsA(A key){
		return ab.containsKey(key);
	}
	
	/**
	 * Returns true if this map contains a mapping for the specified B value.
	 * More formally, returns true iff this map contains a mapping for a B key
	 * such that (key==null ? k==null : key.equals(k)). (There can be at most one such mapping.)
	 * @param key B-value whose presence in the map is being tested
	 * @return true iff the specified key is present in the map
	 */
	public boolean containsB(B key){
		return ba.containsKey(key);
	}
	
	/**
	 * Returns the A-value to which the specified B-value is mapped,
	 * or null if this map contains no mapping for the key.
	 * @param key B-value whose associated A-value is to be returned
	 * @return the A-value to which the specified B-value is mapped, or null if there is no such mapping.
	 */
	public A getA(B key){
		return ba.get(key);
	}
	
	/**
	 * Returns the B-value to which the specified A-value is mapped,
	 * or null if this map contains no mapping for the key.
	 * @param key A-value whose associated B-value is to be returned
	 * @return the B-value to which the specified A-value is mapped, or null if there is no such mapping.
	 */
	public B getB(A key){
		return ab.get(key);
	}
	
	/**
	 * Associates the two specified values. If either value was previously mapped, those mappings will be removed.
	 * @param a A-value to be associated.
	 * @param b B-value to be associated.
	 * @return true iff at least one of the values was already mapped to.
	 */
	public boolean put(A a, B b){
		boolean result = false;
		if(containsA(a)){
			result = true;
			removeA(a);
		}
		if(containsB(b)){
			result = true;
			removeB(b);
		}
		ab.put(a, b);
		ba.put(b, a);
		return result;
	}
	
	/**
	 * Removes the mapping for a A-value from this map if it is present,
	 * returning the B-value it was associated, or null if there was no such B-value.
	 * A return value of null does not necessarily indicate that the map contained no mapping for the A-value;
	 * it's also possible that the map explicitly mapped the A-value to a null B-value.
	 * @param key A-value whose mapping to be removed from the map
	 * @return the B-value to which the specified A-value is mapped, or null if there is no such mapping.
	 */
	public B removeA(A key){
		B b = ab.remove(key);
		ba.remove(b);
		return b;
	}
	
	/**
	 * Removes the mapping for a B-value from this map if it is present,
	 * returning the A-value it was associated, or null if there was no such A-value.
	 * A return value of null does not necessarily indicate that the map contained no mapping for the B-value;
	 * it's also possible that the map explicitly mapped the B-value to a null A-value.
	 * @param key B-value whose mapping to be removed from the map
	 * @return the A-value to which the specified B-value is mapped, or null if there is no such mapping.
	 */
	public A removeB(B key){
		A a = ba.remove(key);
		ab.remove(a);
		return a;
	}
	
	/**
	 * Clears all mappings from the map.
	 */
	public void clear(){
		ab.clear();
		ba.clear();
	}
	
	/**
	 * Returns a Set view of the A-values contained in this map.
	 * The set is backed by the map, so changes to the map are reflected in the set, and vice-versa.
	 * If the map is modified while an iteration over the set is in progress
	 * (except through the iterator's own remove operation), the results of the iteration are undefined.
	 * The Set does not support adding or removing operations.
	 * @return a Set view of the A-values contained in this map.
	 */
	public Set<A> aSet(){
		return ab.keySet();
	}
	
	/**
	 * Returns a Set view of the B-values contained in this map.
	 * The set is backed by the map, so changes to the map are reflected in the set, and vice-versa.
	 * If the map is modified while an iteration over the set is in progress
	 * (except through the iterator's own remove operation), the results of the iteration are undefined.
	 * The Set does not support adding or removing operations.
	 * @return a Set view of the B-values contained in this map.
	 */
	public Set<B> bSet(){
		return ba.keySet();
	}
}
