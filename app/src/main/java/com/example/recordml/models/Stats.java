package com.example.recordml.models;

import java.io.Serializable;

public class Stats implements Serializable {

    private String fileContent;
    private String mostOccurredWord;
    private int wordCount;
    private String longestWord;
    private String shortestWord;
    private String leastOccurredWord;

    public String getFileContent() { return fileContent; }

    public void setFileContent(String fileContent) { this.fileContent = fileContent; }

    public String getMostOccurredWord() { return mostOccurredWord; }

    public void setMostOccurredWord(String mostOccurredWord) { this.mostOccurredWord = mostOccurredWord; }

    public int getWordCount() { return wordCount; }

    public void setWordCount(int wordCount) { this.wordCount = wordCount; }

    public String getLongestWord() { return longestWord; }

    public void setLongestWord(String longestWord) { this.longestWord = longestWord; }

    public String getShortestWord() { return shortestWord; }

    public void setShortestWord(String shortestWord) { this.shortestWord = shortestWord; }

    public String getLeastOccurredWord() { return leastOccurredWord; }

    public void setLeastOccurredWord(String leastOccurredWord) { this.leastOccurredWord = leastOccurredWord; }
}
