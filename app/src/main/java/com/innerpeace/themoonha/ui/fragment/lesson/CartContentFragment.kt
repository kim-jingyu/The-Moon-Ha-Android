package com.innerpeace.themoonha.ui.fragment.lesson

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.adapter.CartItemAdapter
import com.innerpeace.themoonha.data.model.lesson.CartResponse
import com.innerpeace.themoonha.data.model.lesson.SugangRequest
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LessonService
import com.innerpeace.themoonha.data.repository.LessonRepository
import com.innerpeace.themoonha.databinding.FragmentCartContentBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.viewModel.LessonViewModel
import com.innerpeace.themoonha.viewModel.factory.LessonViewModelFactory
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.random.Random

class CartContentFragment : Fragment() {

    private var _binding: FragmentCartContentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LessonViewModel by activityViewModels {
        LessonViewModelFactory(
            LessonRepository(ApiClient.getClient().create(LessonService::class.java))
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private var currentMonth: YearMonth = YearMonth.now()
    private val selectedEvents = mutableMapOf<LocalDate, MutableList<EventInfo>>()
    private val checkedCartIds = mutableSetOf<String>()

    private lateinit var adapter: CartItemAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartContentBinding.inflate(inflater, container, false)
        (activity as? MainActivity)?.hideNavigationBar()
        (activity as? MainActivity)?.setToolbarTitle("장바구니")
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalendar()
        setupLessonCart()
        setupDragHandle()

        binding.applyButton.setOnClickListener {
            applyLessons()
        }

        viewModel.getLessonCart()

    }

    private fun applyLessons() {
        if (checkedCartIds.isEmpty()) {
            showNoSelectionAlert()
            return
        }

        val request = SugangRequest(cartIdList = checkedCartIds.toList())
        viewModel.payLesson(request)
        viewModel.paymentStatus.observe(viewLifecycleOwner, Observer { result ->
            if (result) {
                showSuccessAlert()
                findNavController().navigate(R.id.action_cartContentFragment_to_fragment_lesson)
            } else {
                showFailureAlert()
            }
        })
    }


    private fun showSuccessAlert() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.sugang_success_alert_dialog, null)

        builder.setView(dialogView)

        val alertDialog = builder.create()

        val positiveButton = dialogView.findViewById<Button>(R.id.positiveButton)
        positiveButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
    private fun showFailureAlert() {
        AlertDialog.Builder(requireContext())
            .setTitle("실패")
            .setMessage("강좌 신청에 실패했습니다. 다시 시도해 주세요.")
            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showNoSelectionAlert() {
        AlertDialog.Builder(requireContext())
            .setTitle("알림")
            .setMessage("선택된 강좌가 없습니다.")
            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupCalendar() {
        val calendarView = binding.calendarView
        updateMonthHeader(currentMonth)

        calendarView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return if (e.action == MotionEvent.ACTION_MOVE) {
                    true
                } else {
                    super.onInterceptTouchEvent(rv, e)
                }
            }
        })

        calendarView.apply {
            val firstMonth = YearMonth.now().minusMonths(240)
            val lastMonth = YearMonth.now().plusMonths(240)
            val firstDayOfWeek = WeekFields.of(Locale.KOREAN).firstDayOfWeek
            setup(firstMonth, lastMonth, firstDayOfWeek)
            scrollToMonth(currentMonth)
        }

        binding.previousMonthButton.setOnClickListener {
            currentMonth = currentMonth.minusMonths(1)
            calendarView.scrollToMonth(currentMonth)
            updateMonthHeader(currentMonth)
        }

        binding.nextMonthButton.setOnClickListener {
            currentMonth = currentMonth.plusMonths(1)
            calendarView.scrollToMonth(currentMonth)
            updateMonthHeader(currentMonth)
        }


        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view)
            }

            override fun bind(container: DayViewContainer, day: CalendarDay) {
                val localDate = day.date
                container.dayTextView.text = localDate.dayOfMonth.toString()

                if (day.owner != DayOwner.THIS_MONTH) {
                    container.dayTextView.setTextColor(Color.LTGRAY)
                } else {
                    container.dayTextView.setTextColor(Color.BLACK)
                }

                val events = selectedEvents[localDate]
                if (events != null) {
                    container.colorBarContainer.removeAllViews()
                    container.eventIds.clear() // 기존 이벤트 ID를 지우고 새로운 ID를 추가

                    events.forEach { eventInfo ->
                        val colorBar = View(container.colorBarContainer.context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                10 // 막대 높이
                            ).apply {
                                setMargins(0, 3, 0, 0)
                            }
                            setBackgroundColor(eventInfo.color)
                        }
                        container.colorBarContainer.addView(colorBar)
                        container.eventIds.add(eventInfo.id)
                    }
                    container.colorBarContainer.visibility = View.VISIBLE
                } else {
                    container.colorBarContainer.visibility = View.GONE
                }
            }
        }

        val titlesContainer = binding.titlesContainer
        titlesContainer.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                var dayOfWeek: DayOfWeek? = null
                if (index == 0) {
                    dayOfWeek = DayOfWeek.of(7 - index)
                } else {
                    dayOfWeek = DayOfWeek.of(index)
                }

                val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
                textView.text = title
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateMonthHeader(yearMonth: YearMonth) {
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.getDefault())
        binding.currentMonthText.text = yearMonth.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupLessonCart() {
        val cartRecyclerView = binding.cartRecyclerView
        cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CartItemAdapter { cartItem, isChecked ->
            if (isChecked) {
                if (isLessonTimeConflicting(cartItem)) {
                    showConflictAlert()
                    adapter.uncheckItem(cartItem)
                } else {
                    addEventToCalendar(cartItem)
                    updateCartItemColors()
                    checkedCartIds.add(cartItem.cartId)
                    binding.calendarView.post {
                        binding.calendarView.notifyCalendarChanged()
                    }
                }
            } else {
                removeEventFromCalendar(cartItem)
                updateCartItemColors()
                checkedCartIds.remove(cartItem.cartId)
                binding.calendarView.post {
                    binding.calendarView.notifyCalendarChanged()
                }
            }
        }
        cartRecyclerView.adapter = adapter

        viewModel.lessonCart.observe(viewLifecycleOwner, Observer { cartList ->
            val itemList = mutableListOf<CartItemAdapter.ListItem>()

            cartList.groupBy { it.branchName }.forEach { (branchName, branchCartItems) ->
                itemList.add(CartItemAdapter.ListItem.SectionHeader(branchName))

                branchCartItems.forEach { cartItem ->
                    itemList.add(CartItemAdapter.ListItem.CartItem(cartItem))
                }
            }

            adapter.submitList(itemList)
            updateCartItemColors()
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isLessonTimeConflicting(newCartItem: CartResponse): Boolean {
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val newStartDate = LocalDate.parse(newCartItem.period.split("~")[0], dateFormat)
        val newEndDate = LocalDate.parse(newCartItem.period.split("~")[1], dateFormat)
        var isOneTime = false
        if (newStartDate.equals(newEndDate)) {
            isOneTime = true
        }

        val (newStartTimeStr, newEndTimeStr) = parseLessonTime(newCartItem.lessonTime, isOneTime)

        val newStartTime = LocalTime.parse(newStartTimeStr)
        val newEndTime = LocalTime.parse(newEndTimeStr)

        // 새 강좌의 날짜가 매주 반복되는 경우 확인
        val newDates = generateWeeklyDates(newStartDate, newEndDate)

        for (date in newDates) {
            selectedEvents[date]?.forEach { eventInfo ->
                val existingCartItem = getCartItemById(eventInfo.id)
                if (existingCartItem != null) {
                    val existingStartDate = LocalDate.parse(existingCartItem.period.split("~")[0], dateFormat)
                    val existingEndDate = LocalDate.parse(existingCartItem.period.split("~")[1], dateFormat)
                    if (existingStartDate.equals(existingEndDate)) {
                        isOneTime = true
                    } else {
                        isOneTime = false
                    }
                    val (existingStartTimeStr, existingEndTimeStr) = parseLessonTime(existingCartItem.lessonTime, isOneTime)
                    val existingStartTime = LocalTime.parse(existingStartTimeStr)
                    val existingEndTime = LocalTime.parse(existingEndTimeStr)

                    val existingDates = generateWeeklyDates(existingStartDate, existingEndDate)

                    for (existingDate in existingDates) {
                        if (date == existingDate) {
                            if (newStartTime.isBefore(existingEndTime) && newEndTime.isAfter(existingStartTime)) {
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateWeeklyDates(startDate: LocalDate, endDate: LocalDate): Set<LocalDate> {
        val dates = mutableSetOf<LocalDate>()
        var currentDate = startDate
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate)
            currentDate = currentDate.plusWeeks(1)
        }
        return dates
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCartItemById(id: String): CartResponse? {
        return viewModel.lessonCart.value?.find { cartItem -> cartItem.cartId == id }
    }


    private fun parseLessonTime(lessonTime: String, isOneTime: Boolean): Pair<String, String> {
        if (!isOneTime) {
            val timePart = lessonTime.split(" ")[2]
            val times = timePart.split("~")
            val startTime = times[0]
            val endTime = times[1]
            return Pair(startTime, endTime)
        }

        val timePart = lessonTime.split(" ")[1]
        val times = timePart.split("~")
        val startTime = times[0]
        val endTime = times[1]
        return Pair(startTime, endTime)
    }

    private fun showConflictAlert() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.fragment_cart_duplicate_dialog, null)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        dialogView.findViewById<Button>(R.id.closeButton).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }


    private fun updateCartItemColors() {
        val eventColors = mutableMapOf<String, Int>()
        selectedEvents.values.flatten().forEach { eventInfo ->
            eventColors[eventInfo.id] = eventInfo.color
        }
        (binding.cartRecyclerView.adapter as? CartItemAdapter)?.updateItemColors(eventColors)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addEventToCalendar(cartItem: CartResponse) {
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateRange = cartItem.period.split("~")
        val startDate = LocalDate.parse(dateRange[0], dateFormat)
        val endDate = LocalDate.parse(dateRange[1], dateFormat)
        val color = generateRandomColor() // 랜덤 색상 생성
        val eventId = cartItem.cartId

        var currentDate = startDate
        while (!currentDate.isAfter(endDate)) {
            selectedEvents.computeIfAbsent(currentDate) { mutableListOf() }
                .add(EventInfo(color, eventId))
            currentDate = currentDate.plusWeeks(1)
        }
    }

    private fun generateRandomColor(): Int {
        val random = Random(System.currentTimeMillis())
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun removeEventFromCalendar(cartItem: CartResponse) {
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateRange = cartItem.period.split("~")
        val startDate = LocalDate.parse(dateRange[0], dateFormat)
        val endDate = LocalDate.parse(dateRange[1], dateFormat)
        val eventId = cartItem.cartId

        var currentDate = startDate
        while (!currentDate.isAfter(endDate)) {
            selectedEvents[currentDate]?.let {
                it.removeIf { eventInfo -> eventInfo.id == eventId }
                if (it.isEmpty()) {
                    selectedEvents.remove(currentDate)
                }
            }
            currentDate = currentDate.plusWeeks(1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDragHandle() {
        val dragHandle = binding.dragHandle
        val cartRecyclerView = binding.cartRecyclerView

        dragHandle.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.tag = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val initialY = v.tag as Float
                    val newY = event.rawY
                    val deltaY = newY - initialY
                    val newHeight = (cartRecyclerView.height - deltaY).toInt()

                    if (newHeight in dpToPx(0)..dpToPx(600)) {
                        val params = cartRecyclerView.layoutParams
                        params.height = newHeight
                        cartRecyclerView.layoutParams = params
                    }

                    v.tag = newY
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val currentHeight = cartRecyclerView.height
                    val fixedHeights = listOf(dpToPx(100) ,dpToPx(250), dpToPx(400), dpToPx(500))
                    val closestHeight = fixedHeights.minByOrNull { Math.abs(it - currentHeight) } ?: dpToPx(300)
                    val params = cartRecyclerView.layoutParams
                    params.height = closestHeight
                    cartRecyclerView.layoutParams = params

                    true
                }
                else -> false
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

}

class DayViewContainer(view: View) : ViewContainer(view) {
    lateinit var day: CalendarDay
    val dayTextView = view.findViewById<TextView>(R.id.calendarDayText)
    val colorBarContainer = view.findViewById<LinearLayout>(R.id.colorBarContainer)
    var eventIds: MutableList<String> = mutableListOf()
}

data class EventInfo(
    val color: Int,
    val id: String
)
