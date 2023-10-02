package com.example.unilocal.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.media.Image
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.unilocal.Adapter
import com.example.unilocal.R
import com.example.unilocal.databinding.ActivityRegisterBinding
import com.example.unilocal.databinding.ActivityRegisterFormUser1Binding
import com.example.unilocal.databinding.ActivityRegisterFormUser2Binding
import com.example.unilocal.databinding.ActivityRegisterFormUser3Binding
import com.example.unilocal.db.Users
import com.example.unilocal.model.User
import com.example.unilocal.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {



    lateinit var viewPager: ViewPager
    lateinit var layouts:IntArray
    lateinit var adapter: Adapter
    private lateinit var binding: ActivityRegisterBinding
    lateinit var binding_form_1: ActivityRegisterFormUser1Binding
    lateinit var binding_form_2: ActivityRegisterFormUser2Binding
    lateinit var binding_form_3: ActivityRegisterFormUser3Binding
    lateinit var imageUrl:String
    val dots = mutableListOf<TextView>()
    lateinit var btn_next:Button
    var init:Boolean = true

    //FORM REGISTER 1
    lateinit var input_names:EditText
    lateinit var input_last_names:EditText
    lateinit var input_email:EditText
    lateinit var input_user:EditText
    lateinit var input_pass:EditText

    lateinit var names:String
    lateinit var last_names:String
    lateinit var email:String
    lateinit var user:String
    lateinit var pass:String

    //FORM REGISTER 2

    lateinit var input_phone:EditText
    lateinit var input_country:EditText
    lateinit var input_department:EditText
    lateinit var input_city:EditText
    lateinit var input_age:EditText

    lateinit var phone:String
    lateinit var country:String
    lateinit var department:String
    lateinit var city:String
    lateinit var age:String
    lateinit var terms:CheckBox



    lateinit var img:ImageView

    var REQUEST_CODE_IMAGE_PICKER:Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        binding_form_1 = ActivityRegisterFormUser1Binding.inflate(layoutInflater)
        binding_form_2 = ActivityRegisterFormUser2Binding.inflate(layoutInflater)
        binding_form_3 = ActivityRegisterFormUser3Binding.inflate(layoutInflater)
        setContentView(binding.root)


        viewPager = binding.formPager
        viewPager.offscreenPageLimit = 3

        layouts = intArrayOf(
            R.layout.activity_register_form_user_1,
            R.layout.activity_register_form_user_2,
            R.layout.activity_register_form_user_3

        )
        adapter = Adapter(this, layouts)
        viewPager.adapter = adapter

        initDots()
        imageUrl = ""

        btn_next = binding.Next
        btn_next.setOnClickListener{
            if( btn_next.text == getString(R.string.register_user_finish)){
                register()
            }else if (btn_next.text == getString(R.string.register_user_choose_photo)){
                askImages()
            }else{
                nextListener()
            }
        }


        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    if(init){
                        if(position == 0){
                            initVars()
                            init = false
                        }
                    }
                }

            override fun onPageSelected(position: Int) {
                dots.forEachIndexed { index, dot ->
                    dot.setTextColor(
                        ContextCompat.getColor( this@RegisterActivity,
                            if (index == position) R.color.active else R.color.inactive
                        )
                    )
                }
                if(position == 1){
                    verifyForm1Inputs()
                }
                if(position==2) {
                    verifyForm2Inputs()
                    if(imageUrl == ""){
                        btn_next.text = getString(R.string.register_user_choose_photo)
                    }else{
                        btn_next.text = getString(R.string.register_user_finish)
                    }
                }else{
                    btn_next.text = getString(R.string.register_user_next)
                }
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun register() {
        if(names.isNotEmpty() && last_names.isNotEmpty() && email.isNotEmpty() && user.isNotEmpty() && pass.isNotEmpty()
            && phone.isNotEmpty() && country.isNotEmpty() && department.isNotEmpty() && city.isNotEmpty() && verifyRegexAge()
            && imageUrl != ""){
                if(verifyRegexEmail() && verifyRegexPass() && verifyRegexAge() && verifyRegexPhone()){
                    if(verifyDatesWithDb()){
                        val userRegister = User(Users.size()+1, names, last_names, email, user, pass, 1, 1, 1, age.toInt(), imageUrl, phone)
                        Users.add(userRegister)
                        Toast.makeText(this, getString(R.string.register_user_msg_user_registered), Toast.LENGTH_SHORT).show()
                        finish()
                        goToLogIn()
                    }else{
                        if(terms.isChecked == false){
                            Toast.makeText(this, getString(R.string.register_user_msg_terms_not_checked), Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this, "Asegurate de que todos los campos estén diligenciados correctamente", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
        }else{
            verifyForm1Inputs()
            verifyForm2Inputs()
            Toast.makeText(this, getString(R.string.register_user_msg_all_inpts_obligatories), Toast.LENGTH_SHORT).show()
        }
    }

    private fun verifyRegexPhone(): Boolean {
        if(phone.length == 10){
            return true
        }else{
            input_phone.error = getString(R.string.register_user_msg_invalid_phone)
            return false
        }
    }


    private fun verifyRegexAge(): Boolean {
        if(age.isNotEmpty()){
            if(age.toInt() in 18..110){
                return true
            }else{
                input_age.error = getString(R.string.register_user_msg_invalid_age)
            }
        }
        return false
    }

    private fun verifyRegexEmail(): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})".toRegex()
        if(email.matches(emailRegex)){
            return true
        }else{
            input_email.error = getString(R.string.forgot_invalid_email)
            return false
        }
    }

    private fun verifyRegexPass(): Boolean {
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}\$".toRegex()
        if(pass.matches(passwordRegex)){
            return true
        }else{
            input_pass.error = getString(R.string.login_msg_pass_required)
            return false
        }
    }

    private fun verifyDatesWithDb(): Boolean {
        if(Users.findByEmail(email) != null){
            input_email.error = getString(R.string.register_user_msg_email_exists)
            return false
        }else if(Users.findByUsername(user) != null){
            input_user.error = getString(R.string.register_user_msg_username_exists)
            return false
        }else if(Users.findByPhone(phone) != null){
            input_phone.error = getString(R.string.register_user_msg_phone_exists)
            return false
        }else if(terms.isChecked == false){
            return false
        }
        return true
    }

    private fun verifyForm1Inputs() {
        getInputsText()
        verifyRegexEmail()
        verifyRegexPass()
        verifyDatesWithDb()
        if(names.isEmpty()){
            input_names.error = getString(R.string.forgot_msg_obligatorie_inputs)
        }
        if(last_names.isEmpty()){
            input_last_names.error = getString(R.string.forgot_msg_obligatorie_inputs)
        }
        if(email.isEmpty()){
            input_email.error = getString(R.string.forgot_msg_obligatorie_inputs)
        }
        if(user.isEmpty()){
            input_user.error = getString(R.string.forgot_msg_obligatorie_inputs)
        }
        if(pass.isEmpty()){
            input_pass.error = getString(R.string.forgot_msg_obligatorie_inputs)
        }

    }

    private fun verifyForm2Inputs() {
        getInputsText()
        verifyRegexPhone()
        verifyRegexAge()
        verifyDatesWithDb()
        if(phone.isEmpty()){
            input_phone.error = getString(R.string.forgot_msg_obligatorie_inputs)
        }
        if(country.isEmpty()){
            input_country.error = getString(R.string.forgot_msg_obligatorie_inputs)
        }
        if(department.isEmpty()){
            input_department.error = getString(R.string.forgot_msg_obligatorie_inputs)
        }
        if(city.isEmpty()){
            input_city.error = getString(R.string.forgot_msg_obligatorie_inputs)
        }
        if(age.isEmpty()){
            input_age.error = getString(R.string.forgot_msg_obligatorie_inputs)
        }
    }

    private fun verifyForm3Photo() {
        initVars()
    }



    private fun initVars() {
        //FORM REGISTER 1
        input_names = viewPager.findViewById(R.id.user_name)
        input_last_names = viewPager.findViewById(R.id.user_last_names)
        input_email = viewPager.findViewById(R.id.user_email)
        input_user = viewPager.findViewById(R.id.user_username)
        input_pass = viewPager.findViewById(R.id.user_pass)

        //FORM REGISTER 2

        input_phone = viewPager.findViewById(R.id.user_phone)
        input_country = viewPager.findViewById(R.id.user_country)
        input_department = viewPager.findViewById(R.id.user_department)
        input_city = viewPager.findViewById(R.id.user_city)
        input_age = viewPager.findViewById(R.id.user_age)

        //FORM REGISTER 3

        img = binding.formPager.findViewById(R.id.choose_img)
    }

    private fun getInputsText() {
        //FORM 1 INPUTS
        names = input_names.text.toString()
        last_names = input_last_names.text.toString()
        email = input_email.text.toString()
        user = input_user.text.toString()
        pass = input_pass.text.toString()


        //FORM 2 INPUTS

        phone = input_phone.text.toString()
        country = input_country.text.toString()
        department = input_department.text.toString()
        city = input_city.text.toString()
        age = input_age.text.toString()
        terms = viewPager.findViewById(R.id.user_terms)

    }

    fun goToLogIn(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun nextListener (){
        if (viewPager.currentItem+1 < layouts.size){
            viewPager.setCurrentItem(viewPager.currentItem+1)
        }
    }

    fun askImages (){
        requestPermission()
    }

    private fun requestPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    pickPhotoFromGallery()
                }

                else -> requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            pickPhotoFromGallery()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted ->

        if(isGranted){
            pickPhotoFromGallery()
        }else{
            Toast.makeText(this, getString(R.string.register_user_msg_allow_permissions), Toast.LENGTH_SHORT).show()
        }

    }


    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICKER && resultCode == Activity.RESULT_OK) {
            btn_next.text = getString(R.string.register_user_finish)
            var selectedImageUri = data?.data ?: return
            imageUrl = selectedImageUri.toString()
            img.setImageURI(selectedImageUri)
            img.scaleType = ImageView.ScaleType.FIT_CENTER
            img.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
        }
    }

    private fun initDots() {
        for (i in 0 until (viewPager.adapter?.count ?: 0)) {
            val dot = TextView(this)
            dot.text = "•"
            dot.textSize = 30f
            dot.setTextColor(ContextCompat.getColor( this, R.color.inactive))
            dot.setOnClickListener { viewPager.currentItem = i }
            dots.add(dot)
            binding.positionLayout.addView(dot)
        }
        dots.first().setTextColor(ContextCompat.getColor(this, R.color.active))
    }

    /*private val startForActivityGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result ->

        if(result.resultCode == RESULT_OK){
            val data = result.data?.data
            imageUrl = data.toString()
            btn_next.text = getString(R.string.register_user_finish)
            img.setImageURI(data)
            img.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            img.scaleType = ImageView.ScaleType.FIT_CENTER
        }

    }*/

    //private fun pickPhotoFromGallery() {
      //  val intent = Intent (Intent.ACTION_GET_CONTENT)
        //intent.type = "image/*"
     //   startForActivityGallery.launch(intent)
    //}



}


