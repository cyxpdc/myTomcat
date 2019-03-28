package org.apache.catalina.util;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

/**
 * 生命周期支持，帮助组件管理监听器，并触发相应的生命周期事件,组合进容器类如SimpleContext
 * 其中的add、find、remove是Lifecycle接口的三个方法,与之同名，达到管理的目的
 * @author Administrator
 *
 */
public final class LifecycleSupport {

	public LifecycleSupport(Lifecycle lifecycle) {
		this.lifecycle = lifecycle;
	}
	// ----------------------------------------------------- Instance Variables
	/**
	 * The source component for lifecycle events that we will fire.
	 */
	private Lifecycle lifecycle = null;
	/**
	 * 存储所有生命周期监听器
	 */
	private LifecycleListener listeners[] = new LifecycleListener[0];
	// --------------------------------------------------------- Public Methods
	/**
	 * 添加事件监听器
	 * @param listener
	 */
	public void addLifecycleListener(LifecycleListener listener) {
		synchronized (listeners) {
			LifecycleListener results[] = new LifecycleListener[listeners.length + 1];
			for (int i = 0; i < listeners.length; i++)
				results[i] = listeners[i];
			results[listeners.length] = listener;
			listeners = results;
		}
	}
	/**
	 * 返回所有事件监听器
	 * @return
	 */
	public LifecycleListener[] findLifecycleListeners() {
		return listeners;
	}
	/**
	 * 触发一个生命周期事件,执行所有监听器
	 * @param type
	 * @param data
	 */
	public void fireLifecycleEvent(String type, Object data) {
		LifecycleEvent event = new LifecycleEvent(lifecycle, type, data);
		LifecycleListener interested[] = null;
		synchronized (listeners) {
			interested = (LifecycleListener[]) listeners.clone();
		}
		for (int i = 0; i < interested.length; i++)
			interested[i].lifecycleEvent(event);

	}
	/**
	 * 移除事件监听器
	 * @param listener
	 */
	public void removeLifecycleListener(LifecycleListener listener) {
		synchronized (listeners) {
			int n = -1;
			for (int i = 0; i < listeners.length; i++) {
				if (listeners[i] == listener) {
					n = i;
					break;
				}
			}
			if (n < 0)//没找到这个监听器时，直接返回
				return;
			LifecycleListener results[] = new LifecycleListener[listeners.length - 1];
			int j = 0;
			for (int i = 0; i < listeners.length; i++) {
				if (i != n)
					results[j++] = listeners[i];
			}
			listeners = results;
		}
	}
}
