import axios from 'axios';
import Cookies from 'js-cookie';

export const axiosApi = axios.create({
  baseURL: process.env.REACT_APP_BACKEND_URL,
  withCredentials: true
});

axiosApi.interceptors.request.use(
  config => {
    const csrfToken = Cookies.get('XSRF-TOKEN');

    if (csrfToken) {
      config.headers['X-XSRF-TOKEN'] = csrfToken;
    }

    return config;
  },
  error => {
    // Handle request error
    return Promise.reject(error);
  }
);

axiosApi.interceptors.response.use(
  response => response,
  error => {
    const { status } = error.response;

    if (status === 401 && !window.location.href.includes('/login')) {
      window.location.href = '/login';
    }

    return Promise.reject(error);
  }
);
