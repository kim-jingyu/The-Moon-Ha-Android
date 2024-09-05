package com.innerpeace.themoonha.data.exception

/**
 * 분야별 한 입 Exception
 * @author 김진규
 * @since 2024.09.04
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.04  	김진규       최초 생성
 * </pre>
 */
open class FieldException(message: String) : Exception(message)

class FieldRetrievingException() : FieldException("분야별 한 입 데이터 가져오기에 실패했습니다.")
class FieldMakingException() : FieldException("분야별 한 입 콘텐츠 만들기에 실패했습니다.")