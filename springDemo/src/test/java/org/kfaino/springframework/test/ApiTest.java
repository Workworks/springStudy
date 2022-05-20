package org.kfaino.springframework.test;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import org.junit.Test;
import org.kfaino.springframework.bean.UserDao;
import org.kfaino.springframework.bean.UserService;
import org.kfaino.springframework.beans.PropertyValue;
import org.kfaino.springframework.beans.PropertyValues;
import org.kfaino.springframework.beans.factory.config.BeanDefinition;
import org.kfaino.springframework.beans.factory.config.BeanReference;
import org.kfaino.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ApiTest {

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
         * 疑问?
         *      1.SingletonBeanRegistry为什么只有一个 getSingleton操作 ,而addSingleton 放在DefaultSingletonBeanRegistry
         *      2.BeanDefinitionRegistry为什么只有一个 registerBeanDefinition 而getBeanDefinition 放在DefaultListableBeanFactory实现, 在AbstractBeanFactory组装模板
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
        userService.queryUserInfo();
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
}
