const API_BASE = 'http://localhost:8080/';

let currentUser = null;
let currentExam = null;
let timer = null;
let timeLeft = 0;

document.addEventListener('DOMContentLoaded', function() {
    const path = window.location.pathname;
    if (path.includes('dashboard.html')) {
        loadStudentDashboard();
    } else if (path.includes('admin.html')) {
        loadAdminDashboard();
    } else if (path.includes('exam.html')) {
        startExam();
    } else if (path.includes('result.html')) {
        showResult();
    }
});

function login() {
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    fetch(`${API_BASE}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `username=${username}&password=${password}`,
        credentials: 'include'
    })
    .then(response => response.json())
    .then(data => {
        if (data.role) {
            currentUser = data;
            if (data.role === 'admin') {
                window.location.href = 'admin.html';
            } else {
                window.location.href = 'dashboard.html';
            }
        } else {
            alert('Invalid credentials');
        }
    });
}

function register() {
    const username = document.getElementById('reg-username').value;
    const password = document.getElementById('reg-password').value;

    fetch(`${API_BASE}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `username=${username}&password=${password}`,
        credentials: 'include'
    })
    .then(response => response.json())
    .then(data => {
        alert(data.message || data.error);
    });
}

function logout() {
    currentUser = null;
    window.location.href = 'index.html';
}

function loadStudentDashboard() {
    fetch(`${API_BASE}/student`, { credentials: 'include' })
    .then(response => response.json())
    .then(exams => {
        const examsDiv = document.getElementById('exams-list');
        examsDiv.innerHTML = '<h2>Available Exams</h2>';
        exams.forEach(exam => {
            examsDiv.innerHTML += `<div><button onclick="takeExam(${exam.id})">${exam.title}</button></div>`;
        });
    });

    fetch(`${API_BASE}/results`, { credentials: 'include' })
    .then(response => response.json())
    .then(results => {
        const resultsDiv = document.getElementById('results-list');
        resultsDiv.innerHTML = '<h2>Your Results</h2>';
        results.forEach(result => {
            resultsDiv.innerHTML += `<div>Exam ID: ${result.examId}, Score: ${result.score}/${result.totalQuestions}</div>`;
        });
    });
}

function takeExam(examId) {
    window.location.href = `exam.html?id=${examId}`;
}

function startExam() {
    const urlParams = new URLSearchParams(window.location.search);
    const examId = urlParams.get('id');

    fetch(`${API_BASE}/getExam?id=${examId}`, { credentials: 'include' })
    .then(response => response.json())
    .then(exam => {
        currentExam = exam;
        document.getElementById('exam-title').textContent = exam.title;
        timeLeft = exam.duration * 60;
        startTimer();

        const questionsDiv = document.getElementById('questions');
        exam.questions.forEach((q, index) => {
            questionsDiv.innerHTML += `
                <div class="question">
                    <p>${index + 1}. ${q.questionText}</p>
                    <label><input type="radio" name="q${q.id}" value="A"> ${q.optionA}</label>
                    <label><input type="radio" name="q${q.id}" value="B"> ${q.optionB}</label>
                    <label><input type="radio" name="q${q.id}" value="C"> ${q.optionC}</label>
                    <label><input type="radio" name="q${q.id}" value="D"> ${q.optionD}</label>
                </div>
            `;
        });

        enableProctoring(examId);
    });
}

function startTimer() {
    timer = setInterval(() => {
        timeLeft--;
        document.getElementById('timer').textContent = `Time Left: ${Math.floor(timeLeft / 60)}:${(timeLeft % 60).toString().padStart(2, '0')}`;
        if (timeLeft <= 0) {
            submitExam();
        }
    }, 1000);
}

function enableProctoring(examId) {
    document.addEventListener('visibilitychange', () => {
        if (document.hidden) {
            logActivity('Tab switched', examId);
        }
    });

    document.addEventListener('contextmenu', e => {
        e.preventDefault();
        logActivity('Right-click attempted', examId);
    });

    document.addEventListener('copy', e => {
        e.preventDefault();
        logActivity('Copy attempted', examId);
    });

    document.addEventListener('paste', e => {
        e.preventDefault();
        logActivity('Paste attempted', examId);
    });

    document.addEventListener('keydown', e => {
        if (e.ctrlKey && (e.key === 'c' || e.key === 'v' || e.key === 'x')) {
            e.preventDefault();
            logActivity('Keyboard shortcut attempted', examId);
        }
    });

    if (document.documentElement.requestFullscreen) {
        document.documentElement.requestFullscreen();
        document.addEventListener('fullscreenchange', () => {
            if (!document.fullscreenElement) {
                logActivity('Exited fullscreen', examId);
                document.documentElement.requestFullscreen();
            }
        });
    }
}

function logActivity(activity, examId) {
    fetch(`${API_BASE}/logActivity`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `activity=${activity}&examId=${examId}`,
        credentials: 'include'
    });
}

function submitExam() {
    clearInterval(timer);
    const answers = {};
    currentExam.questions.forEach(q => {
        const selected = document.querySelector(`input[name="q${q.id}"]:checked`);
        if (selected) {
            answers[q.id] = selected.value;
        }
    });

    fetch(`${API_BASE}/submitExam`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `examId=${currentExam.id}&answers=${JSON.stringify(answers)}`,
        credentials: 'include'
    })
    .then(response => response.json())
    .then(result => {
        localStorage.setItem('examResult', JSON.stringify(result));
        window.location.href = 'result.html';
    });
}

function showResult() {
    const result = JSON.parse(localStorage.getItem('examResult'));
    document.getElementById('result').innerHTML = `Score: ${result.score}/${result.total}`;
}

function loadAdminDashboard() {
    fetch(`${API_BASE}/admin`, { credentials: 'include' })
    .then(response => response.json())
    .then(data => {
        const examsDiv = document.getElementById('exams-list');
        examsDiv.innerHTML = '<h2>Exams</h2>';
        data.exams.forEach(exam => {
            examsDiv.innerHTML += `<div>${exam.title} (ID: ${exam.id})</div>`;
        });

        const resultsDiv = document.getElementById('results-list');
        resultsDiv.innerHTML = '<h2>Results</h2>';
        data.results.forEach(result => {
            resultsDiv.innerHTML += `<div>User ${result.userId}, Exam ${result.examId}: ${result.score}/${result.totalQuestions}</div>`;
        });

        const logsDiv = document.getElementById('logs-list');
        logsDiv.innerHTML = '<h2>Activity Logs</h2>';
        data.logs.forEach(log => {
            logsDiv.innerHTML += `<div>${log.timestamp}: ${log.activity}</div>`;
        });
    });
}

function addQuestion() {
    const questionsDiv = document.getElementById('questions-input');
    const qIndex = questionsDiv.children.length;
    questionsDiv.innerHTML += `
        <div class="question-input">
            <input type="text" placeholder="Question ${qIndex + 1}" id="q${qIndex}">
            <input type="text" placeholder="Option A" id="a${qIndex}">
            <input type="text" placeholder="Option B" id="b${qIndex}">
            <input type="text" placeholder="Option C" id="c${qIndex}">
            <input type="text" placeholder="Option D" id="d${qIndex}">
            <select id="correct${qIndex}">
                <option value="A">A</option>
                <option value="B">B</option>
                <option value="C">C</option>
                <option value="D">D</option>
            </select>
        </div>
    `;
}

function createExam() {
    const title = document.getElementById('exam-title').value;
    const duration = document.getElementById('exam-duration').value;
    const questions = [];
    const questionsDiv = document.getElementById('questions-input');
    for (let i = 0; i < questionsDiv.children.length; i++) {
        const q = {
            question: document.getElementById(`q${i}`).value,
            optionA: document.getElementById(`a${i}`).value,
            optionB: document.getElementById(`b${i}`).value,
            optionC: document.getElementById(`c${i}`).value,
            optionD: document.getElementById(`d${i}`).value,
            correct: document.getElementById(`correct${i}`).value
        };
        questions.push(q);
    }

    fetch(`${API_BASE}/createExam`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `title=${title}&duration=${duration}&questions=${JSON.stringify(questions)}`,
        credentials: 'include'
    })
    .then(response => response.json())
    .then(data => {
        alert(data.message || data.error);
        loadAdminDashboard();
    });
}