package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.core.network.ApiCallHandler
import com.solux.luxup.taptap.core.network.ApiException
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.feature.team.model.ButtonRecord
import com.solux.luxup.taptap.feature.team.model.LatestRecord
import com.solux.luxup.taptap.feature.team.model.MemberLatestRecord
import com.solux.luxup.taptap.feature.team.model.MemberProfile
import com.solux.luxup.taptap.feature.team.model.MyButtonPermission
import com.solux.luxup.taptap.feature.team.model.Team
import com.solux.luxup.taptap.feature.team.model.TeamButton
import com.solux.luxup.taptap.feature.team.model.TeamButtonCategory
import com.solux.luxup.taptap.feature.team.model.TeamButtonDetail
import com.solux.luxup.taptap.feature.team.model.TeamButtonForm
import com.solux.luxup.taptap.feature.team.model.TeamButtonLatest
import com.solux.luxup.taptap.feature.team.model.TeamButtonPermission
import com.solux.luxup.taptap.feature.team.model.TeamButtonTimeline
import com.solux.luxup.taptap.feature.team.model.TeamButtonTimelineRecord
import com.solux.luxup.taptap.feature.team.model.TeamCreateForm
import com.solux.luxup.taptap.feature.team.model.TeamCreateResult
import com.solux.luxup.taptap.feature.team.model.TeamButtonSuggestion
import com.solux.luxup.taptap.feature.team.model.TeamMember
import com.solux.luxup.taptap.feature.team.model.TeamMemberButton
import com.solux.luxup.taptap.feature.team.model.TeamMemberDetail
import com.solux.luxup.taptap.feature.team.model.TeamMemberRecord
import com.solux.luxup.taptap.feature.team.model.TeamMemberRole
import com.solux.luxup.taptap.feature.team.model.TeamMemberSharedButton
import com.solux.luxup.taptap.feature.team.model.TeamButtonPermissionRequest
import com.solux.luxup.taptap.feature.team.model.TeamInsightBarCategory
import com.solux.luxup.taptap.feature.team.model.TeamInsightButtonCount
import com.solux.luxup.taptap.feature.team.model.TeamInsightCalendarDay
import com.solux.luxup.taptap.feature.team.model.TeamInsightCategory
import com.solux.luxup.taptap.feature.team.model.TeamInsightCategoryCount
import com.solux.luxup.taptap.feature.team.model.TeamInsightDaily
import com.solux.luxup.taptap.feature.team.model.TeamInsightDailyBar
import com.solux.luxup.taptap.feature.team.model.TeamInsightMember
import com.solux.luxup.taptap.feature.team.model.TeamInsightMemberActivity
import com.solux.luxup.taptap.feature.team.model.TeamInsightMemberTopButton
import com.solux.luxup.taptap.feature.team.model.TeamInsightMonthly
import com.solux.luxup.taptap.feature.team.model.TeamInsightTimelineItem
import com.solux.luxup.taptap.feature.team.model.TeamInsightTopButton
import com.solux.luxup.taptap.feature.team.model.TeamInsightWeekly
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

    // ---- team-button-controller ----

    suspend fun listButtons(teamId: Long): Result<List<TeamButton>> =
        apiCallHandler.execute { teamApi.listButtons(teamId) }.mapCatching { list -> list.map { it.toModel() } }

    suspend fun createButton(teamId: Long, form: TeamButtonForm): Result<TeamButtonResponseDto> =
        apiCallHandler.execute { teamApi.createButton(teamId, form.toCreateRequest()) }

    suspend fun createButton(teamId: Long, suggestion: TeamButtonSuggestion): Result<TeamButtonResponseDto> =
        apiCallHandler.execute {
            teamApi.createButton(
                teamId,
                CreateTeamButtonRequestDto(
                    buttonName = suggestion.buttonName,
                    iconName = suggestion.iconName,
                    iconColor = suggestion.iconColor,
                    tapPermission = TapPermissionAll,
                    categoryId = suggestion.categoryId,
                ),
            )
        }

    suspend fun getButtonDetail(teamId: Long, teamButtonId: Long): Result<TeamButtonDetail> =
        apiCallHandler.execute { teamApi.getButtonDetail(teamId, teamButtonId) }.mapCatching { it.toModel() }

    suspend fun updateButton(teamId: Long, teamButtonId: Long, form: TeamButtonForm): Result<UpdateTeamButtonResponseDto> =
        apiCallHandler.execute { teamApi.updateButton(teamId, teamButtonId, form.toUpdateRequest()) }

    suspend fun deleteButton(teamId: Long, teamButtonId: Long): Result<DeleteTeamButtonResponseDto> =
        apiCallHandler.execute { teamApi.deleteButton(teamId, teamButtonId) }

    suspend fun toggleButtonNotification(teamId: Long, teamButtonId: Long): Result<Boolean> =
        apiCallHandler.execute { teamApi.toggleButtonNotification(teamId, teamButtonId) }.mapCatching { it.isEnabled }

    suspend fun createRecord(teamId: Long, teamButtonId: Long): Result<CreateTeamButtonRecordResponseDto> =
        apiCallHandler.execute {
            teamApi.createRecord(teamId, teamButtonId, CreateTeamButtonRecordRequestDto())
        }

    suspend fun getLatestRecord(teamId: Long, teamButtonId: Long): Result<TeamButtonLatest> =
        apiCallHandler.execute { teamApi.getLatestRecord(teamId, teamButtonId) }.mapCatching { it.toModel() }

    suspend fun getTimeline(teamId: Long, teamButtonId: Long, cursor: Long?, limit: Int = 30): Result<TeamButtonTimeline> =
        apiCallHandler.execute { teamApi.getTimeline(teamId, teamButtonId, cursor, limit) }.mapCatching { it.toModel() }

    suspend fun updateRecordDetail(
        teamId: Long,
        teamButtonId: Long,
        recordId: Long,
        memo: String?,
        emoji: String?,
    ): Result<UpdateTeamButtonRecordDetailResponseDto> =
        apiCallHandler.execute {
            teamApi.updateRecordDetail(teamId, teamButtonId, recordId, UpdateTeamButtonRecordDetailRequestDto(memo, emoji))
        }

    /**
     * DELETE .../records/{record_id}
     * 응답 data가 빈 값으로 내려와 data != null을 요구하는 공통 apiCallHandler를 쓰면
     * 성공해도 실패로 처리될 수 있어, success 플래그만 직접 확인한다.
     */
    suspend fun deleteRecord(teamId: Long, teamButtonId: Long, recordId: Long): Result<Unit> = runCatching {
        val response = teamApi.deleteRecord(teamId, teamButtonId, recordId)
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw ApiException(body?.message ?: "기록을 삭제하지 못했어요.", response.code())
        }
    }

    suspend fun getButtonCategories(teamId: Long): Result<List<TeamButtonCategory>> =
        apiCallHandler.execute { teamApi.getButtonCategories(teamId) }.mapCatching { list -> list.map { it.toModel() } }

    // ---- team-button-permission-controller ----

    suspend fun requestTapPermission(teamId: Long, teamButtonId: Long): Result<TapPermissionRequestResponseDto> =
        apiCallHandler.execute { teamApi.requestPermission(teamId, teamButtonId) }

    suspend fun decideTapPermission(teamId: Long, teamButtonId: Long, userId: Long, action: String): Result<TapPermissionDecisionResponseDto> =
        apiCallHandler.execute { teamApi.decidePermission(teamId, teamButtonId, userId, TapPermissionDecisionRequestDto(action)) }

    suspend fun listPendingTapPermissionRequests(teamId: Long, teamButtonId: Long): Result<List<TeamButtonPermissionRequest>> =
        apiCallHandler.execute { teamApi.listPendingRequests(teamId, teamButtonId) }.mapCatching { list -> list.map { it.toModel() } }

    suspend fun createButtonCategory(teamId: Long, name: String): Result<TeamButtonCategory> =
        apiCallHandler.execute { teamApi.createButtonCategory(teamId, CreateTeamButtonCategoryRequestDto(categoryName = name)) }
            .mapCatching { it.toModel() }

    suspend fun renameButtonCategory(teamId: Long, categoryId: Long, name: String): Result<TeamButtonCategory> =
        apiCallHandler.execute {
            teamApi.updateButtonCategory(teamId, categoryId, UpdateTeamButtonCategoryRequestDto(categoryName = name))
        }.mapCatching { it.toModel() }

    /**
     * DELETE .../categories/{category_id}
     * 응답 data가 빈 오브젝트로 내려와 data != null을 요구하는 공통 apiCallHandler를 쓰면
     * 성공해도 실패로 처리될 수 있어, success 플래그만 직접 확인한다.
     */
    suspend fun deleteButtonCategory(teamId: Long, categoryId: Long, deleteButtons: Boolean): Result<Unit> = runCatching {
        val response = teamApi.deleteButtonCategory(teamId, categoryId, deleteButtons)
        val body = response.body()
        if (!response.isSuccessful || body?.success != true) {
            throw ApiException(body?.message ?: "카테고리를 삭제하지 못했어요.")
        }
    }

    // ---- team-member-profile-controller ----

    /** 내 프로필의 공유 대상 버튼 목록 (isShared 포함) */
    suspend fun getMyTeamProfile(teamId: Long): Result<List<TeamMemberSharedButton>> =
        apiCallHandler.execute { teamApi.getMyTeamProfile(teamId) }.mapCatching { list -> list.buttons.map { it.toModel() } }

    suspend fun updateMyTeamProfile(teamId: Long, displayName: String): Result<UpdateTeamProfileResponseDto> =
        apiCallHandler.execute { teamApi.updateMyTeamProfile(teamId, UpdateTeamProfileRequestDto(displayName = displayName)) }

    suspend fun updateButtonSharing(teamId: Long, buttons: List<TeamMemberSharedButton>): Result<List<TeamMemberSharedButton>> =
        apiCallHandler.execute {
            teamApi.updateButtonSharing(
                teamId,
                UpdateButtonSharingRequestDto(buttons.map { ButtonSharingUpdateItemDto(it.buttonId, it.isShared) }),
            )
        }.mapCatching { list -> list.buttons.map { it.toModel() } }

    suspend fun getMemberRecords(teamId: Long, userId: Long, cursor: Long? = null, limit: Int? = null): Result<TeamMemberDetail> =
        apiCallHandler.execute { teamApi.getMemberRecords(teamId, userId, cursor, limit) }.mapCatching { it.toModel() }

    // ---- team-insight-controller ----

    suspend fun getDailyInsight(teamId: Long, date: String? = null): Result<TeamInsightDaily> =
        apiCallHandler.execute { teamApi.getDailyInsight(teamId, date) }.mapCatching { it.toModel() }

    suspend fun getWeeklyInsight(teamId: Long, weekStart: String? = null): Result<TeamInsightWeekly> =
        apiCallHandler.execute { teamApi.getWeeklyInsight(teamId, weekStart) }.mapCatching { it.toModel() }

    suspend fun getMonthlyInsight(teamId: Long, year: Int? = null, month: Int? = null): Result<TeamInsightMonthly> =
        apiCallHandler.execute { teamApi.getMonthlyInsight(teamId, year, month) }.mapCatching { it.toModel() }
}

private const val TapPermissionAll = "all"

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

private fun LatestRecordSummaryDto.toModel() = ButtonRecord(
    recordedAt = recordedAt.orEmpty(),
    recordedBy = recordedBy.map { it.toModel() },
    recordedByCount = recordedByCount,
)

private fun TeamButtonListItemDto.toModel() = TeamButton(
    teamButtonId = teamButtonId,
    buttonName = buttonName,
    iconName = iconName.orEmpty(),
    iconColor = iconColor.orEmpty(),
    tapPermission = tapPermission.orEmpty(),
    categoryId = categoryId,
    categoryName = categoryName,
    hasTapPermission = hasTapPermission,
    latestRecord = latestRecord?.toModel(),
)

private fun MyPermissionDto.toModel() = MyButtonPermission(
    hasTapPermission = hasTapPermission,
    permissionStatus = permissionStatus,
)

private fun TeamButtonDetailResponseDto.toModel() = TeamButtonDetail(
    teamButtonId = teamButtonId,
    teamId = teamId,
    buttonName = buttonName,
    iconName = iconName.orEmpty(),
    iconColor = iconColor.orEmpty(),
    description = description,
    tapPermission = tapPermission.orEmpty(),
    isActive = isActive,
    createdBy = createdBy.toModel(),
    myPermission = myPermission.toModel(),
    canEdit = canEdit,
    canDelete = canDelete,
    isTeamOwner = isTeamOwner,
    categoryId = categoryId,
    categoryName = categoryName,
    allowedUserIds = allowedUserIds,
    latestRecord = latestRecord?.toModel(),
    createdAt = createdAt.orEmpty(),
    updatedAt = updatedAt.orEmpty(),
)

private fun TeamButtonTimelineItemDto.toModel() = TeamButtonTimelineRecord(
    recordId = recordId,
    recordedAt = recordedAt,
    memo = memo,
    emoji = emoji,
    recordedBy = recordedBy.toModel(),
)

private fun TeamButtonTimelineResponseDto.toModel() = TeamButtonTimeline(
    records = records.map { it.toModel() },
    hasMore = hasMore,
    nextCursor = nextCursor,
)

private fun LatestRecordResponseDto.toModel() = TeamButtonLatest(
    teamButtonId = teamButtonId,
    buttonName = buttonName.orEmpty(),
    iconName = iconName.orEmpty(),
    iconColor = iconColor.orEmpty(),
    latestRecord = latestRecord?.toModel(),
)

private fun TeamButtonCategoryResponseDto.toModel() = TeamButtonCategory(
    categoryId = categoryId,
    categoryName = categoryName.orEmpty(),
    categoryColor = IconColor.from(categoryColor),
    displayOrder = displayOrder ?: 0,
)

private fun TeamButtonForm.toCreateRequest() = CreateTeamButtonRequestDto(
    buttonName = name.ifBlank { null },
    iconName = iconName.ifBlank { null },
    iconColor = iconColor.key,
    description = description.ifBlank { null },
    tapPermission = tapPermission.value,
    categoryId = category?.categoryId,
    allowedUserIds = allowedUserIds.ifEmpty { null },
)

private fun TeamButtonForm.toUpdateRequest() = UpdateTeamButtonRequestDto(
    buttonName = name.ifBlank { null },
    iconName = iconName.ifBlank { null },
    iconColor = iconColor.key,
    description = description.ifBlank { null },
    tapPermission = tapPermission.value,
    categoryId = category?.categoryId,
    allowedUserIds = allowedUserIds.ifEmpty { null },
)

private fun TapPermissionRequestListItemDto.toModel() = TeamButtonPermissionRequest(
    userId = userId,
    displayName = displayName.orEmpty(),
    profileImageUrl = profileImageUrl,
    requestedAt = requestedAt.orEmpty(),
)

private fun CreateTeamButtonCategoryResponseDto.toModel() = TeamButtonCategory(
    categoryId = categoryId,
    categoryName = categoryName,
    categoryColor = IconColor.from(categoryColor),
    displayOrder = displayOrder ?: 0,
)

private fun UpdateTeamButtonCategoryResponseDto.toModel() = TeamButtonCategory(
    categoryId = categoryId,
    categoryName = categoryName,
    categoryColor = IconColor.from(categoryColor),
    displayOrder = displayOrder ?: 0,
)

private fun TeamProfileButtonItemDto.toModel() = TeamMemberSharedButton(
    buttonId = buttonId,
    buttonName = buttonName.orEmpty(),
    iconName = iconName.orEmpty(),
    iconColor = iconColor.orEmpty(),
    categoryId = categoryId,
    categoryName = categoryName,
    isShared = isShared,
)

private fun MemberRecordButtonItemDto.toModel() = TeamMemberButton(
    buttonId = buttonId,
    buttonName = buttonName.orEmpty(),
    iconName = iconName.orEmpty(),
    iconColor = iconColor.orEmpty(),
)

private fun MemberRecordTimelineItemDto.toModel() = TeamMemberRecord(
    recordId = recordId,
    buttonName = buttonName.orEmpty(),
    recordedAt = recordedAt.orEmpty(),
    memo = memo,
    emoji = emoji,
)

private fun MemberRecordsResponseDto.toModel() = TeamMemberDetail(
    userId = userId,
    displayName = displayName.orEmpty(),
    profileImageUrl = profileImageUrl,
    hasMore = hasMore,
    nextCursor = nextCursor,
    buttons = buttons.map { it.toModel() },
    recentTimeline = recentTimeline.map { it.toModel() },
)

private fun MemberProfileDto.toInsightMember() = TeamInsightMember(
    userId = userId,
    displayName = displayName.orEmpty(),
    profileImageUrl = profileImageUrl,
)

private fun InsightTopButtonDto.toModel() = TeamInsightTopButton(
    teamButtonId = teamButtonId,
    buttonName = buttonName.orEmpty(),
    iconName = iconName.orEmpty(),
    iconColor = iconColor.orEmpty(),
    tapCount = tapCount.toInt(),
    tappedMembers = tappedMembers.map { it.toInsightMember() },
)

private fun InsightCategoryTapCountDto.toModel() = TeamInsightCategory(
    categoryId = categoryId,
    categoryName = categoryName.orEmpty(),
    categoryColor = categoryColor.orEmpty(),
    tapCount = tapCount.toInt(),
)

private fun InsightCategoryTapCountDto.toBarCategory() = TeamInsightBarCategory(
    categoryId = categoryId,
    categoryName = categoryName.orEmpty(),
    categoryColor = categoryColor.orEmpty(),
    tapCount = tapCount.toInt(),
)

private fun InsightButtonTapCountDto.toModel() = TeamInsightButtonCount(
    teamButtonId = teamButtonId,
    buttonName = buttonName.orEmpty(),
    iconName = iconName.orEmpty(),
    iconColor = iconColor.orEmpty(),
    categoryId = categoryId,
    categoryName = categoryName,
    tapCount = tapCount.toInt(),
)

private fun InsightMemberTopButtonDto.toModel() = TeamInsightMemberTopButton(
    teamButtonId = teamButtonId,
    buttonName = buttonName.orEmpty(),
    iconName = iconName.orEmpty(),
    iconColor = iconColor.orEmpty(),
    tapCount = tapCount.toInt(),
)

private fun InsightMemberActivityDto.toModel() = TeamInsightMemberActivity(
    userId = userId,
    displayName = displayName.orEmpty(),
    profileImageUrl = profileImageUrl,
    tapCount = tapCount.toInt(),
    topButton = topButton?.toModel(),
)

private fun InsightTimelineItemDto.toModel() = TeamInsightTimelineItem(
    teamButtonId = teamButtonId,
    buttonName = buttonName.orEmpty(),
    iconName = iconName.orEmpty(),
    iconColor = iconColor.orEmpty(),
    tappedAt = tappedAt.orEmpty(),
    member = member.toInsightMember(),
)

private fun WeeklyDailyTapCountDto.toModel() = TeamInsightDailyBar(
    date = date,
    tapCount = tapCount.toInt(),
    categories = categories.map { it.toBarCategory() },
)

private fun MonthlyDailyTapCountDto.toModel() = TeamInsightCalendarDay(
    date = date,
    tapCount = tapCount.toInt(),
)

private fun MonthlyCategoryTapCountDto.toModel() = TeamInsightCategoryCount(
    categoryId = categoryId,
    categoryName = categoryName.orEmpty(),
    categoryColor = categoryColor.orEmpty(),
    tapCount = tapCount.toInt(),
    ratio = ratio,
)

private fun DailyInsightResponseDto.toModel() = TeamInsightDaily(
    teamId = teamId,
    targetDate = targetDate,
    totalTapCount = totalTapCount.toInt(),
    topButton = topButton?.toModel(),
    timeline = timeline.map { it.toModel() },
    categories = categories.map { it.toModel() },
    buttonTapCounts = buttonTapCounts.map { it.toModel() },
    memberActivity = memberActivity.map { it.toModel() },
)

private fun WeeklyInsightResponseDto.toModel() = TeamInsightWeekly(
    teamId = teamId,
    weekStart = weekStart,
    weekEnd = weekEnd,
    totalTapCount = totalTapCount.toInt(),
    topButton = topButton?.toModel(),
    dailyTapCounts = dailyTapCounts.map { it.toModel() },
    buttonTapCounts = buttonTapCounts.map { it.toModel() },
    memberActivity = memberActivity.map { it.toModel() },
)

private fun MonthlyInsightResponseDto.toModel() = TeamInsightMonthly(
    teamId = teamId,
    year = year,
    month = month,
    totalTapCount = totalTapCount.toInt(),
    topButton = topButton?.toModel(),
    dailyTapCounts = dailyTapCounts.map { it.toModel() },
    categoryTapCounts = categoryTapCounts.map { it.toModel() },
    memberActivity = memberActivity.map { it.toModel() },
)
