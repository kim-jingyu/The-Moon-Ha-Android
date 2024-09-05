import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.databinding.FragmentCartContentBinding
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class CartContentFragment : Fragment() {

    private var _binding: FragmentCartContentBinding? = null
    private val binding get() = _binding!!

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

        val calendarView = binding.calendarView

        val lessons = parseLessonData() // Your method to parse lesson data into a map of LocalDate -> Event Title


        calendarView.apply {
            val currentMonth = YearMonth.now()
            val firstMonth = currentMonth.minusMonths(240)
            val lastMonth = currentMonth.plusMonths(240)
            val firstDayOfWeek = DayOfWeek.SUNDAY

            setup(firstMonth, lastMonth, firstDayOfWeek)
            scrollToMonth(currentMonth)
        }

        val titlesContainer = binding.titlesContainer
        titlesContainer.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                val dayOfWeek = DayOfWeek.of((index + 1) % 7 + 1)  // Adjust index to match DayOfWeek
                val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                textView.text = title
            }

        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view)
            }

            override fun bind(container: DayViewContainer, day: CalendarDay) {
                val localDate = day.date
                container.dayTextView.text = localDate.dayOfMonth.toString()

                val lessonTitle = lessons[localDate]
                if (lessonTitle != null) {
                    container.eventTextView.text = lessonTitle
                    container.eventTextView.visibility = View.VISIBLE
                } else {
                    container.eventTextView.visibility = View.GONE
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseLessonData(): Map<LocalDate, String> {
        val lessonData = listOf(
            mapOf(
                "branchName" to "무역센터점",
                "cartId" to "4",
                "lessonTitle" to "test",
                "period" to "2024-08-24~2024-09-10",
                "lessonTime" to "2024-08-24(수) 14:00~15:00",
                "tutorName" to "son",
                "target" to "1",
                "cost" to 123,
                "onlineCost" to 100,
                "onlineYn" to true
            )
        )

        val lessons = mutableMapOf<LocalDate, String>()

        for (lesson in lessonData) {
            val period = lesson["period"] as String
            val lessonTitle = lesson["lessonTitle"] as String

            val dateRange = period.split("~")
            val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val startDate = LocalDate.parse(dateRange[0], dateFormat)
            val endDate = LocalDate.parse(dateRange[1], dateFormat)

            var currentDate = startDate
            while (!currentDate.isAfter(endDate)) {
                lessons[currentDate] = lessonTitle
                currentDate = currentDate.plusWeeks(1)
            }
        }
        return lessons
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class DayViewContainer(view: View) : ViewContainer(view){
    lateinit var day: CalendarDay
    val dayTextView = view.findViewById<TextView>(R.id.calendarDayText)
    val eventTextView: TextView = view.findViewById(R.id.calendarDayEvent)

    init {
        view.setOnClickListener {
            Log.d("CollapisbleActivity", day.toString())
        }
    }
}
