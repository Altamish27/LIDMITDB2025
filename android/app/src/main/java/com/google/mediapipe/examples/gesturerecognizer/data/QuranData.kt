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
                Ayat(5, "ثُمَّ كَلَّا سَيَعۡلَمُونَ", "kemudian sekali-kali tidak! Kelak mereka akan mengetahui."),
                Ayat(6, "أَلَمۡ نَجۡعَلِ ٱلۡأَرۡضَ مِهَٰدٗا", "Bukankah Kami telah menjadikan bumi sebagai hamparan,"),
                Ayat(7, "وَٱلۡجِبَالَ أَوۡتَادٗا", "dan gunung-gunung sebagai pasak?"),
                Ayat(8, "وَخَلَقۡنَٰكُمۡ أَزۡوَٰجٗا", "Dan Kami menciptakan kamu berpasang-pasangan,"),
                Ayat(9, "وَجَعَلۡنَا نَوۡمَكُمۡ سُبَاتٗا", "dan Kami jadikan tidurmu untuk istirahat,"),
                Ayat(10, "وَجَعَلۡنَا ٱلَّيۡلَ لِبَاسٗا", "dan Kami jadikan malam sebagai pakaian,"),
                Ayat(11, "وَجَعَلۡنَا ٱلنَّهَارَ مَعَاشٗا", "dan Kami jadikan siang untuk mencari penghidupan."),
                Ayat(12, "وَبَنَيۡنَا فَوۡقَكُمۡ سَبۡعٗا شِدَادٗا", "Dan Kami membangun di atas kamu tujuh (langit) yang kuat,"),
                Ayat(13, "وَجَعَلۡنَا سِرَاجٗا وَهَّاجٗا", "dan Kami jadikan pelita yang amat terang (matahari),"),
                Ayat(14, "وَأَنزَلۡنَا مِنَ ٱلۡمُعۡصِرَٰتِ مَآءٗ ثَجَّاجٗا", "dan Kami turunkan dari awan air yang banyak tercurah,"),
                Ayat(15, "لِّنُخۡرِجَ بِهِۦ حَبّٗا وَنَبَاتٗا", "agar Kami tumbuhkan dengan air itu biji-bijian dan tumbuh-tumbuhan,"),
                Ayat(16, "وَجَنَّٰتٍ أَلۡفَافًا", "dan kebun-kebun yang rindang."),
                Ayat(17, "إِنَّ يَوۡمَ ٱلۡفَصۡلِ كَانَ مِيقَٰتٗا", "Sesungguhnya hari keputusan adalah suatu waktu yang ditetapkan,"),
                Ayat(18, "يَوۡمَ يُنفَخُ فِي ٱلصُّورِ فَتَأۡتُونَ أَفۡوَاجٗا", "yaitu hari (ketika) sangkakala ditiup, lalu kamu datang berkelompok-kelompok."),
                Ayat(19, "وَفُتِحَتِ ٱلسَّمَآءُ فَكَانَتۡ أَبۡوَٰبٗا", "Dan langit pun dibuka, maka terdapatlah beberapa pintu,"),
                Ayat(20, "وَسُيِّرَتِ ٱلۡجِبَالُ فَكَانَتۡ سَرَابًا", "dan gunung-gunung pun dijalankan sehingga menjadi fatamorgana."),
                Ayat(21, "إِنَّ جَهَنَّمَ كَانَتۡ مِرۡصَادٗا", "Sesungguhnya neraka Jahanam itu (padanya) ada tempat pengintai,"),
                Ayat(22, "لِّلطَّٰغِينَ مَـَٔابٗا", "bagi orang-orang yang melampaui batas, sebagai tempat kembali,"),
                Ayat(23, "لَّٰبِثِينَ فِيهَآ أَحۡقَابٗا", "mereka tinggal di dalamnya berabad-abad lamanya."),
                Ayat(24, "لَّا يَذُوقُونَ فِيهَا بَرۡدٗا وَلَا شَرَابًا", "Mereka tidak merasakan kesejukan di dalamnya dan tidak (pula mendapat) minuman,"),
                Ayat(25, "إِلَّا حَمِيمٗا وَغَسَّاقٗا", "selain air yang mendidih dan nanah,"),
                Ayat(26, "جَزَآءٗ وِفَاقًا", "sebagai pembalasan yang setimpal."),
                Ayat(27, "إِنَّهُمۡ كَانُواْ لَا يَرۡجُونَ حِسَابٗا", "Sesungguhnya mereka tidak takut akan hisab,"),
                Ayat(28, "وَكَذَّبُواْ بِـَٔايَٰتِنَا كِذَّابٗا", "dan mereka mendustakan ayat-ayat Kami dengan sesungguhnya."),
                Ayat(29, "وَكُلَّ شَيۡءٍ أَحۡصَيۡنَٰهُ كِتَٰبٗا", "Dan segala sesuatu telah Kami catat dalam suatu kitab (Lauh Mahfuzh)."),
                Ayat(30, "فَذُوقُواْ فَلَن نَّزِيدَكُمۡ إِلَّا عَذَابًا", "Maka rasakanlah! Kami tidak akan menambah (azab) kepadamu melainkan azab (yang lebih besar)."),
                Ayat(31, "إِنَّ لِلۡمُتَّقِينَ مَفَازًا", "Sesungguhnya bagi orang-orang yang bertakwa (disediakan) tempat yang beruntung,"),
                Ayat(32, "حَدَآئِقَ وَأَعۡنَٰبٗا", "yaitu kebun-kebun dan buah anggur,"),
                Ayat(33, "وَكَوَاعِبَ أَتۡرَابٗا", "dan gadis-gadis remaja yang sebaya,"),
                Ayat(34, "وَكَأۡسٗا دِهَاقٗا", "dan gelas-gelas yang penuh (berisi minuman)."),
                Ayat(35, "لَّا يَسۡمَعُونَ فِيهَا لَغۡوٗا وَلَا كِذَّٰبٗا", "Di dalamnya mereka tidak mendengar perkataan yang sia-sia dan tidak (pula) perkataan dusta."),
                Ayat(36, "جَزَآءٗ مِّن رَّبِّكَ عَطَآءٗ حِسَابٗا", "Sebagai balasan dan pemberian yang cukup banyak dari Tuhanmu."),
                Ayat(37, "رَّبِّ ٱلسَّمَٰوَٰتِ وَٱلۡأَرۡضِ وَمَا بَيۡنَهُمَا ٱلرَّحۡمَٰنِۖ لَا يَمۡلِكُونَ مِنۡهُ خِطَابٗا", "Tuhan (yang memelihara) langit dan bumi dan apa yang ada di antara keduanya; Yang Maha Pengasih. Mereka tidak mampu berbicara dengan-Nya."),
                Ayat(38, "يَوۡمَ يَقُومُ ٱلرُّوحُ وَٱلۡمَلَٰٓئِكَةُ صَفّٗاۖ لَّا يَتَكَلَّمُونَ إِلَّا مَنۡ أَذِنَ لَهُ ٱلرَّحۡمَٰنُ وَقَالَ صَوَابٗا", "Pada hari, ketika ruh dan para malaikat berdiri bersaf-saf, mereka tidak berkata-kata, kecuali siapa yang telah diberi izin kepadanya oleh Tuhan Yang Maha Pengasih dan dia hanya mengatakan yang benar."),
                Ayat(39, "ذَٰلِكَ ٱلۡيَوۡمُ ٱلۡحَقُّۖ فَمَن شَآءَ ٱتَّخَذَ إِلَىٰ رَبِّهِۦ مَـَٔابًا", "Itulah hari yang pasti terjadi. Maka barangsiapa menghendaki, niscaya dia menempuh jalan kembali kepada Tuhannya."),
                Ayat(40, "إِنَّآ أَنذَرۡنَٰكُمۡ عَذَابٗا قَرِيبٗاۚ يَوۡمَ يَنظُرُ ٱلۡمَرۡءُ مَا قَدَّمَتۡ يَدَاهُ وَيَقُولُ ٱلۡكَافِرُ يَٰلَيۡتَنِي كُنتُ تُرَٰبَۢا", "Sesungguhnya Kami telah memperingatkan kepadamu (hai orang kafir) azab yang dekat, pada hari manusia melihat apa yang telah dikerjakan oleh kedua tangannya; dan orang kafir berkata, \"Alangkah baiknya sekiranya aku dahulu adalah tanah.\"")
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
                Ayat(5, "فَٱلۡمُدَبِّرَٰتِ أَمۡرٗا", "dan (malaikat-malaikat) yang mengatur urusan (dunia)."),
                Ayat(6, "يَوۡمَ تَرۡجُفُ ٱلرَّاجِفَةُ", "Pada hari (ketika) bumi berguncang keras,"),
                Ayat(7, "تَتۡبَعُهَا ٱلرَّادِفَةُ", "diikuti oleh guncangan berikutnya,"),
                Ayat(8, "قُلُوبٞ يَوۡمَئِذٖ وَاجِفَةٌ", "hati manusia pada waktu itu sangat takut,"),
                Ayat(9, "أَبۡصَٰرُهَا خَٰشِعَةٞ", "pandangan mereka tunduk."),
                Ayat(10, "يَقُولُونَ أَءِنَّا لَمَرۡدُودُونَ فِي ٱلۡحَافِرَةِ", "Mereka berkata, \"Apakah kita benar-benar akan dikembalikan kepada kehidupan yang semula?"),
                Ayat(11, "أَءِذَا كُنَّا عِظَٰمٗا نَّخِرَةٗ", "Apakah (akan dibangkitkan juga) apabila kita telah menjadi tulang belulang yang hancur?"),
                Ayat(12, "قَالُواْ تِلۡكَ إِذٗا كَرَّةٌ خَاسِرَةٞ", "Mereka berkata, \"Kalau demikian, itu adalah suatu pengembalian yang merugikan.\""),
                Ayat(13, "فَإِنَّمَا هِيَ زَجۡرَةٞ وَٰحِدَةٞ", "Maka sesungguhnya pengembalian itu hanyalah dengan sekali tiupan saja,"),
                Ayat(14, "فَإِذَا هُم بِٱلسَّاهِرَةِ", "maka tiba-tiba mereka berada di atas permukaan bumi."),
                Ayat(15, "هَلۡ أَتَىٰكَ حَدِيثُ مُوسَىٰٓ", "Sudahkah sampai kepadamu kisah Musa?"),
                Ayat(16, "إِذۡ نَادَىٰهُ رَبُّهُۥ بِٱلۡوَادِ ٱلۡمُقَدَّسِ طُوًى", "Ketika Tuhannya memanggilnya di lembah suci, yaitu Lembah Tuwa,"),
                Ayat(17, "ٱذۡهَبۡ إِلَىٰ فِرۡعَوۡنَ إِنَّهُۥ طَغَىٰ", "Pergilah kamu kepada Fir'aun, sesungguhnya dia telah melampaui batas,"),
                Ayat(18, "فَقُلۡ هَل لَّكَ إِلَىٰٓ أَن تَزَكَّىٰ", "dan katakanlah, \"Adakah keinginan bagimu untuk membersihkan diri (dari kesesatan)?"),
                Ayat(19, "وَأَهۡدِيَكَ إِلَىٰ رَبِّكَ فَتَخۡشَىٰ", "dan (keinginan bagimu) agar aku tunjukkan kepadamu jalan menuju Tuhanmu, supaya kamu takut kepada-Nya?\""),
                Ayat(20, "فَأَرَىٰهُ ٱلۡأٓيَةَ ٱلۡكُبۡرَىٰ", "Lalu (Musa) memperlihatkan kepadanya mukjizat yang besar."),
                Ayat(21, "فَكَذَّبَ وَعَصَىٰ", "Tetapi dia (Fir'aun) mendustakan dan mendurhakai."),
                Ayat(22, "ثُمَّ أَدۡبَرَ يَسۡعَىٰ", "Kemudian dia berpaling seraya berusaha menantang (Musa)."),
                Ayat(23, "فَحَشَرَ فَنَادَىٰ", "Lalu dia mengumpulkan (pembesar-pembesarnya) lalu berseru memanggil kaumnya."),
                Ayat(24, "فَقَالَ أَنَا۠ رَبُّكُمُ ٱلۡأَعۡلَىٰ", "(Seraya) berkata, \"Akulah tuhanmu yang paling tinggi.\""),
                Ayat(25, "فَأَخَذَهُ ٱللَّهُ نَكَالَ ٱلۡأٓخِرَةِ وَٱلۡأُولَىٰٓ", "Maka Allah mengazabnya dengan azab di akhirat dan azab di dunia."),
                Ayat(26, "إِنَّ فِي ذَٰلِكَ لَعِبۡرَةٗ لِّمَن يَخۡشَىٰٓ", "Sesungguhnya pada yang demikian itu terdapat pelajaran bagi orang yang takut (kepada Allah)."),
                Ayat(27, "ءَأَنتُمۡ أَشَدُّ خَلۡقًا أَمِ ٱلسَّمَآءُۚ بَنَىٰهَا", "Apakah kamu yang lebih sulit penciptaannya ataukah langit? Allah telah membangunnya,"),
                Ayat(28, "رَفَعَ سَمۡكَهَا فَسَوَّىٰهَا", "Dia meninggikan bangunannya lalu menyempurnakannya,"),
                Ayat(29, "وَأَغۡطَشَ لَيۡلَهَا وَأَخۡرَجَ ضُحَىٰهَا", "dan Dia menjadikan malamnya (gelap gulita), dan menjadikan siangnya (terang benderang)."),
                Ayat(30, "وَٱلۡأَرۡضَ بَعۡدَ ذَٰلِكَ دَحَىٰهَا", "Dan bumi sesudah itu dihamparkan-Nya."),
                Ayat(31, "أَخۡرَجَ مِنۡهَا مَآءَهَا وَمَرۡعَىٰهَا", "Dia memancarkan darinya mata airnya, dan (menumbuhkan) tumbuh-tumbuhannya."),
                Ayat(32, "وَٱلۡجِبَالَ أَرۡسَىٰهَا", "Dan gunung-gunung dipancangkan-Nya dengan teguh."),
                Ayat(33, "مَتَٰعٗا لَّكُمۡ وَلِأَنۡعَٰمِكُمۡ", "Semua itu untuk kesenanganmu dan untuk binatang-binatang ternakmu."),
                Ayat(34, "فَإِذَا جَآءَتِ ٱلطَّآمَّةُ ٱلۡكُبۡرَىٰ", "Maka apabila malapetaka besar (hari Kiamat) telah datang,"),
                Ayat(35, "يَوۡمَ يَتَذَكَّرُ ٱلۡإِنسَٰنُ مَا سَعَىٰ", "yaitu pada hari (ketika) manusia teringat akan apa yang telah dikerjakannya,"),
                Ayat(36, "وَبُرِّزَتِ ٱلۡجَحِيمُ لِمَن يَرَىٰ", "dan neraka diperlihatkan dengan jelas kepada setiap orang yang melihat."),
                Ayat(37, "فَأَمَّا مَن طَغَىٰ", "Adapun orang yang melampaui batas,"),
                Ayat(38, "وَءَاثَرَ ٱلۡحَيَوٰةَ ٱلدُّنۡيَا", "dan lebih mengutamakan kehidupan dunia,"),
                Ayat(39, "فَإِنَّ ٱلۡجَحِيمَ هِيَ ٱلۡمَأۡوَىٰ", "maka sesungguhnya nerakalah tempat tinggal(nya)."),
                Ayat(40, "وَأَمَّا مَنۡ خَافَ مَقَامَ رَبِّهِۦ وَنَهَى ٱلنَّفۡسَ عَنِ ٱلۡهَوَىٰ", "Dan adapun orang-orang yang takut kepada kebesaran Tuhannya dan menahan diri dari keinginan hawa nafsunya,"),
                Ayat(41, "فَإِنَّ ٱلۡجَنَّةَ هِيَ ٱلۡمَأۡوَىٰ", "maka sesungguhnya surgalah tempat tinggal(nya)."),
                Ayat(42, "يَسۡـَٔلُونَكَ عَنِ ٱلسَّاعَةِ أَيَّانَ مُرۡسَىٰهَا", "Mereka bertanya kepadamu (Muhammad) tentang hari Kiamat, \"Kapankah terjadinya?\""),
                Ayat(43, "فِيمَ أَنتَ مِن ذِكۡرَىٰهَا", "Untuk apa kamu menyebutkannya?"),
                Ayat(44, "إِلَىٰ رَبِّكَ مُنتَهَىٰهَا", "Kepada Tuhanmulah (dikembalikan) kesudahannya (ketentuan waktunya)."),
                Ayat(45, "إِنَّمَآ أَنتَ مُنذِرُ مَن يَخۡشَىٰهَا", "Kamu hanyalah pemberi peringatan bagi siapa yang takut kepadanya (hari Kiamat)."),
                Ayat(46, "كَأَنَّهُمۡ يَوۡمَ يَرَوۡنَهَا لَمۡ يَلۡبَثُوٓاْ إِلَّا عَشِيَّةً أَوۡ ضُحَىٰهَا", "Pada hari mereka melihat hari Kiamat itu (karena suasananya hebat), mereka merasa seakan-akan hanya (sebentar saja) tinggal (di dunia) pada waktu sore atau pagi hari.")
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
                Ayat(5, "أَمَّا مَنِ ٱسۡتَغۡنَىٰ", "Adapun orang yang merasa dirinya serba cukup,"),
                Ayat(6, "فَأَنتَ لَهُۥ تَصَدَّىٰ", "maka engkau (Muhammad) memberi perhatian kepadanya,"),
                Ayat(7, "وَمَا عَلَيۡكَ أَلَّا يَزَّكَّىٰ", "padahal tidak ada (celaan) atasmu kalau dia tidak menyucikan diri (beriman)."),
                Ayat(8, "وَأَمَّا مَن جَآءَكَ يَسۡعَىٰ", "Dan adapun orang yang datang kepadamu dengan bersegera (untuk mendapatkan pengajaran),"),
                Ayat(9, "وَهُوَ يَخۡشَىٰ", "sedang dia takut (kepada Allah),"),
                Ayat(10, "فَأَنتَ عَنۡهُ تَلَهَّىٰ", "engkau malah mengabaikannya."),
                Ayat(11, "كَلَّآ إِنَّهَا تَذۡكِرَةٞ", "Sekali-kali jangan (begitu)! Sungguh, (ayat-ayat) itu suatu peringatan,"),
                Ayat(12, "فَمَن شَآءَ ذَكَرَهُۥ", "maka barangsiapa menghendaki, tentulah dia akan memperhatikannya,"),
                Ayat(13, "فِي صُحُفٖ مُّكَرَّمَةٖ", "di dalam kitab-kitab yang dimuliakan (di sisi Allah),"),
                Ayat(14, "مَّرۡفُوعَةٖ مُّطَهَّرَةِۭ", "yang ditinggikan (dan) disucikan,"),
                Ayat(15, "بِأَيۡدِي سَفَرَةٖ", "di tangan para penulis (malaikat),"),
                Ayat(16, "كِرَامِۭ بَرَرَةٖ", "yang mulia dan berbakti."),
                Ayat(17, "قُتِلَ ٱلۡإِنسَٰنُ مَآ أَكۡفَرَهُۥ", "Celakalah manusia! Alangkah kufurnya dia!"),
                Ayat(18, "مِنۡ أَيِّ شَيۡءٍ خَلَقَهُۥ", "Dari apakah Dia (Allah) menciptakannya?"),
                Ayat(19, "مِن نُّطۡفَةٍ خَلَقَهُۥ فَقَدَّرَهُۥ", "Dari setetes mani, Dia menciptakannya lalu menentukannya."),
                Ayat(20, "ثُمَّ ٱلسَّبِيلَ يَسَّرَهُۥ", "Kemudian jalannya Dia mudahkan,"),
                Ayat(21, "ثُمَّ أَمَاتَهُۥ فَأَقۡبَرَهُۥ", "kemudian Dia mematikannya dan memasukkannya ke dalam kubur,"),
                Ayat(22, "ثُمَّ إِذَا شَآءَ أَنشَرَهُۥ", "kemudian apabila Dia menghendaki, Dia membangkitkannya kembali."),
                Ayat(23, "كَلَّا لَمَّا يَقۡضِ مَآ أَمَرَهُۥ", "Sekali-kali jangan! Manusia itu belum melaksanakan apa yang diperintahkan Allah kepadanya."),
                Ayat(24, "فَلۡيَنظُرِ ٱلۡإِنسَٰنُ إِلَىٰ طَعَامِهِۦٓ", "Maka hendaklah manusia itu memperhatikan makanannya."),
                Ayat(25, "أَنَّا صَبَبۡنَا ٱلۡمَآءَ صَبّٗا", "Sesungguhnya Kami benar-benar telah mencurahkan air (dari langit),"),
                Ayat(26, "ثُمَّ شَقَقۡنَا ٱلۡأَرۡضَ شَقّٗا", "kemudian Kami belah bumi dengan sebaik-baiknya,"),
                Ayat(27, "فَأَنۢبَتۡنَا فِيهَا حَبّٗا", "lalu Kami tumbuhkan biji-bijian di bumi itu,"),
                Ayat(28, "وَعِنَبٗا وَقَضۡبٗا", "anggur dan sayur-sayuran,"),
                Ayat(29, "وَزَيۡتُونٗا وَنَخۡلٗا", "zaitun dan kurma,"),
                Ayat(30, "وَحَدَآئِقَ غُلۡبٗا", "kebun-kebun yang lebat,"),
                Ayat(31, "وَفَٰكِهَةٗ وَأَبّٗا", "dan buah-buahan serta rumput-rumputan,"),
                Ayat(32, "مَّتَٰعٗا لَّكُمۡ وَلِأَنۡعَٰمِكُمۡ", "untuk kesenanganmu dan untuk hewan-hewan ternakmu."),
                Ayat(33, "فَإِذَا جَآءَتِ ٱلصَّآخَّةُ", "Maka apabila datang suara yang memekakkan (tiupan sangkakala yang kedua),"),
                Ayat(34, "يَوۡمَ يَفِرُّ ٱلۡمَرۡءُ مِنۡ أَخِيهِ", "pada hari (ketika) manusia lari dari saudaranya,"),
                Ayat(35, "وَأُمِّهِۦ وَأَبِيهِ", "dari ibu dan bapaknya,"),
                Ayat(36, "وَصَٰحِبَتِهِۦ وَبَنِيهِ", "dari istri dan anak-anaknya."),
                Ayat(37, "لِكُلِّ ٱمۡرِيٕٖ مِّنۡهُمۡ يَوۡمَئِذٖ شَأۡنٞ يُغۡنِيهِ", "Setiap orang dari mereka pada hari itu mempunyai urusan yang menyibukkannya."),
                Ayat(38, "وُجُوهٞ يَوۡمَئِذٖ مُّسۡفِرَةٞ", "Banyak wajah pada hari itu berseri-seri,"),
                Ayat(39, "ضَاحِكَةٞ مُّسۡتَبۡشِرَةٞ", "tertawa dan bergembira ria."),
                Ayat(40, "وَوُجُوهٞ يَوۡمَئِذٍ عَلَيۡهَا غَبَرَةٞ", "Dan banyak (pula) wajah pada hari itu tertutup debu,"),
                Ayat(41, "تَرۡهَقُهَا قَتَرَةٌ", "tertutup oleh kegelapan."),
                Ayat(42, "أُوْلَٰٓئِكَ هُمُ ٱلۡكَفَرَةُ ٱلۡفَجَرَةُ", "Mereka itulah orang-orang kafir lagi durhaka.")
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
                Ayat(5, "وَإِذَا ٱلۡوُحُوشُ حُشِرَتۡ", "dan apabila binatang-binatang liar dikumpulkan,"),
                Ayat(6, "وَإِذَا ٱلۡبِحَارُ سُجِّرَتۡ", "dan apabila lautan dijadikan meluap,"),
                Ayat(7, "وَإِذَا ٱلنُّفُوسُ زُوِّجَتۡ", "dan apabila roh-roh dipertemukan (dengan tubuhnya),"),
                Ayat(8, "وَإِذَا ٱلۡمَوۡءُۥدَةُ سُئِلَتۡ", "dan apabila bayi-bayi perempuan yang dikubur hidup-hidup ditanya,"),
                Ayat(9, "بِأَيِّ ذَنۢبٖ قُتِلَتۡ", "karena dosa apakah dia dibunuh,"),
                Ayat(10, "وَإِذَا ٱلصُّحُفُ نُشِرَتۡ", "dan apabila catatan-catatan (amal perbuatan manusia) dibuka,"),
                Ayat(11, "وَإِذَا ٱلسَّمَآءُ كُشِطَتۡ", "dan apabila langit dilenyapkan,"),
                Ayat(12, "وَإِذَا ٱلۡجَحِيمُ سُعِّرَتۡ", "dan apabila neraka Jahim dinyalakan,"),
                Ayat(13, "وَإِذَا ٱلۡجَنَّةُ أُزۡلِفَتۡ", "dan apabila surga didekatkan,"),
                Ayat(14, "عَلِمَتۡ نَفۡسٞ مَّآ أَحۡضَرَتۡ", "setiap jiwa akan mengetahui apa yang telah dikerjakannya."),
                Ayat(15, "فَلَآ أُقۡسِمُ بِٱلۡخُنَّسِ", "Sungguh, Aku bersumpah dengan bintang-bintang,"),
                Ayat(16, "ٱلۡجَوَارِ ٱلۡكُنَّسِ", "yang beredar dan terbenam,"),
                Ayat(17, "وَٱلَّيۡلِ إِذَا عَسۡعَسَ", "dan demi malam apabila telah nyaris hilang (gelapnya),"),
                Ayat(18, "وَٱلصُّبۡحِ إِذَا تَنَفَّسَ", "dan demi subuh apabila mulai terang,"),
                Ayat(19, "إِنَّهُۥ لَقَوۡلُ رَسُولٖ كَرِيمٖ", "sungguh, (Al-Qur'an) itu benar-benar firman (Allah yang dibawa oleh) utusan yang mulia (Jibril),"),
                Ayat(20, "ذِي قُوَّةٍ عِندَ ذِي ٱلۡعَرۡشِ مَكِينٖ", "yang memiliki kekuatan, yang mempunyai kedudukan tinggi di sisi (Allah) yang memiliki 'Arsy,"),
                Ayat(21, "مُّطَاعٖ ثُمَّ أَمِينٖ", "yang ditaati di sana (di alam malaikat) dan dipercaya."),
                Ayat(22, "وَمَا صَاحِبُكُم بِمَجۡنُونٖ", "Dan temanmu (Muhammad) itu bukanlah orang gila."),
                Ayat(23, "وَلَقَدۡ رَءَاهُ بِٱلۡأُفُقِ ٱلۡمُبِينِ", "Dan sungguh, dia (Muhammad) telah melihatnya (Jibril) di ufuk yang terang."),
                Ayat(24, "وَمَا هُوَ عَلَى ٱلۡغَيۡبِ بِضَنِينٖ", "Dan dia (Muhammad) bukanlah orang yang kikir (enggan) untuk menerangkan yang gaib."),
                Ayat(25, "وَمَا هُوَ بِقَوۡلِ شَيۡطَٰنٖ رَّجِيمٖ", "Dan (Al-Qur'an) itu bukanlah perkataan setan yang terkutuk."),
                Ayat(26, "فَأَيۡنَ تَذۡهَبُونَ", "Maka ke manakah kamu akan pergi?"),
                Ayat(27, "إِنۡ هُوَ إِلَّا ذِكۡرٞ لِّلۡعَٰلَمِينَ", "Al-Qur'an itu tidak lain adalah peringatan bagi seluruh alam,"),
                Ayat(28, "لِمَن شَآءَ مِنكُمۡ أَن يَسۡتَقِيمَ", "(yaitu) bagi siapa di antara kamu yang menghendaki menempuh jalan yang lurus."),
                Ayat(29, "وَمَا تَشَآءُونَ إِلَّآ أَن يَشَآءَ ٱللَّهُ رَبُّ ٱلۡعَٰلَمِينَ", "Dan kamu tidak dapat menghendaki (menempuh jalan itu) kecuali apabila dikehendaki Allah, Tuhan seluruh alam.")
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
                Ayat(5, "عَلِمَتۡ نَفۡسٞ مَّا قَدَّمَتۡ وَأَخَّرَتۡ", "(pada hari itu) setiap orang mengetahui apa yang telah dikerjakannya dan yang dilalaikannya."),
                Ayat(6, "يَٰٓأَيُّهَا ٱلۡإِنسَٰنُ مَا غَرَّكَ بِرَبِّكَ ٱلۡكَرِيمِ", "Wahai manusia! Apakah yang telah memperdayakan kamu (berbuat durhaka) terhadap Tuhanmu Yang Maha Pemurah?"),
                Ayat(7, "ٱلَّذِي خَلَقَكَ فَسَوَّىٰكَ فَعَدَّلَكَ", "Yang telah menciptakan kamu lalu menyempurnakan kejadianmu dan menjadikan (susunan tubuh)mu seimbang,"),
                Ayat(8, "فِيٓ أَيِّ صُورَةٖ مَّا شَآءَ رَكَّبَكَ", "dalam bentuk apa saja yang Dia kehendaki, Dia menyusun tubuhmu."),
                Ayat(9, "كَلَّا بَلۡ تُكَذِّبُونَ بِٱلدِّينِ", "Sekali-kali tidak! Bahkan kamu mendustakan hari pembalasan."),
                Ayat(10, "وَإِنَّ عَلَيۡكُمۡ لَحَٰفِظِينَ", "Dan sesungguhnya bagi kamu ada (malaikat-malaikat) yang mengawasi (pekerjaanmu),"),
                Ayat(11, "كِرَامٗا كَٰتِبِينَ", "yang mulia (di sisi Allah) dan yang mencatat (pekerjaan-pekerjaanmu itu),"),
                Ayat(12, "يَعۡلَمُونَ مَا تَفۡعَلُونَ", "mereka mengetahui apa yang kamu kerjakan."),
                Ayat(13, "إِنَّ ٱلۡأَبۡرَارَ لَفِي نَعِيمٖ", "Sesungguhnya orang-orang yang berbakti benar-benar berada dalam (surga yang penuh) kenikmatan,"),
                Ayat(14, "وَإِنَّ ٱلۡفُجَّارَ لَفِي جَحِيمٖ", "dan sesungguhnya orang-orang yang durhaka benar-benar berada dalam neraka."),
                Ayat(15, "يَصۡلَوۡنَهَا يَوۡمَ ٱلدِّينِ", "Mereka masuk ke dalamnya pada hari pembalasan."),
                Ayat(16, "وَمَا هُمۡ عَنۡهَا بِغَآئِبِينَ", "Dan mereka tidak pernah keluar darinya."),
                Ayat(17, "وَمَآ أَدۡرَىٰكَ مَا يَوۡمُ ٱلدِّينِ", "Dan tahukah kamu apakah hari pembalasan itu?"),
                Ayat(18, "ثُمَّ مَآ أَدۡرَىٰكَ مَا يَوۡمُ ٱلدِّينِ", "Sekali lagi, tahukah kamu apakah hari pembalasan itu?"),
                Ayat(19, "يَوۡمَ لَا تَمۡلِكُ نَفۡسٞ لِّنَفۡسٖ شَيۡـٔٗاۖ وَٱلۡأَمۡرُ يَوۡمَئِذٖ لِّلَّهِ", "(Yaitu) hari (ketika) seseorang tidak berdaya sedikit pun untuk menolong orang lain. Dan segala urusan pada hari itu dalam kekuasaan Allah.")
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
                Ayat(5, "لِيَوۡمٍ عَظِيمٖ", "pada suatu hari yang besar (hari Kiamat),"),
                Ayat(6, "يَوۡمَ يَقُومُ ٱلنَّاسُ لِرَبِّ ٱلۡعَٰلَمِينَ", "yaitu hari (ketika) manusia berdiri menghadap Tuhan seluruh alam?"),
                Ayat(7, "كَلَّآ إِنَّ كِتَٰبَ ٱلۡفُجَّارِ لَفِي سِجِّينٖ", "Sekali-kali tidak! Sesungguhnya catatan orang yang durhaka benar-benar tersimpan dalam Sijjin."),
                Ayat(8, "وَمَآ أَدۡرَىٰكَ مَا سِجِّينٞ", "Dan tahukah kamu apakah Sijjin itu?"),
                Ayat(9, "كِتَٰبٞ مَّرۡقُومٞ", "(Ialah) kitab yang bertulis (yang berisi catatan amal)."),
                Ayat(10, "وَيۡلٞ يَوۡمَئِذٖ لِّلۡمُكَذِّبِينَ", "Celakalah pada hari itu, bagi orang-orang yang mendustakan,"),
                Ayat(11, "ٱلَّذِينَ يُكَذِّبُونَ بِيَوۡمِ ٱلدِّينِ", "yaitu orang-orang yang mendustakan hari pembalasan."),
                Ayat(12, "وَمَا يُكَذِّبُ بِهِۦٓ إِلَّا كُلُّ مُعۡتَدٍ أَثِيمٍ", "Dan tidak ada yang mendustakan hari pembalasan itu melainkan setiap orang yang melampaui batas dan berdosa,"),
                Ayat(13, "إِذَا تُتۡلَىٰ عَلَيۡهِ ءَايَٰتُنَا قَالَ أَسَٰطِيرُ ٱلۡأَوَّلِينَ", "yang apabila dibacakan kepadanya ayat-ayat Kami, dia berkata, \"(Ini adalah) dongeng orang-orang dahulu.\""),
                Ayat(14, "كَلَّاۖ بَلۡۜ رَانَ عَلَىٰ قُلُوبِهِم مَّا كَانُواْ يَكۡسِبُونَ", "Sekali-kali tidak! Bahkan apa yang mereka kerjakan itu telah menutupi hati mereka."),
                Ayat(15, "كَلَّآ إِنَّهُمۡ عَن رَّبِّهِمۡ يَوۡمَئِذٖ لَّمَحۡجُوبُونَ", "Sekali-kali tidak! Sesungguhnya mereka pada hari itu benar-benar terhalang dari (melihat) Tuhan mereka."),
                Ayat(16, "ثُمَّ إِنَّهُمۡ لَصَالُواْ ٱلۡجَحِيمِ", "Kemudian sesungguhnya mereka benar-benar masuk neraka."),
                Ayat(17, "ثُمَّ يُقَالُ هَٰذَا ٱلَّذِي كُنتُم بِهِۦ تُكَذِّبُونَ", "Kemudian dikatakan (kepada mereka), \"Inilah (azab) yang dahulu kamu dustakan.\""),
                Ayat(18, "كَلَّآ إِنَّ كِتَٰبَ ٱلۡأَبۡرَارِ لَفِي عِلِّيِّينَ", "Sekali-kali tidak! Sesungguhnya catatan orang-orang yang berbakti benar-benar tersimpan dalam 'Illiyyin."),
                Ayat(19, "وَمَآ أَدۡرَىٰكَ مَا عِلِّيُّونَ", "Dan tahukah kamu apakah 'Illiyyin itu?"),
                Ayat(20, "كِتَٰبٞ مَّرۡقُومٞ", "(Ialah) kitab yang bertulis (yang berisi catatan amal),"),
                Ayat(21, "يَشۡهَدُهُ ٱلۡمُقَرَّبُونَ", "yang disaksikan oleh (malaikat-malaikat) yang didekatkan (kepada Allah)."),
                Ayat(22, "إِنَّ ٱلۡأَبۡرَارَ لَفِي نَعِيمٍ", "Sesungguhnya orang-orang yang berbakti benar-benar berada dalam (surga yang penuh) kenikmatan,"),
                Ayat(23, "عَلَى ٱلۡأَرَآئِكِ يَنظُرُونَ", "mereka (duduk) di atas dipan-dipan sambil memandang."),
                Ayat(24, "تَعۡرِفُ فِي وُجُوهِهِمۡ نَضۡرَةَ ٱلنَّعِيمِ", "Kamu dapat mengetahui dari wajah mereka kesenangan hidup yang penuh kenikmatan."),
                Ayat(25, "يُسۡقَوۡنَ مِن رَّحِيقٖ مَّخۡتُومٍ", "Mereka diberi minum dari khamar murni yang dilak (tempatnya),"),
                Ayat(26, "خِتَٰمُهُۥ مِسۡكٞۚ وَفِي ذَٰلِكَ فَلۡيَتَنَافَسِ ٱلۡمُتَنَٰفِسُونَ", "laknya adalah kesturi; dan untuk yang demikian itu hendaknya orang berlomba-lomba."),
                Ayat(27, "وَمِزَاجُهُۥ مِن تَسۡنِيمٍ", "Dan campurannya adalah air Tasnim,"),
                Ayat(28, "عَيۡنٗا يَشۡرَبُ بِهَا ٱلۡمُقَرَّبُونَ", "(yaitu) mata air yang diminum oleh orang-orang yang didekatkan kepada Allah."),
                Ayat(29, "إِنَّ ٱلَّذِينَ أَجۡرَمُواْ كَانُواْ مِنَ ٱلَّذِينَ ءَامَنُواْ يَضۡحَكُونَ", "Sesungguhnya orang-orang yang berdosa, mereka menertawakan orang-orang yang beriman."),
                Ayat(30, "وَإِذَا مَرُّواْ بِهِمۡ يَتَغَامَزُونَ", "Dan apabila mereka (orang-orang yang berdosa) melintas di hadapan mereka (orang-orang yang beriman), mereka saling mengedip-ngedipkan matanya."),
                Ayat(31, "وَإِذَا ٱنقَلَبُوٓاْ إِلَىٰٓ أَهۡلِهِمُ ٱنقَلَبُواْ فَكِهِينَ", "Dan apabila mereka kembali kepada kaumnya, mereka kembali dengan gembira ria."),
                Ayat(32, "وَإِذَا رَأَوۡهُمۡ قَالُوٓاْ إِنَّ هَٰٓؤُلَآءِ لَضَآلُّونَ", "Dan apabila mereka melihat mereka (orang-orang mukmin), mereka mengatakan, \"Sesungguhnya mereka itu benar-benar orang-orang yang sesat.\""),
                Ayat(33, "وَمَآ أُرۡسِلُواْ عَلَيۡهِمۡ حَٰفِظِينَ", "Padahal mereka (orang-orang yang berdosa) itu tidak diutus sebagai penjaga (bagi orang-orang mukmin)."),
                Ayat(34, "فَٱلۡيَوۡمَ ٱلَّذِينَ ءَامَنُواْ مِنَ ٱلۡكُفَّارِ يَضۡحَكُونَ", "Maka pada hari ini, orang-orang yang beriman menertawakan orang-orang kafir,"),
                Ayat(35, "عَلَى ٱلۡأَرَآئِكِ يَنظُرُونَ", "mereka (duduk) di atas dipan-dipan sambil memandang."),
                Ayat(36, "هَلۡ ثُوِّبَ ٱلۡكُفَّارُ مَا كَانُواْ يَفۡعَلُونَ", "Apakah orang-orang kafir itu telah diberi balasan terhadap apa yang dahulu mereka kerjakan?")
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
                Ayat(5, "وَأَذِنَتۡ لِرَبِّهَا وَحُقَّتۡ", "dan patuh kepada Tuhannya, dan sudah semestinya patuh."),
                Ayat(6, "يَٰٓأَيُّهَا ٱلۡإِنسَٰنُ إِنَّكَ كَادِحٌ إِلَىٰ رَبِّكَ كَدۡحٗا فَمُلَٰقِيهِ", "Wahai manusia! Sesungguhnya kamu telah bekerja keras menuju Tuhanmu, maka kamu akan menemui-Nya."),
                Ayat(7, "فَأَمَّا مَنۡ أُوتِيَ كِتَٰبَهُۥ بِيَمِينِهِۦ", "Adapun orang yang diberikan kitabnya dari sebelah kanannya,"),
                Ayat(8, "فَسَوۡفَ يُحَاسَبُ حِسَابٗا يَسِيرٗا", "maka dia akan diperiksa dengan pemeriksaan yang mudah,"),
                Ayat(9, "وَيَنقَلِبُ إِلَىٰٓ أَهۡلِهِۦ مَسۡرُورٗا", "dan dia akan kembali kepada kaumnya (yang sama-sama beriman) dengan gembira."),
                Ayat(10, "وَأَمَّا مَنۡ أُوتِيَ كِتَٰبَهُۥ وَرَآءَ ظَهۡرِهِۦ", "Dan adapun orang yang diberikan kitabnya dari belakang punggungnya,"),
                Ayat(11, "فَسَوۡفَ يَدۡعُواْ ثُبُورٗا", "maka dia akan berteriak, \"Celakalah aku!\""),
                Ayat(12, "وَيَصۡلَىٰ سَعِيرًا", "Dan dia akan masuk ke dalam api yang menyala-nyala (neraka)."),
                Ayat(13, "إِنَّهُۥ كَانَ فِيٓ أَهۡلِهِۦ مَسۡرُورًا", "Sesungguhnya dia dahulu (di dunia) bergembira di kalangan keluarganya (yang sama-sama kafir)."),
                Ayat(14, "إِنَّهُۥ ظَنَّ أَن لَّن يَحُورَ", "Sesungguhnya dia menyangka bahwa dia tidak akan kembali (kepada Tuhannya)."),
                Ayat(15, "بَلَىٰٓۚ إِنَّ رَبَّهُۥ كَانَ بِهِۦ بَصِيرٗا", "Sekali-kali tidak! Sesungguhnya Tuhannya selalu melihatnya."),
                Ayat(16, "فَلَآ أُقۡسِمُ بِٱلشَّفَقِ", "Maka sesungguhnya Aku bersumpah dengan cahaya merah di waktu senja,"),
                Ayat(17, "وَٱلَّيۡلِ وَمَا وَسَقَ", "dan dengan malam dan apa yang diselubunginya,"),
                Ayat(18, "وَٱلۡقَمَرِ إِذَا ٱتَّسَقَ", "dan dengan bulan apabila jadi purnama,"),
                Ayat(19, "لَتَرۡكَبُنَّ طَبَقًا عَن طَبَقٖ", "sungguh, kamu akan melalui tingkat demi tingkat (dalam kehidupan)."),
                Ayat(20, "فَمَا لَهُمۡ لَا يُؤۡمِنُونَ", "Maka mengapa mereka tidak beriman?"),
                Ayat(21, "وَإِذَا قُرِئَ عَلَيۡهِمُ ٱلۡقُرۡءَانُ لَا يَسۡجُدُونَۤ", "Dan apabila Al-Qur'an dibacakan kepada mereka, mereka tidak (mau) bersujud,"),
                Ayat(22, "بَلِ ٱلَّذِينَ كَفَرُواْ يُكَذِّبُونَ", "bahkan orang-orang kafir itu mendustakan(nya)."),
                Ayat(23, "وَٱللَّهُ أَعۡلَمُ بِمَا يُوعُونَ", "Padahal Allah mengetahui apa yang mereka sembunyikan (dalam hati mereka)."),
                Ayat(24, "فَبَشِّرۡهُم بِعَذَابٍ أَلِيمٍ", "Maka beri kabar gembiralah mereka dengan azab yang pedih,"),
                Ayat(25, "إِلَّا ٱلَّذِينَ ءَامَنُواْ وَعَمِلُواْ ٱلصَّٰلِحَٰتِ لَهُمۡ أَجۡرٌ غَيۡرُ مَمۡنُونِۭ", "tetapi orang-orang yang beriman dan mengerjakan kebajikan, mereka mendapat pahala yang tidak putus-putusnya.")
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
                Ayat(5, "ٱلنَّارِ ذَاتِ ٱلۡوَقُودِ", "(yang berisi) api yang mempunyai kayu bakar,"),
                Ayat(6, "إِذۡ هُمۡ عَلَيۡهَا قُعُودٞ", "ketika mereka duduk di sekitarnya,"),
                Ayat(7, "وَهُمۡ عَلَىٰ مَا يَفۡعَلُونَ بِٱلۡمُؤۡمِنِينَ شُهُودٞ", "sedang mereka menyaksikan apa yang mereka perbuat terhadap orang-orang yang beriman."),
                Ayat(8, "وَمَا نَقَمُواْ مِنۡهُمۡ إِلَّآ أَن يُؤۡمِنُواْ بِٱللَّهِ ٱلۡعَزِيزِ ٱلۡحَمِيدِ", "Dan mereka tidak menyiksa orang-orang mukmin itu melainkan karena orang-orang mukmin itu beriman kepada Allah Yang Maha Perkasa lagi Maha Terpuji,"),
                Ayat(9, "ٱلَّذِي لَهُۥ مُلۡكُ ٱلسَّمَٰوَٰتِ وَٱلۡأَرۡضِۚ وَٱللَّهُ عَلَىٰ كُلِّ شَيۡءٖ شَهِيدٌ", "Yang memiliki kerajaan langit dan bumi. Dan Allah Maha Menyaksikan segala sesuatu."),
                Ayat(10, "إِنَّ ٱلَّذِينَ فَتَنُواْ ٱلۡمُؤۡمِنِينَ وَٱلۡمُؤۡمِنَٰتِ ثُمَّ لَمۡ يَتُوبُواْ فَلَهُمۡ عَذَابُ جَهَنَّمَ وَلَهُمۡ عَذَابُ ٱلۡحَرِيقِ", "Sesungguhnya orang-orang yang mendatangkan cobaan kepada orang-orang yang mukmin laki-laki dan perempuan kemudian mereka tidak bertobat, maka bagi mereka azab Jahanam dan bagi mereka azab (neraka) yang membakar."),
                Ayat(11, "إِنَّ ٱلَّذِينَ ءَامَنُواْ وَعَمِلُواْ ٱلصَّٰلِحَٰتِ لَهُمۡ جَنَّٰتٞ تَجۡرِي مِن تَحۡتِهَا ٱلۡأَنۡهَٰرُۚ ذَٰلِكَ ٱلۡفَوۡزُ ٱلۡكَبِيرُ", "Sesungguhnya orang-orang yang beriman dan mengerjakan amal-amal saleh, bagi mereka surga yang mengalir di bawahnya sungai-sungai; itulah keberuntungan yang besar."),
                Ayat(12, "إِنَّ بَطۡشَ رَبِّكَ لَشَدِيدٌ", "Sesungguhnya azab Tuhanmu benar-benar keras."),
                Ayat(13, "إِنَّهُۥ هُوَ يُبۡدِئُ وَيُعِيدُ", "Sesungguhnya Dialah yang memulai penciptaan dan yang mengembalikannya (menghidupkan kembali)."),
                Ayat(14, "وَهُوَ ٱلۡغَفُورُ ٱلۡوَدُودُ", "Dan Dialah Yang Maha Pengampun, Maha Pengasih,"),
                Ayat(15, "ذُو ٱلۡعَرۡشِ ٱلۡمَجِيدِ", "Yang memiliki 'Arsy, lagi Maha Mulia,"),
                Ayat(16, "فَعَّالٞ لِّمَا يُرِيدُ", "Maha Kuasa berbuat apa yang dikehendaki-Nya."),
                Ayat(17, "هَلۡ أَتَىٰكَ حَدِيثُ ٱلۡجُنُودِ", "Sudahkah sampai kepadamu berita tentang bala tentara,"),
                Ayat(18, "فِرۡعَوۡنَ وَثَمُودَ", "(yaitu) Fir'aun dan (kaum) Tsamud?"),
                Ayat(19, "بَلِ ٱلَّذِينَ كَفَرُواْ فِي تَكۡذِيبٖ", "Tetapi orang-orang kafir selalu dalam keadaan mendustakan,"),
                Ayat(20, "وَٱللَّهُ مِن وَرَآئِهِم مُّحِيطُۢ", "padahal Allah mengepung mereka dari belakang."),
                Ayat(21, "بَلۡ هُوَ قُرۡءَانٞ مَّجِيدٞ", "Bahkan yang didustakan mereka itu ialah Al-Qur'an yang mulia,"),
                Ayat(22, "فِي لَوۡحٖ مَّحۡفُوظِۭ", "yang (tersimpan) dalam Lauh Mahfuzh.")
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
                Ayat(5, "فَلۡيَنظُرِ ٱلۡإِنسَٰنُ مِمَّ خُلِقَ", "Maka hendaklah manusia memperhatikan dari apa dia diciptakan?"),
                Ayat(6, "خُلِقَ مِن مَّآءٖ دَافِقٖ", "Dia diciptakan dari air (mani) yang dipancarkan,"),
                Ayat(7, "يَخۡرُجُ مِن بَيۡنِ ٱلصُّلۡبِ وَٱلتَّرَآئِبِ", "yang keluar dari antara tulang sulbi laki-laki dan tulang dada perempuan."),
                Ayat(8, "إِنَّهُۥ عَلَىٰ رَجۡعِهِۦ لَقَادِرٞ", "Sesungguhnya Allah benar-benar kuasa untuk mengembalikannya (hidup sesudah mati)."),
                Ayat(9, "يَوۡمَ تُبۡلَى ٱلسَّرَآئِرُ", "Pada hari dinampakkan segala rahasia,"),
                Ayat(10, "فَمَا لَهُۥ مِن قُوَّةٖ وَلَا نَاصِرٖ", "maka sekali-kali tidak ada bagi manusia itu suatu kekuatan pun dan tidak (pula) seorang penolong."),
                Ayat(11, "وَٱلسَّمَآءِ ذَاتِ ٱلرَّجۡعِ", "Demi langit yang mengandung hujan,"),
                Ayat(12, "وَٱلۡأَرۡضِ ذَاتِ ٱلصَّدۡعِ", "dan bumi yang merekah (menumbuhkan tumbuh-tumbuhan),"),
                Ayat(13, "إِنَّهُۥ لَقَوۡلٞ فَصۡلٞ", "sungguh, (Al-Qur'an) itu benar-benar firman yang memisahkan (antara yang hak dan yang batil)."),
                Ayat(14, "وَمَا هُوَ بِٱلۡهَزۡلِ", "dan sekali-kali bukanlah senda gurau."),
                Ayat(15, "إِنَّهُمۡ يَكِيدُونَ كَيۡدٗا", "Sesungguhnya mereka merencanakan tipu daya yang jahat,"),
                Ayat(16, "وَأَكِيدُ كَيۡدٗا", "dan Aku pun membuat rencana (pula) dengan sebenar-benarnya."),
                Ayat(17, "فَمَهِّلِ ٱلۡكَٰفِرِينَ أَمۡهِلۡهُمۡ رُوَيۡدَۢا", "Karena itu beri tangguhlah orang-orang kafir itu, beri tangguh mereka barang sebentar.")
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
                Ayat(5, "فَجَعَلَهُۥ غُثَآءً أَحۡوَىٰ", "lalu dijadikan-Nya (rumput-rumput) itu kering kehitam-hitaman."),
                Ayat(6, "سَنُقۡرِئُكَ فَلَا تَنسَىٰٓ", "Kami akan membacakan (Al-Qur'an) kepadamu (Muhammad), maka kamu tidak akan lupa,"),
                Ayat(7, "إِلَّا مَا شَآءَ ٱللَّهُۚ إِنَّهُۥ يَعۡلَمُ ٱلۡجَهۡرَ وَمَا يَخۡفَىٰ", "kecuali kalau Allah menghendaki. Sesungguhnya Dia mengetahui yang terang dan yang tersembunyi."),
                Ayat(8, "وَنُيَسِّرُكَ لِلۡيُسۡرَىٰ", "Dan Kami akan memberimu taufik ke jalan yang mudah,"),
                Ayat(9, "فَذَكِّرۡ إِن نَّفَعَتِ ٱلذِّكۡرَىٰ", "oleh sebab itu berikanlah peringatan karena peringatan itu bermanfaat,"),
                Ayat(10, "سَيَذَّكَّرُ مَن يَخۡشَىٰ", "orang yang takut (kepada Allah) akan mendapat pelajaran,"),
                Ayat(11, "وَيَتَجَنَّبُهَا ٱلۡأَشۡقَى", "sedangkan orang-orang yang celaka (kafir) akan menjauhinya."),
                Ayat(12, "ٱلَّذِي يَصۡلَى ٱلنَّارَ ٱلۡكُبۡرَىٰ", "(Yaitu) orang yang akan memasuki api yang besar (neraka)."),
                Ayat(13, "ثُمَّ لَا يَمُوتُ فِيهَا وَلَا يَحۡيَىٰ", "Kemudian dia tidak akan mati di dalamnya dan tidak (pula) hidup."),
                Ayat(14, "قَدۡ أَفۡلَحَ مَن تَزَكَّىٰ", "Sungguh beruntung orang yang membersihkan diri (dengan beriman),"),
                Ayat(15, "وَذَكَرَ ٱسۡمَ رَبِّهِۦ فَصَلَّىٰ", "dan dia ingat nama Tuhannya, lalu dia salat."),
                Ayat(16, "بَلۡ تُؤۡثِرُونَ ٱلۡحَيَوٰةَ ٱلدُّنۡيَا", "Tetapi kamu (orang-orang kafir) memilih kehidupan dunia."),
                Ayat(17, "وَٱلۡأٓخِرَةُ خَيۡرٞ وَأَبۡقَىٰٓ", "Padahal kehidupan akhirat itu lebih baik dan lebih kekal."),
                Ayat(18, "إِنَّ هَٰذَا لَفِي ٱلصُّحُفِ ٱلۡأُولَىٰ", "Sesungguhnya ini benar-benar terdapat dalam lembaran-lembaran (kitab-kitab) yang terdahulu,"),
                Ayat(19, "صُحُفِ إِبۡرَٰهِيمَ وَمُوسَىٰ", "(yaitu) lembaran-lembaran (kitab-kitab) Ibrahim dan Musa.")
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
                Ayat(5, "تُسۡقَىٰ مِنۡ عَيۡنٍ ءَانِيَةٖ", "diberi minum dari sumber mata air yang mendidih."),
                Ayat(6, "لَّيۡسَ لَهُمۡ طَعَامٌ إِلَّا مِن ضَرِيعٖ", "Tidak ada makanan bagi mereka selain dari pohon yang berduri,"),
                Ayat(7, "لَّا يُسۡمِنُ وَلَا يُغۡنِي مِن جُوعٖ", "yang tidak menggemukkan dan tidak pula menghilangkan lapar."),
                Ayat(8, "وُجُوهٞ يَوۡمَئِذٖ نَّاعِمَةٞ", "Banyak wajah pada hari itu berseri-seri,"),
                Ayat(9, "لِّسَعۡيِهَا رَاضِيَةٞ", "merasa senang karena usahanya sendiri,"),
                Ayat(10, "فِي جَنَّةٍ عَالِيَةٖ", "dalam surga yang tinggi,"),
                Ayat(11, "لَّا تَسۡمَعُ فِيهَا لَٰغِيَةٗ", "tidak kamu dengar di dalamnya perkataan yang tidak berguna."),
                Ayat(12, "فِيهَا عَيۡنٞ جَارِيَةٞ", "Di dalamnya ada mata air yang mengalir."),
                Ayat(13, "فِيهَا سُرُرٞ مَّرۡفُوعَةٞ", "Di dalamnya ada dipan-dipan yang ditinggikan,"),
                Ayat(14, "وَأَكۡوَابٞ مَّوۡضُوعَةٞ", "dan gelas-gelas yang siap (di dekatnya),"),
                Ayat(15, "وَنَمَارِقُ مَصۡفُوفَةٞ", "dan bantal-bantal sandaran yang tersusun,"),
                Ayat(16, "وَزَرَابِيُّ مَبۡثُوثَةٌ", "dan permadani-permadani yang terhampar."),
                Ayat(17, "أَفَلَا يَنظُرُونَ إِلَى ٱلۡإِبِلِ كَيۡفَ خُلِقَتۡ", "Maka mengapa mereka tidak memperhatikan unta, bagaimana dia diciptakan?"),
                Ayat(18, "وَإِلَى ٱلسَّمَآءِ كَيۡفَ رُفِعَتۡ", "Dan langit, bagaimana ia ditinggikan?"),
                Ayat(19, "وَإِلَى ٱلۡجِبَالِ كَيۡفَ نُصِبَتۡ", "Dan gunung-gunung, bagaimana ia ditegakkan?"),
                Ayat(20, "وَإِلَى ٱلۡأَرۡضِ كَيۡفَ سُطِحَتۡ", "Dan bumi, bagaimana ia dihamparkan?"),
                Ayat(21, "فَذَكِّرۡ إِنَّمَآ أَنتَ مُذَكِّرٞ", "Maka berilah peringatan, karena sesungguhnya engkau (Muhammad) hanyalah pemberi peringatan."),
                Ayat(22, "لَّسۡتَ عَلَيۡهِم بِمُصَيۡطِرٍ", "Engkau bukanlah orang yang berkuasa atas mereka,"),
                Ayat(23, "إِلَّا مَن تَوَلَّىٰ وَكَفَرَ", "tetapi orang yang berpaling dan kafir,"),
                Ayat(24, "فَيُعَذِّبُهُ ٱللَّهُ ٱلۡعَذَابَ ٱلۡأَكۡبَرَ", "maka Allah akan mengazabnya dengan azab yang paling besar."),
                Ayat(25, "إِنَّ إِلَيۡنَآ إِيَابَهُمۡ", "Sesungguhnya kepada Kamilah kembali mereka,"),
                Ayat(26, "ثُمَّ إِنَّ عَلَيۡنَا حِسَابَهُم", "kemudian sesungguhnya (kewajiban) Kamilah menghisab mereka.")
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
                Ayat(5, "هَلۡ فِي ذَٰلِكَ قَسَمٞ لِّذِي حِجۡرٍ", "Pada yang demikian itu terdapat sumpah (yang dapat diterima) oleh orang-orang yang berakal."),
                Ayat(6, "أَلَمۡ تَرَ كَيۡفَ فَعَلَ رَبُّكَ بِعَادٍ", "Tidakkah engkau (Muhammad) perhatikan bagaimana Tuhanmu berbuat terhadap (kaum) 'Ad?"),
                Ayat(7, "إِرَمَ ذَاتِ ٱلۡعِمَادِ", "(yaitu) penduduk Iram yang memiliki bangunan-bangunan yang tinggi,"),
                Ayat(8, "ٱلَّتِي لَمۡ يُخۡلَقۡ مِثۡلُهَا فِي ٱلۡبِلَٰدِ", "yang belum pernah dibangun (suatu kota) seperti itu di negeri-negeri lain,"),
                Ayat(9, "وَثَمُودَ ٱلَّذِينَ جَابُواْ ٱلصَّخۡرَ بِٱلۡوَادِ", "dan (terhadap) kaum Tsamud yang memotong batu-batu besar di lembah,"),
                Ayat(10, "وَفِرۡعَوۡنَ ذِي ٱلۡأَوۡتَادِ", "dan (terhadap) Fir'aun yang mempunyai pasak-pasak (tentara yang banyak),"),
                Ayat(11, "ٱلَّذِينَ طَغَوۡاْ فِي ٱلۡبِلَٰدِ", "yang berbuat sewenang-wenang dalam negeri,"),
                Ayat(12, "فَأَكۡثَرُواْ فِيهَا ٱلۡفَسَادَ", "lalu mereka berbuat banyak kerusakan dalam negeri itu,"),
                Ayat(13, "فَصَبَّ عَلَيۡهِمۡ رَبُّكَ سَوۡطَ عَذَابٍ", "karena itu Tuhanmu menimpakan kepada mereka cemeti azab,"),
                Ayat(14, "إِنَّ رَبَّكَ لَبِٱلۡمِرۡصَادِ", "sungguh, Tuhanmu benar-benar mengawasi."),
                Ayat(15, "فَأَمَّا ٱلۡإِنسَٰنُ إِذَا مَا ٱبۡتَلَىٰهُ رَبُّهُۥ فَأَكۡرَمَهُۥ وَنَعَّمَهُۥ فَيَقُولُ رَبِّيٓ أَكۡرَمَنِ", "Adapun manusia, apabila Tuhannya mengujinya lalu dimuliakan-Nya dan diberi-Nya kesenangan, maka dia akan berkata, \"Tuhanku telah memuliakanku.\""),
                Ayat(16, "وَأَمَّآ إِذَا مَا ٱبۡتَلَىٰهُ فَقَدَرَ عَلَيۡهِ رِزۡقَهُۥ فَيَقُولُ رَبِّيٓ أَهَٰنَنِ", "Tetapi apabila Tuhannya mengujinya lalu membatasi rezekinya, maka dia berkata, \"Tuhanku menghinakanku.\""),
                Ayat(17, "كَلَّاۖ بَل لَّا تُكۡرِمُونَ ٱلۡيَتِيمَ", "Sekali-kali tidak! Bahkan kamu tidak memuliakan anak yatim,"),
                Ayat(18, "وَلَا تَحَٰٓضُّونَ عَلَىٰ طَعَامِ ٱلۡمِسۡكِينِ", "dan kamu tidak saling mengajak memberi makan orang miskin,"),
                Ayat(19, "وَتَأۡكُلُونَ ٱلتُّرَاثَ أَكۡلٗا لَّمّٗا", "dan kamu memakan harta warisan dengan cara mencampur-adukkan (yang halal dan yang batil),"),
                Ayat(20, "وَتُحِبُّونَ ٱلۡمَالَ حُبّٗا جَمّٗا", "dan kamu mencintai harta benda dengan kecintaan yang berlebihan."),
                Ayat(21, "كَلَّآ إِذَا دُكَّتِ ٱلۡأَرۡضُ دَكّٗا دَكّٗا", "Sekali-kali tidak! Apabila bumi diguncangkan berturut-turut,"),
                Ayat(22, "وَجَآءَ رَبُّكَ وَٱلۡمَلَكُ صَفّٗا صَفّٗا", "dan datanglah Tuhanmu; sedang malaikat berbaris-baris."),
                Ayat(23, "وَجَآءُو يَوۡمَئِذِۢ بِجَهَنَّمَ يَوۡمَئِذٖ يَتَذَكَّرُ ٱلۡإِنسَٰنُ وَأَنَّىٰ لَهُ ٱلذِّكۡرَىٰ", "Dan pada hari itu diperlihatkan neraka Jahanam; dan pada hari itu ingatlah manusia, akan tetapi tidak berguna lagi mengingat itu baginya."),
                Ayat(24, "يَقُولُ يَٰلَيۡتَنِي قَدَّمۡتُ لِحَيَاتِي", "Dia berkata, \"Alangkah baiknya sekiranya dahulu aku mengerjakan (kebajikan) untuk hidupku ini.\""),
                Ayat(25, "فَيَوۡمَئِذٖ لَّا يُعَذِّبُ عَذَابَهُۥٓ أَحَدٞ", "Maka pada hari itu tidak ada seorang pun yang mengazab seperti azab-Nya,"),
                Ayat(26, "وَلَا يُوثِقُ وَثَاقَهُۥٓ أَحَدٞ", "dan tidak ada seorang pun yang mengikat seperti ikatan-Nya."),
                Ayat(27, "يَٰٓأَيَّتُهَا ٱلنَّفۡسُ ٱلۡمُطۡمَئِنَّةُ", "Wahai jiwa yang tenang!"),
                Ayat(28, "ٱرۡجِعِيٓ إِلَىٰ رَبِّكِ رَاضِيَةٗ مَّرۡضِيَّةٗ", "Kembalilah kepada Tuhanmu dengan hati yang rida dan diridai-Nya."),
                Ayat(29, "فَٱدۡخُلِي فِي عِبَٰدِي", "Maka masuklah ke dalam golongan hamba-hamba-Ku,"),
                Ayat(30, "وَٱدۡخُلِي جَنَّتِي", "dan masuklah ke dalam surga-Ku.")
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
                Ayat(5, "أَيَحۡسَبُ أَن لَّن يَقۡدِرَ عَلَيۡهِ أَحَدٞ", "Apakah dia (manusia) mengira bahwa tidak ada seorang pun yang mampu mengalahkannya?"),
                Ayat(6, "يَقُولُ أَهۡلَكۡتُ مَالٗا لُّبَدًا", "Dia mengatakan, \"Aku telah menghabiskan harta yang banyak.\""),
                Ayat(7, "أَيَحۡسَبُ أَن لَّمۡ يَرَهُۥٓ أَحَدٌ", "Apakah dia mengira bahwa tidak ada seorang pun yang melihatnya?"),
                Ayat(8, "أَلَمۡ نَجۡعَل لَّهُۥ عَيۡنَيۡنِ", "Bukankah Kami telah memberikan kepadanya dua mata,"),
                Ayat(9, "وَلِسَانٗا وَشَفَتَيۡنِ", "lidah dan dua bibir?"),
                Ayat(10, "وَهَدَيۡنَٰهُ ٱلنَّجۡدَيۡنِ", "Dan Kami telah menunjukkan kepadanya dua jalan (baik dan buruk)."),
                Ayat(11, "فَلَا ٱقۡتَحَمَ ٱلۡعَقَبَةَ", "Tetapi dia tidak menempuh jalan yang mendaki dan sukar."),
                Ayat(12, "وَمَآ أَدۡرَىٰكَ مَا ٱلۡعَقَبَةُ", "Dan tahukah kamu apakah jalan yang mendaki dan sukar itu?"),
                Ayat(13, "فَكُّ رَقَبَةٍ", "(Yaitu) melepaskan perbudakan (hamba sahaya),"),
                Ayat(14, "أَوۡ إِطۡعَٰمٞ فِي يَوۡمٖ ذِي مَسۡغَبَةٖ", "atau memberi makan pada hari kelaparan,"),
                Ayat(15, "يَتِيمٗا ذَا مَقۡرَبَةٍ", "(kepada) anak yatim yang ada hubungan kerabat,"),
                Ayat(16, "أَوۡ مِسۡكِينٗا ذَا مَتۡرَبَةٖ", "atau orang miskin yang sangat fakir."),
                Ayat(17, "ثُمَّ كَانَ مِنَ ٱلَّذِينَ ءَامَنُواْ وَتَوَاصَوۡاْ بِٱلصَّبۡرِ وَتَوَاصَوۡاْ بِٱلۡمَرۡحَمَةِ", "Dan dia (tidak pula) termasuk orang-orang yang beriman dan saling berpesan untuk bersabar dan saling berpesan untuk berkasih sayang."),
                Ayat(18, "أُوْلَٰٓئِكَ أَصۡحَٰبُ ٱلۡمَيۡمَنَةِ", "Mereka (orang-orang yang berbuat baik) itu adalah golongan kanan."),
                Ayat(19, "وَٱلَّذِينَ كَفَرُواْ بِـَٔايَٰتِنَا هُمۡ أَصۡحَٰبُ ٱلۡمَشۡـَٔمَةِ", "Dan orang-orang yang kafir kepada ayat-ayat Kami, mereka itu adalah golongan kiri."),
                Ayat(20, "عَلَيۡهِمۡ نَارٞ مُّؤۡصَدَةُۢ", "Mereka berada dalam neraka yang ditutup rapat.")
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
                Ayat(5, "وَٱلسَّمَآءِ وَمَا بَنَىٰهَا", "demi langit dan (Allah) yang membangunnya,"),
                Ayat(6, "وَٱلۡأَرۡضِ وَمَا طَحَىٰهَا", "demi bumi dan (Allah) yang menghamparkannya,"),
                Ayat(7, "وَنَفۡسٖ وَمَا سَوَّىٰهَا", "demi jiwa serta (Allah) yang menyempurnakannya,"),
                Ayat(8, "فَأَلۡهَمَهَا فُجُورَهَا وَتَقۡوَىٰهَا", "maka Dia mengilhamkan kepadanya (jalan) kejahatan dan ketakwaannya,"),
                Ayat(9, "قَدۡ أَفۡلَحَ مَن زَكَّىٰهَا", "sungguh beruntung orang yang menyucikannya (jiwa itu),"),
                Ayat(10, "وَقَدۡ خَابَ مَن دَسَّىٰهَا", "dan sungguh rugi orang yang mengotorinya."),
                Ayat(11, "كَذَّبَتۡ ثَمُودُ بِطَغۡوَىٰهَآ", "Kaum Tsamud telah mendustakan (rasulnya) karena mereka melampaui batas,"),
                Ayat(12, "إِذِ ٱنۢبَعَثَ أَشۡقَىٰهَا", "ketika bangkit orang yang paling celaka di antara mereka,"),
                Ayat(13, "فَقَالَ لَهُمۡ رَسُولُ ٱللَّهِ نَاقَةَ ٱللَّهِ وَسُقۡيَاهَا", "lalu Rasul Allah (Salih) berkata kepada mereka, \"(Biarkanlah) unta betina Allah dan minumannya.\""),
                Ayat(14, "فَكَذَّبُوهُ فَعَقَرُوهَا فَدَمۡدَمَ عَلَيۡهِمۡ رَبُّهُم بِذَنۢبِهِمۡ فَسَوَّىٰهَا", "Tetapi mereka mendustakannya dan menyembelihnya, karena itu Tuhan membinasakan mereka karena dosanya, lalu diratakan-Nya (dengan tanah)."),
                Ayat(15, "وَلَا يَخَافُ عُقۡبَٰهَا", "Dan Dia tidak takut akan akibat pembinasaan itu.")
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
                Ayat(5, "فَأَمَّا مَنۡ أَعۡطَىٰ وَٱتَّقَىٰ", "Adapun orang yang memberikan (hartanya di jalan Allah) dan bertakwa,"),
                Ayat(6, "وَصَدَّقَ بِٱلۡحُسۡنَىٰ", "dan membenarkan (adanya pahala) yang terbaik (surga),"),
                Ayat(7, "فَسَنُيَسِّرُهُۥ لِلۡيُسۡرَىٰ", "maka Kami akan memudahkan baginya jalan menuju kemudahan (kebahagiaan)."),
                Ayat(8, "وَأَمَّا مَن بَخِلَ وَٱسۡتَغۡنَىٰ", "Dan adapun orang yang kikir dan merasa dirinya cukup,"),
                Ayat(9, "وَكَذَّبَ بِٱلۡحُسۡنَىٰ", "serta mendustakan (adanya pahala) yang terbaik,"),
                Ayat(10, "فَسَنُيَسِّرُهُۥ لِلۡعُسۡرَىٰ", "maka Kami akan memudahkan baginya jalan menuju kesukaran (kesengsaraan)."),
                Ayat(11, "وَمَا يُغۡنِي عَنۡهُ مَالُهُۥٓ إِذَا تَرَدَّىٰٓ", "Dan hartanya tidak bermanfaat baginya apabila dia telah binasa."),
                Ayat(12, "إِنَّ عَلَيۡنَا لَلۡهُدَىٰ", "Sesungguhnya Kamilah yang memberi petunjuk."),
                Ayat(13, "وَإِنَّ لَنَا لَلۡأٓخِرَةَ وَٱلۡأُولَىٰ", "Dan sesungguhnya kepunyaan Kamilah akhirat dan dunia."),
                Ayat(14, "فَأَنذَرۡتُكُمۡ نَارٗا تَلَظَّىٰ", "Maka Aku memperingatkan kamu dengan neraka yang menyala-nyala."),
                Ayat(15, "لَا يَصۡلَىٰهَآ إِلَّا ٱلۡأَشۡقَى", "Tidak ada yang masuk ke dalamnya kecuali orang yang paling celaka,"),
                Ayat(16, "ٱلَّذِي كَذَّبَ وَتَوَلَّىٰ", "yang mendustakan (kebenaran) dan berpaling (dari iman)."),
                Ayat(17, "وَسَيُجَنَّبُهَا ٱلۡأَتۡقَى", "Dan kelak akan dijauhkan orang yang paling takwa dari neraka itu,"),
                Ayat(18, "ٱلَّذِي يُؤۡتِي مَالَهُۥ يَتَزَكَّىٰ", "yang menafkahkan hartanya (di jalan Allah) untuk membersihkannya,"),
                Ayat(19, "وَمَا لِأَحَدٍ عِندَهُۥ مِن نِّعۡمَةٖ تُجۡزَىٰٓ", "padahal tidak ada seorang pun memberikan suatu nikmat kepadanya yang harus dibalasnya,"),
                Ayat(20, "إِلَّا ٱبۡتِغَآءَ وَجۡهِ رَبِّهِ ٱلۡأَعۡلَىٰ", "tetapi (dia memberikan itu semata-mata) karena mencari keridaan Tuhannya Yang Mahatinggi."),
                Ayat(21, "وَلَسَوۡفَ يَرۡضَىٰ", "Dan kelak dia benar-benar mendapat kepuasan.")
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
                Ayat(5, "وَلَسَوۡفَ يُعۡطِيكَ رَبُّكَ فَتَرۡضَىٰٓ", "Dan sungguh, Tuhanmu pasti memberikan karunia-Nya kepadamu, sehingga engkau menjadi puas."),
                Ayat(6, "أَلَمۡ يَجِدۡكَ يَتِيمٗا فَـَٔاوَىٰ", "Bukankah Dia mendapatimu sebagai seorang yatim, lalu Dia melindungi(mu)?"),
                Ayat(7, "وَوَجَدَكَ ضَآلّٗا فَهَدَىٰ", "Dan Dia mendapatimu sebagai seorang yang bingung, lalu Dia memberikan petunjuk."),
                Ayat(8, "وَوَجَدَكَ عَآئِلٗا فَأَغۡنَىٰٓ", "Dan Dia mendapatimu sebagai seorang yang kekurangan, lalu Dia memberikan kecukupan."),
                Ayat(9, "فَأَمَّا ٱلۡيَتِيمَ فَلَا تَقۡهَرۡ", "Maka terhadap anak yatim janganlah engkau berlaku sewenang-wenang."),
                Ayat(10, "وَأَمَّا ٱلسَّآئِلَ فَلَا تَنۡهَرۡ", "Dan terhadap orang yang meminta-minta janganlah engkau menghardik(nya)."),
                Ayat(11, "وَأَمَّا بِنِعۡمَةِ رَبِّكَ فَحَدِّثۡ", "Dan terhadap nikmat Tuhanmu, hendaklah engkau nyatakan (dengan bersyukur).")
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
                Ayat(5, "فَإِنَّ مَعَ ٱلۡعُسۡرِ يُسۡرًا", "Maka sesungguhnya bersama kesulitan ada kemudahan,"),
                Ayat(6, "إِنَّ مَعَ ٱلۡعُسۡرِ يُسۡرٗا", "sesungguhnya bersama kesulitan ada kemudahan."),
                Ayat(7, "فَإِذَا فَرَغۡتَ فَٱنصَبۡ", "Maka apabila engkau telah selesai (dari sesuatu urusan), tetaplah bekerja keras (untuk urusan yang lain),"),
                Ayat(8, "وَإِلَىٰ رَبِّكَ فَٱرۡغَب", "dan hanya kepada Tuhanmulah engkau berharap.")
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
                Ayat(5, "ثُمَّ رَدَدۡنَٰهُ أَسۡفَلَ سَٰفِلِينَ", "kemudian Kami kembalikan dia ke tempat yang serendah-rendahnya,"),
                Ayat(6, "إِلَّا ٱلَّذِينَ ءَامَنُواْ وَعَمِلُواْ ٱلصَّٰلِحَٰتِ فَلَهُمۡ أَجۡرٌ غَيۡرُ مَمۡنُونٖ", "kecuali orang-orang yang beriman dan mengerjakan kebajikan; maka mereka akan mendapat pahala yang tidak ada putus-putusnya."),
                Ayat(7, "فَمَا يُكَذِّبُكَ بَعۡدُ بِٱلدِّينِ", "Maka apakah yang menyebabkan (mereka) mendustakanmu (tentang) hari pembalasan sesudah (adanya keterangan-keterangan) itu?"),
                Ayat(8, "أَلَيۡسَ ٱللَّهُ بِأَحۡكَمِ ٱلۡحَٰكِمِينَ", "Bukankah Allah hakim yang paling adil?")
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
                Ayat(5, "عَلَّمَ ٱلۡإِنسَٰنَ مَا لَمۡ يَعۡلَمۡ", "Dia mengajarkan manusia apa yang tidak diketahuinya."),
                Ayat(6, "كَلَّآ إِنَّ ٱلۡإِنسَٰنَ لَيَطۡغَىٰٓ", "Sekali-kali tidak! Sesungguhnya manusia benar-benar melampaui batas,"),
                Ayat(7, "أَن رَّءَاهُ ٱسۡتَغۡنَىٰٓ", "karena dia melihat dirinya serba cukup."),
                Ayat(8, "إِنَّ إِلَىٰ رَبِّكَ ٱلرُّجۡعَىٰٓ", "Sesungguhnya hanya kepada Tuhanmulah kembali(mu)."),
                Ayat(9, "أَرَءَيۡتَ ٱلَّذِي يَنۡهَىٰ", "Bagaimana pendapatmu tentang orang yang melarang,"),
                Ayat(10, "عَبۡدًا إِذَا صَلَّىٰٓ", "seorang hamba ketika dia melaksanakan salat?"),
                Ayat(11, "أَرَءَيۡتَ إِن كَانَ عَلَى ٱلۡهُدَىٰٓ", "Bagaimana pendapatmu jika dia (yang dilarang salat itu) berada di atas kebenaran (petunjuk)?"),
                Ayat(12, "أَوۡ أَمَرَ بِٱلتَّقۡوَىٰٓ", "atau dia menyuruh bertakwa (kepada Allah)?"),
                Ayat(13, "أَرَءَيۡتَ إِن كَذَّبَ وَتَوَلَّىٰٓ", "Bagaimana pendapatmu jika dia (yang melarang) itu mendustakan dan berpaling?"),
                Ayat(14, "أَلَمۡ يَعۡلَم بِأَنَّ ٱللَّهَ يَرَىٰ", "Tidakkah dia mengetahui bahwa sesungguhnya Allah melihat (segala perbuatannya)?"),
                Ayat(15, "كَلَّا لَئِن لَّمۡ يَنتَهِ لَنَسۡفَعَۢا بِٱلنَّاصِيَةِ", "Sekali-kali tidak! Sungguh, jika dia tidak berhenti (berbuat demikian) niscaya Kami tarik ubun-ubunnya,"),
                Ayat(16, "نَاصِيَةٖ كَٰذِبَةٍ خَٰطِئَةٖ", "(yaitu) ubun-ubun orang yang mendustakan lagi durhaka."),
                Ayat(17, "فَلۡيَدۡعُ نَادِيَهُۥ", "Maka biarlah dia memanggil golongannya (untuk menolongnya),"),
                Ayat(18, "سَنَدۡعُ ٱلزَّبَانِيَةَ", "Kelak Kami akan memanggil Malaikat Zabaniyah,"),
                Ayat(19, "كَلَّا لَا تُطِعۡهُ وَٱسۡجُدۡ وَٱقۡتَرِبۡ", "sekali-kali tidak! Janganlah engkau patuh kepadanya; dan sujudlah serta dekatkanlah (dirimu kepada Tuhan).")
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
                Ayat(5, "وَمَآ أُمِرُوٓاْ إِلَّا لِيَعۡبُدُواْ ٱللَّهَ مُخۡلِصِينَ لَهُ ٱلدِّينَ حُنَفَآءَ وَيُقِيمُواْ ٱلصَّلَوٰةَ وَيُؤۡتُواْ ٱلزَّكَوٰةَۚ وَذَٰلِكَ دِينُ ٱلۡقَيِّمَةِ", "Padahal mereka hanya diperintahkan menyembah Allah dengan ikhlas menaati-Nya semata-mata karena (menjalankan) agama, dan juga agar melaksanakan salat dan menunaikan zakat; dan yang demikian itulah agama yang lurus (benar)."),
                Ayat(6, "إِنَّ ٱلَّذِينَ كَفَرُواْ مِنۡ أَهۡلِ ٱلۡكِتَٰبِ وَٱلۡمُشۡرِكِينَ فِي نَارِ جَهَنَّمَ خَٰلِدِينَ فِيهَآۚ أُوْلَٰٓئِكَ هُمۡ شَرُّ ٱلۡبَرِيَّةِ", "Sesungguhnya orang-orang kafir yakni Ahli Kitab dan orang-orang musyrik (akan masuk) ke neraka Jahanam; mereka kekal di dalamnya. Mereka itu adalah seburuk-buruk makhluk."),
                Ayat(7, "إِنَّ ٱلَّذِينَ ءَامَنُواْ وَعَمِلُواْ ٱلصَّٰلِحَٰتِ أُوْلَٰٓئِكَ هُمۡ خَيۡرُ ٱلۡبَرِيَّةِ", "Sesungguhnya orang-orang yang beriman dan mengerjakan kebajikan, mereka itu adalah sebaik-baik makhluk."),
                Ayat(8, "جَزَآؤُهُمۡ عِندَ رَبِّهِمۡ جَنَّٰتُ عَدۡنٖ تَجۡرِي مِن تَحۡتِهَا ٱلۡأَنۡهَٰرُ خَٰلِدِينَ فِيهَآ أَبَدٗاۖ رَّضِيَ ٱللَّهُ عَنۡهُمۡ وَرَضُواْ عَنۡهُۚ ذَٰلِكَ لِمَنۡ خَشِيَ رَبَّهُۥ", "Balasan mereka di sisi Tuhan mereka ialah surga 'Adn yang mengalir di bawahnya sungai-sungai; mereka kekal di dalamnya selama-lamanya. Allah rida terhadap mereka dan mereka pun rida kepada-Nya. Yang demikian itu adalah (balasan) bagi orang yang takut kepada Tuhannya.")
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
                Ayat(5, "بِأَنَّ رَبَّكَ أَوۡحَىٰ لَهَا", "karena sesungguhnya Tuhanmu telah memerintahkan (yang sedemikian itu) kepadanya."),
                Ayat(6, "يَوۡمَئِذٖ يَصۡدُرُ ٱلنَّاسُ أَشۡتَاتٗا لِّيُرَوۡاْ أَعۡمَٰلَهُمۡ", "Pada hari itu manusia ke luar dari kuburnya dalam keadaan bermacam-macam, supaya diperlihatkan kepada mereka (balasan) pekerjaan mereka."),
                Ayat(7, "فَمَن يَعۡمَلۡ مِثۡقَالَ ذَرَّةٍ خَيۡرٗا يَرَهُۥ", "Barangsiapa mengerjakan kebaikan seberat zarah pun, niscaya dia akan melihat (balasan)nya."),
                Ayat(8, "وَمَن يَعۡمَلۡ مِثۡقَالَ ذَرَّةٖ شَرّٗا يَرَهُۥ", "Dan barangsiapa mengerjakan kejahatan seberat zarah pun, niscaya dia akan melihat (balasan)nya pula.")
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
                Ayat(5, "فَوَسَطۡنَ بِهِۦ جَمۡعًا", "lalu menyerbu ke tengah-tengah kumpulan musuh."),
                Ayat(6, "إِنَّ ٱلۡإِنسَٰنَ لِرَبِّهِۦ لَكَنُودٞ", "Sesungguhnya manusia itu sangat ingkar, tidak berterima kasih kepada Tuhannya,"),
                Ayat(7, "وَإِنَّهُۥ عَلَىٰ ذَٰلِكَ لَشَهِيدٞ", "dan sesungguhnya manusia itu menyaksikan (sendiri) keingkarannya,"),
                Ayat(8, "وَإِنَّهُۥ لِحُبِّ ٱلۡخَيۡرِ لَشَدِيدٞ", "dan sesungguhnya dia sangat bakhil karena cintanya kepada harta."),
                Ayat(9, "أَفَلَا يَعۡلَمُ إِذَا بُعۡثِرَ مَا فِي ٱلۡقُبُورِ", "Maka apakah dia tidak mengetahui apabila dibangkitkan apa yang ada di dalam kubur,"),
                Ayat(10, "وَحُصِّلَ مَا فِي ٱلصُّدُورِ", "dan dilahirkan apa yang ada di dalam dada,"),
                Ayat(11, "إِنَّ رَبَّهُم بِهِمۡ يَوۡمَئِذٖ لَّخَبِيرُۢ", "sesungguhnya Tuhan mereka pada hari itu Maha Mengetahui keadaan mereka.")
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
                Ayat(5, "وَتَكُونُ ٱلۡجِبَالُ كَٱلۡعِهۡنِ ٱلۡمَنفُوشِ", "dan gunung-gunung seperti bulu yang dihambur-hamburkan."),
                Ayat(6, "فَأَمَّا مَن ثَقُلَتۡ مَوَٰزِينُهُۥ", "Maka adapun orang yang berat timbangan (kebaikan)nya,"),
                Ayat(7, "فَهُوَ فِي عِيشَةٖ رَّاضِيَةٖ", "maka dia berada dalam kehidupan yang memuaskan (senang)."),
                Ayat(8, "وَأَمَّا مَن خَفَّتۡ مَوَٰزِينُهُۥ", "Dan adapun orang yang ringan timbangan (kebaikan)nya,"),
                Ayat(9, "فَأُمُّهُۥ هَاوِيَةٞ", "maka tempat kembalinya adalah neraka Hawiyah."),
                Ayat(10, "وَمَآ أَدۡرَىٰكَ مَا هِيَهۡ", "Dan tahukah kamu apakah neraka Hawiyah itu?"),
                Ayat(11, "نَارٌ حَامِيَةُۢ", "(Yaitu) api yang sangat panas.")
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
                Ayat(5, "كَلَّا لَوۡ تَعۡلَمُونَ عِلۡمَ ٱلۡيَقِينِ", "Sekali-kali tidak! Sekiranya kamu mengetahui dengan pasti,"),
                Ayat(6, "لَتَرَوُنَّ ٱلۡجَحِيمَ", "niscaya kamu benar-benar akan melihat neraka Jahim,"),
                Ayat(7, "ثُمَّ لَتَرَوُنَّهَا عَيۡنَ ٱلۡيَقِينِ", "kemudian kamu benar-benar akan melihatnya dengan mata kepala sendiri,"),
                Ayat(8, "ثُمَّ لَتُسۡـَٔلُنَّ يَوۡمَئِذٍ عَنِ ٱلنَّعِيمِ", "kemudian kamu pasti akan ditanyai pada hari itu tentang kenikmatan (yang kamu megah-megahkan di dunia itu).")
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
                Ayat(5, "وَمَآ أَدۡرَىٰكَ مَا ٱلۡحُطَمَةُ", "Dan tahukah kamu apakah (neraka) Hutamah itu?"),
                Ayat(6, "نَارُ ٱللَّهِ ٱلۡمُوقَدَةُ", "(Yaitu) api (yang disediakan) Allah yang dinyalakan,"),
                Ayat(7, "ٱلَّتِي تَطَّلِعُ عَلَى ٱلۡأَفۡـِٔدَةِ", "yang (membakar) sampai ke hati."),
                Ayat(8, "إِنَّهَا عَلَيۡهِم مُّؤۡصَدَةٞ", "Sesungguhnya api itu ditutup rapat atas mereka,"),
                Ayat(9, "فِي عَمَدٖ مُّمَدَّدَةِۭ", "(sedang mereka itu) diikat pada tiang-tiang yang panjang.")
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
