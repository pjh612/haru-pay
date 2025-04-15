# Introduction
하루페이는 하루 머니를 충전해 결제에 사용할 수 있는 간편 결제를 서비스입니다.
하루 서비스 회원이라면 하루 머니가 연동된 서비스에서 하루 페이머니로 결제할 수 있습니다.

[자세한 구현 과정 및 트러블 슈팅 Link](https://docs.google.com/document/d/1Vn3GW8Ee16jpYBVr9buV8TqgRKxyDjpv0vMxdo4A9mY/edit?usp=sharing)

| ![Image 1](https://github.com/user-attachments/assets/440c2429-0c12-4de1-81bd-b345947bc933) | ![Image 2](https://github.com/user-attachments/assets/ee3f9500-1715-4993-a9b0-faa10357a0cf) |
|------------------|------------------|
| ![Image 3](https://github.com/user-attachments/assets/52dc2933-a21c-4851-9adc-45e4391f363f) | |

| ![image1](https://github.com/user-attachments/assets/712b28f7-285f-4c40-9754-e85cb6a6d6e6) | ![image2](https://github.com/user-attachments/assets/0139661e-40b8-475e-b367-3a899228fe2e) |
|---------------------------------------------------|---------------------------------------------------|
| ![image3](https://github.com/user-attachments/assets/6db75f1a-006c-4b96-aec2-227509009975) | ![image4](https://github.com/user-attachments/assets/7671189a-4730-4ac5-a655-9f9ee037e978) |

# Start
```
docker-compose up
```

# 연동 가이드

## 결제 준비 요청 API

### URL
`POST /api/payment/prepare`

### 메서드
`POST`

### 요청 헤더

| 키                    | 값                              | 설명                         |
|-----------------------|---------------------------------|------------------------------|
| `Authorization`        | `apiKey <api_key>`              | 클라이언트의 API 키          |
| `X-PAY-CLIENT-ID`      | `<client_id>`                   | 클라이언트 ID                |

### 요청 바디
`application/json` 형식으로 아래와 같은 JSON 데이터를 전달해야 합니다.

| 필드 이름        | 타입         | 필수 여부 | 설명                          |
|------------------|--------------|-----------|-------------------------------|
| `orderId`        | `String`     | ✔️         | 주문 ID                      |
| `requestPrice`   | `BigDecimal` | ✔️         | 요청 결제 금액               |
| `productName`    | `String`     | ✔️         | 제품 이름                    |

#### 요청 예시
```json
{
  "orderId": "ORDER12345",
  "requestPrice": 10000.00,
  "productName": "Wireless Headphone"
}
```

### 응답 예시

#### 성공 시
```json
{
  "paymentId": "d2c98b67-bf7d-4e59-83c3-1b2f905b7b35"
}
```

## 결제 승인 API

### URL
`POST /api/payment/confirm`

### 메서드
`POST`

### 요청 헤더

| 키                    | 값                              | 설명                         |
|-----------------------|---------------------------------|------------------------------|
| `Authorization`        | `apiKey <api_key>`              | 클라이언트의 API 키          |
| `Content-Type`         | `application/json`              | 요청 본문 타입               |

### 요청 바디
`application/json` 형식으로 아래와 같은 JSON 데이터를 전달해야 합니다.

| 필드 이름        | 타입         | 필수 여부 | 설명                          |
|------------------|--------------|-----------|-------------------------------|
| `paymentId`      | `UUID`       | ✔️         | 결제 ID                        |

#### 요청 예시
```json
{
  "paymentId": "a2d8bc7d-0b07-47a1-b3b9-48578de8a63f"
}
```

#### 응답 예시

응답 본문은 없으며, 요청이 성공하면 HTTP 200 OK 상태 코드가 반환됩니다.

## 결제 결과 구독 API

### URL
`GET /api/payment-result/subscribe`

### 메서드
`GET`

### 요청 헤더

| 키                    | 값                              | 설명                         |
|-----------------------|---------------------------------|------------------------------|
| `Authorization`        | `apiKey <api_key>`              | 클라이언트의 API 키          |
| `Last-Event-ID`        | `<last_event_id>`               | 마지막 이벤트 ID (옵션)      |

### 요청 파라미터

| 필드 이름            | 타입         | 필수 여부 | 설명                          |
|----------------------|--------------|-----------|-------------------------------|
| `paymentId`          | `UUID`       | ✔️         | 결제 ID                       |

#### 요청 예시
```http
GET /api/payment-result/subscribe?paymentId=d2c98b67-bf7d-4e59-83c3-1b2f905b7b35 HTTP/1.1
Authorization: apiKey <api_key>
Last-Event-ID: "12345"
```

### 응답

이 API는 **Server-Sent Events (SSE)** 방식으로 실시간 스트리밍 데이터를 제공합니다. 응답 내용은 `PaymentConfirmResponse` 형식에 맞춰 제공됩니다.

#### 응답 예시

```json
{
  "requestId": "cfa8d3ed-d9b2-4207-9bc2-85c16a4d632d",
  "orderId": "abc123",
  "requestMemberId": "fbb1267a-d836-4fd1-96db-774e567d03d8",
  "requestPrice": 500000,
  "clientId": "bcd7991e-cb21-4b99-9013-ef83b10f450d",
  "paymentStatus": 1,
  "approvedAt": "2025-03-08T12:34:56Z"
}
```

#### 필드 설명

- `requestId`: 요청 ID (UUID 형식)
- `orderId`: 주문 ID
- `requestMemberId`: 요청한 사용자 ID (UUID 형식)
- `requestPrice`: 결제 요청 금액 (BigDecimal 형식)
- `clientId`: 클라이언트 ID (UUID 형식)
- `paymentStatus`: 결제 상태 (예: `1` = 승인됨, `0` = 실패 등)
- `approvedAt`: 결제 승인 시간 (ISO 8601 형식)

이 API는 실시간으로 결제 결과를 스트리밍하여 클라이언트에 전달합니다.


