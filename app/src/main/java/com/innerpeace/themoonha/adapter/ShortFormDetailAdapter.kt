package com.innerpeace.themoonha.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.lesson.ShortFormDTO

class ShortFormDetailAdapter(private var shortForms: List<ShortFormDTO>) :
    RecyclerView.Adapter<ShortFormDetailAdapter.ShortFormDetailViewHolder>() {

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
    }

    override fun getItemCount(): Int = shortForms.size

    class ShortFormDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val videoView: VideoView = itemView.findViewById(R.id.videoView)
        private val tutorLessonTitle: TextView = itemView.findViewById(R.id.tutorLessonTitle)
        private val playIcon: ImageView = itemView.findViewById(R.id.playIcon)
        private val pauseIcon: ImageView = itemView.findViewById(R.id.pauseIcon)

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
