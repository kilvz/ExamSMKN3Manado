package com.kilv.examsmkn3manado

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Toast
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var roomSpinner: Spinner
    private lateinit var formSpinner: Spinner
    private lateinit var selectButton: Button
    private lateinit var codeInput: EditText

    // List of rooms
    private val rooms = listOf("Kelas X", "Kelas XI", "LOGIN")

    // Map of room forms (URLs)
    private val roomForms = mapOf(
        "Kelas X" to listOf(
            "https://docs.google.com/forms/d/e/1FAIpQLScJqJKVGTyUM0-Ky_iuImJgm74HUWcr1QLGI11d8bfJL8P0og/viewform?usp=sf_link",
            "https://docs.google.com/forms/d/e/1FAIpQLSeYY7YHDKzCqAhwOaanc5p_M9FSA8iNCK31pkUSuTd-LrgkgA/viewform?usp=sf_link",
            "https://docs.google.com/forms/d/e/1FAIpQLScp2KE-KjE6I48FLTd5WCIdEE3oCmW56is3HubN20NPViO2bA/viewform?usp=sf_link",
            "https://docs.google.com/forms/d/e/1FAIpQLSf1NBjBGwgHlBOqpB2-vUg9Hx2RZmSahoztsNq1IjYgsKcCCA/viewform?usp=sf_link",
            "https://docs.google.com/forms/d/e/1FAIpQLSfQaLUeKrEKuTRGQ1BclCpzWke3waKcEPvOyZjx3E7COctN2g/viewform?usp=sf_link"
        ),
        "Kelas XI" to listOf(
            "https://docs.google.com/forms/d/e/1FAIpQLSeihAZKR8V77DdgrpzGGZmmmcS0Xg-2xaVzqqEuY-JlBFjwdg/viewform?usp=sf_link",
            "https://docs.google.com/forms/d/e/1FAIpQLScfg1Ji5jTUhup46QwIJcaVcYUv1NUctpIKnnZbq4VHFTrr1A/viewform?usp=sf_link",
            "https://docs.google.com/forms/d/e/1FAIpQLSdoac1O-1UWmswacbASAxWpTdAcPtzKphQL4HAxi5fJkiVs1A/viewform?usp=sf_link",
            "https://docs.google.com/forms/d/e/1FAIpQLSfRU-NWeloaGT0QJiXoB3HlT8SmNZjCMDlNC5BEIlQhtkXEww/viewform?usp=sf_link",
            "https://docs.google.com/forms/d/e/1FAIpQLSc9RWuurXjo76--qSb8sVSXzLJkYhQHl_C6oGssLxkxkYAddA/viewform?usp=sf_link"
        ),
        "LOGIN" to listOf(
            "https://accounts.google.com/login"
        )
    )

    // Titles for forms
    private val formTitles = mapOf(
        "Kelas X" to listOf("Pendidikan Agama Islam", "Pendidikan Agama Kristen", "Pendidikan Agama Katolik", "Pendidikan Pancasila", "Bahasa Indonesia"),
        "Kelas XI" to listOf("Pendidikan Agama Islam", "Pendidikan Agama Kristen", "Pendidikan Agama Katolik", "Pendidikan Pancasila", "Bahasa Indonesia"),
        "LOGIN" to listOf("DIMOHON UNTUK MENGGANTI PASSWORD 12345678")
    )

    private val disableTimeCheckCode = "DISABLE12" // Set your special code here

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        // Prevent screenshots
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        // Prevent the screen from turning off
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // Full screen mode
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        webView = findViewById(R.id.webview)
        roomSpinner = findViewById(R.id.room_spinner)
        formSpinner = findViewById(R.id.form_spinner)
        selectButton = findViewById(R.id.select_button)
        codeInput = findViewById(R.id.code_input)

        formSpinner.visibility = View.GONE // Hide the form spinner initially
        selectButton.visibility = View.GONE // Hide the select button initially


        webView.webViewClient = WebViewClient() // Prevents opening the link in a browser
        webView.settings.javaScriptEnabled = true

        // Set up the room spinner
        val roomAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, rooms)
        roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roomSpinner.adapter = roomAdapter

        roomSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedRoom = rooms[position]
                updateFormSpinner(selectedRoom)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        formSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectButton.visibility = View.VISIBLE // Show the select button after selecting a room
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        selectButton.setOnClickListener {
            val selectedRoom = roomSpinner.selectedItem as String
            val selectedSubject = formSpinner.selectedItem as String
            val enteredCode = codeInput.text.toString().trim()

            // Check if the entered code matches the disable code
            if (enteredCode == disableTimeCheckCode) {
                // Bypass the time check
                val selectedFormUrl = roomForms[selectedRoom]!![ formSpinner.selectedItemPosition]
                loadForm(selectedFormUrl)

                // Hide both spinners and the button after a form is selected
                roomSpinner.visibility = View.GONE
                formSpinner.visibility = View.GONE
                selectButton.visibility = View.GONE
                codeInput.visibility = View.GONE

            } else {
                // Perform time check
                if (!isWithinAllowedTime(selectedSubject)) {
                    Toast.makeText(this, "Maaf, Soal Ujian $selectedSubject tidak tersedia untuk saat ini.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val selectedFormUrl = roomForms[selectedRoom]!![formSpinner.selectedItemPosition]
                loadForm(selectedFormUrl)

                // Hide both spinners and the button after a form is selected
                roomSpinner.visibility = View.GONE
                formSpinner.visibility = View.GONE
                selectButton.visibility = View.GONE
                codeInput.visibility = View.GONE
            }
        }
    }

    override fun onPause() {
        super.onPause()
        finish() // Close the activity when it goes to the background
    }

    private fun updateFormSpinner(selectedRoom: String) {
        val formTitlesList = formTitles[selectedRoom] ?: emptyList()
        val formAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, formTitlesList)
        formAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        formSpinner.adapter = formAdapter
        formSpinner.visibility = View.VISIBLE // Show the form spinner after selecting a room
    }

    private fun loadForm(url: String) {
        webView.loadUrl(url)
    }

    private fun isWithinAllowedTime(subject: String): Boolean {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)
        return when (subject) {
            "Pendidikan Agama Islam" -> (currentHour == 8) || (currentHour == 9 && currentMinute <= 30) // Allowed from 8 AM to 9:30 AM
            "Pendidikan Agama Kristen" -> (currentHour == 8) || (currentHour == 9 && currentMinute <= 30) // Allowed from 8 AM to 9:30 AM
            "Pendidikan Agama Katolik" -> (currentHour == 8) || (currentHour == 9 && currentMinute <= 30) // Allowed from 8 AM to 9:30 AM
            "Pendidikan Pancasila" -> (currentHour == 9 && currentMinute >= 30) || (currentHour == 10) // Allowed from 9:30 AM to 11 AM
            "Bahasa Indonesia" -> (currentHour == 12) || (currentHour == 13 && currentMinute <= 30) // Allowed from 12 PM to 1:30 PM
            else -> true // All other subjects are allowed at any time
        }
    }
}