package com.example.servlets;

import com.example.dao.ExamDao;
import com.example.dao.ResultDao;
import com.example.models.Exam;
import com.example.models.Question;
import com.example.models.Result;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SubmitExamServlet extends HttpServlet {
    private ExamDao examDao = new ExamDao();
    private ResultDao resultDao = new ResultDao();
    private Gson gson = new Gson();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8000");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        com.example.models.User user = (com.example.models.User) session.getAttribute("user");
        int examId = Integer.parseInt(request.getParameter("examId"));
        String answersJson = request.getParameter("answers");

        try {
            Exam exam = examDao.getExamById(examId);
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> answers = gson.fromJson(answersJson, type);

            int score = 0;
            for (Question q : exam.getQuestions()) {
                String userAnswer = answers.get(String.valueOf(q.getId()));
                if (q.getCorrectAnswer().equals(userAnswer)) {
                    score++;
                }
            }

            Result result = new Result();
            result.setUserId(user.getId());
            result.setExamId(examId);
            result.setScore(score);
            result.setTotalQuestions(exam.getQuestions().size());
            resultDao.saveResult(result);

            response.setContentType("application/json");
            JsonObject res = new JsonObject();
            res.addProperty("score", score);
            res.addProperty("total", exam.getQuestions().size());
            response.getWriter().write(gson.toJson(res));
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