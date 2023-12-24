import axios from 'axios';
import { ServerConstants } from '../../core/constants/ServerConstants';
import { useNavigate } from 'react-router';

export const axiosApi = axios.create({
  baseURL: ServerConstants.BACKEND_LOCAL_URL,
  withCredentials: true
});

axiosApi.interceptors.response.use(
  response => response,
  error => {
    const { status } = error.response;

    if (status === 401 || status === 403) {
      const navigate = useNavigate();
      if (window.location.href !== '/login') {
        navigate('/login');
      }
    }

    return Promise.reject(error);
  }
);
