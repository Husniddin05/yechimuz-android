package uz.yechim.app.data.remote

import retrofit2.Response
import retrofit2.http.*
import uz.yechim.app.data.model.*

interface ApiService {

    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<UserDto>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<UserDto>>

    @GET("auth/me")
    suspend fun getMe(): Response<ApiResponse<Any>>

    // Problems
    @GET("problems")
    suspend fun getProblems(
        @Query("region") region: String? = null,
        @Query("search") search: String? = null,
        @Query("category") category: String? = null
    ): Response<ApiResponse<List<ProblemDto>>>

    @GET("problems/{id}")
    suspend fun getProblemById(@Path("id") id: String): Response<ApiResponse<ProblemDto>>

    // Categories
    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<List<CategoryDto>>>

    // Solvers Leaderboard
    @GET("solvers")
    suspend fun getSolvers(): Response<ApiResponse<List<SolverDto>>>

    // AI Problem Assistant
    @POST("ai/generate-challenge")
    suspend fun generateChallenge(@Body request: GenerateChallengeRequest): Response<ApiResponse<Any>>

    // Secure Chat
    @GET("chat/{solutionId}")
    suspend fun getChatRoom(@Path("solutionId") solutionId: String): Response<ApiResponse<ChatRoomDto>>

    @POST("chat/{solutionId}")
    suspend fun sendMessage(
        @Path("solutionId") solutionId: String,
        @Body request: SendMessageRequest
    ): Response<ApiResponse<ChatMessageDto>>

    @GET("chat/my")
    suspend fun getMyChats(): Response<ApiResponse<List<ChatRoomDto>>>
}
