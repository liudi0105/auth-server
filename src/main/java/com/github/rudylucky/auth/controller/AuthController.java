package com.github.rudylucky.auth.controller;

import com.github.rudylucky.auth.common.UserStatus;
import com.github.rudylucky.auth.captcha.CaptchaGenerator;
import com.github.rudylucky.auth.captcha.CaptchaValidator;
import com.github.rudylucky.auth.common.AuthConstants;
import com.github.rudylucky.auth.common.exception.CustomException;
import com.github.rudylucky.auth.common.user.LoginUser;
import com.github.rudylucky.auth.common.util.JsonUtils;
import com.github.rudylucky.auth.controller.response.RpcResponseGenerator;
import com.github.rudylucky.auth.dto.UserDTO;
import com.github.rudylucky.auth.common.exception.AuthServiceException;
import com.github.rudylucky.auth.common.exception.ReturnMessageAndTemplateDef;
import com.github.rudylucky.auth.manager.UserManager;
import com.github.rudylucky.auth.service.impl.TokenServiceImpl;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static com.github.rudylucky.auth.service.ApiParamConstants.USERNAME;

@RestController
public class AuthController {

    private TokenServiceImpl tokenService;
    private DefaultKaptcha defaultKaptcha;
    private CaptchaValidator captchaValidator;
    private CaptchaGenerator captchaGenerator;
    private UserManager userManager;

    @Autowired
    public AuthController(
            TokenServiceImpl tokenService
            , DefaultKaptcha defaultKaptcha
            , CaptchaGenerator captchaGenerator
            , CaptchaValidator captchaValidator
            , UserManager userManager){
        this.tokenService = tokenService;
        this.captchaValidator = captchaValidator;
        this.defaultKaptcha = defaultKaptcha;
        this.captchaGenerator = captchaGenerator;
        this.userManager = userManager;
    }

    @PostMapping(value = RouterConstants.USERS_CHANGE_PASSWORD, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> authChangePassword(@RequestBody Map<String, Object> data) {
        try {
            String oldPassword = (String) data.get(AuthConstants.OLD_PASSWORD);
            String newPassword = (String) data.get(AuthConstants.NEW_PASSWORD);
            String username = (String) data.get(USERNAME);
            UserDTO userDTO = userManager.updateOwnPassword(username, oldPassword, newPassword);

            String successMessage = "密码修改成功";
            return RpcResponseGenerator.getOldResponseEntity(JsonUtils.toJson(new UserStatus(userDTO, null, successMessage, "0")));
        } catch (CustomException e) {
            return RpcResponseGenerator.getErrorResponseEntity(e);
        }
    }

    @PostMapping(value = RouterConstants.USERS_LOGIN, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> authLogin(@RequestBody Map<String, Object> data, HttpServletRequest httpServletRequest) {
        try {
            if(Objects.isNull(data))
                throw new AuthServiceException(ReturnMessageAndTemplateDef.Errors.ILLEGAL_REQUEST_BODY);

            LoginUser user = LoginUser.of(data);
            UserDTO userDto = userManager.getUserByUserName(user.getUsername());
            if (!captchaValidator.validateCaptcha(httpServletRequest, userDto, user.getCaptcha()))
                return RpcResponseGenerator.getErrorResponseEntity( ReturnMessageAndTemplateDef.Errors.CAPTCHA_NOT_VALID);

            String userName = (String) data.get(AuthConstants.USERNAME);
            if(StringUtils.isNotBlank(userName))
                return RpcResponseGenerator.getOldResponseEntity(tokenService.verifyAndGetToken(userDto, user.getPassword()).orElse(null));

            return RpcResponseGenerator.getResponseEntity(tokenService.verifyAndGetToken(userDto, user.getPassword()).orElse(null));
        } catch (CustomException e){
            return RpcResponseGenerator.getErrorResponseEntity(e);
        }
    }

    @GetMapping(value = RouterConstants.USERS_CAPTCHA)
    public void authCaptchaGet(HttpServletRequest httpServletRequest,
                               HttpServletResponse httpServletResponse){
        try {
            //生产验证码字符串并保存到session中
            String createText = defaultKaptcha.createText();
            httpServletRequest.getSession().setAttribute(AuthConstants.CAPTCHA, createText);

            //定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
            httpServletResponse.setHeader("Cache-Control", "no-store");
            httpServletResponse.setHeader("Pragma", "no-cache");
            httpServletResponse.setDateHeader("Expires", 0);
            httpServletResponse.setContentType("image/jpeg");
            ServletOutputStream responseOutputStream =
                    httpServletResponse.getOutputStream();
            responseOutputStream.write(captchaGenerator.getCaptchaJpeg(createText));
            responseOutputStream.flush();
            responseOutputStream.close();
        } catch(IOException e){
            throw new RuntimeException(e);
        }

    }
}
