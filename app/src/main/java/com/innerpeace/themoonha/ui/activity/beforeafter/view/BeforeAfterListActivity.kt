package com.innerpeace.themoonha.ui.activity.beforeafter.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.adapter.BeforeAfterAdapter
import com.innerpeace.themoonha.data.model.BeforeAfterContent
import com.innerpeace.themoonha.databinding.ActivityBeforeAfterListBinding

/**
 * Before&After 액티비티
 * @author 김진규
 * @since 2024.08.25
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.25  	김진규       최초 생성
 * </pre>
 */
class BeforeAfterListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBeforeAfterListBinding
    private lateinit var adapter: BeforeAfterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeforeAfterListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val contents = getContents()

        val gridLayoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.layoutManager = gridLayoutManager

        adapter = BeforeAfterAdapter(contents)
        binding.recyclerView.adapter = adapter

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    playVideo(true)
                } else {
                    playVideo(false)
                }
            }
        })

        binding.recyclerView.viewTreeObserver.addOnGlobalLayoutListener {
            playVideo(true)
        }
    }

    private fun playVideo(playStatus: Boolean) {
        val gridLayoutManager = binding.recyclerView.layoutManager as GridLayoutManager
        val firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition()

        for (i in 0 until binding.recyclerView.childCount) {
            val content = binding.recyclerView.getChildAt(i)
            val viewHolder = binding.recyclerView.getChildViewHolder(content) as BeforeAfterAdapter.ViewHolder

            if (i + firstVisibleItemPosition in firstVisibleItemPosition..lastVisibleItemPosition) {
                if (playStatus) {
                    viewHolder.playVideo()
                } else {
                    viewHolder.stopVideo()
                }
            } else {
                viewHolder.stopVideo()
            }
        }
    }

    private fun getContents(): List<BeforeAfterContent> {
        return listOf(
            BeforeAfterContent(
                beforeUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/%E1%84%8B%E1%85%AE%E1%86%B7%E1%84%8C%E1%85%B5%E1%86%A8%E1%84%8B%E1%85%B5%E1%84%82%E1%85%B3%E1%86%AB+%E1%84%89%E1%85%A1%E1%84%85%E1%85%A1%E1%86%B71.mp4",
                beforeIsImage = false,
                afterUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/%E1%84%8B%E1%85%AE%E1%86%B7%E1%84%8C%E1%85%B5%E1%86%A8%E1%84%8B%E1%85%B5%E1%84%82%E1%85%B3%E1%86%AB+%E1%84%89%E1%85%A1%E1%84%85%E1%85%A1%E1%86%B71.mp4",
                afterIsImage = false,
                title = "Barça Atlètic kicks off its League campaign this Sunday 25 August at 8pm CEST with the visit of Real Unión Club de Irún to the Estadi Johan Cruyff. Throughout the season, the competition can be followed for free and live on Barça One, with match broadcasts available in three languages (Catalan, Spanish, and English), accessible in all territories where Barça One operates (Europe, United States, Canada, and Latin America).",
                profileImageUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                memberName = "User 1"
            ),
            BeforeAfterContent(
                beforeUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/%E1%84%8B%E1%85%AE%E1%86%B7%E1%84%8C%E1%85%B5%E1%86%A8%E1%84%8B%E1%85%B5%E1%84%82%E1%85%B3%E1%86%AB+%E1%84%89%E1%85%A1%E1%84%85%E1%85%A1%E1%86%B71.mp4",
                beforeIsImage = false,
                afterUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                afterIsImage = true,
                title = "Example 1",
                profileImageUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                memberName = "User 1"
            ),
            BeforeAfterContent(
                beforeUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/%E1%84%8B%E1%85%AE%E1%86%B7%E1%84%8C%E1%85%B5%E1%86%A8%E1%84%8B%E1%85%B5%E1%84%82%E1%85%B3%E1%86%AB+%E1%84%89%E1%85%A1%E1%84%85%E1%85%A1%E1%86%B71.mp4",
                beforeIsImage = false,
                afterUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                afterIsImage = true,
                title = "Example 1",
                profileImageUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                memberName = "User 1"
            ),
            BeforeAfterContent(
                beforeUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/%E1%84%8B%E1%85%AE%E1%86%B7%E1%84%8C%E1%85%B5%E1%86%A8%E1%84%8B%E1%85%B5%E1%84%82%E1%85%B3%E1%86%AB+%E1%84%89%E1%85%A1%E1%84%85%E1%85%A1%E1%86%B71.mp4",
                beforeIsImage = false,
                afterUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                afterIsImage = true,
                title = "Example 1",
                profileImageUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                memberName = "User 1"
            ),
            BeforeAfterContent(
                beforeUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/%E1%84%8B%E1%85%AE%E1%86%B7%E1%84%8C%E1%85%B5%E1%86%A8%E1%84%8B%E1%85%B5%E1%84%82%E1%85%B3%E1%86%AB+%E1%84%89%E1%85%A1%E1%84%85%E1%85%A1%E1%86%B71.mp4",
                beforeIsImage = false,
                afterUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                afterIsImage = true,
                title = "Example 1",
                profileImageUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                memberName = "User 1"
            ),
            BeforeAfterContent(
                beforeUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/%E1%84%8B%E1%85%AE%E1%86%B7%E1%84%8C%E1%85%B5%E1%86%A8%E1%84%8B%E1%85%B5%E1%84%82%E1%85%B3%E1%86%AB+%E1%84%89%E1%85%A1%E1%84%85%E1%85%A1%E1%86%B71.mp4",
                beforeIsImage = false,
                afterUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                afterIsImage = true,
                title = "Example 1",
                profileImageUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                memberName = "User 1"
            )
        )
    }
}