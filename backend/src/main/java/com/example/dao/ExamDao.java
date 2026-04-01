package com.example.dao;

import com.example.models.Exam;
import com.example.models.Question;
import com.example.utils.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExamDao {
    public void createExam(Exam exam) throws SQLException {
        String sql = "INSERT INTO exams (title, duration) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, exam.getTitle());
            stmt.setInt(2, exam.getDuration());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                exam.setId(rs.getInt(1));
            }
        }
    }

    public Exam getExamById(int id) throws SQLException {
        String sql = "SELECT * FROM exams WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Exam exam = new Exam(rs.getInt("id"), rs.getString("title"), rs.getInt("duration"));
                exam.setQuestions(getQuestionsByExamId(id));
                return exam;
            }
        }
        return null;
    }

    public List<Exam> getAllExams() throws SQLException {
        List<Exam> exams = new ArrayList<>();
        String sql = "SELECT * FROM exams";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Exam exam = new Exam(rs.getInt("id"), rs.getString("title"), rs.getInt("duration"));
                exam.setQuestions(getQuestionsByExamId(rs.getInt("id")));
                exams.add(exam);
            }
        }
        return exams;
    }

    public void addQuestion(Question question) throws SQLException {
        String sql = "INSERT INTO questions (exam_id, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, question.getExamId());
            stmt.setString(2, question.getQuestionText());
            stmt.setString(3, question.getOptionA());
            stmt.setString(4, question.getOptionB());
            stmt.setString(5, question.getOptionC());
            stmt.setString(6, question.getOptionD());
            stmt.setString(7, question.getCorrectAnswer());
            stmt.executeUpdate();
        }
    }

    private List<Question> getQuestionsByExamId(int examId) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE exam_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                questions.add(new Question(rs.getInt("id"), rs.getInt("exam_id"), rs.getString("question_text"),
                        rs.getString("option_a"), rs.getString("option_b"), rs.getString("option_c"),
                        rs.getString("option_d"), rs.getString("correct_answer")));
            }
        }
        return questions;
    }
}