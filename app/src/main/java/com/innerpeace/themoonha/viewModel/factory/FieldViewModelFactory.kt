package com.innerpeace.themoonha.viewModel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.innerpeace.themoonha.data.repository.FieldRepository
import com.innerpeace.themoonha.viewModel.FieldViewModel

/**
 * 분야별 한입 ViewModelFactory
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
class FieldViewModelFactory(private val dataSource: FieldRepository) :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FieldViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FieldViewModel(dataSource) as T
        }
        throw IllegalArgumentException("뷰 모델이 불일치합니다.")
    }
}