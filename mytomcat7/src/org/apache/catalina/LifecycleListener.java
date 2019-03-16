package org.apache.catalina;
/**
 * 生命周期监听器，会传给Lifecycle
 * @author Administrator
 *
 */
public interface LifecycleListener {

	/**
	 * 当某个事件监听器监听到相关事件发生时，会调用该方法
	 * @param event
	 */
    public void lifecycleEvent(LifecycleEvent event);
}
