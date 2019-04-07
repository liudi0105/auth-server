package com.github.rudylucky.auth.dto;


import com.github.rudylucky.auth.common.util.tree.PlainTreeRecord;

public class PageComponentDTO implements PlainTreeRecord {

    private String id;
    private String pageName;
    private Integer sort;
    private String parentId;

    public PageComponentDTO(String id, String pageName, Integer sort, String parentId) {
        this.id = id;
        this.pageName = pageName;
        this.sort = sort;
        this.parentId = parentId;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
