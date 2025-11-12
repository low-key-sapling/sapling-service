package com.sapling.module.system.infrastructure.common.constants;

public class RedisKeyConstants {
    /**
     * 下划线
     */
    public static final String UNDER_LINE = "_";
    /**
     * 统一前缀
     */
    private static final String KEY_TAG = "epc:";
    /**
     * BUS里的缓存
     */
    private static final String BUS_KEY_PREFIX = KEY_TAG + "bus:";
    /**
     * BIZ里的缓存
     */
    private static final String BIZ_KEY_PREFIX = KEY_TAG + "biz:";

    /**
     * BUS  缓存key
     */
    public static class BusCacheKey {
        /**
         * 缓存前缀
         */
        private static final String CACHE_PREFIX = BUS_KEY_PREFIX + "cache:";
        /**
         * 设备ID缓存
         */
        public static final String DEVICE_ID_DEVICE_INFO = CACHE_PREFIX + "device_info";
        /**
         * 安装码缓存
         */
        public static final String INSTALL_CODE_SET = CACHE_PREFIX + "install_code_set";
        /**
         * Cookie缓存
         */
        public static final String COOKIE_PREFIX = CACHE_PREFIX + "cookies:";
        /**
         * 待持久化设备ID缓存
         */
        public static final String REGISTERING_DEVICE_ID_DEVICE_INFO = CACHE_PREFIX + "registering_device_info";
        /**
         * 待注销设备ID缓存
         */
        public static final String REG_CANCELING_DEVICE_INFO = CACHE_PREFIX + "reg_canceling_device_info";
        /**
         * HostID和SID缓存
         */
        public static final String HOST_ID_SID_MAP = CACHE_PREFIX + "host_id_sid_map";
        /**
         * 组织机构ID-用户名称缓存
         */
        public static final String ORG_ID_USER_NAME_MAP = CACHE_PREFIX + "org_id_user_name_map";
        /**
         * 用户信息MAP
         */
        public static final String USER_INFO_MAP = CACHE_PREFIX + "user_info_map";
        /**
         * 用户信息创建补偿缓存
         */
        public static final String USER_ID_DETECT_ZSET = CACHE_PREFIX + "user_id_detect_zset";
    }

    /**
     * BUS  布隆过滤器
     */
    public static class BusBloomFilterKey {
        /**
         * 缓存前缀
         */
        private static final String BLOOM_FILTER_PREFIX = BUS_KEY_PREFIX + "bloom_filter:";
        /**
         * 流水号缓存
         */
        public static final String NONCE_BLOOM_FILTER_KEY = BLOOM_FILTER_PREFIX + "nonce";

    }

    /**
     * BIZ  缓存key
     */
    public static class BizCacheKey {
        /**
         * 缓存前缀
         */
        private static final String CACHE_PREFIX = BIZ_KEY_PREFIX + "cache:";
        /**
         * 组织机构缓存的最后修改时间
         * 使用hashtag确保与其他组织相关的key在同一个slot
         */
        public static final String ORG_CACHE_LAST_MODIFY_DATE = CACHE_PREFIX + "org_cache_last_modify_date";
        /**
         * 组织机构缓存
         */
        public static final String ORG_WITH_CHILD_LIST_PREFIX = CACHE_PREFIX + "org_with_child_list:";
        /**
         * 组织机构缓存key
         */
        public static final String ORG_CACHE_CHILD_LIST_KEYS = ORG_WITH_CHILD_LIST_PREFIX + "keys";
        /**
         * 组织机构同步锁
         */
        public static final String ORG_SYNC_LOCK_KEY = CACHE_PREFIX + "org_sync_lock_key";
        /**
         * 组织机构准入码和组织机构ID的映射关系
         */
        public static final String ORG_ACCESS_CODE_ORG_ID_KEY = CACHE_PREFIX + "org_access_code_org_id";

        /**
         * 策略信息同步锁
         */
        public static final String POLICY_SYNC_LOCK_KEY = CACHE_PREFIX + "policy_sync_lock_key";
        /**
         * 终端与策略映射关系
         */
        public static final String POLICY_DEVICEID_KEY = CACHE_PREFIX + "{policy_deviceId}:";
        /**
         * 组织机构与策略映射关系
         */
        public static final String POLICY_ORGID_KEY = CACHE_PREFIX + "{policy_orgId}:";
        /**
         * 指令信息同步锁
         */
        public static final String COMMAND_SYNC_LOCK_KEY = CACHE_PREFIX + "command_sync_lock_key";
        /**
         * 终端与指令映射关系
         */
        public static final String COMMAND_DEVICEID_KEY = CACHE_PREFIX + "{command_deviceId}:";
        /**
         * 组织机构与指令映射关系
         */
        public static final String COMMAND_ORGID_KEY = CACHE_PREFIX + "{command_orgId}:";

        /**
         * 终端指令版本缓存前缀
         */
        public static final String DEVICE_COMMAND_VERSION_PREFIX = CACHE_PREFIX + "{device_command_version}:";
        /**
         * 指令的cmdId值与版本的对应关系
         */
        public static final String COMMAND_CMDID_VERSION_PREFIX = CACHE_PREFIX + "{command_cmdId_version}:";
        /**
         * 终端策略版本缓存前缀
         */
        public static final String DEVICE_POLICY_VERSION_PREFIX = CACHE_PREFIX + "{device_policy_version}:";
        /**
         * 软件升级指令缓存
         */
        public static final String UPDATE_COMMAND_LIST = CACHE_PREFIX + "update_command_list";
        /**
         * 网络策略缓存
         */
        public static final String NET_POLICY_CHANGE_KEY = CACHE_PREFIX + "net_policy_change_key";

        /**
         * 风险软件策略缓存
         */
        public static final String SOFT_POLICY_CHANGE_KEY = CACHE_PREFIX + "soft_policy_change_key";
        /**
         * 时间戳的自增
         */
        public static final String DATE_STR_INCR = CACHE_PREFIX + "{dateStr_incr}:";
        /**
         * 策略与指令中各类型版本信息发布频道
         */
        public static final String POLICY_COMMAND_MODULE_VERSION_CHANNEL = CACHE_PREFIX + "policy_command_module_version_channel";
        /**
         * 终端收到策略与指令后，响应数据的版本信息发布频道
         */
        public static final String DEVICE_POLICY_COMMAND_RESPONSE_VERSION_CHANNEL = CACHE_PREFIX + "device_policy_command_response_version_channel";
        /**
         * 策略与指令中各类型数据版本
         */
        public static final String POLICY_COMMAND_MODULE_VERSION = CACHE_PREFIX + "{policy_command_module_version}:";
    }

    /**
     * BIZ  缓存key
     */
    public static class ItxCacheKey {
        /**
         * 缓存前缀
         */
        private static final String CACHE_PREFIX = "ITX:CACHE:";
        /**
         * 根节点ID
         */
        public static final String ROOT_NODE_ID = CACHE_PREFIX + "CURRENT_NODE_ID";
        /**
         * 根节点ID
         */
        public static final String ROOT_NODE_NAME = CACHE_PREFIX + "CURRENT_NODE_NAME";
        /**
         * 主机异步处理队列
         */
        public static final String HOST_ASYNC_HANDLE_QUEUE = CACHE_PREFIX + "HOST_ASYNC_HANDLE_QUEUE";
        /**
         * 异步处理用户队列
         */
        public static final String USER_ASYNC_HANDLE_QUEUE = CACHE_PREFIX + "USER_ASYNC_HANDLE_QUEUE";
        /**
         * 已激活主机id set缓存
         */
        public static final String ACTIVATE_HOST_ID_SET_REDIS_KEY = CACHE_PREFIX + "ACTIVATE_HOST_ID_SET";
        /**
         * 已激活主机id:安装码 hash缓存
         */
        public static final String ACTIVATE_HST_PKG_CODE_HASH_REDIS_KEY = CACHE_PREFIX + "ACTIVE_HST_PKG_CODE";
        /**
         * 系统参数缓存key
         */
        public static final String SYS_PARAM = CACHE_PREFIX + "SYS_PARAM";
        /**
         * 系统参数刷新通道
         */
        public static final String SYS_PARAM_REFRESH_CHANNEL = "ITX:PUB_SUB:SYS_PARAM_REFRESH_CHANNEL";
    }
}
