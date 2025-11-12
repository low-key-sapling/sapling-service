package com.sapling.module.system.client.components.rc.interceptors;

import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.reflection.ForestMethod;
import lombok.extern.slf4j.Slf4j;
import net.zfsy.RegistryCenter;
import com.sapling.framework.common.exception.BusinessException;
import com.sapling.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.sapling.framework.common.utils.enc.Sm2SignUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.UUID;

/**
 * Forest请求签名拦截器
 * 在HTTP请求发送前自动收集所有参数，调用SM2签名工具，异常时阻止请求。
 */
@Slf4j
public class RequestSignInterceptor<T> implements Interceptor<T> {

    private static final String SIGNATURE_HEADER_NAME = "X-BACK-SIGN";
    private static final String NONCE_HEADER_NAME = "X-NONCE";
    private static final String IDENTITY_HEADER_NAME = "X-IDENTITY";


    /**
     * 该方法在请求发送之前被调用, 若返回false则不会继续发送请求
     *
     * @Param request Forest请求对象
     */
    @Override
    public boolean beforeExecute(ForestRequest request) {
        try {
            // 生成UUID
            String uuid = UUID.randomUUID().toString();
            // 获取唯一标识
            String unicode = RegistryCenter.getUnicode();
            // 生成签名
            String signature = Sm2SignUtil.signRequest(uuid + unicode);
            log.debug("生成的签名：{}，原文uuid：{}", signature, uuid);
            // 添加签名到请求头
            request.addHeader(NONCE_HEADER_NAME, uuid);
            request.addHeader(SIGNATURE_HEADER_NAME, signature);
            request.addHeader(IDENTITY_HEADER_NAME, unicode);
        } catch (Exception e) {
            log.error("请求签名过程中发生未知异常: {}", ExceptionUtils.getStackTrace(e));
            throw new BusinessException(GlobalErrorCodeConstants.FORBIDDEN.getType(), "请求签名过程中发生未知异常");
        }
        return true;  // 继续执行请求返回true
    }


    /**
     * 该方法在被调用时，并在beforeExecute前被调用
     *
     * @Param request Forest请求对象
     * @Param args 方法被调用时传入的参数数组
     */
    @Override
    public void onInvokeMethod(ForestRequest req, ForestMethod method, Object[] args) {
        return;
    }

    /**
     * 在请求体数据序列化后，发送请求数据前调用该方法
     * 默认为什么都不做
     * 注: multlipart/data类型的文件上传格式的 Body 数据不会调用该回调函数
     *
     * @param request     Forest请求对象
     * @param encoder     Forest转换器
     * @param encodedData 序列化后的请求体数据
     */
    @Override
    public byte[] onBodyEncode(ForestRequest request, ForestEncoder encoder, byte[] encodedData) {
        // request: Forest请求对象
        // encoder: 此次转换请求数据的序列化器
        // encodedData: 序列化后的请求体字节数组
        // 返回的字节数组将替换原有的序列化结果
        // 默认不做任何处理，直接返回参数 encodedData
        return encodedData;
    }


    /**
     * 该方法在请求成功响应时被调用
     */
    @Override
    public void onSuccess(T data, ForestRequest req, ForestResponse res) {
        return;
    }

    /**
     * 该方法在请求发送失败时被调用
     */
    @Override
    public void onError(ForestRuntimeException ex, ForestRequest req, ForestResponse res) {
        return;
    }

    /**
     * 该方法在请求发送之后被调用
     */
    @Override
    public void afterExecute(ForestRequest req, ForestResponse res) {
        return;
    }

} 