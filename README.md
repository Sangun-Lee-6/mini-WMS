# 📦 mini-WMS (Warehouse Management System)

## 01. 프로젝트에 대한 정보

### 📌 프로젝트 제목

**mini-WMS**

> 창고 관리(WMS) 시뮬레이션 프로젝트
입고→재고→피킹→패킹→출고까지 물류 프로세스를 학습 목적으로 구현
>

🎥 **전체 시연 영상**  
![Image](https://github.com/user-attachments/assets/614584ad-7760-4a38-8084-857676074596)

---

### 📌 프로젝트 정보

- **목적:** 물류(WMS) 핵심 프로세스를 이해하고, 입출고 및 피킹 과정을 간단히 시뮬레이션하기 위한 프로젝트
- **개발 기간:** 2025.07 ~ 진행 중
- **주요 목표:**
    - WMS 프로세스(입고 → 보관 → 피킹 → 패킹 → 출고) 전반에 대한 도메인 이해 및 구현
    - Spring Boot + JPA 기반 CRUD 기능 및 프로세스별 API 제공
    - 피킹 최적화 알고리즘(FIFO, Greedy, TSP) 적용 및 KPI(이동거리·소요시간) 비교 기능 구현

---

### 📌 배포 주소

- **웹사이트 : https://sangun-lee-6.shop**
- **배포 환경:** 개인 Ubuntu 홈서버에서 직접 호스팅 및 운영

---

### 📌 팀 소개

- **이상운 (Solo Project)**
    - Backend Engineer
    - GitHub: [Sangun-Lee-6](https://github.com/Sangun-Lee-6)
    - 역할: 전체 설계, API 구현, 서버 배포

---

### 📌 프로젝트 소개

`mini-WMS`는 실제 **물류센터의 기본 프로세스**를 학습하고, 이를 **백엔드 프로젝트**로 구현한 **물류 시뮬레이션 서비스**입니다.

- 📦 **프로세스:** 입고 → 재고 조회 → 피킹 → 패킹 → 출고
- 🚶 **피킹 알고리즘:** `FIFO`, `Greedy`, `TSP` 방식별 **피킹 경로 최적화 결과** 비교 가능
- 🌐 **배포:** Ubuntu 홈서버를 활용해 **개인 도메인**으로 서비스 운영 중


---

## 02. 기술 스택

### 📌 Backend

- **Language:** Java 17
- **Framework:** Spring Boot, Spring MVC, JPA
- **DB:** H2/MariaDB

### 📌 Environment

- **OS:** Ubuntu 22.04 (홈서버)
- **Build Tool:** Gradle

---

## 03. 화면 구성 / API

### 📌 화면 구성

- `/` → 홈 (입고/재고/피킹/출고 버튼)
- `/inbound` → 입고 등록 및 입고 현황 조회

<img width="1464" height="692" alt="Image" src="https://github.com/user-attachments/assets/56fcb86d-8fc3-45ff-b01b-75d8c22dade6" />

- `/inventory` → 재고 위치별 조회

<img width="1457" height="778" alt="Image" src="https://github.com/user-attachments/assets/673b7690-7fa0-435c-b5cf-a31e604e0bfa" />

- `/picking` → 가상 주문을 생성하고, 피킹 대기 목록에 대해 피킹 알고리즘을 실행

<img width="1400" height="739" alt="Image" src="https://github.com/user-attachments/assets/436c8e66-6ce7-45f6-8333-50e456495feb" />


<img width="1375" height="718" alt="Image" src="https://github.com/user-attachments/assets/a929acbb-90c2-4f9e-95d7-7091b629eac1" />

- `/packing` → 패킹 화면 준비 중
- `/shipments` → 출고 화면 준비 중

### 📌 API

1️⃣ 입고 관리 API (`/api/inbound`)

| Method | Endpoint | 설명 |
| --- | --- | --- |
| POST | `/api/inbound` | 입고 등록 |
| GET | `/api/inbound` | 입고 현황 조회 |

2️⃣ 주문 관리 API (`/api/orders`)

| Method | Endpoint | 설명 |
| --- | --- | --- |
| POST | `/api/orders/random` | 랜덤 주문 생성 |
| GET | `/api/orders/waiting` | 피킹 대기 상태 주문 조회 |

3️⃣ 피킹 관리 API (`/api/picking`)

| Method | Endpoint | 설명 |
| --- | --- | --- |
| POST | `/api/picking` | 실제 피킹 실행 (재고 차감 반영) – 지정한 알고리즘(FIFO, Greedy, TSP)으로 피킹 수행 |
| POST | `/api/picking/simulate` | 피킹 시뮬레이션 (재고 차감 없이 KPI만 계산) – 지정한 알고리즘(FIFO, Greedy, TSP)으로 결과 예측 |

---

## 04. 주요 기능

- **입고 관리**
    - 랜덤 상품과 랜덤 수량으로 입고 처리
    - 5×10 창고 그리드에 자동 배치 (칸당 1종류, 최대 5개 저장 가능)
- **재고 조회**
    - 창고 전체 재고 현황 확인
- **피킹 시뮬레이션**
    - 가상 주문 생성 → 피킹 대기 목록 자동 생성
    - 피킹 작업자는 좌측 상단(0,0)에서 출발해 주문 상품을 순회 후 **출발점으로 복귀**
    - 이동 시간은 **맨해튼 거리(|x1 - x2| + |y1 - y2|)** 기준 (칸당 20초)
    - 피킹 작업 시간은 **상품 1개당 5초**로 계산
    - 세 가지 알고리즘(FIFO, Greedy, TSP)으로 시뮬레이션 실행
    - 알고리즘별 KPI(총 이동거리, 총 소요시간) 비교 제공
- **실제 피킹 실행**
    - 선택한 알고리즘으로 피킹을 수행
    - 실행 시 재고 차감 및 패킹 대기 상태로 처리

---

## 05. 아키텍처

- **디렉토리 구조**

    ```
    src
    ├─ main
    │   ├─ java/demo/mini_WMS
    │   │   ├─ controller
    │   │   ├─ domain
    │   │   ├─ repository
    │   │   └─ service
    │   └─ resources
    │       ├─ static
    │       └─ templates
    └─ test
    
    ```


---