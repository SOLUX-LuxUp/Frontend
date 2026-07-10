package com.solux.luxup.taptap.feature.home.template.data

import com.solux.luxup.taptap.feature.home.template.model.ChecklistCategory
import com.solux.luxup.taptap.feature.home.template.model.ChecklistTemplate

val memoryTemplate = ChecklistTemplate(
    id = "memory",
    optionTitle = "잊지 않기",
    description = "바쁜 일상 속 놓치기 쉬운 것들을 기억해요",
    categories = listOf(
        ChecklistCategory(
            title = "집",
            items = listOf(
                "침구류 세탁하기",
                "잠옷 세탁하기",
                "냉장고 정리하기",
                "쓰레기 버리기",
                "설거지하기",
                "집 청소하기",
                "식물에 물 주기",
                "반려동물 산책하기"
            )
        ),
        ChecklistCategory(
            title = "생활",
            items = listOf(
                "카드값 납부",
                "보험료 납부",
                "구독 프로그램 갱신",
                "적금 이체",
                "택배 반품",
                "일정 정리",
                "정산 완료"
            )
        ),
        ChecklistCategory(
            title = "안전",
            items = listOf(
                "가스불 확인",
                "콘센트 확인",
                "냉난방기 확인",
                "전등 확인",
                "창문 확인",
                "문 잠금 확인"
            )
        ),
        ChecklistCategory(
            title = "건강",
            items = listOf(
                "약 먹기",
                "영양제 먹기",
                "스트레칭 하기",
                "운동하기",
                "혈당 체크",
                "병원 방문",
                "치과 방문"
            )
        )
    )
)

val selfCareTemplate = ChecklistTemplate(
    id = "self_care",
    optionTitle = "나 챙기기",
    description = "소중한 내 몸과 마음의 리듬을 기억해요",
    categories = listOf(
        ChecklistCategory(
            title = "몸 관리",
            items = listOf(
                "물 마시기",
                "스트레칭 하기",
                "영양제 챙기기",
                "수면 시간 지키기"
            )
        ),
        ChecklistCategory(
            title = "마음 관리",
            items = listOf(
                "감정 일기 쓰기",
                "명상하기",
                "좋아하는 음악 듣기",
                "혼자만의 시간 갖기"
            )
        ),
        ChecklistCategory(
            title = "생활 리듬",
            items = listOf(
                "규칙적으로 기상하기",
                "제때 식사하기",
                "하루 일과 정리하기",
                "취침 전 루틴 지키기"
            )
        )
    )
)

val productivityTemplate = ChecklistTemplate(
    id = "productivity",
    optionTitle = "성장하기",
    description = "배우고 나아가는 순간들을 기억해요",
    categories = listOf(
        ChecklistCategory(
            title = "정돈",
            items = listOf(
                "책상 정리하기",
                "할 일 목록 정리하기",
                "자료 정리하기"
            )
        ),
        ChecklistCategory(
            title = "학습",
            items = listOf(
                "오늘 배운 내용 정리하기",
                "강의 듣기",
                "책 읽기"
            )
        ),
        ChecklistCategory(
            title = "탐색",
            items = listOf(
                "새로운 정보 찾아보기",
                "관심 분야 조사하기"
            )
        ),
        ChecklistCategory(
            title = "성장",
            items = listOf(
                "목표 점검하기",
                "피드백 정리하기",
                "회고 작성하기"
            )
        )
    )
)

val onboardingTemplates = listOf(memoryTemplate, selfCareTemplate, productivityTemplate)

fun findTemplate(id: String): ChecklistTemplate =
    onboardingTemplates.first { it.id == id }
