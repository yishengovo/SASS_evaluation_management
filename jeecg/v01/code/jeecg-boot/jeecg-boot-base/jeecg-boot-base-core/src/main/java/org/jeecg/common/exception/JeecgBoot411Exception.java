package org.jeecg.common.exception;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.common.exception
 * @Author: GGB
 * @CreateTime: 2022-10-28  17:13
 * @Description: TODO
 * @Version: 1.0
 */
public class JeecgBoot411Exception extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JeecgBoot411Exception(String message){
        super(message);
    }

    public JeecgBoot411Exception(Throwable cause)
    {
        super(cause);
    }

    public JeecgBoot411Exception(String message, Throwable cause)
    {
        super(message,cause);
    }
}
