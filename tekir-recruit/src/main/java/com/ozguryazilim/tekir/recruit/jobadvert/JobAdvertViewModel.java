package com.ozguryazilim.tekir.recruit.jobadvert;

import com.ozguryazilim.telve.entities.ViewModel;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author deniz
 */
public class JobAdvertViewModel implements ViewModel, Serializable {

    private Long id;
    private String code;
    private String topic;
    private String info;
    private Date startDate;
    private Date endDate;
    private String owner;
    private String status;
    private List<String> skills;

    public JobAdvertViewModel(Long id, String code,
                              String topic, String info, Date startDate,
                              Date endDate, String owner, String status, List<String> skills) {
        this.id = id;
        this.code = code;
        this.topic = topic;
        this.info = info;
        this.startDate = startDate;
        this.endDate = endDate;
        this.owner = owner;
        this.status = status;
        this.skills = skills;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}