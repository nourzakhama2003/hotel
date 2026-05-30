// Example environment file for local development.
// Copy to environment.ts locally and set real values as needed.

export const environment = {
    production: false,
    API_URL: 'http://localhost:8082/api',
    KEYCLOAK_URL: 'http://localhost:8080/',
    KEYCLOAK_REALM: 'hotelrealm',
    KEYCLOAK_CLIENT_ID: 'hotel-frontend',
    STRIPE_PUBLIC_KEY: '',
    CHATBOT_URL: 'http://localhost:5001',
    FACE_RECOGNITION_API_URL: 'http://localhost:8082/api/face'
};