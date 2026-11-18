package com.savan.expensetracker.ui

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.savan.expensetracker.databinding.ActivitySummaryBinding
import com.savan.expensetracker.model.Expense
import com.savan.expensetracker.util.ExpenseRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class SummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySummaryBinding
    private val repository = ExpenseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        loadMonthlySummary()
    }

    private fun loadMonthlySummary() {
        lifecycleScope.launch {
            val result = repository.getAllExpenses()

            result.onSuccess { allExpenses ->
                val thisMonthExpenses = filterCurrentMonth(allExpenses)

                if (thisMonthExpenses.isEmpty()) {
                    binding.tvEmptySummary.visibility = android.view.View.VISIBLE
                    binding.pieChart.visibility = android.view.View.GONE
                    binding.tvMonthTotal.text = "₹0.00"
                    return@onSuccess
                }

                binding.tvEmptySummary.visibility = android.view.View.GONE
                binding.pieChart.visibility = android.view.View.VISIBLE

                val total = thisMonthExpenses.sumOf { it.amount }
                binding.tvMonthTotal.text = "₹%.2f".format(total)

                renderPieChart(thisMonthExpenses)

            }.onFailure { e ->
                Toast.makeText(this@SummaryActivity, "Failed to load summary: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** Keeps only expenses that fall within the current calendar month/year. */
    private fun filterCurrentMonth(expenses: List<Expense>): List<Expense> {
        val now = Calendar.getInstance()
        val currentMonth = now.get(Calendar.MONTH)
        val currentYear = now.get(Calendar.YEAR)

        return expenses.filter { expense ->
            val cal = Calendar.getInstance().apply { timeInMillis = expense.date }
            cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
        }
    }

    /** Groups expenses by category and renders them as a pie chart. */
    private fun renderPieChart(expenses: List<Expense>) {
        val groupedByCategory = expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val entries = groupedByCategory.map { (category, total) ->
            PieEntry(total.toFloat(), category)
        }

        val dataSet = PieDataSet(entries, "Spending by Category")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList() + ColorTemplate.VORDIPLOM_COLORS.toList()
        dataSet.valueTextSize = 13f
        dataSet.valueTextColor = Color.WHITE

        val pieData = PieData(dataSet)
        binding.pieChart.data = pieData
        binding.pieChart.description.isEnabled = false
        binding.pieChart.centerText = "This Month"
        binding.pieChart.setCenterTextSize(16f)
        binding.pieChart.legend.isEnabled = true
        binding.pieChart.animateY(700)
        binding.pieChart.invalidate() // refresh the chart
    }
}
