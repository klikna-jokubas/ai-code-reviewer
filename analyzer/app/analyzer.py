import re


def analyze_code(code: str, language: str):
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
        pass

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