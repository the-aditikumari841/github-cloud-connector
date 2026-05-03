# Automated Multi-Language Code Review System

## 🚀 Overview

A CI-driven automated code review system that integrates with GitHub webhooks to analyze pull requests across multiple programming languages using static analysis tools.

The system clones the repository, checks out the exact commit, runs language-specific analyzers, parses results, filters issues to only changed files, and posts structured review feedback directly on the pull request.

---
## ✨ Features

- 🔍 Multi-language support (Java, Python, JavaScript/TypeScript)
- ⚙️ CI-style execution pipeline (clone → analyze → comment)
- 🧩 Pluggable analyzer-parser architecture
- 📂 Analysis limited to changed files in PR
- 📊 Severity mapping via configuration
- 💬 Automated GitHub PR comments
- 🔄 Webhook-driven execution

---

## 🏗️ Architecture

```
The system follows a pipeline-based architecture:

GitHub Webhook  
→ Webhook Service  
→ Analysis Service  
→ Analyzer Factory  
→ Language-specific Analyzers  
→ CI Executor (runs tools)  
→ Parsers (JSON / XML / Text)  
→ Issue Aggregation  
→ Filter (changed files only)  
→ GitHub Client → PR Comment
```

## 🛠️ Tech Stack

- **Backend:** Spring Boot (Java), Maven
- **CI Execution:** ProcessBuilder (CLI tools)
- **Static Analysis Tools:**
    - ESLint (JavaScript/TypeScript)
    - Ruff (Python)
    - Checkstyle (Java)
    - SpotBugs (Java)
- **API Integration:** GitHub REST API (WebClient)
- **Authentication:** OAuth + Personal Access Token (PAT)

---
## 🔄 Workflow

1. GitHub sends a `pull_request` webhook
2. Repository is cloned locally
3. Specific commit (SHA) is checked out
4. Changed files are fetched via GitHub API
5. Relevant analyzers are selected dynamically
6. Static analysis tools are executed
7. Outputs are parsed into a unified `Issue` model
8. Issues are filtered to only changed files
9. A formatted review comment is posted on the PR
10. Temporary repository is cleaned up

---
## 🔌 Setup Instructions

### **1. Clone the repository** 

```powershell
git clone https://github.com/the-aditikumari841/multi-lang-code-review-system
cd multi-lang-code-review-system
```

### **2. Configure environment variables**

```
GITHUB_TOKEN=your_personal_access_token
GITHUB_CLIENT_ID=your_client_id
GITHUB_CLIENT_SECRET=your_client_secret
```
---
### **3. Run the application** 

```
./mvnw spring-boot:run
```

---

### **4. Expose webhook endpoint**

Use ngrok or similar:
```
ngrok http 8080
```
Set webhook URL in GitHub:
```
http://<ngrok-url>/webhook/github
```
---
## ⚠️ Limitations
- Runs analysis on the entire repository (filtered later)
- No parallel execution of analyzers (can be optimized)
- No persistence layer for storing results
- Executes tools on untrusted repositories (security considerations)

---

## 🚀 Future Improvements

- Parallel analyzer execution
- Inline PR comments (per file/line)
- Analyze only changed files directly
- Add database for history and analytics
- Code quality scoring system
- Dockerized sandbox execution for security

## 🧠 Key Learnings

- Designed a scalable, pluggable architecture for multi-language code analysis
- Built end-to-end webhook-driven CI workflows with GitHub API and OAuth
- Unified heterogeneous tool outputs (JSON/XML/Text) into a standardized model
---
## 🤝 Contributing

Feel free to open issues or submit pull requests for improvements.


## 📂 Project Structure

```
src/main/java/com/aditi/githubreviewbot
├── analysis
│   ├── analyzer            Language-specific analyzers (ESLint, Ruff, Checkstyle, SpotBugs)
│   ├── parser              Parses tool outputs (JSON, XML, Text)
│   ├── model               Unified Issue model
│   ├── AnalysisService     Core orchestration logic
│   └── AnalyzerFactory     Selects analyzers based on file types
├── ci
│   └── CIExecutor          Executes CLI tools (mvn, ruff, eslint, etc.)
├── client
│   ├── github              GitHub API integration
│   └── auth                OAuth token exchange
├── controller
│   ├── GithubController    Exposes APIs for repo and issue operations
│   └── OAuthController     REST endpoints
├── service
│   ├── GithubService       Business logic for GitHub operations (repos, issues)
│   └── OAuthService        Business logic layer
├── webhook
│   ├── WebhookController
│   └── WebhookService      Handles GitHub webhook events
├── config                  Configuration (WebClient, properties)
├── dto                     Request/Response models
├── exception               Global exception handling
└── util                    Utility classes (TokenProvider)
```

---

## 👩‍💻 Author

### [Aditi Kumari](https://github.com/the-aditikumari841)