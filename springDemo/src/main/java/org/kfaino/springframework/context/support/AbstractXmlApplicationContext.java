package org.kfaino.springframework.context.support;


import org.kfaino.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.kfaino.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.kfaino.springframework.context.ApplicationContext;

/**
 * Convenient base class for {@link ApplicationContext}
 * implementations, drawing configuration from XML documents containing bean definitions
 * understood by an {@link XmlBeanDefinitionReader}.
 */
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableApplicationContext {

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory, this);
        String[] configLocations = getConfigLocations();
        if (null != configLocations) {
            beanDefinitionReader.loadBeanDefinitions(configLocations);
        }
    }

    protected abstract String[] getConfigLocations();

}
