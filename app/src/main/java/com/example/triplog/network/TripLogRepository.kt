package com.example.triplog.network

import com.example.triplog.authorization.login.data.LoginRequest
import com.example.triplog.authorization.login.data.LoginResult
import com.example.triplog.authorization.login.data.LogoutResult
import com.example.triplog.authorization.registration.data.RegistrationRequest
import com.example.triplog.authorization.registration.data.RegistrationResult
import com.example.triplog.main.data.SearchProfilesResult
import com.example.triplog.main.data.TimelineResult
import com.example.triplog.main.data.UserID
import com.example.triplog.profile.data.profile.AuthenticatedUserProfileResult
import com.example.triplog.profile.data.profile.EditUserProfileResult
import com.example.triplog.profile.data.profile.FriendsOperationResult
import com.example.triplog.profile.data.profile.GetFriendsListResult
import com.example.triplog.profile.data.profile.GetFriendsRequestsResult
import com.example.triplog.profile.data.profile.TravelPreference
import com.example.triplog.profile.data.profile.TravelPreferencesResult
import com.example.triplog.profile.data.profile.UserProfileResult
import com.example.triplog.profile.data.updatePassword.UpdatePasswordRequest
import com.example.triplog.profile.data.updatePassword.UpdatePasswordResult
import com.example.triplog.travel.data.TravelCategoriesResult
import com.example.triplog.travel.data.TravelCategory
import com.example.triplog.travel.data.TravelRequest
import com.example.triplog.travel.data.TravelResult
import com.example.triplog.travel.data.UpdateImageResult
import com.example.triplog.travel.data.UserTravelsResult
import com.squareup.moshi.Moshi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface InterfaceRepository {
    suspend fun getLoginResult(request: LoginRequest): LoginResult?
    suspend fun getRegistrationResult(request: RegistrationRequest): RegistrationResult?
    suspend fun getAuthenticatedUserProfileResult(token: String?): AuthenticatedUserProfileResult?
    suspend fun updatePassword(
        token: String?,
        updatePasswordRequest: UpdatePasswordRequest
    ): UpdatePasswordResult?

    suspend fun getUserProfileResult(token: String?, id: Int): UserProfileResult?
    suspend fun getTravelPreferences(token: String?): TravelPreferencesResult?
    suspend fun editUserProfile(
        token: String?,
        id: Int,
        email: RequestBody,
        name: RequestBody,
        avatar: MultipartBody.Part?,
        facebookLink: RequestBody?,
        instagramLink: RequestBody?,
        xLink: RequestBody?,
        bio: RequestBody?,
        travelPreferences: Map<String, RequestBody>
    ): EditUserProfileResult?


    suspend fun getLogoutResult(token: String?): LogoutResult?
    suspend fun getFriendsList(token: String?): GetFriendsListResult?
    suspend fun getFriendsRequests(token: String?): GetFriendsRequestsResult?
    suspend fun sendFriendRequest(token: String?, userID: UserID): FriendsOperationResult?
    suspend fun acceptFriendRequest(token: String?, requestId: Int): FriendsOperationResult?
    suspend fun rejectFriendRequest(token: String?, requestId: Int): FriendsOperationResult?
    suspend fun deleteFriend(token: String?, friendId: Int): FriendsOperationResult?
    suspend fun getSearchProfilesResult(
        token: String?,
        query: String,
        page: Int?
    ): SearchProfilesResult?

    suspend fun getTravelCategories(token: String?): TravelCategoriesResult?
    suspend fun getTravel(token: String?, travelId: Int): TravelResult?
    suspend fun createTravel(token: String?, travelRequest: TravelRequest): TravelResult?
    suspend fun updateTravel(
        token: String?,
        travelId: Int,
        travelRequest: TravelRequest
    ): TravelResult?

    suspend fun deleteTravel(token: String?, travelId: Int): TravelResult?
    suspend fun toggleFavouriteTravel(token: String?, travelId: Int): TravelResult?
    suspend fun getUserFinishedTravels(token: String?, userId: Int, page: Int?): UserTravelsResult?
    suspend fun getUserFavouriteTravels(token: String?, userId: Int, page: Int?): UserTravelsResult?
    suspend fun getUserPlannedTravels(token: String?, userId: Int, page: Int?): UserTravelsResult?
    suspend fun getTimeline(
        token: String?, page: Int?, dateFrom: String?,
        dateTo: String?,
        sortDirection: String?
    ): TimelineResult?

    suspend fun updateImage(
        token: String?, imageableType: RequestBody?,
        imageableId: RequestBody?,
        image: MultipartBody.Part?,
    ): UpdateImageResult?
}

class Repository(private val tripLogApiService: TripLogApiService) : InterfaceRepository {
    override suspend fun getLoginResult(request: LoginRequest): LoginResult? {
        val response: Response<LoginResult> = tripLogApiService.getLoginResult(request)
        val loginResult: LoginResult?

        if (response.isSuccessful) {
            loginResult = response.body()
            loginResult?.resultCode = response.code()
            return loginResult
        } else {
            val errorBody = response.errorBody()?.string()
            loginResult = Moshi.Builder().build().adapter(LoginResult::class.java)
                .fromJson(errorBody!!)
            loginResult?.resultCode = response.code()
            return loginResult
        }
    }

    override suspend fun getRegistrationResult(request: RegistrationRequest): RegistrationResult? {
        val response: Response<RegistrationResult> =
            tripLogApiService.getRegistrationResult(request)
        val registrationResult: RegistrationResult?

        if (response.isSuccessful) {
            registrationResult = response.body()
            registrationResult?.resultCode = response.code()
            return registrationResult
        } else {
            val errorBody = response.errorBody()?.string()
            registrationResult = Moshi.Builder().build().adapter(RegistrationResult::class.java)
                .fromJson(errorBody!!)
            registrationResult?.resultCode = response.code()
            return registrationResult
        }
    }

    override suspend fun getAuthenticatedUserProfileResult(token: String?): AuthenticatedUserProfileResult? {
        val response: Response<AuthenticatedUserProfileResult> =
            tripLogApiService.getAuthenticatedUserProfileResult("Bearer $token")

        val authenticatedUserProfileResult: AuthenticatedUserProfileResult?
        if (response.isSuccessful) {
            authenticatedUserProfileResult = response.body()
            authenticatedUserProfileResult?.resultCode = response.code()
            return authenticatedUserProfileResult
        } else {
            val errorBody = response.errorBody()?.string()
            authenticatedUserProfileResult =
                Moshi.Builder().build().adapter(AuthenticatedUserProfileResult::class.java)
                    .fromJson(errorBody!!)
            authenticatedUserProfileResult?.resultCode = response.code()
            return authenticatedUserProfileResult
        }
    }

    override suspend fun updatePassword(
        token: String?,
        updatePasswordRequest: UpdatePasswordRequest
    ): UpdatePasswordResult? {
        val response: Response<UpdatePasswordResult> =
            tripLogApiService.updatePassword("Bearer $token", updatePasswordRequest)

        val updatePasswordResult: UpdatePasswordResult?

        if (response.isSuccessful) {
            updatePasswordResult = response.body()
            updatePasswordResult?.resultCode = response.code()
            return updatePasswordResult
        } else {
            val errorBody = response.errorBody()?.string()
            updatePasswordResult = Moshi.Builder().build().adapter(UpdatePasswordResult::class.java)
                .fromJson(errorBody!!)
            updatePasswordResult?.resultCode = response.code()
            return updatePasswordResult
        }
    }

    override suspend fun getUserProfileResult(token: String?, id: Int): UserProfileResult? {
        val response: Response<UserProfileResult> =
            tripLogApiService.getUserProfileResult(
                token = "Bearer $token",
                id = id
            )
        val userProfileResult: UserProfileResult?
        if (response.isSuccessful) {
            userProfileResult = response.body()
            userProfileResult?.resultCode = response.code()
            return userProfileResult
        } else {
            val errorBody = response.errorBody()?.string()
            userProfileResult = Moshi.Builder().build().adapter(UserProfileResult::class.java)
                .fromJson(errorBody!!)
            userProfileResult?.resultCode = response.code()
            return userProfileResult
        }
    }


    override suspend fun getTravelPreferences(token: String?): TravelPreferencesResult {
        val response: Response<List<TravelPreference>> =
            tripLogApiService.getTravelPreferences("Bearer $token")

        val travelPreferencesResult = TravelPreferencesResult(null, null, null)

        if (response.isSuccessful) {
            travelPreferencesResult.travelPreference = response.body()
            travelPreferencesResult.resultCode = response.code()
            return travelPreferencesResult
        } else {
            val errorBody = response.errorBody()?.string()
            travelPreferencesResult.message = errorBody ?: "Error"
            travelPreferencesResult.resultCode = response.code()
            return travelPreferencesResult
        }
    }


    override suspend fun editUserProfile(
        token: String?,
        id: Int,
        email: RequestBody,
        name: RequestBody,
        avatar: MultipartBody.Part?,
        facebookLink: RequestBody?,
        instagramLink: RequestBody?,
        xLink: RequestBody?,
        bio: RequestBody?,
        travelPreferences: Map<String, RequestBody>
    ): EditUserProfileResult? {
        val response: Response<EditUserProfileResult> = tripLogApiService.updateUserProfile(
            token = "Bearer $token",
            id = id,
            email = email,
            name = name,
            avatar = avatar,
            facebookLink = facebookLink,
            instagramLink = instagramLink,
            xLink = xLink,
            bio = bio,
            travelPreferences = travelPreferences
        )

        return if (response.isSuccessful) {
            response.body()?.apply {
                resultCode = response.code()
            }
        } else {
            val errorBody = response.errorBody()?.string()
            Moshi.Builder().build()
                .adapter(EditUserProfileResult::class.java)
                .fromJson(errorBody.orEmpty())?.apply {
                    resultCode = response.code()
                }
        }
    }

    override suspend fun getLogoutResult(
        token: String?,
    ): LogoutResult? {
        val response: Response<LogoutResult> =
            tripLogApiService.getLogoutResult("Bearer $token")
        val logoutResult: LogoutResult?

        if (response.isSuccessful) {
            logoutResult = response.body()
            logoutResult?.resultCode = response.code()
            return logoutResult
        } else {
            val errorBody = response.errorBody()?.string()
            logoutResult =
                Moshi.Builder().build().adapter(LogoutResult::class.java)
                    .fromJson(errorBody!!)
            logoutResult?.resultCode = response.code()
            logoutResult?.message = logoutResult?.message
            return logoutResult
        }
    }

    override suspend fun getFriendsList(token: String?): GetFriendsListResult? {
        val response: Response<GetFriendsListResult> =
            tripLogApiService.getFriendsList("Bearer $token")

        val friendsListResult: GetFriendsListResult?
        if (response.isSuccessful) {
            friendsListResult = response.body()
            friendsListResult?.resultCode = response.code()
            friendsListResult?.message = response.message()
            return friendsListResult
        } else {
            val errorBody = response.errorBody()?.string()
            friendsListResult =
                Moshi.Builder().build().adapter(GetFriendsListResult::class.java)
                    .fromJson(errorBody!!)
            friendsListResult?.resultCode = response.code()
            friendsListResult?.message = response.message()
            return friendsListResult
        }
    }

    override suspend fun getFriendsRequests(token: String?): GetFriendsRequestsResult? {
        val response: Response<GetFriendsRequestsResult> =
            tripLogApiService.getFriendsRequests("Bearer $token")

        val friendsRequestsResult: GetFriendsRequestsResult?
        if (response.isSuccessful) {
            friendsRequestsResult = response.body()
            friendsRequestsResult?.resultCode = response.code()
            friendsRequestsResult?.message = response.message()
            return friendsRequestsResult
        } else {
            val errorBody = response.errorBody()?.string()
            friendsRequestsResult =
                Moshi.Builder().build().adapter(GetFriendsRequestsResult::class.java)
                    .fromJson(errorBody!!)
            friendsRequestsResult?.resultCode = response.code()
            friendsRequestsResult?.message = response.message()
            return friendsRequestsResult
        }
    }

    override suspend fun sendFriendRequest(
        token: String?,
        userID: UserID
    ): FriendsOperationResult? {
        val response: Response<FriendsOperationResult> =
            tripLogApiService.sendFriendRequest("Bearer $token", userID)

        val friendsOperationResult: FriendsOperationResult?
        if (response.isSuccessful) {
            friendsOperationResult = response.body()
            friendsOperationResult?.resultCode = response.code()
            return friendsOperationResult
        } else {
            val errorBody = response.errorBody()?.string()
            friendsOperationResult =
                Moshi.Builder().build().adapter(FriendsOperationResult::class.java)
                    .fromJson(errorBody!!)
            friendsOperationResult?.resultCode = response.code()
            return friendsOperationResult
        }
    }

    override suspend fun acceptFriendRequest(
        token: String?,
        requestId: Int
    ): FriendsOperationResult? {
        val response: Response<FriendsOperationResult> =
            tripLogApiService.acceptFriendRequest("Bearer $token", requestId)

        val friendsOperationResult: FriendsOperationResult?
        if (response.isSuccessful) {
            friendsOperationResult = response.body()
            friendsOperationResult?.resultCode = response.code()
            return friendsOperationResult
        } else {
            val errorBody = response.errorBody()?.string()
            friendsOperationResult =
                Moshi.Builder().build().adapter(FriendsOperationResult::class.java)
                    .fromJson(errorBody!!)
            friendsOperationResult?.resultCode = response.code()
            return friendsOperationResult
        }
    }

    override suspend fun rejectFriendRequest(
        token: String?,
        requestId: Int
    ): FriendsOperationResult? {
        val response: Response<FriendsOperationResult> =
            tripLogApiService.rejectFriendRequest("Bearer $token", requestId)

        val friendsOperationResult: FriendsOperationResult?
        if (response.isSuccessful) {
            friendsOperationResult = response.body()
            friendsOperationResult?.resultCode = response.code()
            return friendsOperationResult
        } else {
            val errorBody = response.errorBody()?.string()
            friendsOperationResult =
                Moshi.Builder().build().adapter(FriendsOperationResult::class.java)
                    .fromJson(errorBody!!)
            friendsOperationResult?.resultCode = response.code()
            return friendsOperationResult
        }
    }

    override suspend fun getSearchProfilesResult(
        token: String?,
        query: String,
        page: Int?
    ): SearchProfilesResult? {
        val response: Response<SearchProfilesResult> =
            tripLogApiService.getSearchProfilesResult("Bearer $token", query, page)

        val searchProfilesResult: SearchProfilesResult?
        if (response.isSuccessful) {
            searchProfilesResult = response.body()
            searchProfilesResult?.resultCode = response.code()
            return searchProfilesResult
        } else {
            val errorBody = response.errorBody()?.string()
            searchProfilesResult =
                Moshi.Builder().build().adapter(SearchProfilesResult::class.java)
                    .fromJson(errorBody!!)
            searchProfilesResult?.resultCode = response.code()
            return searchProfilesResult
        }
    }

    override suspend fun getTravelCategories(token: String?): TravelCategoriesResult {
        val response: Response<List<TravelCategory>> =
            tripLogApiService.getTravelCategories("Bearer $token")

        val travelCategoriesResult = TravelCategoriesResult(null, null, null)

        if (response.isSuccessful) {
            travelCategoriesResult.travelCategories = response.body()
            travelCategoriesResult.resultCode = response.code()
            return travelCategoriesResult
        } else {
            val errorBody = response.errorBody()?.string()
            travelCategoriesResult.message = errorBody ?: "Error"
            travelCategoriesResult.resultCode = response.code()
            return travelCategoriesResult
        }
    }

    override suspend fun deleteFriend(
        token: String?,
        friendId: Int
    ): FriendsOperationResult? {
        val response: Response<FriendsOperationResult> =
            tripLogApiService.deleteFriendRequest("Bearer $token", friendId)

        val friendsOperationResult: FriendsOperationResult?
        if (response.isSuccessful) {
            friendsOperationResult = response.body()
            friendsOperationResult?.resultCode = response.code()
            return friendsOperationResult
        } else {
            val errorBody = response.errorBody()?.string()
            friendsOperationResult =
                Moshi.Builder().build().adapter(FriendsOperationResult::class.java)
                    .fromJson(errorBody!!)
            friendsOperationResult?.resultCode = response.code()
            return friendsOperationResult
        }
    }

    override suspend fun getTravel(
        token: String?,
        travelId: Int
    ): TravelResult? {
        val response: Response<TravelResult> =
            tripLogApiService.getTravel("Bearer $token", travelId)

        val travelResult: TravelResult?
        if (response.isSuccessful) {
            travelResult = response.body()
            travelResult?.resultCode = response.code()
            return travelResult
        } else {
            val errorBody = response.errorBody()?.string()
            travelResult =
                Moshi.Builder().build().adapter(TravelResult::class.java)
                    .fromJson(errorBody!!)
            travelResult?.resultCode = response.code()
            return travelResult
        }
    }

    override suspend fun createTravel(
        token: String?,
        travelRequest: TravelRequest
    ): TravelResult? {
        val response: Response<TravelResult> =
            tripLogApiService.createTravel("Bearer $token", travelRequest)

        val travelResult: TravelResult?
        if (response.isSuccessful) {
            travelResult = response.body()
            travelResult?.resultCode = response.code()
            return travelResult
        } else {
            val errorBody = response.errorBody()?.string()
            travelResult =
                Moshi.Builder().build().adapter(TravelResult::class.java)
                    .fromJson(errorBody!!)
            travelResult?.resultCode = response.code()
            return travelResult
        }
    }

    override suspend fun updateTravel(
        token: String?,
        travelId: Int,
        travelRequest: TravelRequest
    ): TravelResult? {
        val response: Response<TravelResult> =
            tripLogApiService.updateTravel("Bearer $token", travelId, travelRequest)

        val travelResult: TravelResult?
        if (response.isSuccessful) {
            travelResult = response.body()
            travelResult?.resultCode = response.code()
            return travelResult
        } else {
            val errorBody = response.errorBody()?.string()
            travelResult =
                Moshi.Builder().build().adapter(TravelResult::class.java)
                    .fromJson(errorBody!!)
            travelResult?.resultCode = response.code()
            return travelResult
        }
    }

    override suspend fun deleteTravel(
        token: String?,
        travelId: Int
    ): TravelResult? {
        val response: Response<TravelResult> =
            tripLogApiService.deleteTravel("Bearer $token", travelId)

        val travelResult: TravelResult?
        if (response.isSuccessful) {
            travelResult = response.body()
            travelResult?.resultCode = response.code()
            return travelResult
        } else {
            val errorBody = response.errorBody()?.string()
            travelResult =
                Moshi.Builder().build().adapter(TravelResult::class.java)
                    .fromJson(errorBody!!)
            travelResult?.resultCode = response.code()
            return travelResult
        }
    }

    override suspend fun toggleFavouriteTravel(
        token: String?,
        travelId: Int
    ): TravelResult? {
        val response: Response<TravelResult> =
            tripLogApiService.toggleFavouriteTravel("Bearer $token", travelId)

        val travelResult: TravelResult?
        if (response.isSuccessful) {
            travelResult = response.body()
            travelResult?.resultCode = response.code()
            return travelResult
        } else {
            val errorBody = response.errorBody()?.string()
            travelResult =
                Moshi.Builder().build().adapter(TravelResult::class.java)
                    .fromJson(errorBody!!)
            travelResult?.resultCode = response.code()
            return travelResult
        }
    }

    override suspend fun getUserFinishedTravels(
        token: String?,
        userId: Int,
        page: Int?
    ): UserTravelsResult? {
        val response: Response<UserTravelsResult> =
            tripLogApiService.getUserFinishedTravels("Bearer $token", userId, page)

        val travelsResult: UserTravelsResult?
        if (response.isSuccessful) {
            travelsResult = response.body()
            travelsResult?.resultCode = response.code()
            return travelsResult
        } else {
            val errorBody = response.errorBody()?.string()
            travelsResult =
                Moshi.Builder().build().adapter(UserTravelsResult::class.java)
                    .fromJson(errorBody!!)
            travelsResult?.resultCode = response.code()
            return travelsResult
        }
    }

    override suspend fun getUserFavouriteTravels(
        token: String?,
        userId: Int,
        page: Int?
    ): UserTravelsResult? {
        val response: Response<UserTravelsResult> =
            tripLogApiService.getUserFavouriteTravels("Bearer $token", userId, page)

        val travelsResult: UserTravelsResult?
        if (response.isSuccessful) {
            travelsResult = response.body()
            travelsResult?.resultCode = response.code()
            return travelsResult
        } else {
            val errorBody = response.errorBody()?.string()
            travelsResult =
                Moshi.Builder().build().adapter(UserTravelsResult::class.java)
                    .fromJson(errorBody!!)
            travelsResult?.resultCode = response.code()
            return travelsResult
        }
    }

    override suspend fun getUserPlannedTravels(
        token: String?,
        userId: Int,
        page: Int?
    ): UserTravelsResult? {
        val response: Response<UserTravelsResult> =
            tripLogApiService.getUserPlannedTravels("Bearer $token", userId, page)

        val travelsResult: UserTravelsResult?
        if (response.isSuccessful) {
            travelsResult = response.body()
            travelsResult?.resultCode = response.code()
            return travelsResult
        } else {
            val errorBody = response.errorBody()?.string()
            travelsResult =
                Moshi.Builder().build().adapter(UserTravelsResult::class.java)
                    .fromJson(errorBody!!)
            travelsResult?.resultCode = response.code()
            return travelsResult
        }
    }

    override suspend fun getTimeline(
        token: String?,
        page: Int?,
        dateFrom: String?,
        dateTo: String?,
        sortDirection: String?
    ): TimelineResult? {
        val response: Response<TimelineResult> =
            tripLogApiService.getTimeline("Bearer $token", page, dateFrom, dateTo, sortDirection)

        val timelineResult: TimelineResult?
        if (response.isSuccessful) {
            timelineResult = response.body()
            timelineResult?.resultCode = response.code()
            return timelineResult
        } else {
            val errorBody = response.errorBody()?.string()
            timelineResult =
                Moshi.Builder().build().adapter(TimelineResult::class.java)
                    .fromJson(errorBody!!)
            timelineResult?.resultCode = response.code()
            return timelineResult
        }
    }

    override suspend fun updateImage(
        token: String?,
        imageableType: RequestBody?,
        imageableId: RequestBody?,
        image: MultipartBody.Part?
    ): UpdateImageResult? {
        val response: Response<UpdateImageResult> =
            tripLogApiService.updateImage("Bearer $token", imageableType, imageableId, image)

        val updateImageResult: UpdateImageResult?
        if (response.isSuccessful) {
            updateImageResult = response.body()
            updateImageResult?.resultCode = response.code()
            return updateImageResult
        } else {
            val errorBody = response.errorBody()?.string()
            updateImageResult =
                Moshi.Builder().build().adapter(UpdateImageResult::class.java)
                    .fromJson(errorBody!!)
            updateImageResult?.resultCode = response.code()
            return updateImageResult
        }
    }
}