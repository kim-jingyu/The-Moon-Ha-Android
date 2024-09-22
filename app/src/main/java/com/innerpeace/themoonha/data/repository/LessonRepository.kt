package com.innerpeace.themoonha.data.repository

import android.util.Log
import com.innerpeace.themoonha.data.model.CommonResponse
import com.innerpeace.themoonha.data.model.lesson.CartRequest
import com.innerpeace.themoonha.data.model.lesson.CartResponse
import com.innerpeace.themoonha.data.model.lesson.LessonDetailResponse
import com.innerpeace.themoonha.data.model.lesson.LessonEnrollResponse
import com.innerpeace.themoonha.data.model.lesson.LessonListResponse
import com.innerpeace.themoonha.data.model.lesson.SugangRequest
import com.innerpeace.themoonha.data.network.LessonService

/**
 * 강좌 관련 api 조회 레포지토리
 *
 * @author 손승완
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  	손승완       최초 생성
 * </pre>
 * @since 2024.08.31
 */
class LessonRepository(private val lessonService: LessonService) {

    suspend fun fetchLessonList(lessonListRequestMap: Map<String, String>): LessonListResponse? {
        return try {
            lessonService.getLessonList(lessonListRequestMap)
        } catch (e: Exception) {
            Log.e("강좌 목록 조회 응답 실패", "${e.message}", e)
            null
        }
    }

    suspend fun fetchLessonDetail(lessonId: Long): LessonDetailResponse? {
        return try {
            lessonService.getLessonDetail(lessonId)
        } catch (e: Exception) {
            Log.e("강좌 상세 조회 응답 실패", "${e.message}", e)
            null
        }
    }

    suspend fun fetchLessonCart(): List<CartResponse>? {
        return try {
            lessonService.getLessonCart()
        } catch (e: Exception) {
            Log.e("강좌 장바구니 조회 응답 실패", "${e.message}", e)
            null
        }
    }

    suspend fun fetchAddLessonCart(cartRequest: CartRequest) : CommonResponse? {
        return try {
            lessonService.addLessonCart(cartRequest)
        } catch (e: Exception) {
            Log.e("강좌 장바구니 담기 응답 실패", "${e.message}", e)
            null
        }
    }

    suspend fun fetchPayLesson(sugangRequest: SugangRequest) : CommonResponse? {
        return try {
            lessonService.payLesson(sugangRequest)
        } catch (e: Exception) {
            Log.e("강좌 신청 응답 실패", "${e.message}", e)
            null
        }
    }

    suspend fun fetchLessonEnroll(): List<LessonEnrollResponse>? {
        return try {
            lessonService.getLessonListByMember()
        } catch (e: Exception) {
            Log.e("회원별 강좌 목록 조회 응답 실패", "${e.message}", e)
            null
        }
    }

    suspend fun fetchShortFormDetail(shortFormId: Long) {
        try {
            lessonService.getShortFormDetail(shortFormId)
        } catch (e: Exception) {
            Log.e("숏폼 조회 응답 실패", "${e.message}", e)
        }
    }

    suspend fun fetchLessonFieldEnroll(): List<LessonEnrollResponse>? {
        return try {
            lessonService.getLessonFieldListByMember()
        } catch (e: Exception) {
            Log.e("회원별 강좌 목록 조회 응답 실패", "${e.message}", e)
            null
        }
    }

    suspend fun fetchDeleteCart(cartId: Long) {
        try {
            lessonService.removeCart(cartId)
        } catch (e: Exception) {
            Log.e("장바구니 상품 제거 응답 실패", "${e.message}", e)
        }
    }
}