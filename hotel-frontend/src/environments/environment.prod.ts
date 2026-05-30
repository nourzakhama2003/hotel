// ==========================================
// PRODUCTION ENVIRONMENT CONFIGURATION
// ==========================================

export const environment = {
    production: true,

    // API Configuration (Production - VPS with Domain)
    // Using separate domain for API
    API_URL: 'https://app.46.lebondeveloppeur.net/api',

    // Keycloak Configuration (Production - VPS)
    // Keycloak 25.0.5+ doesn't use /auth/ prefix anymore
    KEYCLOAK_URL: 'https://app.46.lebondeveloppeur.net',
    KEYCLOAK_REALM: 'hotelrealm',
    KEYCLOAK_CLIENT_ID: 'hotel-frontend',

    // Stripe Configuration (Production)
    // ✅ Public key is safe in frontend (designed to be public)
    STRIPE_PUBLIC_KEY: 'pk_test_51S1GWi4BxFUD5s92q3WSd0x9yKDzJtTTR4Vc2orIISwUOV97wVBhn6elbNRM7Vs1MNqrAlO7J12ZQ2xtHk32bqWn00mt8IdCtC',

    // Chatbot Service URL (Production)
    CHATBOT_URL: 'http://app.46.lebondeveloppeur.net/chatbot',
    FACE_RECOGNITION_API_URL: 'http://app.46.lebondeveloppeur.net/face-recognition'
};
