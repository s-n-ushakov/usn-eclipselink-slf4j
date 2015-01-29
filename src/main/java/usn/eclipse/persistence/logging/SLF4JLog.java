/*
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     Sergey Ushakov, <s-n-ushakov@yandex.ru> - implementation of SLF4JLog,
 *         rewritten after org.eclipse.persistence.logging.JavaLog
 */

package usn.eclipse.persistence.logging;

import java.io.OutputStream;
import java.io.Writer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.sessions.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

/**
 * An EclipseLink {@link org.eclipse.persistence.logging.SessionLog SessionLog}
 * implementation for logging messages through {@link org.slf4j SLF4J}.
 * 
 * <p>Implemented after EclipseLink
 * {@link org.eclipse.persistence.logging.JavaLog}.</p>
 * 
 * <p>See also other implementations:</p>
 * <ul>
 * <li><a
 *     href="http://adfinmunich.blogspot.ru/2012/03/eclipselinksessionlogger-with-slf4j.html"
 *     >http://adfinmunich.blogspot.ru/2012/03/eclipselinksessionlogger-with-slf4j.html</a
 *     ></li>
 * <li><a
 *     href="https://github.com/PE-INTERNATIONAL/org.eclipse.persistence.logging.slf4j"
 *     >https://github.com/PE-INTERNATIONAL/org.eclipse.persistence.logging.slf4j</a
 *     ></li>
 * </ul>
 * 
 * @author Sergey Ushakov, s-n-ushakov@yandex.ru
 * @version 2015-01-20
 * 
 * <p>See <a
 *     href="http://eclipse.org/eclipselink/documentation/2.5/jpa/extensions/p_logging_logger.htm"
 *     >http://eclipse.org/eclipselink/documentation/2.5/jpa/extensions/p_logging_logger.htm</a
 *     ></p>
 * <p>See <a
 *     href="http://wiki.eclipse.org/EclipseLink/Examples/JPA/Logging"
 *     >http://wiki.eclipse.org/EclipseLink/Examples/JPA/Logging</a
 *     ></p>
 */
public class SLF4JLog
    extends AbstractSessionLog
  {

    /**
     * Logger namespaces.
     */
    public static final String TOPLINK_NAMESPACE = "org.eclipse.persistence";
    public static final String DEFAULT_TOPLINK_NAMESPACE =
        TOPLINK_NAMESPACE + ".default";
    public static final String SESSION_TOPLINK_NAMESPACE =
        TOPLINK_NAMESPACE + ".session";

    /**
     * SLF4J log levels.
     */
    protected enum SLF4JLevel
      {
        TRACE (LocationAwareLogger.TRACE_INT),
        DEBUG (LocationAwareLogger.DEBUG_INT),
        INFO  (LocationAwareLogger.INFO_INT),
        WARN  (LocationAwareLogger.WARN_INT),
        ERROR (LocationAwareLogger.ERROR_INT),
        OFF   (-1);

        public final int levelInt;

        SLF4JLevel (int levelInt)
          {
            this.levelInt = levelInt;
          } // SLF4JLevel
      }; // enum SLF4JLevel

    /**
     * Mapping of TopLink logging levels to SLF4J logging levels,
     * indexed by the former.
     */
    protected static final SLF4JLevel [] levels =
      new SLF4JLevel []
        {
          SLF4JLevel.TRACE, // all
          SLF4JLevel.TRACE, // FINEST
          SLF4JLevel.DEBUG, // FINER
          SLF4JLevel.DEBUG, // FINE
          SLF4JLevel.INFO,  // CONFIG
          SLF4JLevel.INFO,  // INFO
          SLF4JLevel.WARN,  // WARNING
          SLF4JLevel.ERROR, // SEVERE
          SLF4JLevel.OFF    // OFF
        };

    /**
     * A {@link Map} to store all the namespace strings.
     * The keys are category names; the values are namespace strings.
     */
    protected Map<String, String> namespaceMap = new HashMap<String, String> ();

    /**
     * Namespace for a session-specific logger,
     * i.e. {@literal "org.eclipse.persistence.session.<sessionname>"}.
     */
    protected String sessionNamespace;

    /**
     * A {@link Map} to store all the {@link Logger} instances against their
     * categories.
     */
    protected Map<String, Logger> categoryLoggers =
        new HashMap<String, Logger> ();

    /**
     * The constructor.
     */
    public SLF4JLog ()
      {
        super ();
        addLogger (DEFAULT_TOPLINK_NAMESPACE, DEFAULT_TOPLINK_NAMESPACE);
    }

    /**
     * Obtain a logger for a category and add it to the catagoryLoggers.
     * 
     * @param category a logger category
     * @param namespace a namespace for the logger
     */
    protected void addLogger (String category, String namespace)
      {
        categoryLoggers.put (category,
                             LoggerFactory.getLogger (namespace));
      } // addLogger

    /**
     * Obtain the effective log level for the namespace extracted from session
     * and category.
     *
     * @return the effective log level
     */
    @Override // org.eclipse.persistence.logging.AbstractSessionLog / org.eclipse.persistence.logging.SessionLog
    public int getLevel(String category) {
        Logger logger = getLogger (category);
        SLF4JLevel slf4jLevel =
            (logger.isTraceEnabled ()) ? SLF4JLevel.TRACE :
            (logger.isDebugEnabled ()) ? SLF4JLevel.DEBUG :
            (logger.isInfoEnabled  ()) ? SLF4JLevel.INFO :
            (logger.isWarnEnabled  ()) ? SLF4JLevel.WARN :
            (logger.isErrorEnabled ()) ? SLF4JLevel.ERROR :
                                         SLF4JLevel.OFF;

        // obtain the TopLink logging level using the levels mapping,
        // starting from FINEST
        for (int i = 1; i < levels.length ; ++i)
          {
            if (slf4jLevel == levels [i])
              {
                return i;
              }
          }
        return OFF; // should not reach here...
    }

    /**
     * @deprecated there is no SLF4J API for this method
     */
    @Deprecated
    @Override // org.eclipse.persistence.logging.AbstractSessionLog / org.eclipse.persistence.logging.SessionLog
    public void setLevel (int level)
      {
        // ignore silently
      } // setLevel

    /**
     * @deprecated there is no SLF4J API for this method
     */
    @Deprecated
    @Override // org.eclipse.persistence.logging.AbstractSessionLog / org.eclipse.persistence.logging.SessionLog
    public void setLevel (final int level, String category)
      {
        // ignore silently
      } // setLevel

    /**
     * @deprecated there is no SLF4J API for this method
     */
    @Deprecated
    @Override // org.eclipse.persistence.logging.AbstractSessionLog
    public void setWriter (Writer writer)
      {
        // ignore silently
      } // setWriter

    /**
     * @deprecated there is no SLF4J API for this method
     */
    @Deprecated
    @Override // org.eclipse.persistence.logging.AbstractSessionLog / org.eclipse.persistence.logging.SessionLog
    public void setWriter (OutputStream fileOutputStream)
      {
        // ignore silently
      } // setWriter

    /**
     * Obtain a namespace for a category from the map.
     * 
     * @param category the category to find namespace for
     * @return the namespace for the given category
     */
    protected String getNamespaceString (String category)
      {
        if (session == null)
            {
              return DEFAULT_TOPLINK_NAMESPACE;
            }
          else if ((category == null) || (category.length() == 0))
            {
              return sessionNamespace;
            }
          else
            {
              return namespaceMap.get (category);
            }
      } // getNamespaceString

    /**
     * Obtain a {@link Logger} instance for given category
     * 
     * @param category the category to find a Logger for
     * @return the {@link Logger} instance for given category
     */
    protected Logger getLogger (String category)
      {
        if (session == null)
            {
              return categoryLoggers.get (DEFAULT_TOPLINK_NAMESPACE);
            }
          else if ((category == null) ||
                   (category.length () == 0) ||
                   !categoryLoggers.containsKey (category))
            {
              return categoryLoggers.get (sessionNamespace);
            }
          else
            {
              Logger logger = categoryLoggers.get (category);
              // if session != null,
              // categoryLoggers should have an entry for this category
              assert logger != null;
              return logger;
            }
      } // getLogger

    /**
     * Set the session and session namespace.
     *
     * @param session a {@link Session} instance to set
     */
    @Override // org.eclipse.persistence.logging.AbstractSessionLog / org.eclipse.persistence.logging.SessionLog
    public void setSession (Session session)
      {
        super.setSession (session);
        if (session == null)
          {
            return;
          }
        String sessionName = session.getName ();
        if ((sessionName != null) && (sessionName.length () != 0))
            {
              sessionNamespace = SESSION_TOPLINK_NAMESPACE + "." + sessionName;
            }
          else
            {
              sessionNamespace = DEFAULT_TOPLINK_NAMESPACE;
            }

        // initialize loggers eagerly
        addLogger (sessionNamespace, sessionNamespace);
        for (int i = 0; i < loggerCatagories.length; ++i)
          {
            String loggerCategory =  loggerCatagories [i]; 
            String loggerNameSpace = sessionNamespace + "." + loggerCategory;
            namespaceMap.put (loggerCategory, loggerNameSpace);
            addLogger (loggerCategory, loggerNameSpace);
          }
      } // setSession

    @Override // org.eclipse.persistence.logging.AbstractSessionLog / org.eclipse.persistence.logging.SessionLog
    public void log (SessionLogEntry entry)
      {
        Logger logger = getLogger (entry.getNameSpace ());
        SLF4JLevel slf4jLevel = levels [entry.getLevel ()];
        if (((slf4jLevel == SLF4JLevel.TRACE) && !logger.isTraceEnabled ()) ||
            ((slf4jLevel == SLF4JLevel.DEBUG) && !logger.isDebugEnabled ()) ||
            ((slf4jLevel == SLF4JLevel.INFO)  && !logger.isInfoEnabled  ()) ||
            ((slf4jLevel == SLF4JLevel.WARN)  && !logger.isWarnEnabled  ()) ||
            ((slf4jLevel == SLF4JLevel.ERROR) && !logger.isErrorEnabled ()) ||
            ((slf4jLevel == SLF4JLevel.OFF)))
          {
            return;
          }

        // format the message closely to EclipseLink fashion
        StringBuffer sb = new StringBuffer ();
        if (shouldPrintSession ())
          {
            AbstractSession session = entry.getSession ();
            if (session != null)
              {
                sb.append (getSessionString (entry.getSession ()));
                sb.append ("--");
              }
          }
        if (shouldPrintConnection ())
          {
            Accessor connection = entry.getConnection ();
            if (connection != null)
              {
                sb.append (entry.getConnection ());
                sb.append ("--");
              }
          }
        sb.append (formatMessage (entry));
        String msg = sb.toString ();

        Throwable t = entry.getException ();

        if (logger instanceof LocationAwareLogger)
            {
              // hide current location, as it is meaningless
              ((LocationAwareLogger) logger)
                .log (null, null, slf4jLevel.levelInt, msg, null, t);
            }
          else
            {
              switch (slf4jLevel)
                {
                  case TRACE:
                    logger.trace (msg, t);
                    break;
                  case DEBUG:
                    logger.debug (msg, t);
                    break;
                  case INFO:
                    logger.info (msg, t);
                    break;
                  case WARN:
                    logger.warn (msg, t);
                    break;
                  case ERROR:
                    logger.error (msg, t);
                    break;
                  case OFF:
                    break;
                }
            }
      } // log

    @Override // org.eclipse.persistence.logging.AbstractSessionLog / org.eclipse.persistence.logging.SessionLog
    public void throwing (Throwable throwable)
      {
        getLogger (null).error (null, throwable);
      } // throwing

    /**
     * <p>Clone the log.</p>
     * 
     * <p>Each session owns its own session log because session is stored in the
     * session log.</p>
     * 
     * <p>There is no special treatment required for cloning here. The state of
     * this object is described by member variable {@code categoryLoggers}.
     * This state depends on session. If session for the clone is going to be
     * the same as session for this one, there is no need to do "deep" cloning.
     * If not, the session being cloned should call {@link #setSession(Session)}
     * on its {@code SLF4JLog} object to initialize it correctly.</p>
     * 
     * <p>Still to be verified whether this is applicable to SLF4J.</p>
     */
    @Override // org.eclipse.persistence.logging.AbstractSessionLog / org.eclipse.persistence.logging.SessionLog
    public Object clone ()
      {
        SLF4JLog cloneLog = (SLF4JLog) super.clone ();
        return cloneLog;
      } // clone

  } // class SLF4JLog
