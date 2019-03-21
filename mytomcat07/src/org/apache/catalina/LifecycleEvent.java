package org.apache.catalina;

import java.util.EventObject;

/**
 * 生命周期事件的实例，会传给LifecycleListener
 * @author Administrator
 *
 */
public final class LifecycleEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	// --------------------------------------------------------- 构造方法
	public LifecycleEvent(Lifecycle lifecycle, String type) {
		this(lifecycle, type, null);
	}
	public LifecycleEvent(Lifecycle lifecycle, String type, Object data) {
		super(lifecycle);
		this.lifecycle = lifecycle;
		this.type = type;
		this.data = data;
	}
	
	/**
	 * 事件数据
	 */
	private Object data = null;
	/**
	 * The Lifecycle on which this event occurred.
	 */
	private Lifecycle lifecycle = null;
	/**
	 * 实例类型
	 */
	private String type = null;
	
	/**
	 * get方法
	 * @return
	 */
	public Object getData() {
		return this.data;
	}

	public Lifecycle getLifecycle() {
		return this.lifecycle;
	}

	public String getType() {
		return this.type;
	}
}
