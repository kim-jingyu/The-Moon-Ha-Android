package com.innerpeace.themoonha.data.network

import com.innerpeace.themoonha.data.model.auth.LoginRequest
import com.innerpeace.themoonha.data.model.craft.CommonResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): CommonResponse
}