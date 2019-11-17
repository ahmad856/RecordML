package com.example.recordml.models;

import java.io.Serializable;

public class Recording implements Serializable {
    private String stamp;
    private String voiceFilePath;
    private String txtFilePath;
    private Stats stats;
    private String txtFileName;

    public String getStamp() { return stamp; }

    public void setStamp(String stamp) { this.stamp = stamp; }

    public String getVoiceFilePath() { return voiceFilePath; }

    public void setVoiceFilePath(String voiceFilePath) { this.voiceFilePath = voiceFilePath; }

    public String getTxtFilePath() { return txtFilePath; }

    public void setTxtFilePath(String txtFilePath) { this.txtFilePath = txtFilePath; }

    public String getTxtFileName() { return txtFileName; }

    public void setTxtFileName(String txtFileName) { this.txtFileName = txtFileName; }

    public Stats getStats() { return stats; }

    public void setStats(Stats stats) { this.stats = stats; }
}