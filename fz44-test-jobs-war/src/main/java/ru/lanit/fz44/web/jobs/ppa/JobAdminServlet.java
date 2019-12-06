package ru.lanit.fz44.web.jobs.ppa;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.JobExecutionContextImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.spi.TriggerFiredBundle;

import ru.lanit.fz44.web.jobs.ppa.scheduler.JobWrapper;

public class JobAdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String beanName = req.getParameter("ejb");

        final JobWrapper wrapper = new JobWrapper();
        final JobExecutionContext context = new JobExecutionContextImpl(null,
                new TriggerFiredBundle(new JobDetailImpl(), new CronTriggerImpl(), null, false, null, null, null, null),
                wrapper);
        context.getMergedJobDataMap().put(JobWrapper.BEAN_NAME, beanName);

        new Thread() {
            @Override
            public void run() {
                try {
                    wrapper.execute(context);
                } catch (JobExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

        }.start();
    }

}
