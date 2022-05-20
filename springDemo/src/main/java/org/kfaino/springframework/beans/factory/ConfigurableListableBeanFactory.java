package org.kfaino.springframework.beans.factory;


import org.kfaino.springframework.beans.BeansException;
import org.kfaino.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.kfaino.springframework.beans.factory.config.BeanDefinition;
import org.kfaino.springframework.beans.factory.config.ConfigurableBeanFactory;

/**
 * Configuration interface to be implemented by most listable bean factories.
 * In addition to {@link ConfigurableBeanFactory}, it provides facilities to
 * analyze and modify bean definitions, and to pre-instantiate singletons.
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

    BeanDefinition getBeanDefinition(String beanName) throws BeansException;

}
