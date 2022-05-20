package org.kfaino.springframework.test;

import cn.hutool.core.io.IoUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import org.junit.Before;
import org.junit.Test;
import org.kfaino.springframework.bean.UserDao;
import org.kfaino.springframework.bean.UserService;
import org.kfaino.springframework.beans.PropertyValue;
import org.kfaino.springframework.beans.PropertyValues;
import org.kfaino.springframework.beans.factory.config.BeanDefinition;
import org.kfaino.springframework.beans.factory.config.BeanReference;
import org.kfaino.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.kfaino.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.kfaino.springframework.core.io.DefaultResourceLoader;
import org.kfaino.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ApiTest {

    private DefaultResourceLoader resourceLoader;

    @Before
    public void init() {
        resourceLoader = new DefaultResourceLoader();
    }

    // 更换源Https为SSH git config --global url.ssh://git@github.com/.insteadOf https://github.com/
    @Test
    public void test_BeanFactory01() {
        // 1.初始化 BeanFactory
        // BeanFactory beanFactory1 = new BeanFactory();

        /**
         * AbstractBeanFactory 抽象Bean工厂, 模板方法模式 (继承默认单例Registry)
         *      getBean 做的事
         *          1. [getSingleton] 获取单例Bean对象是否存在 (在一级缓存)
         *          2. 获取BeanDefinition , 由外部 [registerBeanDefinition]
         *          3. 通过Class对象 newInstance()
         *          4. [addSingleton]
         *
         * AbstractAutowireCapableBeanFactory 装配能力工厂
         *      1.装配能力工厂创建不支持带参构造, 第三版需要改造
         *
         * DefaultListableBeanFactory 聚合工厂
         *      |- AbstractAutowireCapableBeanFactory
         *      |- AbstractBeanFactory
         *
         * 疑问:
         *      1.SingletonBeanRegistry为什么只有一个 getSingleton操作 ,而addSingleton 放在DefaultSingletonBeanRegistry ?
         *      2.BeanDefinitionRegistry为什么只有一个 registerBeanDefinition 而getBeanDefinition 放在DefaultListableBeanFactory实现, 在AbstractBeanFactory组装模板??
         */
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2.注入bean
        // 第一版的是后注入的是实例化对象
//        BeanDefinition beanDefinition = new BeanDefinition(new UserService());
        // 第二版注入的是类对象
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 3.获取bean
//        UserService userService = (UserService) beanFactory.getBean("userService");
//        userService.queryUserInfo();

        /**
         * AbstractAutowireCapableBeanFactory 装配能力工厂
         *      1. 组合字节码提升实例化策略 CglibSubclassingInstantiationStrategy 有该类去创建带参实例
         *
         */
        // 3.获取带参Bean
        UserService userService = (UserService) beanFactory.getBean("userService", "kfaino");
        userService.queryUserInfo();
    }

    /**
     * 除了支持带参构造器的实例, 还要支持实例化的对象属性进行赋值
     * 因此,我们应该在 AbstractAutowireCapableBeanFactory 装配能力工厂 [createBean] 的时候 [applyPropertyValues] 步骤如下
     * 1. 通过 BeanDefinition 获取 PropertyValues 遍历
     * 2. 通过遍历的属性 K:V 设置进实例对象中 BeanUtil.setFieldValue
     */
    @Test
    public void test_BeanFactory02() {
        // 1.初始化 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2. UserDao 注册
        beanFactory.registerBeanDefinition("userDao", new BeanDefinition(UserDao.class));

        // 3. UserService 设置属性[uId、userDao]
        PropertyValues propertyValues = new PropertyValues();
        propertyValues.addPropertyValue(new PropertyValue("uId", "10001"));
        propertyValues.addPropertyValue(new PropertyValue("userDao", new BeanReference("userDao")));

        // 4. UserService 注入bean
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class, propertyValues);
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 5. UserService 获取bean
        UserService userService = (UserService) beanFactory.getBean("userService");
//        userService.queryUserInfo();

        String result = userService.queryUserInfo();
        System.out.println("测试结果：" + result);
    }

    /**
     * 把 bean的创建交给容器, 用户只需要定义bean和属性赋值
     * 把 test_BeanFactory02 的 2,3,4的步骤整合到框架中
     * xml 方式注入属性
     * <p>
     * new XmlBeanDefinitionReader(xx) 到底做了什么?
     * 1. super父类 AbstractBeanDefinitionReader 的方法获取默认的资源加载器(DefaultResourceLoader)
     * 2. 调用 loadBeanDefinitions传入xml定位信息(location) 获取到输入流(InputStream)
     * 3. 通过 doLoadBeanDefinitions 核心方法解析xml文件
     * 1. 解析标签, 获取标签属性
     * 2. 通过class标签获取类Reference 进行实例化
     * 3. 进行属性赋值 , 和上面的自动装配有异曲同工之妙 ,这里依赖有循环依赖问题
     * 4. 最后一步 , 把BeanDefinition 注册到容器中(registerBeanDefinition)
     * 5. 至此 ,创建bean的初始化要素已经全部生成好了 : 类信息(Class) , 类定义信息(BeanDefinition)
     * 主要方法
     * XmlBeanDefinitionReader -> doLoadBeanDefinitions 该方法读取xml 解析xml标签, 把获取的标签
     * <p>
     * <p>
     * 疑问:
     * 关于新增的接口BeanFactory 为什么要增加 HierarchicalBeanFactory 层级接口, 目的是什么?
     * ConfigurableListableBeanFactory 作为聚合工厂 , 为何还要实现 getBeanDefinition ,
     * 而它的实现类 DefaultListableBeanFactory 继承的 AbstractAutowireCapableBeanFactory 也有一个protect的 getBeanDefinition方法 这两个有什么不一样吗?
     */
    @Test
    public void test_BeanFactory03() {
        // 1.初始化 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2. 读取配置文件&注册Bean
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions("classpath:spring.xml");

        // 3. 获取Bean对象调用方法
        UserService userService = beanFactory.getBean("userService", UserService.class);
        String result = userService.queryUserInfo();
        System.out.println("测试结果：" + result);
    }


    /**
     * 字节码提升测试
     */
    @Test
    public void test_cglib() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserService.class);
        enhancer.setCallback(new NoOp() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        });
        Object obj = enhancer.create(new Class[]{String.class}, new Object[]{"kfaino"});
        System.out.println(obj);
    }

    /**
     * 实例化测试
     */
    @Test
    public void test_newInstance() throws IllegalAccessException, InstantiationException {
        UserService userService = UserService.class.newInstance();
        System.out.println(userService);
    }

    /**
     * 实例化带参测试
     */
    @Test
    public void test_constructor() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<UserService> userServiceClass = UserService.class;
        Constructor<UserService> declaredConstructor = userServiceClass.getDeclaredConstructor(String.class);
        UserService userService = declaredConstructor.newInstance("kfaino");
        System.out.println(userService);
    }

    /**
     * 实例化带参测试 , 获取[1], 即有一个带参的构造器实例化对象
     */
    @Test
    public void test_parameterTypes() throws Exception {
        Class<UserService> beanClass = UserService.class;
        Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
        Constructor<?> constructor = declaredConstructors[1];
        Constructor<UserService> declaredConstructor = beanClass.getDeclaredConstructor(constructor.getParameterTypes());
        UserService userService = declaredConstructor.newInstance("kfaino");
        System.out.println(userService);
    }

    @Test
    public void test_classpath() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:important.properties");
        InputStream inputStream = resource.getInputStream();
        String content = IoUtil.readUtf8(inputStream);
        System.out.println(content);
    }

    @Test
    public void test_file() throws IOException {
        Resource resource = resourceLoader.getResource("src/test/resources/important.properties");
        InputStream inputStream = resource.getInputStream();
        String content = IoUtil.readUtf8(inputStream);
        System.out.println(content);
    }

    @Test
    public void test_url() throws IOException {
        Resource resource = resourceLoader.getResource("https://github.com/Workworks/springStudy/important.properties");
        InputStream inputStream = resource.getInputStream();
        String content = IoUtil.readUtf8(inputStream);
        System.out.println(content);
    }
}
