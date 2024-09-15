package com.innerpeace.themoonha.adapter.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.data.model.schedule.ScheduleMonthlyResponse
import com.innerpeace.themoonha.databinding.ItemDialogLessonInfoBinding
import com.innerpeace.themoonha.databinding.ItemNextLessonBinding

class ScheduleDialogAdapter(
    private val lessons: List<ScheduleMonthlyResponse>
) : RecyclerView.Adapter<ScheduleDialogAdapter.LessonViewHolder>() {

    inner class LessonViewHolder(val binding: ItemNextLessonBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val binding = ItemNextLessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LessonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = lessons[position]
        holder.binding.tvTitle.text = lesson.lessonTitle
        holder.binding.tvBranchName.text = lesson.branchName
        holder.binding.tvCnt.text = "${lesson.cnt}회"
        holder.binding.tvTutorName.text = lesson.tutorName
        holder.binding.tvLessonTime.text = lesson.lessonTime
    }

    override fun getItemCount(): Int = lessons.size
}