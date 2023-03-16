package com.example.acompanhamentoestudantil

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class Profile : AppCompatActivity() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private val PERMISSION_REQUEST_CAMERA = 0
    private val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1
    private var _image: Bitmap? = null

    companion object {
        private const val REQUEST_IMAGE_GALLERY = 1
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.hide()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        val user = firebaseAuth.currentUser
        if(user != null) {
           // val displayName = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl
            //val splitName = displayName.toString().split("")

            //findViewById<EditText>(R.id.etName).setText(splitName[0])
            //findViewById<EditText>(R.id.etLastName).setText(splitName[1])
            findViewById<EditText>(R.id.etEmail).setText(email)
        }

        findViewById<View>(R.id.btn_edit_profile).setOnClickListener {

        }

        findViewById<View>(R.id.btnBack).setOnClickListener {
            val activity = Intent(this, Dashboard::class.java)
            startActivity(activity)
        }

        findViewById<View>(R.id.fab_files).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
        }

        findViewById<View>(R.id.fab_camera).setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_READ_EXTERNAL_STORAGE)
        }
    }

    fun saveLocalFile() {

    }

    fun saveProfile() {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.approved_permission, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.denied_permission, Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.approved_permission, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.denied_permission, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK) {
            when(resultCode){
                REQUEST_IMAGE_GALLERY -> {
                    val selectedImage: Uri? = data?.data
                    val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                    findViewById<ImageView>(R.id.profile_image).setImageBitmap(imageBitmap)
                    this._image = imageBitmap
                }
                REQUEST_IMAGE_CAPTURE-> {
                    val imageCaptured = data?.extras?.get("data") as Bitmap
                    findViewById<ImageView>(R.id.profile_image).setImageBitmap(imageCaptured)
                    this._image = imageCaptured
                }
            }
        }
    }
}