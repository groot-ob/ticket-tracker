# Ticket Tracking System

A full-stack support ticket tracking application using **Java 21 / Spring Boot** backend and a **Vue 3 / TypeScript / Tailwind CSS** frontend.

---

## 🚀 Tech Stack

* **Backend:** Java 21, Spring Boot, Spring Data JPA, PostgreSQL, Flyway, Maven, JUnit 5 / Testcontainers, Problem Details.
* **Frontend:** Vue 3 (Composition API), TypeScript, Vite, Tailwind CSS, Axios.

---

## 📋 Requirements

Ensure you have the following installed on your local machine:
* **Java Development Kit (JDK) 21** or higher
* **Maven** (or use the included `./mvnw` wrapper)
* **Node.js** (v18+ recommended) & npm
* Docker
* **PostgreSQL** (running locally or via Docker)

---

## 🛠️ Step-by-Step Setup Instructions

### Step 1: Clone the Repository
Clone the project repository to your local machine and navigate into the project root directory:
```bash
git clone [https://github.com/groot-ob/ticket-tracker.git](https://github.com/groot-ob/ticket-tracker.git)
cd mini-ticket-tracker
```

### Step 2: Configure the Backend Database
Ensure PostgreSQL is running locally or via Docker. To use docker navigate to **/backend/local-env** and create a `.env` file with the following environment variables

```bash
export POSTGRES_DB=your-postgres-db
export POSTGRES_PASSWORD=your-postgres-db-password
export POSTGRES_USER=postgres-db-user
export POSTGRES_PORT=5432
export POSTGRES_DATA_DIR=.data/postgres
export SPRING_PROFILES_ACTIVE=local
export SPRING_DATASOURCE_URL=jdbc:postgresql://ticket-tracker-postgres:5432/db
```

Inside the `/backend/local-env` directory spin up docker with the following command

``
dccker compose -p ticket-tracker up -d
``
## Running the Application

### Backend
To run the backend navigate to `/mini-ticket-tracke/backend/` and run the following command in the terminal

``` 
cd backend
./mvnw spring-boot:run
```

### Frontend

To run the Vuejs frontend. Make sure you are inside the directory `/mini-ticket-tracker/frontend` and run
``` 
cd frontend
npm run dev
```

The backend server will run on ``http://localhost:8080`` and the frontend can be accessed from the browser on this url ``http://localhost:5173``
Navigate to ``http://localhost:8080/swagger-ui/index.html#/`` to view the API endpoints.




### Decisions and Tradeoffs
