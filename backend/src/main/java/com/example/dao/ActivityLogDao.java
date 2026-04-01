package com.example.dao;

import com.example.models.ActivityLog;
import com.example.utils.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDao {
    public void logActivity(ActivityLog log) throws SQLException {
        String sql = "INSERT INTO activity_logs (user_id, exam_id, activity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, log.getUserId());
            if (log.getExamId() != null) {
                stmt.setInt(2, log.getExamId());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setString(3, log.getActivity());
            stmt.executeUpdate();
        }
    }

    public List<ActivityLog> getLogsByUserId(int userId) throws SQLException {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                logs.add(new ActivityLog(rs.getInt("id"), rs.getInt("user_id"), rs.getInt("exam_id"),
                        rs.getString("activity"), rs.getTimestamp("timestamp")));
            }
        }
        return logs;
    }

    public List<ActivityLog> getAllLogs() throws SQLException {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                logs.add(new ActivityLog(rs.getInt("id"), rs.getInt("user_id"), rs.getInt("exam_id"),
                        rs.getString("activity"), rs.getTimestamp("timestamp")));
            }
        }
        return logs;
    }
}