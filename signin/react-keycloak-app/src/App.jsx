import { useState, useEffect, useRef } from "react";
import keycloak from "./keycloak";
import api from "./api";

function App() {
  const [authenticated, setAuthenticated] = useState(false);
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const isRun = useRef(false);

  useEffect(() => {
    if (isRun.current) return;
    isRun.current = true;

    keycloak
      .init({
        onLoad: "check-sso",
        pkceMethod: "S256",
        checkLoginIframe: false,
      })
      .then((auth) => {
        setAuthenticated(auth);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Keycloak 초기화 오류:", err);
        setLoading(false);
      });
  }, []);

  const handleLogin = () => {
    keycloak.login();
  };

  const handleLogout = () => {
    keycloak.logout({ redirectUri: "http://localhost:5173" });
  };

  const fetchProfile = async () => {
    try {
      const response = await api.get("/api/users/me");
      setProfile(response.data);
    } catch (error) {
      console.error("API 호출 실패:", error);
      alert("프로필 조회 실패: " + (error.response?.status || error.message));
    }
  };

  if (loading) {
    return (
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          height: "100vh",
          fontSize: "1.1rem",
          color: "#495057",
        }}
      >
        Keycloak SSO 상태 확인 중...
      </div>
    );
  }

  return (
    <div
      style={{
        width: "100%",
        minHeight: "100vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        padding: "2rem 1rem",
      }}
    >
      <div
        style={{
          width: "100%",
          maxWidth: "720px",
          backgroundColor: "#ffffff",
          borderRadius: "12px",
          boxShadow: "0 4px 20px rgba(0, 0, 0, 0.08)",
          padding: "2.5rem 2rem",
        }}
      >
        {/* 제목 영역: clamp로 화면 확대 시 과도하게 커지는 현상 방지 */}
        <h1
          style={{
            fontSize: "clamp(1.3rem, 2.5vw, 1.75rem)",
            fontWeight: "700",
            textAlign: "center",
            color: "#1a1a1a",
            marginBottom: "1.2rem",
            lineHeight: 1.3,
          }}
        >
          React + Spring Boot 4.1 Resource Server
        </h1>
        <hr
          style={{
            border: "none",
            borderTop: "1px solid #e9ecef",
            marginBottom: "2rem",
          }}
        />

        {!authenticated ? (
          <div style={{ textAlign: "center", padding: "1rem 0" }}>
            <p
              style={{
                color: "#495057",
                fontSize: "1rem",
                marginBottom: "1.5rem",
              }}
            >
              현재 인증되지 않은 상태입니다. 로그인을 진행해 주세요.
            </p>
            <button
              onClick={handleLogin}
              style={{
                padding: "12px 28px",
                fontSize: "1rem",
                fontWeight: "600",
                cursor: "pointer",
                backgroundColor: "#0d6efd",
                color: "#fff",
                border: "none",
                borderRadius: "6px",
                transition: "background-color 0.2s ease",
              }}
            >
              Keycloak으로 로그인
            </button>
          </div>
        ) : (
          <div>
            <div style={{ textAlign: "center", marginBottom: "1.5rem" }}>
              <h3
                style={{
                  fontSize: "1.15rem",
                  color: "#198754",
                  fontWeight: "600",
                }}
              >
                ✓ 인증 성공:{" "}
                <span style={{ color: "#212529" }}>
                  {keycloak.tokenParsed?.preferred_username}
                </span>
                님 환영합니다.
              </h3>
            </div>

            {/* 버튼 중앙 정렬 영역 */}
            <div
              style={{
                display: "flex",
                justifyContent: "center",
                gap: "12px",
                flexWrap: "wrap",
                marginBottom: "2rem",
              }}
            >
              <button
                onClick={fetchProfile}
                style={{
                  padding: "10px 20px",
                  fontSize: "0.95rem",
                  fontWeight: "600",
                  cursor: "pointer",
                  backgroundColor: "#198754",
                  color: "#fff",
                  border: "none",
                  borderRadius: "6px",
                }}
              >
                내 프로필 조회 (Spring Boot API)
              </button>
              <button
                onClick={handleLogout}
                style={{
                  padding: "10px 20px",
                  fontSize: "0.95rem",
                  fontWeight: "600",
                  cursor: "pointer",
                  backgroundColor: "#dc3545",
                  color: "#fff",
                  border: "none",
                  borderRadius: "6px",
                }}
              >
                로그아웃
              </button>
            </div>

            {/* Access Token 영역 */}
            <div style={{ marginBottom: "1.5rem" }}>
              <label
                style={{
                  display: "block",
                  fontSize: "0.85rem",
                  fontWeight: "600",
                  color: "#495057",
                  marginBottom: "0.4rem",
                }}
              >
                클라이언트 보유 Access Token (JWT):
              </label>
              <textarea
                readOnly
                value={keycloak.token || ""}
                style={{
                  width: "100%",
                  height: "70px",
                  fontFamily: "SFMono-Regular, Consolas, monospace",
                  fontSize: "12px",
                  padding: "10px",
                  backgroundColor: "#f8f9fa",
                  border: "1px solid #dee2e6",
                  borderRadius: "6px",
                  resize: "none",
                  color: "#495057",
                  wordBreak: "break-all",
                }}
              />
            </div>

            {/* 프로필 응답 JSON 영역 */}
            {profile && (
              <div
                style={{
                  backgroundColor: "#f8f9fa",
                  padding: "1.2rem",
                  borderRadius: "6px",
                  border: "1px solid #dee2e6",
                }}
              >
                <h4
                  style={{
                    fontSize: "0.9rem",
                    fontWeight: "600",
                    color: "#495057",
                    marginBottom: "0.6rem",
                  }}
                >
                  Spring Boot 4.1 Resource Server 응답 데이터:
                </h4>
                <pre
                  style={{
                    margin: 0,
                    fontFamily: "SFMono-Regular, Consolas, monospace",
                    fontSize: "13px",
                    color: "#212529",
                    overflowX: "auto",
                  }}
                >
                  {JSON.stringify(profile, null, 2)}
                </pre>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
