# --- 1. Dependency Stage ---
FROM node:20-slim AS deps
WORKDIR /app

# Copy package.json and package-lock.json to leverage Docker cache
COPY package.json package-lock.json* ./
RUN npm ci

# --- 2. Builder Stage ---
FROM node:20-slim AS builder
WORKDIR /app

# Copy dependencies from the previous stage
COPY --from=deps /app/node_modules ./node_modules
# Copy the rest of the application source code
COPY . .

# Build the Next.js application for production
RUN npm run build

# --- 3. Runner Stage ---
FROM node:20-slim AS runner
WORKDIR /app

# Set the environment to production
ENV NODE_ENV production

# Copy the built application from the builder stage
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static

# Expose the port Next.js will run on
EXPOSE 3000

# The command to start the optimized Next.js server
CMD ["node", "server.js"]