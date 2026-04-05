import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  scenarios: {
    map_main_rps_10: {
      executor: 'constant-arrival-rate',
      rate: 10,
      timeUnit: '1s',
      duration: '5m',
      preAllocatedVUs: 10,
      maxVUs: 30,
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<1000'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://3.39.31.114';
const TARGET_URL = `${BASE_URL}/api/map/main`;

export default function () {
  const res = http.get(TARGET_URL, {
    headers: {
      Accept: 'application/json',
    },
    tags: {
      name: 'map-main',
    },
  });

  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  sleep(0.1);
}
