package com.turik2304.coursework.recyclerViewBase.items

import com.turik2304.coursework.FakeServerApi
import com.turik2304.coursework.R
import com.turik2304.coursework.recyclerViewBase.ViewTyped

data class OutMessageUI(
    val userName: String,
    val message: String,
    var reactions: List<FakeServerApi.Reaction>,
    val dateInMillis: Long,
    override val uid: String = "OUTCOMING_MESSAGE_UI_ID",
    override val viewType: Int = R.layout.item_outcoming_message
) : ViewTyped {


}
