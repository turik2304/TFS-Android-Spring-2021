package com.turik2304.coursework.data.network.utils

import com.turik2304.coursework.data.network.models.data.Reaction
import com.turik2304.coursework.data.network.models.data.ReactionEvent
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.items.ReactionUI

interface ReactionHelper {
    fun updateReactions(
        currentList: List<ViewTyped>,
        reactionEvents: List<ReactionEvent>
    ): List<ViewTyped>

    fun parseReactions(
        zulipReactions: List<Reaction>,
    ): List<ReactionUI>
}