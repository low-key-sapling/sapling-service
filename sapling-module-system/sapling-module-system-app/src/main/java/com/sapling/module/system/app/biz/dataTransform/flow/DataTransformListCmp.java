package com.sapling.module.system.app.biz.dataTransform.flow;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.infrastructure.common.slot.SystemContext;

import java.util.List;

/**
 * dataTransformList: 遍历 esChildDocsList 做字段映射转换，生成 outputJsonArrary（JSON 数组字符串）。
 */
@Slf4j
@LiteflowComponent("dataTransformList")
public class DataTransformListCmp extends NodeComponent {

    @Override
    public void process() throws Exception {
        SystemContext ctx = this.getFirstContextBean();
        List<JSONObject> esChildDocsList = ctx.get("esChildDocsList");
        JSONArray outputArray = new JSONArray();
        for (JSONObject inputJson : esChildDocsList) {
            // 业务字段映射：提取所需字段并标准化命名
            JSONObject outputJson = new JSONObject();

            String fileName = inputJson.getString("fileName");
            String md5 = firstNonEmpty(
                    inputJson.getString("realmd5"),
                    inputJson.getString("copymd5"),
                    inputJson.getString("current_location_md5")
            );
            String filePath = inputJson.getString("filePath");
            String source = firstNonEmpty(
                    inputJson.getString("dataSource"),
                    inputJson.getString("datasource"),
                    inputJson.getString("source_type")
            );
            String orgPath = inputJson.getString("orgPath");
            String userName = inputJson.getString("userName");
            String ip = firstNonEmpty(
                    inputJson.getString("srcip"),
                    inputJson.getString("ip")
            );
            String reportTime = firstNonEmpty(
                    inputJson.getString("updateTime"),
                    inputJson.getString("sendTime"),
                    inputJson.getString("accessTime"),
                    inputJson.getString("es_access_time")
            );
            String judgeLevel = firstNonEmpty(
                    inputJson.getString("mgLevel"),
                    inputJson.getString("realLevelInfo"),
                    inputJson.getString("documentclass")
            );
            String judgeTime = inputJson.getString("judgeTime");
            String isCleared = firstNonEmpty(
                    toStringSafe(inputJson.get("iscleared")),
                    toStringSafe(inputJson.get("is_file_delete"))
            );

            outputJson.put("alarm_file_name", nullToEmpty(fileName));
            outputJson.put("alarm_md5", nullToEmpty(md5));
            outputJson.put("alarm_src_location", nullToEmpty(filePath));
            outputJson.put("alarm_source", nullToEmpty(source));
            outputJson.put("alarm_company", nullToEmpty(orgPath));
            outputJson.put("user_name", nullToEmpty(userName));
            outputJson.put("host_ip", nullToEmpty(ip));
            outputJson.put("alarm_updatetime", nullToEmpty(reportTime));
            outputJson.put("judge_mglevel", nullToEmpty(judgeLevel));
            outputJson.put("judge_time", nullToEmpty(judgeTime));
            outputJson.put("alarm_iscleared", nullToEmpty(isCleared));

            outputArray.add(outputJson);
        }
        ctx.set("outputJsonArray", outputArray);
    }

    private String firstNonEmpty(String... values) {
        if (values == null) return "";
        for (String v : values) {
            if (v != null && v.trim().length() > 0) return v;
        }
        return "";
    }

    private String nullToEmpty(String v) {
        return v == null ? "" : v;
    }

    private String toStringSafe(Object v) {
        return v == null ? "" : String.valueOf(v);
    }
}
