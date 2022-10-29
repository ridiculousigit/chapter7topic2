package binar.academy.chapter7topic2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import binar.academy.chapter7topic2.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.registerButton.setOnClickListener {
            val email = binding.registerEmail.text.toString()
            val pass = binding.registerPassword.text.toString()
            val con = binding.registerConfirm.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && con.isNotEmpty()) {
                if (pass == con) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "Akun berhasil terbuat !", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Pembuatan akun gagal !", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Cek kembali password yang dimasukkan !", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Wajib mengisi semua kolom !", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerSubHeader.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }
}