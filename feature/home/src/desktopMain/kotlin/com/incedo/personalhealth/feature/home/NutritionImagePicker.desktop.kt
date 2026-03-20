package com.incedo.personalhealth.feature.home

actual object NutritionImagePicker {
    actual fun pickImage(onImagePicked: (NutritionUploadedImage?) -> Unit) {
        onImagePicked(null)
    }
}
