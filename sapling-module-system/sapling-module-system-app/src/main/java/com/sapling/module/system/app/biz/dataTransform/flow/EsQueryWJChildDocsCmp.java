package com.sapling.module.system.app.biz.dataTransform.flow;

import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.client.biz.host.IChkHostService;
import com.sapling.module.system.client.biz.host.dto.ChkHostDto;
import com.sapling.module.system.domain.biz.transform.gateway.TransformGateway;
import com.sapling.module.system.infrastructure.common.slot.SystemContext;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@LiteflowComponent("esQueryWJChildDocs")
public class EsQueryWJChildDocsCmp extends NodeComponent {

    @Resource
    private TransformGateway transformGateway;
    @Resource
    private IChkHostService chkHostService;

    @Override
    public void process() throws Exception {
        List<Object> esChildDocsList = new ArrayList<>();

        SystemContext ctx = this.getFirstContextBean();

        List<String> fileCodes = ctx.get("fileCodes");
        if (fileCodes == null || fileCodes.isEmpty()) {
            log.warn("LiteFlow esQueryWJChildDocs: fileCodes is empty");
            ctx.set("esChildDocsList", esChildDocsList);
            return;
        }

        log.info("开始查询WJ文件信息，fileCodes数量: {}", fileCodes.size());

        // 1. 获取文件信息
        List<JSONObject> fileInfoList = transformGateway.getWJFileInfoByFileCode(fileCodes);
        if (fileInfoList == null || fileInfoList.isEmpty()) {
            log.warn("未查询到WJ文件信息");
            ctx.set("esChildDocsList", esChildDocsList);
            return;
        }

        log.info("查询到WJ文件信息数量: {}", fileInfoList.size());

        // 2. 提取hostId字段集合
        List<String> hostIds = extractHostIds(fileInfoList);
        if (hostIds.isEmpty()) {
            log.warn("未提取到有效的hostId");
            ctx.set("esChildDocsList", fileInfoList);
            return;
        }

        log.info("提取到hostId数量: {}", hostIds.size());

        // 3. 批量查询主机信息
        List<ChkHostDto> hostInfoList = chkHostService.listByHostIds(hostIds);
        log.info("查询到主机信息数量: {}", hostInfoList.size());

        // 4. 构建hostId到主机信息的映射
        Map<String, ChkHostDto> hostInfoMap = hostInfoList.stream()
                .collect(Collectors.toMap(ChkHostDto::getHostId, Function.identity()));

        // 5. 将主机信息合并到文件信息中
        List<JSONObject> enrichedFileInfoList = enrichFileInfoWithHostInfo(fileInfoList, hostInfoMap);

        log.info("完成文件信息与主机信息合并，最终数量: {}", enrichedFileInfoList.size());

        ctx.set("esChildDocsList", enrichedFileInfoList);
    }

    /**
     * 从文件信息列表中提取hostId字段集合
     *
     * @param fileInfoList 文件信息列表
     * @return hostId集合
     */
    private List<String> extractHostIds(List<JSONObject> fileInfoList) {
        return fileInfoList.stream()
                .map(f -> f.getString("hostId"))
                .filter(Objects::nonNull)
                .distinct()  // 去重
                .collect(Collectors.toList());
    }


    /**
     * 将主机信息合并到文件信息中
     *
     * @param fileInfoList 文件信息列表
     * @param hostInfoMap  hostId到主机信息的映射
     * @return 合并后的文件信息列表
     */
    private List<JSONObject> enrichFileInfoWithHostInfo(List<JSONObject> fileInfoList,
                                                        Map<String, ChkHostDto> hostInfoMap) {
        return fileInfoList.stream()
                .map(fileInfo -> enrichSingleFileInfo(fileInfo, hostInfoMap))
                .collect(Collectors.toList());
    }

    /**
     * 为单个文件信息添加主机信息
     *
     * @param fileInfo    文件信息
     * @param hostInfoMap 主机信息映射
     * @return 合并后的文件信息
     */
    private JSONObject enrichSingleFileInfo(JSONObject fileInfo, Map<String, ChkHostDto> hostInfoMap) {
        try {
            // 创建新的JSONObject以避免修改原对象
            JSONObject enrichedFileInfo = new JSONObject(fileInfo);

            String hostId = fileInfo.getString("hostId");
            if (hostId != null && hostInfoMap.containsKey(hostId)) {
                ChkHostDto hostInfo = hostInfoMap.get(hostId);

                // 添加主机信息到文件信息中
                enrichedFileInfo.put("hostInfo", JSONObject.parseObject(JSONObject.toJSONString(hostInfo)));

                // 也可以将主机的关键信息直接添加到根级别
                enrichedFileInfo.put("hostName", hostInfo.getHostName());


                log.debug("为文件信息添加主机信息成功，hostId: {}, hostName: {}",
                        hostId, hostInfo.getHostName());
            } else {
                log.debug("未找到对应的主机信息，hostId: {}", hostId);
                // 添加空的主机信息标识
                enrichedFileInfo.put("hostInfo", null);
                enrichedFileInfo.put("hostFound", false);
            }

            return enrichedFileInfo;

        } catch (Exception e) {
            log.error("合并文件信息和主机信息时发生异常: {}", fileInfo, e);
            // 发生异常时返回原始文件信息
            return fileInfo;
        }
    }
}
