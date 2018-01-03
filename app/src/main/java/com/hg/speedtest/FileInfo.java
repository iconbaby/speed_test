package com.hg.speedtest;


public class FileInfo {

    public double currentSpeed;

    public double aveSpeed;

    public long hadFinishByte;

    public long totalByte;
    public long spendTime;
    public long startTime;
    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public double getAveSpeed() {
        return aveSpeed;
    }

    public void setAveSpeed(double aveSpeed) {
        this.aveSpeed = aveSpeed;
    }

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public long getHadFinishByte() {
        return hadFinishByte;
    }

    public void setHadFinishByte(long hadFinishByte) {
        this.hadFinishByte = hadFinishByte;
    }

    public long getTotalByte() {
        return totalByte;
    }

    public void setTotalByte(long totalByte) {
        this.totalByte = totalByte;
    }

    public long getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(long spendTime) {
        this.spendTime = spendTime;
    }


}
