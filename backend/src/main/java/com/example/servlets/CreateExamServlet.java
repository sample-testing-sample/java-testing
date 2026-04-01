package com.example.servlets;

import com.example.dao.ExamDao;
import com.example.models.Exam;
import com.example.models.Question;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CreateExamServlet extends HttpServlet {
    private ExamDao examDao = new ExamDao();
    private Gson gson = new Gson();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8000");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(((com.example.models.User) session.getAttribute("user")).getRole())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String title = request.getParameter("title");
        int duration = Integer.parseInt(request.getParameter("duration"));
        String questionsJson = request.getParameter("questions");

        Exam exam = new Exam();
        exam.setTitle(title);
        exam.setDuration(duration);

        try {
            examDao.createExam(exam);

            JsonArray questionsArray = JsonParser.parseString(questionsJson).getAsJsonArray();
            for (JsonElement elem : questionsArray) {
                JsonObject qObj = elem.getAsJsonObject();
                Question q = new Question();
                q.setExamId(exam.getId());
                q.setQuestionText(qObj.get("question").getAsString());
                q.setOptionA(qObj.get("optionA").getAsString());
                q.setOptionB(qObj.get("optionB").getAsString());
                q.setOptionC(qObj.get("optionC").getAsString());
                q.setOptionD(qObj.get("optionD").getAsString());
                q.setCorrectAnswer(qObj.get("correct").getAsString());
                examDao.addQuestion(q);
            }

            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Exam created successfully\"}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to create exam\"}");
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