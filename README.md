# 목차

- [서비스 소개](#서비스-소개)
  - [타임라인](#타임라인)
  - [주요 기능](#주요-기능)
- [개발 경험](#개발-경험)
  - [개발 원칙](#개발-원칙)
  - [핵심 경험](#핵심-경험)
- [프로젝트 구조](#프로젝트-구조)
  - [기술스택](#기술스택)
  - [아키텍처](#아키텍처)
  - [ERD](#ERD)

# 서비스 소개

### 🤔 네트워킹, 이런 경험 있으신가요?

- 반복되는 즉석 자기소개 부담스러워요.
- 여러 사람을 만나고 나면 누가 누구였는지 헷갈려요.
- SNS를 주고 받을 때 번거로워요.

### 💡 SHARE:V는

QR 기반의 행사용 명함 서비스로, 더 쉽고 즐겁게 네트워킹 해보세요.

- 서로의 명함을 공유하며 자연스럽게 대화를 시작할 수 있어요.
- 행사별 명함으로 인맥과 대화 내용을 구별할 수 있어요.
- 서로의 SNS에 쉽게 접속하며 행사 이후에도 인연을 이어갈 수 있어요.

## 타임라인
<img width="1526" height="653" alt="쉐어브_타임라인" src="https://github.com/user-attachments/assets/02cd13ea-2485-4ad2-9ed3-a1b7388d032f" />

## 주요 기능


| 가입 및 프로필 생성                                               | 행사 참여 및 카드 생성               | 참가자 명함 조회 (명함 공유 전)        |
| -------------------------------------------------- | --------------------------- | ------------------- |
| ![쉐어브_가입+프로필생성](https://github.com/user-attachments/assets/8f6e9637-c634-4387-a15b-81656482c8a4)| ![쉐어브_행사참여+명함생성](https://github.com/user-attachments/assets/03490536-c6b1-4d86-8f70-8ed35f638512)| ![쉐어브_카드공유전](https://github.com/user-attachments/assets/b383582b-2eed-4ed9-a579-e94072a3ef2e)|
| 참가자 명함 받기 (PIN 번호)                                            | 참가자 명함 조회 (명함 공유 후) 및 피드백 제보            | 프로필 수정 및 로그아웃         |
| ![쉐어브_카드공유](https://github.com/user-attachments/assets/3ffcd590-9e1b-43ed-9783-0d556c24578e)|![쉐어브_명함조회_공유완료](https://github.com/user-attachments/assets/5c097046-b254-4073-9a0b-1af8d226ceff) |  ![쉐어브_프로필수정](https://github.com/user-attachments/assets/2a6f69e8-8510-480f-8c58-50fceaa60450) |
| 탈퇴 및 약관 조회                                         | 예외처리 (존재하지 않는 페이지)           | 예외처리 (네트워크 연결 끊김)    |
![쉐어브_탈퇴 및 약관](https://github.com/user-attachments/assets/998c072b-3c87-4ecd-88ab-4832bbf27a3d) |![쉐어브_예외처리1](https://github.com/user-attachments/assets/a21b3ebf-32ba-4699-8354-e29d8819a2f3)|![쉐어브_예외처리_서버](https://github.com/user-attachments/assets/2e756707-9375-4b69-a59c-a3fae5d2280e)


# 프로젝트 구조
## 기술스택

| Category          | Stack                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| ----------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Frontend**      | ![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=flat&logo=typescript&logoColor=white) ![npm](https://img.shields.io/badge/npm-CB3837?style=flat&logo=npm&logoColor=white) ![ESLint](https://img.shields.io/badge/ESLint-4B32C3?style=flat&logo=eslint&logoColor=white) ![Prettier](https://img.shields.io/badge/Prettier-F7B93E?style=flat&logo=prettier&logoColor=black) ![Vite](https://img.shields.io/badge/Vite-646CFF?style=flat&logo=vite&logoColor=white) ![React](https://img.shields.io/badge/React-61DAFB?style=flat&logo=react&logoColor=black) ![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=flat&logo=tailwind-css&logoColor=white) ![React Query](https://img.shields.io/badge/React_Query-FF4154?style=flat&logo=react-query&logoColor=white) ![Zustand](https://img.shields.io/badge/Zustand-000000?style=flat&logo=zustand&logoColor=white) ![MSW](https://img.shields.io/badge/MSW-FF6A33?style=flat&logoColor=white) |
| **Backend**       | ![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white) ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat&logo=spring-security&logoColor=white) ![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat&logo=spring&logoColor=white) ![QueryDSL](https://img.shields.io/badge/QueryDSL-4479A1?style=flat&logoColor=white) ![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white) ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white)                                                                                                                                                                                                                                                               |
| **Deployment**    | ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white) ![Google Cloud](https://img.shields.io/badge/Google_Cloud-4285F4?style=flat&logo=google-cloud&logoColor=white) ![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat&logo=github-actions&logoColor=white)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| **Communication** | ![Notion](https://img.shields.io/badge/Notion-000000?style=flat&logo=notion&logoColor=white) ![Figma](https://img.shields.io/badge/Figma-F24E1E?style=flat&logo=figma&logoColor=white) ![Discord](https://img.shields.io/badge/Discord-7289DA?style=flat&logo=discord&logoColor=white)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |

## 아키텍처

<img width="2684" height="892" alt="image" src="https://github.com/user-attachments/assets/09dd6b24-4592-4346-82e0-9c4e3e0be154" />

- [FE 구경하기](https://github.com/Nowdays-Goodnight-Cool-Niang/SharEv-Frontend)

## ERD
```mermaid
erDiagram
	accounts {
	BIGINT account_id PK
	BIGINT kakao_oauth_id UK
	VARCHAR name
	VARCHAR email
	BOOLEAN initial_role_granted_flag
	VARCHAR linkedin_url
	VARCHAR github_url
	VARCHAR instagram_url
	TIMESTAMP created_at
	TIMESTAMP updated_at
	}
	
	events {
	    BINARY event_id PK
	    TIMESTAMP created_at
	    TIMESTAMP updated_at
	}
	
	profiles {
	    BIGINT profile_id PK
	    BINARY event_id FK
	    BIGINT account_id FK
	    INT pin_number
	    INT icon_number
	    VARCHAR introduce
	    VARCHAR proudest_experience
	    VARCHAR tough_experience
	    TIMESTAMP created_at
	    TIMESTAMP updated_at
	}
	
	relations {
	    BIGINT first_profile_id PK, FK
	    BIGINT second_profile_id PK, FK
	    TIMESTAMP created_at
	    TIMESTAMP updated_at
	}
	
	feedbacks {
	    BIGINT feedback_id PK
	    VARCHAR feedback
	    TIMESTAMP created_at
	    TIMESTAMP updated_at
	}
	
	accounts ||--o{ profiles : "has"
	events ||--o{ profiles : "hosts"
	profiles }o--|| relations : "is_first_profile"
	profiles }o--|| relations : "is_second_profile"
```
