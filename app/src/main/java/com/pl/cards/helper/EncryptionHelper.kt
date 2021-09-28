package com.pl.cards.helper

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.IOException
import java.security.*
import java.util.*
import javax.security.cert.CertificateException

class EncryptionHelper {
    private val AndroidKeyStore = "AndroidKeyStore"

    @Throws(
        NoSuchProviderException::class,
        NoSuchAlgorithmException::class,
        KeyStoreException::class,
        InvalidAlgorithmParameterException::class,
        UnrecoverableEntryException::class,
        CertificateException::class,
        IOException::class
    )
    fun getDbEncryptionKey(ctx: Context, alias: String): String {
        val keyStore: KeyStore = KeyStore.getInstance(AndroidKeyStore)

        keyStore.load(null)
        if (!keyStore.containsAlias(alias)) {
            val generator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, AndroidKeyStore
            )
            generator.initialize(
                KeyGenParameterSpec.Builder(
                    alias, KeyProperties.PURPOSE_SIGN
                )
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PSS)
                    .setKeySize(2048)
                    //.setUserAuthenticationRequired(true)
                    .build()
            )
            generator.generateKeyPair()
        }
        val privateKeyEntry: KeyStore.PrivateKeyEntry =
            keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry
        return Arrays.toString(privateKeyEntry.privateKey.encoded)
    }
}