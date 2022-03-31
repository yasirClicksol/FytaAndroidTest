package com.example.fytatask

import android.Manifest
import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import com.example.fytatask.utils.*
import kotlinx.android.synthetic.main.activity_gallery_camera.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import com.example.fytatask.utils.Constants.GALLERY_PICTURE
import com.example.fytatask.utils.Constants.CAMERA_PICTURE
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions

import java.io.File


class GalleryCameraActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_camera)
        /*****************************
         * Permission and opening gallery
         * ****************************/


        iv_gallery_picker.setOnClickListener {
            Permissions.check(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        openGallery(this@GalleryCameraActivity)
                    }
                })
        }

        /*****************************
         * Permission and opening camera
         * ****************************/


        button_camera.setOnClickListener {
            Permissions.check(
                this,
                Manifest.permission.CAMERA,
                null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        openCamera(this@GalleryCameraActivity)
                    }
                })
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            /*****************************
             * Calling API for Gallery Image
             * ****************************/

            GALLERY_PICTURE -> {
                if (isOnline(this)) {
                    var imageUri = data?.data
                    if (imageUri != null) {
                        showProgressView()
                        val imageStream = imageUri.let { contentResolver.openInputStream(it) }
                        val selectedImage = BitmapFactory.decodeStream(imageStream)
                        image_preview.setImageBitmap(selectedImage)
                        val galleryImagePath = File(getPath(imageUri, this))
                        callApi(galleryImagePath, progress_circular, tv_upload_data, image_preview)

                    }

                }
            }

            /*****************************
             * Calling API for Camera Image
             * ****************************/

            CAMERA_PICTURE -> {
                if (isOnline(this)) {
                    val photo = data?.extras?.get("data") as Bitmap?
                    // Set the image in imageview for display
                    if (photo != null) {
                        showProgressView()
                        image_preview.setImageBitmap(photo)
                        val imageName = System.currentTimeMillis()
                        val cameraPicPath =
                            photo.let { this.persistImage(it, imageName.toString()) }
                        callApi(cameraPicPath, progress_circular, tv_upload_data, image_preview)
                    }
                }

            }

        }


    }

    /*****************************
     * Showing Progress
     * ****************************/

    private fun showProgressView() {
        progress_circular.visibility = View.VISIBLE
        tv_upload_data.visibility = View.VISIBLE
    }

}