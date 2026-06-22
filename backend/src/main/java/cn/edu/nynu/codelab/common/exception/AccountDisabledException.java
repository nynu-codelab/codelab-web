package cn.edu.nynu.codelab.common.exception;

/**
 * 账号已被禁用异常。
 * <p>
 * 当禁用用户尝试访问受保护接口时抛出，由 GlobalExceptionHandler 统一处理返回 403。
 * </p>
 *
 * @author NYNU Code Lab
 */
public class AccountDisabledException extends RuntimeException {

    public AccountDisabledException(String message) {
        super(message);
    }

    public AccountDisabledException() {
        super("账号已被禁用，请联系管理员");
    }

}
