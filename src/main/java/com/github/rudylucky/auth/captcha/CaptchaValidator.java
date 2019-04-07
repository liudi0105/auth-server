package com.github.rudylucky.auth.captcha;

import com.github.rudylucky.auth.common.AuthConstants;
import com.github.rudylucky.auth.dto.UserDTO;
import com.github.rudylucky.auth.enums.UserTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Component
public class CaptchaValidator {

    @Value("${captcha.enabled:true}")
    private Boolean captchaEnabled;

    public Boolean validateCaptcha(HttpServletRequest request, UserDTO userDto, String captcha){
        return !captchaEnabled // 未激活验证码功能
                || Objects.isNull(captcha)
                || Objects.equals(userDto.getUserType(), UserTypeEnum.SCRIPT) // 用户为脚本用户
                || (Objects.equals(request.getSession().getAttribute(AuthConstants.CAPTCHA), captcha) && StringUtils.isNotBlank(captcha)); // 验证码验证通过
    }
}
