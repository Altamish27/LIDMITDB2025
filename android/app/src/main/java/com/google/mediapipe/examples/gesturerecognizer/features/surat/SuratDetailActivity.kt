package com.google.mediapipe.examples.gesturerecognizer.features.surat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R

class SuratDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surat_detail)

        val title = intent.getStringExtra("surat_title") ?: "Surat"
        val tvTitle = findViewById<TextView>(R.id.tv_surat_title)
        val tvSub = findViewById<TextView>(R.id.tv_surat_sub)
        val rv = findViewById<RecyclerView>(R.id.rv_ayat_list)

        tvTitle.text = title
        if (title == "Al-Fatihah") {
            tvSub.text = "Jumlah Ayat: 7    Juz: 30"
            val ayatList = buildAlFatihahList()
            rv.layoutManager = LinearLayoutManager(this)
            rv.adapter = AyatAdapter(ayatList)
        } else {
            tvSub.text = "Konten belum tersedia"
        }
    }

    private fun buildAlFatihahList(): List<Ayat> {
        return listOf(
            Ayat(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "Dengan nama Allah Yang Maha Pengasih lagi Maha Penyayang."),
            Ayat(2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Segala puji bagi Allah, Tuhan seluruh alam."),
            Ayat(3, "الرَّحْمَٰنِ الرَّحِيمِ", "Yang Maha Pengasih, Maha Penyayang."),
            Ayat(4, "مَالِكِ يَوْمِ الدِّينِ", "Pemilik hari pembalasan."),
            Ayat(5, "إِيَّاكَ نَعْبُدُ وإِيَّاكَ نَسْتَعِينُ", "Hanya kepada Engkaulah kami menyembah dan hanya kepada Engkaulah kami mohon pertolongan."),
            Ayat(6, "اهدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Tunjukilah kami jalan yang lurus,"),
            Ayat(7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "(yaitu) jalan orang-orang yang telah Engkau anugerahkan nikmat kepada mereka; bukan (jalan) mereka yang dimurkai, dan bukan (pula jalan) mereka yang sesat.")
        )
    }

    data class Ayat(val number: Int, val arab: String, val terjemahan: String)

    inner class AyatAdapter(private val items: List<Ayat>) : RecyclerView.Adapter<AyatAdapter.VH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_ayat, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val a = items[position]
            holder.tvNumber.text = a.number.toString()
            holder.tvArab.text = a.arab
            holder.tvTerjemah.text = a.terjemahan
            // Load isyarat image from assets if available (gesturerec.gif at project root android/)
            // Use a bundled drawable placeholder for sign-language image so it displays on all items
            // Build sign-language representation per huruf: map each Arabic letter to its hijaiyah glyph
            val gestureMap = mapOf(
                'ا' to "ا", 'ب' to "ب", 'ت' to "ت", 'ث' to "ث", 'ج' to "ج",
                'ح' to "ح", 'خ' to "خ", 'د' to "د", 'ذ' to "ذ", 'ر' to "ر",
                'ز' to "ز", 'س' to "س", 'ش' to "ش", 'ص' to "ص", 'ض' to "ض",
                'ط' to "ط", 'ظ' to "ظ", 'ع' to "ع", 'غ' to "غ", 'ف' to "ف",
                'ق' to "ق", 'ك' to "ك", 'ل' to "ل", 'م' to "م", 'ن' to "ن",
                'و' to "و", 'ه' to "ه", 'ي' to "ي"
            )

            // Normalize arabic string: remove spaces and diacritics (basic approach)
            val normalized = a.arab.replace(Regex("[\u064B-\u065F\u0610-\u061A\u06D6-\u06ED]"), "").replace(" ", "")
            val isyaratBuilder = StringBuilder()
            for (ch in normalized) {
                val glyph = gestureMap[ch]
                if (glyph != null) {
                    isyaratBuilder.append(glyph)
                    isyaratBuilder.append(' ')
                }
            }
            val isyaratText = if (isyaratBuilder.isNotEmpty()) isyaratBuilder.toString().trim() else ""
            holder.tvIsyarat.text = isyaratText
            holder.ivCheck.visibility = View.GONE
        }

        override fun getItemCount(): Int = items.size

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvNumber: TextView = view.findViewById(R.id.tv_ayat_number)
            val tvArab: TextView = view.findViewById(R.id.tv_arab)
            val tvTerjemah: TextView = view.findViewById(R.id.tv_terjemahan)
            val tvIsyarat: TextView = view.findViewById(R.id.tv_isyarat)
            val ivCheck: ImageView = view.findViewById(R.id.iv_check)
        }
    }
}
