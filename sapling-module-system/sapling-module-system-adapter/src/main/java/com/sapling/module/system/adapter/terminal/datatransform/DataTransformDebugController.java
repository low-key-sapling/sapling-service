package com.sapling.module.system.adapter.terminal.datatransform;

import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.app.biz.dataTransform.executor.DataTransformExe;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Debug controller for running dataTransformScriptChain.
 * Supports passing full input string directly or (filePath,fileName) pair.
 */
@Slf4j
@RestController
@RequestMapping("/debug")
public class DataTransformDebugController {

    @Resource
    private DataTransformExe dataTransformExe;


    @PostMapping(path = "/data-transform", consumes = {"text/plain", "application/json"})
    public ResponseEntity<Map<String, Object>> postTransform(@RequestBody(required = true) String inputString) {
        // Directly use raw string as the only input
        Map<String, Object> result = new HashMap<>();
        try {
            String outputJson = dataTransformExe.transformInputString(inputString);
            boolean success = outputJson != null && outputJson.length() > 2;
            result.put("executed", true);
            result.put("outputJson", outputJson);
            result.put("outputSuccess", success);
        } catch (Exception e) {
            log.error("[debug] dataTransformScriptChain error", e);
            result.put("executed", false);
            result.put("exception", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
}
