package com.github.rudylucky.auth.service;


import com.github.rudylucky.auth.dto.PageComponent;

import java.util.Collection;
import java.util.Map;

public interface PageComponentService {

    PageComponent authPageComponentList();

    Boolean authPagePermissionSet(Collection<Map<String, Object>> permissions);

}
