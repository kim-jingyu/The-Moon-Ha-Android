package com.innerpeace.themoonha.data.repository

import android.util.Log
import com.innerpeace.themoonha.data.model.craft.CraftMainResponse
import com.innerpeace.themoonha.data.network.CraftService

class CraftRepository(private val craftService: CraftService) {
    suspend fun fetchCraftMain(): CraftMainResponse? {
        return try {
            craftService.getCraftMain()
        } catch (e: Exception) {
            Log.e("문화공방 메인 페이지 응답 실패", "${e.message}", e)
            null
        }
    }
}