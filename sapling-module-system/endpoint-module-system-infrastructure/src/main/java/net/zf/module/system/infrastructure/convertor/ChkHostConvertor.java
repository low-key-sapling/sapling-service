package net.zf.module.system.infrastructure.convertor;

import net.zf.module.system.client.biz.host.dto.ChkHostDto;
import net.zf.module.system.domain.biz.host.model.ChkHostEntity;
import net.zf.module.system.infrastructure.gatewayImpl.database.dataobject.ChkHostDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 主机检查转换器
 * 
 * @author mbws
 */
@Mapper
public interface ChkHostConvertor {
    
    ChkHostConvertor INSTANCE = Mappers.getMapper(ChkHostConvertor.class);
    
    /**
     * DO转Entity
     * 
     * @param chkHostDO 数据对象
     * @return 领域实体
     */
    ChkHostEntity toEntity(ChkHostDO chkHostDO);
    
    /**
     * Entity转DTO（演示各种映射方式）
     * 
     * @param chkHostEntity 领域实体
     * @return 数据传输对象
     */
    // 如果字段名不同，可以这样映射：
    // @Mapping(source = "hostName", target = "name")
    // @Mapping(source = "hostIp", target = "ipAddress")
    // @Mapping(source = "orgId", target = "organizationId")
    // @Mapping(target = "fullInfo", expression = "java(chkHostEntity.getHostName() + \"(\" + chkHostEntity.getHostIp() + \")\")")
    // @Mapping(target = "isOnline", expression = "java(chkHostEntity.getStatus() != null && chkHostEntity.getStatus() == 1)")
    // @Mapping(target = "version", constant = "1.0")
    // @Mapping(target = "source", constant = "SYSTEM")
    // @Mapping(target = "internalField", ignore = true)
    ChkHostDto toDto(ChkHostEntity chkHostEntity);

    /**
     * Entity列表转DTO列表
     * 
     * @param chkHostEntityList 领域实体列表
     * @return 数据传输对象列表
     */
    List<ChkHostDto> toDtoList(List<ChkHostEntity> chkHostEntityList);

}