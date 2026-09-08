import re
from app.ai_client import analyze_with_ai


def analyze_code(code: str, language: str):
    ai_result = analyze_with_ai(code, language)

    if not ai_result["is_code"]:
        return [{
            "severity": "LOW",
            "type": "INVALID_CODE",
            "line": None,
            "message": f"The provided input does not appear to be valid {language} code.",
            "suggestion": f"Provide valid {language} source code for review."
        }]

    issues = run_rule_checks(code, language)

    issues.extend(ai_result["issues"])

    return deduplicate_issues(issues)

def run_rule_checks(code: str, language: str):

    issues = []

    if language.lower() == "java":
        for line_number, line in enumerate(code.splitlines(), start=1):
            if "System.out.println" in line:
                issues.append({
                    "severity": "LOW",
                    "type": "SYSTEM_OUT",
                    "line": line_number,
                    "message": "Avoid using System.out.println() in production code."
                })

    if language.lower() == "python":
        # for Python
        pass

    pattern = r'(password|secret|api_key|apikey)\s*=\s*["\'][^"\']+["\']'

    matches = re.finditer(pattern, code, re.IGNORECASE)

    for match in matches:
        line_number = code[:match.start()].count("\n") + 1

        issues.append({
            "severity": "HIGH",
            "type": "HARDCODED_SECRET",
            "line": line_number,
            "message": "Possible hardcoded secret detected."
        })

    sql_pattern = r'["\']SELECT .*["\']\s*\+'

    sql_matches = re.finditer(sql_pattern, code, re.IGNORECASE)

    for match in sql_matches:
        line_number = code[:match.start()].count("\n") + 1

        issues.append({
            "severity": "HIGH",
            "type": "SQL_INJECTION",
            "line": line_number,
            "message": "Possible SQL injection detected. SQL appears to be built using string concatenation.",
            "suggestion": "Use parameterized queries instead of concatenating user input into SQL."
        })

    return issues

def normalize_issue_type(issue_type: str):
    return issue_type.lower().replace("_", "").replace(" ", "")

def deduplicate_issues(issues):
    unique_issues = []
    seen = set()

    for issue in issues:
        key = (
            normalize_issue_type(issue["type"]),
            issue["line"]
        )

        if key not in seen:
            seen.add(key)
            unique_issues.append(issue)

    return unique_issues