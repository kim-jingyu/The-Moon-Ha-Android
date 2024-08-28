package com.innerpeace.themoonha.ui.fragment.beforeafter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.adapter.BeforeAfterAdapter
import com.innerpeace.themoonha.data.model.BeforeAfterContent
import com.innerpeace.themoonha.databinding.FragmentBeforeAfterListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Before&After 프래그먼트
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
class BeforeAfterListFragment : Fragment() {
    private var _binding: FragmentBeforeAfterListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BeforeAfterAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBeforeAfterListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val contents = withContext(Dispatchers.IO) {
                getContents()
            }

            val gridLayoutManager = GridLayoutManager(context, 2)
            binding.recyclerView.layoutManager = gridLayoutManager

            adapter = BeforeAfterAdapter(contents, this@BeforeAfterListFragment)
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
    }

    private fun playVideo(playStatus: Boolean) {
        if (_binding == null) {
            return
        }

        lifecycleScope.launch {
            val layoutManager = binding.recyclerView.layoutManager
            if (layoutManager is GridLayoutManager) {
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                for (i in 0 until binding.recyclerView.childCount) {
                    val content = binding.recyclerView.getChildAt(i)
                    val viewHolder = binding.recyclerView.getChildViewHolder(content) as BeforeAfterAdapter.ViewHolder

                    if (i + firstVisibleItemPosition in firstVisibleItemPosition..lastVisibleItemPosition) {
                        withContext(Dispatchers.Main) {
                            if (playStatus) {
                                viewHolder.playVideo()
                            } else {
                                viewHolder.stopVideo()
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            viewHolder.stopVideo()
                        }
                    }
                }
            } else {
                Log.e("BeforeAfterListFragment", "GridLayoutManager 할당 에러!!")
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
                title = "Example 1",
                profileImageUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                memberName = "User 1",
                hashtags = mutableListOf("안녕", "하세요", "나무", "사과", "바나나", "파인애플")
            ),
            BeforeAfterContent(
                beforeUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/c.png",
                beforeIsImage = true,
                afterUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/c.png",
                afterIsImage = true,
                title = "Barça Atlètic kicks off its League campaign this Sunday 25 August at 8pm CEST with the visit of Real Unión Club de Irún to the Estadi Johan Cruyff. Throughout the season, the competition can be followed for free and live on Barça One, with match broadcasts available in three languages (Catalan, Spanish, and English), accessible in all territories where Barça One operates (Europe, United States, Canada, and Latin America).",
                profileImageUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                memberName = "User 1",
                hashtags = mutableListOf("안녕", "하세요", "나무", "사과", "바나나", "파인애플")
            ),
            BeforeAfterContent(
                beforeUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/%E1%84%8B%E1%85%AE%E1%86%B7%E1%84%8C%E1%85%B5%E1%86%A8%E1%84%8B%E1%85%B5%E1%84%82%E1%85%B3%E1%86%AB+%E1%84%89%E1%85%A1%E1%84%85%E1%85%A1%E1%86%B71.mp4",
                beforeIsImage = false,
                afterUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                afterIsImage = true,
                title = "Example 1",
                profileImageUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                memberName = "User 1",
                hashtags = mutableListOf("안녕", "하세요", "나무", "사과", "바나나", "파인애플")
            ),
            BeforeAfterContent(
                beforeUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/%E1%84%8B%E1%85%AE%E1%86%B7%E1%84%8C%E1%85%B5%E1%86%A8%E1%84%8B%E1%85%B5%E1%84%82%E1%85%B3%E1%86%AB+%E1%84%89%E1%85%A1%E1%84%85%E1%85%A1%E1%86%B71.mp4",
                beforeIsImage = false,
                afterUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                afterIsImage = true,
                title = "Example 1",
                profileImageUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                memberName = "User 1",
                hashtags = mutableListOf("안녕", "하세요", "나무", "사과", "바나나", "파인애플")
            ),
            BeforeAfterContent(
                beforeUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/%EC%A7%B1%EA%B5%AC%EC%99%80+%EC%B9%9C%EA%B5%AC_2560X1440.jpg",
                beforeIsImage = true,
                afterUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/%EC%A7%B1%EA%B5%AC%EC%99%80+%EC%B9%9C%EA%B5%AC_2560X1440.jpg",
                afterIsImage = true,
                title = "Barça Atlètic kicks off its League campaign this Sunday 25 August at 8pm CEST with the visit of Real Unión Club de Irún to the Estadi Johan Cruyff. Throughout the season, the competition can be followed for free and live on Barça One, with match broadcasts available in three languages (Catalan, Spanish, and English), accessible in all territories where Barça One operates (Europe, United States, Canada, and Latin America).",
                profileImageUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                memberName = "User 1",
                hashtags = mutableListOf("안녕", "하세요", "나무", "사과", "바나나", "파인애플")
            ),
            BeforeAfterContent(
                beforeUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/%E1%84%8B%E1%85%AE%E1%86%B7%E1%84%8C%E1%85%B5%E1%86%A8%E1%84%8B%E1%85%B5%E1%84%82%E1%85%B3%E1%86%AB+%E1%84%89%E1%85%A1%E1%84%85%E1%85%A1%E1%86%B71.mp4",
                beforeIsImage = false,
                afterUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                afterIsImage = true,
                title = "Example 1",
                profileImageUrl = "https://moonha.s3.ap-northeast-2.amazonaws.com/tip2.png",
                memberName = "User 1",
                hashtags = mutableListOf("안녕", "하세요", "나무", "사과", "바나나", "파인애플")
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}