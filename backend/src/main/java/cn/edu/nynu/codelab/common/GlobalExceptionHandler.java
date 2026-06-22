package cn.edu.nynu.codelab.common;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.edu.nynu.codelab.common.exception.AccountDisabledException;
import cn.edu.nynu.codelab.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
            return Result.error(e.getCode(), "认证失败，请重新登录");
        }
        return Result.error(500, "系统内部认证错误");
    }

    /**
     * 处理账号已被禁用异常。
     */
    @ExceptionHandler(AccountDisabledException.class)
    public Result<?> handleAccountDisabled(AccountDisabledException e) {
        log.warn("账号已被禁用: {}", e.getMessage());
        return Result.error(403, e.getMessage());
    }

    /**
     * 处理请求体解析异常（JSON 格式错误等）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败: {}", e.getMessage());
        return Result.error(400, "请求格式错误，请检查 JSON 格式");
    }

    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<?> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        log.warn("文件上传超限: {}", e.getMessage());
        return Result.error(413, "文件大小超过限制（最大 10MB）");
    }

    /**
     * 处理数据完整性约束冲突（如唯一键重复、外键约束等）
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<?> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.warn("数据完整性约束冲突: {}", e.getMessage());
        // 提取友好消息：常见如 Duplicate entry 'xxx' for key 'uk_xxx'
        String msg = e.getMessage() != null ? e.getMessage() : "";
        if (msg.contains("Duplicate entry")) {
            return Result.error(409, "数据已存在，请勿重复提交");
        }
        return Result.error(409, "数据冲突，请检查输入");
    }

    /**
     * 处理业务异常，返回 400 + 友好提示。
     * <p>
     * 所有可预期的用户操作错误应抛出 {@link BusinessException}，
     * 消息直接返回给客户端引导用户修正。
     * </p>
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理未预期的运行时异常，返回 500 通用错误。
     * <p>
     * 这里的 RuntimeException 是不属于 {@link BusinessException} 的异常
     * （如 NullPointerException、IllegalStateException），属于服务端 bug，
     * 不向客户端暴露异常细节。
     * </p>
     * <p>
     * 注：历史代码中部分业务逻辑仍直接抛出 {@code new RuntimeException("消息")}，
     * 这些将被此 handler 捕获并返回 500 通用错误——应逐步迁移为 {@link BusinessException}。
     * </p>
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.error("未预期的运行时异常: {}", e.getMessage(), e);
        return Result.error(500, "服务器内部错误，请稍后重试");
    }

    /**
     * 处理通用异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error(500, "服务器内部错误，请稍后重试");
    }

}
