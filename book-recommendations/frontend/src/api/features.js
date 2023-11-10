import axios from "axios";

export async function getRecommendationsStatus() {
  const result = await axios.get("/api/features/recommendations/status");
  return result.data;
}
