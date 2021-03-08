package com.turik2304.coursework

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.children


class FlexboxLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private var imageView: ImageView
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
        imageView = ImageView(context)
        addView(imageView)
        imageView.setImageResource(R.drawable.image_view_add_emoji)
//        imageView.setBackgroundColor(resources.getColor(R.color.image_view_add_emoji_day))
        val imageViewLayoutParams = FrameLayout.LayoutParams(widthOfImageView, heightOfImageView)
        imageViewLayoutParams.gravity = Gravity.CENTER
        imageView.layoutParams = imageViewLayoutParams
        imageView.setOnClickListener {
            val em = EmojiView(context)
            em.selectCounter = (0..1000).random()
            em.layoutParams = MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            addView(em)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        var topOfChildren = 0
        var currentWidth = 0
        var heightOfLayout = 0

        children.plus(imageView).forEach {
            measureChildWithMargins(it, widthMeasureSpec, 0, heightMeasureSpec, 0)
        }
        removeView(imageView)

        val maxHeightOfChild = children.plus(imageView).maxOf { children ->
            children.measuredHeight
        }

        children.plus(imageView).forEach { children ->
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
        children.plus(imageView).forEach { children ->
            children.layout(children.left, children.top, children.right, children.bottom)
        }
        addView(imageView)

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

