package org.kfaino.springframework.test.common;


import org.kfaino.springframework.beans.BeansException;
import org.kfaino.springframework.beans.PropertyValue;
import org.kfaino.springframework.beans.PropertyValues;
import org.kfaino.springframework.beans.factory.ConfigurableListableBeanFactory;
import org.kfaino.springframework.beans.factory.config.BeanDefinition;
import org.kfaino.springframework.beans.factory.config.BeanFactoryPostProcessor;

public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("userService");
        PropertyValues propertyValues = beanDefinition.getPropertyValues();

        propertyValues.addPropertyValue(new PropertyValue("company", "改为：字节跳动"));
    }

}
