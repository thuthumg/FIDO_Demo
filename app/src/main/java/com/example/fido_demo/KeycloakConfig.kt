package com.example.fido_demo

object KeycloakConfig {
    const val AUTH_URL = "https://your-keycloak-domain/auth/realms/your-realm/protocol/openid-connect/auth"
    const val TOKEN_URL = "https://your-keycloak-domain/auth/realms/your-realm/protocol/openid-connect/token"
    const val CLIENT_ID = "android-webauthn"
    const val REDIRECT_URI = "com.example.fidodemoapp://oauth2redirect"
    const val LOGOUT_URL = "https://your-keycloak-domain/auth/realms/your-realm/protocol/openid-connect/logout"
}
