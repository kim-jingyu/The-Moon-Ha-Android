package com.innerpeace.themoonha.adapter.lesson

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.lesson.ShortFormDTO
import com.innerpeace.themoonha.viewModel.LessonViewModel

/**
 * 숏폼 상세 조회 어댑터
 *
 * @author 손승완
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.01  	손승완       최초 생성
 * </pre>
 * @since 2024.09.01
 */
class ShortFormDetailAdapter(
    private var shortForms: List<ShortFormDTO>,
    private val viewModel: LessonViewModel
) : RecyclerView.Adapter<ShortFormDetailAdapter.ShortFormDetailViewHolder>() {

    fun getShortFormAt(position: Int): ShortFormDTO? {
        return if (position in shortForms.indices) shortForms[position] else null
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShortFormDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_short_form_detail_item, parent, false)
        return ShortFormDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShortFormDetailViewHolder, position: Int) {
        val shortForm = shortForms[position]
        holder.bind(shortForm)

        holder.showDetailButton.setOnClickListener {
            viewModel.currentPage = position
            val bundle = bundleOf("lessonId" to shortForm.lessonId)
            holder.itemView.findNavController().navigate(R.id.action_shortFormDetailFragment_to_lessonDetailFragment, bundle)
        }
    }

    override fun getItemCount(): Int = shortForms.size

    class ShortFormDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val videoView: VideoView = itemView.findViewById(R.id.videoView)
        private val tutorLessonTitle: TextView = itemView.findViewById(R.id.tutorLessonTitle)
        private val playIcon: ImageView = itemView.findViewById(R.id.playIcon)
        private val pauseIcon: ImageView = itemView.findViewById(R.id.pauseIcon)
        val showDetailButton: Button = itemView.findViewById(R.id.showDetailButton) // 클릭 이벤트 설정할 버튼

        fun bind(shortFormDTO: ShortFormDTO) {
            videoView.setVideoPath(shortFormDTO.videoUrl)
            tutorLessonTitle.text = "${shortFormDTO.tutorName} | ${shortFormDTO.lessonTitle}"

            videoView.setOnPreparedListener {
                it.start()
            }

            itemView.setOnClickListener {
                if (videoView.isPlaying) {
                    videoView.pause()
                    showIconWithAnimation(pauseIcon)
                } else {
                    videoView.start()
                    showIconWithAnimation(playIcon)
                }
            }
        }

        private fun showIconWithAnimation(icon: ImageView) {
            icon.visibility = View.VISIBLE
            icon.alpha = 1.0f

            val animator = ObjectAnimator.ofFloat(icon, View.ALPHA, 1.0f, 0.0f)
            animator.duration = 1500

            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    icon.visibility = View.GONE
                }
            })

            animator.start()
        }
    }
}
