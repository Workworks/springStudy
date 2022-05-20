package org.kfaino.springframework.test;

import org.junit.Test;
import org.kfaino.springframework.BeanDefinition;
import org.kfaino.springframework.BeanFactory;
import org.kfaino.springframework.bean.UserService;

public class ApiTest {

    @Test
    public void test_BeanFactory(){
        // 1.初始化 BeanFactory
        BeanFactory beanFactory = new BeanFactory();

        // 2.注入bean
        BeanDefinition beanDefinition = new BeanDefinition(new UserService());
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 3.获取bean
        UserService userService = (UserService) beanFactory.getBean("userService");
        userService.queryUserInfo();
    }
}
