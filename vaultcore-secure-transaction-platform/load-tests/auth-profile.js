import http from "k6/http";
import { check } from "k6";

const BASE_URL = "http://localhost:8080";

export const options = {
  vus: 50,
  duration: "30s",

  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<200"],
  },
};

export function setup() {
  const payload = JSON.stringify({
    email: __ENV.LOAD_TEST_EMAIL,
    password: __ENV.LOAD_TEST_PASSWORD,
  });

  const params = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  const loginRes = http.post(`${BASE_URL}/api/v1/auth/login`, payload, params);

  check(loginRes, {
    "login successful": (r) => r.status === 200,
  });

  if (loginRes.status !== 200) {
    throw new Error(`Login failed: ${loginRes.status} ${loginRes.body}`);
  }

  const token = loginRes.json("token");

  return {
    token,
  };
}

export default function (data) {
  const res = http.get(`${BASE_URL}/api/v1/auth/me`, {
    headers: {
      Authorization: `Bearer ${data.token}`,
      "Content-Type": "application/json",
    },
  });

  check(res, {
    "status is 200": (r) => r.status === 200,
  });
}
