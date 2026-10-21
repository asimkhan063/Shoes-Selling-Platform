# Shoe Store — Full Stack Project

A local-development shoe shopping platform built with Spring Boot, MySQL, React, and Razorpay test checkout.

## Prerequisites

- Docker Desktop with Compose
- A Razorpay test account for real test checkout (the app can still run without credentials)

## Run locally

1. In this folder, copy `.env.example` to `.env` and add your Razorpay **test** keys.
2. Start the stack: `docker compose up --build`
3. Open the React app at http://localhost:5173 and the API at http://localhost:8181/api/health.

Local default passwords and JWT secrets are for development only. Replace them before deployment.

## Build sequence

1. Local stack and project skeleton
2. Spring Boot API, database entities, authentication, products, cart, orders, and payment verification
3. React storefront, login, cart, checkout, and order history
4. Connect Razorpay test checkout and run through the complete purchase flow
