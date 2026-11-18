package com.savan.expensetracker.model

/**
 * Data model for a single expense entry.
 * Stored in Firestore under: users/{uid}/expenses/{documentId}
 *
 * No-arg constructor + default values are required for Firestore's
 * automatic object mapping (toObject()/toObjects()) to work.
 */
data class Expense(
    var id: String = "",
    var amount: Double = 0.0,
    var category: String = "",
    var note: String = "",
    var date: Long = System.currentTimeMillis()
)

/**
 * Fixed set of categories used across Add Expense and Summary screens.
 * Keeping this centralized avoids typos causing mismatched chart slices.
 */
object ExpenseCategories {
    val ALL = listOf(
        "Food",
        "Transport",
        "Shopping",
        "Bills & Utilities",
        "Entertainment",
        "Health",
        "Education",
        "Other"
    )
}
