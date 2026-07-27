package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.core.network.ApiCallHandler
import com.solux.luxup.taptap.feature.team.model.LatestRecord
import com.solux.luxup.taptap.feature.team.model.MemberLatestRecord
import com.solux.luxup.taptap.feature.team.model.MemberProfile
import com.solux.luxup.taptap.feature.team.model.Team
import com.solux.luxup.taptap.feature.team.model.TeamButtonPermission
import com.solux.luxup.taptap.feature.team.model.TeamCreateForm
import com.solux.luxup.taptap.feature.team.model.TeamCreateResult
import com.solux.luxup.taptap.feature.team.model.TeamButtonSuggestion
import com.solux.luxup.taptap.feature.team.model.TeamMember
import com.solux.luxup.taptap.feature.team.model.TeamMemberRole
import com.solux.luxup.taptap.feature.team.model.TeamSettings
import com.solux.luxup.taptap.feature.team.model.TeamTemplate
import com.solux.luxup.taptap.feature.team.model.TeamTemplateStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamRepository @Inject constructor(
    private val teamApi: TeamApi,
    private val apiCallHandler: ApiCallHandler,
) {
    suspend fun listTeams(): Result<List<Team>> =
        apiCallHandler.execute { teamApi.listTeams() }.mapCatching { list -> list.map { it.toModel() } }

    suspend fun createTeam(form: TeamCreateForm): Result<TeamCreateResult> =
        apiCallHandler.execute {
            teamApi.createTeam(
                CreateTeamRequestDto(
                    teamName = form.teamName.ifBlank { null },
                    teamImageUrl = form.teamImageUrl,
                    iconName = form.iconName,
                    iconColor = form.iconColor,
                    maxMember = form.maxMember,
                )
            )
        }.mapCatching { it.toResult() }

    suspend fun joinTeam(inviteCode: String): Result<JoinTeamResponseDto> =
        apiCallHandler.execute { teamApi.joinTeam(JoinTeamRequestDto(inviteCode)) }

    suspend fun getSettings(teamId: Long): Result<TeamSettings> =
        apiCallHandler.execute { teamApi.getSettings(teamId) }.mapCatching { it.toModel() }

    suspend fun updateSettings(teamId: Long, request: UpdateTeamSettingsRequestDto): Result<UpdateTeamSettingsResponseDto> =
        apiCallHandler.execute { teamApi.updateSettings(teamId, request) }

    /** 응답의 최종 notificationEnabled 값을 반환한다 (토글이라 요청 바디 없음) */
    suspend fun toggleNotification(teamId: Long): Result<Boolean> =
        apiCallHandler.execute { teamApi.toggleNotification(teamId) }.mapCatching { it.notificationEnabled }

    suspend fun toggleFavorite(teamId: Long): Result<Boolean> =
        apiCallHandler.execute { teamApi.toggleFavorite(teamId) }.mapCatching { it.isFavorite }

    suspend fun listMembers(teamId: Long): Result<List<TeamMember>> =
        apiCallHandler.execute { teamApi.listMembers(teamId) }.mapCatching { list -> list.map { it.toModel() } }

    suspend fun deleteTeam(teamId: Long): Result<DeleteTeamResponseDto> =
        apiCallHandler.execute { teamApi.deleteTeam(teamId) }

    suspend fun kickMember(teamId: Long, userId: Long): Result<KickMemberResponseDto> =
        apiCallHandler.execute { teamApi.kickMember(teamId, userId) }

    suspend fun leaveTeam(teamId: Long): Result<LeaveTeamResponseDto> =
        apiCallHandler.execute { teamApi.leaveTeam(teamId) }

    // ---- team-template-controller ----

    suspend fun listTeamTemplates(): Result<List<TeamTemplate>> =
        apiCallHandler.execute { teamApi.listTeamTemplates() }.mapCatching { list -> list.map { it.toModel() } }

    /** 활동 탭 초기 분기 · "+" 빠르게 생성 노출 여부 판단용 (아직 소비하는 화면 없음) */
    suspend fun getTemplateStatus(teamId: Long): Result<TeamTemplateStatus> =
        apiCallHandler.execute { teamApi.getTemplateStatus(teamId) }.mapCatching { it.toModel() }

    suspend fun selectTemplate(teamId: Long, templateId: Long): Result<ApplyTeamTemplateResponseDto> =
        apiCallHandler.execute { teamApi.selectTemplate(teamId, ApplyTeamTemplateRequestDto(templateId)) }

    suspend fun skipTemplate(teamId: Long): Result<SkipTeamTemplateResponseDto> =
        apiCallHandler.execute { teamApi.skipTemplate(teamId) }

    suspend fun getTemplateSuggestions(teamId: Long): Result<List<TeamButtonSuggestion>> =
        apiCallHandler.execute { teamApi.getTemplateSuggestions(teamId) }.mapCatching { list -> list.map { it.toModel() } }
}

private fun MemberProfileDto.toModel() = MemberProfile(
    userId = userId,
    displayName = displayName.orEmpty(),
    profileImageUrl = profileImageUrl,
)

private fun TeamLatestRecordDto.toModel() = LatestRecord(
    teamButtonId = teamButtonId,
    buttonName = buttonName.orEmpty(),
    iconName = iconName.orEmpty(),
    iconColor = iconColor.orEmpty(),
    recordedAt = recordedAt.orEmpty(),
)

private fun TeamListItemDto.toModel() = Team(
    teamId = teamId,
    teamName = teamName,
    teamImageUrl = teamImageUrl,
    iconName = iconName,
    iconColor = iconColor,
    isOwner = isOwner,
    isFavorite = isFavorite,
    maxMember = maxMember,
    memberCount = memberCount.toInt(),
    memberProfiles = memberProfiles.map { it.toModel() },
    latestRecord = latestRecord?.toModel(),
    recentUpdatedMembers = recentUpdatedMembers.map { it.toModel() },
    updatedAt = updatedAt,
)

private fun TeamResponseDto.toResult() = TeamCreateResult(
    teamId = teamId,
    teamName = teamName,
    teamImageUrl = teamImageUrl,
    iconName = iconName,
    iconColor = iconColor,
    inviteCode = inviteCode,
    maxMember = maxMember,
    ownerUserId = ownerUserId,
    createdAt = createdAt,
)

private fun TeamSettingsResponseDto.toModel() = TeamSettings(
    teamId = teamId,
    teamName = teamName,
    teamImageUrl = teamImageUrl,
    iconName = iconName,
    iconColor = iconColor,
    inviteCode = inviteCode,
    maxMember = maxMember,
    memberCount = memberCount.toInt(),
    buttonCreatePermission = TeamButtonPermission.from(buttonCreatePermission),
    buttonEditPermission = TeamButtonPermission.from(buttonEditPermission),
    buttonDeletePermission = TeamButtonPermission.from(buttonDeletePermission),
    ownerUserId = ownerUserId,
    notificationEnabled = notificationEnabled,
    isDeleting = isDeleting,
    scheduledDeletionAt = scheduledDeletionAt,
)

private fun TeamMemberListItemDto.toModel() = TeamMember(
    userId = userId,
    displayName = displayName,
    profileImageUrl = profileImageUrl,
    role = TeamMemberRole.from(role),
    joinedAt = joinedAt,
    latestRecord = latestRecord?.let { MemberLatestRecord(it.buttonName.orEmpty(), it.recordedAt.orEmpty()) },
)

private fun TeamTemplateDto.toModel() = TeamTemplate(
    templateId = templateId,
    templateType = templateType,
    templateName = templateName,
    description = description.orEmpty(),
    subDescription = subDescription.orEmpty(),
)

private fun TeamTemplateStatusResponseDto.toModel() = TeamTemplateStatus(
    hasSelectedTemplate = hasSelectedTemplate,
    isSkipped = isSkipped,
    templateId = templateId,
    templateType = templateType,
    templateName = templateName,
)

private fun TemplateSuggestionDto.toModel() = TeamButtonSuggestion(
    buttonName = buttonName,
    iconName = iconName,
    iconColor = iconColor,
    categoryId = categoryId,
    categoryName = categoryName,
)
