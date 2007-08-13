package org.jboss.seam.mail;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.naming.NamingException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.AbstractMutable;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Naming;

/**
 * Manager component for a javax.mail.Session
 */
@Name("org.jboss.seam.mail.mailSession")
@Install(precedence=BUILT_IN, classDependencies="javax.mail.Session")
@Scope(APPLICATION)
@BypassInterceptors
public class MailSession extends AbstractMutable implements Serializable
{
   private static final LogProvider log = Logging.getLogProvider(MailSession.class);

	private Session session;

	private String host = "localhost";
   private Integer port;
	private String username;
	private String password;
	private boolean debug = false;
   private String sessionJndiName;
   private boolean ssl;
   private boolean tls = true;

   @Unwrap
   public Session getSession() throws NamingException
   {
      if ( session==null ) 
      {
         // This simulates an EVENT scope component
         return  (Session) Naming.getInitialContext().lookup( getSessionJndiName() );
      }
      else 
      {
         return session;
      }
   }

   /**
    * Initialise mail session
    * 
    * Unless disabled, if a mail Session can be found in JNDI, then just manage
    * be a simple wrapper; otherwise configure the session as specified in
    * components.xml
    */
   @Create
	public void create()
	{
      if ( getSessionJndiName()==null ) 
      {
         createSession();
      }
	}

   private void createSession()
   {
      log.info("Creating JavaMail Session (" + getHost() + ':' + getPort() + ")");
      
      Properties properties = new Properties();
  
      // Enable debugging if set
      properties.put("mail.debug", isDebug());
  
      if ( getUsername()!=null && getPassword()==null )
      {
      	log.warn("username supplied without a password (if an empty password is required supply an empty string)");
      }
      if ( getUsername()==null && getPassword()!=null )
      {
      	log.warn("password supplied without a username (if no authentication required supply neither)");
      }
      
      if ( getHost()!=null )
      {
         if (isSsl()) 
         {
            properties.put("mail.smtps.host", getHost());
         }
         else
         {
            properties.put("mail.smtp.host", getHost());
         }
         
      }
      if ( getPort()!=null ) 
      {
         if (isSsl())
         {
            properties.put("mail.smtps.port", getPort().toString());
         }
         else
         {
            properties.put("mail.smtp.port", getPort().toString());
         }
      }
      else
      {
         if (isSsl())
         {
            properties.put("mail.smtps.port", "465");
         }
         else
         {
            properties.put("mail.smtp.port", "25");
         }
      }
      
      if (isSsl())
      {
         properties.put("mail.transport.protocol", "smtps");
      }
      else
      {
         properties.put("mail.transport.protocol", "smtp");
      }
  
      // Authentication if required
      Authenticator authenticator = null;
      if ( getUsername()!=null && getPassword()!=null )
      {
         if (isSsl())
         {
            properties.put("mail.smtps.auth", "true");
         } 
         else
         {
         
            properties.put("mail.smtp.auth", "true");
         }
      	authenticator = new Authenticator()
      	{
      		@Override
      		protected PasswordAuthentication getPasswordAuthentication()
      		{
      			return new PasswordAuthentication(getUsername(), getPassword());
      		}
      	};
      }
      
      // Use TLS (if supported)
      if (isTls())
      {
         properties.put("mail.smtp.starttls.enable", "true");
      }
  
      session = javax.mail.Session.getInstance(properties, authenticator);
      session.setDebug( isDebug() );
      
      log.info("connected to mail server");
   }

	public String getPassword()
	{
		return password;
	}

	/**
	 * @param password
	 *            The password to use to authenticate to the sending server. If
	 *            no authentication is required it should be left empty. Must be
	 *            supplied in conjunction with username.
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getUsername()
	{
		return username;
	}

	/**
	 * @param username
	 *            The username to use to authenticate to the server. If not set
	 *            then no authentication is used. Must be set in conjunction
	 *            with password.
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	public boolean isDebug()
	{
		return debug;
	}

	/**
	 * @param debug
	 *            Whether to display debug message logging. Warning, very
	 *            verbose.
	 */
	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	public String getHost()
	{
		return host;
	}

	/**
	 * @param host
	 *            The host to connect to. Used unless overriden by a protocol
	 *            specific host
	 */
	public void setHost(String host)
	{
		this.host = host;
	}

	public void setPort(Integer port)
	{
		this.port = port;
	}
	
	public Integer getPort()
	{
		return port;
	}
   
   public String getSessionJndiName()
   {
      return sessionJndiName;
   }

   public void setSessionJndiName(String jndiName)
   {
      this.sessionJndiName = jndiName;
   }

   public boolean isSsl()
   {
      return ssl;
   }

   public void setSsl(boolean ssl)
   {
      this.ssl = ssl;
   }
   
   public boolean isTls()
   {
      return tls;
   }
   
   public void setTls(boolean tls)
   {
      this.tls = tls;
   }

   public static Session instance() 
   {
      return (Session) Component.getInstance(MailSession.class);
   }

}
