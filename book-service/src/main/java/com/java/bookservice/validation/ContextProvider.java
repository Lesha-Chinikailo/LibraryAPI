package com.java.bookservice.validation;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ContextProvider implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ContextProvider.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> cls) {
        return ContextProvider.applicationContext.getBean(cls);
    }

}
