package com.incedo.personalhealth.feature.home

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.NSItemProvider
import platform.Foundation.base64EncodedStringWithOptions
import platform.Photos.PHPhotoLibrary
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@OptIn(ExperimentalForeignApi::class)
actual object NutritionImagePicker {
    private var activeDelegate: NutritionImagePickerDelegate? = null

    actual fun pickImage(onImagePicked: (NutritionUploadedImage?) -> Unit) {
        val presenter = findTopViewController() ?: run {
            onImagePicked(null)
            return
        }

        val configuration = PHPickerConfiguration(photoLibrary = PHPhotoLibrary.sharedPhotoLibrary()).apply {
            selectionLimit = 1
            filter = PHPickerFilter.imagesFilter()
        }

        val delegate = NutritionImagePickerDelegate { uploadedImage ->
            activeDelegate = null
            onImagePicked(uploadedImage)
        }
        val picker = PHPickerViewController(configuration = configuration).apply {
            this.delegate = delegate
        }

        activeDelegate = delegate
        presenter.presentViewController(
            viewControllerToPresent = picker,
            animated = true,
            completion = null
        )
    }
}

private class NutritionImagePickerDelegate(
    private val onImagePicked: (NutritionUploadedImage?) -> Unit
) : NSObject(), PHPickerViewControllerDelegateProtocol {

    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(flag = true, completion = null)

        val result = didFinishPicking.firstOrNull() as? PHPickerResult ?: run {
            complete(null)
            return
        }
        val provider = result.itemProvider
        val suggestedCaption = provider.suggestedName?.takeIf { it.isNotBlank() } ?: "Nieuwe foto"

        provider.loadDataRepresentationForTypeIdentifier("public.image") { data, _ ->
            complete(
                data?.let {
                    NutritionUploadedImage(
                        dataUrl = it.toDataUrl(mimeType = mimeTypeFor(provider)),
                        suggestedCaption = suggestedCaption
                    )
                }
            )
        }
    }

    private fun complete(image: NutritionUploadedImage?) {
        dispatch_async(dispatch_get_main_queue()) {
            onImagePicked(image)
        }
    }
}

private fun findTopViewController(): UIViewController? {
    val application = UIApplication.sharedApplication
    val rootController = application.keyWindow?.rootViewController
        ?: (application.windows.firstOrNull() as? UIWindow)?.rootViewController
    var controller: UIViewController? = rootController

    while (controller?.presentedViewController != null) {
        controller = controller.presentedViewController
    }
    return controller
}

private fun mimeTypeFor(provider: NSItemProvider): String = when {
    provider.hasItemConformingToTypeIdentifier("public.png") -> "image/png"
    provider.hasItemConformingToTypeIdentifier("public.heic") -> "image/heic"
    provider.hasItemConformingToTypeIdentifier("public.heif") -> "image/heif"
    provider.hasItemConformingToTypeIdentifier("com.compuserve.gif") -> "image/gif"
    else -> "image/jpeg"
}

private fun NSData.toDataUrl(mimeType: String): String {
    val base64 = base64EncodedStringWithOptions(0u)
    return "data:$mimeType;base64,$base64"
}
