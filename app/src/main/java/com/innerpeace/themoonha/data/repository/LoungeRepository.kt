package com.innerpeace.themoonha.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.innerpeace.themoonha.data.model.CommonResponse
import com.innerpeace.themoonha.data.model.lounge.Attendance
import com.innerpeace.themoonha.data.model.lounge.AttendanceMembersResponse
import com.innerpeace.themoonha.data.model.lounge.LoungeCommentRequest
import com.innerpeace.themoonha.data.model.lounge.LoungeHomeResponse
import com.innerpeace.themoonha.data.model.lounge.LoungeListResponse
import com.innerpeace.themoonha.data.model.lounge.LoungePostListResponse
import com.innerpeace.themoonha.data.model.lounge.LoungePostRequest
import com.innerpeace.themoonha.data.model.lounge.LoungePostResponse
import com.innerpeace.themoonha.data.network.LoungeService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * 라운지 레포지토리
 * @author 조희정
 * @since 2024.08.30
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.30  	조희정       최초 생성
 * 2024.08.30   조희정       라운지 목록 불러오기 구현
 * 2024.09.02   조희정       라운지 게시글 상세보기 구현
 * 2024.09.03   조희정       라운지 게시글 저장 구현
 * 2024.09.09   조희정       출석 시작, 출석 수정 구현
 * </pre>
 */
class LoungeRepository(private val loungeService: LoungeService) {

    // 라운지 목록 가져오기
    suspend fun fetchLoungeList(): List<LoungeListResponse>? {
        return try {
            loungeService.getLoungeList()
        } catch (e: Exception) {
            Log.e("라운지 목록 조회 응답 실패", "${e.message}", e)
            null
        }
    }

    // 라운지 홈 정보 가져오기
    suspend fun fetchLoungeHome(loungeId: Long): LoungeHomeResponse? {
        return try {
            loungeService.getLoungeHome(loungeId)
        } catch (e: Exception) {
            Log.e("라운지 홈 조회 응답 실패", "${e.message}", e)
            null
        }
    }

    // 라운지 게시글 목록 가져오기
    suspend fun fetchLoungePostList(loungeId: Long, page: Int, size: Int): List<LoungePostListResponse>? {
        return try {
            loungeService.getLoungePostList(loungeId, page, size)
        } catch (e: Exception) {
            Log.e("라운지 게시물 목록 조회 응답 실패", "${e.message}", e)
            null
        }
    }

    // 게시글 상세 정보 가져오기
    suspend fun fetchPostDetail(loungeId: Long, postId: Long): LoungePostResponse? {
        return try {
            loungeService.getPostDetail(loungeId, postId)
        } catch (e: Exception) {
            Log.e("라운지 게시글 상세 조회 응답 실패", "${e.message}", e)
            null
        }
    }

    // 게시글 저장
    suspend fun registerLoungePost(
        loungePostRequest: LoungePostRequest,
        imageUris: List<Uri>,
        context: Context
    ): CommonResponse? {
        return try {
            val gson = Gson()
            val jsonLoungePostRequest = gson.toJson(loungePostRequest)
            val requestBody =
                jsonLoungePostRequest.toRequestBody("application/json".toMediaTypeOrNull())

            // 이미지 파일들을 MultipartBody.Part로 변환
            val files = imageUris.map { uri -> prepareFilePart("files", uri, context) }

            val response = loungeService.registerLoungePost(requestBody, files)

            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("라운지 게시글 작성 실패", "${e.message}", e)
            null
        }
    }

    // Uri에서 File을 생성하는 함수
    private fun prepareFilePart(
        partName: String,
        fileUri: Uri,
        context: Context
    ): MultipartBody.Part {
        val file = createTempFileFromUri(context, fileUri)
        val requestFile =
            file.asRequestBody(context.contentResolver.getType(fileUri)?.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    // Uri를 사용해 임시 파일을 만드는 함수
    private fun createTempFileFromUri(context: Context, uri: Uri): File {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        inputStream.use { input ->
            FileOutputStream(tempFile).use { output ->
                input?.copyTo(output)
            }
        }
        return tempFile
    }

    // 댓글 저장
    suspend fun registerComment(loungeCommentRequest: LoungeCommentRequest): CommonResponse? {
        return try {
            val response = loungeService.registerComment(loungeCommentRequest)

            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("라운지 댓글 작성 실패", "${e.message}", e)
            null
        }
    }

    // 출석 시작
    suspend fun startAttendance(lessonId: Long): List<Attendance>? {
        return try {
            loungeService.startAttendance(lessonId)
        } catch (e: Exception) {
            Log.e("출석 시작 실패", "${e.message}", e)
            null
        }
    }

    // 출석 수정
    suspend fun updateAttendanceStatus(attendanceId: Long): CommonResponse? {
        return try {
            val response = loungeService.updateAttendanceStatus(attendanceId)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("출석 상태 업데이트 실패", "${e.message}", e)
            null
        }
    }
}