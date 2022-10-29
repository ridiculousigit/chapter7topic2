package binar.academy.chapter7topic2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import binar.academy.chapter7topic2.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleSIC: GoogleSignInClient
    lateinit var userPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPrefs = getSharedPreferences("User", Context.MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSIC = GoogleSignIn.getClient(this, gso)

        binding.loginButton.setOnClickListener {
            loginWithEmailPass()
        }

        binding.btnGoogle.setOnClickListener {
            loginGoogleOptions()
        }

        binding.loginRegister.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }
    }

    private fun loginWithEmailPass() {
        val email = binding.loginEmail.text.toString()
        val pass = binding.loginPassword.text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty()){
            firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        localData(email)
                        Toast.makeText(this, "Selamat Datang, WAHAI PEMAIN 1 !", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Wajib mengisi semua kolom !", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginGoogleOptions() {
        val intent = googleSIC.signInIntent
        result.launch(intent)
    }

    private var result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            resultHandler(task)
        }
    }

    private fun resultHandler(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null){
                getDataUser(account)
            }
        }catch(e : ApiException){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDataUser(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                localData(account.displayName.toString())
                Toast.makeText(this, "Selamat Datang, WAHAI PEMAIN 1 !", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, "Proses Login Gagal !", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun localData(username : String) {
        val setUsername = userPrefs.edit()
        setUsername.putString("Username", username)
        setUsername.apply()
    }

    override fun onStart() {
        super.onStart()
        if (GoogleSignIn.getLastSignedInAccount(this) != null || firebaseAuth.currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}