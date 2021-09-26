package com.pl.cards.helper

class BarcodeHelper {
    companion object {
        const val UPC_A = "UPC-A"
        const val UPC_E = "UPC-E"
        const val EAN_8 = "EAN-8"
        const val EAN_13 = "EAN-13"
        const val CODE_39 = "Code 39"
        const val CODE_93 = "Code 93"
        const val CODE_128 = "Code 128"
        const val ITF = "ITF"
        const val CODABAR = "Codabar"
        const val QR_CODE = "QR Code"
        const val DATA_MATRIX = "Data Matrix"
        const val AZTEC = "Aztec"
        const val PDF_417 = "PDF 417"
    }

    val barcodeTypes = arrayOf(
        UPC_A,
        UPC_E,
        EAN_8,
        EAN_13,
        CODE_39,
        CODE_93,
        CODE_128,
        ITF,
        CODABAR,
        QR_CODE,
        DATA_MATRIX,
        AZTEC,
        PDF_417,
    )

    fun getStringType(code: Int): String {
        return when (code) {
            4096 -> AZTEC
            8 -> CODABAR
            1 -> CODE_128
            2 -> CODE_39
            4 -> CODE_93
            16 -> DATA_MATRIX
            32 -> EAN_13
            64 -> EAN_8
            2048 -> PDF_417
            256 -> QR_CODE
            512 -> UPC_A
            1024 -> UPC_E
            128 -> ITF
            else -> ""
        }
    }
}