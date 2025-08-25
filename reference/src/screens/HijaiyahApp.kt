package com.<your>.<application>
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.bumptech.glide.Glide
class HijaiyahApp : AppCompatActivity() {
	private var editTextValue1: String = ""
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_hijaiyah_app)
		Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/V3lJW2VieK/6274lto4_expires_30_days.png").into(findViewById(R.id.rnt1lusrcl1))
		Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/V3lJW2VieK/dclrxniv_expires_30_days.png").into(findViewById(R.id.rfi6s2p3vtal))
		val editText1: EditText = findViewById(R.id.rpkrfbsvwzm)
		editText1.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
				// before Text Changed
			}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
				editTextValue1 = s.toString()  // on Text Changed
			}
			override fun afterTextChanged(s: Editable?) {
				// after Text Changed
			}
		})
		val button1: View = findViewById(R.id.r8hdq98d5tjj)
		button1.setOnClickListener {
			println("Pressed")
		}
		val button2: View = findViewById(R.id.rdm00exahgzn)
		button2.setOnClickListener {
			println("Pressed")
		}
		val button3: View = findViewById(R.id.r7fl38cpy199)
		button3.setOnClickListener {
			println("Pressed")
		}
		val button4: View = findViewById(R.id.rj2kf9rnvnz)
		button4.setOnClickListener {
			println("Pressed")
		}
		val button5: View = findViewById(R.id.r2z8zmy9ttq6)
		button5.setOnClickListener {
			println("Pressed")
		}
		val button6: View = findViewById(R.id.rijj1o9gor2)
		button6.setOnClickListener {
			println("Pressed")
		}
		val button7: View = findViewById(R.id.rzvv0zlk1qg)
		button7.setOnClickListener {
			println("Pressed")
		}
		val button8: View = findViewById(R.id.rj0sfnkuj97p)
		button8.setOnClickListener {
			println("Pressed")
		}
		val button9: View = findViewById(R.id.rfisdci7xxxp)
		button9.setOnClickListener {
			println("Pressed")
		}
		val button10: View = findViewById(R.id.rgbq3y1ek0b5)
		button10.setOnClickListener {
			println("Pressed")
		}
	}
}