package com.sapling.module.system.infrastructure.gatewayImpl.transform;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.domain.biz.transform.gateway.TransformGateway;
import com.sapling.module.system.infrastructure.common.constants.KafkaTopicConstants;
import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuanjifan
 * @description 转换数据获取数据实现
 * @date 2025年10月22日 9:21
 */
@Slf4j
@Component
public class TransformGatewayImpl implements TransformGateway {

    @Resource(name = "shsjKafkaTemplate")
    private KafkaTemplate kafkaTemplate;

    @Resource
    public BBossESStarter bossESStarter;

    public String xmlPath = "esmapper/gj_zfmg_2_1_4.xml";


    @Override
    public List<JSONObject> getWJFileInfoByFileCode(List<String> fileCode) {
        List<JSONObject> result = new ArrayList<>();
        if (fileCode == null || fileCode.isEmpty()) {
            return result;
        }
        try {
            ClientInterface clientUtil = this.bossESStarter.getConfigRestClient(this.xmlPath);

            // 首次请求（无 afterKey）
            java.util.Map<String, Object> params = new java.util.HashMap<>();
            params.put("fileCodes", fileCode);
            String resp = clientUtil.executeRequest("gj_zfmg_2_1_4_group*/_search","queryWJChildByFileCodeWithAfter", params);
            JSONObject afterKey = parseAggAndCollect(resp, result);

            // 迭代翻页，请求直到没有 after_key
            while (afterKey != null && !afterKey.isEmpty()) {
                java.util.Map<String, Object> nextParams = new java.util.HashMap<>();
                nextParams.put("fileCodes", fileCode);
                nextParams.put("afterKey", afterKey);
                String nextResp = clientUtil.executeRequest("gj_zfmg_2_1_4_group*/_search","queryWJChildByFileCodeWithAfter", nextParams);
                afterKey = parseAggAndCollect(nextResp, result);
            }
        } catch (Exception e) {
            log.error("getWJFileInfoByFileCode error: {}", e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<JSONObject> getMGFileInfoByFileCode(List<String> fileCode) {
        List<JSONObject> result = new ArrayList<>();
        if (fileCode == null || fileCode.isEmpty()) {
            return result;
        }
        try {
            ClientInterface clientUtil = this.bossESStarter.getConfigRestClient(this.xmlPath);

            // 首次请求（无 afterKey）
            java.util.Map<String, Object> params = new java.util.HashMap<>();
            params.put("fileCodes", fileCode);
            String resp = clientUtil.executeRequest("gj_zfmg_2_1_4/_search","queryChildByFileCodeWithAfter", params);
            JSONObject afterKey = parseAggAndCollect(resp, result);

            // 迭代翻页，请求直到没有 after_key
            while (afterKey != null && !afterKey.isEmpty()) {
                java.util.Map<String, Object> nextParams = new java.util.HashMap<>();
                nextParams.put("fileCodes", fileCode);
                nextParams.put("afterKey", afterKey);
                String nextResp = clientUtil.executeRequest("gj_zfmg_2_1_4/_search","queryChildByFileCodeWithAfter", nextParams);
                afterKey = parseAggAndCollect(nextResp, result);
            }
        } catch (Exception e) {
            log.error("getMGFileInfoByFileCode error: {}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 解析聚合结果，收集每个分组的 top_hits 第一条 _source 到 result，并返回 after_key（如存在）。
     */
    private JSONObject parseAggAndCollect(String resp, List<JSONObject> result) {
        if (resp == null || resp.isEmpty()) {
            return null;
        }
        JSONObject json = JSON.parseObject(resp);
        JSONObject aggs = json.getJSONObject("aggregations");
        if (aggs == null) return null;
        JSONObject by = aggs.getJSONObject("by_host_file");
        if (by == null) return null;
        JSONArray buckets = by.getJSONArray("buckets");
        if (buckets != null) {
            for (Object b : buckets) {
                JSONObject bucket = (JSONObject) b;
                JSONObject one = bucket.getJSONObject("one");
                if (one == null) continue;
                JSONObject hits = one.getJSONObject("hits");
                if (hits == null) continue;
                JSONArray subHits = hits.getJSONArray("hits");
                if (subHits == null || subHits.isEmpty()) continue;
                JSONObject hit = subHits.getJSONObject(0);
                JSONObject source = hit.getJSONObject("_source");
                if (source != null) result.add(source);
            }
        }
        return by.getJSONObject("after_key");
    }

    @Override
    public void sendMessage(String message) {
        kafkaTemplate.send(KafkaTopicConstants.MBWS_FILE_ALARM_TARGET_TOPIC, message).addCallback(
                result -> log.info("MBWS文件判定消息发送完成-topic：{}，result：{}", KafkaTopicConstants.MBWS_FILE_ALARM_TARGET_TOPIC, result),
                ex -> log.error("MBWS文件判定消息发送异常-topic：{}，error：{}", KafkaTopicConstants.MBWS_FILE_ALARM_TARGET_TOPIC, ex.getMessage(), ex));

    }
}
