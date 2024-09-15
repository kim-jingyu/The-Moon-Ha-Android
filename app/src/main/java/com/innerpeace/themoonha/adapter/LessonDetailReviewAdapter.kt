package com.innerpeace.themoonha.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.R

class LessonDetailReviewAdapter(private val context: Context, private val reviewImages: List<Int>) :
    RecyclerView.Adapter<LessonDetailReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewReviewDetails: ImageView = view.findViewById(R.id.imageViewReviewDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.fragment_lesson_detail_review_item, parent, false)
        return ReviewViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.imageViewReviewDetails.setImageResource(reviewImages[position])
    }

    override fun getItemCount(): Int {
        return reviewImages.size
    }
}