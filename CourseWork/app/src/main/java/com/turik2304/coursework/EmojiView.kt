package com.turik2304.coursework

import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Px
import androidx.core.view.setPadding
import com.turik2304.coursework.network.ServerApi
import java.lang.IllegalArgumentException
import kotlin.math.roundToInt


class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {
    var emojiCode = DEFAULT_EMOJI_CODE
        set(value) {
            if (field != value) {
                field = value
                updateViewContent()
            }
        }

    var selectCounter = DEFAULT_SELECT_COUNTER
        set(value) {
            if (field != value) {
                field = value
                updateViewContent()
            }
        }
    val listOfUsersWhoClicked = mutableListOf<String>()

    private val superellipseDayColor =
        resources.getColor(R.color.design_default_color_background, context.theme)
    private val superellipseNightColor =
        resources.getColor(R.color.emoji_view_night_color, context.theme)
    private val superellipseDayColorSelected =
        resources.getColor(R.color.emoji_view_day_selected_color, context.theme)
    private val superellipseNightColorSelected =
        resources.getColor(R.color.emoji_view_night_selected_color, context.theme)

    private val currentNightMode = resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

    private val superellipsePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = if (currentNightMode) {
            superellipseNightColor
        } else superellipseDayColor
        style = Paint.Style.FILL
    }

    private val superellipseSelectedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = if (currentNightMode) {
            superellipseNightColorSelected
        } else superellipseDayColorSelected
        style = Paint.Style.FILL
    }

    private val superellipseBoundaryPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.emoji_view_night_selected_color, context.theme)
        style = Paint.Style.STROKE
        strokeWidth = dpToPx(2f).toFloat()
    }

    private val contentPaint = Paint().apply {
        color = if (currentNightMode) {
            resources.getColor(R.color.emoji_view_content_color, context.theme)
        } else Color.BLACK
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    private var textSize: Int
        get() = contentPaint.textSize.toInt()
        set(value) {
            if (contentPaint.textSize.toInt() != value) {
                contentPaint.textSize = value.toFloat()
                requestLayout()
            }
        }

    private var viewContent: String = getFormattedContent(emojiCode, selectCounter)
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    private var width: Float = 0F
    private var height: Float = 0F
    private val contentBoundsRect = Rect()
    private val boundariesRect = RectF()
    private var coordinateXOfContent: Float = 0F
    private var coordinateYOfContent: Float = 0F
    private val padding: Int = textSize
    private val radius: Float = textSize.toFloat()
    private val strokeWidth = superellipseBoundaryPaint.strokeWidth.toInt()

    init {
        isClickable = true
        context.obtainStyledAttributes(attrs, R.styleable.EmojiView).apply {
            textSize = getDimensionPixelSize(
                R.styleable.EmojiView_ev_text_size, spToPx(
                    DEFAULT_TEXT_SIZE_SP
                )
            )
            emojiCode = getInteger(R.styleable.EmojiView_emojiCode, DEFAULT_EMOJI_CODE)
            selectCounter = getInteger(R.styleable.EmojiView_selectCounter, selectCounter)
            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        contentPaint.getTextBounds(viewContent, 0, viewContent.length, contentBoundsRect)
        val contentWight = contentBoundsRect.width()
        val contentHeight = contentBoundsRect.height()
        setPadding(padding)
        val fullWidthContent = contentWight + paddingStart + paddingEnd
        val fullHeightContent = contentHeight + paddingTop + paddingBottom
        width = resolveSize(fullWidthContent, widthMeasureSpec).toFloat()
        coordinateXOfContent = width / 2

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)

        height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSpecSize.toFloat()
            MeasureSpec.AT_MOST -> fullHeightContent.toFloat()
            else -> fullHeightContent.toFloat()
        }

        coordinateYOfContent = when (heightMode) {
            MeasureSpec.EXACTLY -> height / 2 + paddingTop
            MeasureSpec.AT_MOST -> contentPaint.fontMetrics
                .run { height - paddingBottom - bottom }
            else -> contentPaint.fontMetrics
                .run { height - paddingBottom - bottom }
        }
        setMeasuredDimension(width.toInt(), height.toInt())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        boundariesRect.set(0f, 0f, w.toFloat(), h.toFloat())
        setBordersOffsetToPreventStrokeClipping(boundariesRect, strokeWidth)
    }

    override fun onDraw(canvas: Canvas) {
        val canvasCount = canvas.save()
        canvas.drawRoundRect(boundariesRect, radius, radius, superellipseBoundaryPaint)
        if (isSelected) {
            canvas.drawRoundRect(boundariesRect, radius, radius, superellipseSelectedPaint)
        } else {
            canvas.drawRoundRect(boundariesRect, radius, radius, superellipsePaint)
        }
        canvas.drawText(viewContent, coordinateXOfContent, coordinateYOfContent, contentPaint)
        canvas.restoreToCount(canvasCount)
    }

    override fun performClick(): Boolean {
        val userClicked = listOfUsersWhoClicked.contains(MyUserId.MY_USER_ID)
        val directParent = parent as FlexboxLayout
        val mainParent = directParent.parent as MessageViewGroup
        val uidOfMessage = mainParent.uid

        if (!isSelected && !userClicked) {
            isSelected = !isSelected
            selectCounter++
            listOfUsersWhoClicked.add(MyUserId.MY_USER_ID)
            val handlerIncreasingCounter = handler@{ reactionsOfClickedMessage: MutableList<ServerApi.Reaction> ->
                val updatedReaction = ServerApi.Reaction(emojiCode, selectCounter, listOfUsersWhoClicked)
                val currentReaction = ServerApi.Reaction(emojiCode, selectCounter - 1, (listOfUsersWhoClicked - MyUserId.MY_USER_ID))
                reactionsOfClickedMessage.forEachIndexed { index, reaction ->
                    if (reaction.emojiCode == currentReaction.emojiCode &&
                        reaction.counter == currentReaction.counter &&
                        reaction.usersWhoClicked.toSet() == currentReaction.usersWhoClicked.toSet()) {
                        reactionsOfClickedMessage[index] = updatedReaction
                    }
                }
                return@handler true
            }
            ChatActivity.updateReactionsOfMessages(uidOfMessage, handlerIncreasingCounter)
        } else {
            isSelected = !isSelected
            selectCounter--
            listOfUsersWhoClicked.remove(MyUserId.MY_USER_ID)
            directParent.checkZeroesCounters()
            val handlerDecreasingCounter = handler@{ reactionsOfClickedMessage: MutableList<ServerApi.Reaction> ->
                val updatedReaction = ServerApi.Reaction(emojiCode, selectCounter, listOfUsersWhoClicked)
                val currentReaction = ServerApi.Reaction(emojiCode, selectCounter + 1, (listOfUsersWhoClicked + MyUserId.MY_USER_ID))
                reactionsOfClickedMessage.forEachIndexed { index, reaction ->
                    if (reaction.emojiCode == currentReaction.emojiCode &&
                            reaction.counter == currentReaction.counter &&
                            reaction.usersWhoClicked.toSet() == currentReaction.usersWhoClicked.toSet()) {
                        reactionsOfClickedMessage[index] = updatedReaction
                    }
                }
                return@handler true
            }
            ChatActivity.updateReactionsOfMessages(uidOfMessage, handlerDecreasingCounter)
        }
        return super.performClick()
    }

    @Px
    private fun spToPx(sp: Float): Int {
        return (sp * resources.displayMetrics.scaledDensity).roundToInt()
    }

    @Px
    private fun dpToPx(dp: Float): Int {
        return (dp * resources.displayMetrics.density).roundToInt()
    }

    private fun setBordersOffsetToPreventStrokeClipping(rect: RectF, value: Int) {
        rect.top += value / 2
        rect.bottom -= value / 2
        rect.right -= value / 2
        rect.left += value / 2
    }

    private fun updateViewContent() {
        viewContent = getFormattedContent(emojiCode, selectCounter)
    }

    private fun getFormattedContent(emojiCode: Int, counter: Int): String {
        val emoji: String = try {
            String(Character.toChars(emojiCode))
        } catch (e: IllegalArgumentException) {
            String(Character.toChars(DEFAULT_EMOJI_CODE))
        }
        return "$emoji $counter"
    }

    companion object {
        private const val DEFAULT_TEXT_SIZE_SP = 14F
        private const val DEFAULT_EMOJI_CODE: Int = 0x1F60A
        private const val DEFAULT_SELECT_COUNTER: Int = 0
    }

}

