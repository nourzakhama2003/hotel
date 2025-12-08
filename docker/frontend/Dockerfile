FROM node:20 AS build
WORKDIR /build
COPY package.json package-lock.json* ./
RUN npm i --legacy-peer-deps
COPY . .
# Build configuration passed from Docker Compose or workflow
# Default to production for security
ARG ENVIRONMENT=production
ENV ANGULAR_ENV=${ENVIRONMENT}
RUN echo "Building Angular app with configuration: ${ANGULAR_ENV}" && \
    npm run build -- --configuration=${ANGULAR_ENV} && \
    ls -lh dist/hotel-frontend/browser/ | head -20

FROM nginx:alpine AS runtime 
COPY nginx.conf /etc/nginx/nginx.conf
WORKDIR /usr/share/nginx/html
COPY --from=build /build/dist/hotel-frontend/browser .
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
