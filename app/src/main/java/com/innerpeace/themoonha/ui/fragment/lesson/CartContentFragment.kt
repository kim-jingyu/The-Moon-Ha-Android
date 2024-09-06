import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.adapter.CartItemAdapter
import com.innerpeace.themoonha.data.model.lesson.CartResponse
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LessonService
import com.innerpeace.themoonha.data.repository.LessonRepository
import com.innerpeace.themoonha.databinding.FragmentCartContentBinding
import com.innerpeace.themoonha.viewModel.LessonViewModel
import com.innerpeace.themoonha.viewModel.factory.LessonViewModelFactory
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
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
    private val selectedEvents = mutableMapOf<LocalDate, MutableList<EventInfo>>() // 날짜별로 이벤트 정보 저장

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalendar()
        setupLessonCart()

        viewModel.getLessonCart()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupCalendar() {
        val calendarView = binding.calendarView
        updateMonthHeader(currentMonth)

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
                                setMargins(0, 0, 0, 3) // 아래쪽 마진
                            }
                            setBackgroundColor(eventInfo.color)
                        }
                        container.colorBarContainer.addView(colorBar)
                        container.eventIds.add(eventInfo.id) // 이벤트 ID를 추가
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
        val adapter = CartItemAdapter { cartItem, isChecked ->
            if (isChecked) {
                addEventToCalendar(cartItem)
            } else {
                removeEventFromCalendar(cartItem) // 각 이벤트 ID를 사용하여 제거
            }
            updateCartItemColors()
            binding.calendarView.post {
                binding.calendarView.notifyCalendarChanged()
            }
        }
        cartRecyclerView.adapter = adapter

        viewModel.lessonCart.observe(viewLifecycleOwner, Observer { cartList ->
            adapter.submitList(cartList)
            updateCartItemColors()
        })
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
}

class DayViewContainer(view: View) : ViewContainer(view) {
    lateinit var day: CalendarDay
    val dayTextView = view.findViewById<TextView>(R.id.calendarDayText)
    val colorBarContainer = view.findViewById<LinearLayout>(R.id.colorBarContainer)
    var eventIds: MutableList<String> = mutableListOf() // 이벤트 ID를 저장하는 리스트
}

// 이벤트 정보를 저장할 데이터 클래스
data class EventInfo(
    val color: Int,
    val id: String
)
