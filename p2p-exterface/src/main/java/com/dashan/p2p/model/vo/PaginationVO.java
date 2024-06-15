package com.dashan.p2p.model.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 分页类
 */
public class PaginationVO<T> implements Serializable {

    // 分页数据
    private List<T> datas;

    // 总条数
    private Integer totalSize;


    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }
}
