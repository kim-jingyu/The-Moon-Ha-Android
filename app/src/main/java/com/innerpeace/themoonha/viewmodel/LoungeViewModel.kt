package com.innerpeace.themoonha.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.innerpeace.themoonha.data.model.LoungeListResponse
import com.innerpeace.themoonha.service.LoungeService
import retrofit2.Retrofit

/**
 * 라운지 ViewModel
 * @author 조희정
 * @since 2024.08.30
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.30  	조희정       최초 생성
 * </pre>
 */
class LoungeViewModel(application: Application): AndroidViewModel(application) {
    private lateinit var loungeService: LoungeService


    // 임시 데이터
    fun fetchLoungeList(): List<LoungeListResponse> {
        return listOf(
            LoungeListResponse(
                loungeId = 1,
                loungeImgUrl = "https://themoonha-bucket.s3.ap-northeast-2.amazonaws.com/lounge/20240828_57c041f7-d982-4b83-905c-fb4cc30292ed_image-removebg-preview.png",
                title = "Lounge Title 1",
                latestPostTime = "2024년 08월 27일 오전 08시 29분"
            ),
            LoungeListResponse(
                loungeId = 2,
                loungeImgUrl = "https://themoonha-bucket.s3.ap-northeast-2.amazonaws.com/lounge/20240828_57c041f7-d982-4b83-905c-fb4cc30292ed_image-removebg-preview.png",
                title = "오오또모 후미꼬 일본어 회화(초급2,기본 문법 습득이상)",
                latestPostTime = "2024년 08월 27일 오전 08시 29분"
            ),
            LoungeListResponse(
                loungeId = 3,
                loungeImgUrl = "https://themoonha-bucket.s3.ap-northeast-2.amazonaws.com/lounge/20240828_57c041f7-d982-4b83-905c-fb4cc30292ed_image-removebg-preview.png",
                title = "Lounge Title 2",
                latestPostTime = "5분 전"
            ),
            LoungeListResponse(
                loungeId = 4,
                loungeImgUrl = "https://themoonha-bucket.s3.ap-northeast-2.amazonaws.com/lounge/20240828_57c041f7-d982-4b83-905c-fb4cc30292ed_image-removebg-preview.png",
                title = "Lounge Title 2",
                latestPostTime = "5분 전"
            ),
            LoungeListResponse(
                loungeId = 5,
                loungeImgUrl = "https://themoonha-bucket.s3.ap-northeast-2.amazonaws.com/lounge/20240828_57c041f7-d982-4b83-905c-fb4cc30292ed_image-removebg-preview.png",
                title = "Lounge Title 2",
                latestPostTime = "5분 전"
            )
        )
    }
}