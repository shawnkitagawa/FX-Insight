package com.example.fxinsight.data.repositiory

import android.util.Log
import com.example.fxinsight.data.network.dto.DeleteAccountResult
import com.example.fxinsight.data.network.dto.profile.request.ProfileCreate
import com.example.fxinsight.data.network.dto.profile.response.DeleteProfileResponse
import com.example.fxinsight.data.network.dto.profile.response.ProfileResponse
import com.example.fxinsight.data.repository.AuthRepository

interface AcountRepository {


    suspend fun createAccount(email: String, password: String, userName: String): Result<ProfileResponse>

    suspend fun deleteAccount(): Result<DeleteAccountResult>

    suspend fun login(email: String, password: String): Result<Unit>

    suspend fun logout(): Result<Unit>

    suspend fun isSession(): Boolean
}

class DefaultAccountRepository(private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
): AcountRepository {

    override suspend fun createAccount(email: String, password: String, userName: String): Result<ProfileResponse>
    {
        val authResult = authRepository.signUp(email, password)
        return authResult.fold(
            onSuccess = {message ->
                Log.d("createAccount", "${message}")
                val profileRequest = ProfileCreate(userName)
                val profileResult = profileRepository.createProfile(profileRequest)

                profileResult.fold(
                    onSuccess = { response ->
                        Log.d("createAccount", "Created ${response}")
                        Result.success(response)
                    },
                    onFailure = { error ->
                        Log.d("createAccount", "Failed to create profile")
                        authRepository.signOut()
                        Result.failure(error)
                    },
                )
            },
            onFailure = { error ->
                Result.failure(error)
            },
        )

    }

    // sign up succesful but create profile not succesful then show screen
    // finishing account setup or we coudn't complete your profile set up

    override suspend fun deleteAccount(): Result<DeleteAccountResult> {
        val profileResult = profileRepository.deleteProfile()

        profileResult.fold(
            onSuccess = { response ->
                Log.d("deleteAccount", "Deleted ${response}")
                val authResult = authRepository.signOut()

                authResult.fold(
                    onSuccess = {
                        Log.d("deleteAccount", "Signed out")
                        return Result.success(DeleteAccountResult(
                            response = response,
                            shouldForceLogout = false
                        ))
                    },
                    onFailure = { error ->

                        Log.d("deleteAccount", "delete account succesfully but" +
                                "failed to signout")

                        return Result.success(DeleteAccountResult(
                            response = response,
                            shouldForceLogout = true
                        ))
                    },
                )
            },
            onFailure = { error ->
                Log.d("deleteAccount", "Failed to delete profile")
                return Result.failure(error)
            },

        )
    }

    // Even it fails to sign out just



    override suspend fun login(email: String, password: String): Result<Unit> {
        val authResult = authRepository.signIn(email, password)
        authResult.fold(
            onSuccess = {
                val profileResult = profileRepository.fetchProfile()

                profileResult.fold(
                    onSuccess = {

                        Log.d("login", "Logged in")
                        return Result.success(Unit)},
                    onFailure = {error ->
                        Log.d("login", "Failed to login profile does not exist")
                        return Result.failure(error)},
                )

            },
            onFailure = {error ->
                Log.d("login", "Failed to login")
                return Result.failure(error)
            },
        )
    }

    override suspend fun logout(): Result<Unit> {
        val authResult = authRepository.signOut()
        authResult.fold(
            onSuccess = {
                Log.d("logout", "Logged out")
                return Result.success(Unit)
            },
            onFailure = {error ->
                Log.d("logout", "Failed to logout")
                return Result.failure(error)
            },
        )
    }

    override suspend fun isSession(): Boolean {
        return authRepository.isSession()
    }

}
