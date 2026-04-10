package com.example.fxinsight.data.repository

import android.util.Log
import com.example.fxinsight.data.remote.dto.UserProfileDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from

interface FXInsightRepository {
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String, userName: String): Result<Unit>

    suspend fun signOut(): Result<Unit>
    suspend fun addUserName(userName: String): Result<Unit>
    suspend fun isSession(): Boolean
    suspend fun getUsername(): Result<String>

}

class DefaultFXInsightRepository(
    private val supabase: SupabaseClient
) : FXInsightRepository {

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("SignIN", "failed login")
            Result.failure(e)
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        userName: String
    ): Result<Unit> {
        return try {
            // 2. create auth account
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            Log.d("SignUP", "Execute succesfully ")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("SignUP", "Failed ")
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try{
            supabase.auth.signOut()
            Result.success(Unit)
        }catch(e: Exception)
        {
            Result.failure(e)
        }
    }


    private suspend fun waitForSession(
        maxAttempts: Int = 10,
        delayMillis: Long = 300
    ): Boolean {
        repeat(maxAttempts) {
            val user = supabase.auth.currentUserOrNull()
            if (user != null) return true
            kotlinx.coroutines.delay(delayMillis)
        }
        return false
    }

    private suspend fun checkUsernameExist(userName: String): Result<Unit> {
        return try {
            val exists = supabase
                .from("profile")
                .select {
                    filter {
                        eq("user_name", userName)
                    }
                }
                .decodeList<UserProfileDto>()
                .isNotEmpty()

            if (exists) {
                Result.failure(Exception("Username is already taken"))
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addUserName(userName: String): Result<Unit> {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return Result.failure(Exception("User is not logged in"))

        return try {
            supabase.from("profile").insert(
                UserProfileDto(
                    user_id = userId,
                    user_name = userName
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isSession(): Boolean {
        return supabase.auth.currentUserOrNull() != null
    }

    override suspend fun getUsername(): Result<String> {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return Result.failure(Exception("User is not logged in"))

        return try {
            val profile = supabase
                .from("profile")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<UserProfileDto>()

            Result.success(profile.user_name)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}