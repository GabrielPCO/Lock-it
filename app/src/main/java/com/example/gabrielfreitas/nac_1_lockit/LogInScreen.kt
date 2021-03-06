package com.example.gabrielfreitas.nac_1_lockit

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login_screen.*

class LogInScreen: AppCompatActivity() {

    private val TAG = "LoginActivity"
    //global variables
    private var email: String? = null
    private var password: String? = null
    private var etConnected: Boolean? = false

    //UI elements
    private var tvForgotPassword: TextView? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnLogin: Button? = null
    private var btnCreateAccount: Button? = null
    //Firebase references
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        val imageButton = findViewById<ImageButton>(R.id.btn_voltar)
        imageButton?.setOnClickListener { updateUI() }

        initialise()
    }

    private fun initialise() {
        tvForgotPassword = findViewById<View>(R.id.tv_forgot_password) as TextView
        etEmail = findViewById<View>(R.id.et_email) as EditText
        etPassword = findViewById<View>(R.id.et_password) as EditText
        btnLogin = findViewById<View>(R.id.btn_login) as Button
        btnCreateAccount = findViewById<View>(R.id.btn_register_account) as Button
        //mProgressBar = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        tvForgotPassword!!
                .setOnClickListener { startActivity(Intent(this,
                        ForgotPassword::class.java)) }
        btnCreateAccount!!
                .setOnClickListener { startActivity(Intent(this,
                        RegistrationScreen::class.java)) }
        btnLogin!!.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progress.visibility = ProgressBar.VISIBLE
            Log.d(TAG, "Logging in user.")
            mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                    .addOnCompleteListener(this) { task ->
                        progress.visibility = ProgressBar.INVISIBLE
                        //btn_voltar.visibility = View.VISIBLE
                        if (task.isSuccessful) {
                            // Sign in success, update UI with signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            val intent5 = Intent(this, MapsActivity::class.java)
                            intent5.putExtra("etConnected",true)
                            etConnected=true
                            intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            Log.d("LogInScreen","TERMINADO")
                            startActivity(intent5)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(this, "Falha de Autenticação.",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
        } else {
            Toast.makeText(this, "Preencha todas as linhas", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateUI() {
        val intent = Intent(this, MapsActivity::class.java)
        if(etConnected==true){
            intent.putExtra("etConnected",true)
        }
        startActivity(intent)
    }

}

