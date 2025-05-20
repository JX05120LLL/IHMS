import axios from "axios"
import { getToken } from "@/utils/storage.js";
const URL_API = 'http://localhost:21090/api/personal-heath/v1.0'
const request = axios.create({
  baseURL: URL_API,
  timeout: 8000
});
//全局拦截器
request.interceptors.request.use(config => {
  const token = getToken();
  if (token !== null) {
    config.headers["token"] = token;
  }
  return config;
}, error => {
  return Promise.reject(error);
});
export default request;