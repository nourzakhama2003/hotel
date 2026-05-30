// ==========================================
// DEVELOPMENT ENVIRONMENT CONFIGURATION
// ==========================================

export const environment = {
    production: false,

    // API Configuration (Development - Local)
    API_URL: 'http://localhost:8082/api',

    // Keycloak Configuration (Development - Local)
    KEYCLOAK_URL: 'http://localhost:8080/',
    KEYCLOAK_REALM: 'hotelrealm',
    KEYCLOAK_CLIENT_ID: 'hotel-frontend',

    // Stripe Configuration (Development - Test Keys)
    STRIPE_PUBLIC_KEY: 'pk_test_51S1GWi4BxFUD5s92q3WSd0x9yKDzJtTTR4Vc2orIISwUOV97wVBhn6elbNRM7Vs1MNqrAlO7J12ZQ2xtHk32bqWn00mt8IdCtC',

    // Chatbot Service URL (Development)
    CHATBOT_URL: 'http://localhost:5001',
    FACE_RECOGNITION_API_URL: 'http://localhost:8082/api/face'
};
