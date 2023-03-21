package org.jeecg.common.exception;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.common.exception
 * @Author: GGB
 * @CreateTime: 2022-10-28  17:13
 * @Description: TODO
 * @Version: 1.0
 */
public class JeecgBoot412Exception extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JeecgBoot412Exception(String message){
        super(message);
    }

    public JeecgBoot412Exception(Throwable cause)
    {
        super(cause);
    }

    public JeecgBoot412Exception(String message, Throwable cause)
    {
        super(message,cause);
    }
}
