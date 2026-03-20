# HaruPay 개발자 센터

HaruPay 개발자 포털입니다. 클라이언트 등록, API 키 관리, API 문서를 제공합니다.

## 기능

- 클라이언트 등록 및 API 키 발급
- API 키 재발급 및 관리
- 결제 API 문서
- 클라이언트 상태 관리

## 기술 스택

- React 18
- TypeScript
- Vite
- React Router DOM
- Axios

## 시작하기

```bash
# 의존성 설치
npm install

# 개발 서버 실행
npm run dev

# 프로덕션 빌드
npm run build
```

## 환경 변수

`.env` 파일을 생성하고 다음 변수를 설정하세요:

```
VITE_API_BASE_URL=http://payments:8071
```

## API 연동

개발자 센터는 payments 서비스의 API를 호출합니다:

- `POST /api/clients` - 클라이언트 등록
- `GET /api/clients/:id` - 클라이언트 조회
- `POST /api/clients/:id/regenerate-api-key` - API 키 재발급
- `POST /api/clients/:id/deactivate` - 클라이언트 비활성화
- `POST /api/clients/:id/activate` - 클라이언트 활성화
