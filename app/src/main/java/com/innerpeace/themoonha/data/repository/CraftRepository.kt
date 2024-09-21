package com.innerpeace.themoonha.data.repository

import android.util.Log
import com.innerpeace.themoonha.data.model.craft.CommonResponse
import com.innerpeace.themoonha.data.model.craft.CraftMainResponse
import com.innerpeace.themoonha.data.model.craft.SuggestionRequest
import com.innerpeace.themoonha.data.model.craft.SuggestionResponse
import com.innerpeace.themoonha.data.network.CraftService

/**
 * 문화공방 api 조회 레포지토리
 *
 * @author 손승완
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  	손승완       최초 생성
 * </pre>
 * @since 2024.09.03
 */
class CraftRepository(private val craftService: CraftService) {
    suspend fun fetchCraftMain(): CraftMainResponse? {
        return try {
            craftService.getCraftMain()
        } catch (e: Exception) {
            Log.e("문화공방 메인 페이지 응답 실패", "${e.message}", e)
            null
        }
    }

    suspend fun fetchPrologueLike(prologueId: Long): CommonResponse? {
        return try {
            craftService.likePrologue(prologueId)
        } catch (e: Exception) {
            Log.e("프롤로그 좋아요 실패", "${e.message}", e)
            null
        }
    }

    suspend fun fetchWishLessonVote(wishLessonId: Long): CommonResponse? {
        return try {
            craftService.voteWishLesson(wishLessonId)
        } catch (e: Exception) {
            Log.e("듣고싶은 강좌 투표 실패", "${e.message}", e)
            null
        }
    }

    suspend fun fetchSuggestionList(pageNum: Int): SuggestionResponse? {
        return try {
            craftService.getSuggestionList(pageNum)
        } catch (e: Exception) {
            Log.e("제안합니다 댓글 목록 조회 실패", "${e.message}", e)
            null
        }
    }

    suspend fun fetchSuggestionWrite(suggestionRequest: SuggestionRequest): CommonResponse? {
        return try {
            craftService.writeSuggestion(suggestionRequest)
        } catch (e: Exception) {
            Log.e("제안합니다 댓글 작성 실패", "${e.message}", e)
            null
        }
    }
}