package com.savan.expensetracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.savan.expensetracker.R
import com.savan.expensetracker.adapter.ExpenseAdapter
import com.savan.expensetracker.databinding.ActivityMainBinding
import com.savan.expensetracker.model.Expense
import com.savan.expensetracker.util.ExpenseRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val repository = ExpenseRepository()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var adapter: ExpenseAdapter
    private var expenseList: List<Expense> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupRecyclerView()

        binding.fabAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        binding.swipeRefresh.setOnRefreshListener { loadExpenses() }
    }

    override fun onResume() {
        super.onResume()
        // Refresh every time the screen becomes visible (e.g. after adding an expense)
        loadExpenses()
    }

    private fun setupRecyclerView() {
        adapter = ExpenseAdapter(expenseList) { expense ->
            deleteExpense(expense)
        }
        binding.rvExpenses.layoutManager = LinearLayoutManager(this)
        binding.rvExpenses.adapter = adapter
    }

    private fun loadExpenses() {
        binding.swipeRefresh.isRefreshing = true
        lifecycleScope.launch {
            val result = repository.getAllExpenses()
            binding.swipeRefresh.isRefreshing = false

            result.onSuccess { expenses ->
                expenseList = expenses
                adapter.updateData(expenses)

                val total = expenses.sumOf { it.amount }
                binding.tvTotalAmount.text = "₹%.2f".format(total)

                binding.tvEmptyState.visibility =
                    if (expenses.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE

            }.onFailure { e ->
                Toast.makeText(this@MainActivity, "Failed to load expenses: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteExpense(expense: Expense) {
        lifecycleScope.launch {
            val result = repository.deleteExpense(expense.id)
            result.onSuccess {
                Toast.makeText(this@MainActivity, "Expense deleted", Toast.LENGTH_SHORT).show()
                loadExpenses()
            }.onFailure { e ->
                Toast.makeText(this@MainActivity, "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_summary -> {
                startActivity(Intent(this, SummaryActivity::class.java))
                true
            }
            R.id.action_logout -> {
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
