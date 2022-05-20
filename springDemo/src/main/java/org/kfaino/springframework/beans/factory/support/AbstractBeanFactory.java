package org.kfaino.springframework.beans.factory.support;


import org.kfaino.springframework.beans.BeansException;
import org.kfaino.springframework.beans.factory.BeanFactory;
import org.kfaino.springframework.beans.factory.config.BeanDefinition;

/**
 * BeanDefinition 注册表接口
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {

    @Override
    public Object getBean(String name) throws BeansException {
        // 判断是否已经在容器里面了
        Object bean = getSingleton(name);
        if (bean != null) {
            return bean;
        }

        // 测试类 -> [1] registerBeanDefinition put
        // 本类方法 -> [2] get
        BeanDefinition beanDefinition = getBeanDefinition(name);

        // [1] newInstance()
        // [2] addSingleton()
        return createBean(name, beanDefinition);
    }

    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException;

}
