package com.innerpeace.themoonha.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.lesson.LessonDTO

class LessonAdapter(
    private var lessons: List<LessonDTO>,
    private val onLessonClick: (Long) -> Unit,
    private val onAddToCartClick: () -> Unit
) : RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_lesson_item, parent, false)
        return LessonViewHolder(view, onLessonClick, onAddToCartClick)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.bind(lessons[position])
    }

    override fun getItemCount(): Int = lessons.size

    fun updateLessons(newLessons: List<LessonDTO>) {
        this.lessons = newLessons
        notifyDataSetChanged()
    }

    class LessonViewHolder(
        itemView: View, private val onLessonClick: (Long) -> Unit,
        private val onAddToCartClick: () -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
        private val targetDescription: TextView = itemView.findViewById(R.id.targetDescription)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val cnt: TextView = itemView.findViewById(R.id.cnt)
        private val tutorName: TextView = itemView.findViewById(R.id.tutorName)
        private val lessonTime: TextView = itemView.findViewById(R.id.lessonTime)
        private val cost: TextView = itemView.findViewById(R.id.cost)
        private val addToCartButton: Button = itemView.findViewById(R.id.addToCartButton)

        fun bind(lesson: LessonDTO) {
            Glide.with(itemView.context)
                .load(lesson.thumbnailUrl)
                .into(thumbnail)

            targetDescription.text = lesson.getTargetDescription()
            title.text = lesson.title
            cnt.text = "${lesson.cnt}회"
            tutorName.text = lesson.tutorName
            lessonTime.text = lesson.lessonTime
            cost.text = "${lesson.cost} 원"

            itemView.setOnClickListener {
                onLessonClick(lesson.lessonId)
            }

            addToCartButton.setOnClickListener {
                onAddToCartClick()
            }
        }
    }
}
