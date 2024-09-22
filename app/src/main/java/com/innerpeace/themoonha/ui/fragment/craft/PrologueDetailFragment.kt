import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.CraftService
import com.innerpeace.themoonha.data.repository.CraftRepository
import com.innerpeace.themoonha.databinding.FragmentPrologueDetailBinding
import com.innerpeace.themoonha.viewModel.CraftViewModel
import com.innerpeace.themoonha.viewModel.factory.CraftViewModelFactory

/**
 * 문화공방 프롤로그 상세 조회 페이지 프레그먼트
 *
 * @author 손승완
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.04  	손승완       최초 생성
 * </pre>
 * @since 2024.09.04
 */
class PrologueDetailFragment : Fragment() {
    private var _binding: FragmentPrologueDetailBinding? = null
    private val binding get() = _binding!!
    private val craftViewModel: CraftViewModel by activityViewModels {
        CraftViewModelFactory(
            CraftRepository(ApiClient.getClient().create(CraftService::class.java))
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.findViewById<Toolbar>(R.id.toolbar)?.visibility = View.GONE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)?.visibility = View.GONE
        _binding = FragmentPrologueDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        craftViewModel.currentPrologueDetail.observe(viewLifecycleOwner, Observer { prologue ->
            prologue?.let { currentPrologue ->
                val alreadyLiked = currentPrologue.alreadyLiked
                val likeCnt = currentPrologue.likeCnt
                val prologueId = currentPrologue.prologueId

                binding.titleTextView.text = currentPrologue.title
                binding.likeCountTextView.text = likeCnt.toString()

                val heartIcon = if (alreadyLiked) {
                    R.drawable.prologue_already_liked
                } else {
                    R.drawable.prologue_like
                }
                binding.heartImageView.setImageResource(heartIcon)

                binding.heartImageView.setOnClickListener {
                    if (!alreadyLiked) {
                        val newLikeCount = likeCnt + 1
                        binding.likeCountTextView.text = newLikeCount.toString()
                        binding.heartImageView.setImageResource(R.drawable.prologue_already_liked)

                        val updatedPrologue = currentPrologue.copy(likeCnt = newLikeCount, alreadyLiked = true)
                        craftViewModel.setCurrentPrologueDetail(updatedPrologue)
                        craftViewModel.likePrologue(prologueId)
                    }
                }

                binding.leftArrow.setOnClickListener {
                    findNavController().navigateUp()
                }

                binding.videoView.setVideoPath(currentPrologue.videoUrl)
                binding.videoView.setOnPreparedListener {
                    it.start()
                }

                binding.videoView.setOnClickListener {
                    if (binding.videoView.isPlaying) {
                        binding.videoView.pause()
                        showIconWithAnimation(binding.pauseIcon) // 일시정지 아이콘 표시
                    } else {
                        binding.videoView.start()
                        showIconWithAnimation(binding.playIcon) // 재생 아이콘 표시
                    }
                }
            }
        })

        craftViewModel.toastMessage.observe(viewLifecycleOwner, Observer { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                craftViewModel.clearToastMessage()
            }
            message
        })
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
