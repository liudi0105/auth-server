package com.github.rudylucky.auth.controller;

import com.github.rudylucky.auth.common.user.LoginInfo;
import com.github.rudylucky.auth.common.user.LoginMap;
import com.github.rudylucky.auth.common.util.SpringBeanUtils;
import com.github.rudylucky.auth.dto.ClientMessage;
import com.github.rudylucky.auth.dto.ServerMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.Objects;

@Controller
public class WebSocketController {

	@MessageMapping("/login")
	public Object wsLoginInfoGet(ClientMessage msg){
		if(!ClientMessage.isValid(msg)){
			return new ServerMessage("500", "not valid msg");
		}
		LoginMap loginMap = (LoginMap) SpringBeanUtils.getBean("loginMap");
		Map<String, LoginInfo> map = loginMap.getMap();
		String name = msg.getName();
		LoginInfo info = map.get(name);
		if(info == null){
			map.put(name,new LoginInfo(name, System.currentTimeMillis(), msg.getHash()));
			return new ServerMessage("100", "first login");
		}
		if(Objects.equals(info.getHash(),msg.getHash())){
			info.setLatestLogin(System.currentTimeMillis());
			return new ServerMessage("101", "the same agent login",
					"", info.getHash(), info.getLatestLogin());
		}else{
			info = new LoginInfo(name, System.currentTimeMillis(), msg.getHash());
			map.put(name, info);
			return new ServerMessage("400", "log off", name,
					info.getHash(), info.getLatestLogin());
		}
	}
}
