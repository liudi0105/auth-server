package com.github.rudylucky.auth.common.util;

import com.github.rudylucky.auth.common.bo.MailConfigBO;
import com.github.rudylucky.auth.common.exception.CustomException;
import com.github.rudylucky.auth.common.exception.ErrorCode;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.CollectionUtils;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;
import java.util.Properties;

public class EmailUtils {

    public static boolean sendEmail(String[] tos, String[] ccs, String[] bccs, String subject, String content, List<File> files, MailConfigBO mailConfig) throws CustomException, MessagingException {
        if (mailConfig == null){
            throw new CustomException(ErrorCode.INPUT_NOT_VALID, "邮箱配置类不能为空");
        }
        Properties properties = new Properties();
         //设置smtp协议
        properties.put("mail.transport.protocol", "smtp");
        //设置服务器连接地址
        properties.put("mail.smtp.host", "smtp.exmail.qq.com");
        //设置TLS保护连接，默认为false
        properties.put("mail.smtp.starttls.enable", "true");
        //设置身份校验
        properties.put("mail.smtp.auth", "true");
        //设置默认端口号及SSL
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.socketFactory.port", "465");
        //不做服务器证书校验
        properties.put("mail.smtp.ssl.checkserveridentity", "false");
        //信任全部
        properties.put("mail.smtp.ssl.trust", "*");
        //使用环境属性和授权信息，创建邮件会话
        Session session = Session.getInstance(properties);
        //创建邮件消息
        MimeMessage msg = new MimeMessage(session);
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setFrom(mailConfig.getUsername());
        if(ArrayUtils.isEmpty(tos)){
            throw new CustomException(ErrorCode.INPUT_NOT_VALID, "收件人邮箱不能为空");
        }
        helper.setTo(tos);
        if(ArrayUtils.isNotEmpty(ccs)){
            helper.setCc(ccs);
        }
        if(ArrayUtils.isNotEmpty(bccs)){
            helper.setBcc(bccs);
        }
        helper.setSubject(subject);
        helper.setText(content);
        if(!CollectionUtils.isEmpty(files)){
            for (File attachment : files) {
                if(attachment != null && attachment.exists() && attachment.isFile()){
                    FileSystemResource file = new FileSystemResource(attachment);
                    if(!file.exists()){
                        throw new CustomException(ErrorCode.INPUT_NOT_VALID, "附件不存在");
                    }
                    helper.addAttachment(attachment.getName(), file);//添加附加
                }else{
                    throw new CustomException(ErrorCode.INPUT_NOT_VALID, "附件不存在");
                }
            }
        }
        //建立邮件传输对象
        Transport transport = session.getTransport("smtp");
        //与服务端建立连接
        transport.connect("smtp.exmail.qq.com", mailConfig.getUsername(), mailConfig.getPassword());
        //发送邮件
        transport.sendMessage(msg, msg.getAllRecipients());
        //关闭
        transport.close();
        return true;
    }

    /**
     * 多个邮箱以；或，分割
     */
    public static boolean sendEmail(String tos, String ccs, String bccs, String subject, String content, List<File> files, MailConfigBO mailConfig) throws CustomException, MessagingException {
        return sendEmail(splitEmailStr(tos), splitEmailStr(ccs), splitEmailStr(bccs), subject, content, files, mailConfig);
    }

    private static String[] splitEmailStr(String emailStr){
        if (StringUtils.isEmpty(emailStr)){
            return null;
        }
        String[] arr;
        if(emailStr.contains(";")){
            arr = emailStr.split(";");
        }else if(emailStr.contains(",")){
            arr = emailStr.split(",");
        }else{
            arr = new String[]{emailStr};
        }
        return arr;
    }
}
