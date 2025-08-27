package com.google.mediapipe.examples.gesturerecognizer.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.mediapipe.examples.gesturerecognizer.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnMasuk = findViewById<Button>(R.id.btn_masuk)
        val btnTanpa = findViewById<Button>(R.id.btn_tanpa_login)

        btnMasuk.setOnClickListener {
            val intent = Intent(this, com.google.mediapipe.examples.gesturerecognizer.ui.HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnTanpa.setOnClickListener {
            val intent = Intent(this, com.google.mediapipe.examples.gesturerecognizer.ui.HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
