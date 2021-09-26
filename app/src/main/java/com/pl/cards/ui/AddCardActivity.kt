package com.pl.cards.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
import com.pl.cards.R
import com.pl.cards.helper.BarcodeHelper
import com.pl.cards.helper.StoresTemplate
import com.pl.cards.model.Card
import com.pl.cards.viewmodel.CardViewModel
import java.security.SecureRandom

class AddCardActivity : AppCompatActivity() {
    companion object {
        const val CARD_EDIT = "com.pl.cards.card_edit"
        const val CARD_ID = "com.pl.cards.card_id"
    }

    private var storeId: Long = -1
    private var type: String = ""
    private var card: Card? = null

    private lateinit var numberTIL: TextInputLayout
    private lateinit var numberET: TextInputEditText
    private lateinit var storesDropdown: MaterialAutoCompleteTextView
    private lateinit var storeTIL: TextInputLayout
    private lateinit var typeDropdown: MaterialAutoCompleteTextView
    private lateinit var typeTIL: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        val nameET = findViewById<TextInputEditText>(R.id.cardNameTIET)
        numberTIL = findViewById(R.id.cardNumberTIL)
        numberET = findViewById(R.id.cardNumberTIET)
        storesDropdown = findViewById(R.id.cardStoreACTV)
        storeTIL = findViewById(R.id.cardStoreTIL)
        typeDropdown = findViewById(R.id.cardTypeACTV)
        typeTIL = findViewById(R.id.cardValueTypeTIL)
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
                prepareEdit(card!!, nameET, numberET)
                deleteBtn.visibility = View.VISIBLE
            }
        }

        deleteBtn.setOnClickListener { deleteConfirm(cardViewModel) }

        numberTIL.setEndIconOnClickListener { scanCode() }

        storeDropdownInit()
        typeDropdownInit()

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
                            storeId, type, cardViewModel
                        )
                } else
                    save(
                        nameET.text.toString().trim(),
                        numberET.text.toString().trim(),
                        storeId, type, cardViewModel
                    )
            }
        }
    }

    private fun prepareEdit(
        card: Card,
        nameTv: TextInputEditText,
        numberTv: TextInputEditText
    ) {
        nameTv.setText(card.name)
        numberTv.setText(card.value)
        storeId = card.store
        typeDropdown.setText(card.type)
        type = card.type

        numberTIL.endIconMode = END_ICON_NONE
        numberTIL.isEnabled = false
        numberTv.isEnabled = false
        typeDropdownDisable()
        storeDropdownDisable()
    }

    private fun typeDropdownInit() {
        val types = BarcodeHelper().barcodeTypes

        val typeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            types
        )

        typeDropdown.setOnItemClickListener { parent, view, position, id ->
            type = typeDropdown.text.toString()//types[position]
        }

        typeDropdown.setAdapter(typeAdapter)
    }

    private fun storeDropdownInit() {
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

        StoresTemplate().storesList.stream().forEach { store ->
            storesMap[store.name] = store.id

            val n = store.name
            if (!array.contains(n))
                array.add(n)
        }

        if (intent.hasExtra(CARD_EDIT))
            if (intent.getBooleanExtra(CARD_EDIT, false)) {
                val ind =
                    array.indexOf(storesMap.filter { card!!.store == it.value }.keys.first())
                storeId = card!!.store
                //storesDropdown.setSelection(ind)
                storesDropdown.setText(array[ind])
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
        cardViewModel: CardViewModel
    ) {
        val card = Card(SecureRandom().nextLong(), store, name, number, type)
        cardViewModel.insert(card)
        finish()
    }

    private fun update(
        card: Card,
        name: String,
        number: String,
        store: Long,
        type: String,
        cardViewModel: CardViewModel
    ) {
        card.name = name
        card.value = number
        card.store = store
        card.type = type

        cardViewModel.update(card)
        finish()
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                typeDropdownDisable()
                val data: Intent? = result.data
                numberET.setText(data?.getStringExtra(ScanActivity.CODE_VALUE))
                val t = data?.getStringExtra(ScanActivity.CODE_TYPE)
                typeDropdown.setText(t)
                type = t!!
            }
        }

    private fun typeDropdownDisable() {
        typeDropdown.isEnabled = false
        typeTIL.isEnabled = false
        typeTIL.endIconMode = END_ICON_NONE
        typeDropdown.dropDownHeight = 0
    }

    private fun storeDropdownDisable() {
        storesDropdown.isEnabled = false
        storeTIL.isEnabled = false
        storeTIL.endIconMode = END_ICON_NONE
        storesDropdown.dropDownHeight = 0
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
        builder.setMessage(getString(R.string.you_sure))
            .setPositiveButton(getString(R.string.yes), dialogClickListener)
            .setNegativeButton(getString(R.string.no), dialogClickListener).show()
    }
}