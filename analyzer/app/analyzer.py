import re


def analyze_code(code: str):
    issues = []

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