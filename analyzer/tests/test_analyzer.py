from unittest.mock import patch

from app.analyzer import analyze_code, run_rule_checks


def test_hardcoded_secret():
    code = 'password = "secret123"'

    issues = run_rule_checks(code, "python")

    assert len(issues) == 1
    assert issues[0]["type"] == "HARDCODED_SECRET"
    assert issues[0]["line"] == 1

def test_system_out():
    code = 'System.out.println("Hello");'

    issues = run_rule_checks(code, "java")

    assert len(issues) == 1
    assert issues[0]["type"] == "SYSTEM_OUT"
    assert issues[0]["line"] == 1


def test_no_issues():
    code = 'String name = "Jokubas";'

    issues = run_rule_checks(code, "java")

    assert len(issues) == 0

def test_sql_injection():
    code = 'query = "SELECT * FROM users WHERE id = " + user_id'

    issues = run_rule_checks(code, "java")

    assert len(issues) == 1
    assert issues[0]["type"] == "SQL_INJECTION"
    assert issues[0]["line"] == 1


def test_hardcoded_secret_line_number():
    code = """String name = "Jokubas";
String age = "20";
String password = "secret123";"""

    issues = run_rule_checks(code, "java")

    assert len(issues) == 1
    assert issues[0]["type"] == "HARDCODED_SECRET"
    assert issues[0]["line"] == 3


@patch("app.analyzer.analyze_with_ai")
def test_analyze_code_combines_rule_and_ai_issues(mock_ai):
    mock_ai.return_value = {
        "is_code": True,
        "issues": [
            {
                "severity": "MEDIUM",
                "type": "CUSTOM_AI_ISSUE",
                "line": 2,
                "message": "AI found a problem.",
                "suggestion": "Fix it."
            }
        ]
    }

    code = """String name = "Jokubas";
String x = "hello";"""

    issues = analyze_code(code, "java")

    assert len(issues) == 1
    assert issues[0]["type"] == "CUSTOM_AI_ISSUE"


@patch("app.analyzer.analyze_with_ai")
def test_invalid_code(mock_ai):
    mock_ai.return_value = {
        "is_code": False,
        "issues": []
    }

    issues = analyze_code("I like pizza and coffee", "python")

    assert len(issues) == 1
    assert issues[0]["type"] == "INVALID_CODE"
    assert issues[0]["line"] is None


@patch("app.analyzer.analyze_with_ai")
def test_ai_failure_uses_rule_results(mock_ai):
    mock_ai.side_effect = Exception("AI service unavailable")

    code = 'password = "secret123"'

    issues = analyze_code(code, "python")

    assert len(issues) == 1
    assert issues[0]["type"] == "HARDCODED_SECRET"