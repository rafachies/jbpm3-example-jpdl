package com.sample.action;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmException;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.identity.mail.IdentityAddressResolver;
import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.VariableResolver;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.util.ClassLoaderUtil;
import org.jbpm.util.XmlUtil;

/**
 * 
 * This class is an extensiÃ³n of classic jBPM mail ActionHandler.
 * 
 * Some new features have been added to use gmail smtp server.
 * 
 * @author pigui
 *
 */
public class Mail implements ActionHandler {

	private static final long serialVersionUID = 1L;

	String template = null;
	String actors = null;
	String to = null;
	String subject = null;
	String text = null;

	ExecutionContext executionContext = null;

	public Mail() {
	}

	public Mail(String template,
			String actors,
			String to,
			String subject,
			String text) {
		this.template = template;
		this.actors = actors;
		this.to = to;
		this.subject = subject;
		this.text = text;
	}

	public void execute(ExecutionContext executionContext) {
		this.executionContext = executionContext;
		send();
	}

	public List getRecipients() {
		List recipients = new ArrayList();
		if (actors!=null) {
			String evaluatedActors = evaluate(actors);
			List tokenizedActors = tokenize(evaluatedActors);
			if (tokenizedActors!=null) {
				recipients.addAll(resolveAddresses(tokenizedActors));
			}
		}
		if (to!=null) {
			String resolvedTo = evaluate(to);
			recipients.addAll(tokenize(resolvedTo));
		}
		return recipients;
	}

	public String getSubject() {
		if (subject==null) return null;
		return evaluate(subject);
	}

	public String getText() {
		if (text==null) return null;
		return evaluate(text);
	}

	public String getFromAddress() {
		if (JbpmConfiguration.Configs.hasObject("jbpm.mail.from.address")) {
			return JbpmConfiguration.Configs.getString("jbpm.mail.from.address");
		} 
		return "jbpm@noreply";
	}
	/*
	 * 
	 * New functions added by Pigui 
	 * 
	 */

	/**
	 * 
	 * Checks if is new features are enabled
	 * 
	 * @return 
	 * 
	 */
	public static boolean getAdvancedConfig() {
		if (JbpmConfiguration.Configs.hasObject("jbpm.mail.advanced.config")) {
			String config;
			config = JbpmConfiguration.Configs.getString("jbpm.mail.advanced.config");
			if (config.compareTo("true") == 0 )
				return true; 
		} 
		return false;
	}

	/**
	 * 
	 * Returns smtp user acount from config file 
	 * 
	 * @return
	 */
	public static String getUser() {
		if (JbpmConfiguration.Configs.hasObject("jbpm.mail.user")) {
			return JbpmConfiguration.Configs.getString("jbpm.mail.user");
		} 
		return null;
	}

	/**
	 * 
	 * Gets smtp password acount from config file
	 * 
	 * @return
	 */
	public static String getPass() {
		if (JbpmConfiguration.Configs.hasObject("jbpm.mail.pass")) {
			return JbpmConfiguration.Configs.getString("jbpm.mail.pass");
		} 
		return null;
	}

	/**
	 * 
	 * Gets smtp server port from config file
	 * 
	 * @return
	 */  
	public String getSmtpPort() {
		if (JbpmConfiguration.Configs.hasObject("jbpm.mail.port")) {
			return JbpmConfiguration.Configs.getString("jbpm.mail.port");
		} 
		return null;
	} 

	/**
	 * 
	 * Gets socket factory port from config file
	 * 
	 * @return
	 */  
	public String getSocketFactoryPort() {
		if (JbpmConfiguration.Configs.hasObject("jbpm.mail.smtp.socketFactory.port")) {
			return JbpmConfiguration.Configs.getString("jbpm.mail.smtp.socketFactory.port");
		} 
		return null;
	} 

	/**
	 * 
	 * Gets socket factory class from config file
	 * 
	 * @return
	 */  
	public String getSocketFactoryClass() {
		if (JbpmConfiguration.Configs.hasObject("jbpm.mail.smtp.socketFactory.class")) {
			return JbpmConfiguration.Configs.getString("jbpm.mail.smtp.socketFactory.class");
		} 
		return null;
	} 

	/**
	 * 
	 * Gets socket factory fallback from config file
	 * 
	 * @return
	 */  
	public String getSocketFactoryFallback() {
		if (JbpmConfiguration.Configs.hasObject("jbpm.mail.smtp.socketFactory.fallback")) {
			return JbpmConfiguration.Configs.getString("jbpm.mail.smtp.socketFactory.fallback");
		} 
		return null;
	}  


	/**
	 * 
	 * Gets authentication from config file
	 * 
	 * @return
	 */  
	public String getSmtpAuth() {
		if (JbpmConfiguration.Configs.hasObject("jbpm.mail.smtp.auth")) {
			return JbpmConfiguration.Configs.getString("jbpm.mail.smtp.auth");
		} 
		return null;
	}  


	/**
	 * 
	 * Gets smtp start TLS from config file
	 * 
	 * @return
	 */  
	public String getSmtpStarttls() {
		if (JbpmConfiguration.Configs.hasObject("jbpm.mail.smtp.starttls.enable")) {
			return JbpmConfiguration.Configs.getString("jbpm.mail.smtp.starttls.enable");
		} 
		return null;
	} 


	/**
	 * 
	 * Gets debug mode from config file
	 * 
	 * @return
	 */  
	public String getDebug() {
		if (JbpmConfiguration.Configs.hasObject("jbpm.mail.debug")) {
			return JbpmConfiguration.Configs.getString("jbpm.mail.debug");
		} 
		return null;
	}  

	/*
	 * 
	 * End new functions
	 * 
	 */  

	public void send() {
		if (template!=null) {
			Properties properties = getMailTemplateProperties(template);
			if (actors==null) {
				actors = properties.getProperty("actors");
			}
			if (to==null) {
				to = properties.getProperty("to");
			}
			if (subject==null) {
				subject = properties.getProperty("subject");
			}
			if (text==null) {
				text = properties.getProperty("text");
			}
		}

		send(getMailServerProperties(), 
				getFromAddress(), 
				getRecipients(), 
				getSubject(), 
				getText());
	}

	public static void send(Properties mailServerProperties, String fromAddress, List recipients, String subject, String text) {
		if ( (recipients==null)
				|| (recipients.isEmpty())
		) {
			log.debug("skipping mail because there are no recipients");
			return;
		}
		log.debug("sending email to '"+recipients+"' about '"+subject+"'");

		/*
		 * 
		 * New features adaptation
		 * START
		 * 
		 */    
		Session session;
		Authenticator auth;
		if(getAdvancedConfig()){
			String user = getUser();
			String pass = getPass();
			auth = new SMTPAuthenticator(user,pass);
			session = Session.getInstance(mailServerProperties, auth);
		} else{
			session = Session.getDefaultInstance(mailServerProperties, null);
		}
		/*
		 * 
		 * New features adaptation
		 * END
		 * 
		 */    
		MimeMessage message = new MimeMessage(session);

		try {
			if (fromAddress!=null) {
				message.setFrom(new InternetAddress(fromAddress));
			}
			Iterator iter = recipients.iterator();
			while (iter.hasNext()) {
				InternetAddress recipient = new InternetAddress((String) iter.next());
				message.addRecipient(Message.RecipientType.TO, recipient);
			}
			if (subject!=null) {
				message.setSubject(subject);
			}
			if (text!=null) {
				message.setText(text);
			}
			message.setSentDate(new Date());

			/*
			 * 
			 * New features adaptation
			 * START
			 * 
			 */
			if (getAdvancedConfig()){
				Transport transport;
				transport = session.getTransport("smtp");
				transport.connect();
			}
			/*
			 * 
			 * New features adaptation
			 * END
			 * 
			 */    
			Transport.send(message);
		} catch (Exception e) {
			throw new JbpmException("couldn't send email", e);
		}
	}

	protected List tokenize(String text) {
		if (text==null) {
			return null;
		}
		List list = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(text, ";:");
		while (tokenizer.hasMoreTokens()) {
			list.add(tokenizer.nextToken());
		}
		return list;
	}

	protected Collection resolveAddresses(List actorIds) {
		List emailAddresses = new ArrayList();
		Iterator iter = actorIds.iterator();
		while (iter.hasNext()) {
			String actorId = (String) iter.next();
			IdentityAddressResolver addressResolver = (IdentityAddressResolver) JbpmConfiguration.Configs.getObject("jbpm.mail.address.resolver");
			Object resolvedAddresses = addressResolver.resolveAddress(actorId);
			if (resolvedAddresses!=null) {
				if (resolvedAddresses instanceof String) {
					emailAddresses.add((String)resolvedAddresses);
				} else if (resolvedAddresses instanceof Collection) {
					emailAddresses.addAll((Collection)resolvedAddresses);
				} else if (resolvedAddresses instanceof String[]) {
					emailAddresses.addAll(Arrays.asList((String[])resolvedAddresses));
				} else {
					throw new JbpmException("Address resolver '"+addressResolver+"' returned '"+resolvedAddresses.getClass().getName()+"' instead of a String, Collection or String-array: "+resolvedAddresses);
				}
			}
		}
		return emailAddresses;
	}

	Properties getMailServerProperties() {
		Properties mailServerProperties = new Properties();

		if (JbpmConfiguration.Configs.hasObject("resource.mail.properties")) {
			String mailServerPropertiesResource = JbpmConfiguration.Configs.getString("resource.mail.properties");
			try {
				InputStream mailServerStream = ClassLoaderUtil.getStream(mailServerPropertiesResource);
				mailServerProperties.load(mailServerStream);
			} catch (Exception e) {
				throw new JbpmException("couldn't get configuration properties for jbpm mail server from resource '"+mailServerPropertiesResource+"'", e);
			}

		} else if (JbpmConfiguration.Configs.hasObject("jbpm.mail.smtp.host")) {
			String smtpServer = JbpmConfiguration.Configs.getString("jbpm.mail.smtp.host");
			mailServerProperties.put("mail.smtp.host", smtpServer);
			/*
			 * 
			 * New features adaptation
			 * START
			 * 
			 */
			if (getAdvancedConfig()){
				String auth = getSmtpAuth();
				String debug = getDebug();
				String tls = getSmtpStarttls();
				String port = getSmtpPort();
				String sfport = getSocketFactoryPort();
				String sfclass = getSocketFactoryClass();
				String sffallback = getSocketFactoryFallback();

				mailServerProperties.put("mail.smtp.auth", auth);
				mailServerProperties.put("mail.debug", debug);
				mailServerProperties.put("mail.smtp.starttls.enable", tls);
				mailServerProperties.put("mail.smtp.port", port);
				mailServerProperties.put("mail.smtp.socketFactory.port", sfport);
				mailServerProperties.put("mail.smtp.socketFactory.class",sfclass);
				mailServerProperties.put("mail.smtp.socketFactory.fallback", sffallback);
			}
			/*
			 * 
			 * New features adaptation
			 * END
			 * 
			 */    
		} else {

			log.error("couldn't get mail properties");
		}

		return mailServerProperties;
	}

	static Map templates = null;
	static Map templateVariables = null;
	synchronized Properties getMailTemplateProperties(String templateName) {
		if (templates==null) {
			templates = new HashMap();
			String mailTemplatesResource = JbpmConfiguration.Configs.getString("resource.mail.templates");
			org.w3c.dom.Element mailTemplatesElement = XmlUtil.parseXmlResource(mailTemplatesResource, true).getDocumentElement();
			List mailTemplateElements = XmlUtil.elements(mailTemplatesElement, "mail-template");
			Iterator iter = mailTemplateElements.iterator();
			while (iter.hasNext()) {
				org.w3c.dom.Element mailTemplateElement = (org.w3c.dom.Element) iter.next();

				Properties templateProperties = new Properties();
				addTemplateProperty(mailTemplateElement, "actors", templateProperties);
				addTemplateProperty(mailTemplateElement, "to", templateProperties);
				addTemplateProperty(mailTemplateElement, "subject", templateProperties);
				addTemplateProperty(mailTemplateElement, "text", templateProperties);

				templates.put(mailTemplateElement.getAttribute("name"), templateProperties);
			}

			templateVariables = new HashMap();
			List variableElements = XmlUtil.elements(mailTemplatesElement, "variable");
			iter = variableElements.iterator();
			while (iter.hasNext()) {
				org.w3c.dom.Element variableElement = (org.w3c.dom.Element) iter.next();
				templateVariables.put(variableElement.getAttribute("name"), variableElement.getAttribute("value"));
			}
		}
		return (Properties) templates.get(templateName);
	}

	void addTemplateProperty(org.w3c.dom.Element mailTemplateElement, String property, Properties templateProperties) {
		org.w3c.dom.Element element = XmlUtil.element(mailTemplateElement, property);
		if (element!=null) {
			templateProperties.put(property, XmlUtil.getContentText(element));
		}
	}

	String evaluate(String expression) {
		if (expression==null) {
			return null;
		}
		VariableResolver variableResolver = JbpmExpressionEvaluator.getUsedVariableResolver();
		if (variableResolver!=null) {
			variableResolver = new MailVariableResolver(templateVariables, variableResolver);
		}
		return (String) JbpmExpressionEvaluator.evaluate(expression, executionContext, variableResolver, null);
	}

	class MailVariableResolver implements VariableResolver, Serializable {
		private static final long serialVersionUID = 1L;
		Map templateVariables = null;
		VariableResolver variableResolver = null;

		public MailVariableResolver(Map templateVariables, VariableResolver variableResolver) {
			this.templateVariables = templateVariables;
			this.variableResolver = variableResolver;
		}

		public Object resolveVariable(String pName) throws ELException {
			if ( (templateVariables!=null)
					&& (templateVariables.containsKey(pName))
			){
				return templateVariables.get(pName);
			}
			return variableResolver.resolveVariable(pName);
		}
	}

	private static Log log = LogFactory.getLog(Mail.class);
} 

