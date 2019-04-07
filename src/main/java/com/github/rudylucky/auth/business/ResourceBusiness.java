package com.github.rudylucky.auth.business;


import com.github.rudylucky.auth.dto.Resource;

public interface ResourceBusiness {

    Resource modifyResource(String resourceId, String resourceName, String parentId);

}
