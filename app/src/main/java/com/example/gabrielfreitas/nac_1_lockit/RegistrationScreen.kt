package com.example.gabrielfreitas.nac_1_lockit

import android.app.ProgressDialog
import android.content.Intent
import android.widget.EditText
import android.widget.Button
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistrationScreen : AppCompatActivity() {

    //UI elements
    private var etFirstName: EditText? = null
    private var etLastName: EditText? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnCreateAccount: Button? = null
    private var btnVoltar: Button? = null
    private var mProgressBar: ProgressDialog? = null

    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private val TAG = "CreateAccountActivity"
    //global variables
    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_screen)

        initialise()
    }

    private fun initialise() {
        etFirstName = findViewById<View>(R.id.et_first_name) as? EditText
        etLastName = findViewById<View>(R.id.et_last_name) as? EditText
        etEmail = findViewById<View>(R.id.et_email) as? EditText
        etPassword = findViewById<View>(R.id.et_password) as? EditText
        btnCreateAccount = findViewById<View>(R.id.btn_register) as? Button
        btnVoltar = findViewById<View>(R.id.btn_voltar) as? Button
        mProgressBar = ProgressDialog(this)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()
        btnCreateAccount!!.setOnClickListener { createNewAccount() }
        btnVoltar!!.setOnClickListener { updateUserInfoAndUI() }
    }

    private fun createNewAccount() {
        firstName = etFirstName?.text.toString()
        lastName = etLastName?.text.toString()
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()

        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)
                && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mProgressBar!!.setMessage("Registrando Usuário...")
            mProgressBar!!.show()

            mAuth!!
                    .createUserWithEmailAndPassword(email!!, password!!)
                    .addOnCompleteListener(this) { task ->
                        mProgressBar!!.hide()
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val userId = mAuth!!.currentUser!!.uid
                            //Verify Email
                            verifyEmail();
                            //update user profile information
                            val currentUserDb = mDatabaseReference!!.child(userId)
                            currentUserDb.child("firstName").setValue(firstName)
                            currentUserDb.child("lastName").setValue(lastName)
                            updateUserInfoAndUI()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(this, "Falha de Autenticação.",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
        } else {
            Toast.makeText(this, "Coloque todos os detalhes", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateUserInfoAndUI() {
        //start next activity
        val intent = Intent(this, LogInScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun verifyEmail() {
        val mUser = mAuth!!.currentUser;
        mUser!!.sendEmailVerification()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,
                                "Verificação de email enviada para " + mUser.getEmail(),
                                Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.exception)
                        Toast.makeText(this,
                                "Falha ao enviar email de autenticação",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

}