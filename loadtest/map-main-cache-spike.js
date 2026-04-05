import http from 'k6/http';
import { check } from 'k6';

export const options = {
  scenarios: {
    cache_stampede_probe: {
      executor: 'per-vu-iterations',
      vus: Number(__ENV.VUS || 50),
      iterations: 1,
      maxDuration: __ENV.MAX_DURATION || '30s',
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<3000'],
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
      name: 'map-main-cache-spike',
      test_type: 'cache_stampede',
    },
  });

  check(res, {
    'status is 200': (r) => r.status === 200,
  });
}
