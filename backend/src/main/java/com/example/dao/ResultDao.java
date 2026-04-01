package com.example.dao;

import com.example.models.Result;
import com.example.utils.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ResultDao {
    public void saveResult(Result result) throws SQLException {
        String sql = "INSERT INTO results (user_id, exam_id, score, total_questions) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, result.getUserId());
            stmt.setInt(2, result.getExamId());
            stmt.setInt(3, result.getScore());
            stmt.setInt(4, result.getTotalQuestions());
            stmt.executeUpdate();
        }
    }

    public List<Result> getResultsByUserId(int userId) throws SQLException {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT * FROM results WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(new Result(rs.getInt("id"), rs.getInt("user_id"), rs.getInt("exam_id"),
                        rs.getInt("score"), rs.getInt("total_questions"), rs.getTimestamp("submitted_at")));
            }
        }
        return results;
    }

    public List<Result> getAllResults() throws SQLException {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT * FROM results";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(new Result(rs.getInt("id"), rs.getInt("user_id"), rs.getInt("exam_id"),
                        rs.getInt("score"), rs.getInt("total_questions"), rs.getTimestamp("submitted_at")));
            }
        }
        return results;
    }
}