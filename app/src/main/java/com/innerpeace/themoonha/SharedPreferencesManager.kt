package com.innerpeace.themoonha

import android.content.Context
import android.content.SharedPreferences

/**
 * 로그인 상태 유지를 위한 클래스
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
class SharedPreferencesManager(context: Context) {
    private val authSharedPreferences: SharedPreferences =
        context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)

    fun setIsLogin(isLogin: Boolean) {
        with(authSharedPreferences.edit()) {
            putBoolean("isLoggedIn", isLogin)
            apply()
        }
    }

    fun getIsLogin(): Boolean {
        return authSharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun clear() {
        with(authSharedPreferences.edit()) {
            clear()
            apply()
        }
    }

    fun setLoginToken(accessToken: String, refreshToken: String) {
        with(authSharedPreferences.edit()) {
            putString("accessToken", accessToken)
            putString("refreshToken", refreshToken)
            apply()
        }
    }

    fun getLoginToken(): Pair<String?, String?> {
        val accessToken = authSharedPreferences.getString("accessToken", null)
        val refreshToken = authSharedPreferences.getString("refreshToken", null)
        return Pair(accessToken, refreshToken)
    }
}