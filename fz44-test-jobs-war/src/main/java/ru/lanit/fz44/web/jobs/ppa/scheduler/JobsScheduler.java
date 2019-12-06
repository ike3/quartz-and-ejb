package ru.lanit.fz44.web.jobs.ppa.scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * User: Литвяк Валерий
 * Date: 21.05.2016
 */
public class JobsScheduler implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobsScheduler.class);
    //
    public void contextInitialized(ServletContextEvent event) {
        LOGGER.info("Start PPA jobs initializer");
        //
        Map<String, String> params = getParams(event.getServletContext());
        //
        try {
            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler sched = sf.getScheduler();
            if (!sched.isStarted()) {
                 sched.start();
            }

            Collection<JobInfo> jobs = new JobInfoPreparer().prepare(params);
            //
            for (JobInfo job : jobs) {
                JobKey jobKey = new JobKey(job.getName());
                JobDetail existJobDetail = sched.getJobDetail(jobKey);
                //
                boolean needNewTask = job.isEnable();
                String actualJobName = getJobFullName(job.getName(), job.getRevision());
                //
                if (existJobDetail != null) {
                    LOGGER.info("Found PPA task " + existJobDetail.getKey().getName());
                    //
                    sched.deleteJob(jobKey);
                }
                //
                if (needNewTask) {
                    JobDataMap jobData = new JobDataMap();
                    jobData.put(JobWrapper.BEAN_NAME, job.getBean());
                    JobDetail taskJobDetail = JobBuilder.newJob(JobWrapper.class)
                            .withIdentity(jobKey)
                            .build();
                    Trigger taskTrigger = TriggerBuilder.newTrigger()
                            .withIdentity(actualJobName)
                            .usingJobData(jobData)
                            .withSchedule(CronScheduleBuilder.cronSchedule(job.getCron())).build();
                    Date nextCall = sched.scheduleJob(taskJobDetail, taskTrigger);
                    //
                    LOGGER.info("PPA SCHEDULER: Task created, name = {}, ejb = {}, next call = {}", new Object[]{ actualJobName, job.getBean(), nextCall});
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private String getJobFullName(String name, String revision) {
        return name + "_" + revision;
    }

    private Map<String, String> getParams(ServletContext context) {
        Map<String, String> params = new HashMap<String, String>();
        //
        Enumeration paramNames = context.getInitParameterNames();
        while (paramNames.hasMoreElements()) {
            Object o = paramNames.nextElement();
            if (o != null && o instanceof String) {
                String paramName = (String) o;
                params.put(paramName, context.getInitParameter(paramName));
            }
        }
        //
        return params;
    }

    public void contextDestroyed(ServletContextEvent event) {}
}
