# 来自《深入剖析Tomcat》
# 第一章

Servlet容器是如何工作的：

![1552222380900](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552222380900.png)





HTTP1.1的请求方法：![1552303474324](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552303474324.png)





CRLF符：回车换行





可移植性：File.seperator





ServerSocket的一个重要属性：backlog(积压)，表示在服务器拒绝接受传入的请求之前，传入的连接请求的最大队列长度





一直报空指针异常：Response解析时没加上response头，浏览器无法解析



# 第二章

Servlet程序必须实现javax.servlet.servlet接口或继承自实现了该接口的类

其方法为：

![1552377017884](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552377017884.png)





ServletRequest和ServeltResponse和service()方法：

![1552353054844](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552353054844.png)





一个功能齐全的servlet容器要做的事：

![1552353527905](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552353527905.png)

本章的功能：

![1552353550900](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552353550900.png)

UML：

![1552354039652](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552354039652.png)





bug：Response类中

![1552356095710](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552356095710.png)





URLClassLoader：https://www.cnblogs.com/rogge7/p/7766522.html

![1552363791237](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552363791237.png)





版本1：

![1552365317840](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552365317840.png)





版本2：

使用设计模式提高安全性：外观模式，保护parse()和sendStaticResource()

> parse()在HttpServer类中使用来解析request，sendStaticResource()在StaticResourceProcessor类中来发送静态资源，所以不能为私有;
>
> 但是在servlet中不应该是可用的，所以可以将request和response类都设为默认的访问修饰符，这样就不能从com.pdc.tomcat包外对它们进行访问了，但是更好的方法就是使用外观类：
>
> 将request和response设置为私有，不暴露parse()和sendStaticResource()，只能使用提供的方法，成功保护了parse()和sendStaticResource()



![1552367388488](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552367388488.png)

![1552367724278](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552367724278.png)

代码差别：

![1552368173560](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552368173560.png)





# 第三章

本章建立了连接器connector

需要创建javax.servlet.http.HttpServletRequest和javax.servlet.http.HttpServletResponse对象，并作为servlet的service()方法的参数传入





org.apache.catalina.util.StringManager:用来处理应用程序中不同模块和Catalina本身中错误消息的国际化操作:

每个包下有一个properties文件，且使用**单例模式**，使同一个包下的所有对象共享StringManager对象，每个StringManager都会读取各自包下指定的properties文件

重要代码：

```java
private static volatile HashMap<String, StringManager> managers = new HashMap<>();
//双重检查加锁，如果存在这个包对应的实例，则直接返回，不存在则创建实例
public static StringManager getManager(String packageName) {
    StringManager mgr = (StringManager)managers.get(packageName);
    if (mgr == null) {
        synchronized(StringManager.class){
            if(mgr == null){
                mgr = new StringManager(packageName);
                managers.put(packageName, mgr);
            }
        }
    }
    return mgr;
}
```

getString()方法用来获取错误消息：

![1552380762494](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552380762494.png)





思想:分模块开发

连接器模块：

![1552380863463](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552380863463.png)

启动模块：Bootstrap

核心模块：![1552380872091](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552380872091.png)





UML:

![1552381054358](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552381054358.png)

HttpConnector、HttpProcessor：

![1552382618239](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552382618239.png)

![1552382205903](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552382205903.png)

HttpRequest：解析头、行，体等需要时再解析，优点：节约资源

![1552381863294](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552381863294.png)

HttpResponse：

![1552480402209](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552480402209.png)

SocketInputStream:

![1552381791864](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552381791864.png)

SocketInputStream并不会去把得到的字节码装换成字符串而是保持字节码的形态,逐位的读取字节码,转化成char,填充到method,path和protocol对应的数组中,最后从method,path和protocol对应的数组中得到其对应的字符串.readHeader中也是相似的方法.节省了字符串的"繁重"的操作.





获取参数

![1552478218549](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552478218549.png)





需要使用HashMap，但是需要定制化操作时，可以继承HashMap，如ParameterMap





解析请求体

![1552479908300](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552479908300.png)





PrintWriter：向输出流中写字符





解析HTTP请求：HttpProcessor

![1552485300974](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552485300974.png)

1.获取套接字的输入流

![1552485402439](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552485402439.png)

2.解析请求行

parseRequest()请求行信息用HttpRequestLine表示,使用了SocketInputStream的readRequestLine()

3.解析请求头

parseHead(),请求头信息用HttpHeader表示,使用了SocketInputStream的readHeader()

4.解析cookie

parseCookieHeader()，在RequestUtil中，由parseHead()调用

5.获取参数

parseParameters(),参数信息用ParameterMap表示，里面分为参数的解析和请求体的解析，RequestUtil解析参数，其本身解析请求体的参数

调用如下：优点：做到了节约资源，类似Spring使用ClassLoader实现懒加载

![1552486011557](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552486011557.png)





![1552486510080](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552486510080.png)

前两个用来做限制字节数，第三个用来增加自动刷新功能





# 第四章

Tomcat连接器要求：

![1552530980737](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552530980737.png)





优化：线程池

![1552531166399](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552531166399.png)

且实现了HTTP1.1的全部新特性





HTTP1.1新特性：

1.持久连接：节约资源

![1552531494502](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552531494502.png)

2.块编码

![1552531982365](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552531982365.png)

3.状态码100的使用：接收与拒绝

![1552532020862](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552532020862.png)





默认连接器的UML类图

![1552532501481](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552532501481.png)

关系：

![1552532596371](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552532596371.png)

Connector接口最重要的方法：

![1552532660287](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552532660287.png)





HttpConnector：

![1552554235806](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552554235806.png)

实现了Lifecycle接口，则创建HttpConnector实例后，就应该调用其initialize()和start();在组件的整个生命周期内，这两个方法只应该被调用一次





工厂模式：ServerSocketFactory为接口，DefaultServerSocketFactory为默认实现类，open里面调用createSocket，createSocket里面为new ServerSocket

可以避免直接写new SeverSocket，解耦

![1552555445615](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552555445615.png)



HttpConnector：

一个HttpConnector有多个HttpProcessor，这样就可以同时处理多个HTTP请求（用栈来保存实例，用Vector created来保存已存在实例，都由start()调用）

![1552562238693](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552562238693.png)

数量由protected int minProcessors（默认为5）和private int maxProcessors（默认为20）决定

curProcessors保存当前已有的数量

![1552562477800](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552562477800.png)

start中启动HttpConnector线程且创建初始数量的实例，start由启动类来调用

![1552563564073](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552563564073.png)

run中，通过processor.assign(socket)来调用processor的方法





HttpProcessor：读取套接字的输入流，解析HTTP请求

重点：

![1552565548884](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552565548884.png)

构造方法：

![1552565985154](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552565985154.png)

异步：HttpConnector的newProcessor中，创建HttpProcessor对象时会调用其start()

![1552566129923](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552566129923.png)

设计技巧：assign和await的通信：boolean available，wait和notifyAll()

![1552567153553](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552567153553.png)

调用了assign时，available变为true，且调用了notifyAll，则await唤醒，判断条件，发现不用wait了，会返回一个socket给run里的socket

![1552567141828](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552567141828.png)

处理请求：run里获得socket后，交给process,process会调用servlet容器(SimpleContainer)的invoke来加载servelt

process中的SocketInputStream:

![1552569623968](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552569623968.png)

HttpHeader使用字符数组而不是字符串，来避免代价高昂的字符串操作

设计技巧：HttpHeader缓存池，避免频繁new创建过多对象：protected HttpHeader[] headerPool = new HttpHeader[INITIAL_POOL_SIZE];





Request对象：

![1552568105117](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552568105117.png)

Response对象：

![1552568329695](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552568329695.png)





# 第五章：

### servlet容器需要实现org.apache.catalina.Container接口

四种容器：

![1552632397371](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552632397371.png)

UML:![1552632512501](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552632512501.png)

![1552632465182](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552632465182.png)





### Container接口：

通过set、get方法来与一些如载入器、记录器、管理器、领域和资源等产生关联

```java
public interface Container {

	// ----------------------------------------------------- Manifest Constants

	/**
	 * The ContainerEvent event type sent when a child container is added by
	 * <code>addChild()</code>.
	 */
	public static final String ADD_CHILD_EVENT = "addChild";

	/**
	 * The ContainerEvent event type sent when a Mapper is added by
	 * <code>addMapper()</code>.
	 */
	public static final String ADD_MAPPER_EVENT = "addMapper";

	/**
	 * The ContainerEvent event type sent when a valve is added by
	 * <code>addValve()</code>, if this Container supports pipelines.
	 */
	public static final String ADD_VALVE_EVENT = "addValve";

	/**
	 * The ContainerEvent event type sent when a child container is removed by
	 * <code>removeChild()</code>.
	 */
	public static final String REMOVE_CHILD_EVENT = "removeChild";

	/**
	 * The ContainerEvent event type sent when a Mapper is removed by
	 * <code>removeMapper()</code>.
	 */
	public static final String REMOVE_MAPPER_EVENT = "removeMapper";

	/**
	 * The ContainerEvent event type sent when a valve is removed by
	 * <code>removeValve()</code>, if this Container supports pipelines.
	 */
	public static final String REMOVE_VALVE_EVENT = "removeValve";
	// ------------------------------------------------------------- Properties
	/**
	 * Return descriptive information about this Container implementation and
	 * the corresponding version number, in the format
	 * <code>&lt;description&gt;/&lt;version&gt;</code>.
	 */
	public String getInfo();
	/**
	 * Return the Loader with which this Container is associated. If there is no
	 * associated Loader, return the Loader associated with our parent Container
	 * (if any); otherwise, return <code>null</code>.
	 */
	public Loader getLoader();
	/**
	 * Set the Loader with which this Container is associated.
	 *
	 * @param loader
	 *            The newly associated loader
	 */
	public void setLoader(Loader loader);
	/**
	 * Return the Logger with which this Container is associated. If there is no
	 * associated Logger, return the Logger associated with our parent Container
	 * (if any); otherwise return <code>null</code>.
	 */
	public Logger getLogger();
	/**
	 * Set the Logger with which this Container is associated.
	 *
	 * @param logger
	 *            The newly associated Logger
	 */
	public void setLogger(Logger logger);
	/**
	 * Return the Manager with which this Container is associated. If there is
	 * no associated Manager, return the Manager associated with our parent
	 * Container (if any); otherwise return <code>null</code>.
	 */
	public Manager getManager();
	/**
	 * Set the Manager with which this Container is associated.
	 *
	 * @param manager
	 *            The newly associated Manager
	 */
	public void setManager(Manager manager);
	/**
	 * Return the Cluster with which this Container is associated. If there is
	 * no associated Cluster, return the Cluster associated with our parent
	 * Container (if any); otherwise return <code>null</code>.
	 */
	public Cluster getCluster();
	/**
	 * Set the Cluster with which this Container is associated.
	 *
	 * @param connector
	 *            The Connector to be added
	 */
	public void setCluster(Cluster cluster);
	/**
	 * Return a name string (suitable for use by humans) that describes this
	 * Container. Within the set of child containers belonging to a particular
	 * parent, Container names must be unique.
	 */
	public String getName();
	/**
	 * Set a name string (suitable for use by humans) that describes this
	 * Container. Within the set of child containers belonging to a particular
	 * parent, Container names must be unique.
	 *
	 * @param name
	 *            New name of this container
	 *
	 * @exception IllegalStateException
	 *                if this Container has already been added to the children
	 *                of a parent Container (after which the name may not be
	 *                changed)
	 */
	public void setName(String name);
	/**
	 * Return the Container for which this Container is a child, if there is
	 * one. If there is no defined parent, return <code>null</code>.
	 */
	public Container getParent();
	/**
	 * Set the parent Container to which this Container is being added as a
	 * child. This Container may refuse to become attached to the specified
	 * Container by throwing an exception.
	 *
	 * @param container
	 *            Container to which this Container is being added as a child
	 *
	 * @exception IllegalArgumentException
	 *                if this Container refuses to become attached to the
	 *                specified Container
	 */
	public void setParent(Container container);
	/**
	 * Return the parent class loader (if any) for web applications.
	 */
	public ClassLoader getParentClassLoader();
	/**
	 * Set the parent class loader (if any) for web applications. This call is
	 * meaningful only <strong>before</strong> a Loader has been configured, and
	 * the specified value (if non-null) should be passed as an argument to the
	 * class loader constructor.
	 *
	 * @param parent
	 *            The new parent class loader
	 */
	public void setParentClassLoader(ClassLoader parent);
	/**
	 * Return the Realm with which this Container is associated. If there is no
	 * associated Realm, return the Realm associated with our parent Container
	 * (if any); otherwise return <code>null</code>.
	 */
	public Realm getRealm();
	/**
	 * Set the Realm with which this Container is associated.
	 *
	 * @param realm
	 *            The newly associated Realm
	 */
	public void setRealm(Realm realm);
	/**
	 * Return the Resources with which this Container is associated. If there is
	 * no associated Resources object, return the Resources associated with our
	 * parent Container (if any); otherwise return <code>null</code>.
	 */
	public DirContext getResources();

	/**
	 * Set the Resources object with which this Container is associated.
	 *
	 * @param resources
	 *            The newly associated Resources
	 */
	public void setResources(DirContext resources);
	// --------------------------------------------------------- Public Methods
	/**
	 * Add a new child Container to those associated with this Container, if
	 * supported. Prior to adding this Container to the set of children, the
	 * child's <code>setParent()</code> method must be called, with this
	 * Container as an argument. This method may thrown an
	 * <code>IllegalArgumentException</code> if this Container chooses not to be
	 * attached to the specified Container, in which case it is not added
	 *
	 * @param child
	 *            New child Container to be added
	 *
	 * @exception IllegalArgumentException
	 *                if this exception is thrown by the
	 *                <code>setParent()</code> method of the child Container
	 * @exception IllegalArgumentException
	 *                if the new child does not have a name unique from that of
	 *                existing children of this Container
	 * @exception IllegalStateException
	 *                if this Container does not support child Containers
	 */
	public void addChild(Container child);
	/**
	 * Add a container event listener to this component.
	 *
	 * @param listener
	 *            The listener to add
	 */
	public void addContainerListener(ContainerListener listener);
	/**
	 * Add the specified Mapper associated with this Container.
	 *
	 * @param mapper
	 *            The corresponding Mapper implementation
	 *
	 * @exception IllegalArgumentException
	 *                if this exception is thrown by the
	 *                <code>setContainer()</code> method of the Mapper
	 */
	public void addMapper(Mapper mapper);
	/**
	 * Add a property change listener to this component.
	 *
	 * @param listener
	 *            The listener to add
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);
	/**
	 * Return the child Container, associated with this Container, with the
	 * specified name (if any); otherwise, return <code>null</code>
	 *
	 * @param name
	 *            Name of the child Container to be retrieved
	 */
	public Container findChild(String name);
	/**
	 * Return the set of children Containers associated with this Container. If
	 * this Container has no children, a zero-length array is returned.
	 */
	public Container[] findChildren();
	/**
	 * Return the set of container listeners associated with this Container. If
	 * this Container has no registered container listeners, a zero-length array
	 * is returned.
	 */
	public ContainerListener[] findContainerListeners();
	/**
	 * Return the Mapper associated with the specified protocol, if there is
	 * one. If there is only one defined Mapper, use it for all protocols. If
	 * there is no matching Mapper, return <code>null</code>.
	 *
	 * @param protocol
	 *            Protocol for which to find a Mapper
	 */
	public Mapper findMapper(String protocol);
	/**
	 * Return the set of Mappers associated with this Container. If this
	 * Container has no Mappers, a zero-length array is returned.
	 */
	public Mapper[] findMappers();
	/**
	 * Process the specified Request, and generate the corresponding Response,
	 * according to the design of this particular Container.
	 *
	 * @param request
	 *            Request to be processed
	 * @param response
	 *            Response to be produced
	 *
	 * @exception IOException
	 *                if an input/output error occurred while processing
	 * @exception ServletException
	 *                if a ServletException was thrown while processing this
	 *                request
	 */
	public void invoke(Request request, Response response) throws IOException, ServletException;
	/**
	 * Return the child Container that should be used to process this Request,
	 * based upon its characteristics. If no such child Container can be
	 * identified, return <code>null</code> instead.
	 *
	 * @param request
	 *            Request being processed
	 * @param update
	 *            Update the Request to reflect the mapping selection?
	 */
	public Container map(Request request, boolean update);
	/**
	 * Remove an existing child Container from association with this parent
	 * Container.
	 *
	 * @param child
	 *            Existing child Container to be removed
	 */
	public void removeChild(Container child);
	/**
	 * Remove a container event listener from this component.
	 *
	 * @param listener
	 *            The listener to remove
	 */
	public void removeContainerListener(ContainerListener listener);
	/**
	 * Remove a Mapper associated with this Container, if any.
	 *
	 * @param mapper
	 *            The Mapper to be removed
	 */
	public void removeMapper(Mapper mapper);
	/**
	 * Remove a property change listener from this component.
	 *
	 * @param listener
	 *            The listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);
}

```

可以通过编辑配置文件server.xml来决定使用哪种容器，实现方法：pipeline和valve





### servlet的invoke()方法被调用后发生的事情：

1.责任链模式：

![1552633815148](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552633815148.png)

2.硬编码：直接在invoke中

![1552636298516](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552636298516.png)

设计技巧：解决硬编码：当连接器调用容器的invoke后，容器中要执行的任务并没有硬编码写在invoke方法中，相反，**容器会调用其管道的invoke方法**

如ContainBase的invoke：

```java
public void invoke(Request request, Response response)
    throws IOException, ServletException {
    pipeline.invoke(request, response);//此invoke即下面第一个代码块
}
```

通过引入org.apache.catalina.ValveContext来实现阀的遍历执行：

ValveContext是作为管道的一个内部类实现的，从图5-2就可以看出，这样做的目的是为了让ValveContext可以访问管道的所有成员。

最重要的方法：

![1552634916947](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552634916947.png)

流程：即解决硬编码

每个阀的invoke:

![1552635006169](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552635006169.png)

pipeline中的invoke来开始调用管道中的阀：

```java
public void invoke(Request request, Response response) throws IOException, 		                                                      ServletException {
    // Invoke the first Valve in this pipeline for this request
    (new StandardPipelineValveContext()).invokeNext(request, response);
}

```

invokeNext：

```java
public void invokeNext(Request request, Response response) throws IOException, ServletException {
    int subscript = stage;
    stage++;
    // Invoke the requested Valve for the current request thread
    if (subscript < valves.length) {
        valves[subscript].invoke(request, response, this);//此invoke即上图的代码,循环调用
    } else if ((subscript == valves.length) && (basic != null)) {
        basic.invoke(request, response, this);
    } else {
        throw new ServletException(sm.getString("standardPipeline.noValve"));
    }
}
```





WrapperUML：

![1552637964444](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552637964444.png)





设计技巧：组合了哪个类，就实现其对应的接口





### 流程：一个servlet

1.Bootstrap1的main,调用HttpConnector的start()

2.HttpConnector的start()，start()调用run()，run调用HttpProcessor的assign()

3.HttpProcessor的assign()会唤醒await()、await()激活后，调用run()**(阻塞等待socket)**、run()调用process(),process()调用servlet容器的invoke()，即SimpleWrapper容器类

4.SimpleWrapper的invoke()调用管道SimplePipeline的invoke()

5.SimplePipeline的invoke()调用SimplePipelineValveContext的invokeNext()

6.SimplePipelineValveContext的invokeNext()调用阀的invoke，除基础阀外的invoke会继续调用invokeNext()

7.到了基础阀SimpleWrapperValve后，调用servlet的service方法，即在Bootstrap中加载的servlet类





### Context：用来包含多个servlet

tomcat4中，需要映射器(SimpleContextMapper)来帮助servlet容器(Context)来选择一个子容器来处理某个指定的请求

一个映射器支持一个请求协议，servlet容器可以使用多个映射器来支持不同的协议(HTTP\HTTPS)

ContextUML：

![1552644010997](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552644010997.png)

流程：
![1552652562494](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552652562494.png)

不同之处：基础阀类不再是直接调用servlet的service方法，而是使用Context实例的映射器来查找Wrapper容器（map()方法），找到之后，再调用servlet的service方法

注：

![1552653119133](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552653119133.png)







### 流程：

> 1.Bootstrap2的main,调用HttpConnector的start()

> 2.HttpConnector的start()**(启动线程，创建指定数量的HttpProcessor)**，start()调用threadStrat，会使run()跑起来，run()接受到socket后，获取HttpProcessor，调用HttpProcessor的assign()

> 3.HttpProcessor的assign()会唤醒await()**(设计技巧:使用available变量来通信，这样就达到了异步的操作，assign唤醒await后直接返回，HttpConnector继续等待请求,不像第三章的process，直接处理请求)**、await()激活后，调用run()**(阻塞等待socket)**、run()调用process()**(解析连接、请求、请求头，填充response对象，11完成后，回到这里来finishResponse(刷新到客户端后关闭，此处没有发送静态资源sendStaticResource(3的HttpResponse))和finishRequest(关闭request))**,process()调用servlet容器的invoke()，即SimpleContext容器类

> 4.SimpleContext的invoke()调用其管道SimplePipeline的invoke()

> 5.SimplePipeline的invoke()调用SimplePipelineValveContext的invokeNext()

> 6.SimplePipelineValveContext的invokeNext()调用阀的invoke，除基础阀外的invoke会继续调用invokeNext()

> 7.到了基础阀SimpleContextValve后，查找得到对应的Wrapper实例，调用其invoke方法，即找到SimpleWrapper

> 8.SimpleWrapper的invoke()调用其管道SimplePipeline的invoke()

> 9.SimplePipeline的invoke()调用SimplePipelineValveContext的invokeNext()

> 10.SimplePipelineValveContext的invokeNext()调用阀的invoke，除基础阀外的invoke会继续调用invokeNext()

> 11.到了基础阀SimpleWrapperValve后，调用servlet的service方法，即在Bootstrap2中加载的servlet类，通过SimpleWrapper的allocate来获取，allocate通过loadServlet()来获取，**因为SimpleWrapper保存了servlet类的信息(Bootstrap2中保存)**

> HttpConnector的run会调用createProcessor，createProcessor的本质是newProcessor，而newProcessor的时候，会调用processor的run，一直阻塞等待socket，即每个Processor都会一直等待，直到HttpProcessor的assign被HttpConnector调用时，而HttpConnector的run结束时，调用**threadSync**对象的notifyAll，唤醒threadStop的threadSync.wait，且HttpConnector的stop方法会调用HttpProcesspr的stop(猜测，这里的HttpProcesspr是created集合的，而不是stack的,即所有创建过的HttpProcesspr都要stop)

设计技巧：注：创建过的HttpProcessor结束的话会出栈(不是这个原因)，可能找不到，用额外的集合created来保存，保证都能找到，然后全部stop(上面最后一段)





cookie以枚举的方式传给客户端,





# 第六章

org.apache.catalina.Lifecycle和Listener

![1552714382993](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552714382993.png)



Catalina设计

![1552715199786](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552715199786.png)



![1552715321186](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552715321186.png)

设计技巧：实现：因为都实现了同一个接口，所以可以在父类的start方法中强转其组件为此接口，调用start方法即可

因为为Context context = new SimpleContext(),而Context 并没有Lifecycle的方法，所以要强转，除非直接

SimpleContextcontext = new SimpleContext()，但是不符合面向接口编程





Lifecycle

![1552726465181](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552726465181.png)

LifecycleEvent

![1552726472425](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552726472425.png)

LifecycleListener

![1552726482140](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552726482140.png)

LifecycleSupport(util包下):实现了三个同名方法

![1552726493623](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552726493623.png)





设计技巧：SimpleContext等容器类组合了LifecycleSupport，实现了：

![1552725397721](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552725397721.png)

这样，SimpleContext类下的这三个方法就能直接调用LifecycleSupport的方法





UML:

![1552726177789](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552726177789.png)





开启/关闭组件和子容器时，注意顺序：

开启：

![1552727391832](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552727391832.png)

关闭：

![1552727403290](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552727403290.png)





SimpleContextLifecycleListener中有六个if else，可以使用策略模式优化





设计技巧：SimpleLoader（即容器的组件，又如管道pipeline）实现了Lifecycle后，启动SimpleLoader实例的任务就可以由与其相关联的servlet容器来完成





提供了优雅的方式向其他的组件发生事件消息，且可以通过单一启动/关闭机制**(通过强制到同一接口来实现)**来启动/关闭所有的组件





观察者模式:

LifecycleListener:抽象观察者,lifecycleEvent就是当主题变化时要执行的方法

SimpleContextLifecycleListener:具体观察者

Lifecycle:抽象主题，定义了管理观察者的方法和它要做的其他方法,add即添加观察者,remove和fireLifecycleEvent是必要的方法，而start和stop是扩展的，为了实现可以通过单一启动/关闭机制**(通过强制到同一接口来实现)**来启动/关闭所有的组件添加的

SimpleContext:具体主题

LifecycleSupport:扩展，代理了具体主题对观察者的操作,fireLifecycleEvent使用了克隆模式

LifecycleEvent:定义事件类别，对不同的事件可区别处理，更加灵活

#### 流程：

main将观察者add进SimpleContext的观察者列表中，调用SimpleContext(实现主题接口lifecycle)的start方法，start方法调用lifecycle.fireLifecycleEvent()方法**（用LifecycleSupport来实现，而不是直接将观察者列表放进SimpleContext，达到代码复用，因为会有多个lifecycle实现类）**，即主题发生变化，fireLifecycleEvent()会调用观察者列表LifecycleListener[]的lifecycleEvent()，即通知所有观察者,而通过type参数，观察者可以自行判断是否是自己想要的操作





# 第七章：日志记录器

需要与某个servlet容器相关联





日志记录器必须实现org.apache.catalina.Logger接口





verbosity：

![1552740518200](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552740518200.png)

![1552741510372](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552741510372.png)





设计技巧：接口中存在的其他类的依赖，如addXXX，则实现类中要有XXX的实例集合





UML:

![1552740851788](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552740851788.png)





设计：

LoggerBase：实现了Logger接口中除log(String msg)方法外的全部方法,其余重载的log方法最终都是调用此方法





设计技巧：

原先：直接刷新流到文件

改动：加了层判断：close会将剩余的内容刷新到文件上再关闭，open会重新创建PrintWrite实例来记录日志

多了层判断，可以避免如秒杀系统时的高并发操作一直创建PrintWriter对象(猜的)

![1552743311167](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552743311167.png)





# 第八章：载入器

为什么要实现一个自定义的载入器：因为servlet容器不应该完全信任它正在运行的servlet类

![1552794683520](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552794683520.png)

增加规则、缓存、预载入

![1552795803378](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552795803378.png)

规则：

![1552795966417](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552795966417.png)

![1552812613818](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552812613818.png)

缓存：可以用redis来实现

![1552811319630](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552811319630.png)

![1552812376855](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552812376855.png)

protected HashMap<String, ResourceEntry> resourceEntries = new HashMap<>();

![1552812387227](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552812387227.png)





Tomcat中的载入器：

![1552796028039](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552796028039.png)





双亲委派：通过继承ClassLoader来编写类载入器，Tomcat的载入器不仅仅是类载入器(如上图)，所以继承下面的Loader接口

![1552795781421](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552795781421.png)





Loader接口：载入器需要实现的接口





WebappClassLoader就是自定义的类载入器





modified()：支持类的自动重载：

![1552796468294](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552796468294.png)

注：

![1552800880018](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552800880018.png)





UML：

![1552796803764](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552796803764.png)

实现：

![1552796848975](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552796848975.png)





WebappLoader：web应用程序中的载入器

start():(未看：设置仓库设置类路径设置访问权限 p118),由容器启动一起启动(lifecycle)

![1552800663551](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552800663551.png)





反射：WebappLoader的createClassLoader,有父加载器时，如else所示来使用

![1552801629655](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552801629655.png)





设计技巧：每当要做一些比较耗时的事情，可以使用内部类新开线程

![1552810448086](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552810448086.png)





流程：

main中StandardContext.start，会启动WebappLoader.start()，WebappLoader会创建类加载器createClassLoader()





# 第九章：Session管理

Session对象由javax.servlet.http.HttpSession接口表示



获取Session对象：javax.servlet.http.HttpServletRequest接口，实现类：HttpRequestBase的getSession



### Catalina如何管理Session对象：

外观模式：

> 为了安全起见，使用StandardSessionFacade(仅实现了HttpSession接口，没有实现Session接口)作为StandardSession(Session接口的标准实现)的外观类，这样程序员就不能将HttpSession对象向下转换为StandardSession类型，也阻止了程序员访问一些敏感方法

> Session接口是作为Catalina内部的外观类使用的,包装HttpSession对象：is used to maintain state information between requests for a particular user of a web application.(用于维护Web应用程序特定用户的请求之间的状态信息)





UML:

![1552906301318](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552906301318.png)





StandardSession是Session接口的标准实现，即标准Session对象；

去除空参构造，强迫每个Session对象必须拥有一个Session管理器实例

变量：

![1552908211601](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552908211601.png)





org.apache.catalina.Manager接口：负责创建、更新、销毁(tomcat4通过开线程)Session对象

UML:

![1552910649177](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552910649177.png)

基本实现类：ManagerBase，有Session池变量，**为HashMap(protected HashMap<String, Session> sessions = new HashMap<>();)**；基本实现类的子类：StandardManager(标准实现类)、PersistentManagerBase(辅助存储器)



StandardManager:

![1552924594685](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552924594685.png)



PersistentManagerBase是用来将Session对象存储到辅助存储器中的管理器的基类，其有两个子类：PersistentManager类和DistributedManager类



Tomcat4、Tomcat5：不开线程，节约资源

![1552919715326](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552919715326.png)

![1552919703583](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552919703583.png)

![1552920806554](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552920806554.png)

总结：
![1553152274636](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1553152274636.png)





持久化Session管理器和StandardManager(存储于内存中)的区别在于存储器的表现形式，前者用Store对象来表示，好处：![1552919567327](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552919567327.png)

换出：

![1552922777380](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552922777380.png)

备份：

![1552922798827](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552922798827.png)





DistributedManager:

![1552920208902](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552920208902.png)

开启线程，专门检查Session对象是否过期，并从集群中的其他节点上接受消息

集群操作类：

![1552920553133](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552920553133.png)



Store接口UML：

![1552920590767](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552920590767.png)

FileStore：将Session对象序列化，用ObjectOutputStream

JDBCStore：

![1552921226780](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552921226780.png)





Catalina运行关闭时session的运作：![1552910623973](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552910623973.png)





### 应用程序如何通过关联管理器来使用Conext容器：



getSession()原理：如下图操作后，getSession会调用doGetSession，doGetSession会根据其Context来findSession()（Context关联了Session管理器）,若找到，返回，若没有，则创建

![1552921545584](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552921545584.png)





流程：

容器类关联Manager，Manager关联Session,request关联容器类，可以通过容器类寻找Manager来getSession()





# 第十章：安全性

#### 使用验证器阀来支持安全限制

流程：![1552971840621](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552971840621.png)

![1552978863622](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552978863622.png)

验证器：org.paache.catalina.Authenticator接口的实例

UML：![1552980854160](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552980854160.png)

设计：Authenticator接口本身并没有方法，只是起到一个标记的作用，这样其他的组件可以通过instanceof关键字来检查其是否为一个验证器

基本实现类：AuthenticatorBase类，这个类还继承了ValveBase类，成为一个阀

其他实现类：![1552980960685](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552980960685.png)







#### 领域对象：org.apache.catalina.Reaim接口![1552977662450](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552977662450.png)

如何验证用户身份：领域保存了**所有**有效用户的用户名和密码对,或会返回存储这些数据的存储器(比如tomcat-user.xml文件或数据库)

基本实现类：RealmBase

具体实现类：JDBCRealm,JNDIRealm,MemoryRealm(默认,读取tomcat-users.xml),UserDatabaseRealm





#### 主体对象：java.security.Principal接口

实现类：GenericPrincipal，没有空参构造，一个GenericPrincipal实例必须始终与一个领域对象相关联，必须有**一个**用户名和密码对，且该用户名密码对所对应的角色列表是可选的





#### 登录配置：org.apache.catalina.deploy.LoginConfig类实例

包含一个领域对象的名字和所要使用的身份验证名字（BASIC、DIGEST、FORM、CLIENT-CERT），如果使用的是基于表单的身份验证方法，还需要再loginPage属性和errorPage属性中分别存储字符串形式的登录页面和错误页面的URL

如果web.xml文件有login-config配置，则创建此对象，流程：![1552980392572](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552980392572.png)





#### 验证器阀和登录配置的结合：

一个Context实例只能有一个LoginConfig实例和一个验证器类的实现：login-config标签包含的auto-method属性决定验证器类，若没有配置属性，则用NonLoginAuthenticator：![1552981416843](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552981416843.png)

验证器类是运行时才确定的，因此需要动态载入，使用org.apache.catalina.startup.ContextConfig**(本章用SimpleContextConfig，负责动态载入BasicAuthenticator类，实例化其对象，并将其作为一个阀安装到StandardContext实例中)**类来对StandardContext实例的属性进行设置，如实例化验证器类，并将该实例与Context实例相关联





#### 应用程序

###### SimpleContextConfig:

lifecycleEvent：调用authenticatorConfig

authenticatorConfig()：实例化BasicAuthenticator类，并将其作为阀添加到StandardContext实例的管道中





###### SimpleRealm:领域对象，组合进StandardContext

内部有个user类，其角色用String表示，不用单独的类

anthenticate方法由其关联的StandardContext容器关联的SimpleContextConfig调用

![1552984879204](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552984879204.png)





###### SimpleUserDatabaseRealm:复杂一点的领域对象

![1552985060357](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552985060357.png)

实例化SimpleUserDatabaseRealm后，调用createDatabase方法，传递xml路径，使用MemoryUserDatabase来读取并解析XML文档(未看，黑盒)





###### Bootstrap1:

设计技巧：将BasicAuthenticator对象安装到StandardContext对象的管道中这一操作交给StandardContext的监听器来完成(lifecycleEvent会调用authenticatorConfig完成)，达到解耦的目的

未看代码：![1552986479241](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552986479241.png)





流程:

验证器类会作为阀加入到管道中，invoke调用验证器类的invoke，获得其Context关联的领域对象，进行验证





注：SimpleContextConfig的异常用了Throwable





# 第十一章：StandardWrapper

#### 1.对处理HTTP请求的方法序列进行说明

流程：![1552994372999](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552994372999.png)







#### 2.介绍javax.servlet.SingleThreadModel接口

对该接口的理解是理解Wrapper如何载入servlet类的关键

servlet类实现此接口的目的是保证servlet实例一次只处理一个请求

弃用：多线程的虚假安全性![1552995812239](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552995812239.png)





#### 3.对StandardWrapper和StandardWrapperValve类的说明

StandardWrapper负责载入它所代表的servlet类并进行实例化，StandardWrapperValve负责从StandardWrapper中获取servlet实例并执行service()

StandardWrapperValve请求servlet实例时，StandardWrapper需要考虑该servlet类是否实现了SingleThreadModel接口。
如果没有：![1552996372216](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552996372216.png)如果有：StandardWrapper必须保证每个时刻只有一个线程在执行STM serlvet类的service方法
如果只有一个STM servlet实例：![1552996465347](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552996465347.png)
如果有多个STM servlet实例：维护一个STM servlet实例池，锁住整个线程池来操作





###### 如何分配并载入servlet类：

分配：StandardWrapper的allocate()：
获取请求的servlet的一个实例，分为两个部分，分别处理STM和非STM
非：![1552997538284](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552997538284.png)
是：![1552997547126](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1552997547126.png)





载入：StandardWrappe类实现Wrapper接口的load()方法，调用loadServlet的方法载入某个servlet类，并调用其init()方法
流程：解析servlet类名，获取载入器，根据载入器获取类加载器，载入servlet类，实例化该类，调用servlet的init，调用servlet的service，若为STM servlet类，则创建一个STM池
专用的servlet类：

![1553001587232](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1553001587232.png)



ServletConfig：载入过程中的init方法需要传入一个javax.servlet.ServletConfig实例**(其实是其外观对象,类型为StandardWrapperFacade,即其实是StandardWrappe)**作为参数
StandardWrappe如何获取servletConfig对象呢？答案是StandardWrappe、StandardWrapperFacade实现ServletConfig接口，StandardWrapperFacade内将StandardWrappe强转为ServletConfig，**这样StandardWrapper的与ServletConfig接口无关的方法就被保护起来了**。代码:
![1553052210522](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1553052210522.png)

ApplicationContext实现了ServletConfig接口，也是用外观类ApplicationContextFacade进行外界访问，StandardWrapperFacade的getServletContext方法就是得到ApplicationContextFacade



StandardWrapperValve：StandardWrapper基础阀
1.执行与该servlet实例相关联的全部过滤器Filter
2.调用servlet的service方法
invoke流程:涉及过滤器
过滤器链：ApplicationFilterChain



![1553063888701](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1553063888701.png)





过滤器：	解释加上图流程详解

![1553067598711](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1553067598711.png)

org.apache.catalina.deploy.FilterDef:表示一个过滤器定义，属性表示定义filter元素时声明的子元素，用来用户定义在xml文件中，保护Filter对象,通过**getFilterClass()**方法来获取其类名

org.apache.catalina.core.ApplicationFilterConfig:实现FilterConfig接口，用于管理web应用程序(Context)第一次启动时创建的所有过滤器(FilterDef)实例，所以构造方法需要这两个对象，,通过**getFilter()**来获得Filter对象,可以理解为外观类，或装饰者模式

org.apache.catalina.core.ApplicationFilterChain:实现javax.servlet.FilterChain接口,StandardWrapperValve的invoke方法会创建此对象，调用其doFilter方法,doFilter做一系列判断后调用internalDoFilter，internalDoFilter循环其ApplicationFilterConfig集合，获取每个Filter执行doFilter方法，最后一个filter会调用servlet的doFilter方法

拦截器保存在StandardContext的filterMaps属性中





# 第十二章：StandardContext类

#### 1.StandardContext类的实例化和配置和相关重要属性

available属性表明StandardContext对象是否可用，start失败时，被设置为false
start正确执行，表明StandardContext对象(及其子容器和组件)配置正确，才能读取并解析默认的web.xml

boolean configured表明StandardContext实例是否正确设置。start中，触发一个生命周期事件调用作为配置器的监听器，对StandardContext实例进行配置，若成功，configured为true，否则为false，拒绝启动

构造方法：设置基础阀(最重要)，和为namingResources设置为此Container
start()流程：
![1553136094424](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1553136094424.png)
invoke()：paused属性为true，则表明应用程序正在重载,12.3介绍
tomcat5中只执行ContainBase类的invoke**(调用其管道的invokeContainBase的管道为StandardPipeline,即StandardContext的管道定义在其父类中,StandardWrapper也是如此)**，检查重载的工作交给了基础阀的invoke
![1553136709781](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1553136709781.png)

StandardContext的mapper属性定义在ContainBase中,定义为protected，这样StandardContext在同一包下就能直接使用

reloadable属性指明该应用程序是否启用了重载功能，启用后，web.xml文件发送变化或WEB-INF/classes目录下的其中一个文件被重新编译后，应用程序会重载
实现：通过载入器，使用一个线程(内部类作为新线程)来检查WEB-INF目录中所有类和JAR文件的时间戳
流程：main的setLoader也会让其loader进行setContainer，setContainer会调用setReloadable()，setReloadable设置reloadable，若为true，启动线程，false，关闭线程





#### 2.StandardContextMapper、ContextConfig

StandardContextMapper:映射器，StandardContextValve实例在它包含的StandardContext中的映射器map()方法找到一个合适的Wrapper实例，获得Wrapper实例后，调用Wrapper的invoke()方法（映射器的key为协议）





#### 3.Tomcat5中的backgroundProcess()

参见tomcat4、tomcat5区别

共享线程在ContainerBase中创建，由start()中的threadStart来启动
![1553152719627](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1553152719627.png)
![1553152730929](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1553152730929.png)











































































































































