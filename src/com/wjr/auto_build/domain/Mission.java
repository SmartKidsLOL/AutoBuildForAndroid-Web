package com.wjr.auto_build.domain;

import java.io.Serializable;

/**
 * Created by 王金瑞
 * 2018/12/9
 * 18:27
 * com.wjr.auto_build.domain
 */
public class Mission implements Serializable {

    private String missionId;
    private String zipName;
    private String downloadName;
    private int missionStatus;
    private boolean isExist;

    public Mission() {
    }

    public Mission(String missionId, String zipName, int missionStatus) {
        this.missionId = missionId;
        this.zipName = zipName;
        this.missionStatus = missionStatus;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getZipName() {
        return zipName;
    }

    public void setZipName(String zipName) {
        this.zipName = zipName;
    }

    public int getMissionStatus() {
        return missionStatus;
    }

    public void setMissionStatus(int missionStatus) {
        this.missionStatus = missionStatus;
    }

    public String getDownloadName() {
        return downloadName;
    }

    public void setDownloadName(String downloadName) {
        this.downloadName = downloadName;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "missionId='" + missionId + '\'' +
                ", zipName='" + zipName + '\'' +
                ", downloadName='" + downloadName + '\'' +
                ", missionStatus=" + missionStatus +
                ", isExist=" + isExist +
                '}';
    }
}
