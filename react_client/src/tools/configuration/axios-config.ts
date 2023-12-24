import axios from 'axios';
import { ServerConstants } from '../../core/constants/ServerConstants';

export const axiosApi = axios.create({
  baseURL: ServerConstants.BACKEND_LOCAL_URL
});

axiosApi.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    if (error.response) {
      const { status } = error.response;
    }

    return Promise.reject(error);
  }
);
