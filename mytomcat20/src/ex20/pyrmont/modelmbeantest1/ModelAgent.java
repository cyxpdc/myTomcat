package ex20.pyrmont.modelmbeantest1;

import javax.management.Attribute;
import javax.management.Descriptor;
import javax.management.MalformedObjectNameException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.ModelMBean;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import javax.management.modelmbean.RequiredModelMBean;

public class ModelAgent {

	private String MANAGED_CLASS_NAME = "ex20.pyrmont.modelmbeantest1.Car";
	private MBeanServer mBeanServer = null;

	public ModelAgent() {
		mBeanServer = MBeanServerFactory.createMBeanServer();
	}

	public MBeanServer getMBeanServer() {
		return mBeanServer;
	}

	private ObjectName createObjectName(String name) {
		ObjectName objectName = null;
		try {
			objectName = new ObjectName(name);
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		}
		return objectName;
	}

	private ModelMBean createMBean(ObjectName objectName, String mbeanName) {
		ModelMBeanInfo mBeanInfo = createModelMBeanInfo(objectName, mbeanName);
		RequiredModelMBean modelMBean = null;
		try {
			modelMBean = new RequiredModelMBean(mBeanInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelMBean;
	}
	//列出所有要暴露的属性和方法并将他们传给ModelMBeanInfo对象
	private ModelMBeanInfo createModelMBeanInfo(ObjectName inMbeanObjectName, String inMbeanName) {
		ModelMBeanInfo mBeanInfo = null;
		ModelMBeanAttributeInfo[] attributes = new ModelMBeanAttributeInfo[1];
		ModelMBeanOperationInfo[] operations = new ModelMBeanOperationInfo[3];
		try {
			attributes[0] = new ModelMBeanAttributeInfo
					("Color", "java.lang.String", "the color.", true, true, false,null);
			operations[0] = new ModelMBeanOperationInfo
					("drive", "the drive method", null, "void",
					MBeanOperationInfo.ACTION, null);
			operations[1] = new ModelMBeanOperationInfo
					("getColor", "get color attribute", null, "java.lang.String",
					MBeanOperationInfo.ACTION, null);

			Descriptor setColorDesc = new DescriptorSupport(new String[] 
					{ "name=setColor", "descriptorType=operation",
					"class=" + MANAGED_CLASS_NAME, "role=operation" });
			MBeanParameterInfo[] setColorParams = new MBeanParameterInfo[] {
					(new MBeanParameterInfo("new color", "java.lang.String", "new Color value")) };
			operations[2] = new ModelMBeanOperationInfo("setColor", "set Color attribute", setColorParams, "void",
					MBeanOperationInfo.ACTION, setColorDesc);

			mBeanInfo = new ModelMBeanInfoSupport(MANAGED_CLASS_NAME, null, attributes, null, operations, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mBeanInfo;
	}

	public static void main(String[] args) {
		ModelAgent agent = new ModelAgent();//创建代理
		MBeanServer mBeanServer = agent.getMBeanServer();//创建服务器
		Car car = new Car();
		String domain = mBeanServer.getDefaultDomain();
		ObjectName objectName = agent.createObjectName(domain + ":type=MyCar");//创建对象名称
		String mBeanName = "myMBean";
		ModelMBean modelMBean = agent.createMBean(objectName, mBeanName);//创建模型MBean
		try {
			modelMBean.setManagedResource(car, "ObjectReference");//托管资源
			mBeanServer.registerMBean(modelMBean, objectName);//注册MBean
		} catch (Exception e) {
		}
		//通过MBean服务器操作MBean来操作Car类
		try {
			Attribute attribute = new Attribute("Color", "green");
			mBeanServer.setAttribute(objectName, attribute);//给指定的MBean复制
			String color = (String) mBeanServer.getAttribute(objectName, "Color");
			System.out.println("Color:" + color);
			//调用drive()方法
			mBeanServer.invoke(objectName, "drive", null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
