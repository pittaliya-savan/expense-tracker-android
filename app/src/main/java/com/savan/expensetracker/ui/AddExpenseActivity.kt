package com.savan.expensetracker.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.savan.expensetracker.databinding.ActivityAddExpenseBinding
import com.savan.expensetracker.model.Expense
import com.savan.expensetracker.model.ExpenseCategories
import com.savan.expensetracker.util.ExpenseRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private val repository = ExpenseRepository()

    private var selectedDate: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCategoryDropdown()
        setupDatePicker()

        binding.btnSave.setOnClickListener { saveExpense() }
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupCategoryDropdown() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            ExpenseCategories.ALL
        )
        binding.actvCategory.setAdapter(adapter)
        // Default to first category so the field is never empty
        binding.actvCategory.setText(ExpenseCategories.ALL.first(), false)
    }

    private fun setupDatePicker() {
        updateDateLabel()
        binding.etDate.setOnClickListener {
            val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    val newCal = Calendar.getInstance()
                    newCal.set(year, month, day)
                    selectedDate = newCal.timeInMillis
                    updateDateLabel()
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateDateLabel() {
        val fmt = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        binding.etDate.setText(fmt.format(selectedDate))
    }

    private fun saveExpense() {
        val amountText = binding.etAmount.text.toString().trim()
        val category = binding.actvCategory.text.toString().trim()
        val note = binding.etNote.text.toString().trim()

        if (amountText.isEmpty()) {
            binding.etAmount.error = "Enter an amount"
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.etAmount.error = "Enter a valid amount"
            return
        }

        if (category.isEmpty()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSave.isEnabled = false

        val expense = Expense(
            amount = amount,
            category = category,
            note = note,
            date = selectedDate
        )

        lifecycleScope.launch {
            val result = repository.addExpense(expense)
            binding.btnSave.isEnabled = true

            result.onSuccess {
                Toast.makeText(this@AddExpenseActivity, "Expense added", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure { e ->
                Toast.makeText(this@AddExpenseActivity, "Failed to save: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
