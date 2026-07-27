package com.solux.luxup.taptap.feature.team.data

import kotlinx.serialization.Serializable

@Serializable
data class MemberProfileDto(
    val userId: Long,
    val displayName: String? = null,
    val profileImageUrl: String? = null,
)

@Serializable
data class TeamLatestRecordDto(
    val teamButtonId: Long,
    val buttonName: String? = null,
    val iconName: String? = null,
    val iconColor: String? = null,
    val recordedAt: String? = null,
)

@Serializable
data class MemberLatestRecordDto(
    val buttonName: String? = null,
    val recordedAt: String? = null,
)

@Serializable
data class CreateTeamRequestDto(
    val teamName: String? = null,
    val teamImageUrl: String? = null,
    val iconName: String? = null,
    val iconColor: String? = null,
    val maxMember: Int? = null,
)

@Serializable
data class TeamResponseDto(
    val teamId: Long,
    val teamName: String,
    val teamImageUrl: String? = null,
    val iconName: String? = null,
    val iconColor: String? = null,
    val inviteCode: String,
    val maxMember: Int,
    val ownerUserId: Long,
    val createdAt: String,
)

@Serializable
data class TeamListItemDto(
    val teamId: Long,
    val teamName: String,
    val teamImageUrl: String? = null,
    val iconName: String? = null,
    val iconColor: String? = null,
    val isFavorite: Boolean = false,
    val isOwner: Boolean = false,
    val maxMember: Int,
    val memberCount: Long,
    val memberProfiles: List<MemberProfileDto> = emptyList(),
    val latestRecord: TeamLatestRecordDto? = null,
    val recentUpdatedMembers: List<MemberProfileDto> = emptyList(),
    val isDeleting: Boolean = false,
    val scheduledDeletionAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class JoinTeamRequestDto(
    val inviteCode: String,
)

@Serializable
data class JoinTeamResponseDto(
    val teamId: Long,
    val teamName: String,
    val teamImageUrl: String? = null,
    val joinedAt: String,
)

@Serializable
data class TeamSettingsResponseDto(
    val teamId: Long,
    val teamName: String,
    val teamImageUrl: String? = null,
    val iconName: String? = null,
    val iconColor: String? = null,
    val inviteCode: String,
    val maxMember: Int,
    val memberCount: Long,
    val buttonCreatePermission: String,
    val buttonEditPermission: String,
    val buttonDeletePermission: String,
    val ownerUserId: Long,
    val notificationEnabled: Boolean,
    val isDeleting: Boolean = false,
    val scheduledDeletionAt: String? = null,
)

/** PATCH /api/teams/{team_id}/settings — 부분 업데이트. null 필드는 전송되지 않는다. */
@Serializable
data class UpdateTeamSettingsRequestDto(
    val teamName: String? = null,
    val teamImageUrl: String? = null,
    val iconName: String? = null,
    val iconColor: String? = null,
    val maxMember: Int? = null,
    val buttonCreatePermission: String? = null,
    val buttonEditPermission: String? = null,
    val buttonDeletePermission: String? = null,
    val newOwnerUserId: Long? = null,
)

@Serializable
data class UpdateTeamSettingsResponseDto(
    val teamId: Long,
    val teamName: String,
    val updatedAt: String,
)

@Serializable
data class TeamNotificationToggleResponseDto(
    val teamId: Long,
    val userId: Long,
    val notificationEnabled: Boolean,
)

@Serializable
data class FavoriteResponseDto(
    val teamId: Long,
    val isFavorite: Boolean,
)

@Serializable
data class TeamMemberListItemDto(
    val userId: Long,
    val displayName: String,
    val profileImageUrl: String? = null,
    val role: String,
    val joinedAt: String,
    val latestRecord: MemberLatestRecordDto? = null,
)

@Serializable
data class DeleteTeamResponseDto(
    val teamId: Long,
    val requestedAt: String,
    val scheduledDeletionAt: String? = null,
)

@Serializable
data class KickMemberResponseDto(
    val teamId: Long,
    val userId: Long,
    val removedAt: String,
)

@Serializable
data class LeaveTeamResponseDto(
    val teamId: Long,
    val userId: Long,
    val leftAt: String,
)

// ---- team-template-controller ----

@Serializable
data class TeamTemplateDto(
    val templateId: Long,
    val templateType: String,
    val templateName: String,
    val description: String? = null,
    val subDescription: String? = null,
)

@Serializable
data class TeamTemplateStatusResponseDto(
    val hasSelectedTemplate: Boolean = false,
    val isSkipped: Boolean = false,
    val templateId: Long? = null,
    val templateType: String? = null,
    val templateName: String? = null,
)

@Serializable
data class ApplyTeamTemplateRequestDto(
    val templateId: Long,
)

@Serializable
data class TeamButtonCategoryResponseDto(
    val categoryId: Long,
    val categoryName: String? = null,
    val categoryColor: String? = null,
    val displayOrder: Int? = null,
)

@Serializable
data class ApplyTeamTemplateResponseDto(
    val teamId: Long,
    val templateId: Long,
    val templateType: String? = null,
    val templateName: String? = null,
    val categories: List<TeamButtonCategoryResponseDto> = emptyList(),
)

@Serializable
data class SkipTeamTemplateResponseDto(
    val teamId: Long,
    val isSkipped: Boolean,
)

@Serializable
data class TemplateSuggestionDto(
    val buttonName: String,
    val iconName: String,
    val iconColor: String,
    val categoryId: Long? = null,
    val categoryName: String? = null,
)
