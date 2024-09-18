package com.innerpeace.themoonha.adapter.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.schedule.ScheduleMonthlyResponse
import com.innerpeace.themoonha.databinding.ItemNextLessonBinding
import com.innerpeace.themoonha.viewModel.LoungeViewModel

class ScheduleMonthlyDialogAdapter(
    private val lessons: List<ScheduleMonthlyResponse>,
    private val fragment: Fragment,
    private val viewModel: LoungeViewModel
) : RecyclerView.Adapter<ScheduleMonthlyDialogAdapter.LessonViewHolder>() {

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

        if (lesson.loungeId == null || lesson.loungeId == 0L) {
            holder.binding.btnApplyBtn.visibility = View.INVISIBLE
        } else {
            holder.binding.btnApplyBtn.visibility = View.VISIBLE
            holder.binding.btnApplyBtn.setOnClickListener {
                navigateToDetailFragment(lesson)
            }
        }
    }

    override fun getItemCount(): Int = lessons.size

    private fun navigateToDetailFragment(item: ScheduleMonthlyResponse) {
        viewModel.setSelectedLoungeId(item.loungeId)
        fragment.findNavController().navigate(R.id.action_fragment_schedule_to_loungeHomeFragment)
    }
}