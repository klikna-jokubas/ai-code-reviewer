from fastapi import FastAPI
from pydantic import BaseModel

from analyzer.app.analyzer import analyze_code

app = FastAPI()


class CodeRequest(BaseModel):
    code: str


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/analyze")
def analyze(request: CodeRequest):
    issues = analyze_code(request.code)

    return {
        "issues": issues
    }