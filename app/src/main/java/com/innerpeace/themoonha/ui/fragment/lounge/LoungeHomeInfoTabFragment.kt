package com.innerpeace.themoonha.ui.fragment.lounge

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.innerpeace.themoonha.adapter.lounge.AttendanceViewAdapter
import com.innerpeace.themoonha.adapter.lounge.LoungeHomeMemberViewAdapter
import com.innerpeace.themoonha.data.model.lounge.AttendanceMembersResponse
import com.innerpeace.themoonha.viewModel.SharedViewModel
import com.innerpeace.themoonha.data.model.lounge.LoungeHomeResponse
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LoungeService
import com.innerpeace.themoonha.data.repository.LoungeRepository
import com.innerpeace.themoonha.databinding.DialogAttendanceBinding
import com.innerpeace.themoonha.databinding.FragmentLoungeHomeInfoTabBinding
import com.innerpeace.themoonha.ui.util.ConditionalScrollLayoutManager
import com.innerpeace.themoonha.viewModel.LoungeViewModel
import com.innerpeace.themoonha.viewModel.factory.LoungeViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 라운지 강좌 정보 탭 프레그먼트
 * @author 조희정
 * @since 2024.09.01
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.01  	조희정       최초 생성
 * 2024.09.01   조희정       강사 정보, 회원목록 recycler view 구현
 * 2024.09.09   조희정       출석 시작, 출석 정보 테이블 구현
 * </pre>
 */
class LoungeHomeInfoTabFragment : Fragment() {
    private var _binding: FragmentLoungeHomeInfoTabBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var layoutManager: ConditionalScrollLayoutManager

    private lateinit var adapter: LoungeHomeMemberViewAdapter
    private val viewModel: LoungeViewModel by activityViewModels {
        LoungeViewModelFactory(LoungeRepository(ApiClient.getClient().create(LoungeService::class.java)))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoungeHomeInfoTabBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 스크롤 Control
        layoutManager = ConditionalScrollLayoutManager(context)
        binding.rvMemberList.layoutManager = layoutManager

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        sharedViewModel.isScrollEnabled.observe(viewLifecycleOwner, { isEnabled ->
            layoutManager.setScrollEnabled(isEnabled)
        })

        // 출석 시작 버튼
        binding.btnAttendanceStart.setOnClickListener {
            startAttendanceDialog()
        }

        viewModel.loungeHome.observe(viewLifecycleOwner, Observer { home ->
            if (home != null) {
                Glide.with(binding.ivProfileImage.context)
                    .load(home.loungeInfo.tutorImgUrl)
                    .circleCrop()
                    .into(binding.ivProfileImage)
                binding.tvTutorName.text = home.loungeInfo.tutorName
                if (home.attendanceList.attendanceDates.isEmpty() || home.attendanceList.students.isEmpty()) {
                    binding.tableAttendance.visibility = View.INVISIBLE
                    binding.tvNoAttendance.visibility = View.VISIBLE
                } else {
                    binding.tableAttendance.visibility = View.VISIBLE
                    binding.tvNoAttendance.visibility = View.INVISIBLE
                    setupAttendance(home.attendanceList, home.loungeInfo.permissionYn)
                }
                binding.tvPlanDetail.text = home.loungeInfo.summary
                setupMemberRecyclerView(home.loungeMemberList)

                binding.btnAttendanceStart.visibility = if (home.loungeInfo.permissionYn) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        })
    }

    // 출석 시작 다이얼로그
    private fun startAttendanceDialog() {
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val attendanceDates = viewModel.loungeHome.value?.attendanceList?.attendanceDates

        // 오늘 이미 출석을 진행한 경우
        if (attendanceDates?.contains(todayDate) == true) {
            AlertDialog.Builder(requireContext())
                .setMessage("오늘은 이미 출석을 완료하였습니다")
                .setPositiveButton("확인", null)
                .show()
        } else {
            // 출석 시작
            viewModel.startAttendance()

            val dialogBinding = DialogAttendanceBinding.inflate(LayoutInflater.from(requireContext()))

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogBinding.root)
                .setCancelable(false)
                .create()

            dialogBinding.btnOk.setOnClickListener {
                dialog.dismiss()
                viewModel.fetchLoungeHome(viewModel.selectedLoungeId.value!!)
            }

            dialog.show()

            // RecyclerView 설정
            dialogBinding.rvAttendanceMember.layoutManager = GridLayoutManager(requireContext(), 3)

            viewModel.attendanceStartList.observe(viewLifecycleOwner, { memberList ->
                val adapter = memberList?.let { AttendanceViewAdapter(it, viewModel) }
                dialogBinding.rvAttendanceMember.adapter = adapter
            })
        }
    }


    // 출석 정보
    private fun setupAttendance(item: AttendanceMembersResponse, permissionYn: Boolean) {
        val attendanceDates = item.attendanceDates
        val tableLayout = binding.attendanceTable

        // 헤더
        tableLayout.removeAllViews()

        val paddingInDp = 4
        val paddingInPx = (paddingInDp * resources.displayMetrics.density).toInt()

        if (permissionYn) {
            // permissionYn이 true일 때, 여러 학생의 출석 정보를 표시하는 테이블
            val headerRow = TableRow(context)

            val headerNumber = TextView(context).apply {
                text = "번호"
                setTableTextViewHeaderProperties(this)
                setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
            }
            headerRow.addView(headerNumber)

            val headerName = TextView(context).apply {
                text = "이름"
                setTableTextViewHeaderProperties(this)
                setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
            }
            headerRow.addView(headerName)

            val headerCount = TextView(context).apply {
                text = "출석횟수"
                setTableTextViewHeaderProperties(this)
                setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
            }
            headerRow.addView(headerCount)

            attendanceDates.forEachIndexed { index, date ->
                val dateTextView = TextView(context) // TextView 생성
                val spannableString = SpannableString("${index + 1}회차\n$date")

                setTableTextViewHeaderProperties(dateTextView)

                dateTextView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)

                spannableString.setSpan(
                    RelativeSizeSpan(0.8f),
                    spannableString.indexOf("\n") + 1, spannableString.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // 텍스트 적용 및 레이아웃 속성 설정
                dateTextView.text = spannableString
                dateTextView.gravity = Gravity.CENTER

                // 행에 추가
                headerRow.addView(dateTextView)
            }

            tableLayout.addView(headerRow)

            // 출석 정보 추가
            item.students.forEachIndexed { index, student ->
                val row = TableRow(context)

                // 번호
                val numberTextView = TextView(context).apply {
                    text = (index + 1).toString()
                    setTableTextViewRowProperties(this)
                    setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
                }
                row.addView(numberTextView)

                // 이름
                val nameTextView = TextView(context).apply {
                    text = student.name
                    setTableTextViewRowProperties(this)
                    setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
                }
                row.addView(nameTextView)

                // 출석 횟수
                val countTextView = TextView(context).apply {
                    text = student.attendanceCnt.toString()
                    setTableTextViewRowProperties(this)
                    setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
                }
                row.addView(countTextView)

                // 출석 상태
                student.attendance.forEach { isPresent ->
                    val attendanceTextView = TextView(context).apply {
                        text = if (isPresent) "O" else "X"
                        setTableTextViewRowProperties(this)
                        setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
                        setTextColor(
                            if (isPresent) Color.GREEN else Color.RED // O는 초록색, X는 빨간색
                        )
                    }
                    row.addView(attendanceTextView)
                }

                // 테이블에 행 추가
                tableLayout.addView(row)
            }

        } else {
            // permissionYn이 false일 때, 한 명의 학생의 출석 정보를 날짜별로 표시하는 테이블
            val headerRow = TableRow(context)

            val headerNumber = TextView(context).apply {
                text = "번호"
                setTableTextViewHeaderProperties(this)
                setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
            }
            headerRow.addView(headerNumber)

            val headerDate = TextView(context).apply {
                text = "날짜"
                setTableTextViewHeaderProperties(this)
                setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
            }
            headerRow.addView(headerDate)

            val headerAttendance = TextView(context).apply {
                text = "출결"
                setTableTextViewHeaderProperties(this)
                setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
            }
            headerRow.addView(headerAttendance)

            tableLayout.addView(headerRow)

            // 학생 한 명의 출석 정보를 날짜별로 표시
            val student = item.students.firstOrNull() // 학생 한 명만 존재
            student?.let {
                attendanceDates.forEachIndexed { index, date ->
                    val row = TableRow(context)

                    // 번호
                    val numberTextView = TextView(context).apply {
                        text = (index + 1).toString()
                        setTableTextViewRowProperties(this)
                        setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
                    }
                    row.addView(numberTextView)

                    // 날짜
                    val dateTextView = TextView(context).apply {
                        text = date
                        setTableTextViewRowProperties(this)
                        setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
                    }
                    row.addView(dateTextView)

                    // 출결 상태
                    val attendanceTextView = TextView(context).apply {
                        text = if (student.attendance[index]) "O" else "X"
                        setTableTextViewRowProperties(this)
                        setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
                        setTextColor(
                            if (student.attendance[index]) Color.GREEN else Color.RED // O는 초록색, X는 빨간색
                        )
                    }
                    row.addView(attendanceTextView)

                    // 테이블에 행 추가
                    tableLayout.addView(row)
                }
            }
        }
    }

    private fun setTableTextViewHeaderProperties(textView: TextView) {
        textView.apply {
            textSize = 12f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
        }
    }

    private fun setTableTextViewRowProperties(textView: TextView) {
        textView.apply {
            textSize = 10f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
        }
    }



    // 회원 목록 Recycler view
    private fun setupMemberRecyclerView(item: List<LoungeHomeResponse.LoungeMember>) {
        adapter = LoungeHomeMemberViewAdapter { loungeItem ->
            navigateToDetailFragment(loungeItem)
        }
        binding.rvMemberList.adapter = adapter

        adapter.setItems(item)
    }

    // 회원 페이지로 이동
    private fun navigateToDetailFragment(item: LoungeHomeResponse.LoungeMember) {
        viewModel.setSelectedMemberId(item.memberId)
//        findNavController().navigate(R.id.action_loungeHomeFragment_to_loungePostFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}