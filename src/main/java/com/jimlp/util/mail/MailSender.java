package com.jimlp.util.mail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.core.io.FileSystemResource;

import com.jimlp.util.HtmlUtils;
import com.jimlp.util.ProjectUtils;
import com.sun.mail.util.MailSSLSocketFactory;

/**
 * 邮件发送类
 * 
 * @author jxb
 *
 */
public final class MailSender {
	// 邮件体
	MimeMessage mimeMessage;
	// 邮件多部件容器
	Multipart mimeMultipart = new MimeMultipart();

	/**
	 * 配置发件人
	 * 
	 * @param personalName
	 *            发件人显示名称
	 * @param from
	 *            发件人地址
	 * @param pw
	 *            发件人邮箱密码
	 * @param host
	 *            发件邮箱主机地址
	 * @param needSSL
	 *            是否需要 SSL 加密
	 * @throws MessagingException
	 */
	public MailSender(String personalName, final String from, final String pw, String host, boolean needSSL) throws MessagingException {
		// 获取系统属性
		Properties properties = System.getProperties();
		// 设置邮件服务器
		properties.setProperty("mail.smtp.host", host);
		// SSL 加密
		if (needSSL) {
			try {
				MailSSLSocketFactory ssl = new MailSSLSocketFactory();
				ssl.setTrustAllHosts(true);
				properties.put("mail.smtp.ssl.socketFactory", ssl);
				properties.put("mail.smtp.ssl.enable", "true");
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
		// 邮箱服务器用户认证
		Authenticator authenticator = null;
		if (pw != null) {
			properties.put("mail.smtp.auth", "true");
			authenticator = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(from, pw);
				}
			};
		}

		// 创建 Session 对象
		Session session = Session.getDefaultInstance(properties, authenticator);

		// 创建邮件体
		this.mimeMessage = new MimeMessage(session);

		// 发件地址
		try {
			this.mimeMessage.setFrom(new InternetAddress(from, personalName, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置收件地址
	 * 
	 * @param to
	 *            收件人
	 * @throws MessagingException
	 */
	public void setRecipients(String[] to) throws MessagingException {
		Address[] addresses = null;
		if (to != null) {
			int length = to.length;
			if (to.length > 0) {
				addresses = new Address[length];
				for (int i = 0; i < length; i++) {
					addresses[i] = new InternetAddress(to[i]);
				}
			}
		}
		this.mimeMessage.setRecipients(Message.RecipientType.TO, addresses);
	}

	/**
	 * 配置邮件消息
	 * 
	 * @param subject
	 *            邮件主题
	 * @param content
	 *            邮件正文
	 * @param htmlFormat
	 *            正文是否按 html 格式解析
	 * @throws MessagingException
	 */
	public void setContent(String subject, String content, boolean htmlFormat) throws MessagingException {
		// 设置邮件主题
		this.mimeMessage.setSubject(subject);

		if (content == null) {
			return;
		}

		// 创建一个邮件部件
		BodyPart mimeBodyPart = new MimeBodyPart();

		// 纯文本正文
		if (!htmlFormat) {
			mimeBodyPart.setText(content);
			// 加入多部件主体
			this.mimeMultipart.addBodyPart(mimeBodyPart);
			return;
		}

		// HTML正文
		// 说明：嵌入图片<img src="cid:id"/>，其中 cid: 是固定的写法，而id 是一个 contentId 。
		List<String> srcList = HtmlUtils.listRelativePathSrcUrl(content);
		String regex2 = "(src){1}\\s*=\\s*(\")";
		content = content.replaceAll(regex2, "src=\"cid:");
		mimeBodyPart.setContent(content, "text/html;charset=utf-8");
		this.mimeMultipart.addBodyPart(mimeBodyPart);
		String basePath = ProjectUtils.getProjectPath();
		StringBuilder filePath = new StringBuilder();
		for (int i = 0, l = srcList.size(); i < l; i++) {
			String src = srcList.get(i);
			// 忽略不是本项目内的图片
			if (!src.startsWith("/")) {
				continue;
			}
			filePath.setLength(0);
			filePath.append(basePath).append(src);
			final FileSystemResource img = new FileSystemResource(new File(filePath.toString()));
			mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setDisposition(MimeBodyPart.INLINE);
			mimeBodyPart.setHeader("Content-ID", "<" + src + ">");
			DataSource ds = new DataSource() {
				public InputStream getInputStream() throws IOException {
					return img.getInputStream();
				}

				public OutputStream getOutputStream() {
					throw new UnsupportedOperationException("Read-only javax.activation.DataSource");
				}

				public String getContentType() {
					return "application/octet-stream";
				}

				public String getName() {
					return img.getFilename();
				}
			};
			mimeBodyPart.setDataHandler(new DataHandler(ds));
			this.mimeMultipart.addBodyPart(mimeBodyPart);
		}

	}

	/**
	 * 添加附件
	 * 
	 * @param files
	 *            文件
	 * @throws MessagingException
	 */
	public void setFiles(File[] files) throws MessagingException {
		if (files != null) {
			// 创建一个邮件部件
			BodyPart messageBodyPart = new MimeBodyPart();
			for (int i = 0; i < files.length; i++) {
				messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(files[i]);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(files[i].getName());
				this.mimeMultipart.addBodyPart(messageBodyPart);
			}
		}
	}

	/**
	 * 发送邮件
	 * 
	 * @throws MessagingException
	 */
	public void send() throws MessagingException {
		if (this.mimeMultipart.getCount() < 1) {
			throw new MessagingException("邮件内容为空。");
		}
		this.mimeMessage.setContent(this.mimeMultipart);
		Transport.send(this.mimeMessage);
	}
}
