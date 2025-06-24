package com.example.fido_demo

import retrofit2.Call
import retrofit2.http.*

interface KeycloakApiService {
    @FormUrlEncoded
    @POST("realms/dev/protocol/openid-connect/logout")
    fun logout(
        @Field("client_id") clientId: String,
        @Field("refresh_token") refreshToken: String
    ): Call<Void>
}
