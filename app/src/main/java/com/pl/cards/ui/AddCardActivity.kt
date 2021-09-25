package com.pl.cards.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pl.cards.R
import com.pl.cards.helper.BarcodeHelper
import com.pl.cards.model.Card
import com.pl.cards.viewmodel.CardViewModel
import com.pl.cards.viewmodel.StoreViewModel
import java.security.SecureRandom

class AddCardActivity : AppCompatActivity() {
    companion object {
        const val CARD_EDIT = "com.pl.cards.card_edit"
        const val CARD_ID = "com.pl.cards.card_id"
    }

    private var storeId: Long = -1
    private var type: String = ""
    private var card: Card? = null

    private lateinit var numberET: TextInputEditText
    private lateinit var typeDropdown: MaterialAutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        val storeViewModel = ViewModelProvider(this).get(StoreViewModel::class.java)

        val nameET = findViewById<TextInputEditText>(R.id.cardNameTIET)
        val numberTIL = findViewById<TextInputLayout>(R.id.cardNumberTIL)
        numberET = findViewById(R.id.cardNumberTIET)
        val storesDropdown = findViewById<MaterialAutoCompleteTextView>(R.id.cardStoreACTV)
        typeDropdown = findViewById(R.id.cardTypeACTV)
        val privateLayout = findViewById<ConstraintLayout>(R.id.cardPrivateConstraintLayout)
        val privateChkBox = findViewById<MaterialCheckBox>(R.id.cardPrivateChkBox)
        val saveBtn = findViewById<MaterialButton>(R.id.cardSaveBtn)
        val deleteBtn = findViewById<MaterialButton>(R.id.cardDeleteBtn)
        val rootView = findViewById<ConstraintLayout>(R.id.cardConstraintLayout)

        val cardViewModel = ViewModelProvider(this).get(CardViewModel::class.java)

        if (intent.hasExtra(CARD_EDIT)) {
            if (intent.getBooleanExtra(CARD_EDIT, false)) {
                card = cardViewModel.getCard(
                    intent.getLongExtra(
                        CARD_ID, -1
                    )
                )
                prepareEdit(card!!, nameET, numberET, privateChkBox, typeDropdown)
                deleteBtn.visibility = View.VISIBLE
            }
        }

        deleteBtn.setOnClickListener { deleteConfirm(cardViewModel) }

        numberTIL.setEndIconOnClickListener { scanCode() }

        storeDropdownInit(storeViewModel, storesDropdown)
        typeDropdownInit(typeDropdown)

        privateLayout.setOnClickListener { privateChkBox.isChecked = !privateChkBox.isChecked }

        saveBtn.setOnClickListener {
            if (validate(
                    nameET.text.toString().trim(),
                    numberET.text.toString().trim(),
                    storeId,
                    type,
                    rootView
                )
            ) {
                if (intent.hasExtra(CARD_EDIT)) {
                    if (intent.getBooleanExtra(CARD_EDIT, false))
                        update(
                            card!!, nameET.text.toString().trim(),
                            numberET.text.toString().trim(),
                            storeId, type, privateChkBox.isChecked, cardViewModel
                        )
                } else
                    save(
                        nameET.text.toString().trim(),
                        numberET.text.toString().trim(),
                        storeId, type, privateChkBox.isChecked, cardViewModel
                    )
            }
        }
    }

    private fun prepareEdit(
        card: Card,
        nameTv: TextInputEditText,
        numberTv: TextInputEditText,
        privateChkBox: MaterialCheckBox,
        typeDropdown: MaterialAutoCompleteTextView
    ) {
        nameTv.setText(card.name)
        numberTv.setText(card.value)
        privateChkBox.isChecked = card.priv == 1
        storeId = card.store
        typeDropdown.setText(card.type)
        type = card.type
    }

    private fun typeDropdownInit(typeDropdown: MaterialAutoCompleteTextView) {
        val types = BarcodeHelper().barcodeTypes

        val typeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            types
        )

        typeDropdown.setOnItemClickListener { parent, view, position, id ->
            type = types[position]
        }

        typeDropdown.setAdapter(typeAdapter)
    }

    private fun storeDropdownInit(
        storeViewModel: StoreViewModel,
        storesDropdown: MaterialAutoCompleteTextView
    ) {
        val storesMap: HashMap<String, Long> = java.util.HashMap()

        val array = arrayListOf<String>()

        val storesAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            array
        )

        storesDropdown.setOnItemClickListener { parent, view, position, id ->
            storeId = storesMap[array[position]]!!
        }

        storesDropdown.setAdapter(storesAdapter)

        storeViewModel.getAllStores().observe(this) { stores ->
            if (array.isNotEmpty())
                array.clear()
            if (stores != null) {
                for (i in stores.indices) {
                    storesMap[stores[i].name] = stores[i].id

                    val n = stores[i].name
                    if (!array.contains(n))
                        array.add(n)
                }
                storesAdapter.notifyDataSetChanged()

                if (intent.hasExtra(CARD_EDIT))
                    if (intent.getBooleanExtra(CARD_EDIT, false)) {
                        val ind =
                            array.indexOf(storesMap.filter { card!!.store == it.value }.keys.first())
                        storesDropdown.setSelection(ind)
                        storesDropdown.setText(array[ind])
                    }
            }
        }
    }

    private fun validate(
        name: String,
        number: String,
        store: Long,
        type: String,
        rootView: ConstraintLayout
    ): Boolean {
        return if (name.isNotEmpty() && number.isNotEmpty() && store != -1L && type != "") {
            true
        } else {
            Snackbar.make(rootView, getString(R.string.invalid_data), Snackbar.LENGTH_SHORT)
                .setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show()
            false
        }
    }

    private fun save(
        name: String,
        number: String,
        store: Long,
        type: String,
        priv: Boolean,
        cardViewModel: CardViewModel
    ) {
        val privVal = if (priv) 1 else 0

        val card = Card(SecureRandom().nextLong(), store, name, number, privVal, type)
        cardViewModel.insert(card)
        finish()
    }

    private fun update(
        card: Card,
        name: String,
        number: String,
        store: Long,
        type: String,
        priv: Boolean,
        cardViewModel: CardViewModel
    ) {
        card.name = name
        card.value = number
        card.store = store
        card.type = type
        card.priv = if (priv) 1 else 0

        cardViewModel.update(card)
        finish()
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                numberET.setText(data?.getStringExtra(ScanActivity.CODE_VALUE))
                val t = data?.getStringExtra(ScanActivity.CODE_TYPE)
                typeDropdown.setText(t)
                type = t!!
            }
        }

    private fun scanCode() {
        val intent = Intent(this, ScanActivity::class.java)
        resultLauncher.launch(intent)
    }

    private fun deleteConfirm(cardViewModel: CardViewModel) {
        val dialogClickListener: DialogInterface.OnClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        cardViewModel.delete(card!!)
                        finish()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        dialog?.dismiss()
                    }
                }
            }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.you_sure)).setPositiveButton(getString(R.string.yes), dialogClickListener)
            .setNegativeButton(getString(R.string.no), dialogClickListener).show()
    }
}