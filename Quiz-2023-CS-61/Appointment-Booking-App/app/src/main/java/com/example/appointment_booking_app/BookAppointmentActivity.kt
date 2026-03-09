package com.example.appointment_booking_app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class BookAppointmentActivity : AppCompatActivity() {

    var selectedDate = ""
    var selectedTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_appointment)

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val spinnerType = findViewById<Spinner>(R.id.spinnerType)
        val btnSelectDate = findViewById<Button>(R.id.btnSelectDate)
        val btnSelectTime = findViewById<Button>(R.id.btnSelectTime)
        val rgGender = findViewById<RadioGroup>(R.id.rgGender)
        val cbTerms = findViewById<CheckBox>(R.id.cbTerms)
        val btnConfirmBooking = findViewById<Button>(R.id.btnConfirmBooking)

        cbTerms.setOnCheckedChangeListener { _, isChecked ->
            btnConfirmBooking.isEnabled = isChecked
        }

        val appointmentTypes = arrayOf("Select Department...", "Cardiology", "Dermatology", "Neurology", "Orthopedics", "Pediatrics", "Psychiatry")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, appointmentTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter

        btnSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                btnSelectDate.text = selectedDate
            }, year, month, day)
            datePickerDialog.show()
        }

        btnSelectTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val amPm = if (selectedHour >= 12) "PM" else "AM"
                val formattedHour = if (selectedHour == 0) 12 else if (selectedHour > 12) selectedHour - 12 else selectedHour
                val formattedMinute = String.format(Locale.getDefault(), "%02d", selectedMinute)
                selectedTime = "$formattedHour:$formattedMinute $amPm"
                btnSelectTime.text = selectedTime
            }, hour, minute, false)
            timePickerDialog.show()
        }

        btnConfirmBooking.setOnClickListener {
            val name = etFullName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val type = spinnerType.selectedItem.toString()
            
            val selectedGenderId = rgGender.checkedRadioButtonId
            val genderButton = findViewById<RadioButton>(selectedGenderId)
            val gender = genderButton.text.toString()

            if (name.isEmpty() || name.length < 3) {
                etFullName.error = "Enter a valid full name"
            } else if (phone.isEmpty() || phone.length < 10) {
                etPhone.error = "Enter a valid phone number (min 10 digits)"
            } else if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Enter a valid email address"
            } else if (type == "Select Department...") {
                Toast.makeText(this, "Please select a department", Toast.LENGTH_SHORT).show()
            } else if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Please select an appointment date", Toast.LENGTH_SHORT).show()
            } else if (selectedTime.isEmpty()) {
                Toast.makeText(this, "Please select an appointment time", Toast.LENGTH_SHORT).show()
            } else if (!cbTerms.isChecked) {
                Toast.makeText(this, "Please accept Terms and Conditions", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, ConfirmationActivity::class.java)
                intent.putExtra("USER_NAME", name)
                intent.putExtra("USER_PHONE", phone)
                intent.putExtra("USER_EMAIL", email)
                intent.putExtra("APPT_TYPE", type)
                intent.putExtra("APPT_DATE", selectedDate)
                intent.putExtra("APPT_TIME", selectedTime)
                intent.putExtra("USER_GENDER", gender)
                
                startActivity(intent)
            }
        }
    }
}
