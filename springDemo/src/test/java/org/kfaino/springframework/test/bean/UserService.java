package org.kfaino.springframework.test.bean;

import org.kfaino.springframework.beans.BeansException;
import org.kfaino.springframework.beans.factory.*;
import org.kfaino.springframework.context.ApplicationContext;
import org.kfaino.springframework.context.ApplicationContextAware;

public class UserService implements InitializingBean, DisposableBean, BeanNameAware, BeanClassLoaderAware, ApplicationContextAware, BeanFactoryAware {

    private ApplicationContext applicationContext;
    private BeanFactory beanFactory;

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }


    private String uId;
    private String company;
    private String location;
    //    private UserDao userDao;
    private IUserDao userDao;

    @Override
    public void destroy() throws Exception {
        System.out.println("DisposableBean: 执行UserService.destroy");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("InitializingBean: 执行UserService.afterPropertiesSet");
    }

    public String queryUserInfo() {
        return userDao.queryUserName(uId) + "," + company + "," + location;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public IUserDao getUserDao() {
        return userDao;
    }
//    public UserDao getUserDao() {
//        return userDao;
//    }

    //    public void setUserDao(UserDao userDao) {
//        this.userDao = userDao;
//    }
    public void setUserDao(IUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("Aware:" + applicationContext.toString());
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("Aware Bean Name is：" + name);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        System.out.println("Aware ClassLoader：" + classLoader);
    }
}
