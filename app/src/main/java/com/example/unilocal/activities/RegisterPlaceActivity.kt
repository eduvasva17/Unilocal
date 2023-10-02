package com.example.unilocal.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.viewpager.widget.ViewPager
import com.example.unilocal.Adapter
import com.example.unilocal.ImagePicker
import com.example.unilocal.R
import com.example.unilocal.adapter.ImagePagerAdapter
import com.example.unilocal.databinding.ActivityRegisterPlaceBinding
import com.example.unilocal.db.Places
import com.example.unilocal.db.Schedules
import com.example.unilocal.db.Users
import com.example.unilocal.fragment.TimePickerFragment
import com.example.unilocal.model.Place
import com.example.unilocal.model.PlaceStatus
import com.example.unilocal.model.Schedule
import com.example.unilocal.model.User
import com.example.unilocal.ui.login.LoginActivity
import java.sql.Time
import java.util.*

class RegisterPlaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterPlaceBinding
    lateinit var viewPager: ViewPager
    lateinit var layouts:IntArray
    lateinit var adapter: Adapter
    lateinit var user:User
    var init:Boolean = true
    lateinit var btn_next: Button
    var selected_categorie:Int = 0
    var categorie_to_register:Int = 0
    lateinit var categorie_string: String
    val dots = mutableListOf<TextView>()
    lateinit var position_layout:LinearLayout
    var condition = false
    var idUser = 0



    //FORM 1 VARS
    lateinit var input_place_name:EditText
    lateinit var input_place_description:EditText
    lateinit var input_open_hour:EditText
    lateinit var input_close_hour:EditText
    lateinit var input_place_phone:EditText
    lateinit var input_place_secundary_phone:EditText
    lateinit var input_place_adress:EditText


    lateinit var place_name:String
    lateinit var place_description:String
    lateinit var open_hour:String
    lateinit var close_hour:String
    lateinit var place_phone:String
    lateinit var place_secundary_phone:String
    lateinit var place_adress:String

    //FORM 3 VARS
    lateinit var btn_hotel_categorie:Button
    lateinit var btn_coffe_categorie:Button
    lateinit var btn_restaurant_categorie:Button
    lateinit var btn_park_categorie:Button
    lateinit var btn_bar_categorie:Button

    //FORM 4 VARS
    var images = mutableListOf<Uri>()
    private lateinit var viewPager2: ViewPager
    private lateinit var imagePicker: ImagePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.formPager

        viewPager.offscreenPageLimit = 3
        layouts = intArrayOf(
            R.layout.activity_register_place_form_1,
            R.layout.activity_register_place_form_2,
            R.layout.activity_register_place_form_3,
            R.layout.activity_register_place_form_4
        )

        adapter = Adapter(this, layouts)
        viewPager.adapter = adapter


        btn_next = binding.Next

        imagePicker = ImagePicker(this)

        val sp = getSharedPreferences("sesion", Context.MODE_PRIVATE)
        idUser = sp.getInt("id_user", -1)

        btn_next.setOnClickListener{
            if( btn_next.text == getString(R.string.register_user_finish)){
                register()
            }else if (btn_next.text == getString(R.string.register_place_choose_photos)){

                imagePicker.pickImages()
            }else{
                nextListener()
            }
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {


            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if(init){
                    if(position == 0){
                        initVars()
                        initOnclickListeners()
                        init = false
                    }
                }
            }

            override fun onPageSelected(position: Int) {

                if(position == 1){
                    verifyForm1Inputs()
                }
                if(position==3) {
                    if(images.isEmpty()){
                        btn_next.text = getString(R.string.register_place_choose_photos)
                    }else{
                        btn_next.text = getString(R.string.register_user_finish)
                    }
                }else{
                    btn_next.text = getString(R.string.register_user_next)
                }
            }
            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    private fun register() {
        if(place_name.isNotEmpty() && place_description.isNotEmpty() && open_hour.isNotEmpty() && close_hour.isNotEmpty() && place_phone.isNotEmpty()
            && place_secundary_phone.isNotEmpty() && place_adress.isNotEmpty() && categorie_string.isNotEmpty() && images.isNotEmpty() && verifyRegexPhone()){

            if(verifyHours(open_hour, close_hour) && verifyPhones()){
                val placeRegister = Place(Places.size()+1, place_name, place_description, idUser, PlaceStatus.PENDING, categorie_to_register, place_adress, 45.545454f, -23.87867f, 1, 1, 1)
                placeRegister.phoneNumbers.add(place_phone)
                placeRegister.phoneNumbers.add(place_secundary_phone)
                placeRegister.images = images
                val only_open_hour = determinHour(open_hour)
                val only_close_hour = determinHour(close_hour)
                val schedule1 = Schedule(5, Schedules.getAll(),only_open_hour,only_close_hour)
                placeRegister.schedules.add(schedule1)
                Places.add(placeRegister)
                Toast.makeText(this, "Lugar registrado", Toast.LENGTH_SHORT).show()
                finish()
                goToMap()
            }else{
                Toast.makeText(this, "Asegurate de que todos los campos estén diligenciados correctamente", Toast.LENGTH_SHORT).show()
            }
        }else{
            verifyForm1Inputs()
            Toast.makeText(this, getString(R.string.register_user_msg_all_inpts_obligatories), Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToMap() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    private fun determinHour(hour:String): Int {
        val parts = hour.split(":")
        val open_hours = parts[0].toInt()
        return open_hours
    }

    /*private fun verifyDatesWithDb(): Boolean {
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
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ImagePicker.PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            images = mutableListOf()

            data?.let {
                val clipData = it.clipData
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        images.add(clipData.getItemAt(i).uri)
                    }
                } else {
                    it.data?.let { uri ->
                        images.add(uri)
                    }
                }
            }
            val adapter = ImagePagerAdapter(this, images)
            viewPager2.adapter = adapter
            btn_next.text = getString(R.string.register_user_finish)
            initDots()
        }
    }


    private fun verifyForm1Inputs(){
        getInputsText()
        verifyRegexPhone()
        /*verifyRegexEmail()
        verifyRegexPass()
        verifyDatesWithDb()*/
        if(place_phone.equals(place_secundary_phone)){
            input_place_secundary_phone.error = "No pude ser igual al otro número"
        }
        if(open_hour.isEmpty()){
            input_open_hour.error = getString(R.string.forgot_msg_obligatorie_inputs)
        }else{
            input_open_hour.error = null
        }
        if(close_hour.isEmpty()){
            input_close_hour.error = getString(R.string.forgot_msg_obligatorie_inputs)
        }else{
            input_close_hour.error = null
        }
        if(place_name.isEmpty()){
            input_place_name.error = getString(R.string.forgot_msg_obligatorie_inputs)
        }
        if(place_description.isEmpty()){
            input_place_description.error = getString(R.string.forgot_msg_obligatorie_inputs)
        }
        if(place_phone.isEmpty()){
            input_place_phone.error = getString(R.string.forgot_msg_obligatorie_inputs)
        }
        if(place_secundary_phone.isEmpty()){
            input_place_secundary_phone.error = getString(R.string.forgot_msg_obligatorie_inputs)
        }
        if(place_adress.isEmpty()){
            input_place_adress.error = getString(R.string.forgot_msg_obligatorie_inputs)
        }
    }

    private fun getInputsText() {
        //FORM 1 INPUTS
        place_name = input_place_name.text.toString()
        place_description = input_place_description.text.toString()
        open_hour = input_open_hour.text.toString()
        close_hour = input_close_hour.text.toString()
        place_phone = input_place_phone.text.toString()
        place_secundary_phone = input_place_secundary_phone.text.toString()
        place_adress = input_place_adress.text.toString()

        verifyHours(open_hour, close_hour)
    }

    private fun initOnclickListeners(){
        input_open_hour.setOnClickListener{
            showTimePickerDialog(input_open_hour)
        }
        input_close_hour.setOnClickListener{
            showTimePickerDialog(input_close_hour)
        }
        btn_hotel_categorie.setOnClickListener{
            changeButton(btn_hotel_categorie)
            categorie_string = "Hotel"
            categorie_to_register = 1
        }
        btn_coffe_categorie.setOnClickListener{
            changeButton(btn_coffe_categorie)
            categorie_string = "Café"
            categorie_to_register = 2
        }
        btn_restaurant_categorie.setOnClickListener{
            changeButton(btn_restaurant_categorie)
            categorie_string = "Restaurante"
            categorie_to_register = 3
        }
        btn_park_categorie.setOnClickListener{
            changeButton(btn_park_categorie)
            categorie_string = "Parque"
            categorie_to_register = 4
        }
        btn_bar_categorie.setOnClickListener{
            changeButton(btn_bar_categorie)
            categorie_string = "Bar"
            categorie_to_register = 5
        }

    }

    private fun initVars(){

        //FORM 1 VARS
        input_place_name = viewPager.findViewById(R.id.place_name)
        input_place_description = viewPager.findViewById(R.id.place_description)
        input_open_hour = viewPager.findViewById(R.id.place_open_hour)
        input_close_hour = viewPager.findViewById(R.id.place_close_hour)
        input_place_phone = viewPager.findViewById(R.id.place_phone)
        input_place_secundary_phone = viewPager.findViewById(R.id.place_secundary_phone)
        input_place_adress = viewPager.findViewById(R.id.place_adress)

        //FORM 2 VARS

        //FORM 3 VARS

        btn_hotel_categorie = viewPager.findViewById(R.id.btn_hotel)
        btn_coffe_categorie = viewPager.findViewById(R.id.btn_coffe)
        btn_restaurant_categorie = viewPager.findViewById(R.id.btn_restaurant)
        btn_park_categorie = viewPager.findViewById(R.id.btn_park)
        btn_bar_categorie = viewPager.findViewById(R.id.btn_bar)

        //FORM 4 VARS

        viewPager2 = viewPager.findViewById(R.id.form_pager_photos)
        position_layout = viewPager.findViewById(R.id.position_layout_place)
        initDots()
    }


    fun changeButton (btn:Button){
        if (btn.isSelected) {
            btn.setTextColor(resources.getColor(R.color.black))
            btn.isSelected = false
            selected_categorie--
        } else {
            if(selected_categorie == 1){
                Toast.makeText(this,"Solo puedes escoger una categoría",Toast.LENGTH_LONG).show()
            }else{
                btn.setTextColor(resources.getColor(R.color.white))
                btn.isSelected = true
                selected_categorie++
            }
        }
    }

    fun nextListener (){
        if (viewPager.currentItem+1 < layouts.size){
            viewPager.setCurrentItem(viewPager.currentItem+1)
        }
    }

    private fun showTimePickerDialog(view: EditText) {
        val timePicker = TimePickerFragment {onTimeSelected(it, view)}
        timePicker.show(supportFragmentManager, "time")
    }

    private fun onTimeSelected (time:String, view:EditText){{}
        view.setText("$time")
        verifyForm1Inputs()
    }

    private fun verifyRegexPhone(): Boolean {
        if(place_phone.length == 10 && place_secundary_phone.length == 10){
            return true
        }else{
            if(place_phone.length != 10){
                input_place_phone.error = getString(R.string.register_user_msg_invalid_phone)
            }
            if(place_secundary_phone.length != 10){
                input_place_secundary_phone.error = getString(R.string.register_user_msg_invalid_phone)
            }
            return false
        }
    }

    private fun verifyPhones(): Boolean {
        if(place_phone.equals(place_secundary_phone)){
            input_place_secundary_phone.error = "No pude ser igual al otro número"
            return false
        }else{
            return true
        }
    }

    private fun verifyHours(hour_open:String, hour_close:String): Boolean {
        if(hour_open.isNotEmpty() && hour_close.isNotEmpty()){
            val only_open_hour = determinHour(hour_open)
            val only_close_hour = determinHour(hour_close)
            if(only_open_hour > only_close_hour){
                Toast.makeText(this,"La hora de apertura no puede ser mayor a la del cierre",Toast.LENGTH_LONG).show()
                return false
            }
            return true
        }
        return false
    }

    private fun initDots() {
            for (i in 0 until (viewPager2.adapter?.count ?: 0)) {1
                val dot = TextView(this)
                dot.text = "•"
                dot.textSize = 30f
                dot.setTextColor(ContextCompat.getColor( this, R.color.inactive))
                dot.setOnClickListener { viewPager2.currentItem = i }
                dots.add(dot)
                position_layout.addView(dot)
                condition = true
            }
        if(dots.isNotEmpty()){
            dots.first().setTextColor(ContextCompat.getColor(this, R.color.active))
        }
    }
}