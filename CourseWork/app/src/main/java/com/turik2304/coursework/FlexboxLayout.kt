package com.turik2304.coursework

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import androidx.core.view.children

class FlexboxLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private var imageViewAddsEmojis: ImageView
    private val widthOfImageView = 100
    private val heightOfImageView = 60
    private val gap = 10
    private var widthOfLayout = 0
        set(value) {
            if (value > field) {
                field = value
            }
        }

    init {
        setWillNotDraw(true)
        imageViewAddsEmojis = ImageView(context)
        addView(imageViewAddsEmojis)
        imageViewAddsEmojis.setImageResource(R.drawable.image_view_add_emoji)
//        imageViewAddsEmojis.setBackgroundColor(resources.getColor(R.color.emoji_view_night_color))
        imageViewAddsEmojis.layoutParams.height = heightOfImageView
        imageViewAddsEmojis.layoutParams.width = widthOfImageView
        imageViewAddsEmojis.setOnClickListener {
            val em = EmojiView(context)
            em.selectCounter = (0..1000).random()
            em.emojiCode = (128512..128533).random()
            em.layoutParams = MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            addView(em)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        var topOfChildren = 0
        var currentWidth = 0
        var heightOfLayout = 0

        children.plus(imageViewAddsEmojis).forEach {
            measureChildWithMargins(it, widthMeasureSpec, 0, heightMeasureSpec, 0)
        }
        removeView(imageViewAddsEmojis)

        val maxHeightOfChild = children.plus(imageViewAddsEmojis).maxOf { children ->
            children.measuredHeight
        }

        children.plus(imageViewAddsEmojis).forEach { children ->
            if ((currentWidth < widthSpecSize) &&
                    (currentWidth + children.measuredWidth) < widthSpecSize) {
                placeChild(children, currentWidth, topOfChildren)
                currentWidth += children.measuredWidth + gap
                widthOfLayout = currentWidth
            } else {
                currentWidth = 0
                topOfChildren += maxHeightOfChild + gap
                heightOfLayout = topOfChildren + maxHeightOfChild
                placeChild(children, currentWidth, topOfChildren)
                currentWidth += children.measuredWidth + gap
                widthOfLayout = currentWidth
            }
        }

        setMeasuredDimension(
                resolveSize(widthOfLayout, widthMeasureSpec),
                resolveSize(if (heightOfLayout == 0) maxHeightOfChild else heightOfLayout, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        children.plus(imageViewAddsEmojis).forEach { children ->
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

