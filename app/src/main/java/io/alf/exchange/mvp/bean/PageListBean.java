package io.alf.exchange.mvp.bean;

import java.io.Serializable;
import java.util.List;

public class PageListBean<T> implements Serializable {
    public Pagination pagination;
    public List<T> list;
}
