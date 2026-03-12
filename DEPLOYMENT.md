# Deploying Healio-spring

This guide will walk you through the deployment process for both the Next.js Frontend and the Spring Boot Backend.

## 1. Backend Deployment (Spring Boot)

We recommend deploying the backend on a Platform-as-a-Service (PaaS) like **Render**, **Railway**, or **Heroku**.

### Steps for Render/Railway
1. **GitHub Connection**: Push your code to a GitHub repository, then connect Render or Railway to your GitHub account.
2. **Setup the Web Service**:
   - Create a new "Web Service".
   - Select the repository containing your project.
   - Set the Root Directory to `backend/`.
3. **Build & Start Commands**:
   - Build Command: `./mvnw clean package -DskipTests`
   - Start Command: `java -jar target/healio-0.0.1-SNAPSHOT.jar`
4. **Environment Variables**: Add your backend environment variables from `application.properties`:
   - `GEMINI_API_KEY`
   - `CLERK_ISSUER_URL`
5. **Database (Production Setup)**: Currently, the backend uses an H2 in-memory database. For production, you must provision a PostgreSQL or MySQL database (available natively on Render/Railway) and set the following env variables (and ensure the proper driver is added in `pom.xml`):
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `SPRING_JPA_DATABASE_PLATFORM`

Once deployed, copy the newly generated backend URL (e.g., `https://healio-backend.onrender.com`).

---

## 2. Frontend Deployment (Next.js)

We recommend deploying the Next.js frontend on **Vercel**.

### Steps for Vercel
1. **Import the Project**: Go to Vercel, click "Add New... Project", and import your GitHub repository.
2. **Project Setup**:
   - Set the "Framework Preset" to **Next.js**.
   - Set the "Root Directory" to `frontend/`.
3. **Environment Variables**: Add all the variables from your local `frontend/.env.local` to the Vercel dashboard:
   - `NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY`
   - `CLERK_SECRET_KEY`
   - `NEXT_PUBLIC_API_URL` -> **Important**: set this to your newly deployed Backend URL (e.g., `https://healio-backend.onrender.com`).
   - `NEXT_PUBLIC_PAYPAL_CLIENT_ID`
   - `NEXT_PUBLIC_GEMINI_API_KEY`
   - *(... and other specified variables in your `.env.local`)*
4. **Deploy**: Click the **Deploy** button. Vercel will build the frontend and provide you with a live URL.

---

## 3. Post-Deployment Checks

1. **CORS Configuration**: Update your Spring Boot CORS settings (`app.cors.allowed-origins`) to allow requests from your newly deployed Vercel domain instead of just `localhost:3000`.
2. **Clerk Configuration**: In the Clerk Dashboard, update your instances to include your new Vercel domain.
3. **PayPal Settings**: When moving to production, use PayPal Live Mode keys rather than your sandbox credentials.
