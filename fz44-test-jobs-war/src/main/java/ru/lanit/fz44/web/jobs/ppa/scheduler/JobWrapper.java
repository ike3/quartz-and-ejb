package ru.lanit.fz44.web.jobs.ppa.scheduler;

import java.util.Properties;

import javax.ejb.NoSuchEJBException;
import javax.naming.*;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.lanit.fz44.ejb.job.PpaJobRemote;

public class JobWrapper implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobWrapper.class);

    public static final String BEAN_NAME = "BEAN_NAME";
    public static final int MAX_ATTEMPTS = 10;
    public static final long ATTEMPT_WAIT_TIMEOUT = 10000;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        String beanName = (String) context.getMergedJobDataMap().get(BEAN_NAME);

        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        p.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");

        PpaJobRemote remote = null;
        Exception lastException = null;
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            try {
                LOGGER.info("Looking for Job EJB by JNDI name: {}, Attempt {}", new Object[] { beanName, attempt + 1 });
                InitialContext initialContext = new InitialContext();
                remote = (PpaJobRemote) initialContext.lookup(beanName);
                remote.process();
            } catch (NoSuchEJBException e) {
                LOGGER.warn("Cannot lookup EJB by jndi name: {}, error: {}. This is not an error if the server is not fully started yet.",
                        new Object[] { beanName, e.getMessage() }, e);
                lastException = e;
                try { Thread.sleep(ATTEMPT_WAIT_TIMEOUT); } catch (InterruptedException e1) {}
                continue;
            } catch (NamingException e) {
                LOGGER.error("Cannot lookup EJB by jndi name: " + beanName, e);
                throw new JobExecutionException(e);
            }
            LOGGER.info("Lookup successful for Job EJB by JNDI name: {} in attempt {}", new Object[] { beanName, attempt + 1 });
            break;
        }

        if (lastException != null) {
            LOGGER.error("Cannot lookup EJB by jndi name: " + beanName, lastException);
            throw new RuntimeException(lastException);
        }

    }

}
