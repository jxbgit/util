package com.jimlp.util.mail;

import java.io.File;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.jimlp.util.ProjectUtils;
import com.jimlp.util.web.html.HtmlUtils;

/**
 * 邮件发送工具
 *
 * <br>
 * 创建时间 2018年1月4日下午4:02:00
 *
 * @author jxb
 *
 */
public class MailUtils {

    /**
     * 配置 html 邮件正文
     * 
     * @param messageHelper
     *            org.springframework.mail.javamail.MimeMessageHelper
     * @param html
     *            html 源码
     * @throws MessagingException
     */
    public static void setHtmlContent(MimeMessageHelper messageHelper, String html) throws MessagingException {
        // 说明：嵌入图片<img src="cid:id"/>，其中 cid: 是固定的写法，而id 是一个 contentId 。
        List<String> srcList = HtmlUtils.listValOfAttr(html, "img", "src", "/[^'\"]+\\.[a-zA-Z]+");
        int l = srcList.size();
        if (srcList != null && l > 0) {
            String regex2 = "(src){1}\\s*=\\s*(\")";
            html = html.replaceAll(regex2, "src=\"cid:");
            String basePath = ProjectUtils.getProjectPath();
            StringBuilder filePath = new StringBuilder();
            messageHelper.setText(html, true);
            for (int i = 0; i < l; i++) {
                String src = srcList.get(i);
                // 忽略其他路径的图片
                if (!src.startsWith("/")) {
                    continue;
                }
                filePath.setLength(0);
                filePath.append(basePath).append(src);
                FileSystemResource img = new FileSystemResource(new File(filePath.toString()));
                messageHelper.addInline(src, img);
            }
		} else {
			messageHelper.setText(html, true);
		}
    }

    /**
     * 添加附件
     * 
     * @param messageHelper
     *            org.springframework.mail.javamail.MimeMessageHelper
     * @param attachments
     *            附件
     * @throws MessagingException
     */
    public static void addAttachment(MimeMessageHelper messageHelper, File attachments[]) throws MessagingException {
        if (attachments != null) {
            for (int i = 0, l = attachments.length; i < l; i++) {
                File f = attachments[i];
                if (f == null) {
                    continue;
                }
                FileSystemResource file = new FileSystemResource(f);
                String name = f.getName();
                messageHelper.addAttachment(name, file);
            }
        }
    }
}
