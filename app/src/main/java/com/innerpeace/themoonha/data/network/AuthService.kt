package com.innerpeace.themoonha.data.network

import com.innerpeace.themoonha.data.model.auth.LoginRequest
import com.innerpeace.themoonha.data.model.craft.CommonResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 로그인 서비스
 *
 * @author 손승완
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.10  	손승완       최초 생성
 * </pre>
 * @since 2024.09.10
 */
interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): CommonResponse
}