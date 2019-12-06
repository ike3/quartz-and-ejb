package ru.lanit.fz44.ejb.job;

import javax.ejb.Local;
import javax.ejb.Remote;

@Remote
public interface PpaJobRemote {
    void process();
}
