package com.example.fxinsight.data.repositiory

import android.util.Log
import com.example.fxinsight.data.remote.dto.UserProfileDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth

import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from

interface FXInsightRepository {
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String, userName: String): Result<Unit>

     suspend fun isSession(): Boolean


}





class DefaultFXInsightRepositiory(private val supabase: SupabaseClient): FXInsightRepository
{
    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String, userName: String): Result<Unit>
    {

        return try{
            // check user exists
            val exits = checkUsernameExist(userName)
            exits.fold(
                onSuccess ={ Log.d("SignUp", "Username not found")},
                onFailure = {error -> return Result.failure(error)}
            )
             // sign up
            supabase.auth.signUpWith(Email)
            {
                this.email = email
                this.password = password
            }

            // add username
            val addUserName = addUserName(userName)

            if (addUserName.isFailure)
            {
                val error = addUserName.exceptionOrNull()
                return Result.failure(error?: Exception("Unkown Error"))
            }
            Result.success(Unit)
        }catch(e: Exception)
        {
            Result.failure(e)
        }
    }
            // helper for sign up
    suspend fun checkUsernameExist(userName: String): Result<Unit> {
        return try{
            val exists = supabase.from("profile")
                .select{
                    filter{
                        eq("username", userName)
                    }
                }
                .decodeList<UserProfileDto>()
                .isNotEmpty()

            if (exists)
            {
               return  Result.failure(Exception("The username is already taken"))
            }
            Result.success(Unit)
        }catch(e: Exception)
        {
            Result.failure(e)
        }

    }
        // helper for sign up
    suspend fun addUserName(userName: String): Result<Unit>
    {
        val userId =  supabase.auth.currentUserOrNull()?.id
            ?: return Result.failure(Exception("The User is not logged in "))
        return try{
            supabase.from("profile")
                .insert(
                    UserProfileDto(
                        user_id = userId,
                        user_name = userName
                    )
                )
            Result.success(Unit)
        }catch(e: Exception)
        {
            Result.failure(e)
        }
    }

     override suspend fun isSession(): Boolean {

        return supabase.auth.currentUserOrNull() != null
    }

    //helper for signn in
    suspend fun getUsername(): Result<String>
    {
        val userId = supabase.auth.currentUserOrNull() ?:
        return Result.failure(Exception("The User is not logged in "))

        return try {
            val userName = supabase.from("profile")
                .select{
                    filter{
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<UserProfileDto>()
                .user_name

            Result.success(userName)
        }catch(e: Exception)
        {
            Result.failure(e)
        }
    }

}