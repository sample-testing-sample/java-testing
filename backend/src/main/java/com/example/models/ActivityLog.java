package com.example.models;

import java.sql.Timestamp;

public class ActivityLog {
    private int id;
    private int userId;
    private Integer examId;
    private String activity;
    private Timestamp timestamp;

    public ActivityLog() {}

    public ActivityLog(int id, int userId, Integer examId, String activity, Timestamp timestamp) {
        this.id = id;
        this.userId = userId;
        this.examId = examId;
        this.activity = activity;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getExamId() { return examId; }
    public void setExamId(Integer examId) { this.examId = examId; }

    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}