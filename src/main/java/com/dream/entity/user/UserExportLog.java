package com.dream.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
public class UserExportLog extends AbstractPersistable<Long> {

    @ManyToOne
    @JsonIgnore
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date();

    // 0：成功，1：等待处理，2：正在处理，3：处理失败，4：成功但通知失败。
    private int status = 1;

    //七牛持久化 操作id
    private String persistentId;

    private String excelUrl;

    private String zipUrl;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public void setPersistentId(String persistentId) {
        this.persistentId = persistentId;
    }

    public String getExcelUrl() {
        return excelUrl;
    }

    public void setExcelUrl(String excelUrl) {
        this.excelUrl = excelUrl;
    }

    public String getZipUrl() {
        return zipUrl;
    }

    public void setZipUrl(String zipUrl) {
        this.zipUrl = zipUrl;
    }
}
