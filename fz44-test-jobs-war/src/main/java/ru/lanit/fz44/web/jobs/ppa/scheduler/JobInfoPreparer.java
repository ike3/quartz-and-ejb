package ru.lanit.fz44.web.jobs.ppa.scheduler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Литвяк Валерий
 * Date: 22.05.2016
 */
public class JobInfoPreparer {
    private static final String JOB_ENABLE_POSTFIX_PARAM = "enable";
    private static final String JOB_REVISION_POSTFIX_PARAM = "revision";
    private static final String JOB_EJB_POSTFIX_PARAM = "ejb";
    private static final String JOB_CRON_POSTFIX_PARAM = "cron";

    private interface ParamSetter {
        void setParam(JobInfo job, String value);
    }

    private class EnableSetter implements ParamSetter {

        public void setParam(JobInfo job, String value) {
            job.setEnable(Boolean.valueOf(value));
        }
    }

    private class RevisionSetter implements ParamSetter {

        public void setParam(JobInfo job, String value) {
            job.setRevision(value);
        }
    }

    private class EjbSetter implements ParamSetter {

        public void setParam(JobInfo job, String value) {
            job.setBean(value);
        }
    }

    private class CronSetter implements ParamSetter {

        public void setParam(JobInfo job, String value) {
            job.setCron(value);
        }
    }

    private Pattern pattern = Pattern.compile("^job\\.(.*)\\.(" + JOB_ENABLE_POSTFIX_PARAM + "|" + JOB_REVISION_POSTFIX_PARAM + "|" + JOB_EJB_POSTFIX_PARAM + "|" + JOB_CRON_POSTFIX_PARAM + ")$");
    private Map<String, ParamSetter> paramSetters = new HashMap<String, ParamSetter>();
    //
    private Map<String, JobInfo> jobs = new HashMap<String, JobInfo>();

    public JobInfoPreparer() {
        paramSetters.put(JOB_ENABLE_POSTFIX_PARAM, new EnableSetter());
        paramSetters.put(JOB_REVISION_POSTFIX_PARAM, new RevisionSetter());
        paramSetters.put(JOB_EJB_POSTFIX_PARAM, new EjbSetter());
        paramSetters.put(JOB_CRON_POSTFIX_PARAM, new CronSetter());
    }

    public Collection<JobInfo> prepare(Map<String, String> params) {
        for (Map.Entry<String, String> param : params.entrySet()) {
            String paramName = param.getKey();
            String paramValue = param.getValue();
            //
            Matcher matcher = pattern.matcher(paramName);
            if (matcher.matches()) {
                String jobName = matcher.group(1);
                String attribute = matcher.group(2);
                //
                JobInfo job = getJob(jobName);
                //
                paramSetters.get(attribute).setParam(job, paramValue);
            }
        }
        //
        return jobs.values();
    }

    private JobInfo getJob(String jobName) {
        JobInfo job = jobs.get(jobName);
        if (job == null) {
            job = new JobInfo();
            job.setName(jobName);
            //
            jobs.put(jobName, job);
        }
        return job;
    }
}
