import axios from 'axios';

export const axiosApi = axios.create({
  baseURL: process.env.REACT_APP_BACKEND_URL,
  withCredentials: true
});

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
