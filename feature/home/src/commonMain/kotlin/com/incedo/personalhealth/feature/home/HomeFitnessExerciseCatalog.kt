package com.incedo.personalhealth.feature.home

enum class FitnessExerciseTemplate(
    val label: String,
    val defaultSets: Int,
    val defaultReps: Int,
    val defaultWeightKg: Int,
    val primaryMuscleGroup: FitnessPrimaryMuscleGroup,
    val detailMuscle: FitnessMuscleDetail
) {
    INCLINE_DUMBBELL_PRESS("Incline dumbbell press", 4, 10, 24, FitnessPrimaryMuscleGroup.CHEST, FitnessMuscleDetail.UPPER_CHEST),
    LOW_TO_HIGH_CABLE_FLY("Low to high cable fly", 3, 14, 10, FitnessPrimaryMuscleGroup.CHEST, FitnessMuscleDetail.UPPER_CHEST),
    BENCH_PRESS("Bench press", 4, 8, 60, FitnessPrimaryMuscleGroup.CHEST, FitnessMuscleDetail.MID_CHEST),
    MACHINE_CHEST_PRESS("Machine chest press", 3, 12, 55, FitnessPrimaryMuscleGroup.CHEST, FitnessMuscleDetail.MID_CHEST),
    BARBELL_OVERHEAD_PRESS("Barbell overhead press", 4, 6, 40, FitnessPrimaryMuscleGroup.SHOULDERS, FitnessMuscleDetail.FRONT_DELTS),
    ARNOLD_PRESS("Arnold press", 3, 10, 16, FitnessPrimaryMuscleGroup.SHOULDERS, FitnessMuscleDetail.FRONT_DELTS),
    LATERAL_RAISE("Lateral raise", 4, 15, 8, FitnessPrimaryMuscleGroup.SHOULDERS, FitnessMuscleDetail.SIDE_DELTS),
    CABLE_LATERAL_RAISE("Cable lateral raise", 3, 14, 6, FitnessPrimaryMuscleGroup.SHOULDERS, FitnessMuscleDetail.SIDE_DELTS),
    REVERSE_PEC_DECK("Reverse pec deck", 4, 14, 28, FitnessPrimaryMuscleGroup.SHOULDERS, FitnessMuscleDetail.REAR_DELTS),
    FACE_PULL("Face pull", 3, 15, 18, FitnessPrimaryMuscleGroup.SHOULDERS, FitnessMuscleDetail.REAR_DELTS),
    BARBELL_SHRUG("Barbell shrug", 4, 12, 80, FitnessPrimaryMuscleGroup.BACK, FitnessMuscleDetail.TRAPS),
    DUMBBELL_SHRUG("Dumbbell shrug", 3, 14, 28, FitnessPrimaryMuscleGroup.BACK, FitnessMuscleDetail.TRAPS),
    CHEST_SUPPORTED_ROW("Chest supported row", 4, 10, 42, FitnessPrimaryMuscleGroup.BACK, FitnessMuscleDetail.RHOMBOIDS),
    SEATED_CABLE_ROW("Seated cable row", 4, 12, 45, FitnessPrimaryMuscleGroup.BACK, FitnessMuscleDetail.RHOMBOIDS),
    LAT_PULLDOWN("Lat pulldown", 4, 10, 52, FitnessPrimaryMuscleGroup.BACK, FitnessMuscleDetail.LATS),
    SINGLE_ARM_ROW("Single arm row", 3, 12, 28, FitnessPrimaryMuscleGroup.BACK, FitnessMuscleDetail.LATS),
    INCLINE_CURL("Incline curl", 3, 12, 12, FitnessPrimaryMuscleGroup.ARMS, FitnessMuscleDetail.BICEPS_LONG),
    DRAG_CURL("Drag curl", 3, 10, 24, FitnessPrimaryMuscleGroup.ARMS, FitnessMuscleDetail.BICEPS_LONG),
    PREACHER_CURL("Preacher curl", 3, 12, 18, FitnessPrimaryMuscleGroup.ARMS, FitnessMuscleDetail.BICEPS_SHORT),
    CABLE_CURL("Cable curl", 3, 14, 16, FitnessPrimaryMuscleGroup.ARMS, FitnessMuscleDetail.BICEPS_SHORT),
    OVERHEAD_TRICEPS_EXTENSION("Overhead triceps extension", 3, 12, 18, FitnessPrimaryMuscleGroup.ARMS, FitnessMuscleDetail.TRICEPS_LONG),
    SKULL_CRUSHER("Skull crusher", 3, 10, 24, FitnessPrimaryMuscleGroup.ARMS, FitnessMuscleDetail.TRICEPS_LONG),
    ROPE_PRESSDOWN("Rope pressdown", 4, 12, 22, FitnessPrimaryMuscleGroup.ARMS, FitnessMuscleDetail.TRICEPS_LATERAL),
    CLOSE_GRIP_PUSHUP("Close grip push-up", 3, 15, 0, FitnessPrimaryMuscleGroup.ARMS, FitnessMuscleDetail.TRICEPS_LATERAL),
    CABLE_CRUNCH("Cable crunch", 4, 14, 24, FitnessPrimaryMuscleGroup.CORE, FitnessMuscleDetail.UPPER_ABS),
    DECLINE_SIT_UP("Decline sit-up", 3, 15, 0, FitnessPrimaryMuscleGroup.CORE, FitnessMuscleDetail.UPPER_ABS),
    RUSSIAN_TWIST("Russian twist", 3, 20, 8, FitnessPrimaryMuscleGroup.CORE, FitnessMuscleDetail.OBLIQUES),
    PALLOF_PRESS("Pallof press", 3, 12, 16, FitnessPrimaryMuscleGroup.CORE, FitnessMuscleDetail.OBLIQUES),
    BARBELL_HIP_THRUST("Barbell hip thrust", 4, 8, 90, FitnessPrimaryMuscleGroup.GLUTES, FitnessMuscleDetail.GLUTE_MAX),
    GLUTE_BRIDGE("Glute bridge", 3, 15, 40, FitnessPrimaryMuscleGroup.GLUTES, FitnessMuscleDetail.GLUTE_MAX),
    CABLE_HIP_ABDUCTION("Cable hip abduction", 3, 15, 12, FitnessPrimaryMuscleGroup.GLUTES, FitnessMuscleDetail.GLUTE_MED),
    BANDED_LATERAL_WALK("Banded lateral walk", 3, 20, 0, FitnessPrimaryMuscleGroup.GLUTES, FitnessMuscleDetail.GLUTE_MED),
    FRONT_SQUAT("Front squat", 4, 6, 70, FitnessPrimaryMuscleGroup.LEGS, FitnessMuscleDetail.QUAD_SWEEP),
    HACK_SQUAT("Hack squat", 4, 10, 90, FitnessPrimaryMuscleGroup.LEGS, FitnessMuscleDetail.QUAD_SWEEP),
    LEG_EXTENSION("Leg extension", 3, 15, 40, FitnessPrimaryMuscleGroup.LEGS, FitnessMuscleDetail.QUAD_TEARDROP),
    HEEL_ELEVATED_GOBLET_SQUAT("Heel elevated goblet squat", 3, 12, 28, FitnessPrimaryMuscleGroup.LEGS, FitnessMuscleDetail.QUAD_TEARDROP),
    ROMANIAN_DEADLIFT("Romanian deadlift", 4, 8, 80, FitnessPrimaryMuscleGroup.LEGS, FitnessMuscleDetail.HAMSTRING_LONG),
    GOOD_MORNING("Good morning", 3, 10, 45, FitnessPrimaryMuscleGroup.LEGS, FitnessMuscleDetail.HAMSTRING_LONG),
    LYING_LEG_CURL("Lying leg curl", 4, 12, 32, FitnessPrimaryMuscleGroup.LEGS, FitnessMuscleDetail.HAMSTRING_MEDIAL),
    SEATED_LEG_CURL("Seated leg curl", 3, 14, 28, FitnessPrimaryMuscleGroup.LEGS, FitnessMuscleDetail.HAMSTRING_MEDIAL),
    STANDING_CALF_RAISE("Standing calf raise", 4, 15, 70, FitnessPrimaryMuscleGroup.CALVES, FitnessMuscleDetail.GASTROCNEMIUS),
    DONKEY_CALF_RAISE("Donkey calf raise", 3, 18, 60, FitnessPrimaryMuscleGroup.CALVES, FitnessMuscleDetail.GASTROCNEMIUS),
    SEATED_CALF_RAISE("Seated calf raise", 4, 18, 40, FitnessPrimaryMuscleGroup.CALVES, FitnessMuscleDetail.SOLEUS),
    BENT_KNEE_CALF_PRESS("Bent knee calf press", 3, 20, 90, FitnessPrimaryMuscleGroup.CALVES, FitnessMuscleDetail.SOLEUS)
}

fun exerciseTemplatesFor(
    primaryGroup: FitnessPrimaryMuscleGroup?,
    selectedDetails: Set<FitnessMuscleDetail>
): List<FitnessExerciseTemplate> {
    if (primaryGroup == null) return FitnessExerciseTemplate.entries
    val primaryMatches = FitnessExerciseTemplate.entries.filter { it.primaryMuscleGroup == primaryGroup }
    return if (selectedDetails.isEmpty()) {
        primaryMatches
    } else {
        primaryMatches.filter { it.detailMuscle in selectedDetails }
    }
}

fun exerciseTemplatesByDetail(
    primaryGroup: FitnessPrimaryMuscleGroup?,
    selectedDetails: Set<FitnessMuscleDetail>
): Map<FitnessMuscleDetail, List<FitnessExerciseTemplate>> {
    val focusDetails = if (selectedDetails.isEmpty()) {
        primaryGroup?.let(::detailMusclesFor).orEmpty()
    } else {
        selectedDetails.toList()
    }
    return focusDetails.associateWith { detail ->
        FitnessExerciseTemplate.entries.filter { it.detailMuscle == detail }
    }
}
