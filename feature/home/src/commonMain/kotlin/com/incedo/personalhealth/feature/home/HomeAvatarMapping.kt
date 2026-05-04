package com.incedo.personalhealth.feature.home

import com.incedo.personalhealth.core.designsystem.PhAvatarVariant

internal fun FitnessBodyProfile.toAvatarVariant(): PhAvatarVariant = when (this) {
    FitnessBodyProfile.MALE -> PhAvatarVariant.Masculine
    FitnessBodyProfile.FEMALE -> PhAvatarVariant.Feminine
}
