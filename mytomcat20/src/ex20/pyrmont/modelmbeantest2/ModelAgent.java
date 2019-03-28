package ex20.pyrmont.modelmbeantest2;

import java.io.InputStream;
import java.net.URL;
import javax.management.Attribute;
import javax.management.MalformedObjectNameException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBean;

import org.apache.commons.modeler.ManagedBean;
import org.apache.commons.modeler.Registry;

public class ModelAgent {
	private Registry registry;
	private MBeanServer mBeanServer;

	public ModelAgent() {
		registry = createRegistry();
		try {
			mBeanServer = Registry.getServer();
		} catch (Throwable t) {
			t.printStackTrace(System.out);
			System.exit(1);
		}
	}

	public MBeanServer getMBeanServer() {
		return mBeanServer;
	}

	public Registry createRegistry() {
		Registry registry = null;
		try {
			URL url = ModelAgent.class.getResource("/ex20/pyrmont/modelmbeantest2/car-mbean-descriptor.xml");
			InputStream stream = url.openStream();
			Registry.loadRegistry(stream);
			stream.close();
			registry = Registry.getRegistry();
		} catch (Throwable t) {
			System.out.println(t.toString());
		}
		return (registry);
	}

	public ModelMBean createModelMBean(String mBeanName) throws Exception {
		ManagedBean managed = registry.findManagedBean(mBeanName);
		if (managed == null) {
			System.out.println("ManagedBean null");
			return null;
		}
		ModelMBean mbean = managed.createMBean();
		return mbean;
	}

	private ObjectName createObjectName() {
		ObjectName objectName = null;
		String domain = mBeanServer.getDefaultDomain();
		try {
			objectName = new ObjectName(domain + ":type=MyCar");
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		}
		return objectName;
	}

	public static void main(String[] args) {
		ModelAgent agent = new ModelAgent();//创建代理
		MBeanServer mBeanServer = agent.getMBeanServer();//创建服务器
		Car car = new Car();
		System.out.println("Creating ObjectName");
		ObjectName objectName = agent.createObjectName();//创建唯一标识
		try {
			ModelMBean modelMBean = agent.createModelMBean("myMBean");//创建模型MBean
			modelMBean.setManagedResource(car, "ObjectReference");//设置托管资源
			mBeanServer.registerMBean(modelMBean, objectName);//注册MBean
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		//使用
		try {
			Attribute attribute = new Attribute("Color", "green");
			mBeanServer.setAttribute(objectName, attribute);
			String color = (String) mBeanServer.getAttribute(objectName, "Color");
			System.out.println("Color:" + color);

			mBeanServer.invoke(objectName, "drive", null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}