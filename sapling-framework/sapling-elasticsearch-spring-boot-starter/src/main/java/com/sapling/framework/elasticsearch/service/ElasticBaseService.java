package com.sapling.framework.elasticsearch.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.client.ClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author 小工匠
 * @version 1.0
 * @description: ElasticBaseService 基类
 * @date 2022/4/4 23:05
 * @mark: show me the code , change the world
 */
public class ElasticBaseService<T> extends AbstractElasticBaseService<T> {

    private static final Logger log = LoggerFactory.getLogger(ElasticBaseService.class);

    @Autowired
    private BBossESStarter bossESStarter;

    /**
     * Xml创建索引
     */
    public String createIndexByXml(String propertyName) {
        //创建加载配置文件的客户端工具，单实例多线程安全
        ClientInterface restClient = bossESStarter.getConfigRestClient(xmlPath);
        //判读索引表是否存在，存在返回true，不存在返回false
        boolean exist = restClient.existIndice(this.indexName);

        //如果索引表存在，返回索引结构aa
        if (exist) {
            log.warn("{} 已经存在,请勿重复创建索引,本次返回已存在的索引Mapping", this.indexName);
            return restClient.getIndice(this.indexName);
        }
        //创建索引表
        restClient.createIndiceMapping(this.indexName, propertyName);
        //获取最新建立的索引表结构
        return restClient.getIndice(this.indexName);
    }

    /**
     * 自动创建索引
     */
    public void createIndex() {
        ClientInterface restClient = bossESStarter.getRestClient();
        boolean exist = restClient.existIndice(this.indexName);
        //如果索引表存在，返回索引结构aa
        if (exist) {
            log.warn("{} 已经存在,请勿重复创建索引,本次返回已存在的索引Mapping", this.indexName);
            restClient.getIndice(this.indexName);
            return;
        }
        log.info("创建索引：" + this.mapping);
        restClient.executeHttp(indexName, this.mapping, ClientUtil.HTTP_PUT);
    }

    /**
     * actionName xml中创建模板的方法名称,默认：createTemplate
     * 自动创建索引
     */
    public void createIndexTemplate(String actionName) {
        ClientInterface restClient = bossESStarter.getConfigRestClient(this.xmlPath);
        //判断模板是否创建,未创建开始创建模板
        String allTemplateResult = restClient.executeHttp("/_template/", ClientUtil.HTTP_GET);
        JSONObject allTemplateSet = JSONUtil.parseObj(allTemplateResult);
        if (!allTemplateSet.containsKey(this.indexTemplateName)) {
            try {
                //创建模板
                String templateNameResult = restClient.createTempate(this.indexTemplateName, StrUtil.isEmpty(actionName) ? "createTemplate" : actionName);
                log.info("创建模板{}成功：{}", this.indexTemplateName, templateNameResult);
            } catch (Exception e) {
                log.error("创建模板{}失败", this.indexTemplateName, e);
            }
        }
    }

    /**
     * 删除索引
     */
    public String delIndex() {
        return bossESStarter.getRestClient().dropIndice(this.indexName);
    }

    /**
     * 添加文档
     *
     * @param t       实体类
     * @param refresh 是否强制刷新
     */
    public String addDocument(T t, Boolean refresh) {
        return bossESStarter.getRestClient().addDocument(indexName, indexType, t, "refresh=" + refresh);
    }

    /**
     * 添加文档
     *
     * @param ts      实体类集合
     * @param refresh 是否强制刷新
     */
    public String addDocuments(List<T> ts, Boolean refresh) {
        return bossESStarter.getRestClient().addDocuments(indexName, indexType, ts, "refresh=" + refresh);
    }

    /**
     * 分页-添加文档集合
     *
     * @param ts      实体类集合
     * @param refresh 是否强制刷新
     */
    public void addDocumentsOfPage(List<T> ts, Boolean refresh) {
        this.delIndex();
        this.createIndex();
        int start = 0;
        int rows = 100;
        int size;
        do {
            List<T> list = pageDate(start, rows);
            if (!list.isEmpty()) {
                //批量同步信息
                bossESStarter.getRestClient().addDocuments(indexName, indexType, ts, "refresh=" + refresh);
            }
            size = list.size();
            start += size;
        } while (size > 0);
    }

    /**
     * 使用分页添加文档必须重写该类
     *
     * @param start 起始
     * @param rows  项数
     * @return
     */
    public List<T> pageDate(int start, int rows) {
        return null;
    }

    /**
     * 删除文档
     *
     * @param id      id
     * @param refresh 是否强制刷新
     * @return
     */
    public String delDocument(String id, Boolean refresh) {
        return bossESStarter.getRestClient().deleteDocument(indexName, indexType, id, "refresh=" + refresh);
    }

    /**
     * 删除文档
     *
     * @param ids     id集合
     * @param refresh 是否强制刷新
     * @return
     */
    public String delDocuments(String[] ids, Boolean refresh) {
        return bossESStarter.getRestClient().deleteDocumentsWithrefreshOption(indexName, indexType, "refresh=" + refresh, ids);
    }

    /**
     * id获取文档
     *
     * @param id
     * @return
     */
    public T getDocument(String id, Class<T> clazz) {
        return bossESStarter.getRestClient().getDocument(indexName, indexType, id, clazz);
    }

    /**
     * id更新文档
     *
     * @param t       实体
     * @param refresh 是否强制刷新
     * @return
     */
    public String updateDocument(String id, T t, Boolean refresh) {
        return bossESStarter.getRestClient().updateDocument(indexName, indexType, id, t, "refresh=" + refresh);
    }

}
    