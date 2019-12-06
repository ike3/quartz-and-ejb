package ru.lanit.fz44.ejb.job.ppa;

import javax.ejb.*;

import ru.lanit.fz44.ejb.job.PpaJobRemote;

@Stateless(name = "SendMailMessageJob")
@TransactionAttribute(TransactionAttributeType.NEVER)
public class SendMailMessageJob implements PpaJobRemote {

    public void process() {
        System.err.println("Running SendMailMessageJob");
    }

}
