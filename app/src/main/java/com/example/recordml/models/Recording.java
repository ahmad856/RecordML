package com.example.recordml.models;

import java.io.File;

public class Recording {
    private String stamp;
    private String voiceFile;
    private String txtFile;
    private Stats stats;

    public String getStamp() { return stamp; }

    public void setStamp(String stamp) { this.stamp = stamp; }

    public String getVoiceFile() { return voiceFile; }

    public void setVoiceFile(String voiceFile) { this.voiceFile = voiceFile; }

    public String getTxtFile() { return txtFile; }

    public void setTxtFile(String txtFile) { this.txtFile = txtFile; }

    public Stats getStats() { return stats; }

    public void setStats(Stats stats) { this.stats = stats; }
}