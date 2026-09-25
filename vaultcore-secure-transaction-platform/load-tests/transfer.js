import http from "k6/http";
import { check } from "k6";

const BASE_URL = __ENV.BASE_URL;

export const options = {
  vus: 50,
  duration: "30s",

  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<500"],
  },
};

export function setup() {
  const loginRes = http.post(
    `${BASE_URL}/api/v1/auth/login`,
    JSON.stringify({
      email: __ENV.SENDER_EMAIL,
      password: __ENV.SENDER_PASSWORD,
    }),
    {
      headers: {
        "Content-Type": "application/json",
      },
    }
  );

  check(loginRes, {
    "login successful": (r) => r.status === 200,
  });

  if (loginRes.status !== 200) {
    throw new Error(
      `Login failed: ${loginRes.status} ${loginRes.body}`
    );
  }

  return {
    token: loginRes.json("token"),
  };
}

export default function (data) {
  const res = http.post(
    `${BASE_URL}/api/v1/transactions/transfer`,
    JSON.stringify({
      receiverId: Number(__ENV.RECEIVER_ID),
      amount: 1,
    }),
    {
      headers: {
        Authorization: `Bearer ${data.token}`,
        "Content-Type": "application/json",
        "Idempotency-Key": `${__VU}-${__ITER}`,
      },
    }
  );

  const success = check(res, {
    "transfer successful": (r) => r.status === 200,
  });

  // Print only the first 5 failures
  if (!success && __ITER < 5) {
    console.log("================================");
    console.log(`VU: ${__VU}`);
    console.log(`ITER: ${__ITER}`);
    console.log(`STATUS: ${res.status}`);
    console.log(`BODY: ${res.body}`);
    console.log("================================");
  }
}