# Introduction
하루페이는 하루 머니를 충전해 결제에 사용할 수 있는 간편 결제를 서비스입니다.
하루 서비스 회원이라면 하루 머니가 연동된 서비스에서 하루 페이머니로 결제할 수 있습니다.


# 연동 예시

## Joy Store와 Haru Pay 연동 예시
![image](https://github.com/user-attachments/assets/4243651d-734f-43dd-8a3f-60eccbe8d9af)
<img width="612" alt="image" src="https://github.com/user-attachments/assets/325e3859-6b1b-4e7a-a25e-4f1e86c91e39" />

머니가 충분할 시 충전하지 않고 결제합니다.

<img width="612" alt="image" src="https://github.com/user-attachments/assets/02ae3fc9-e306-4acb-90de-33aefd1e7dcb" />

머니가 충분하지 않으면 만원 단위로 자동 충전 후 결제를 진행합니다.

<img width="612" alt="image" src="https://github.com/user-attachments/assets/2c4cb694-30ef-4bf8-a91f-2a66a271a70e" />


결제 버튼을 누르면 Joy-Store가 결제 승인을 요청합니다. 이 단계에서 최종적으로 결제가 진행됩니다.

<img width="612" alt="image" src="https://github.com/user-attachments/assets/712b28f7-285f-4c40-9754-e85cb6a6d6e6" />

결제 승인 요청을 보내고 결제 승인 완료 이벤트 수신을 대기합니다.

<img width="612" alt="image" src="https://github.com/user-attachments/assets/0139661e-40b8-475e-b367-3a899228fe2e" />

결제 완료 페이지로 결제 승인 결과를 받습니다.

<img width="612" alt="image" src="https://github.com/user-attachments/assets/6db75f1a-006c-4b96-aec2-227509009975" />
<img width="612" alt="image" src="https://github.com/user-attachments/assets/7671189a-4730-4ac5-a655-9f9ee037e978" />

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


