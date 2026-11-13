package uz.yechim.app.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("error") val error: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("token") val token: String?
)

data class ProblemDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("rawDescription") val rawDescription: String?,
    @SerializedName("region") val region: String?,
    @SerializedName("organization") val organization: String?,
    @SerializedName("problemType") val problemType: String?,
    @SerializedName("severityLevel") val severityLevel: String?,
    @SerializedName("budget") val budget: Double?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("category") val category: CategoryDto?,
    @SerializedName("owner") val owner: OwnerDto?,
    @SerializedName("challenge") val challenge: ChallengeDto?
)

data class CategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("nameUz") val nameUz: String,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("slug") val slug: String?,
    @SerializedName("icon") val icon: String?
)

data class ChallengeDto(
    @SerializedName("id") val id: String,
    @SerializedName("problemId") val problemId: String,
    @SerializedName("structuredTitle") val structuredTitle: String?,
    @SerializedName("formattedObjective") val formattedObjective: String?,
    @SerializedName("suggestedSkills") val suggestedSkills: Any?,
    @SerializedName("targetKPIs") val targetKPIs: Any?,
    @SerializedName("challengeFormat") val challengeFormat: String?
)

data class OwnerDto(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String?,
    @SerializedName("phone") val phone: String?
)

data class SolverDto(
    @SerializedName("id") val id: String,
    @SerializedName("rank") val rank: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("region") val region: String?,
    @SerializedName("ratingAvg") val ratingAvg: Double?,
    @SerializedName("solvedCount") val solvedCount: Int?
)

data class ChatRoomDto(
    @SerializedName("id") val id: String,
    @SerializedName("solutionId") val solutionId: String,
    @SerializedName("problemId") val problemId: String,
    @SerializedName("ownerId") val ownerId: String,
    @SerializedName("solverId") val solverId: String,
    @SerializedName("status") val status: String,
    @SerializedName("messages") val messages: List<ChatMessageDto>?
)

data class ChatMessageDto(
    @SerializedName("id") val id: String,
    @SerializedName("chatRoomId") val chatRoomId: String,
    @SerializedName("senderId") val senderId: String,
    @SerializedName("content") val content: String,
    @SerializedName("isRead") val isRead: Boolean,
    @SerializedName("createdAt") val createdAt: String
)

data class SendMessageRequest(
    @SerializedName("content") val content: String
)

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("profile") val profile: UserProfileDto?,
    @SerializedName("roles") val roles: List<String>?
)

data class UserProfileDto(
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("region") val region: String?,
    @SerializedName("organizationName") val organizationName: String?
)

data class LoginRequest(
    @SerializedName("emailOrUsername") val emailOrUsername: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("password") val password: String,
    @SerializedName("roles") val roles: List<String> = listOf("USER"),
    @SerializedName("region") val region: String? = "Toshkent"
)

data class GenerateChallengeRequest(
    @SerializedName("title") val title: String,
    @SerializedName("rawDescription") val rawDescription: String,
    @SerializedName("categoryId") val categoryId: String?,
    @SerializedName("region") val region: String?
)
