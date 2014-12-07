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
	
	public int size(){
		return ab.size();
	}
	
	public boolean isEmpty(){
		return ab.isEmpty();
	}
	
	public boolean containsA(A key){
		return ab.containsKey(key);
	}
	
	public boolean containsB(B key){
		return ba.containsKey(key);
	}
	
	public A getA(B key){
		return ba.get(key);
	}
	
	public B getB(A key){
		return ab.get(key);
	}
	
	public void put(A a, B b){
		ab.put(a, b);
		ba.put(b, a);
	}
	
	public B removeA(A key){
		B b = ab.remove(key);
		ba.remove(b);
		return b;
	}
	
	public A removeB(B key){
		A a = ba.remove(key);
		ab.remove(a);
		return a;
	}
	
	public void clear(){
		ab.clear();
		ba.clear();
	}
	
	public Set<A> aSet(){
		return ab.keySet();
	}
	
	public Set<B> bSet(){
		return ba.keySet();
	}
}
