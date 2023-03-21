package com.example.acompanhamentoestudantil

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

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
            val displayName = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl
            if (displayName != "") {
                val splitName = displayName.toString().split(" ")
                if (splitName.size > 1) {
                    findViewById<EditText>(R.id.etName).setText(splitName[0])
                    findViewById<EditText>(R.id.etLastName).setText(splitName[1])
                } else {
                    findViewById<EditText>(R.id.etName).setText(displayName.toString())
                }
            }
            findViewById<EditText>(R.id.etEmail).setText(email)
            if (photoUrl != null) {
                Thread {
                    val file = saveLocalFile(photoUrl.toString())
                    runOnUiThread {
                        val bitmap = BitmapFactory.decodeFile(file.path)
                        findViewById<ImageView>(R.id.profile_image).setImageBitmap(bitmap)
                    }
                }.start()
            }
        }

        findViewById<View>(R.id.btn_edit_profile).setOnClickListener {
            saveProfile()
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

        findViewById<View>(R.id.btn_edit_profile).setOnClickListener {
            val u = firebaseAuth.currentUser
            val emailAddress = u?.email.toString()

            firebaseAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, R.string.email_sent_password, Toast.LENGTH_SHORT).show()
                }
            }
        }

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_READ_EXTERNAL_STORAGE)
        }
    }

    private fun saveLocalFile(_url: String): File {
        val url = URL(_url)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true;
        connection.connect()
        val input = connection.inputStream
        val dir = File(getExternalFilesDir(null), "images")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, "imagem.jpge")
        val output = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var read: Int
        while(input.read(buffer).also { read = it } != -1) {
            output.write(buffer, 0, read)
        }

        output.flush()
        output.close()
        input.close()
        return file
    }

    private fun saveProfile() {
        val email = findViewById<EditText>(R.id.etEmail).text.toString()
        val name = findViewById<EditText>(R.id.etName).text.toString()
        val lastName = findViewById<EditText>(R.id.etLastName).text.toString()

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val user = FirebaseAuth.getInstance().currentUser
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val imageRef = storageRef.child("profile_images/${uid}.jpeg")
        val baos = ByteArrayOutputStream()
        val data = baos.toByteArray()
        this._image?.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        val uploadTask = imageRef.putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(this, R.string.failure_image, Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {_ ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val profileUpdates = userProfileChangeRequest {
                    displayName = "${name} ${lastName}"
                    photoUri = Uri.parse(uri.toString())
                }

                user!!.updateProfile(profileUpdates).addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, R.string.success_profile_update, Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, R.string.failure_profile_update, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
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