package cn.edu.nynu.codelab.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

/**
 * 修复 MyBatis-Plus MapperFactoryBean 与 Spring 6.1+ 的兼容性问题。
 *
 * <p>MyBatis-Plus ClassPathMapperScanner 将 {@code FactoryBean.OBJECT_TYPE_ATTRIBUTE}
 * 存储为 String 类型（类名），但 Spring Framework 6.1+ 期望 ResolvableType。
 * 此处理器在 Bean 初始化之前将 String 值转换为 ResolvableType，避免
 * {@code Invalid value type for attribute 'factoryBeanObjectType'} 错误。</p>
 *
 * <p>该问题影响 Spring Boot 3.3+ / Spring Framework 6.1+。
 * 待 MyBatis-Plus 上游修复后可移除此类。</p>
 *
 * @author NYNU Code Lab
 */
@Component
public class MybatisPlusSpringFix implements BeanFactoryPostProcessor {

    private static final String FACTORY_BEAN_OBJECT_TYPE = "factoryBeanObjectType";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
            if (bd instanceof AbstractBeanDefinition abd) {
                Object attr = abd.getAttribute(FACTORY_BEAN_OBJECT_TYPE);
                if (attr instanceof String className) {
                    try {
                        Class<?> clazz = Class.forName(className);
                        abd.setAttribute(FACTORY_BEAN_OBJECT_TYPE, ResolvableType.forClass(clazz));
                    } catch (ClassNotFoundException e) {
                        // 类名无法解析，移除该属性让 Spring 走其他路径解析
                        abd.removeAttribute(FACTORY_BEAN_OBJECT_TYPE);
                    }
                }
            }
        }
    }
}
