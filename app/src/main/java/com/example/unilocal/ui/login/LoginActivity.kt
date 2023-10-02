package com.example.unilocal.ui.login

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.example.unilocal.databinding.ActivityLoginBinding

import com.example.unilocal.R
import com.example.unilocal.activities.*
import com.example.unilocal.db.People
import com.example.unilocal.db.Users
import com.example.unilocal.model.Administrator
import com.example.unilocal.model.City
import com.example.unilocal.model.Moderator
import com.example.unilocal.model.User


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sp = getSharedPreferences("sesion", Context.MODE_PRIVATE)

        val email = sp.getString("email_user", "")
        val type = sp.getString("type_user", "")

        if(email!!.isNotEmpty() && type!!.isNotEmpty()){
            Log.e("LoginActivity", "entro $type")
            when(type){
                "user" -> startActivity(Intent(this, MapActivity::class.java))
                "moderator" -> startActivity(Intent((this), ModeratorActivity::class.java))
            }
            finish()
        } else
        {
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)


            //val loading = binding.loading


            binding.forgot.setOnClickListener{goToForgotPass()}
            binding.registerNow.setOnClickListener{goToRegister()}
            binding.btnLogin.setOnClickListener { login() }


            binding.registerNow.setOnClickListener{
                val intent = Intent(this, RegisterActivity::class.java)
                intent.putExtra("object", "(Aqui el objeto que se pasará)")
                startActivity(intent)
            }
        }

    }

    private fun filter(){
        val filter = InputFilter { source, _, _, _, _, _ ->
            val regex = Regex("[a-zA-Z]+")
            if (source.toString().matches(regex)) {
                source
            } else {
                ""
            }
        }
    }

    private fun login(){
        var input_email = binding.email
        var input_pass = binding.password

        var email = input_email.text.toString()
        var password = input_pass.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){

            val person = People.login(email.toString(), password.toString())

            val user = Users.findByEmail(email.toString())

            if(person != null){
                val type = if(person is User) "user" else if (person is Moderator) "moderator" else "admin"
                val sharedPreferences = this.getSharedPreferences("sesion", Context.MODE_PRIVATE).edit()

                sharedPreferences.putInt("id_user", person.id)
                sharedPreferences.putString("email_user", person.email)
                sharedPreferences.putString("type_user", type)

                sharedPreferences.commit()
                when(person){
                    is User -> goToMap()
                    is Moderator -> startActivity(Intent(this, ModeratorActivity::class.java))
                    is Administrator -> startActivity(Intent(this, AdminActivity::class.java))
                }
                finish()

            }else if(person == null) {
                input_email.error = getString(R.string.login_msg_user_do_not_exist)
            }else{
                Toast.makeText(this,getString(R.string.login_msg_pass_do_not_match),Toast.LENGTH_LONG).show()
            }

        }else{
            if(email.isEmpty()){
                input_email.error = getString(R.string.forgot_invalid_email)
            }
            Toast.makeText(this,getString(R.string.register_user_msg_all_inpts_obligatories),Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    fun goToMap(){
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    fun goToDetailPlace(){
        val intent = Intent(this, DetailPlaceActivity::class.java)
        startActivity(intent)
    }

    fun goToWall(){
        val intent = Intent(this, WallActivity::class.java)
        startActivity(intent)
    }

    fun goToRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun goToForgotPass(){
        val intent = Intent(this, ForgotPassActivity::class.java)
        startActivity(intent)
    }

    /* fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Cambia el color del texto a medida que el usuario escribe
        if (s.toString().isEmpty()) {
            myEditText.setTextColor(Color.GRAY) // Cambia el color del texto a gris si el texto está vacío
        } else {
            myEditText.setTextColor(Color.BLACK) // Cambia el color del texto a negro si el texto no está vacío
        }
    }*/
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })


}