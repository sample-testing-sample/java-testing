package com.example.servlets;

import com.example.dao.ExamDao;
import com.example.dao.ResultDao;
import com.example.dao.ActivityLogDao;
import com.example.models.Exam;
import com.example.models.Result;
import com.example.models.ActivityLog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminServlet extends HttpServlet {
    private ExamDao examDao = new ExamDao();
    private ResultDao resultDao = new ResultDao();
    private ActivityLogDao logDao = new ActivityLogDao();
    private Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(((com.example.models.User) session.getAttribute("user")).getRole())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            List<Exam> exams = examDao.getAllExams();
            List<Result> results = resultDao.getAllResults();
            // For simplicity, get logs for all users, but since no method, get for admin or add method
            List<ActivityLog> logs = logDao.getAllLogs();

            JsonObject data = new JsonObject();
            data.add("exams", gson.toJsonTree(exams));
            data.add("results", gson.toJsonTree(results));
            data.add("logs", gson.toJsonTree(logs));

            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(data));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8000");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}