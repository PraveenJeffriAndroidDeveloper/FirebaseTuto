package com.example.fire2

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            registerUser()
        }
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            loginUser()
        }

        findViewById<Button>(R.id.btnUpdateProfile).setOnClickListener {
            updateProfile()
        }

    }

    private fun updateProfile()
    {
        auth.currentUser?.let {user->
            val userName = findViewById<TextView>(R.id.etUsername).text.toString()
            val photoUri = Uri.parse("android.resource://$packageName/${R.drawable.abls_background}")
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .setPhotoUri(photoUri)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdates).await()
                    withContext(Dispatchers.Main)
                    {
                        checkLoggedInState()
                        Toast.makeText(this@MainActivity , "Successfully updated user profile" ,Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e:Exception)
                {
                    withContext(Dispatchers.Main)
                    {
                        Toast.makeText(this@MainActivity , e.message ,Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        checkLoggedInState()
    }
    private fun registerUser()
    {
        var email = findViewById<EditText>(R.id.etEmailRegister).text.toString()
        var password = findViewById<EditText>(R.id.etPasswordRegister).text.toString()
        if (email.isNotEmpty() && password.isNotEmpty())
        {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email , password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }

                }
                catch (e : Exception)
                {
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity , e.message , Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun loginUser()
    {
        var email = findViewById<EditText>(R.id.etEmailLogin).text.toString()
        var password = findViewById<EditText>(R.id.etPasswordLogin).text.toString()
        if (email.isNotEmpty() && password.isNotEmpty())
        {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email , password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }

                }
                catch (e : Exception)
                {
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity , e.message , Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun checkLoggedInState()
    {
        val user = auth.currentUser
        if (user == null)
        {
            findViewById<TextView>(R.id.tvLoggedIn).text = "Your are not Logged"
        }
        else
        {
            findViewById<TextView>(R.id.tvLoggedIn).text = "Your are Logged"
            findViewById<EditText>(R.id.etUsername).setText(user.displayName)
            findViewById<ImageView>(R.id.ivProfilePicture).setImageURI(user.photoUrl)
        }
    }
}