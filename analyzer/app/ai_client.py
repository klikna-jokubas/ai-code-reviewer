import os
import json

from groq import Groq


client = Groq(api_key=os.environ.get("GROQ_API_KEY"))


def analyze_with_ai(code: str, language: str):
    response = client.chat.completions.create(
        model="openai/gpt-oss-20b",
        messages=[
            {
                "role": "system",
                "content": """
You are an expert code reviewer.

First determine whether the provided input is actually code written in the requested language.

Set is_code to false if the input is clearly not code or is unrelated natural language.

If is_code is false, return no code-review issues.

If is_code is true, analyze the code normally.

Analyze the provided code for real bugs, security vulnerabilities,
and important code-quality problems.

Only report issues that you have good reason to believe are real.
Do not invent problems.

Return every finding with:
- severity: HIGH, MEDIUM, or LOW
- type: short issue category
- line: line number where the issue occurs
- message: explanation of the problem
- suggestion: how to improve it
"""
            },
            {
                "role": "user",
                "content": f"Language: {language}\n\nCode:\n{code}"
            }
        ],
        response_format={
            "type": "json_schema",
            "json_schema": {
                "name": "code_review",
                "strict": True,
                "schema": {
                    "type": "object",
                    "properties": {
                        "is_code": {
                            "type": "boolean"
                        },
                        "issues": {
                            "type": "array",
                            "items": {
                                "type": "object",
                                "properties": {
                                    "severity": {
                                        "type": "string",
                                        "enum": ["HIGH", "MEDIUM", "LOW"]
                                    },
                                    "type": {
                                        "type": "string"
                                    },
                                    "line": {
                                        "type": ["integer", "null"]
                                    },
                                    "message": {
                                        "type": "string"
                                    },
                                    "suggestion": {
                                        "type": "string"
                                    }
                                },
                                "required": [
                                    "severity",
                                    "type",
                                    "line",
                                    "message",
                                    "suggestion"
                                ],
                                "additionalProperties": False
                            }
                        }
                    },
                    "required": ["is_code", "issues"],
                    "additionalProperties": False
                }
            }
        }
    )

    return json.loads(response.choices[0].message.content)


if __name__ == "__main__":
    result = analyze_with_ai(
        "I like pizza and coffee",
        "python"
    )

    print(result)
