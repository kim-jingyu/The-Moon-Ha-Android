package com.innerpeace.themoonha.viewModel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.innerpeace.themoonha.data.repository.BeforeAfterRepository
import com.innerpeace.themoonha.viewModel.BeforeAfterViewModel

/**
 * 비포애프터 API ViewModelFactory
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
class BeforeAfterViewModelFactory(private val dataSource: BeforeAfterRepository) :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BeforeAfterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BeforeAfterViewModel(dataSource) as T
        }
        throw IllegalArgumentException("뷰 모델이 불일치합니다.")
    }
}