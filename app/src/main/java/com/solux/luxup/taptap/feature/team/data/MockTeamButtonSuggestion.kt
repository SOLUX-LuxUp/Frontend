package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.feature.team.model.TeamButtonSuggestion

/**
 * GET /api/teams/{team_id}/template/suggestions 응답 목데이터.
 * '탭탭_팀_템플릿아이콘.xlsx' 매핑 기준.
 *
 * 실제 서버 응답은 이미 생성된 버튼을 제외하고 내려주며,
 * 노출 순서(랜덤 여부)는 백엔드 확인 필요.
 */

/** templateId = 1 (함께하기) */
val mockSuggestionsTogether = listOf(
    // 가족
    TeamButtonSuggestion("가족 식사", "food", "yellow", 1, "가족"),
    TeamButtonSuggestion("가족 회의", "chat", "darkgrey", 1, "가족"),
    TeamButtonSuggestion("가족 여행", "travel", "cyan", 1, "가족"),
    TeamButtonSuggestion("가족 사진", "camera", "indigo", 1, "가족"),
    TeamButtonSuggestion("기념일", "celebrate", "red", 1, "가족"),
    TeamButtonSuggestion("장보기", "shopping2", "red", 1, "가족"),
    TeamButtonSuggestion("집안일", "clean", "green", 1, "가족"),
    TeamButtonSuggestion("반려동물 산책", "dog", "orange", 1, "가족"),
    // 친구
    TeamButtonSuggestion("보고 싶어", "lightning", "cyan", 2, "친구"),
    TeamButtonSuggestion("심심해", "sleep", "purple", 2, "친구"),
    TeamButtonSuggestion("뭐해?", "chat", "black", 2, "친구"),
    TeamButtonSuggestion("밥 먹자", "food", "orange", 2, "친구"),
    TeamButtonSuggestion("카페 가자", "cup", "yellow", 2, "친구"),
    TeamButtonSuggestion("놀러 가자", "travel", "cyan", 2, "친구"),
    TeamButtonSuggestion("한잔할까?", "drink", "blue", 2, "친구"),
    TeamButtonSuggestion("정산하자", "pay", "indigo", 2, "친구"),
    // 연인
    TeamButtonSuggestion("데이트", "fire", "pink", 3, "연인"),
    TeamButtonSuggestion("기념일", "celebrate", "red", 3, "연인"),
    TeamButtonSuggestion("추억", "camera", "darkgrey", 3, "연인"),
    TeamButtonSuggestion("여행", "travel", "grey", 3, "연인"),
    // No Category
    TeamButtonSuggestion("보고 싶어", "lightning", "cyan", null, null),
    TeamButtonSuggestion("♥", "flower", "pink", null, null),
)

/** templateId = 2 (협력하기) */
val mockSuggestionsCollaborate = listOf(
    // 프로젝트
    TeamButtonSuggestion("공지 등록", "book", "red", 4, "프로젝트"),
    TeamButtonSuggestion("회의", "chat", "green", 4, "프로젝트"),
    TeamButtonSuggestion("새로운 아이디어", "lightbulb", "yellow", 4, "프로젝트"),
    TeamButtonSuggestion("기획 업데이트", "note", "cyan", 4, "프로젝트"),
    TeamButtonSuggestion("개발 업데이트", "labtop", "blue", 4, "프로젝트"),
    TeamButtonSuggestion("디자인 업데이트", "pencil", "purple", 4, "프로젝트"),
    TeamButtonSuggestion("작업물 수정", "pencil", "green", 4, "프로젝트"),
    TeamButtonSuggestion("QA", "chat", "blue", 4, "프로젝트"),
    TeamButtonSuggestion("유저 테스트", "person", "red", 4, "프로젝트"),
    TeamButtonSuggestion("피드백", "chat", "indigo", 4, "프로젝트"),
    TeamButtonSuggestion("발표", "person", "black", 4, "프로젝트"),
    TeamButtonSuggestion("버전 업데이트", "lightning", "yellow", 4, "프로젝트"),
    TeamButtonSuggestion("완료", "celebrate", "orange", 4, "프로젝트"),
    // 목표 달성
    TeamButtonSuggestion("목표 시작", "plant", "green", 5, "목표 달성"),
    TeamButtonSuggestion("출석", "person", "grey", 5, "목표 달성"),
    TeamButtonSuggestion("활동 인증", "sport2", "blue", 5, "목표 달성"),
    TeamButtonSuggestion("진행 공유", "music2", "purple", 5, "목표 달성"),
    TeamButtonSuggestion("하루 시작", "sun", "red", 5, "목표 달성"),
    TeamButtonSuggestion("하루 끝", "sleep", "indigo", 5, "목표 달성"),
    TeamButtonSuggestion("목표 달성", "celebrate", "yellow", 5, "목표 달성"),
    TeamButtonSuggestion("쉬어가기", "cup", "darkgrey", 5, "목표 달성"),
    TeamButtonSuggestion("응원", "fire", "orange", 5, "목표 달성"),
)