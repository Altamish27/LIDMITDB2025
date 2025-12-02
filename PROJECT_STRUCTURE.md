# Struktur Proyek LIDMITDB2025

## Ringkasan Refactoring Terbaru
Proyek ini telah direfactor untuk meningkatkan organisasi kode berdasarkan fitur (feature-based architecture) dan menghilangkan file-file yang tidak terpakai.

### Perubahan yang Dilakukan:

#### 1. Penghapusan File yang Tidak Terpakai
- ❌ **Folder `examples/`** - File contoh yang tidak digunakan
- ❌ **Folder `reference/`** - File referensi yang tidak digunakan
- ❌ **`fragment_camera_backup.xml`** - File backup layout
- ❌ **`fragment_camera_new.xml`** - File backup layout
- ❌ **Folder `layout-land/`** - Layout landscape (hanya gunakan portrait)

#### 2. Reorganisasi ke Feature-Based Architecture
Struktur lama yang tersebar di folder `ui/` dan `fragment/` telah direorganisasi menjadi struktur berdasarkan fitur:

**Struktur Lama:**
```
├── ui/
│   ├── adapter/
│   ├── gallery/
│   ├── hijaiyah/
│   ├── hijaiyahdb/
│   ├── latihan/
│   ├── panduan/
│   ├── surat/
│   ├── HomeActivity.kt
│   ├── LoginActivity.kt
│   └── ProfileActivity.kt
├── fragment/
│   └── CameraFragment.kt
```

**Struktur Baru:**
```
├── features/          # ✨ Feature modules
│   ├── auth/         # 🔐 Autentikasi
│   ├── camera/       # 📸 Deteksi gesture kamera
│   ├── gallery/      # 🖼️ Deteksi gesture dari galeri
│   ├── hijaiyah/     # 📝 Belajar huruf hijaiyah
│   ├── hijaiyahdb/   # 💾 Database hijaiyah
│   ├── home/         # 🏠 Home screen
│   ├── latihan/      # 📚 Latihan/practice
│   ├── panduan/      # 📖 Panduan hijaiyah
│   └── surat/        # 📜 Al-Quran surat
├── core/             # ⚙️ Shared components
│   ├── adapter/      # RecyclerView adapters
│   ├── helper/       # Helper classes
│   ├── main/         # MainActivity
│   ├── overlay/      # Overlay components
│   ├── permissions/  # Permission handling
│   ├── supabase/     # Supabase client
│   └── viewmodel/    # ViewModels
└── data/             # 📊 Data classes & models
```

## Struktur Proyek Detail

```
android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/google/mediapipe/examples/gesturerecognizer/
│   │   │   │   │
│   │   │   │   ├── features/                    # 🎯 FITUR-FITUR UTAMA
│   │   │   │   │   │
│   │   │   │   │   ├── auth/                   # 🔐 Authentication
│   │   │   │   │   │   ├── LoginActivity.kt    # Halaman login
│   │   │   │   │   │   └── ProfileActivity.kt  # Halaman profil user
│   │   │   │   │   │
│   │   │   │   │   ├── camera/                 # 📸 Camera Gesture Recognition
│   │   │   │   │   │   └── fragment/
│   │   │   │   │   │       └── CameraFragment.kt  # Fragment kamera utama
│   │   │   │   │   │
│   │   │   │   │   ├── gallery/                # 🖼️ Gallery Gesture Recognition
│   │   │   │   │   │   └── GalleryFragment.kt  # Fragment galeri
│   │   │   │   │   │
│   │   │   │   │   ├── hijaiyah/               # 📝 Belajar Huruf Hijaiyah
│   │   │   │   │   │   ├── ArabicLetterAdapter.kt
│   │   │   │   │   │   ├── HijaiyahAdapter.kt
│   │   │   │   │   │   └── HijaiyahFragment.kt
│   │   │   │   │   │
│   │   │   │   │   ├── hijaiyahdb/             # 💾 Database Hijaiyah
│   │   │   │   │   │   ├── HijaiyahDBAdapter.kt
│   │   │   │   │   │   ├── HijaiyahDBFragment.kt
│   │   │   │   │   │   ├── HijaiyahDBListActivity.kt
│   │   │   │   │   │   └── model/
│   │   │   │   │   │       └── HijaiyahDbItem.kt
│   │   │   │   │   │
│   │   │   │   │   ├── home/                   # 🏠 Home Screen
│   │   │   │   │   │   └── HomeActivity.kt     # Activity utama
│   │   │   │   │   │
│   │   │   │   │   ├── latihan/                # 📚 Latihan/Practice
│   │   │   │   │   │   ├── HurufGridAdapter.kt
│   │   │   │   │   │   ├── LatihanAdapter.kt
│   │   │   │   │   │   ├── LatihanDetailActivity.kt
│   │   │   │   │   │   ├── LatihanFragment.kt
│   │   │   │   │   │   ├── LatihanHurufAdapter.kt
│   │   │   │   │   │   ├── LatihanHurufGridAdapter.kt
│   │   │   │   │   │   └── LatihanPracticeActivity.kt
│   │   │   │   │   │
│   │   │   │   │   ├── panduan/                # 📖 Panduan Hijaiyah
│   │   │   │   │   │   └── PanduanHijaiyahActivity.kt
│   │   │   │   │   │
│   │   │   │   │   └── surat/                  # 📜 Al-Quran Surat
│   │   │   │   │       ├── SuratDetailActivity.kt
│   │   │   │   │       └── SuratListActivity.kt
│   │   │   │   │
│   │   │   │   ├── core/                        # ⚙️ KOMPONEN SHARED
│   │   │   │   │   ├── adapter/                # RecyclerView adapters
│   │   │   │   │   │   ├── GestureRecognizerResultsAdapter.kt
│   │   │   │   │   │   ├── HijaiyahListAdapter.kt
│   │   │   │   │   │   └── PanduanHijaiyahAdapter.kt
│   │   │   │   │   │
│   │   │   │   │   ├── helper/                 # Helper classes
│   │   │   │   │   │   └── GestureRecognizerHelper.kt  # ML helper
│   │   │   │   │   │
│   │   │   │   │   ├── main/                   # Main activity
│   │   │   │   │   │   └── MainActivity.kt     # Navigation host
│   │   │   │   │   │
│   │   │   │   │   ├── overlay/                # Overlay components
│   │   │   │   │   │   ├── CoordinateMapper.kt
│   │   │   │   │   │   ├── MovementDetectionListener.kt
│   │   │   │   │   │   ├── OverlayView.kt      # Basic overlay
│   │   │   │   │   │   ├── TrajectoryAnalyzer.kt
│   │   │   │   │   │   ├── TrajectoryOverlayView.kt
│   │   │   │   │   │   └── TrajectoryRingBuffer.kt
│   │   │   │   │   │
│   │   │   │   │   ├── permissions/            # Permission handling
│   │   │   │   │   │   └── PermissionsFragment.kt
│   │   │   │   │   │
│   │   │   │   │   ├── supabase/               # Supabase client
│   │   │   │   │   │   └── SupabaseClient.kt
│   │   │   │   │   │
│   │   │   │   │   └── viewmodel/              # ViewModels (MVVM)
│   │   │   │   │       └── MainViewModel.kt
│   │   │   │   │
│   │   │   │   └── data/                        # 📊 DATA LAYER
│   │   │   │       ├── ArabicLetter.kt
│   │   │   │       ├── DhammahData.kt
│   │   │   │       ├── FathahData.kt
│   │   │   │       ├── KasrahData.kt
│   │   │   │       ├── HijaiyahData.kt
│   │   │   │       └── HijaiyahProgressManager.kt
│   │   │   │
│   │   │   ├── res/                            # Resources
│   │   │   │   ├── layout/                     # Layout files (portrait only)
│   │   │   │   ├── drawable/                   # Drawables
│   │   │   │   ├── values/                     # Values (strings, colors, etc)
│   │   │   │   └── ...
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   └── androidTest/                        # Instrumented tests
│   │
│   ├── build.gradle                            # App-level Gradle config
│   └── download_tasks.gradle                   # Download ML models
│
├── build.gradle                                # Project-level Gradle config
├── settings.gradle                             # Project settings
└── gradle.properties                           # Gradle properties
```

## Manfaat Feature-Based Architecture

### 1. **Modularitas yang Lebih Baik** 🎯
- Setiap fitur memiliki folder sendiri
- Mudah menemukan file terkait dengan fitur tertentu
- Mengurangi coupling antar fitur

### 2. **Skalabilitas** 📈
- Mudah menambahkan fitur baru
- Bisa di-convert ke multi-module di masa depan
- Tim bisa bekerja parallel pada fitur berbeda

### 3. **Maintainability** 🔧
- Struktur yang jelas dan intuitif
- Lebih mudah untuk debugging
- Code review lebih fokus per fitur

### 4. **Separation of Concerns** 🎭
- **Features**: Fitur-fitur aplikasi (UI + logic spesifik fitur)
- **Core**: Komponen yang digunakan bersama
- **Data**: Model dan data classes

### 5. **Clean Codebase** ✨
- Tidak ada file duplikat atau backup
- Tidak ada layout untuk landscape (hanya portrait)
- Struktur konsisten di seluruh proyek

## Mapping Fitur ke Package

| Fitur | Package | Deskripsi |
|-------|---------|-----------|
| 🔐 Login & Profile | `features.auth` | Autentikasi dan profil user |
| 📸 Kamera Deteksi | `features.camera` | Deteksi gesture real-time dengan kamera |
| 🖼️ Galeri Deteksi | `features.gallery` | Deteksi gesture dari gambar/video |
| 📝 Sign Quran | `features.hijaiyah` | Pembelajaran huruf hijaiyah |
| 💾 Database Hijaiyah | `features.hijaiyahdb` | Penyimpanan data hijaiyah |
| 🏠 Home | `features.home` | Halaman utama aplikasi |
| 📚 Latihan | `features.latihan` | Latihan menulis huruf |
| 📖 Panduan | `features.panduan` | Panduan penggunaan |
| 📜 Al-Quran | `features.surat` | Daftar surat Al-Quran |

## Dependencies Utama

- **MediaPipe**: Gesture recognition (v0.10.14)
- **CameraX**: Kamera dan preview (v1.2.0-alpha02)
- **Supabase**: Backend & database (v2.6.0)
- **Jetpack Compose**: UI modern (v1.5.0)
- **Ktor**: HTTP client untuk Supabase (v2.3.8)
- **Kotlinx Serialization**: JSON serialization (v1.6.3)

## Cara Build Proyek

1. Buka proyek di Android Studio
2. Sync Gradle
3. Build → Make Project atau `./gradlew assembleDebug`
4. Run pada emulator atau device fisik

## Catatan Penting

- ✅ Semua import statements telah diperbarui
- ✅ Semua package declarations telah diperbaiki
- ✅ Tidak ada breaking changes pada fungsionalitas
- ✅ Proyek sudah di-verifikasi dan berhasil di-build
- ⚠️ Ada beberapa deprecation warnings (non-critical)
- 📱 Hanya mendukung orientasi portrait

## Migration Guide (untuk Developer)

Jika Anda memiliki code yang menggunakan struktur lama:

### Update Import Statements:
```kotlin
// Lama
import com.google.mediapipe.examples.gesturerecognizer.ui.HomeActivity
import com.google.mediapipe.examples.gesturerecognizer.ui.helper.GestureRecognizerHelper

// Baru
import com.google.mediapipe.examples.gesturerecognizer.features.home.HomeActivity
import com.google.mediapipe.examples.gesturerecognizer.core.helper.GestureRecognizerHelper
```

### Update Class References:
```kotlin
// Lama
Intent(this, com.google.mediapipe.examples.gesturerecognizer.ui.ProfileActivity::class.java)

// Baru  
Intent(this, ProfileActivity::class.java)  // dengan proper import
```

---

**Last Updated**: November 8, 2025
**Architecture**: Feature-Based (Clean Architecture inspired)
**Min SDK**: 24
**Target SDK**: 35
