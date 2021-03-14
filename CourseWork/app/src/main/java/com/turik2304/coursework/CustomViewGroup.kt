package com.turik2304.coursework

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

class CustomViewGroup @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private val avatarImageView: ImageView
    private val userName: TextView
    private val message: TextView
    private val flexboxLayout: FlexboxLayout

    private val avatarRect = Rect()
    private val userNameRect = Rect()
    private val messageRect = Rect()
    private val flexboxLayoutRect = Rect()

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_view_group, this, true)
        avatarImageView = findViewById(R.id.avatarView)
        userName = findViewById(R.id.userName)
        message = findViewById(R.id.message)
        flexboxLayout = findViewById(R.id.flexBoxLayout)
        avatarImageView.clipToOutline = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val avatarSize: Pair<Int, Int>
        val userNameSize: Pair<Int, Int>
        val messageSize: Pair<Int, Int>
        val flexboxLayoutSize: Pair<Int, Int>

        val avatarLayoutParams = avatarImageView.layoutParams as MarginLayoutParams
        val userNameLayoutParams = userName.layoutParams as MarginLayoutParams
        val messageLayoutParams = message.layoutParams as MarginLayoutParams
        val flexboxLayoutParams = flexboxLayout.layoutParams as MarginLayoutParams

        avatarSize = avatarImageView.getSizeAfterMeasuringWithMargins(
                widthMeasureSpec,
                widthUsed = 0,
                heightMeasureSpec,
                heightUsed = 0,
                marginLayoutParams = avatarLayoutParams
        )

        userNameSize = userName.getSizeAfterMeasuringWithMargins(
                widthMeasureSpec,
                widthUsed = avatarSize.width(),
                heightMeasureSpec,
                0,
                marginLayoutParams = userNameLayoutParams,
        )

        messageSize = message.getSizeAfterMeasuringWithMargins(
                widthMeasureSpec,
                widthUsed = avatarSize.width(),
                heightMeasureSpec,
                heightUsed = userNameSize.second,
                marginLayoutParams = messageLayoutParams,
        )

        flexboxLayoutSize = flexboxLayout.getSizeAfterMeasuringWithMargins(
                widthMeasureSpec,
                widthUsed = avatarSize.width(),
                heightMeasureSpec,
                heightUsed = userNameSize.second + messageSize.second,
                marginLayoutParams = flexboxLayoutParams
        )

        setMeasuredDimension(
                resolveSize(avatarSize.width() + maxOf(userNameSize.width(), messageSize.width(), flexboxLayoutSize.width()),
                        widthMeasureSpec),
                resolveSize(maxOf(avatarSize.height(), userNameSize.height() + messageSize.height() + flexboxLayoutSize.height()),
                        heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        avatarImageView.layout(avatarRect, null, null, null)
        userName.layout(userNameRect, avatarImageView, avatarRect, null)
        message.layout(messageRect, avatarImageView, avatarRect, userNameRect)
        flexboxLayout.layout(flexboxLayoutRect, avatarImageView, avatarRect, messageRect)
    }

    override fun generateDefaultLayoutParams(): LayoutParams = MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)

    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams = MarginLayoutParams(p)

    private fun View.getSizeAfterMeasuringWithMargins(
            widthMeasureSpec: Int,
            widthUsed: Int,
            heightMeasureSpec: Int,
            heightUsed: Int,
            marginLayoutParams: MarginLayoutParams,
    ): Pair<Int, Int> {
        measureChildWithMargins(this, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed)
        val width = this.measuredWidth + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin
        val height = this.measuredHeight + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin
        return Pair(width, height)
    }

    private fun Pair<Int, Int>.width(): Int {
        return this.first
    }

    private fun Pair<Int, Int>.height(): Int {
        return this.second
    }

    private fun View.layout(
            currentChildRect: Rect,
            childOnLeft: View?,
            childOnLeftRect: Rect?,
            childOnTopRect: Rect?,
    ) {
        currentChildRect.left = (childOnLeftRect?.right ?: 0) + (childOnLeft?.marginRight
                ?: 0) + this.marginLeft
        currentChildRect.top = (childOnTopRect?.bottom ?: 0) + this.marginTop
        currentChildRect.right = currentChildRect.left + this.measuredWidth
        currentChildRect.bottom = currentChildRect.top + this.measuredHeight + this.marginBottom
        this.layout(currentChildRect.left, currentChildRect.top, currentChildRect.right, currentChildRect.bottom)
    }
}