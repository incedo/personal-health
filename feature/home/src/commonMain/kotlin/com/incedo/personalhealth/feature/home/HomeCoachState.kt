package com.incedo.personalhealth.feature.home

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import com.incedo.personalhealth.core.coaches.CoachProfile
import com.incedo.personalhealth.core.coaches.CoachSearchItem
import com.incedo.personalhealth.core.coaches.CoachType
import com.incedo.personalhealth.core.goals.CoachFocusGoal
import com.incedo.personalhealth.core.goals.CoachGoalsState
import com.incedo.personalhealth.core.goals.CoachIntakeProfile
import com.incedo.personalhealth.core.goals.CoachProfileTrait
import com.incedo.personalhealth.core.goals.CoachProtocolId
import com.incedo.personalhealth.core.goals.defaultCoachGoals

internal data class CoachEditorState(
    val editingCoachId: String? = null,
    val searchQuery: String = "",
    val selectedSearchItemId: String? = null,
    val selectedType: CoachType = CoachType.AI_COACH,
    val name: String = "",
    val companyName: String = "",
    val location: String = "",
    val imageDataUrl: String? = null
)

internal fun CoachEditorState.applySearchItem(item: CoachSearchItem): CoachEditorState = copy(
    searchQuery = item.name,
    selectedSearchItemId = item.id,
    selectedType = item.type,
    name = item.name,
    companyName = item.companyName,
    location = item.location,
    imageDataUrl = imageDataUrl ?: item.imageDataUrl
)

internal enum class CoachPage {
    OVERVIEW,
    INTAKE,
    GOALS,
    DETAILS
}
internal fun coachIntakeProfileSaver(): Saver<CoachIntakeProfile, Any> = Saver(
    save = { profile ->
        buildList<String> {
            add(profile.focusGoal?.name.orEmpty())
            profile.traits.forEach { add(it.name) }
        }
    },
    restore = { raw ->
        val values = raw as? List<*> ?: return@Saver null
        val focusGoal = (values.firstOrNull() as? String)
            ?.takeIf(String::isNotBlank)
            ?.let(CoachFocusGoal::valueOf)
        val traits = values.drop(1)
            .mapNotNull { it as? String }
            .mapNotNull { traitName ->
                runCatching { CoachProfileTrait.valueOf(traitName) }.getOrNull()
            }
            .toSet()
        CoachIntakeProfile(focusGoal = focusGoal, traits = traits)
    }
)

internal fun coachProtocolIdSaver(): Saver<CoachProtocolId?, Any> = Saver(
    save = { it?.name.orEmpty() },
    restore = { raw ->
        (raw as? String)?.takeIf(String::isNotBlank)?.let(CoachProtocolId::valueOf)
    }
)

internal fun coachProfilesSaver() = listSaver<List<CoachProfile>, String>(
    save = { profiles ->
        buildList {
            profiles.forEach { profile ->
                add(profile.id)
                add(profile.type.name)
                add(profile.name)
                add(profile.companyName)
                add(profile.location)
                add(profile.imageDataUrl.orEmpty())
                add(profile.isSelected.toString())
            }
        }
    },
    restore = { raw ->
        val chunkSize = if (raw.size % 7 == 0) 7 else 6
        raw.chunked(chunkSize).mapNotNull { chunk ->
            if (chunk.size < chunkSize) {
                null
            } else {
                val type = runCatching { CoachType.valueOf(chunk[1]) }.getOrNull() ?: return@mapNotNull null
                CoachProfile(
                    id = chunk[0],
                    type = type,
                    name = chunk[2],
                    companyName = if (chunkSize == 7) chunk[3] else "",
                    location = if (chunkSize == 7) chunk[4] else chunk[3],
                    imageDataUrl = if (chunkSize == 7) chunk[5].ifBlank { null } else chunk[4].ifBlank { null },
                    isSelected = if (chunkSize == 7) chunk[6].toBooleanStrictOrNull() ?: true else chunk[5].toBooleanStrictOrNull() ?: true
                )
            }
        }
    }
)

internal fun coachEditorStateSaver() = listSaver<CoachEditorState?, String>(
    save = { state ->
        if (state == null) {
            listOf()
        } else {
            listOf(
                state.editingCoachId.orEmpty(),
                state.searchQuery,
                state.selectedSearchItemId.orEmpty(),
                state.selectedType.name,
                state.name,
                state.companyName,
                state.location,
                state.imageDataUrl.orEmpty()
            )
        }
    },
    restore = { raw ->
        if (raw.isEmpty()) {
            null
        } else {
            val legacy = raw.size <= 5
            CoachEditorState(
                editingCoachId = raw.getOrNull(0).orEmpty().ifBlank { null },
                searchQuery = if (legacy) raw.getOrNull(2).orEmpty() else raw.getOrNull(1).orEmpty(),
                selectedSearchItemId = if (legacy) null else raw.getOrNull(2).orEmpty().ifBlank { null },
                selectedType = raw.getOrNull(if (legacy) 1 else 3)?.let(CoachType::valueOf) ?: CoachType.AI_COACH,
                name = raw.getOrNull(if (legacy) 2 else 4).orEmpty(),
                companyName = if (legacy) "" else raw.getOrNull(5).orEmpty(),
                location = raw.getOrNull(if (legacy) 3 else 6).orEmpty(),
                imageDataUrl = raw.getOrNull(if (legacy) 4 else 7).orEmpty().ifBlank { null }
            )
        }
    }
)


internal fun coachGoalsStateSaver() = listSaver<CoachGoalsState, String>(
    save = { state ->
        buildList {
            add(state.draftTitle)
            state.goals.forEach { goal ->
                add(goal.title)
                add(goal.cadence)
                add(goal.focus)
            }
        }
    },
    restore = { raw ->
        val draftTitle = raw.firstOrNull().orEmpty()
        val goals = raw.drop(1).chunked(3).mapNotNull { chunk ->
            if (chunk.size < 3) null else com.incedo.personalhealth.core.goals.CoachGoal(
                title = chunk[0],
                cadence = chunk[1],
                focus = chunk[2]
            )
        }
        CoachGoalsState(goals = goals.ifEmpty { defaultCoachGoals }, draftTitle = draftTitle)
    }
)
