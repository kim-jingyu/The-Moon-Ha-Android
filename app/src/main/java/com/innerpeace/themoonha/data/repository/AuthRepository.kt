package com.innerpeace.themoonha.data.repository

import android.util.Log
import com.innerpeace.themoonha.data.model.auth.LoginRequest
import com.innerpeace.themoonha.data.model.craft.CommonResponse
import com.innerpeace.themoonha.data.network.AuthService

class AuthRepository(private val authService: AuthService) {
    suspend fun fetchLogin(loginRequest: LoginRequest): CommonResponse? {
        return try {
            authService.login(loginRequest)
        } catch (e: Exception) {
            Log.e("로그인 응답 실패", "${e.message}", e)
            null
        }
    }
}