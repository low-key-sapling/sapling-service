package com.sapling.framework.web.core.version;

import com.sapling.framework.web.core.annotation.ApiVersion;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;

/**
 * 一个扩展了RequestMappingHandlerMapping的类，支持API版本路由。
 * 它允许方法或类通过ApiVersion注解来支持版本控制。
 * @author Artisan
 */
public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private RequestMappingInfo.BuilderConfiguration  config = new RequestMappingInfo.BuilderConfiguration();

    /**
     * API版本在URL中的前缀
     */
    private final String prefix;
    /**
     * API版本在URL中的后缀，默认为空字符串，如果未提供则为空字符串
     */
    private final String suffix;

    /**
     * 构造函数用于初始化API版本的前缀和后缀。
     *
     * @param prefix API版本在URL中的前缀
     * @param suffix API版本在URL中的后缀，如果没有提供则默认为空字符串
     */
    public ApiVersionRequestMappingHandlerMapping(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = StringUtils.isEmpty(suffix) ? "" : suffix;
    }

    /**
     * 覆盖此方法以获取方法的路由信息，并支持基于ApiVersion注解的自定义条件。
     *
     * @param method 需要获取路由信息的方法
     * @param handlerType 处理器类型
     * @return 方法的路由信息，包括基于API版本的自定义条件
     */
    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, @NotNull Class<?> handlerType) {
        // 获取基本的路由信息
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
        if (info == null) {
            return null;
        }

        // 检查方法是否使用了ApiVersion注解
        ApiVersion methodAnnotation = AnnotationUtils.findAnnotation(method, ApiVersion.class);
        if (methodAnnotation != null) {
            // 获取自定义方法条件
            RequestCondition<?> methodCondition = getCustomMethodCondition(method);

            final PathPatternParser patternParser = getPatternParser();
            if (this.config.getPatternParser() == null){
                this.config.setPatternParser(patternParser);
            }
            // 创建基于API版本的信息并合并到基本信息中
            // info = createApiVersionInfo(methodAnnotation, methodCondition).combine(info);

            return RequestMappingInfo.paths(prefix + methodAnnotation.value() + suffix).options(this.config)
                    .build().combine(info);



        } else {
            // 如果方法没有使用ApiVersion注解，则检查类是否使用了该注解
            ApiVersion typeAnnotation = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);
            if (typeAnnotation != null) {
                // 获取自定义类条件
                RequestCondition<?> typeCondition = getCustomTypeCondition(handlerType);

                final PathPatternParser patternParser = getPatternParser();
                if (this.config.getPatternParser() == null){
                    this.config.setPatternParser(patternParser);
                }

                // 创建基于API版本的信息并合并到基本信息中
                // info = createApiVersionInfo(typeAnnotation, typeCondition).combine(info);

                return RequestMappingInfo.paths(prefix + typeAnnotation.value() + suffix).options(this.config)
                        .build().combine(info);
            }
        }

        return info;
    }

    /**
     * 根据ApiVersion注解创建路由信息。
     *
     * 该方法解析ApiVersion注解的值，并根据这些值构建URL模式，
     * 然后结合自定义条件创建RequestMappingInfo对象，用于支持版本控制。
     *
     * @param annotation ApiVersion注解实例，包含API版本信息。
     * @param customCondition 自定义条件，用于进一步细化请求映射。
     * @return 基于API版本的路由信息，用于将请求映射到特定版本的API处理方法上。
     */
//    private RequestMappingInfo createApiVersionInfo(ApiVersion annotation, RequestCondition<?> customCondition) {
//        // 获取注解中指定的API版本数组
//        int[] values = annotation.value();
//        // 为每个API版本创建对应的URL模式
//        String[] patterns = new String[values.length];
//        for (int i = 0; i < values.length; i++) {
//            // 构建URL前缀
//            patterns[i] = prefix + values[i] + suffix;
//
//            RequestMappingInfo.paths( prefix + values[i] + suffix)
//                    .options(this.config)
//                    .build();
//        }
//
//
//        // 使用构建的URL模式和其他请求条件创建并返回RequestMappingInfo对象
//        return new RequestMappingInfo(
//                new PatternsRequestCondition(patterns, getUrlPathHelper(), getPathMatcher(),
//                        useSuffixPatternMatch(), useTrailingSlashMatch(), getFileExtensions()),
//                new RequestMethodsRequestCondition(),
//                new ParamsRequestCondition(),
//                new HeadersRequestCondition(),
//                new ConsumesRequestCondition(),
//                new ProducesRequestCondition(),
//                customCondition);
//    }

}
