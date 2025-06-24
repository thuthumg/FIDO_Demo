package com.example.fido_demo.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import com.example.fido_demo.AuthStateManager
import com.example.fido_demo.KeycloakConfig
import com.example.fido_demo.R
import com.google.gson.GsonBuilder

import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.CodeVerifierUtil
import net.openid.appauth.ResponseTypeValues

class KeycloakActivity : BaseActivity() {

    private lateinit var authService: AuthorizationService
    private lateinit var authState: AuthState

    private val authResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val resp = AuthorizationResponse.fromIntent(data!!)
        val ex = AuthorizationException.fromIntent(data)

        if (resp != null) {
            authState = AuthState(resp, ex)
            val tokenRequest = resp.createTokenExchangeRequest()

            authService.performTokenRequest(tokenRequest) { tokenResponse, tokenEx ->
                if (tokenResponse != null) {
                    authState.update(tokenResponse, tokenEx)
                    Log.d("Dashboard", "Token state ${authState.idToken}")
                    Log.d("Dashboard", "Token accesstoken ${authState.accessToken}")

                    AuthStateManager.writeAuthState(this, authState)

                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val json = gson.toJson(authState)
                    Log.d("MainActivity", "json data ${json}")


                    val idToken = authState.idToken
                    Log.d("Token", "ID Token: $idToken")

                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Token Error", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } else {
            Toast.makeText(this, "Authorization Failed", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupSystemBar(
            window = window,
            statusBarColor = "#b2000e".toColorInt()
        )
        authService = AuthorizationService(this)
        initiateLogin()
    }


    private fun initiateLogin() {
        val serviceConfig = AuthorizationServiceConfiguration(
            KeycloakConfig.AUTH_URL.toUri(),
            KeycloakConfig.TOKEN_URL.toUri()
        )

        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            KeycloakConfig.CLIENT_ID,
            ResponseTypeValues.CODE,
            KeycloakConfig.REDIRECT_URI.toUri()
        ).setCodeVerifier(CodeVerifierUtil.generateRandomCodeVerifier())
            .setScope("openid profile email")
            .build()

        val builder = CustomTabsIntent.Builder()

        val defaultColors = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .build()

        builder.setDefaultColorSchemeParams(defaultColors)

        builder.setShowTitle(true)
       // builder.setCloseButtonIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_back))

        val customTabsIntent = builder.build()

        val authIntent = authService.getAuthorizationRequestIntent(authRequest, customTabsIntent)
        authResultLauncher.launch(authIntent)



    }


}