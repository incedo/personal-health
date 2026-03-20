package com.incedo.personalhealth.feature.home

import kotlin.js.JsString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class NutritionUploadedImagePayload(
    val dataUrl: String,
    val suggestedCaption: String
)

private val nutritionImagePickerJson = Json { ignoreUnknownKeys = true }

actual object NutritionImagePicker {
    actual fun pickImage(onImagePicked: (NutritionUploadedImage?) -> Unit) {
        openNutritionImagePicker { payload ->
            val decoded = payload?.toString()
                ?.takeIf { it.isNotBlank() }
                ?.let { serialized ->
                    runCatching {
                        nutritionImagePickerJson.decodeFromString<NutritionUploadedImagePayload>(serialized)
                    }.getOrNull()
                }

            onImagePicked(
                decoded?.let {
                    NutritionUploadedImage(
                        dataUrl = it.dataUrl,
                        suggestedCaption = it.suggestedCaption
                    )
                }
            )
        }
    }
}

@JsFun(
    """callback => {
        const input = document.createElement('input');
        input.type = 'file';
        input.accept = 'image/*';
        input.addEventListener('change', () => {
            const file = input.files && input.files[0];
            if (!file) {
                callback(null);
                return;
            }
            const reader = new FileReader();
            reader.addEventListener('load', () => {
                const dataUrl = typeof reader.result === 'string' ? reader.result : null;
                if (!dataUrl) {
                    callback(null);
                    return;
                }
                callback(JSON.stringify({
                    dataUrl,
                    suggestedCaption: file.name.replace(/\.[^/.]+$/, '') || 'Nieuwe foto'
                }));
            });
            reader.addEventListener('error', () => callback(null));
            reader.readAsDataURL(file);
        });
        input.click();
    }"""
)
private external fun openNutritionImagePicker(callback: (JsString?) -> Unit)
