package org.apache.catalina.util;

import java.util.HashMap;
import java.util.Map;

//特殊之处在于lock变量,因为不可以修改参数值,所以可以用锁这种方式来限制
//当lock为false时，才能对ParameterMap对象中的名/值对进行添加、更新、删除
public final class ParameterMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = 1L;

	public ParameterMap() {
		super();
	}

	public ParameterMap(int initialCapacity) {
		super(initialCapacity);
	}

	public ParameterMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public ParameterMap(Map<K, V> map) {
		super(map);
	}

	private boolean locked = false;

	public boolean isLocked() {
		return this.locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	private static final StringManager sm = StringManager.getManager("org.apache.catalina.util");

	public void clear() {
		if (locked)
			throw new IllegalStateException(sm.getString("parameterMap.locked"));
		super.clear();
	}

	public V put(K key, V value) {
		if (locked)
			throw new IllegalStateException(sm.getString("parameterMap.locked"));
		return super.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends V> map) {
		if (locked)
			throw new IllegalStateException(sm.getString("parameterMap.locked"));
		super.putAll(map);
	}

	public V remove(Object key) {
		if (locked)
			throw new IllegalStateException(sm.getString("parameterMap.locked"));
		return (super.remove(key));
	}
}
