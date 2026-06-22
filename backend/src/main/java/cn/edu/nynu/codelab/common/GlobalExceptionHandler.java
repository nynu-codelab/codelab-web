package cn.edu.nynu.codelab.common;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author NYNU Code Lab
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 @Valid 校验异常（JSON 请求体）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return Result.error(400, message);
    }

    /**
     * 处理 @Valid 校验异常（表单请求）
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定失败: {}", message);
        return Result.error(400, message);
    }

    /**
     * 处理未登录异常。
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<?> handleNotLogin(NotLoginException e) {
        log.warn("未登录访问受保护接口: {}", e.getMessage());
        return Result.error(401, "未登录或登录已过期");
    }

    /**
     * 处理无权限/角色不足异常。
     */
    @ExceptionHandler({NotRoleException.class, NotPermissionException.class})
    public Result<?> handleForbidden(Exception e) {
        log.warn("权限不足: {}", e.getMessage());
        return Result.error(403, "无权限访问");
    }

    /**
     * 处理 Sa-Token 框架异常（兜底）。
     * <p>
     * Sa-Token 中所有认证/授权异常均继承 SaTokenException → RuntimeException。
     * 已单独处理的异常（NotLoginException → 401, NotRoleException/NotPermissionException → 403）
     * 由对应更具体的 handler 拦截。
     * <p>
     * 此 handler 负责兜底，捕获：
     * <ul>
     *   <li>JWT 解析异常（SaJwtException 等）→ 401</li>
     *   <li>其他未知 Sa-Token 异常 → 500（记录日志用于排查）</li>
     * </ul>
     */
    @ExceptionHandler(SaTokenException.class)
    public Result<?> handleSaTokenException(SaTokenException e) {
        log.error("Sa-Token 框架异常: code={}, message={}", e.getCode(), e.getMessage());
        // SaTokenException.getCode() 返回异常对应的状态码，< 500 的按认证失败处理
        if (e.getCode() > 0 && e.getCode() < 500) {
            return Result.error(e.getCode(), "认证失败: " + e.getMessage());
        }
        return Result.error(500, "系统内部认证错误");
    }

    /**
     * 处理业务异常（RuntimeException），返回友好提示。
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error(500, "服务器内部错误: " + e.getMessage());
    }

}
