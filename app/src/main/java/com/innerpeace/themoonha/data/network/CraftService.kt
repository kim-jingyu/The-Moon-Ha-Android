package com.innerpeace.themoonha.data.network

import com.innerpeace.themoonha.data.model.craft.CraftMainResponse
import retrofit2.http.GET

interface CraftService {
    @GET("craft/list")
    suspend fun getCraftMain(): CraftMainResponse
}