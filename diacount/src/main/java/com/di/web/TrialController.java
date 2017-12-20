package com.di.web;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.io.Files;
import com.ucpaas.restDemo.RestTest;

/**
 * 本Controller为用户试用申请表提交
 * 10.10.1.7/home/izhiliao/java/apache-tomcat-8.0.23/webapps
 * @author jiahh 2016年3月1日
 *
 */
@RestController
@Slf4j
@RequestMapping("/trial")
public class TrialController {

	// 用户邮箱集合
	Set<String> mailSet = new HashSet<String>();
	// 用户手机集合
	Set<String> phoneSet = new HashSet<String>();
	@RequestMapping("/mail")
	/**
	 * "/trial/mail?userid=jiahh&name=贾辉辉&sex=男&mail=phoema@163.com&company=出版社&phone=18932658411&tel=13000000000&note="
	 * @param userid
	 * @param name
	 * @param sex
	 * @param mail
	 * @param company
	 * @param phone
	 * @param tel
	 * @param note
	 * @return
	 * @throws Exception
	 */
	public String mail(String userid, String name, String sex, String mail, String company, String phone, String tel, String note)
			throws Exception {
		// yyyymmdd
		//long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		long datetime = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
		userid = URLDecoder.decode(userid,"UTF-8");
		name = URLDecoder.decode(name,"UTF-8");
		sex = URLDecoder.decode(sex,"UTF-8");
		company = URLDecoder.decode(company,"UTF-8");
		tel = URLDecoder.decode(tel,"UTF-8");
		note = URLDecoder.decode(note,"UTF-8");
		// 邮箱配置
		String smtp = "smtp.263xmail.com";
		String frommail = "diinspiro@cnipr.com";
		String mailpwd = "di@88888";
		String subject = "DI Inspiro试用申请表";
		// 密送邮箱
		String bccmail = "4001880860@cnipr.com";
		String bccmail2 = "53452368@qq.com";
		// 用户列表文件地址及文件名
		String fileUserList = "/home/izhiliao/java/trail/userlist.txt";
		String pathUserList = "/home/izhiliao/java/trail/";
		File dirs = new File(pathUserList);
		final File newFile = new File(fileUserList);
		if (!dirs.exists()) {
			Files.createParentDirs(dirs);
			dirs.mkdirs();

		}
		if (!newFile.exists()) {
			Files.append("注册日\t邮箱\t登录名\t姓名\t性别\t单位\t手机\t座机\t备注\t\r\n", newFile,StandardCharsets.UTF_8);
		}
		//判断文件行数，每达到10个新注册用户，则给邮箱发一封全用户列表附件  
		List<String> lines = Files.readLines(newFile,StandardCharsets.UTF_8);
		// 如果内存表无数据且文件行大于1则装载mail到内存
		if(mailSet.size() == 0 && lines.size() > 1){
			for(int i = 1;i <lines.size();i++){
				String parm1 = lines.get(i).split("\t")[1];
				if(!mailSet.contains(parm1)){
					mailSet.add(parm1);
				}
			}
		}
		
		if(mailSet.contains(mail)){
			return "此邮箱曾经申请过，请耐心等待,如账号已过期，请联系客服或续费。";
		}		
		// 20160927 手机号码入缓存 start 如果内存表无数据且文件行大于1则装载phone到内存
		if(phoneSet.size() == 0 && lines.size() > 1){
			for(int i = 1;i <lines.size();i++){
				String parm1 = lines.get(i).split("\t")[6];
				if(!phoneSet.contains(parm1)){
					phoneSet.add(parm1);
				}
			}
		}
		
		if(phoneSet.contains(phone)){
			return "此手机已申请过，请耐心等待,如账号已过期，请联系客服或续费。";
		}
		// 20160927 手机号码入缓存 end
		

		// 邮件的文本内容
		StringBuilder builder = new StringBuilder();

		builder.append("#name#:<br>");
		builder.append("&nbsp;&nbsp;&nbsp;&nbsp;您好！<br>");
		builder.append("&nbsp;&nbsp;&nbsp;&nbsp;我们已收到您的申请资料如下：<br>");
		builder.append("&nbsp;&nbsp;&nbsp;&nbsp;登录名：#userid#<br>");
		builder.append("&nbsp;&nbsp;&nbsp;&nbsp;姓名：#name#<br>");
		builder.append("&nbsp;&nbsp;&nbsp;&nbsp;性别：#sex#<br>");
		builder.append("&nbsp;&nbsp;&nbsp;&nbsp;单位：#company#<br>");
		builder.append("&nbsp;&nbsp;&nbsp;&nbsp;邮箱：#mail#<br>");
		builder.append("&nbsp;&nbsp;&nbsp;&nbsp;手机：#phone#<br>");
		builder.append("&nbsp;&nbsp;&nbsp;&nbsp;电话：#tel#<br>");
		builder.append("&nbsp;&nbsp;&nbsp;&nbsp;备注：#note#<br>");
		builder.append("<br>");
		builder.append("&nbsp;&nbsp;&nbsp;&nbsp;我们会尽快审核并开通您的系统账号。").append("<br>");
		builder.append("<br>");
		builder.append("请不要回复此邮件，").append("<br>");
		builder.append("如您在系统使用过程中遇到任何疑问，请通过以下方式联系我们，").append("<br>");
		builder.append("客服电话：400-188-0860").append("<br>");
		builder.append("客服邮箱：4001880860@cnipr.com").append("<br>");

		String mailcontent = builder.toString().replace("#userid#", userid).replace("#name#", name)
				.replace("#sex#", sex).replace("#mail#", mail).replace("#company#", company).replace("#phone#", phone)
				.replace("#tel#", tel).replace("#note#", note);
		// 邮件的文本内容 End

		Properties prop = new Properties();
		// prop.setProperty("mail.host", "smtp.sohu.com");
		prop.setProperty("mail.host", smtp);
		prop.setProperty("mail.transport.protocol", "smtp");
		prop.setProperty("mail.smtp.auth", "true");
		// 使用JavaMail发送邮件的5个步骤
		// 1、创建session
		Session session = Session.getInstance(prop);
		// 开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
		session.setDebug(true);
		// 2、通过session得到transport对象
		Transport ts = session.getTransport();
		// 3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
		// ts.connect("smtp.263xmail.com", "phoema@sohu.com", "");
		ts.connect(smtp, frommail, mailpwd);
		// 4、创建邮件
		// 创建邮件对象 Start
		MimeMessage message = new MimeMessage(session);
		// 指明邮件的发件人
		message.setFrom(new InternetAddress(frommail));
		// 指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(mail));
		// 抄送，不抄送，全部密送
		// message.setRecipient(Message.RecipientType.CC, new InternetAddress(frommail));
		// 密送
		message.setRecipient(Message.RecipientType.BCC, new InternetAddress(frommail));
		//message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccmail));
		//message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccmail2));
		// 邮件的标题
		message.setSubject(subject);
		// 邮件的文本内容
		message.setContent(mailcontent, "text/html;charset=UTF-8");
		// 返回创建好的邮件对象 End

		// 5、发送邮件
		ts.sendMessage(message, message.getAllRecipients());
		
		
		//String info = "登录名：\t%s\t姓名：\t%s\t性别：\t%s\t单位：\t%s\t邮箱：\t%s\t电话：\t%s\t备注：\t%s\t注册日：\t%s\r\n";

		// 6、追加用户邮箱集合
		if(!mailSet.contains(mail)){
			mailSet.add(mail);
		}
		if(!phoneSet.contains(phone)){
			phoneSet.add(phone);
		}
		//String out = String.format(info,"20160307","jiahuihui@cnipr.com", "jiahuihui","贾辉辉","男","出版社","13000000","010","");
		String info = "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\r\n";

		String out = String.format(info,datetime,mail, userid,name,sex,company,phone,tel,note.replace("\r\n", "\\r\\n"));
		// 7、保存到文件
		Files.append(out, newFile,StandardCharsets.UTF_8);

		/** 发送实时通知短信  start **/
		String accountSid="c6dcf7faa4204b5bb3c20f7fca7326f6";
		String token="f12b60eca7d5b2130c36555ac01f2204";
		String appId="367fc04792f849c68b84054ff579424e";
		String templateId="24202";
		//String to="18932658411";
		//String para="phoema";
		String to = phone;
		String para = name;
		try{
			String result = RestTest.testTemplateSMS(true, accountSid,token,appId, templateId,to,para);
			log.info(mail+": " + phone +":"+result);
		}catch(Exception ex){
			log.error(ex.getMessage());
		}
		/** 发送实时通知短信  end **/
		
		// 8、判断文件行数，每达到10个新注册用户，则给邮箱发一封全用户列表附件  
       if(lines.size()%10 == 0){
    	   // 备份
    	    Files.copy(newFile, new File(fileUserList+(lines.size())));
    		// 创建邮件对象 Start
    		MimeMessage msg = new MimeMessage(session);
    		// 指明邮件的发件人
    		msg.setFrom(new InternetAddress(frommail));
    		// 指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
    		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(frommail));
    		msg.setRecipient(Message.RecipientType.BCC, new InternetAddress(bccmail));
    		msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccmail2));
    		// 邮件的标题
    		msg.setSubject("试用申请用户列表" + (lines.size()));

    		Multipart mp = new MimeMultipart();  
            MimeBodyPart mbp = new MimeBodyPart();  
     
            FileDataSource fds=new FileDataSource(fileUserList); //得到数据源  
            mbp.setDataHandler(new DataHandler(fds)); //得到附件本身并至入BodyPart  
            mbp.setFileName(newFile.getName());  //得到文件名同样至入BodyPart  
            mp.addBodyPart(mbp);  
            
            msg.setContent(mp); //Multipart加入到信件  
            msg.setSentDate(new Date());     //设置信件头的发送日期  
            msg.saveChanges();   
    		// 5、发送邮件
    		ts.sendMessage(msg, msg.getAllRecipients());
    		
        }
        
        //Transport关闭
		ts.close();
		return "您的试用申请信息提交成功，我们的工作人员会在一个工作日之内与您取得联系。";
	}

}
