package com.innerpeace.themoonha.data.exception

/**
 * 비포애프터 Exception
 * @author 김진규
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  	김진규       최초 생성
 * </pre>
 */
open class BeforeAfterException(message: String) : Exception(message)

class BeforeAfterRetrievingException() : BeforeAfterException("비포애프터 데이터 가져오기에 실패했습니다.")
class BeforeAfterMakingException() : BeforeAfterException("비포애프터 콘텐츠 만들기에 실패했습니다.")