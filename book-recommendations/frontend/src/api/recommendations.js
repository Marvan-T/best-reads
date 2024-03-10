import axios from "axios";

export async function getRecommendations(book) {
  const recommendationsUrl = "https://best-reads-extensionsv1.live/api/extensions/BookRecommendations"
  const result = await axios.post(recommendationsUrl, book, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
  return result.data;
}
