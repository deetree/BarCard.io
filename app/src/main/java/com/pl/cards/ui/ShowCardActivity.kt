package com.pl.cards.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.aztec.AztecWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.datamatrix.DataMatrixWriter
import com.google.zxing.oned.*
import com.google.zxing.pdf417.PDF417Writer
import com.google.zxing.qrcode.QRCodeWriter
import com.pl.cards.R
import com.pl.cards.helper.BarcodeHelper
import com.pl.cards.helper.StoresTemplate
import com.pl.cards.viewmodel.CardViewModel


class ShowCardActivity : AppCompatActivity() {

    lateinit var barcodeImg: ImageView
    lateinit var barcodeValue: MaterialTextView
    lateinit var logo: ImageView
    lateinit var cardview: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_card)

        supportActionBar?.hide()

        setMaxBrightness()

        val root = findViewById<ConstraintLayout>(R.id.showCardRoot)
        logo = findViewById(R.id.showCardLogo)
        cardview = findViewById(R.id.showCardView)
        barcodeImg = findViewById(R.id.showCardBarcode)
        barcodeValue = findViewById(R.id.showCardBarcodeValue)

        val cardId = intent.getLongExtra(AddCardActivity.CARD_ID, -1)
        val cardViewModel = ViewModelProvider(this).get(CardViewModel::class.java)
        val card = cardViewModel.getCard(cardId)

        val store = StoresTemplate(this).storesList.first { s -> s.id == card.store }

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        root.setBackgroundColor(Color.parseColor(store.color))
        window.statusBarColor = Color.parseColor(store.color)
        logo.setImageResource(store.image)
        setLogoSize(width, height)
        setCardSize(width, height)

        try {
            displayBitmap(card.value, card.type, width, height / 5 * 3)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, getString(R.string.cannot_generate), Toast.LENGTH_LONG).show()
        }
    }

    private fun copyToClipboard(text: String) {
        val myClipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip: ClipData = ClipData.newPlainText("barcode_value", text)
        myClipboard.setPrimaryClip(myClip)

        Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show()
    }

    private fun setMaxBrightness() {
        val layout = window.attributes
        layout.screenBrightness = 1f
        window.attributes = layout
    }

    private fun setLogoSize(width: Int, height: Int) {
        val params = logo.layoutParams
        params.width = width - 20
        params.height = height / 4
        logo.layoutParams = params
    }

    private fun setCardSize(width: Int, height: Int) {
        val params = cardview.layoutParams
        params.width = width - 100
        params.height = height / 3 * 2
        cardview.layoutParams = params
    }

    private fun displayBitmap(value: String, type: String, widthPixels: Int, heightPixels: Int) {
        barcodeImg.setImageBitmap(
            createBarcodeBitmap(
                value,
                getColor(R.color.black),
                getColor(android.R.color.white),
                widthPixels,
                heightPixels,
                type
            )
        )
        barcodeValue.text = value
        barcodeValue.setOnClickListener { copyToClipboard(value) }
    }

    private fun createBarcodeBitmap(
        barcodeValue: String,
        @ColorInt barcodeColor: Int,
        @ColorInt backgroundColor: Int,
        widthPixels: Int,
        heightPixels: Int,
        type: String
    ): Bitmap {
        val bitMatrix = getBitMatrix(barcodeValue, widthPixels, heightPixels, type)!!

        val pixels = IntArray(bitMatrix.width * bitMatrix.height)
        for (y in 0 until bitMatrix.height) {
            val offset = y * bitMatrix.width
            for (x in 0 until bitMatrix.width) {
                pixels[offset + x] =
                    if (bitMatrix.get(x, y)) barcodeColor else backgroundColor
            }
        }

        val bitmap = Bitmap.createBitmap(
            bitMatrix.width,
            bitMatrix.height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.setPixels(
            pixels,
            0,
            bitMatrix.width,
            0,
            0,
            bitMatrix.width,
            bitMatrix.height
        )
        return bitmap
    }

    private fun getBitMatrix(
        barcodeValue: String,
        widthPixels: Int,
        heightPixels: Int,
        type: String
    ): BitMatrix? {
        return when (type) {
            BarcodeHelper.CODE_128 -> Code128Writer().encode(
                barcodeValue,
                BarcodeFormat.CODE_128,
                widthPixels,
                heightPixels
            )
            BarcodeHelper.CODE_39 -> Code39Writer().encode(
                barcodeValue,
                BarcodeFormat.CODE_39,
                widthPixels,
                heightPixels
            )
            BarcodeHelper.CODE_93 -> Code93Writer().encode(
                barcodeValue,
                BarcodeFormat.CODE_93,
                widthPixels,
                heightPixels
            )
            BarcodeHelper.QR_CODE -> QRCodeWriter().encode(
                barcodeValue,
                BarcodeFormat.QR_CODE,
                widthPixels,
                heightPixels
            )
            BarcodeHelper.UPC_A -> UPCAWriter().encode(
                barcodeValue,
                BarcodeFormat.UPC_A,
                widthPixels,
                heightPixels
            )
            BarcodeHelper.UPC_E -> UPCEWriter().encode(
                barcodeValue,
                BarcodeFormat.UPC_E,
                widthPixels,
                heightPixels
            )
            BarcodeHelper.EAN_8 -> EAN8Writer().encode(
                barcodeValue,
                BarcodeFormat.EAN_8,
                widthPixels,
                heightPixels
            )
            BarcodeHelper.EAN_13 -> EAN13Writer().encode(
                barcodeValue,
                BarcodeFormat.EAN_13,
                widthPixels,
                heightPixels
            )
            BarcodeHelper.ITF -> ITFWriter().encode(
                barcodeValue,
                BarcodeFormat.ITF,
                widthPixels,
                heightPixels
            )
            BarcodeHelper.CODABAR -> CodaBarWriter().encode(
                barcodeValue,
                BarcodeFormat.CODABAR,
                widthPixels,
                heightPixels
            )
            BarcodeHelper.DATA_MATRIX -> DataMatrixWriter().encode(
                barcodeValue,
                BarcodeFormat.DATA_MATRIX,
                widthPixels,
                heightPixels
            )
            BarcodeHelper.AZTEC -> AztecWriter().encode(
                barcodeValue,
                BarcodeFormat.AZTEC,
                widthPixels,
                heightPixels
            )
            BarcodeHelper.PDF_417 -> PDF417Writer().encode(
                barcodeValue,
                BarcodeFormat.PDF_417,
                widthPixels,
                heightPixels
            )
            else -> null
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}