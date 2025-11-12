package com.sapling.module.system.client.components.mq.dto;


public interface MessagePayload   {


    /**
     * 消息内容
     * 转换终端上报的原始数据
     * @param content
     * @return
     */
     String contentToJson(Object content);

     /**
      * 转换为BusMessage
      * @return
      */
     String busMessageToJson();

}