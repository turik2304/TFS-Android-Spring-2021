package com.turik2304.coursework.data.network.utils

import com.turik2304.coursework.data.network.models.data.OperationEnum
import com.turik2304.coursework.data.network.models.data.Reaction
import com.turik2304.coursework.data.network.models.data.ReactionEvent
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.items.InMessageUI
import com.turik2304.coursework.presentation.recycler_view.items.OutMessageUI
import com.turik2304.coursework.presentation.recycler_view.items.ReactionUI
import java.util.*

class ReactionHelperImpl : ReactionHelper {

    override fun updateReactions(
        currentList: List<ViewTyped>,
        reactionEvents: List<ReactionEvent>
    ): List<ViewTyped> {
        var updatedList: List<ViewTyped> = currentList
        reactionEvents.forEach { reactionEvent ->
            updatedList = updatedList.map { viewTyped ->
                if (viewTyped.uid == reactionEvent.messageId) {
                    return@map when (viewTyped) {
                        is InMessageUI -> viewTyped.copy(
                            reactions = handleReactions(
                                viewTyped.reactions,
                                reactionEvent
                            )
                        )
                        is OutMessageUI -> viewTyped.copy(
                            reactions = handleReactions(
                                viewTyped.reactions,
                                reactionEvent
                            )
                        )
                        else -> viewTyped
                    }
                } else return@map viewTyped
            }
        }
        return updatedList
    }

    override fun parseReactions(
        zulipReactions: List<Reaction>,
    ): List<ReactionUI> {
        val listOfReactions = mutableListOf<ReactionUI>()
        zulipReactions.forEach { zulipReaction ->
            var isTheSameReaction = false
            var indexOfSameReaction = -1
            val emojiCode = Integer.parseInt(zulipReaction.emojiCode, 16)
            val userId = zulipReaction.userId
            listOfReactions.forEachIndexed { index, reaction ->
                if (reaction.emojiCode == emojiCode) {
                    isTheSameReaction = true
                    indexOfSameReaction = index
                }
            }
            if (isTheSameReaction) {
                listOfReactions[indexOfSameReaction].counter++
                listOfReactions[indexOfSameReaction].usersWhoClicked.add(userId)
            } else {
                listOfReactions.add(ReactionUI(emojiCode, 1, mutableListOf(userId)))
            }
        }
        return listOfReactions.reversed()
    }

    private fun handleReactions(
        reactions: List<ReactionUI>,
        reactionEvent: ReactionEvent
    ): List<ReactionUI> {
        val targetReaction = ReactionUI(
            emojiCode = Integer.parseInt(reactionEvent.emojiCode, 16),
            counter = 1,
            usersWhoClicked = mutableListOf(reactionEvent.userId)
        )
        var reactionFounded = false
        val updatedReactions = reactions.map { currentReaction ->
            if (currentReaction.emojiCode == targetReaction.emojiCode) {
                when (reactionEvent.operation) {
                    OperationEnum.ADD -> {
                        reactionFounded = true
                        if (targetReaction.usersWhoClicked.single() !in currentReaction.usersWhoClicked) {
                            val emojiCode = currentReaction.emojiCode
                            val counter = currentReaction.counter + 1
                            val usersWhoClicked =
                                (currentReaction.usersWhoClicked + targetReaction.usersWhoClicked).toMutableList()
                            return@map currentReaction.copy(
                                emojiCode = emojiCode,
                                counter = counter,
                                usersWhoClicked = usersWhoClicked
                            )
                        } else return@map currentReaction
                    }
                    OperationEnum.REMOVE -> {
                        reactionFounded = true
                        if (targetReaction.usersWhoClicked.single() in currentReaction.usersWhoClicked) {
                            val emojiCode = currentReaction.emojiCode
                            val counter = currentReaction.counter - 1
                            val usersWhoClicked =
                                (currentReaction.usersWhoClicked - targetReaction.usersWhoClicked).toMutableList()
                            return@map currentReaction.copy(
                                emojiCode = emojiCode,
                                counter = counter,
                                usersWhoClicked = usersWhoClicked
                            )
                        } else return@map currentReaction
                    }
                }
            } else return@map currentReaction
        }
        return if (reactionFounded) {
            updatedReactions.filter { it.counter > 0 }
        } else {
            return if (reactionEvent.operation == OperationEnum.ADD) {
                (updatedReactions + listOf(targetReaction))
            } else updatedReactions
        }
    }
}