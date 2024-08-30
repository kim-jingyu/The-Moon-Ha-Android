package com.innerpeace.themoonha.ui.fragment.lounge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innerpeace.themoonha.data.model.LoungeListResponse
import com.innerpeace.themoonha.databinding.ItemLoungeBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * 라운지 목록 Recycler View Holder
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
class LoungeViewHolder(private val binding: ItemLoungeBinding) : RecyclerView.ViewHolder(binding.root) {

    // Recycler View에 데이터 바인딩
    fun onBind(lounge : LoungeListResponse) {
        Glide.with(binding.ivLoungeImage.context)
            .load(lounge.loungeImgUrl)
            .into(binding.ivLoungeImage)

        binding.tvLoungeTitle.text = lounge.title

        val formattedTime = getFormattedPostedTime(lounge.latestPostTime)

        binding.ivNewIcon.visibility = if (formattedTime == lounge.latestPostTime) View.VISIBLE else View.GONE
        binding.tvLatestPostTime.text = formattedTime
    }

    companion object {
        fun from(parent: ViewGroup): LoungeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemLoungeBinding.inflate(layoutInflater, parent, false)
            return LoungeViewHolder(binding)
        }
    }

    fun getFormattedPostedTime(latestPostTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy년 MM월 dd일 a hh시 mm분", Locale.KOREAN)
            val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREAN)
            val date = inputFormat.parse(latestPostTime)
            if (date != null) {
                outputFormat.format(date)
            } else {
                latestPostTime
            }
        } catch (e: Exception) {
            latestPostTime
        }
    }
}