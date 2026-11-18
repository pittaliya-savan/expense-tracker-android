package com.savan.expensetracker.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.savan.expensetracker.model.Expense
import kotlinx.coroutines.tasks.await

/**
 * Single point of contact for all Firestore read/write operations.
 * Keeping this separate from Activities/Fragments follows a basic
 * repository pattern, making the data layer easy to test or swap later.
 */
class ExpenseRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private fun currentUserId(): String? = auth.currentUser?.uid

    private fun expensesCollection() =
        currentUserId()?.let { uid ->
            db.collection("users").document(uid).collection("expenses")
        }

    /** Adds a new expense document for the logged-in user. */
    suspend fun addExpense(expense: Expense): Result<Unit> {
        return try {
            val collection = expensesCollection()
                ?: return Result.failure(IllegalStateException("User not logged in"))

            val docRef = collection.document() // auto-generated ID
            val expenseWithId = expense.copy(id = docRef.id)
            docRef.set(expenseWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Fetches all expenses for the logged-in user, most recent first. */
    suspend fun getAllExpenses(): Result<List<Expense>> {
        return try {
            val collection = expensesCollection()
                ?: return Result.failure(IllegalStateException("User not logged in"))

            val snapshot = collection.orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val expenses = snapshot.toObjects(Expense::class.java)
            Result.success(expenses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Deletes a single expense by its Firestore document ID. */
    suspend fun deleteExpense(expenseId: String): Result<Unit> {
        return try {
            val collection = expensesCollection()
                ?: return Result.failure(IllegalStateException("User not logged in"))

            collection.document(expenseId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
