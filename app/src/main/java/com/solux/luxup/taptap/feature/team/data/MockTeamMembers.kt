package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.feature.team.model.MemberLatestRecord
import com.solux.luxup.taptap.feature.team.model.TeamMember
import com.solux.luxup.taptap.feature.team.model.TeamMemberRole

val MockTeamMembers: List<TeamMember> = listOf(
    TeamMember(1, "누리", null, TeamMemberRole.OWNER,  MemberLatestRecord("버터 세척", "2025-05-23T14:12:00")),
    TeamMember(2, "희경", null, TeamMemberRole.MEMBER, MemberLatestRecord("물 주기",   "2025-05-22T09:00:00")),
    TeamMember(3, "수민", null, TeamMemberRole.MEMBER, MemberLatestRecord("물 주기",   "2025-05-22T21:00:00")),
    TeamMember(4, "하연", null, TeamMemberRole.MEMBER, MemberLatestRecord("코드 수정", "2025-05-23T13:50:00")),
    TeamMember(5, "세희", null, TeamMemberRole.MEMBER, MemberLatestRecord("알림 끄기", "2025-05-23T09:20:00")),
    TeamMember(6, "정민", null, TeamMemberRole.MEMBER, MemberLatestRecord("운동하기", "2025-05-23T11:30:00")),
    TeamMember(7, "은서", null, TeamMemberRole.MEMBER, null),
)