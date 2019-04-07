package com.github.rudylucky.auth.dto;

import com.github.rudylucky.auth.common.util.tree.TreeEntity;

public class PageComponent extends TreeEntity<PageComponent> {

    private String pageName;

    public PageComponent(String id, Integer sort, PageComponent parent, String pageName) {
        super(id, sort, parent);
        this.pageName = pageName;
    }

    public PageComponent(String id, Integer sort, String pageName) {
        super(id, sort);
        this.pageName = pageName;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }
}

