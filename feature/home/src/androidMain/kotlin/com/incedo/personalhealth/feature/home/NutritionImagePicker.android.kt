package com.incedo.personalhealth.feature.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import java.lang.ref.WeakReference

actual object NutritionImagePicker {
    private var hostActivityRef: WeakReference<Activity>? = null
    private var pendingCallback: ((NutritionUploadedImage?) -> Unit)? = null

    fun attachHost(activity: Activity) {
        hostActivityRef = WeakReference(activity)
    }

    actual fun pickImage(onImagePicked: (NutritionUploadedImage?) -> Unit) {
        val hostActivity = hostActivityRef?.get()
        if (hostActivity == null) {
            onImagePicked(null)
            return
        }

        pendingCallback = onImagePicked
        hostActivity.startActivity(
            Intent(hostActivity, NutritionImagePickerActivity::class.java)
        )
    }

    internal fun dispatchPickedImage(context: Context, imageUri: Uri?) {
        val callback = pendingCallback
        pendingCallback = null
        callback?.invoke(imageUri?.let { context.readUploadedImage(it) })
    }
}

class NutritionImagePickerActivity : ComponentActivity() {
    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        NutritionImagePicker.dispatchPickedImage(this, uri)
        finish()
    }

    override fun onStart() {
        super.onStart()
        imagePicker.launch("image/*")
    }
}

private fun Context.readUploadedImage(uri: Uri): NutritionUploadedImage? {
    val contentResolver = contentResolver
    val bytes = contentResolver.openInputStream(uri)?.use { stream -> stream.readBytes() } ?: return null
    val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
    val dataUrl = "data:$mimeType;base64,${Base64.encodeToString(bytes, Base64.NO_WRAP)}"
    val suggestedCaption = queryDisplayName(uri)
        ?.substringBeforeLast('.')
        ?.takeIf { it.isNotBlank() }
        ?: "Nieuwe foto"
    return NutritionUploadedImage(
        dataUrl = dataUrl,
        suggestedCaption = suggestedCaption
    )
}

private fun Context.queryDisplayName(uri: Uri): String? {
    if (uri.scheme == "file") {
        return uri.lastPathSegment
    }

    return contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) cursor.getString(nameIndex) else null
        }
}
