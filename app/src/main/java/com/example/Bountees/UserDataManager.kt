package com.example.Bountees.database

import android.content.Context
import android.util.Log
import com.example.loginactivity.DBHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

/**
 * A manager class that handles synchronization between SQLite and Firebase
 */
class UserDataManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://com-example-bountees-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    private val localDb = DBHelper(context)

    /**
     * Register a new user in both SQLite and Firebase
     */
    fun registerUser(username: String, password: String, callback: (success: Boolean, message: String) -> Unit) {
        Log.d("UserDataManager", "Starting registerUser for username: $username") // Added log

        // First register in SQLite
        val localId = localDb.insertUser(username, password)
        Log.d("UserDataManager", "SQLite insert result for username '$username': localId = $localId")

        if (localId == -1L) {
            callback(false, "User already exists in local database")
            Log.w("UserDataManager", "Registration failed: User already exists locally - username: $username")
            return
        }

        // Then register in Firebase Auth with email (username + dummy domain)
        val email = "$username@example.com"  // Create a pseudo email

        Log.d("UserDataManager", "Attempting Firebase Auth createUserWithEmailAndPassword for email: $email") // Added log
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                Log.d("UserDataManager", "Firebase Auth createUserWithEmailAndPassword completed for email: $email, successful: ${task.isSuccessful}") // Added log
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId == null) {
                        Log.e("UserDataManager", "Firebase Auth registration successful, but UID is null for username: $username")
                        callback(false, "Registration failed: Could not get Firebase UID")
                        return@addOnCompleteListener
                    }

                    // Store user data in Firebase Realtime Database - STORE ONLY USERNAME, NOT EMAIL
                    val user = mapOf(
                        "username" to username, // Store only username in RTDB
                        "password" to password  // Note: In a real app, never store plaintext passwords
                    )

                    Log.d("UserDataManager", "Attempting Firebase RTDB setValue for userId: $userId, data: $user") // Added log
                    database.child("users").child(userId).setValue(user)
                        .addOnSuccessListener {
                            Log.d("UserDataManager", "Firebase RTDB setValue successful for userId: $userId") // Added log
                            Log.d("UserDataManager", "User registered in both SQLite and Firebase - username: $username, Firebase UID: $userId")
                            callback(true, "User registered successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("UserDataManager", "Failed to register in Firebase RTDB for username: $username, error: ${e.message}")
                            callback(false, "Failed to register in Firebase Database: ${e.message}")
                        }
                } else {
                    Log.e("UserDataManager", "Firebase Auth registration failed for username: $username, error: ${task.exception?.message}")
                    callback(false, "Firebase registration failed: ${task.exception?.message}")
                }
            }
    }

    /**
     * Log in a user using both SQLite and Firebase
     */
    fun loginUser(username: String, password: String, callback: (success: Boolean, message: String) -> Unit) {
        Log.d("UserDataManager", "Login attempt for username: $username")

        // First check if user exists in SQLite
        val localUserExists = localDb.readUser(username, password)
        Log.d("UserDataManager", "SQLite user check for username '$username': exists = $localUserExists")


        if (!localUserExists) {
            Log.d("UserDataManager", "User not found in SQLite, checking Firebase for username: $username")
            // Try to fetch from Firebase and sync to local if found
            val email = "$username@example.com"  // Create a pseudo email

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("UserDataManager", "Firebase Auth login successful for username: $username (synced from Firebase)")
                        // Found in Firebase, now sync to local (already done during registration, but in case of data loss, re-sync)
                        val localInsertResult = localDb.insertUser(username, password) // Re-insert to ensure local is synced if somehow missed before
                        Log.d("UserDataManager", "Re-inserting user into SQLite during Firebase-sync login, result: $localInsertResult")
                        callback(true, "Login successful (synced from Firebase)")
                    } else {
                        Log.w("UserDataManager", "Firebase Auth login failed for username: $username, error: ${task.exception?.message}")
                        callback(false, "User not found in either database")
                    }
                }
        } else {
            Log.d("UserDataManager", "User found in SQLite, verifying with Firebase Auth for username: $username")
            // User exists locally, try to verify with Firebase too
            val email = "$username@example.com"

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("UserDataManager", "Firebase Auth login successful for username: $username (local and firebase verified)")
                        callback(true, "Login successful")
                    } else {
                        Log.w("UserDataManager", "Firebase Auth login failed (even though user is local) for username: $username, error: ${task.exception?.message}")
                        // User exists locally but Firebase Auth login failed. This could be due to password mismatch in Firebase, or network issues.
                        // For now, we'll consider local login successful since user exists locally. In a real app, you might want more robust error handling and syncing.
                        callback(true, "Login successful (local only)") // Consider this case carefully in production.
                        // syncLocalUserToFirebase(username, password) { success -> // Re-sync if Firebase fails? Be cautious about infinite loops if sync always fails.
                        //     if (success) {
                        //         callback(true, "Login successful (synced to Firebase)")
                        //     } else {
                        //         callback(true, "Login successful (local only)")
                        //     }
                        // }
                    }
                }
        }
    }

    /**
     * Sync a user from local SQLite to Firebase
     */
    private fun syncLocalUserToFirebase(username: String, password: String, callback: (success: Boolean) -> Unit) {
        val email = "$username@example.com"

        auth.createUserWithEmailAndPassword(email, password) // Re-creating Firebase Auth user? Be cautious, might be better to update password if needed.
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: run {
                        Log.e("UserDataManager", "Firebase Auth user created during sync, but UID is null for username: $username")
                        callback(false)
                        return@addOnCompleteListener
                    }


                    val user = mapOf(
                        "username" to username,
                        "password" to password
                    )

                    database.child("users").child(userId).setValue(user)
                        .addOnSuccessListener {
                            Log.d("UserDataManager", "Local user synced to Firebase - username: $username, Firebase UID: $userId")
                            callback(true)
                        }
                        .addOnFailureListener { e ->
                            Log.e("UserDataManager", "Failed to sync local user to Firebase RTDB - username: $username, error: ${e.message}")
                            callback(false)
                        }
                } else {
                    Log.e("UserDataManager", "Failed to create Firebase auth for local user during sync - username: $username, error: ${task.exception?.message}")
                    callback(false)
                }
            }
    }

    /**
     * Sync all users from Firebase to local SQLite
     */
    fun syncFirebaseToLocal(callback: (success: Boolean, count: Int) -> Unit) {
        database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var syncCount = 0

                for (userSnapshot in snapshot.children) {
                    val username = userSnapshot.child("username").getValue(String::class.java) ?: continue
                    val password = userSnapshot.child("password").getValue(String::class.java) ?: continue

                    val result = localDb.insertUser(username, password)
                    if (result != -1L) {
                        syncCount++
                    }
                }

                Log.d("UserDataManager", "Synced $syncCount users from Firebase to local")
                callback(true, syncCount)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserDataManager", "Firebase sync cancelled: ${error.message}")
                callback(false, 0)
            }
        })
    }

    /**
     * Sync all users from local SQLite to Firebase
     */
    fun syncLocalToFirebase(callback: (success: Boolean, count: Int) -> Unit) {
        val localUsers = localDb.getAllUsers()
        var syncCount = 0

        for (user in localUsers) {
            val email = "${user.username}@example.com"

            // Check if user already exists in Firebase
            database.child("users")
                .orderByChild("username")
                .equalTo(user.username)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            // User doesn't exist in Firebase, create them
                            auth.createUserWithEmailAndPassword(email, user.password)
                                .addOnSuccessListener { authResult ->
                                    val userId = authResult.user?.uid ?: return@addOnSuccessListener

                                    val userData = mapOf(
                                        "username" to user.username,
                                        "password" to user.password
                                    )

                                    database.child("users").child(userId).setValue(userData)
                                        .addOnSuccessListener {
                                            syncCount++
                                            if (syncCount == localUsers.size) {
                                                callback(true, syncCount)
                                            }
                                        }
                                }
                                .addOnFailureListener {
                                    if (syncCount == localUsers.size) {
                                        callback(syncCount > 0, syncCount)
                                    }
                                }
                        } else {
                            // User already exists in Firebase
                            if (syncCount == localUsers.size) {
                                callback(true, syncCount)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        if (syncCount == localUsers.size) {
                            callback(syncCount > 0, syncCount)
                        }
                    }
                })
        }

        // If no local users, return immediately
        if (localUsers.isEmpty()) {
            callback(true, 0)
        }
    }

    /**
     * Logout user from both systems
     */
    fun logoutUser() {
        auth.signOut()
        // SQLite doesn't have sessions, so no logout needed
    }
}