package org.kfaino.springframework.beans.factory.support;


import org.kfaino.springframework.beans.BeansException;
import org.kfaino.springframework.core.io.Resource;
import org.kfaino.springframework.core.io.ResourceLoader;

/**
 * Simple interface for bean definition readers.
 * <p>
 */
public interface BeanDefinitionReader {

    BeanDefinitionRegistry getRegistry();

    ResourceLoader getResourceLoader();

    void loadBeanDefinitions(Resource resource) throws BeansException;

    void loadBeanDefinitions(Resource... resources) throws BeansException;

    void loadBeanDefinitions(String location) throws BeansException;

    void loadBeanDefinitions(String... locations) throws BeansException;

}
