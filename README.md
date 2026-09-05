# TapTap

습관을 "버튼"으로 만들어 탭 한 번으로 기록하고, 개인 인사이트로 돌아보고, 팀 스페이스에서 함께 습관을 관리하는 안드로이드 앱입니다.

## 소개

TapTap은 반복하고 싶은 행동(운동, 독서, 물 마시기 등)을 하나의 "버튼"으로 만들어두고, 실행할 때마다 버튼을 눌러 기록하는 습관 관리 앱입니다. 기록은 일간/주간/월간 인사이트로 시각화되고, 버튼별로 알림(리마인더)을 걸어둘 수 있습니다. 개인 습관뿐 아니라 "팀 스페이스"를 통해 팀원들과 공유 버튼을 만들고 서로의 기록/활동을 확인하며 함께 습관을 관리할 수 있습니다.

## 담당

| 파트 | 담당자 |
| --- | --- |
| 개인 (Home / Insight / Notification / Auth) | 정수민 |
| 팀 스페이스 | 유하연 |

## 주요 기능

### 개인 (Home / Insight / Notification)
- 버튼 생성·기록·삭제, 즐겨찾기, 카테고리별 관리
- 온보딩 템플릿으로 첫 버튼 빠르게 만들기
- 일간·주간·월간 인사이트 (기록 타임라인, 비율, 캘린더, 라이프스타일 분석)
- 버튼별 알림(리마인더) 설정
- 로그인/회원가입/계정 설정(비밀번호 변경, 회원 탈퇴 등)

### 팀 스페이스
- 팀 생성(아이콘/이미지/템플릿 선택) 및 초대 코드로 팀 참여
- 팀 공유 버튼 생성·기록·카테고리·탭 권한 관리
- 팀 인사이트 (일간/주간/월간, 멤버별 활동·최다 기록 버튼)
- 멤버 목록/프로필, 공유 중인 버튼 설정, 최근 기록 확인
- 팀 설정(이름/이미지·아이콘/최대 인원/알림/버튼 권한/팀장 위임/팀 삭제)

## 기술 스택

- **언어/UI**: Kotlin, Jetpack Compose (Material3)
- **아키텍처**: MVVM (ViewModel + Compose State)
- **DI**: Hilt
- **네트워킹**: Retrofit, OkHttp, kotlinx.serialization
- **비동기**: Kotlin Coroutines
- **이미지 로딩**: Coil
- **보안 저장소**: EncryptedSharedPreferences (로그인 토큰)
- **네비게이션**: Navigation Compose

## 프로젝트 구조

```
app/src/main/java/com/solux/luxup/taptap/
├── core/                 # 공용 UI 컴포넌트, 네트워크, 인증, 유틸리티
│   ├── auth/             # 토큰 관리
│   ├── navigation/       # 하단 네비게이션
│   ├── network/          # API 클라이언트, 공통 응답 처리
│   ├── ui/               # 공용 테마, 아이콘, 컴포넌트
│   └── util/             # 공용 유틸 (카테고리, 아바타, 검색바 등)
├── feature/
│   ├── auth/             # 로그인·회원가입·계정 설정
│   ├── home/             # 홈 (버튼 생성/기록/즐겨찾기)
│   ├── insight/          # 개인 인사이트 (일간/주간/월간)
│   ├── notification/     # 알림·리마인더
│   ├── splash/           # 스플래시/온보딩 진입
│   └── team/             # 팀 스페이스 (팀/버튼/멤버/인사이트/설정)
└── MainActivity.kt       # 앱 진입점, 전체 네비게이션 그래프
```

각 기능은 `data`(API/Repository) · `model`(도메인 모델) · `presentation`(Screen/ViewModel) 계층으로 구성되어 있습니다.

## 시작하기

### 요구 사항
- Android Studio (최신 버전 권장)
- JDK 11
- minSdk 24 / targetSdk 36

### 빌드

```bash
git clone <repository-url>
cd LuxUp_TAPTAP
./gradlew assembleDebug
```

Android Studio에서 프로젝트를 열고 Gradle Sync 후 실행해도 됩니다.

## 라이선스

내부 프로젝트로, 별도 라이선스 명시 전까지 무단 배포를 금합니다.
