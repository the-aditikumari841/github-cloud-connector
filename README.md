# GitHub Cloud Connector

## 🚀 Overview

This project is a **GitHub Cloud Connector** built using Spring Boot.
It integrates with GitHub APIs to perform actions like fetching repositories and creating issues.

The project demonstrates:

* External API integration (GitHub REST API)
* Secure authentication using Personal Access Token (PAT)
* Clean architecture and modular design
* Proper error handling

---

## 🛠️ Tech Stack

* Java 17
* Spring Boot
* WebClient (Spring WebFlux)
* Maven

---

## 🔐 Authentication

This project uses **GitHub Personal Access Token (PAT)** for authentication.

The token is **not hardcoded** and is loaded securely via environment variables.

### Set Token (Windows PowerShell)

```powershell
setx GITHUB_TOKEN "your_token_here"
```

### application.yml

```yaml
github:
  token: ${GITHUB_TOKEN}
```

---

## 📌 API Endpoints

### 1. Get Public Repositories

Fetch repositories of any GitHub user.

```
GET /github/repos/{username}
```

Example:

```
GET /github/repos/octocat
```

---

### 2. Get Authenticated User Repositories

Fetch repositories of the authenticated user.

```
GET /github/my-repos
```

---

### 3. Create Issue

Create an issue in a repository.

```
POST /github/issues
```

Request Body:

```json
{
  "owner": "your-username",
  "repo": "your-repo",
  "title": "Issue title",
  "body": "Issue description"
}
```

---

## ⚠️ Error Handling

The application handles GitHub API errors using:

* WebClient (.onStatus())
* GlobalExceptionHandler

It properly propagates:

* 401 Unauthorized (invalid token)
* 403 Forbidden (permission issues)
* 404 Not Found (invalid repo)
* 422 Unprocessable Entity (invalid request body)

---

## 📂 Project Structure

```
controller/   → REST endpoints  
service/      → Business logic  
client/       → GitHub API integration  
config/       → WebClient configuration  
dto/          → Request/Response models  
exception/    → Global error handling  
util/         → Token management  
```

---

## ▶️ How to Run

1. Clone the repository:

```bash
git clone https://github.com/the-aditikumari841/github-cloud-connector.git
```
2. Navigate into project directory:

```bash
cd github-cloud-connector
```
3. Set environment variable:

```powershell
setx GITHUB_TOKEN "your_token_here"
```

Note: Restart your terminal after setting the environment variable.

4. Run the application:

```bash
mvn spring-boot:run
```

5. Access APIs at:

```
http://localhost:8080
```

---

## ✅ Features Implemented

* Fetch public repositories
* Fetch repositories of authenticated Github users (Requires a valid GitHub Personal Access Token)
* Create GitHub issues
* Centralized token management
* Global error handling

---

## 🎯 Future Enhancements (Optional)

* OAuth 2.0 authentication
* List issues from repository
* Create pull requests
* Input validation using @Valid

---

## 👩‍💻 Author

Aditi Kumari
