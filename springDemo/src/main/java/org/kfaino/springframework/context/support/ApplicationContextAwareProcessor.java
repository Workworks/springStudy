package org.kfaino.springframework.context.support;


import org.kfaino.springframework.beans.BeansException;
import org.kfaino.springframework.beans.factory.config.BeanPostProcessor;
import org.kfaino.springframework.context.ApplicationContext;
import org.kfaino.springframework.context.ApplicationContextAware;

/**
 * 应用上下文感知处理器
 */
public class ApplicationContextAwareProcessor implements BeanPostProcessor {

    private final ApplicationContext applicationContext;

    public ApplicationContextAwareProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(applicationContext);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
