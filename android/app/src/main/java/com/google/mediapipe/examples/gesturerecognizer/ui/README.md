# UI Structure Documentation

## Folder Structure

Proyek ini telah direorganisasi dengan struktur UI yang lebih rapi:

```
ui/
├── main/
│   └── MainActivity.kt           # Main Activity dengan navigation
├── camera/
│   └── CameraFragment.kt         # Camera page untuk real-time recognition
├── gallery/
│   └── GalleryFragment.kt        # Gallery page untuk image/video selection
├── permissions/
│   └── PermissionsFragment.kt    # Permissions handling page
└── adapter/
    └── GestureRecognizerResultsAdapter.kt  # Results adapter
```

## Package Structure

- `com.google.mediapipe.examples.gesturerecognizer.ui.main` - Main activity dan core UI
- `com.google.mediapipe.examples.gesturerecognizer.ui.camera` - Camera related UI
- `com.google.mediapipe.examples.gesturerecognizer.ui.gallery` - Gallery related UI  
- `com.google.mediapipe.examples.gesturerecognizer.ui.permissions` - Permission related UI
- `com.google.mediapipe.examples.gesturerecognizer.ui.adapter` - UI adapters

## Benefits

1. **Better Organization**: Setiap page memiliki folder sendiri
2. **Easy Maintenance**: Lebih mudah untuk maintain dan debug
3. **Scalable**: Mudah untuk menambah page baru
4. **Clean Architecture**: Separation of concerns yang jelas
