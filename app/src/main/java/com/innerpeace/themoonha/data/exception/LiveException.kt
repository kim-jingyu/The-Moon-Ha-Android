package com.innerpeace.themoonha.data.exception

/**
 * 실시간 강좌 Exception
 * @author 김진규
 * @since 2024.09.06
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06  	김진규       최초 생성
 * </pre>
 */
open class LiveException(message: String) : Exception(message)

class LiveRetrievingException() : LiveException("실시간 강좌 데이터 가져오기에 실패했습니다.")
class LiveMakingException() : LiveException("실시간 강좌 콘텐츠 만들기에 실패했습니다.")
class LiveJoinException() : LiveException("실시간 강좌 콘텐츠 참여에 실패했습니다.")
class LiveLeaveException() : LiveException("실시간 강좌 콘텐츠 나가기에 실패했습니다.")
class LiveLikeException() : LiveException("실시간 강좌 콘텐츠 좋아요에 실패했습니다.")
class LiveSharedException() : LiveException("실시간 강좌 콘텐츠 공유에 실패했습니다.")