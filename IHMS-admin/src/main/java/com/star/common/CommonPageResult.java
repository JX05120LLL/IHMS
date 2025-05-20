package com.star.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 通用响应 - 分页
 *
 * @param <T> 泛型
 *  
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonPageResult<T> extends Result<T> {

    /**
     * 分页响应数据
     */
    private T data;
    /**
     * 符合条件的总记录数
     */
    private Integer total;

    /**
     * 参数构造
     *
     * @param code 响应码
     *  
     */
    public CommonPageResult(Integer code) {
        super(code, "查询成功");
    }

    /**
     * 分页查血结果反馈
     *
     * @param data  数据源
     * @param total 总记录数
     * @param <T>   泛型
     * @return <T>
     *  
     */
    public static <T> Result<T> success(T data, Integer total) {
        CommonPageResult<T> result = new CommonPageResult<>(ResultCode.REQUEST_SUCCESS.getCode());
        result.setData(data);
        result.setTotal(total);
        return result;
    }

}
