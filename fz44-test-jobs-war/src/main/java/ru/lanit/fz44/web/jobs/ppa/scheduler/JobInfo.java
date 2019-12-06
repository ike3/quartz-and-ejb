package ru.lanit.fz44.web.jobs.ppa.scheduler;

/**
 * User: Литвяк Валерий
 * Date: 22.05.2016
 */
class JobInfo {
    private String name;
    private boolean enable;
    private String revision;
    private String bean;
    private String cron;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }
}
