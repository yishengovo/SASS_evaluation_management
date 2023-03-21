package org.jeecg.common.exception;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.common.exception
 * @Author: GGB
 * @CreateTime: 2022-10-28  17:13
 * @Description: TODO
 * @Version: 1.0
 */
public class JeecgBoot413Exception extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JeecgBoot413Exception(String message){
        super(message);
    }

    public JeecgBoot413Exception(Throwable cause)
    {
        super(cause);
    }

    public JeecgBoot413Exception(String message, Throwable cause)
    {
        super(message,cause);
    }
}
