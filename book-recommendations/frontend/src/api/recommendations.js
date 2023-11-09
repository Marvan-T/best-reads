import axios from "axios";

export async function getRecommendations(book) {
  const result = await axios.post("/api/extensions/BookRecommendations", book, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
  return result.data;
}
