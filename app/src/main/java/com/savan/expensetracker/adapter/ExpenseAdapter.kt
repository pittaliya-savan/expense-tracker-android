package com.savan.expensetracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.savan.expensetracker.databinding.ItemExpenseBinding
import com.savan.expensetracker.model.Expense
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseAdapter(
    private var expenses: List<Expense>,
    private val onDeleteClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        with(holder.binding) {
            tvCategory.text = expense.category
            tvNote.text = expense.note.ifEmpty { "No note" }
            tvAmount.text = "₹%.2f".format(expense.amount)
            tvDate.text = dateFormat.format(expense.date)

            btnDelete.setOnClickListener { onDeleteClick(expense) }
        }
    }

    override fun getItemCount(): Int = expenses.size

    /** Call this after fetching updated data from Firestore. */
    fun updateData(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}
