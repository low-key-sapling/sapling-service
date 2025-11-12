package com.sapling.module.system.client.components.rc.interceptors;

import cn.hutool.core.util.ObjectUtil;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.reflection.ForestMethod;
import lombok.extern.slf4j.Slf4j;
import net.zfsy.RegistryCenter;

/**
 * Forest服务发现拦截器
 */
@Slf4j
public class ServiceDiscoveryInterceptor<T> implements Interceptor<T> {

    /**
     * 该方法在请求发送之前被调用, 若返回false则不会继续发送请求
     */
    @Override
    public boolean beforeExecute(ForestRequest request) {
        try {
            // 获取服务地址
            String url = request.getUrl();

            // 获取URL中的服务名称，url格式为：http://mbc-zfmg/ZFMG/api/v1/device/register
            String serviceName = getServiceName(url);

            // 获取服务地址
            if (isServiceName(serviceName)) {
                // 查询服务地址，即mbc-zfmg对应的ip端口：10.11.100.132:8888
                String serviceAddress = RegistryCenter.getInstance(serviceName);
                if (ObjectUtil.isNotEmpty(serviceAddress)) {
                    // 替换URL
                    url = url.replace(serviceName, serviceAddress);
                }
            }

            // 重置URL
            request.setUrl(url);
            return true;
        } catch (IllegalArgumentException e) {
            // 记录错误日志但不中断请求
            log.error("服务发现拦截器处理URL时发生错误: {}", e.getMessage());
            return true; // 继续执行请求，使用原始URL
        } catch (Exception e) {
            // 记录其他未预期的异常
            log.error("服务发现拦截器发生未预期的错误", e);
            return true; // 继续执行请求，使用原始URL
        }
    }

    /**
     * 判断是否是服务名称
     * 服务名称的特征：
     * 1. 不包含端口号分隔符 ":"
     * 2. 不包含域名分隔符 "."
     *
     * @param serviceName 待判断的服务名称
     * @return true表示是服务名称，false表示是IP地址或域名
     */
    private boolean isServiceName(String serviceName) {
        // 输入参数验证
        if (serviceName == null || serviceName.trim().isEmpty()) {
            return false;
        }

        String trimmedServiceName = serviceName.trim();

        // 检查是否包含端口号分隔符（如：localhost:8080）
        return !trimmedServiceName.contains(":") && !trimmedServiceName.contains(".");
    }


    /**
     * 获取服务名称
     *
     * @param url 完整的URL地址，格式如：http://mbc-zfmg/ZFMG/api/v1/device/register
     * @return 服务名称，如：mbc-zfmg
     * @throws IllegalArgumentException 当URL格式无效时抛出
     */
    private String getServiceName(String url) {
        // 输入参数验证
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL不能为空");
        }

        String trimmedUrl = url.trim();

        // 检查URL是否包含协议分隔符
        int protocolIndex = trimmedUrl.indexOf("://");
        if (protocolIndex == -1) {
            throw new IllegalArgumentException("无效的URL格式，缺少协议分隔符 '://': " + url);
        }

        // 提取协议后的部分（去掉协议前缀）
        String urlWithoutProtocol = trimmedUrl.substring(protocolIndex + 3);

        // 检查是否包含路径分隔符
        int pathSeparatorIndex = urlWithoutProtocol.indexOf("/");
        if (pathSeparatorIndex == -1) {
            // 如果没有路径分隔符，整个剩余部分就是服务名称
            return urlWithoutProtocol;
        }

        // 提取服务名称（协议后到第一个路径分隔符之间的部分）
        String serviceName = urlWithoutProtocol.substring(0, pathSeparatorIndex);

        // 验证提取的服务名称不为空
        if (serviceName.isEmpty()) {
            throw new IllegalArgumentException("无法从URL中提取服务名称: " + url);
        }

        return serviceName;
    }


    /**
     * 该方法在被调用时，并在beforeExecute前被调用
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