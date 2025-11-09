package com.google.mediapipe.examples.gesturerecognizer.data

import com.google.mediapipe.examples.gesturerecognizer.data.model.Ayat
import com.google.mediapipe.examples.gesturerecognizer.data.model.Surat

object QuranData {
    
    fun getAllSuratJuz30(): List<Surat> {
        return listOf(
            getSuratAnNaba(),
            getSuratAnNaziat(),
            getSuratAbasa(),
            getSuratAtTakwir(),
            getSuratAlInfitar(),
            getSuratAlMutaffifin(),
            getSuratAlInshiqaq(),
            getSuratAlBuruj(),
            getSuratAtTariq(),
            getSuratAlAla(),
            getSuratAlGhashiyah(),
            getSuratAlFajr(),
            getSuratAlBalad(),
            getSuratAshShams(),
            getSuratAlLail(),
            getSuratAdhDhuha(),
            getSuratAshSharh(),
            getSuratAtTin(),
            getSuratAlAlaq(),
            getSuratAlQadr(),
            getSuratAlBayyinah(),
            getSuratAlZalzalah(),
            getSuratAlAdiyat(),
            getSuratAlQariah(),
            getSuratAtTakathur(),
            getSuratAlAsr(),
            getSuratAlHumazah(),
            getSuratAlFil(),
            getSuratQuraisy(),
            getSuratAlMaun(),
            getSuratAlKauthar(),
            getSuratAlKafirun(),
            getSuratAnNasr(),
            getSuratAlMasad(),
            getSuratAlIkhlas(),
            getSuratAlFalaq(),
            getSuratAnNas()
        )
    }

    // Surat 1: An-Naba' (78)
    private fun getSuratAnNaba(): Surat {
        return Surat(
            nomor = 1,
            nama = "An-Naba'",
            namaArab = "النبإ",
            jumlahAyat = 40,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "عَمَّ يَتَسَآءَلُونَ", "Tentang apakah mereka saling bertanya-tanya?"),
                Ayat(2, "عَنِ ٱلنَّبَإِ ٱلۡعَظِيمِ", "Tentang berita yang besar,"),
                Ayat(3, "ٱلَّذِي هُمۡ فِيهِ مُخۡتَلِفُونَ", "yang mereka perselisihkan."),
                Ayat(4, "كَلَّا سَيَعۡلَمُونَ", "Sekali-kali tidak! Kelak mereka akan mengetahui,"),
                Ayat(5, "ثُمَّ كَلَّا سَيَعۡلَمُونَ", "kemudian sekali-kali tidak! Kelak mereka akan mengetahui.")
            )
        )
    }

    // Surat 2: An-Nazi'at (79)
    private fun getSuratAnNaziat(): Surat {
        return Surat(
            nomor = 2,
            nama = "An-Nazi'at",
            namaArab = "النازعات",
            jumlahAyat = 46,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "وَٱلنَّٰزِعَٰتِ غَرۡقٗا", "Demi (malaikat-malaikat) yang mencabut (roh) dengan keras,"),
                Ayat(2, "وَٱلنَّٰشِطَٰتِ نَشۡطٗا", "dan (malaikat-malaikat) yang mencabut (roh) dengan lemah lembut,"),
                Ayat(3, "وَٱلسَّٰبِحَٰتِ سَبۡحٗا", "dan (malaikat-malaikat) yang turun dari langit dengan cepat,"),
                Ayat(4, "فَٱلسَّٰبِقَٰتِ سَبۡقٗا", "dan (malaikat-malaikat) yang mendahului dengan kencang,"),
                Ayat(5, "فَٱلۡمُدَبِّرَٰتِ أَمۡرٗا", "dan (malaikat-malaikat) yang mengatur urusan (dunia).")
            )
        )
    }

    // Surat 3: 'Abasa (80)
    private fun getSuratAbasa(): Surat {
        return Surat(
            nomor = 3,
            nama = "Abasa",
            namaArab = "عبس",
            jumlahAyat = 42,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "عَبَسَ وَتَوَلَّىٰٓ", "Dia (Muhammad) bermuka masam dan berpaling,"),
                Ayat(2, "أَن جَآءَهُ ٱلۡأَعۡمَىٰ", "karena seorang buta datang kepadanya."),
                Ayat(3, "وَمَا يُدۡرِيكَ لَعَلَّهُۥ يَزَّكَّىٰٓ", "Dan tahukah engkau (Muhammad) barangkali dia ingin menyucikan dirinya (dari dosa),"),
                Ayat(4, "أَوۡ يَذَّكَّرُ فَتَنفَعَهُ ٱلذِّكۡرَىٰٓ", "atau (ingin) mendapat pengajaran, yang memberi manfaat kepadanya?"),
                Ayat(5, "أَمَّا مَنِ ٱسۡتَغۡنَىٰ", "Adapun orang yang merasa dirinya serba cukup,")
            )
        )
    }

    // Surat 4: At-Takwir (81)
    private fun getSuratAtTakwir(): Surat {
        return Surat(
            nomor = 4,
            nama = "At-Takwir",
            namaArab = "التكوير",
            jumlahAyat = 29,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "إِذَا ٱلشَّمۡسُ كُوِّرَتۡ", "Apabila matahari digulung,"),
                Ayat(2, "وَإِذَا ٱلنُّجُومُ ٱنكَدَرَتۡ", "dan apabila bintang-bintang berjatuhan,"),
                Ayat(3, "وَإِذَا ٱلۡجِبَالُ سُيِّرَتۡ", "dan apabila gunung-gunung dihancurkan,"),
                Ayat(4, "وَإِذَا ٱلۡعِشَارُ عُطِّلَتۡ", "dan apabila unta-unta yang bunting ditinggalkan (tidak terurus),"),
                Ayat(5, "وَإِذَا ٱلۡوُحُوشُ حُشِرَتۡ", "dan apabila binatang-binatang liar dikumpulkan,")
            )
        )
    }

    // Surat 5: Al-Infitar (82)
    private fun getSuratAlInfitar(): Surat {
        return Surat(
            nomor = 5,
            nama = "Al-Infitar",
            namaArab = "الإنفطار",
            jumlahAyat = 19,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "إِذَا ٱلسَّمَآءُ ٱنفَطَرَتۡ", "Apabila langit terbelah,"),
                Ayat(2, "وَإِذَا ٱلۡكَوَاكِبُ ٱنتَثَرَتۡ", "dan apabila bintang-bintang berjatuhan,"),
                Ayat(3, "وَإِذَا ٱلۡبِحَارُ فُجِّرَتۡ", "dan apabila lautan dijadikan meluap,"),
                Ayat(4, "وَإِذَا ٱلۡقُبُورُ بُعۡثِرَتۡ", "dan apabila kubur-kubur dibongkar,"),
                Ayat(5, "عَلِمَتۡ نَفۡسٞ مَّا قَدَّمَتۡ وَأَخَّرَتۡ", "(pada hari itu) setiap orang mengetahui apa yang telah dikerjakannya dan yang dilalaikannya.")
            )
        )
    }

    // Surat 6: Al-Mutaffifin (83)
    private fun getSuratAlMutaffifin(): Surat {
        return Surat(
            nomor = 6,
            nama = "Al-Mutaffifin",
            namaArab = "المطففين",
            jumlahAyat = 36,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "وَيۡلٞ لِّلۡمُطَفِّفِينَ", "Celakalah bagi orang-orang yang curang,"),
                Ayat(2, "ٱلَّذِينَ إِذَا ٱكۡتَالُواْ عَلَى ٱلنَّاسِ يَسۡتَوۡفُونَ", "(yaitu) orang-orang yang apabila menerima takaran dari orang lain mereka minta dipenuhi,"),
                Ayat(3, "وَإِذَا كَالُوهُمۡ أَو وَّزَنُوهُمۡ يُخۡسِرُونَ", "dan apabila mereka menakar atau menimbang (untuk orang lain), mereka mengurangi."),
                Ayat(4, "أَلَا يَظُنُّ أُوْلَٰٓئِكَ أَنَّهُم مَّبۡعُوثُونَ", "Tidakkah mereka itu mengira, bahwa sesungguhnya mereka akan dibangkitkan,"),
                Ayat(5, "لِيَوۡمٍ عَظِيمٖ", "pada suatu hari yang besar (hari Kiamat),")
            )
        )
    }

    // Surat 7: Al-Inshiqaq (84)
    private fun getSuratAlInshiqaq(): Surat {
        return Surat(
            nomor = 7,
            nama = "Al-Inshiqaq",
            namaArab = "الإنشقاق",
            jumlahAyat = 25,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "إِذَا ٱلسَّمَآءُ ٱنشَقَّتۡ", "Apabila langit terbelah,"),
                Ayat(2, "وَأَذِنَتۡ لِرَبِّهَا وَحُقَّتۡ", "dan patuh kepada Tuhannya, dan sudah semestinya patuh,"),
                Ayat(3, "وَإِذَا ٱلۡأَرۡضُ مُدَّتۡ", "dan apabila bumi diratakan,"),
                Ayat(4, "وَأَلۡقَتۡ مَا فِيهَا وَتَخَلَّتۡ", "dan memuntahkan apa yang ada di dalamnya dan menjadi kosong,"),
                Ayat(5, "وَأَذِنَتۡ لِرَبِّهَا وَحُقَّتۡ", "dan patuh kepada Tuhannya, dan sudah semestinya patuh,")
            )
        )
    }

    // Surat 8: Al-Buruj (85)
    private fun getSuratAlBuruj(): Surat {
        return Surat(
            nomor = 8,
            nama = "Al-Buruj",
            namaArab = "البروج",
            jumlahAyat = 22,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "وَٱلسَّمَآءِ ذَاتِ ٱلۡبُرُوجِ", "Demi langit yang mempunyai gugusan bintang,"),
                Ayat(2, "وَٱلۡيَوۡمِ ٱلۡمَوۡعُودِ", "demi hari yang dijanjikan,"),
                Ayat(3, "وَشَاهِدٖ وَمَشۡهُودٖ", "demi yang menyaksikan dan yang disaksikan,"),
                Ayat(4, "قُتِلَ أَصۡحَٰبُ ٱلۡأُخۡدُودِ", "binasalah orang-orang yang membuat parit,"),
                Ayat(5, "ٱلنَّارِ ذَاتِ ٱلۡوَقُودِ", "(yang berisi) api yang mempunyai kayu bakar,")
            )
        )
    }

    // Surat 9: At-Tariq (86)
    private fun getSuratAtTariq(): Surat {
        return Surat(
            nomor = 9,
            nama = "At-Tariq",
            namaArab = "الطارق",
            jumlahAyat = 17,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "وَٱلسَّمَآءِ وَٱلطَّارِقِ", "Demi langit dan yang datang pada malam hari,"),
                Ayat(2, "وَمَآ أَدۡرَىٰكَ مَا ٱلطَّارِقُ", "dan tahukah kamu apakah yang datang pada malam hari itu?"),
                Ayat(3, "ٱلنَّجۡمُ ٱلثَّاقِبُ", "(yaitu) bintang yang bersinar tajam,"),
                Ayat(4, "إِن كُلُّ نَفۡسٖ لَّمَّا عَلَيۡهَا حَافِظٞ", "setiap orang pasti ada penjaganya (malaikat)."),
                Ayat(5, "فَلۡيَنظُرِ ٱلۡإِنسَٰنُ مِمَّ خُلِقَ", "Maka hendaklah manusia memperhatikan dari apa dia diciptakan?")
            )
        )
    }

    // Surat 10: Al-A'la (87)
    private fun getSuratAlAla(): Surat {
        return Surat(
            nomor = 10,
            nama = "Al-A'la",
            namaArab = "الأعلى",
            jumlahAyat = 19,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "سَبِّحِ ٱسۡمَ رَبِّكَ ٱلۡأَعۡلَى", "Sucikanlah nama Tuhanmu Yang Mahatinggi,"),
                Ayat(2, "ٱلَّذِي خَلَقَ فَسَوَّىٰ", "Yang menciptakan, lalu menyempurnakan (ciptaan-Nya),"),
                Ayat(3, "وَٱلَّذِي قَدَّرَ فَهَدَىٰ", "dan Yang menentukan kadar (masing-masing) dan memberi petunjuk,"),
                Ayat(4, "وَٱلَّذِيٓ أَخۡرَجَ ٱلۡمَرۡعَىٰ", "dan Yang menumbuhkan rumput-rumputan,"),
                Ayat(5, "فَجَعَلَهُۥ غُثَآءً أَحۡوَىٰ", "lalu dijadikan-Nya (rumput-rumput) itu kering kehitam-hitaman.")
            )
        )
    }

    // Surat 11: Al-Ghashiyah (88)
    private fun getSuratAlGhashiyah(): Surat {
        return Surat(
            nomor = 11,
            nama = "Al-Ghashiyah",
            namaArab = "الغاشية",
            jumlahAyat = 26,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "هَلۡ أَتَىٰكَ حَدِيثُ ٱلۡغَٰشِيَةِ", "Sudahkah sampai kepadamu berita (tentang terjadinya) hari Kiamat?"),
                Ayat(2, "وُجُوهٞ يَوۡمَئِذٍ خَٰشِعَةٌ", "Banyak wajah pada hari itu tunduk terhina,"),
                Ayat(3, "عَامِلَةٞ نَّاصِبَةٞ", "berusaha keras lagi kepayahan,"),
                Ayat(4, "تَصۡلَىٰ نَارًا حَامِيَةٗ", "memasuki api yang sangat panas (neraka),"),
                Ayat(5, "تُسۡقَىٰ مِنۡ عَيۡنٍ ءَانِيَةٖ", "diberi minum dari sumber mata air yang mendidih.")
            )
        )
    }

    // Surat 12: Al-Fajr (89)
    private fun getSuratAlFajr(): Surat {
        return Surat(
            nomor = 12,
            nama = "Al-Fajr",
            namaArab = "الفجر",
            jumlahAyat = 30,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "وَٱلۡفَجۡرِ", "Demi fajar,"),
                Ayat(2, "وَلَيَالٍ عَشۡرٖ", "dan malam yang sepuluh,"),
                Ayat(3, "وَٱلشَّفۡعِ وَٱلۡوَتۡرِ", "dan yang genap dan yang ganjil,"),
                Ayat(4, "وَٱلَّيۡلِ إِذَا يَسۡرِ", "dan malam apabila berlalu."),
                Ayat(5, "هَلۡ فِي ذَٰلِكَ قَسَمٞ لِّذِي حِجۡرٍ", "Pada yang demikian itu terdapat sumpah (yang dapat diterima) oleh orang-orang yang berakal.")
            )
        )
    }

    // Surat 13: Al-Balad (90)
    private fun getSuratAlBalad(): Surat {
        return Surat(
            nomor = 13,
            nama = "Al-Balad",
            namaArab = "البلد",
            jumlahAyat = 20,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "لَآ أُقۡسِمُ بِهَٰذَا ٱلۡبَلَدِ", "Aku bersumpah demi negeri ini (Makkah),"),
                Ayat(2, "وَأَنتَ حِلُّۢ بِهَٰذَا ٱلۡبَلَدِ", "sedang engkau (Muhammad) bertempat di negeri ini,"),
                Ayat(3, "وَوَالِدٖ وَمَا وَلَدَ", "demi bapak dan anaknya."),
                Ayat(4, "لَقَدۡ خَلَقۡنَا ٱلۡإِنسَٰنَ فِي كَبَدٍ", "Sungguh, Kami telah menciptakan manusia dalam susah payah."),
                Ayat(5, "أَيَحۡسَبُ أَن لَّن يَقۡدِرَ عَلَيۡهِ أَحَدٞ", "Apakah dia (manusia) mengira bahwa tidak ada seorang pun yang mampu mengalahkannya?")
            )
        )
    }

    // Surat 14: Ash-Shams (91)
    private fun getSuratAshShams(): Surat {
        return Surat(
            nomor = 14,
            nama = "Ash-Shams",
            namaArab = "الشمس",
            jumlahAyat = 15,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "وَٱلشَّمۡسِ وَضُحَىٰهَا", "Demi matahari dan sinar pagi,"),
                Ayat(2, "وَٱلۡقَمَرِ إِذَا تَلَىٰهَا", "demi bulan apabila mengiringinya,"),
                Ayat(3, "وَٱلنَّهَارِ إِذَا جَلَّىٰهَا", "demi siang apabila menampakkannya,"),
                Ayat(4, "وَٱلَّيۡلِ إِذَا يَغۡشَىٰهَا", "demi malam apabila menutupinya,"),
                Ayat(5, "وَٱلسَّمَآءِ وَمَا بَنَىٰهَا", "demi langit dan (Allah) yang membangunnya,")
            )
        )
    }

    // Surat 15: Al-Lail (92)
    private fun getSuratAlLail(): Surat {
        return Surat(
            nomor = 15,
            nama = "Al-Lail",
            namaArab = "الليل",
            jumlahAyat = 21,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "وَٱلَّيۡلِ إِذَا يَغۡشَىٰ", "Demi malam apabila menutupi (cahaya siang),"),
                Ayat(2, "وَٱلنَّهَارِ إِذَا تَجَلَّىٰ", "demi siang apabila terang benderang,"),
                Ayat(3, "وَمَا خَلَقَ ٱلذَّكَرَ وَٱلۡأُنثَىٰٓ", "demi (Tuhan) yang menciptakan laki-laki dan perempuan,"),
                Ayat(4, "إِنَّ سَعۡيَكُمۡ لَشَتَّىٰ", "sungguh, usaha kamu memang berbeda-beda."),
                Ayat(5, "فَأَمَّا مَنۡ أَعۡطَىٰ وَٱتَّقَىٰ", "Adapun orang yang memberikan (hartanya di jalan Allah) dan bertakwa,")
            )
        )
    }

    // Surat 16: Adh-Dhuha (93)
    private fun getSuratAdhDhuha(): Surat {
        return Surat(
            nomor = 16,
            nama = "Adh-Dhuha",
            namaArab = "الضحى",
            jumlahAyat = 11,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "وَٱلضُّحَىٰ", "Demi waktu matahari sepenggalahan naik,"),
                Ayat(2, "وَٱلَّيۡلِ إِذَا سَجَىٰ", "dan demi malam apabila telah sunyi,"),
                Ayat(3, "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ", "Tuhanmu tidak meninggalkan engkau (Muhammad) dan tidak (pula) membencimu,"),
                Ayat(4, "وَلَلۡأٓخِرَةُ خَيۡرٞ لَّكَ مِنَ ٱلۡأُولَىٰ", "dan sungguh, yang kemudian itu lebih baik bagimu daripada yang permulaan."),
                Ayat(5, "وَلَسَوۡفَ يُعۡطِيكَ رَبُّكَ فَتَرۡضَىٰٓ", "Dan sungguh, Tuhanmu pasti memberikan karunia-Nya kepadamu, sehingga engkau menjadi puas.")
            )
        )
    }

    // Surat 17: Ash-Sharh (94)
    private fun getSuratAshSharh(): Surat {
        return Surat(
            nomor = 17,
            nama = "Ash-Sharh",
            namaArab = "الشرح",
            jumlahAyat = 8,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "أَلَمۡ نَشۡرَحۡ لَكَ صَدۡرَكَ", "Bukankah Kami telah melapangkan dadamu (Muhammad)?"),
                Ayat(2, "وَوَضَعۡنَا عَنكَ وِزۡرَكَ", "Dan Kami pun telah menurunkan bebanmu darimu,"),
                Ayat(3, "ٱلَّذِيٓ أَنقَضَ ظَهۡرَكَ", "yang memberatkan punggungmu,"),
                Ayat(4, "وَرَفَعۡنَا لَكَ ذِكۡرَكَ", "dan Kami tinggikan sebutan (nama)mu bagimu."),
                Ayat(5, "فَإِنَّ مَعَ ٱلۡعُسۡرِ يُسۡرًا", "Maka sesungguhnya bersama kesulitan ada kemudahan,")
            )
        )
    }

    // Surat 18: At-Tin (95)
    private fun getSuratAtTin(): Surat {
        return Surat(
            nomor = 18,
            nama = "At-Tin",
            namaArab = "التين",
            jumlahAyat = 8,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "وَٱلتِّينِ وَٱلزَّيۡتُونِ", "Demi (buah) Tin dan (buah) Zaitun,"),
                Ayat(2, "وَطُورِ سِينِينَ", "demi gunung Sinai,"),
                Ayat(3, "وَهَٰذَا ٱلۡبَلَدِ ٱلۡأَمِينِ", "dan demi negeri (Makkah) yang aman ini."),
                Ayat(4, "لَقَدۡ خَلَقۡنَا ٱلۡإِنسَٰنَ فِيٓ أَحۡسَنِ تَقۡوِيمٖ", "Sungguh, Kami telah menciptakan manusia dalam bentuk yang sebaik-baiknya,"),
                Ayat(5, "ثُمَّ رَدَدۡنَٰهُ أَسۡفَلَ سَٰفِلِينَ", "kemudian Kami kembalikan dia ke tempat yang serendah-rendahnya,")
            )
        )
    }

    // Surat 19: Al-'Alaq (96)
    private fun getSuratAlAlaq(): Surat {
        return Surat(
            nomor = 19,
            nama = "Al-Alaq",
            namaArab = "العلق",
            jumlahAyat = 19,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "ٱقۡرَأۡ بِٱسۡمِ رَبِّكَ ٱلَّذِي خَلَقَ", "Bacalah dengan (menyebut) nama Tuhanmu yang menciptakan,"),
                Ayat(2, "خَلَقَ ٱلۡإِنسَٰنَ مِنۡ عَلَقٍ", "Dia telah menciptakan manusia dari segumpal darah."),
                Ayat(3, "ٱقۡرَأۡ وَرَبُّكَ ٱلۡأَكۡرَمُ", "Bacalah! Tuhanmulah Yang Mahamulia,"),
                Ayat(4, "ٱلَّذِي عَلَّمَ بِٱلۡقَلَمِ", "yang mengajar (manusia) dengan pena."),
                Ayat(5, "عَلَّمَ ٱلۡإِنسَٰنَ مَا لَمۡ يَعۡلَمۡ", "Dia mengajarkan manusia apa yang tidak diketahuinya.")
            )
        )
    }

    // Surat 20: Al-Qadr (97)
    private fun getSuratAlQadr(): Surat {
        return Surat(
            nomor = 20,
            nama = "Al-Qadr",
            namaArab = "القدر",
            jumlahAyat = 5,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "إِنَّآ أَنزَلۡنَٰهُ فِي لَيۡلَةِ ٱلۡقَدۡرِ", "Sesungguhnya Kami telah menurunkannya (Al-Qur'an) pada malam Lailatul Qadar."),
                Ayat(2, "وَمَآ أَدۡرَىٰكَ مَا لَيۡلَةُ ٱلۡقَدۡرِ", "Dan tahukah kamu apakah malam Lailatul Qadar itu?"),
                Ayat(3, "لَيۡلَةُ ٱلۡقَدۡرِ خَيۡرٞ مِّنۡ أَلۡفِ شَهۡرٖ", "Malam Lailatul Qadar itu lebih baik daripada seribu bulan."),
                Ayat(4, "تَنَزَّلُ ٱلۡمَلَٰٓئِكَةُ وَٱلرُّوحُ فِيهَا بِإِذۡنِ رَبِّهِم مِّن كُلِّ أَمۡرٖ", "Pada malam itu turun para malaikat dan Ruh (Jibril) dengan izin Tuhannya untuk mengatur semua urusan."),
                Ayat(5, "سَلَٰمٌ هِيَ حَتَّىٰ مَطۡلَعِ ٱلۡفَجۡرِ", "Sejahteralah (malam itu) sampai terbit fajar.")
            )
        )
    }

    // Surat 21: Al-Bayyinah (98)
    private fun getSuratAlBayyinah(): Surat {
        return Surat(
            nomor = 21,
            nama = "Al-Bayyinah",
            namaArab = "البينة",
            jumlahAyat = 8,
            tempatTurun = "Madinah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "لَمۡ يَكُنِ ٱلَّذِينَ كَفَرُواْ مِنۡ أَهۡلِ ٱلۡكِتَٰبِ وَٱلۡمُشۡرِكِينَ مُنفَكِّينَ حَتَّىٰ تَأۡتِيَهُمُ ٱلۡبَيِّنَةُ", "Orang-orang kafir dari golongan Ahli Kitab dan orang-orang musyrik (mengatakan bahwa mereka) tidak akan meninggalkan (agamanya) sampai datang kepada mereka bukti yang nyata,"),
                Ayat(2, "رَسُولٞ مِّنَ ٱللَّهِ يَتۡلُواْ صُحُفٗا مُّطَهَّرَةٗ", "(yaitu) seorang Rasul dari Allah yang membacakan lembaran-lembaran yang suci (Al-Qur'an),"),
                Ayat(3, "فِيهَا كُتُبٞ قَيِّمَةٞ", "di dalamnya terdapat (isi) kitab-kitab yang lurus (benar)."),
                Ayat(4, "وَمَا تَفَرَّقَ ٱلَّذِينَ أُوتُواْ ٱلۡكِتَٰبَ إِلَّا مِنۢ بَعۡدِ مَا جَآءَتۡهُمُ ٱلۡبَيِّنَةُ", "Dan tidak berpecah belah Ahli Kitab kecuali setelah datang kepada mereka bukti yang nyata."),
                Ayat(5, "وَمَآ أُمِرُوٓاْ إِلَّا لِيَعۡبُدُواْ ٱللَّهَ مُخۡلِصِينَ لَهُ ٱلدِّينَ حُنَفَآءَ وَيُقِيمُواْ ٱلصَّلَوٰةَ وَيُؤۡتُواْ ٱلزَّكَوٰةَۚ وَذَٰلِكَ دِينُ ٱلۡقَيِّمَةِ", "Padahal mereka hanya diperintahkan menyembah Allah dengan ikhlas menaati-Nya semata-mata karena (menjalankan) agama, dan juga agar melaksanakan salat dan menunaikan zakat; dan yang demikian itulah agama yang lurus (benar).")
            )
        )
    }

    // Surat 22: Al-Zalzalah (99)
    private fun getSuratAlZalzalah(): Surat {
        return Surat(
            nomor = 22,
            nama = "Al-Zalzalah",
            namaArab = "الزلزلة",
            jumlahAyat = 8,
            tempatTurun = "Madinah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "إِذَا زُلۡزِلَتِ ٱلۡأَرۡضُ زِلۡزَالَهَا", "Apabila bumi diguncangkan dengan guncangan yang dahsyat,"),
                Ayat(2, "وَأَخۡرَجَتِ ٱلۡأَرۡضُ أَثۡقَالَهَا", "dan bumi mengeluarkan beban-beban berat (yang dikandung)nya,"),
                Ayat(3, "وَقَالَ ٱلۡإِنسَٰنُ مَا لَهَا", "dan manusia bertanya, \"Apa yang terjadi padanya?\""),
                Ayat(4, "يَوۡمَئِذٖ تُحَدِّثُ أَخۡبَارَهَا", "Pada hari itu bumi menyampaikan beritanya,"),
                Ayat(5, "بِأَنَّ رَبَّكَ أَوۡحَىٰ لَهَا", "karena sesungguhnya Tuhanmu telah memerintahkan (yang sedemikian itu) kepadanya.")
            )
        )
    }

    // Surat 23: Al-'Adiyat (100)
    private fun getSuratAlAdiyat(): Surat {
        return Surat(
            nomor = 23,
            nama = "Al-Adiyat",
            namaArab = "العاديات",
            jumlahAyat = 11,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "وَٱلۡعَٰدِيَٰتِ ضَبۡحٗا", "Demi kuda perang yang berlari kencang dengan terengah-engah,"),
                Ayat(2, "فَٱلۡمُورِيَٰتِ قَدۡحٗا", "dan kuda yang memercikkan bunga api (dengan pukulan kuku kakinya),"),
                Ayat(3, "فَٱلۡمُغِيرَٰتِ صُبۡحٗا", "dan kuda yang menyerang (dengan tiba-tiba) pada waktu pagi,"),
                Ayat(4, "فَأَثَرۡنَ بِهِۦ نَقۡعٗا", "maka menerbangkan debu,"),
                Ayat(5, "فَوَسَطۡنَ بِهِۦ جَمۡعًا", "lalu menyerbu ke tengah-tengah kumpulan musuh,")
            )
        )
    }

    // Surat 24: Al-Qari'ah (101)
    private fun getSuratAlQariah(): Surat {
        return Surat(
            nomor = 24,
            nama = "Al-Qari'ah",
            namaArab = "القارعة",
            jumlahAyat = 11,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "ٱلۡقَارِعَةُ", "Hari Kiamat,"),
                Ayat(2, "مَا ٱلۡقَارِعَةُ", "apakah hari Kiamat itu?"),
                Ayat(3, "وَمَآ أَدۡرَىٰكَ مَا ٱلۡقَارِعَةُ", "Dan tahukah kamu apakah hari Kiamat itu?"),
                Ayat(4, "يَوۡمَ يَكُونُ ٱلنَّاسُ كَٱلۡفَرَاشِ ٱلۡمَبۡثُوثِ", "(Hari) ketika manusia seperti laron yang beterbangan,"),
                Ayat(5, "وَتَكُونُ ٱلۡجِبَالُ كَٱلۡعِهۡنِ ٱلۡمَنفُوشِ", "dan gunung-gunung seperti bulu yang dihambur-hamburkan.")
            )
        )
    }

    // Surat 25: At-Takathur (102)
    private fun getSuratAtTakathur(): Surat {
        return Surat(
            nomor = 25,
            nama = "At-Takathur",
            namaArab = "التكاثر",
            jumlahAyat = 8,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "أَلۡهَىٰكُمُ ٱلتَّكَاثُرُ", "Bermegah-megahan telah melalaikan kamu,"),
                Ayat(2, "حَتَّىٰ زُرۡتُمُ ٱلۡمَقَابِرَ", "sampai kamu masuk ke dalam kubur."),
                Ayat(3, "كَلَّا سَوۡفَ تَعۡلَمُونَ", "Sekali-kali tidak! Kelak kamu akan mengetahui (akibat perbuatanmu itu),"),
                Ayat(4, "ثُمَّ كَلَّا سَوۡفَ تَعۡلَمُونَ", "kemudian sekali-kali tidak! Kelak kamu akan mengetahui."),
                Ayat(5, "كَلَّا لَوۡ تَعۡلَمُونَ عِلۡمَ ٱلۡيَقِينِ", "Sekali-kali tidak! Sekiranya kamu mengetahui dengan pasti,")
            )
        )
    }

    // Surat 26: Al-'Asr (103)
    private fun getSuratAlAsr(): Surat {
        return Surat(
            nomor = 26,
            nama = "Al-Asr",
            namaArab = "العصر",
            jumlahAyat = 3,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "وَٱلۡعَصۡرِ", "Demi masa,"),
                Ayat(2, "إِنَّ ٱلۡإِنسَٰنَ لَفِي خُسۡرٍ", "sungguh, manusia berada dalam kerugian,"),
                Ayat(3, "إِلَّا ٱلَّذِينَ ءَامَنُواْ وَعَمِلُواْ ٱلصَّٰلِحَٰتِ وَتَوَاصَوۡاْ بِٱلۡحَقِّ وَتَوَاصَوۡاْ بِٱلصَّبۡرِ", "kecuali orang-orang yang beriman dan mengerjakan kebajikan serta saling menasihati untuk kebenaran dan saling menasihati untuk kesabaran.")
            )
        )
    }

    // Surat 27: Al-Humazah (104)
    private fun getSuratAlHumazah(): Surat {
        return Surat(
            nomor = 27,
            nama = "Al-Humazah",
            namaArab = "الهمزة",
            jumlahAyat = 9,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "وَيۡلٞ لِّكُلِّ هُمَزَةٖ لُّمَزَةٍ", "Celakalah bagi setiap pengumpat dan pencela,"),
                Ayat(2, "ٱلَّذِي جَمَعَ مَالٗا وَعَدَّدَهُۥ", "(yaitu) orang yang mengumpulkan harta dan menghitung-hitungnya,"),
                Ayat(3, "يَحۡسَبُ أَنَّ مَالَهُۥٓ أَخۡلَدَهُۥ", "dia (manusia) mengira bahwa hartanya itu dapat mengekalkannya."),
                Ayat(4, "كَلَّاۖ لَيُنۢبَذَنَّ فِي ٱلۡحُطَمَةِ", "Sekali-kali tidak! Pasti dia akan dilemparkan ke dalam (neraka) Hutamah."),
                Ayat(5, "وَمَآ أَدۡرَىٰكَ مَا ٱلۡحُطَمَةُ", "Dan tahukah kamu apakah (neraka) Hutamah itu?")
            )
        )
    }

    // Surat 28: Al-Fil (105)
    private fun getSuratAlFil(): Surat {
        return Surat(
            nomor = 28,
            nama = "Al-Fil",
            namaArab = "الفيل",
            jumlahAyat = 5,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "أَلَمۡ تَرَ كَيۡفَ فَعَلَ رَبُّكَ بِأَصۡحَٰبِ ٱلۡفِيلِ", "Tidakkah engkau (Muhammad) perhatikan bagaimana Tuhanmu telah bertindak terhadap pasukan bergajah?"),
                Ayat(2, "أَلَمۡ يَجۡعَلۡ كَيۡدَهُمۡ فِي تَضۡلِيلٖ", "Bukankah Dia telah menjadikan tipu daya mereka itu sia-sia?"),
                Ayat(3, "وَأَرۡسَلَ عَلَيۡهِمۡ طَيۡرًا أَبَابِيلَ", "Dan Dia mengirimkan kepada mereka burung yang berbondong-bondong,"),
                Ayat(4, "تَرۡمِيهِم بِحِجَارَةٖ مِّن سِجِّيلٖ", "yang melempari mereka dengan batu dari tanah liat yang dibakar,"),
                Ayat(5, "فَجَعَلَهُمۡ كَعَصۡفٖ مَّأۡكُولِۭ", "sehingga mereka dijadikan-Nya seperti daun-daun yang dimakan (ulat).")
            )
        )
    }

    // Surat 29: Quraisy (106)
    private fun getSuratQuraisy(): Surat {
        return Surat(
            nomor = 29,
            nama = "Quraisy",
            namaArab = "قريش",
            jumlahAyat = 4,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "لِإِيلَٰفِ قُرَيۡشٍ", "Karena kebiasaan orang-orang Quraisy,"),
                Ayat(2, "إِۦلَٰفِهِمۡ رِحۡلَةَ ٱلشِّتَآءِ وَٱلصَّيۡفِ", "(yaitu) kebiasaan mereka bepergian pada musim dingin dan musim panas."),
                Ayat(3, "فَلۡيَعۡبُدُواْ رَبَّ هَٰذَا ٱلۡبَيۡتِ", "Maka hendaklah mereka menyembah Tuhan (pemilik) rumah ini (Ka'bah),"),
                Ayat(4, "ٱلَّذِيٓ أَطۡعَمَهُم مِّن جُوعٖ وَءَامَنَهُم مِّنۡ خَوۡفِۭ", "yang telah memberi makanan kepada mereka untuk menghilangkan lapar dan mengamankan mereka dari ketakutan.")
            )
        )
    }

    // Surat 30: Al-Ma'un (107)
    private fun getSuratAlMaun(): Surat {
        return Surat(
            nomor = 30,
            nama = "Al-Maa'uun",
            namaArab = "الماعون",
            jumlahAyat = 7,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "أَرَءَيۡتَ ٱلَّذِي يُكَذِّبُ بِٱلدِّينِ", "Tahukah kamu (orang) yang mendustakan agama?"),
                Ayat(2, "فَذَٰلِكَ ٱلَّذِي يَدُعُّ ٱلۡيَتِيمَ", "Itulah orang yang menghardik anak yatim,"),
                Ayat(3, "وَلَا يَحُضُّ عَلَىٰ طَعَامِ ٱلۡمِسۡكِينِ", "dan tidak menganjurkan memberi makan orang miskin."),
                Ayat(4, "فَوَيۡلٞ لِّلۡمُصَلِّينَ", "Maka celakalah orang-orang yang salat,"),
                Ayat(5, "ٱلَّذِينَ هُمۡ عَن صَلَاتِهِمۡ سَاهُونَ", "(yaitu) orang-orang yang lalai terhadap salatnya,"),
                Ayat(6, "ٱلَّذِينَ هُمۡ يُرَآءُونَ", "(yaitu) orang-orang yang berbuat riya,"),
                Ayat(7, "وَيَمۡنَعُونَ ٱلۡمَاعُونَ", "dan menolak memberikan bantuan.")
            )
        )
    }

    // Surat 31: Al-Kawthar (108)
    private fun getSuratAlKauthar(): Surat {
        return Surat(
            nomor = 31,
            nama = "Al-Kauthar",
            namaArab = "الكوثر",
            jumlahAyat = 3,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "إِنَّآ أَعۡطَيۡنَٰكَ ٱلۡكَوۡثَرَ", "Sesungguhnya Kami telah memberikan kepadamu (Muhammad) nikmat yang banyak."),
                Ayat(2, "فَصَلِّ لِرَبِّكَ وَٱنۡحَرۡ", "Maka laksanakanlah salat karena Tuhanmu, dan berkorbanlah."),
                Ayat(3, "إِنَّ شَانِئَكَ هُوَ ٱلۡأَبۡتَرُ", "Sungguh, yang membencimu dialah yang terputus (dari rahmat).")
            )
        )
    }

    // Surat 32: Al-Kafirun (109)
    private fun getSuratAlKafirun(): Surat {
        return Surat(
            nomor = 32,
            nama = "Al-Kafirun",
            namaArab = "الكافرون",
            jumlahAyat = 6,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "قُلۡ يَٰٓأَيُّهَا ٱلۡكَٰفِرُونَ", "Katakanlah (Muhammad), \"Wahai orang-orang kafir!"),
                Ayat(2, "لَآ أَعۡبُدُ مَا تَعۡبُدُونَ", "Aku tidak akan menyembah apa yang kamu sembah,"),
                Ayat(3, "وَلَآ أَنتُمۡ عَٰبِدُونَ مَآ أَعۡبُدُ", "dan kamu bukan penyembah apa yang aku sembah,"),
                Ayat(4, "وَلَآ أَنَا۠ عَابِدٞ مَّا عَبَدتُّمۡ", "dan aku tidak pernah menjadi penyembah apa yang kamu sembah,"),
                Ayat(5, "وَلَآ أَنتُمۡ عَٰبِدُونَ مَآ أَعۡبُدُ", "dan kamu tidak pernah (pula) menjadi penyembah apa yang aku sembah."),
                Ayat(6, "لَكُمۡ دِينُكُمۡ وَلِيَ دِينِ", "Untukmu agamamu, dan untukku agamaku.\"")
            )
        )
    }

    // Surat 33: An-Nasr (110)
    private fun getSuratAnNasr(): Surat {
        return Surat(
            nomor = 33,
            nama = "An-Nasr",
            namaArab = "النصر",
            jumlahAyat = 3,
            tempatTurun = "Madinah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "إِذَا جَآءَ نَصۡرُ ٱللَّهِ وَٱلۡفَتۡحُ", "Apabila telah datang pertolongan Allah dan kemenangan,"),
                Ayat(2, "وَرَأَيۡتَ ٱلنَّاسَ يَدۡخُلُونَ فِي دِينِ ٱللَّهِ أَفۡوَاجٗا", "dan engkau melihat manusia berbondong-bondong masuk agama Allah,"),
                Ayat(3, "فَسَبِّحۡ بِحَمۡدِ رَبِّكَ وَٱسۡتَغۡفِرۡهُۚ إِنَّهُۥ كَانَ تَوَّابَۢا", "maka bertasbihlah dengan memuji Tuhanmu dan mohonlah ampunan kepada-Nya. Sungguh, Dia Maha Penerima tobat.")
            )
        )
    }

    // Surat 34: Al-Masad (111)
    private fun getSuratAlMasad(): Surat {
        return Surat(
            nomor = 34,
            nama = "Al-Masad",
            namaArab = "المسد",
            jumlahAyat = 5,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "تَبَّتۡ يَدَآ أَبِي لَهَبٖ وَتَبَّ", "Binasalah kedua tangan Abu Lahab dan sungguh dia akan binasa."),
                Ayat(2, "مَآ أَغۡنَىٰ عَنۡهُ مَالُهُۥ وَمَا كَسَبَ", "Tidaklah berfaedah kepadanya hartanya dan apa yang dia usahakan."),
                Ayat(3, "سَيَصۡلَىٰ نَارٗا ذَاتَ لَهَبٖ", "Kelak dia akan masuk ke dalam api yang bergejolak (neraka),"),
                Ayat(4, "وَٱمۡرَأَتُهُۥ حَمَّالَةَ ٱلۡحَطَبِ", "dan (begitu juga) istrinya, pembawa kayu bakar (penyebar fitnah),"),
                Ayat(5, "فِي جِيدِهَا حَبۡلٞ مِّن مَّسَدِۭ", "yang di lehernya ada tali dari sabut.")
            )
        )
    }

    // Surat 35: Al-Ikhlas (112)
    private fun getSuratAlIkhlas(): Surat {
        return Surat(
            nomor = 35,
            nama = "Al-Ikhlas",
            namaArab = "الإخلاص",
            jumlahAyat = 4,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "قُلۡ هُوَ ٱللَّهُ أَحَدٌ", "Katakanlah (Muhammad), \"Dialah Allah, Yang Maha Esa."),
                Ayat(2, "ٱللَّهُ ٱلصَّمَدُ", "Allah tempat meminta segala sesuatu."),
                Ayat(3, "لَمۡ يَلِدۡ وَلَمۡ يُولَدۡ", "Dia tidak beranak dan tidak pula diperanakkan."),
                Ayat(4, "وَلَمۡ يَكُن لَّهُۥ كُفُوًا أَحَدُۢ", "Dan tidak ada sesuatu yang setara dengan Dia.\"")
            )
        )
    }

    // Surat 36: Al-Falaq (113)
    private fun getSuratAlFalaq(): Surat {
        return Surat(
            nomor = 36,
            nama = "Al-Falaq",
            namaArab = "الفلق",
            jumlahAyat = 5,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "قُلۡ أَعُوذُ بِرَبِّ ٱلۡفَلَقِ", "Katakanlah, \"Aku berlindung kepada Tuhan yang menguasai subuh,"),
                Ayat(2, "مِن شَرِّ مَا خَلَقَ", "dari kejahatan (makhluk) yang diciptakan-Nya,"),
                Ayat(3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "dan dari kejahatan malam apabila telah gelap gulita,"),
                Ayat(4, "وَمِن شَرِّ ٱلنَّفَّٰثَٰتِ فِي ٱلۡعُقَدِ", "dan dari kejahatan (perempuan-perempuan) tukang sihir yang meniup pada buhul-buhul (talinya),"),
                Ayat(5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "dan dari kejahatan orang yang dengki apabila dia dengki.\"")
            )
        )
    }

    // Surat 37: An-Nas (114)
    private fun getSuratAnNas(): Surat {
        return Surat(
            nomor = 37,
            nama = "An-Nas",
            namaArab = "الناس",
            jumlahAyat = 6,
            tempatTurun = "Makkah",
            juz = 30,
            ayatList = listOf(
                Ayat(1, "قُلۡ أَعُوذُ بِرَبِّ ٱلنَّاسِ", "Katakanlah, \"Aku berlindung kepada Tuhan (yang memelihara dan menguasai) manusia,"),
                Ayat(2, "مَلِكِ ٱلنَّاسِ", "Raja manusia,"),
                Ayat(3, "إِلَٰهِ ٱلنَّاسِ", "Sembahan manusia,"),
                Ayat(4, "مِن شَرِّ ٱلۡوَسۡوَاسِ ٱلۡخَنَّاسِ", "dari kejahatan (bisikan) setan yang biasa bersembunyi,"),
                Ayat(5, "ٱلَّذِي يُوَسۡوِسُ فِي صُدُورِ ٱلنَّاسِ", "yang membisikkan (kejahatan) ke dalam dada manusia,"),
                Ayat(6, "مِنَ ٱلۡجِنَّةِ وَٱلنَّاسِ", "dari (golongan) jin dan manusia.\"")
            )
        )
    }
}
