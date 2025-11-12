package com.sapling.framework.common.exception;

import com.sapling.framework.common.exception.enums.AppHttpStatus;

/**

 * @Description: 调用第三方接口异常
 * @author: mbws
 */
public class RemoteInvokeException extends BasicException {
    public RemoteInvokeException() {
        super(AppHttpStatus.ERROR);
    }

    public RemoteInvokeException(AppHttpStatus httpStatus) {
        super(httpStatus);
    }

    public RemoteInvokeException(int status, String errorMessage) {
        super(status, errorMessage);
    }

    public RemoteInvokeException(int status, String errorMessage, boolean error) {
        super(status, errorMessage, error);
    }
}
