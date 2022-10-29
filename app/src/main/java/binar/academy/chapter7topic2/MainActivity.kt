package binar.academy.chapter7topic2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import binar.academy.chapter7topic2.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleSIC: GoogleSignInClient
    lateinit var userPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPrefs = getSharedPreferences("User", Context.MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSIC = GoogleSignIn.getClient(this, gso)

        val dataUser = userPrefs.getString("Username", "undefined")
        binding.homeHeader.text = "Welcome, $dataUser"

        binding.homeButton.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        firebaseAuth.signOut()
        googleSIC.signOut().addOnCompleteListener {
            entrypoint()
        }
    }

    private fun entrypoint() {
        val intent= Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}