package com.turik2304.coursework

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import androidx.annotation.Px
import androidx.core.view.children
import kotlin.math.roundToInt

class FlexboxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private var imageViewAddsEmojis: ImageView
    private val gap = dpToPx(7f)

    init {
        setWillNotDraw(true)

        //get default size of EmojiView
        val emojiViewForSizingImageView = EmojiView(context)
        addView(emojiViewForSizingImageView)
        measureChild(emojiViewForSizingImageView, MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val height = emojiViewForSizingImageView.measuredHeight
        val width = emojiViewForSizingImageView.measuredWidth
        removeView(emojiViewForSizingImageView)

        imageViewAddsEmojis = ImageView(context)
        addView(imageViewAddsEmojis)
        imageViewAddsEmojis.setImageResource(R.drawable.image_view_add_emoji)
        imageViewAddsEmojis.setBackgroundResource(R.drawable.round_corners)
        imageViewAddsEmojis.layoutParams.height = height
        imageViewAddsEmojis.layoutParams.width = width
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
        var widthOfLayout = 0

        children.forEach {
            measureChildWithMargins(it, widthMeasureSpec, 0, heightMeasureSpec, 0)
        }
        removeView(imageViewAddsEmojis)
        val maxHeightOfChild = (children + imageViewAddsEmojis).maxOf { children ->
            children.measuredHeight
        }

        (children + imageViewAddsEmojis).forEach { children ->
            if ((currentWidth < widthSpecSize) &&
                (currentWidth + children.measuredWidth) < widthSpecSize
            ) {
                placeChild(children, currentWidth, topOfChildren)
                currentWidth += children.measuredWidth + gap
                widthOfLayout = if (currentWidth > widthOfLayout) currentWidth else widthOfLayout
            } else {
                currentWidth = 0
                topOfChildren += maxHeightOfChild + gap
                heightOfLayout = topOfChildren + maxHeightOfChild
                placeChild(children, currentWidth, topOfChildren)
                currentWidth += children.measuredWidth + gap
                widthOfLayout = if (currentWidth > widthOfLayout) currentWidth else widthOfLayout
            }
        }

        setMeasuredDimension(
            resolveSize(widthOfLayout, widthMeasureSpec),
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

    @Px
    private fun dpToPx(dp: Float): Int {
        return (dp * resources.displayMetrics.density).roundToInt()
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

