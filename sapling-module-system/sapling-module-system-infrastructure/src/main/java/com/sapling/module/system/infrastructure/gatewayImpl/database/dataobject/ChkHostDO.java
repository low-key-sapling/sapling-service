package com.sapling.module.system.infrastructure.gatewayImpl.database.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 主机检查数据对象
 * 
 * @author mbws
 */
@Data
@TableName("chkproof.tb_chk_host")
public class ChkHostDO {
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 主机名称
     */
    @TableField("hostName")
    private String hostName;
    
    /**
     * 主机ID地址
     */
    @TableField("hostId")
    private String hostId;

}