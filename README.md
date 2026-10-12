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
git clone https://github.com/groot-ob/ticket-tracker.git
cd mini-ticket-tracker
```

### Step 2: Configure the Backend Database
Ensure PostgreSQL is running locally or via Docker. To use docker navigate to **/backend/local-env** and create a `.env` file with the following environment variables.

```bash
export POSTGRES_DB=your-postgres-db
export POSTGRES_PASSWORD=your-postgres-db-password
export POSTGRES_USER=postgres-db-user
export POSTGRES_PORT=5432
export POSTGRES_DATA_DIR=.data/postgres
export SPRING_PROFILES_ACTIVE=local
export SPRING_DATASOURCE_URL=jdbc:postgresql://ticket-tracker-postgres:5432/db
```
Inside the `frontend` directory ensure that you have a `.env` file with `VITE_API_BASE_URL` . The base url of the backend.
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
 - Used docker docker to containerize our application and run flyway migration scripts instead of running them separately.
 - Wrote web slice tests for the `TicketController.java`. I've also added test containers which I will use to write integration tests (Specificallly database integration test.) This is work in progress.
 - For the `priority` and `status` columns on our database I used `varchar` instead of `enum`. The assumption here is that if the application grows new values will require to run an alter table query on the db table.


### Considerations with more time
Given more time I woulld:
 - Add user authentication and ensure role-based access.
 - Add logs and observability/monitoring to keep track on user actions such as  creating , deleting, and updating a ticket