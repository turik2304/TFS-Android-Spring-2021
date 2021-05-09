package com.turik2304.coursework.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import androidx.core.view.children
import com.turik2304.coursework.R
import com.turik2304.coursework.extensions.dpToPx
import kotlin.math.roundToInt

class FlexboxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    var imageViewAddsEmojis: ImageView
    private val gap = 7f.dpToPx().toInt()

    init {
        setWillNotDraw(true)
        imageViewAddsEmojis = ImageView(context)
        addView(imageViewAddsEmojis)
        imageViewAddsEmojis.setImageResource(R.drawable.ic_add_emoji)
        imageViewAddsEmojis.visibility = INVISIBLE
    }

    fun checkZeroesCounters() {
        (children - imageViewAddsEmojis).forEachIndexed { index, emojiView ->
            if ((emojiView as EmojiView).selectCounter == 0) {
                removeViewAt(index)
            }
        }
        if (childCount == 1) imageViewAddsEmojis.visibility = INVISIBLE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        var topOfChildren = 0
        var currentWidth = 0
        var heightOfLayout = 0
        var widthOfLayout = 0
        val maxNumberOfChildsInRow = 5
        var counterOfChildsInRow = 0

        children.forEach {
            measureChildWithMargins(it, widthMeasureSpec, 0, heightMeasureSpec, 0)
        }
        removeView(imageViewAddsEmojis)
        val maxHeightOfChild = (children + imageViewAddsEmojis).maxOf { children ->
            children.measuredHeight
        }

        (children + imageViewAddsEmojis).forEach { children ->
            if ((currentWidth < widthSpecSize) &&
                (currentWidth + children.measuredWidth) < widthSpecSize &&
                counterOfChildsInRow < maxNumberOfChildsInRow
            ) {
                placeChild(children, currentWidth, topOfChildren)
                currentWidth += children.measuredWidth + gap
                widthOfLayout = maxOf(currentWidth, widthOfLayout)
                counterOfChildsInRow++
            } else {
                currentWidth = 0
                counterOfChildsInRow = 1
                topOfChildren += maxHeightOfChild + gap
                heightOfLayout = topOfChildren + maxHeightOfChild
                placeChild(children, currentWidth, topOfChildren)
                currentWidth += children.measuredWidth + gap
                widthOfLayout = maxOf(currentWidth, widthOfLayout)
            }
        }

        setMeasuredDimension(
            resolveSize(widthOfLayout - gap, widthMeasureSpec),
            resolveSize(
                if (heightOfLayout == 0) maxHeightOfChild else heightOfLayout,
                heightMeasureSpec
            )
        )

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        removeView(imageViewAddsEmojis)
        (children + imageViewAddsEmojis).forEach { children ->
            children.layout(children.left, children.top, children.right, children.bottom)
        }
        addView(imageViewAddsEmojis)
    }

    override fun generateDefaultLayoutParams(): LayoutParams =
        MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)

    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams = MarginLayoutParams(p)

    private fun placeChild(children: View, currentWidth: Int, topOfChildren: Int) {
        children.left = currentWidth
        children.right = children.left + children.measuredWidth
        children.top = topOfChildren
        children.bottom = children.top + children.measuredHeight
    }

}
