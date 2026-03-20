package com.incedo.personalhealth.feature.home

expect object NutritionImagePicker {
    fun pickImage(onImagePicked: (NutritionUploadedImage?) -> Unit)
}

data class NutritionUploadedImage(
    val dataUrl: String,
    val suggestedCaption: String
)
