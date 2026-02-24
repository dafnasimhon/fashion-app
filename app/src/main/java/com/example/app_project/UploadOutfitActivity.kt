package com.example.app_project

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.airbnb.lottie.LottieAnimationView
import com.example.app_project.repository.OutfitRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText

/**
 * Activity that handles the selection, tagging, and uploading of new outfits to Firebase.
 */
class UploadOutfitActivity : BaseActivity() {

    private lateinit var ivPreview: ImageView
    private lateinit var btnUpload: MaterialButton
    private lateinit var animationView: LottieAnimationView // הוחלף מ-ProgressBar כדי להתאים ל-Lottie
    private lateinit var actvVibe: AutoCompleteTextView

    private lateinit var etTop: TextInputEditText
    private lateinit var etBottom: TextInputEditText
    private lateinit var etJacket: TextInputEditText
    private lateinit var etShoes: TextInputEditText
    private lateinit var etJewelry: TextInputEditText
    private lateinit var etSunglasses: TextInputEditText
    private lateinit var etBag: TextInputEditText

    // שימוש ב-Singleton: פנייה ישירה לאובייקט ה-Repository
    private val outfitRepository = OutfitRepository
    private var imageUri: Uri? = null

    // Launcher לבחירת תמונה מהגלריה
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            ivPreview.setImageURI(it)
            ivPreview.setPadding(0, 0, 0, 0)
            ivPreview.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_outfit)

        // פתרון בעיית המקלדת וה-Insets דרך ה-BaseActivity
        applyEdgeToEdge(findViewById(R.id.main))

        // סימון כפתור ה"הוספה" בתפריט הניווט התחתון
        setupBottomNavigation(R.id.btn_add_outfit)

        initViews()
        setupVibeDropdown()

        // פתיחת הגלריה בלחיצה על אזור התמונה
        findViewById<MaterialCardView>(R.id.upload_CV_image_container).setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        btnUpload.setOnClickListener {
            validateAndUpload()
        }
    }

    private fun initViews() {
        ivPreview = findViewById(R.id.upload_IV_preview)
        btnUpload = findViewById(R.id.upload_BTN_upload)
        // אתחול האנימציה לפי ה-ID החדש שהגדרנו ב-XML
        animationView = findViewById(R.id.upload_animation)
        actvVibe = findViewById(R.id.upload_ACTV_vibe)

        etTop = findViewById(R.id.upload_ET_top)
        etBottom = findViewById(R.id.upload_ET_bottom)
        etJacket = findViewById(R.id.upload_ET_jacket)
        etShoes = findViewById(R.id.upload_ET_shoes)
        etJewelry = findViewById(R.id.upload_ET_jewelry)
        etSunglasses = findViewById(R.id.upload_ET_sunglasses)
        etBag = findViewById(R.id.upload_ET_bag)
    }

    private fun setupVibeDropdown() {
        val vibes = resources.getStringArray(R.array.outfit_vibes)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, vibes)
        actvVibe.setAdapter(adapter)
    }

    private fun validateAndUpload() {
        val vibe = actvVibe.text.toString().trim()
        val top = etTop.text.toString().trim()
        val bottom = etBottom.text.toString().trim()
        val jacket = etJacket.text.toString().trim()
        val shoes = etShoes.text.toString().trim()
        val jewelry = etJewelry.text.toString().trim()
        val sunglasses = etSunglasses.text.toString().trim()
        val bag = etBag.text.toString().trim()

        if (imageUri == null) {
            showToast("Please select an outfit image")
            return
        }

        if (vibe.isEmpty()) {
            showToast(getString(R.string.msg_error_vibe))
            return
        }

        if (top.isEmpty() || bottom.isEmpty()) {
            showToast("Top and Bottom are required")
            return
        }

        setLoading(true)

        // העלאת האאוטפיט דרך ה-Singleton Repository
        outfitRepository.uploadOutfit(
            imageUri!!, top, bottom, jacket, shoes, jewelry, sunglasses, bag, vibe
        ) { success, error ->
            setLoading(false)
            if (success) {
                showToast(getString(R.string.msg_upload_success))
                finish()
            } else {
                showToast("Upload failed: $error")
            }
        }
    }

    /**
     * ניהול מצב הטעינה באמצעות האנימציה של הבחורה עם השקיות
     */
    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            animationView.visibility = View.VISIBLE
            animationView.playAnimation() // התחלת האנימציה
            btnUpload.isEnabled = false
            btnUpload.alpha = 0.5f
        } else {
            animationView.visibility = View.GONE
            animationView.pauseAnimation() // עצירת האנימציה
            btnUpload.isEnabled = true
            btnUpload.alpha = 1.0f
        }
    }
}