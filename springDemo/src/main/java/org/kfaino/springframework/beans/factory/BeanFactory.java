package org.kfaino.springframework.beans.factory;


import org.kfaino.springframework.beans.BeansException;

public interface BeanFactory {

    Object getBean(String name) throws BeansException;

}
