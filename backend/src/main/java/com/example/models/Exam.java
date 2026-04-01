package com.example.models;

import java.util.List;

public class Exam {
    private int id;
    private String title;
    private int duration;
    private List<Question> questions;

    public Exam() {}

    public Exam(int id, String title, int duration) {
        this.id = id;
        this.title = title;
        this.duration = duration;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}