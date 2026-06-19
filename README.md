# AI-Powered Job Board

A job board backend where companies post jobs and candidates apply — 
but with one extra thing: AI tells you how well your resume matches a job before you apply.

Built this to learn how real backend systems work beyond basic CRUD. 
Wanted to combine things I'd learned separately — Kafka, Redis, AWS, JWT — 
into one project that actually makes sense together.

---

## What it does

Companies register and post jobs. Candidates register, upload their resume, 
and can check an AI-generated match score for any job. The score tells you 
what percentage your resume matches the job requirements, and why.

The matching uses Gemini AI — it reads your resume text and the job description 
and gives back a score with feedback. That score gets cached in Redis so 
repeated requests don't keep calling the API.

When a job is posted, a Kafka event fires. Same when a resume is uploaded. 
The notification service picks these up — right now it logs them, 
but the structure is ready for real email/push notifications.

---

## Tech Stack

- Java 17 + Spring Boot
- MySQL — users, jobs, resumes, match results
- AWS S3 — resume PDF storage
- Apache PDFBox — extract text from uploaded PDFs
- Gemini AI API — resume to job matching and scoring
- Apache Kafka — async events for job posting and resume upload
- Redis — cache match scores
- JWT — authentication with COMPANY and CANDIDATE roles
- Docker Compose — Kafka, Zookeeper, Redis

---

## API Overview

**Auth**
- POST /api/auth/register
- POST /api/auth/login

**Jobs**
- POST /api/jobs/post — COMPANY only
- GET /api/jobs/all — paginated
- GET /api/jobs/search?keyword=java
- PUT /api/jobs/{id}/close — COMPANY only

**Resume**
- POST /api/resume/upload — CANDIDATE only
- GET /api/resume/my-resume

**Matching**
- GET /api/match/{jobId} — returns AI score + feedback

---

## Setup

### Prerequisites
- Java 17
- Docker Desktop
- AWS account with S3 bucket
- Gemini API key from aistudio.google.com

### Steps

```bash
git clone https://github.com/krish-02-code/job-board-application.git
cd job-board-application
```

Copy and fill in config:
```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

Start Kafka and Redis:
```bash
docker-compose up -d
```

Run the app:
```bash
mvn spring-boot:run
```

---

## How matching works

1. Candidate uploads PDF resume
2. PDFBox extracts the text
3. Text stored in MySQL, PDF stored in S3
4. Candidate requests match score for a job
5. Redis checked first — if cached, returns instantly
6. Cache miss → resume text + job description sent to Gemini API
7. Gemini returns a score (0-100) and feedback explaining the match
8. Score cached in Redis for 24 hours

---

## Known limitations

- Notifications are logged only, no actual email/push yet
- No frontend — API only, test with Postman
- Single instance — no horizontal scaling setup yet
