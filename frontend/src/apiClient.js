import axios from "axios";
//not used
const apiClient = axios.create({
  baseURL: "http://spring-boot-app:8080", // Replace with your backend proxy server URL
});

export default apiClient;
