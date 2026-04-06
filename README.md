# GitHub Cloud Connector

## 🚀 Overview

This project is a **GitHub Cloud Connector** built using Spring Boot.
It integrates with GitHub APIs to perform actions like fetching repositories and creating issues, supporting both **Personal Access Token (PAT)** and **OAuth-based authentication**.

---

The project demonstrates:

* External API integration (GitHub REST API)
* Secure authentication using **Personal Access Token (PAT) and OAuth 2.0**
* Clean architecture and modular design
* Proper error handling
* Dynamic token management (OAuth overrides PAT)

---

## 🛠️ Tech Stack

* Java 17
* Spring Boot
* Spring WebFlux (`WebClient`)
* Maven
* GitHub REST API

---

## 🔐 Authentication

This project uses **two authentication methods** : GitHub Personal Access Token and GitHub OAuth. 

---

### **GitHub Personal Access Token (PAT)** 
It is used as default authentication.


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
### **GitHub OAuth (Authorization Code Flow)** 
OAuth allows users to securely log in via GitHub.

#### Steps:
1. After running the application, Open in browser:```http://localhost:8080/auth/github/login```
2. Authorize the application on GitHub redirects to:```/auth/github/callback```
3. Access token is fetched and stored in memory
🔁 Token Priority

OAuth Token (if available) → used  
Else → PAT Token used

---

## 📌 API Endpoints

### 🔐 OAuth Endpoints

#### 1. Initiate GitHub Login

Redirects user to GitHub for authorization.
```
GET /auth/github/login
```

#### 2. OAuth Callback

Handles GitHub callback and exchanges code for access token.
```
GET /auth/github/callback?code=...
```
Response:
```
OAuth Success! Token stored.
```
### 📦 GitHub API Endpoints
#### 1. Get Public Repositories

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

Fetch repositories of the authenticated user (requires valid PAT or OAuth login).

```
GET /github/my-repos
```

---

### 3. Create Issue

Create a new issue in Github repository.

⚠️ This action requires proper permissions. 

You can create issues only if:

* The repository allows issues,
* And You have access (owner/collaborator) or the repository is public
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

The application handles GitHub API errors in a structured and consistent way.

It uses:

* WebClient (.onStatus()) to capture errors returned by the GitHub API 
* A centralized GlobalExceptionHandler to format and return clean responses to the client.

The following HTTP errors are properly propagated:

* **401 Unauthorized** : Invalid or missing token (PAT or OAuth)
* **403 Forbidden** : Permission issues or insufficient access rights
* **404 Not Found** : Repository or resource does not exist
* **422 Unprocessable Entity** : Invalid request data (e.g., missing title while creating an issue)

🔐 With OAuth enabled, the same error handling applies.
Errors related to invalid or expired OAuth tokens are also handled in the same flow, ensuring consistent responses across both authentication methods.

---

## 📂 Project Structure

```
controller/    → REST endpoints (GithubController, OAuthController)
service/       → Business logic  (GitHub operations, OAuth flow handling)
client/
├── github/    → GitHub API client (repo, issues)
└── auth/      → OAuth client (token exchange)
config/        → WebClient configuration  
dto/
├── request/   → Request payloads  
└── response/  → Response models
exception/     → Global exception handling  
util/          → Utility classes (TokenProvider for PAT + OAuth token management)
```
---


## ▶️ How to Run

1. Clone the repository:

```bash
git clone https://github.com/the-aditikumari841/github-cloud-connector.git
```
2. Navigate into the project directory:

```bash
cd github-cloud-connector
```
3. Set environment variable:

* For Personal Access Token (PAT):

```powershell
setx GITHUB_TOKEN "your_token_here"
```
* For OAuth (GitHub App credentials):
```
setx GITHUB_CLIENT_ID "your_client_id"
setx GITHUB_CLIENT_SECRET "your_client_secret"
```
⚠️ Note: Restart your terminal and IDE after setting environment variables.
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

* Fetch public repositories of any Github user
* Fetch repositories of authenticated Github user (via PAT or OAuth)
* Create GitHub issues (with proper repository permissions)
* OAuth 2.0 integration (Github login flow)
* Centralized token management (PAT + OAuth support via TokenProvider)
* Global exception handling for GitHub API responses
---

## 🎯 Future Enhancements 

* Integrate Spring Security to standardize authentication and authorization
* Extend OAuth flow with refresh tokens / session management
* Enhance role-based access control (RBAC) for fine-grained permissions
* Add pagination for scalable API responses

---

## 👩‍💻 Author

### [Aditi Kumari](https://github.com/the-aditikumari841)