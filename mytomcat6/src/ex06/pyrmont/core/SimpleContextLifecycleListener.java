package ex06.pyrmont.core;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

public class SimpleContextLifecycleListener implements LifecycleListener {

	public void lifecycleEvent(LifecycleEvent event) {
		System.out.println("Lifecycle组件:" + event.getLifecycle());
		System.out.println("SimpleContextLifecycleListener's event " 
							+ event.getType().toString());
		if (Lifecycle.START_EVENT.equals(event.getType())) {
			System.out.println("Starting context.");
		} else if (Lifecycle.STOP_EVENT.equals(event.getType())) {
			System.out.println("Stopping context.");
		}
	}
}