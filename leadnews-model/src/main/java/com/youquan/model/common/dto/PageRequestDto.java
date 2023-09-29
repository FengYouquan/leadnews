package com.youquan.model.common.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/9/29 11:11
 */
@Data
@Slf4j
public class PageRequestDto {

    protected Integer size;
    protected Integer page;

    public void checkParam() {
        if (this.page == null || this.page < 0) {
            setPage(1);
        }
        if (this.size == null || this.size < 0 || this.size > 100) {
            setSize(10);
        }
    }
}