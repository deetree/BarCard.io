package com.pl.cards.ui

import android.Manifest
import androidx.core.view.isVisible

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.*
import com.pl.cards.R
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.pl.cards.helper.BarcodeHelper
import com.pl.cards.viewmodel.CameraXViewModel
import java.util.concurrent.Executors
import kotlin.IllegalStateException
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ScanActivity : AppCompatActivity() {

    private var previewView: PreviewView? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector: CameraSelector? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var vibrationImgBtn: ImageButton? = null
    private var flashlightImgBtn: ImageButton? = null
    private var soundImgBtn: ImageButton? = null
    private var camera: Camera? = null

    private var soundEnabled: Boolean = true
    private var vibrationEnabled: Boolean = true
    private var flashlightEnabled: Boolean = false

    private val SOUND_TAG: String = "SOUND"
    private val VIBRATION_TAG: String = "VIBRATION"

    private val screenAspectRatio: Int
        get() {
            // Get screen metrics used to setup camera for full screen resolution
            val metrics = DisplayMetrics().also { previewView?.display?.getRealMetrics(it) }
            return aspectRatio(metrics.widthPixels, metrics.heightPixels)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        supportActionBar?.hide()

        previewView = findViewById(R.id.preview_view)
        vibrationImgBtn = findViewById(R.id.vibrationImgBtn)
        soundImgBtn = findViewById(R.id.volumeImgBtn)
        flashlightImgBtn = findViewById(R.id.flashlightImgBtn)

        captured = false

        setupCamera()
        setupFlashlight()
        setupButtonListeners()
    }

    private fun setupFlashlight() {
        val isFlashAvailable = camera?.cameraInfo?.hasFlashUnit()
        if (isFlashAvailable != null) {
            flashlightImgBtn?.isVisible = isFlashAvailable
        }
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC

        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        soundEnabled = sharedPref.getBoolean(SOUND_TAG, soundEnabled)
        vibrationEnabled = sharedPref.getBoolean(VIBRATION_TAG, vibrationEnabled)

        updateSoundIcon()
        updateVibrationIcon()

        flashlightEnabled = false
        updateFlashlightIcon()
    }

    private fun setupButtonListeners() {
        soundImgBtn?.setOnClickListener {
            if (soundEnabled)
                makeToast(getString(R.string.sound_disabled))
            else
                makeToast(getString(R.string.sound_enabled))

            soundEnabled = !soundEnabled
            updateSoundIcon()
        }

        vibrationImgBtn?.setOnClickListener {
            if (vibrationEnabled)
                makeToast(getString(R.string.vibration_disabled))
            else
                makeToast(getString(R.string.vibration_enabled))

            vibrationEnabled = !vibrationEnabled
            updateVibrationIcon()
        }

        flashlightImgBtn?.setOnClickListener {
            flashlightEnabled = !flashlightEnabled
            updateFlashlightIcon()
            switchFlashlight(flashlightEnabled)
        }
    }

    private fun updateSoundIcon() {
        if (soundEnabled)
            soundImgBtn?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_volume_up_24,
                    null
                )
            )
        else
            soundImgBtn?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_volume_off_24,
                    null
                )
            )
    }

    private fun updateVibrationIcon() {
        if (vibrationEnabled)
            vibrationImgBtn?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_vibration_24,
                    null
                )
            )
        else
            vibrationImgBtn?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_phonelink_erase_24,
                    null
                )
            )
    }

    private fun updateFlashlightIcon() {
        if (flashlightEnabled)
            flashlightImgBtn?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_flashlight_off_24,
                    null
                )
            )
        else
            flashlightImgBtn?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_flashlight_on_24,
                    null
                )
            )
    }

    private fun switchFlashlight(status: Boolean) {
        camera!!.cameraControl.enableTorch(status)
    }

    private fun setupCamera() {
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(CameraXViewModel::class.java)
            .processCameraProvider
            .observe(
                this,
                Observer { provider: ProcessCameraProvider? ->
                    cameraProvider = provider
                    if (isCameraPermissionGranted()) {
                        bindCameraUseCases()
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.CAMERA),
                            PERMISSION_CAMERA_REQUEST
                        )
                    }
                }
            )
    }

    private fun bindCameraUseCases() {
        bindPreviewUseCase()
        bindAnalyseUseCase()
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        previewUseCase = try {
            Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(previewView!!.display.rotation)
                .build()
        } catch (e: IllegalStateException) {
            Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .build()
        }
        previewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)

        try {
            camera = cameraProvider!!.bindToLifecycle(
                this,
                cameraSelector!!,
                previewUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message.toString())
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message.toString())
        }
    }

    private fun bindAnalyseUseCase() {
        // Note that if you know which format of barcode your app is dealing with, detection will be
        // faster to specify the supported barcode formats one by one, e.g.
        // BarcodeScannerOptions.Builder()
        //     .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        //     .build();
        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()

        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }

        try {
            analysisUseCase = ImageAnalysis.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(previewView!!.display.rotation)
                .build()
        } catch (e: java.lang.IllegalStateException) {
            analysisUseCase = ImageAnalysis.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .build()
        }

        // Initialize our background executor
        val cameraExecutor = Executors.newSingleThreadExecutor()

        analysisUseCase?.setAnalyzer(
            cameraExecutor,
            { imageProxy ->
                processImageProxy(barcodeScanner, imageProxy)
            }
        )

        try {
            cameraProvider!!.bindToLifecycle(
                this,
                cameraSelector!!,
                analysisUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message.toString())
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message.toString())
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        val inputImage =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                barcodes.distinct()
                barcodes.forEach {
                    if (!captured) {
                        captured = true
                        if (soundEnabled)
                            beep(100)
                        if (vibrationEnabled)
                            vibrate(100)

                        sendResult(it)
                    }
                }
            }
            .addOnFailureListener {
                //Log.e(TAG, it.message)
            }.addOnCompleteListener {
                // When the image is from CameraX analysis use case, must call image.close() on received
                // images when finished using them. Otherwise, new images may not be received or the camera
                // may stall.
                imageProxy.close()
            }
    }

    private fun sendResult(barcode: Barcode) {
        val i = Intent()
        i.putExtra(CODE_VALUE, barcode.displayValue)//todo
        i.putExtra(CODE_TYPE, BarcodeHelper().getStringType(barcode.format))
        setResult(RESULT_OK, i)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (isCameraPermissionGranted()) bindCameraUseCases() else makeToast(getString(R.string.camera_not_granted))
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun beep(duration: Int) {
        val audioManager: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentVolume: Int = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        Log.d("volume", currentVolume.toString())
        val toneG = ToneGenerator(AudioManager.STREAM_ALARM, currentVolume * 10)
        toneG.startTone(ToneGenerator.TONE_CDMA_PIP, duration)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            toneG.release()
        }, (duration + 50).toLong())
    }

    private fun vibrate(duration: Int) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    duration.toLong(),
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }
    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean(SOUND_TAG, soundEnabled)
            putBoolean(VIBRATION_TAG, vibrationEnabled)
            apply()
        }

        flashlightEnabled = false
        switchFlashlight(flashlightEnabled)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    companion object {
        private val TAG = ScanActivity::class.java.simpleName
        private const val PERMISSION_CAMERA_REQUEST = 1

        private var captured = false

        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

        const val CODE_VALUE = "code_value"
        const val CODE_TYPE = "code_type"
    }
}