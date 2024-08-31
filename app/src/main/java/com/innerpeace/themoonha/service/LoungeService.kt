package com.innerpeace.themoonha.service

import com.innerpeace.themoonha.data.model.LoungeListResponse
import retrofit2.Call
import retrofit2.http.GET

/**
 * 라운지 서비스
 * @author 조희정
 * @since 2024.08.30
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.30  	조희정       최초 생성
 * 2024.08.30   조희정       라운지 목록 불러오기 구현
 * </pre>
 */
interface LoungeService {

    @GET("lounge/list")
    fun loungeList(): Call<List<LoungeListResponse>>

}