package com.turik2304.coursework

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Px
import androidx.core.view.setPadding
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
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
    val listOfUsersWhoClicked = mutableListOf<Int>()

    private val superellipseColor =
        resources.getColor(R.color.gray_not_selected_background, context.theme)
    private val superellipseColorSelected =
        resources.getColor(R.color.gray_selected_background, context.theme)
    private val contentColor =
        resources.getColor(R.color.gray_send_message, context.theme)

    private val superellipsePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = superellipseColor
        style = Paint.Style.FILL
    }

    private val superellipseSelectedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = superellipseColorSelected
        style = Paint.Style.FILL
    }

    private val contentPaint = Paint().apply {
        color = contentColor
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
    private val padding: Int = dpToPx(6f).toInt()
    private val radius: Float = dpToPx(10f)

    init {
        isClickable = true
        context.obtainStyledAttributes(attrs, R.styleable.EmojiView).apply {
            textSize = spToPx(14f)
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
    }

    override fun onDraw(canvas: Canvas) {
        val canvasCount = canvas.save()
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
        val parentOfShimmer = ((mainParent.parent as View).parent as View)
        val uidOfMessage = mainParent.uid
        val nameAndZulipEmojiCode = EmojiEnum.getNameByCodePoint(emojiCode)
        val name = nameAndZulipEmojiCode.first
        val zulipEmojiCode = nameAndZulipEmojiCode.second
        val chatShimmer = parentOfShimmer.findViewById<ShimmerFrameLayout>(R.id.chatShimmer)
        chatShimmer.showShimmer(true)
        if (!isSelected && !userClicked) {
            isSelected = !isSelected
            selectCounter++
            listOfUsersWhoClicked.add(MyUserId.MY_USER_ID)
            ChatActivity.api.sendReaction(uidOfMessage, zulipEmojiCode, name)
                .subscribe(
                    {
                        ChatActivity.updateMessages(context, chatShimmer)
                    },
                    { onError ->
                        Error.showError(
                            context,
                            onError
                        )
                        chatShimmer.stopAndHideShimmer()
                    })
        } else {
            isSelected = !isSelected
            selectCounter--
            listOfUsersWhoClicked.remove(MyUserId.MY_USER_ID)
            directParent.checkZeroesCounters()
            ChatActivity.api.removeReaction(uidOfMessage, zulipEmojiCode, name)
                .subscribe(
                    {
                        ChatActivity.updateMessages(context, chatShimmer)
                    },
                    { onError ->
                        Error.showError(
                            context,
                            onError
                        )
                        chatShimmer.stopAndHideShimmer()
                    })
        }
        return super.performClick()
    }

    @Px
    private fun spToPx(sp: Float): Int {
        return (sp * resources.displayMetrics.scaledDensity).roundToInt()
    }

    @Px
    private fun dpToPx(dp: Float): Float {
        return (dp * resources.displayMetrics.density)
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
        private const val DEFAULT_EMOJI_CODE: Int = 0x1F60A
        private const val DEFAULT_SELECT_COUNTER: Int = 0
    }

}

