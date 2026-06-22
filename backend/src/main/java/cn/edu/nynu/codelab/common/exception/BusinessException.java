package cn.edu.nynu.codelab.common.exception;

/**
 * 业务异常——表示用户可自行修正的错误（如参数不合法、状态不允许等）。
 * <p>
 * 在 {@link cn.edu.nynu.codelab.common.GlobalExceptionHandler} 中被映射为
 * HTTP 400（Bad Request），错误消息直接返回给客户端。
 * </p>
 * <p>
 * 与普通 {@link RuntimeException} 的区别：
 * <ul>
 *   <li>{@code BusinessException} → 400（用户可修正）</li>
 *   <li>其他 {@code RuntimeException} → 500（服务端 bug，不暴露细节）</li>
 * </ul>
 * </p>
 *
 * @author NYNU Code Lab
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

}
