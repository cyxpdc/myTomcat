# 来自《深入剖析Tomcat》

**HTTP请求流程：**

每当引入一个HTTP请求：

​	外部处理：

org.apache.catalina.startup.Bootstrap可由startup.sh/bat来启动其main()，main()调用Catalina的process()
org.apache.catalina.startup.Catalina类的process()：创建Digester对象，注册关闭钩子和调用StandardServer的initialize()、start()；还会调用StandardServer的await()阻塞来等待关闭命令；其本身的start会使用Digester解析server.xml文件，获取所有对象
StandardServer的initialize()、start()调用所有的服务组件(**数组**)StandardService的initialize()、start()，用boolean start来判断，stop会重置
StandardService的initialize()、start()调用servlet容器Container和所有(**数组**)连接器HttpConnection的initialize()、start()

​	HttpConnection过程

HttpConnection的start()会启动生成很多HttpProcessor，让这些processor进行run()(阻塞),自身也调用run()
HttpConnection的run()会调用HttpProcessor的assign()**（解析request）**
HttpProcessor的assign()会唤醒await,即HttpProcessor的run()不再阻塞(**异步操作,HttpConnect不会阻塞，可以马上返回进行下一个请求**)
HttpProcessor的run()调用getContainer().invoke(),即在main中HttpConnext设置的容器，为StandardEngine
所以请求都会调用StandardEngine实例的invoke()

​	开始容器的过程：

​	A.
StandardEngine类没有实现invoke，所以调用父类ContainerBase的invoke()
ContainerBase的invoke()会调用StandardEnginetVavle的invoke()（通过管道）
StandardEngineVavle的invoke())验证了request和response对象的类型后，调用ContainerBase的map()
ContainerBase的map()调用StandardEngineMapper的map()
StandardEngineMapper的map()得到Host实例，返回给ContainerBase的map()再返回给StandardEngineVavle的invoke中，即StandardEngineVavle中获取Host实例，调用其invoke()
​	B.

StandardHost类也没有实现invoke()，所以调用父类ContainerBase的invoke()
ContainerBase的invoke()会调用StandardHostVavle的invoke()（通过管道）
StandardHostVavle的invoke()调用ContainerBase的map()
ContainerBase的map()调用StandardHostMapper的map()
**StandardHostMapper的map()简单判断后调用StandardHost的map(uri)来获得Context来获取相应的Context来处理HTTP请求**,即StandardHostVavle中会获得Context实例，调用其invoke()

​	C.
StandardContext的invoke()也调用父类ContainerBase的invoke()
ContainerBase的invoke()也会调用StandardContextVavle的invoke()（通过管道）
StandardContextVavle的invoke()会调用ContainBase的map()
**ContainBase的map()直接调用StandardContextMapper的map()（因为不需要uri来判断是哪个Context）（完全匹配，前缀匹配，扩展匹配，默认匹配）获得StandardWrapper**,调用其invoke()
​	D.
StandWrapper的invoke()也调用父类ContainerBase的invoke()
ContainerBase的invoke()也会调用StandardWrapperVavle的invoke()（通过管道）
**StandardWrapperVavle的invoke()通过过滤器链doFilter()，调用servlet的service()方法**
(调用servlet实例时，会先调用StandardWrapper对象的loadServlet()，然后调用servlet实例的init()，然后调用service())

​	start()过程：部分
当调用StandardHost的start()时，触发START事件，HostConfig会响应并调用start()，该方法(调用StandardHostDeploy的start()，即委派模式)会逐个部署并安装指定目录中的所有Web应用程序(HostConfig类会处理StandardHost实例的start()和stop方法触发的事件)

StandardContext实例start()时触发事件，调用ContextConfig的lifecycleEvent()
ContextConfig的lifecycleEvent()会调用其start()或stop()
start()中调用了defaultConfig()和applicationConfig()来分别处理默认和自定义的web.xml；所有过程成功的话将configured设置为true，失败的话设置为false
defaultConfig()创建一个引用默认web.xml得File对象，锁定webDigester对象变量，开始解析该文件，**将Context对象压栈**
applicationConfig()类似，**也将Context对象压栈**

​	创建管理对象过程：
server.xml中，Server元素定义了ServerLifecycleListener类型的监听器，当调用StandardServer的start()时，触发START_EVENT事件，即ServerLifecycleListener的lifecycleEvent()，这个方法会调用createMBeans()来创建所有的MBean实例(即会一步步创建Service、Engine、Connection、Host、Context等)

即创建对象和创建管理对象都是start
initialize()只会设置initialized变量为true