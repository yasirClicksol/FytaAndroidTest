package com.example.fytatask.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.MediaStore
import java.lang.Exception
import java.util.*
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.fytatask.R
import com.example.fytatask.ui.ShowPlantResult
import com.example.fytatask.utils.Constants.BASE_URL
import com.example.fytatask.utils.Constants.GALLERY_PICTURE
import com.example.fytatask.utils.Constants.CAMERA_PICTURE
import kotlinx.android.synthetic.main.activity_gallery_camera.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import java.io.*

/*****************************
 * Converting bitmap to image path
 * ****************************/
fun Context.persistImage(bitmap: Bitmap, name: String): File {
    val filesDir: File = filesDir
    val imageFile = File(filesDir, "$name.jpg")
    val os: OutputStream
    try {
        os = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
        os.flush()
        os.close()

    } catch (e: Exception) {
    }
    return imageFile
}

/*****************************
 * Gallery open intent
 * ****************************/
fun openGallery(activity: Activity) {
    val intent =
        Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "image/*"
    activity.startActivityForResult(
        Intent.createChooser(intent, "Select Picture"),
        GALLERY_PICTURE
    )
}

/*****************************
 * Checking internet connectivity
 * ****************************/
fun isOnline(activity: Activity): Boolean {
    val networkInfo =
        (activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

/*****************************
 * Camera opening intent
 * ****************************/
fun openCamera(activity: Activity) {
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    if (cameraIntent.resolveActivity(activity.packageManager) != null) activity.startActivityForResult(
        cameraIntent,
        CAMERA_PICTURE
    )
}

/*****************************
 * Getting path for gallery selected picture
 * ****************************/
@SuppressLint("NewApi")
fun getPath(uri: Uri, context: Context): String? {
    var uri = uri
    val needToCheckUri = Build.VERSION.SDK_INT >= 19
    var selection: String? = null
    var selectionArgs: Array<String>? = null
    // Uri is different in versions after KITKAT (Android 4.4), we need to
    // deal with different Uris.
    if (needToCheckUri && DocumentsContract.isDocumentUri(context, uri)) {
        when {
            isExternalStorageDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
            isDownloadsDocument(uri) -> {
                val id = DocumentsContract.getDocumentId(uri)
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:".toRegex(), "")
                }
                uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
            }
            isMediaDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                when (type) {
                    "image" -> uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                selection = "_id=?"
                selectionArgs = arrayOf(
                    split[1]
                )
            }
        }
    }
    if ("content".equals(uri.scheme, ignoreCase = true)) {
        val projection = arrayOf(
            MediaStore.Images.Media.DATA
        )
        try {
            context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                .use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        val columnIndex =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        return cursor.getString(columnIndex)
                    }
                }
        } catch (e: Exception) {
            Log.e("on getPath", "Exception", e)
        }
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
private fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
private fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
private fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

/*****************************
 * Calling API
 * ****************************/
fun Activity.callApi(
    imagePath: File,
    progressBar: ProgressBar,
    uploadData: TextView,
    imageView: ImageView
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {

            val entity: HttpEntity = MultipartEntityBuilder.create()
                .addPart("images", FileBody(imagePath))
                .addTextBody("organs", "flower")
                .build()

            val request = HttpPost(BASE_URL)
            request.entity = entity

            val client: HttpClient = HttpClientBuilder.create().build()
            val response: HttpResponse
            var jsonString = ""
            try {
                response = client.execute(request)
                jsonString =
                    EntityUtils.toString(response.entity)
                println(jsonString)

            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    uploadData.visibility = View.GONE
                }
            }
            withContext(Dispatchers.Main) {
                if (jsonString.isNotEmpty()) {
                    if (!jsonString.contains("Species not found")) {
                        val intent = Intent(this@callApi, ShowPlantResult::class.java)
                        intent.putExtra("plantResult", jsonString)
                        intent.putExtra("imagePath", imagePath.toString())
                        startActivity(intent)
                        progressBar.visibility = View.GONE
                        uploadData.visibility = View.GONE
                        imageView.setImageResource(0)

                    } else {
                        progressBar.visibility = View.GONE
                        uploadData.visibility = View.GONE
                        imageView.setImageResource(0)
                        showRetakeDialog()

                    }
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(
                this@callApi,
                "Something went wrong",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

/*****************************
 * Opening detail view
 * ****************************/
fun Context.showDialog(speName: String?, speImagePath: String?) {
    val dialog = Dialog(this, R.style.CustomDialogWithMargin)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window?.setBackgroundDrawableResource(R.color.transparent)
    dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    dialog.window?.setDimAmount(0.8f)
    dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
    dialog.setCancelable(true)
    dialog.setContentView(R.layout.show_detail_dialog)
    val speciesName = dialog.findViewById(R.id.d_species_name) as TextView
    val speciesImage = dialog.findViewById(R.id.d_species_image) as ImageView
    speciesName.text = speName
    Glide.with(this)
        .load(speImagePath)
        .apply(RequestOptions().transform(CenterCrop(), GranularRoundedCorners(0f, 0f, 32f, 32f)))
        .into(speciesImage)
    dialog.show()

}

/*****************************
 * Showing retake dialog when no result found
 * ****************************/
fun Activity.showRetakeDialog() {
    val dialog = Dialog(this, R.style.CustomDialogWithMargin)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window?.setBackgroundDrawableResource(R.color.transparent)
    dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    dialog.window?.setDimAmount(0.8f)
    dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.retake_photo_dialog)
    val retakePhoto = dialog.findViewById(R.id.tv_retake) as TextView
    dialog.apply {
        retakePhoto.setOnClickListener {
            dismiss()
        }
        if (!isFinishing) show()
    }


}