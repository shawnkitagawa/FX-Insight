package com.example.fxinsight.data.repository

import android.util.Log
import com.example.fxinsight.data.network.APIService.ProfileAPIService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun isSession(): Boolean



}

class DefaultAuthRepository(
    private val supabase: SupabaseClient,
) : AuthRepository {

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

    override suspend fun isSession(): Boolean {
        return supabase.auth.currentUserOrNull() != null
    }




}