package com.sapling.framework.common.exception;

import lombok.Data;
import com.sapling.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.sapling.framework.common.exception.enums.ServiceErrorCodeRange;

/**
 * 错误码对象
 *
 * 全局错误码，占用 [0, 999], 参见 {@link GlobalErrorCodeConstants}
 * 业务异常错误码，占用 [1 000 000 000, +∞)，参见 {@link ServiceErrorCodeRange}
 *
 * @author mbws
 */
@Data
public class ErrorCode {

    /**
     * 错误码
     */
    private final Integer type;
    /**
     * 错误提示
     */
    private final String message;

    public ErrorCode(Integer type, String message) {
        this.type = type;
        this.message = message;
    }

}
