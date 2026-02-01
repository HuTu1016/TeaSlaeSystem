package org.example.teasystem.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer totalPages;

    /**
     * 数据列表
     */
    private List<T> items;

    public PageResult(Integer page, Integer pageSize, Long total, List<T> items) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
        this.items = items;
    }

    public static <T> PageResult<T> of(Integer page, Integer pageSize, Long total, List<T> items) {
        return new PageResult<>(page, pageSize, total, items);
    }

    /**
     * 设置数据列表（别名，兼容setList调用）
     */
    public void setList(List<T> list) {
        this.items = list;
    }

    /**
     * 获取数据列表（别名，兼容getList调用）
     */
    public List<T> getList() {
        return this.items;
    }
}
