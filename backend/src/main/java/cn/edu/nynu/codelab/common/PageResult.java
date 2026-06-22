package cn.edu.nynu.codelab.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通用分页返回结果。
 * <p>
 * 用于所有列表接口的分页响应，后续文章、项目、成员、方向等模块均可复用。
 * </p>
 *
 * @param <T> 记录类型
 * @author NYNU Code Lab
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /** 当前页记录列表 */
    private List<T> records;

    /** 总记录数 */
    private long total;

    /** 当前页码 */
    private long page;

    /** 每页大小 */
    private long pageSize;

    /**
     * 便捷工厂方法。
     *
     * @param records  当前页记录
     * @param total    总记录数
     * @param page     当前页码
     * @param pageSize 每页大小
     * @param <T>      记录类型
     * @return PageResult 实例
     */
    public static <T> PageResult<T> of(List<T> records, long total, long page, long pageSize) {
        return new PageResult<>(records, total, page, pageSize);
    }

}
