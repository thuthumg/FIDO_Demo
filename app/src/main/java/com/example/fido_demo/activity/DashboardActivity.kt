package com.example.fido_demo.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fido_demo.AuthStateManager
import com.example.fido_demo.KeycloakApiService
import com.example.fido_demo.KeycloakConfig
import com.example.fido_demo.R
import com.example.fido_demo.databinding.ActivityDashboardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DashboardActivity : BaseActivity() {
    lateinit var activityDashboardBinding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        activityDashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(activityDashboardBinding.root)
        setupSystemBar(
            window = window,
            statusBarColor = "#b2000e".toColorInt()
        )
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        activityDashboardBinding.btnSignOut.setOnClickListener {
            logoutFromKeycloak()
        }
    }
    fun logoutFromKeycloak() {
        val authState = AuthStateManager.readAuthState(this)
        val clientId = KeycloakConfig.CLIENT_ID
        val refreshToken = authState?.refreshToken.toString()
        //retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("https://your-keycloak-domain/auth/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val keycloakApi = retrofit.create(KeycloakApiService::class.java)

        val call = keycloakApi.logout(clientId, refreshToken)
        call.enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    println("Logout successful")
                    AuthStateManager.clear(this@DashboardActivity)

                    val intent = Intent(this@DashboardActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                } else {
                    println("Logout failed: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@DashboardActivity,"Logout failed: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Logout failed: ${t.message}")
                Toast.makeText(this@DashboardActivity,"Logout failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}