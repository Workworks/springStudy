package org.kfaino.springframework.test;

import org.junit.Test;
import org.kfaino.springframework.bean.UserService;
import org.kfaino.springframework.beans.factory.config.BeanDefinition;
import org.kfaino.springframework.beans.factory.support.DefaultListableBeanFactory;

public class ApiTest {

    // 更换源Https为SSH git config --global url.ssh://git@github.com/.insteadOf https://github.com/
    @Test
    public void test_BeanFactory() {
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
        UserService userService = (UserService) beanFactory.getBean("userService");
        userService.queryUserInfo();
    }
}
