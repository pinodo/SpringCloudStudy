import axios from "axios";
import keycloak from "./keycloak";

const api = axios.create({
  baseURL: "http://localhost:8090",
});

api.interceptors.request.use(async (config) => {
  if (keycloak.authenticated) {
    // 토큰 만료 30초 전이면 백그라운드 갱신(Silent Refresh) 수행
    await keycloak.updateToken(30);
    config.headers.Authorization = `Bearer ${keycloak.token}`;
  }
  return config;
});

export default api;
