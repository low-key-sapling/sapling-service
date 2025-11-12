package com.sapling.framework.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * chkproof响应结构
 */
@Slf4j
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChkProofResponse<T> implements Serializable {

    public Integer totalPage;

    public Integer totalRows;

    public Integer total;

    public String rows;

    @Data
    public static class OrgInfo implements Serializable {
        //组织机构id  12sf81-ahf123a-1dfs23oa-asdf81
        private String orgId;
        //组织名称  A公司/B单位/C部门/D科室
        private String orgName;
        //是否本机组织  1本级  0非本级
        private String isCurrent;
    }

    @Data
    public static class HostInfo implements Serializable {
        //终端feature
        private String featureCode;
        //终端id  12sf81-ahf123a-1dfs23oa-asdf81
        private String hostId;
        //组织名称  A公司/B单位/C部门/D科室
        private String orgName;
        //主机用户名
        private String userName;
        //主机ip
        private String hostIp;
    }

}