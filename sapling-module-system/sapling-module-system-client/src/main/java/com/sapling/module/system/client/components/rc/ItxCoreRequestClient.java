package com.sapling.module.system.client.components.rc;

import com.dtflys.forest.annotation.*;
import com.sapling.module.system.client.components.rc.interceptors.RequestSignInterceptor;
import com.sapling.module.system.client.components.rc.interceptors.ServiceDiscoveryInterceptor;

@ForestClient
@BaseRequest(
        baseURL = "${itx_core_url}",
        interceptor = {RequestSignInterceptor.class, ServiceDiscoveryInterceptor.class},
        headers = {
                "Accept-Charset: utf-8",
                "Content-Type: application/json",
                "Content-Encoding: gzip",
                "Accept-Encoding: gzip,deflate"
        }
)
@DecompressGzip
@Retry(maxRetryCount = "2", maxRetryInterval = "2000")
public interface ItxCoreRequestClient {
    @Post("/current_login_info")
    String currentLoginInfo(@Query("session_id") String sessionId, @Header("zmct") String zmct);

    @Post("/keep_session")
    void keepSession(@Query("session_id") String sessionId, @Header("zmct") String zmct);
}
