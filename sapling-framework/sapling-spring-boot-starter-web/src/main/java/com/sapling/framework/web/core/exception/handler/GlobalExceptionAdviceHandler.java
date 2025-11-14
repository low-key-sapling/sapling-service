package com.sapling.framework.web.core.exception.handler;


import com.sapling.framework.common.exception.enums.AppHttpStatus;
import com.sapling.framework.common.enums.DateFormat;
import com.sapling.framework.common.exception.BasicException;
import com.sapling.framework.common.exception.PrintExceptionInfo;
import com.sapling.framework.common.utils.json.JSONUtils;
import com.sapling.framework.core.context.holder.ContextHolder;
import com.sapling.framework.core.entity.ApiResponse;
import com.sapling.framework.core.entity.BaseLogger;
import com.sapling.framework.core.helper.RequestHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author mbws
 * @Description: 控制并统一处理异常类
 * @ExceptionHandler标注的方法优先级问题，它会找到异常的最近继承关系，也就是继承关系最浅的注解方法
 * @Version: 1.0
 */
@RestControllerAdvice
public class GlobalExceptionAdviceHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionAdviceHandler.class);

    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    /**
     * 处理所有异常，主要是提供给 Filter 使用
     * 因为 Filter 不走 SpringMVC 的流程，但是我们又需要兜底处理异常，所以这里提供一个全量的异常处理过程，保持逻辑统一。
     *
     * @param request 请求
     * @param ex      异常
     * @return 通用返回
     */
    public <T> ResponseEntity<ApiResponse<T>> allExceptionHandler(HttpServletRequest request, Throwable ex) {
        if (ex instanceof MissingServletRequestParameterException) {
            return missingServletRequestParameterExceptionHandler((MissingServletRequestParameterException) ex,request);
        }
        if (ex instanceof MethodArgumentTypeMismatchException) {
            return methodArgumentTypeMismatchExceptionHandler((MethodArgumentTypeMismatchException) ex,request);
        }
        if (ex instanceof MethodArgumentNotValidException) {
            return methodArgumentNotValidExceptionExceptionHandler((MethodArgumentNotValidException) ex,request);
        }
        if (ex instanceof BindException) {
            return validModelBindException((BindException) ex,request);
        }
        if (ex instanceof ConstraintViolationException) {
            return constraintViolationExceptionHandler((ConstraintViolationException) ex,request);
        }
        if (ex instanceof ValidationException) {
            return validationExceptionHandler((ValidationException) ex,request);
        }
        if (ex instanceof NoHandlerFoundException) {
            return noHandlerFoundExceptionHandler((NoHandlerFoundException) ex,request);
        }
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            return httpRequestMethodNotSupportedException((HttpRequestMethodNotSupportedException) ex,request);
        }
        if (ex instanceof BasicException) {
            return basicException((BasicException) ex, request);
        }
        return defaultExceptionHandler(ex, request);
    }

    /**
     * 处理 SpringMVC 请求参数缺失
     * <p>
     * 例如说，接口上设置了 @RequestParam("xx") 参数，结果并未传递 xx 参数
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public <T> ResponseEntity<ApiResponse<T>> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException ex, HttpServletRequest request) {
        logger.warn("[missingServletRequestParameterExceptionHandler]", ex);
        recordErrorMsg(ex, request);
        return ApiResponse.fail(AppHttpStatus.API400_BAD_REQUEST_EXCEPTION.getStatus(), String.format("请求参数缺失:%s", ex.getParameterName()));
    }

    /**
     * 处理 SpringMVC 请求参数类型错误
     * <p>
     * 例如说，接口上设置了 @RequestParam("xx") 参数为 Integer，结果传递 xx 参数类型为 String
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public <T> ResponseEntity<ApiResponse<T>> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException ex,HttpServletRequest request) {
        logger.warn("[missingServletRequestParameterExceptionHandler]", ex);
        recordErrorMsg(ex, request);
        return ApiResponse.fail(AppHttpStatus.API400_BAD_REQUEST_EXCEPTION.getStatus(), String.format("请求参数类型错误:%s", ex.getMessage()));
    }

    /**
     * 处理 SpringMVC 参数校验不正确
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public <T> ResponseEntity<ApiResponse<T>> methodArgumentNotValidExceptionExceptionHandler(MethodArgumentNotValidException ex,HttpServletRequest request) {
        logger.warn("[methodArgumentNotValidExceptionExceptionHandler]", ex);
        FieldError fieldError = ex.getBindingResult().getFieldError();
        // 断言，避免告警
        assert fieldError != null;
        recordErrorMsg(ex, request);

        return ApiResponse.fail(AppHttpStatus.API400_BAD_REQUEST_EXCEPTION.getStatus(), String.format("请求参数不正确:%s", fieldError.getDefaultMessage()));
    }

    /**
     * 处理 Validator 校验不通过产生的异常
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public <T> ResponseEntity<ApiResponse<T>> constraintViolationExceptionHandler(ConstraintViolationException ex,HttpServletRequest request) {
        logger.warn("[constraintViolationExceptionHandler]", ex);
        recordErrorMsg(ex, request);
        ConstraintViolation<?> constraintViolation = ex.getConstraintViolations().iterator().next();
        return ApiResponse.fail(AppHttpStatus.API400_BAD_REQUEST_EXCEPTION.getStatus(), String.format("请求参数不正确:%s", constraintViolation.getMessage()));
    }

    /**
     * 处理 Dubbo Consumer 本地参数校验时，抛出的 ValidationException 异常
     */
    @ExceptionHandler(value = ValidationException.class)
    public <T> ResponseEntity<ApiResponse<T>> validationExceptionHandler(ValidationException ex,HttpServletRequest request) {
        logger.warn("[validationExceptionHandler]", ex);
        recordErrorMsg(ex, request);
        // 无法拼接明细的错误信息
        return ApiResponse.fail(AppHttpStatus.API400_BAD_REQUEST_EXCEPTION,HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理 SpringMVC 请求地址不存在
     * <p>
     * 注意，它需要设置如下两个配置项：
     * 1. spring.mvc.throw-exception-if-no-handler-found 为 true
     * 2. spring.mvc.static-path-pattern 为 /statics/**
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public <T> ResponseEntity<ApiResponse<T>> noHandlerFoundExceptionHandler(NoHandlerFoundException ex, HttpServletRequest request) {
        recordErrorMsg(ex, request);
        return ApiResponse.fail(AppHttpStatus.API404_EXCEPTION.getStatus(), String.format("请求地址不存在:%s", ex.getRequestURL()));
    }

    /**
     * API-请求method不匹配
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public  <T> ResponseEntity<ApiResponse<T>> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return ApiResponse.fail(AppHttpStatus.ILLEGAL_METHOD.getStatus(),AppHttpStatus.ILLEGAL_METHOD.getMessage());
    }


    /**
     * 未知异常 处理系统异常，兜底处理所有的一切
     */
    @ExceptionHandler(value = Exception.class)
    public <T> ResponseEntity<ApiResponse<T>> defaultExceptionHandler(Throwable e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return  ApiResponse.fail(e.getMessage());
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(value = RuntimeException.class)
    public <T> ResponseEntity<ApiResponse<T>> runtimeExceptionHandler(RuntimeException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return ApiResponse.fail(e.getMessage());
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public  <T> ResponseEntity<ApiResponse<T>> nullPointerExceptionHandler(NullPointerException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return ApiResponse.fail(AppHttpStatus.NULL_POINTER.getStatus(),AppHttpStatus.NULL_POINTER.getMessage());
    }

    /**
     * 类型转换异常
     */
    @ExceptionHandler(ClassCastException.class)
    public  <T> ResponseEntity<ApiResponse<T>> classCastExceptionHandler(ClassCastException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return ApiResponse.fail(AppHttpStatus.ILLEGAL_CLASS_CONVERT.getStatus(),AppHttpStatus.ILLEGAL_CLASS_CONVERT.getMessage());
    }

    /**
     * IO异常
     */
    @ExceptionHandler(IOException.class)
    public  <T> ResponseEntity<ApiResponse<T>> ioExceptionHandler(IOException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return ApiResponse.fail(AppHttpStatus.IO_EXCEPTION.getStatus(),AppHttpStatus.IO_EXCEPTION.getMessage());
    }

    /**
     * 数组越界异常
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public  <T> ResponseEntity<ApiResponse<T>> indexOutOfBoundsException(IndexOutOfBoundsException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return ApiResponse.fail(AppHttpStatus.ILLEGAL_INDEX.getStatus(),AppHttpStatus.ILLEGAL_INDEX.getMessage());
    }

    /**
     * API-参数类型不匹配
     */
    @ExceptionHandler(TypeMismatchException.class)
    public  <T> ResponseEntity<ApiResponse<T>> requestTypeMismatch(TypeMismatchException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return ApiResponse.fail(AppHttpStatus.MISMATCH_PARAMETER,HttpStatus.BAD_REQUEST);
    }

    /**
     * API-缺少参数
     */
    @ExceptionHandler(MissingRequestValueException.class)
    public  <T> ResponseEntity<ApiResponse<T>> requestMissingServletRequest(MissingRequestValueException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return ApiResponse.fail(AppHttpStatus.MISSING_PARAMETER,HttpStatus.BAD_REQUEST);
    }


    /**
     * API-控制器方法参数Validate异常
     *
     * @throws BindException
     * @throws MethodArgumentNotValidException
     */
    @ExceptionHandler({BindException.class, IllegalArgumentException.class, HttpMessageConversionException.class})
    public  <T> ResponseEntity<ApiResponse<T>> validModelBindException(Exception e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return ApiResponse.fail(AppHttpStatus.ILLEGAL_PARAMETER,HttpStatus.BAD_REQUEST);
    }


    /**
     * 数字格式异常
     */
    @ExceptionHandler(NumberFormatException.class)
    public  <T> ResponseEntity<ApiResponse<T>> numberFormatException(NumberFormatException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return ApiResponse.fail(AppHttpStatus.ILLEGAL_NUMBER_FORMAT,HttpStatus.BAD_REQUEST);
    }

    /**
     * 业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BasicException.class)
    public  <T> ResponseEntity<ApiResponse<T>> basicException(BasicException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return ApiResponse.fail(e.getStatus(), e.getMessage());
    }


    /**
     * 获取异常堆栈信息并记录到error文件中
     */
    private static void recordErrorMsg(Throwable ex, HttpServletRequest request) {
        String errorMsg = PrintExceptionInfo.printErrorInfo(ex);
        if (ex instanceof BasicException) {
            BasicException systemException = (BasicException) ex;
            errorMsg = MessageFormat.format("业务异常，异常码是【{0}】，异常消息是【{1}】，异常详情{2}", systemException.getStatus(), systemException.getMessage(), errorMsg);
        }
        logger.error("recordErrorMsg: {}" ,errorMsg);
        //记录错误日志
        recordErrorLogger(request, errorMsg);
    }

    /**
     * 记录错误日志
     *
     * @param request
     * @param errorMsg
     */
    private static void recordErrorLogger(HttpServletRequest request, String errorMsg) {
        if (!ContextHolder.get().getStage().equals(ContextHolder.Stage.MAPPING)) {
            return;
        }
        try {
            BaseLogger baseLogger = new BaseLogger();
            //系统编号
            baseLogger.setSystemNumber(ContextHolder.get().getSystemNumber());
            //事务唯一编号
            baseLogger.setTraceId(ContextHolder.get().getTraceId());
            //请求URL
            threadLocal.set(request.getRequestURI());
            baseLogger.setUrl(getAndRemove());
            //客户端IP
            baseLogger.setClientIp(ContextHolder.get().getClientIp());
            //服务端IP
            baseLogger.setServerIp(ContextHolder.get().getServerIp());
            //触发时间
            baseLogger.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
            //请求参数
            baseLogger.setRequestParams(RequestHelper.getApiParamsMap());
            //响应体
            baseLogger.setBody(errorMsg);
            //耗时(未处理任何逻辑)
            baseLogger.setTime(0L);
            //记录日志到文件
            logger.error("异常请求 {}" ,JSONUtils.toJSONString(baseLogger));
        } catch (Exception exception) {
            logger.error(MessageFormat.format("记录错误日志异常：{0}", PrintExceptionInfo.printErrorInfo(exception)));
        } finally {
            //移除线程上下文对应的变量
            ContextHolder.remove();
        }
    }

    public static String getAndRemove(){
        try {
            return threadLocal.get();
        }finally {
            threadLocal.remove();
        }
    }

}

