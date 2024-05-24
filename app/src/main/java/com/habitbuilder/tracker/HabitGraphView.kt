package com.habitbuilder.tracker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.habitbuilder.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

class HabitGraphView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paddingBetweenCircles = 5
    private val weekOfDayTextSize = 24
    private val dateTextSize = 16
    private val radiusInDp = 20
    private var year = 0
    private var month = 0
    private var radius = 0
    private var currentDayOfMonth = 0
    private var totalCircles = 0
    private var firstCircleOffset = 0
    private var circlePerRow = 0
    private var scheduledCompletedDatePaint: DailyRecordPaint
    private var scheduledTodayDatePaint: DailyRecordPaint
    private var scheduledFutureDatePaint: DailyRecordPaint
    private var scheduledPastDueDatePaint: DailyRecordPaint
    private var unscheduledDatePaint: DailyRecordPaint
    private var weekOfDayPaint: Paint
    private var textBounds: Rect
    private lateinit var scheduledDays: ArrayList<Boolean>
    private lateinit var completedDays: ArrayList<Boolean>

    init {
        val numTextSize = convertDpToPixel(context, dateTextSize)
        val categoryPaint = initPaint(ContextCompat.getColor(context, R.color.tagNavy), numTextSize)
        val markedTextPaint =
            initPaint(ContextCompat.getColor(context, R.color.markerWhite), numTextSize)
        val unmarkedBackgroundPaint =
            initPaint(ContextCompat.getColor(context, R.color.markerLightGrey), numTextSize)
        val unmarkedTextPaint =
            initPaint(ContextCompat.getColor(context, R.color.markerGrey), numTextSize)
        val pastBackgroundPaint =
            initPaint(ContextCompat.getColor(context, R.color.markerGrey), numTextSize)
        val pastTextPaint =
            initPaint(ContextCompat.getColor(context, R.color.markerLightGrey), numTextSize)
        val unscheduledTextPaint =
            initPaint(ContextCompat.getColor(context, R.color.markerGrey), numTextSize)
        weekOfDayPaint = initPaint(
            ContextCompat.getColor(context, R.color.colorOnSurface),
            convertDpToPixel(context, weekOfDayTextSize)
        )
        scheduledCompletedDatePaint = DailyRecordPaint(markedTextPaint, categoryPaint)
        scheduledTodayDatePaint = DailyRecordPaint(categoryPaint, unmarkedBackgroundPaint)
        scheduledFutureDatePaint = DailyRecordPaint(unmarkedTextPaint, unmarkedBackgroundPaint)
        scheduledPastDueDatePaint = DailyRecordPaint(pastTextPaint, pastBackgroundPaint)
        unscheduledDatePaint = DailyRecordPaint(unscheduledTextPaint)
        textBounds = Rect()
        circlePerRow = DayOfWeek.entries.size
        totalCircles = YearMonth.now().lengthOfMonth()
        val offsetDay = YearMonth.now().atDay(1).dayOfWeek
        firstCircleOffset = if (offsetDay == DayOfWeek.SUNDAY) 0 else offsetDay.value
        currentDayOfMonth = LocalDate.now().dayOfMonth
        radius = convertDpToPixel(context, radiusInDp).toInt()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val unitWidth: Int = 2 * radius + paddingBetweenCircles
        val unitHeight: Int = 2 * radius + paddingBetweenCircles
        val startX: Int = radius
        val startY: Int = radius
        var cx = startX
        var cy = startY
        var text = DayOfWeek.SUNDAY.getDisplayName(TextStyle.NARROW_STANDALONE, Locale.getDefault())
        weekOfDayPaint.getTextBounds(text, 0, text.length, textBounds)
        canvas.drawText(
            text,
            cx - textBounds.width() / 2f - textBounds.left,
            cy + textBounds.height() / 2f - textBounds.bottom,
            weekOfDayPaint
        )
        cx += unitWidth
        for (i in DayOfWeek.MONDAY.value..DayOfWeek.SATURDAY.value) {
            val day = DayOfWeek.of(i)
            text = day.getDisplayName(TextStyle.NARROW_STANDALONE, Locale.getDefault())
            weekOfDayPaint.getTextBounds(text, 0, text.length, textBounds)
            canvas.drawText(
                text,
                cx - textBounds.width() / 2f - textBounds.left,
                cy + textBounds.height() / 2f - textBounds.bottom,
                weekOfDayPaint
            )
            cx += unitWidth
        }
        cx = startX + firstCircleOffset % circlePerRow * unitWidth
        cy = startY + (floor(firstCircleOffset / circlePerRow.toDouble())
            .toInt() + 1) * unitHeight
        for (i in 1..totalCircles) {
            val backgroundPaint: Paint? = getDailyRecordPaint(i).backgroundPaint
            val numPaint: Paint = getDailyRecordPaint(i).textPaint
            if (backgroundPaint != null) {
                canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius.toFloat(), backgroundPaint)
            }
            text = i.toString()
            numPaint.getTextBounds(text, 0, text.length, textBounds)
            canvas.drawText(
                text,
                cx - textBounds.width() / 2f - textBounds.left,
                cy + textBounds.height() / 2f - textBounds.bottom,
                numPaint
            )
            cx += unitWidth
            if ((i + firstCircleOffset) % circlePerRow == 0) {
                cx = startX
                cy += unitHeight
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth: Int =
            2 * radius * circlePerRow + paddingBetweenCircles * (circlePerRow - 1)
        val rowCount =
            ceil((firstCircleOffset + totalCircles) / circlePerRow.toDouble()).toInt() + 1
        val desiredHeight: Int = 2 * radius * rowCount + paddingBetweenCircles * (rowCount - 1)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val width: Int = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
            else -> desiredWidth
        }
        val height: Int = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
            else -> desiredHeight
        }
        setMeasuredDimension(width, height)
    }

    fun setGraphData(
        year: Int,
        month: Int,
        @ColorInt categoryColor: Int,
        scheduledDays: ArrayList<Boolean>,
        completedDays: ArrayList<Boolean>
    ) {
        this.year = year
        this.month = month
        this.scheduledDays = scheduledDays
        this.completedDays = completedDays
        totalCircles = completedDays.size
        val paint: Paint? = scheduledCompletedDatePaint.backgroundPaint
        paint?.let {
            it.color = categoryColor
            scheduledCompletedDatePaint.backgroundPaint = it
            scheduledTodayDatePaint.textPaint = it
        }
        invalidate()
    }

    private fun convertDpToPixel(context: Context, dp: Int): Float {
        val screenPixelDensity = context.resources.displayMetrics.density
        return dp * screenPixelDensity
    }

    private fun initPaint(@ColorInt color: Int, textSize: Float): Paint {
        val paint = Paint()
        paint.color = color
        paint.textSize = textSize
        paint.textAlign = Paint.Align.LEFT
        return paint
    }

    private fun getDailyRecordPaint(dayOfMonth: Int): DailyRecordPaint {
        currentDayOfMonth = LocalDate.now().dayOfMonth
        val date: LocalDate = LocalDate.of(year, month, dayOfMonth)
        var dailyRecordPaint: DailyRecordPaint = unscheduledDatePaint
        if (scheduledDays[date.dayOfWeek.value - 1]) {
            dailyRecordPaint = if (completedDays[dayOfMonth - 1]) scheduledCompletedDatePaint else
                if (dayOfMonth < currentDayOfMonth) {
                    scheduledPastDueDatePaint
                } else if (dayOfMonth == currentDayOfMonth) {
                    scheduledTodayDatePaint
                } else {
                    scheduledFutureDatePaint
                }
        }
        return dailyRecordPaint
    }

    data class DailyRecordPaint(var textPaint: Paint, var backgroundPaint: Paint? = null)

}