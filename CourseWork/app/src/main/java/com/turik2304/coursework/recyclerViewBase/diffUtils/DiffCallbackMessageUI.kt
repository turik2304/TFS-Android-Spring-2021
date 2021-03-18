package com.turik2304.coursework.recyclerViewBase.diffUtils

import android.util.Log
import android.view.View
import com.turik2304.coursework.R
import com.turik2304.coursework.recyclerViewBase.BaseDiffCallback
import com.turik2304.coursework.recyclerViewBase.ViewTyped
import com.turik2304.coursework.recyclerViewBase.items.DateSeparatorUI
import com.turik2304.coursework.recyclerViewBase.items.InMessageUI
import com.turik2304.coursework.recyclerViewBase.items.OutMessageUI

class DiffCallbackMessageUI : BaseDiffCallback<ViewTyped>() {

    override fun areItemsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
        return oldItem.viewType == newItem.viewType
    }

    override fun areContentsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {

        var areInContentsTheSame = false
        var areOutContentsTheSame = false
        var areDateSeparatorContentsTheSame = false

        if (oldItem.viewType == R.layout.item_incoming_message &&
            newItem.viewType == R.layout.item_incoming_message
        ) {
            areInContentsTheSame = (oldItem as InMessageUI) == (newItem as InMessageUI)
        }

        if (oldItem.viewType == R.layout.item_outcoming_message &&
            newItem.viewType == R.layout.item_outcoming_message
        ) {
            areOutContentsTheSame = (oldItem as OutMessageUI) == (newItem as OutMessageUI)
        }

        if (oldItem.viewType == R.layout.item_date_separator &&
            newItem.viewType == R.layout.item_date_separator
        ) {
            areDateSeparatorContentsTheSame =
                (oldItem as DateSeparatorUI) == (newItem as DateSeparatorUI)
        }

        return areInContentsTheSame || areOutContentsTheSame || areDateSeparatorContentsTheSame
    }

}