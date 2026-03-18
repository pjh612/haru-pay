import './ApiDocsPage.css'

function ApiDocsPage() {
  return (
    <div className="api-docs">
      <div className="docs-sidebar">
        <nav className="docs-nav">
          <h3>목차</h3>
          <ul>
            <li><a href="#overview">개요</a></li>
            <li><a href="#authentication">인증</a></li>
            <li><a href="#prepare">결제 준비</a></li>
            <li><a href="#confirm">결제 확정</a></li>
            <li><a href="#status">결제 상태 조회</a></li>
            <li><a href="#sse">결제 결과 구독 (SSE)</a></li>
            <li><a href="#errors">에러 코드</a></li>
          </ul>
        </nav>
      </div>

      <div className="docs-content">
        <section id="overview">
          <h1>HaruPay API 문서</h1>
          <p>
            HaruPay API를 사용하여 간편하고 안전한 결제 기능을 통합하세요.
          </p>
          
          <h2>Base URL</h2>
          <code className="code-block">https://api.harupay.io/api</code>
          
          <h2>Content-Type</h2>
          <code className="code-block">application/json</code>
        </section>

        <section id="authentication">
          <h2>인증</h2>
          <p>모든 API 요청에는 다음 헤더가 필요합니다:</p>
          
          <table className="docs-table">
            <thead>
              <tr>
                <th>헤더</th>
                <th>설명</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td><code>Authorization</code></td>
                <td>API 키 (Bearer 없이)</td>
              </tr>
              <tr>
                <td><code>X-PAY-CLIENT-ID</code></td>
                <td>클라이언트 ID</td>
              </tr>
            </tbody>
          </table>
        </section>

        <section id="prepare">
          <h2>결제 준비</h2>
          <div className="endpoint">
            <span className="method post">POST</span>
            <code>/payment/prepare</code>
          </div>
          
          <h3>요청 본문</h3>
          <pre className="code-block">{`{
  "orderId": "ORDER-12345",
  "requestPrice": 10000,
  "productName": "상품명",
  "idempotencyKey": "unique-key-123"
}`}</pre>
          
          <h3>응답</h3>
          <pre className="code-block">{`{
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "orderId": "ORDER-12345",
  "requestPrice": 10000,
  "clientId": "550e8400-e29b-41d4-a716-446655440001",
  "paymentStatus": 0,
  "createdAt": "2024-01-15T10:30:00Z"
}`}</pre>
        </section>

        <section id="confirm">
          <h2>결제 확정</h2>
          <div className="endpoint">
            <span className="method post">POST</span>
            <code>/payment/confirm</code>
          </div>
          
          <h3>요청 본문</h3>
          <pre className="code-block">{`{
  "paymentId": "550e8400-e29b-41d4-a716-446655440000",
  "idempotencyKey": "confirm-key-123"
}`}</pre>
        </section>

        <section id="status">
          <h2>결제 상태 조회</h2>
          <div className="endpoint">
            <span className="method get">GET</span>
            <code>/payment/{"{paymentId}"}</code>
          </div>
          
          <h3>응답</h3>
          <pre className="code-block">{`{
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "orderId": "ORDER-12345",
  "requestPrice": 10000,
  "clientId": "550e8400-e29b-41d4-a716-446655440001",
  "paymentStatus": 1,
  "approvedAt": "2024-01-15T10:35:00Z"
}`}</pre>
        </section>

        <section id="sse">
          <h2>결제 결과 구독 (SSE)</h2>
          <p>
            Server-Sent Events(SSE)를 사용하여 결제 결과를 실시간으로 수신합니다.
            결제 확정 후 서버에서 결과를 푸시하므로 폴링 없이 즉시 알림을 받을 수 있습니다.
          </p>
          <div className="endpoint">
            <span className="method get">GET</span>
            <code>/payment-result/subscribe</code>
          </div>

          <h3>요청 파라미터</h3>
          <table className="docs-table">
            <thead>
              <tr>
                <th>파라미터</th>
                <th>위치</th>
                <th>필수</th>
                <th>설명</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td><code>paymentId</code></td>
                <td>Query</td>
                <td>필수</td>
                <td>구독할 결제 ID (UUID)</td>
              </tr>
              <tr>
                <td><code>Last-Event-ID</code></td>
                <td>Header</td>
                <td>선택</td>
                <td>연결 재시도 시 마지막으로 받은 이벤트 ID (누락 이벤트 재전송용)</td>
              </tr>
            </tbody>
          </table>

          <h3>응답 형식</h3>
          <p><code>Content-Type: text/event-stream</code></p>
          <pre className="code-block">{`event: payment-result
id: 550e8400-e29b-41d4-a716-446655440000
data: {
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "orderId": "ORDER-12345",
  "requestPrice": 10000,
  "clientId": "550e8400-e29b-41d4-a716-446655440001",
  "paymentStatus": 1,
  "approvedAt": "2024-01-15T10:35:00Z"
}`}</pre>

          <h3>JavaScript 예제</h3>
          <pre className="code-block">{`const paymentId = '550e8400-e29b-41d4-a716-446655440000';
const url = \`https://api.harupay.io/api/payment-result/subscribe?paymentId=\${paymentId}\`;

const eventSource = new EventSource(url);

eventSource.addEventListener('payment-result', (event) => {
  const result = JSON.parse(event.data);
  console.log('결제 결과:', result);
  eventSource.close();
});

eventSource.onerror = () => {
  console.error('SSE 연결 오류');
  eventSource.close();
};`}</pre>

          <h3>주의사항</h3>
          <ul>
            <li>SSE 연결에도 <code>Authorization</code> 및 <code>X-PAY-CLIENT-ID</code> 헤더가 필요합니다.</li>
            <li>결제 결과 수신 후 연결을 반드시 종료하세요.</li>
            <li>연결이 끊어진 경우 <code>Last-Event-ID</code> 헤더와 함께 재연결하면 누락된 이벤트를 재수신할 수 있습니다.</li>
          </ul>
        </section>

        <section id="errors">
          <h2>에러 코드</h2>
          <table className="docs-table">
            <thead>
              <tr>
                <th>코드</th>
                <th>설명</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>400</td>
                <td>잘못된 요청</td>
              </tr>
              <tr>
                <td>401</td>
                <td>인증 실패</td>
              </tr>
              <tr>
                <td>404</td>
                <td>결제 정보 없음</td>
              </tr>
              <tr>
                <td>409</td>
                <td>멱등성 키 중복</td>
              </tr>
              <tr>
                <td>422</td>
                <td>결제 확정 불가 상태</td>
              </tr>
            </tbody>
          </table>
        </section>
      </div>
    </div>
  )
}

export default ApiDocsPage
