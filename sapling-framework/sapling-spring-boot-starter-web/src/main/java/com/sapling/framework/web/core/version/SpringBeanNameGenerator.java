package com.sapling.framework.web.core.version;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.StringUtils;

/**
 * 类SpringBeanNameGenerator用于生成Spring Bean的名称，通过扩展AnnotationBeanNameGenerator实现
 * 其目的是为了提供一个定制化的Bean名称生成策略，使得在Spring容器中，Bean的名称可以更加明确和有意义
 *
 * @author Artisan
 */
public class SpringBeanNameGenerator extends AnnotationBeanNameGenerator {

    /**
     * 构建默认的Bean名称策略
     * 此方法首先检查BeanDefinition是否为AnnotatedBeanDefinition类型，如果是，则尝试从注解中确定Bean的名称
     * 如果通过注解能够确定一个显式的Bean名称，则使用该名称；否则，使用Bean的类名作为默认名称
     *
     * @param definition Bean的定义，包含了Bean的详细信息
     * @return 生成的Bean名称
     */
    @Override
    protected String buildDefaultBeanName(BeanDefinition definition) {
        // 检查是否为AnnotatedBeanDefinition类型，以便可以从注解中读取Bean名称
        if (definition instanceof AnnotatedBeanDefinition) {
            // 尝试从注解中确定Bean名称
            String beanName = determineBeanNameFromAnnotation((AnnotatedBeanDefinition) definition);
            // 如果找到了有效的Bean名称，则返回该名称
            if (StringUtils.hasText(beanName)) {
                return beanName;
            }
        }
        // 如果没有从注解中找到有效的Bean名称，则使用Bean的类名作为默认名称
        return definition.getBeanClassName();
    }
}

