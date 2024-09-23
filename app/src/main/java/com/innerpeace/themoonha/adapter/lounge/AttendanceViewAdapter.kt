package com.innerpeace.themoonha.adapter.lounge

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.lounge.Attendance
import com.innerpeace.themoonha.databinding.ItemAttendanceMemberBinding
import com.innerpeace.themoonha.viewModel.LoungeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * 라운지 출석 정보 Recycler View
 * @author 조희정
 * @since 2024.09.09
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.09  	조희정       최초 생성
 * </pre>
 */
class AttendanceViewAdapter(
    private val memberList: List<Attendance>,
    private val viewModel: LoungeViewModel
) : RecyclerView.Adapter<AttendanceViewAdapter.AttendanceViewHolder>() {

    // ViewHolder에 ViewBinding 적용
    class AttendanceViewHolder(private val binding: ItemAttendanceMemberBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Attendance, viewModel: LoungeViewModel) {
            // 프로필 이미지
            Glide.with(binding.ivProfileImage.context)
                .load(item.profileImgUrl)
                .circleCrop()
                .into(binding.ivProfileImage)

            // 이름
            binding.tvName.text = item.name

            // 출석 여부
            binding.btnAttendanceYn.text = if (item.attendanceYn) "출석" else "결석"

            // 출석/결석 update
            binding.btnAttendanceYn.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    val isSuccess = viewModel.updateAttendanceStatus(item.attendanceId)

                    if (isSuccess) {
                        item.attendanceYn = !item.attendanceYn
                        binding.btnAttendanceYn.apply {
                            text = if (item.attendanceYn) "출석" else "결석"
                            setBackgroundResource(if (item.attendanceYn) R.drawable.rounded_green_background else R.drawable.rounded_red_background)
                        }
                    } else {
                        Toast.makeText(binding.root.context, "출석 업데이트 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val binding = ItemAttendanceMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttendanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder.bind(memberList[position], viewModel)
    }

    override fun getItemCount(): Int = memberList.size
}

