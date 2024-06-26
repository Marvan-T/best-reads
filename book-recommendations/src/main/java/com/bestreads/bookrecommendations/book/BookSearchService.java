package com.bestreads.bookrecommendations.book;

import com.bestreads.bookrecommendations.googlebooks.GoogleBooksService;
import com.bestreads.bookrecommendations.utils.SearchTermUtils;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookSearchService {

  private final GoogleBooksService googleBooksService;
  private final HttpResponseToBook httpResponseToBook;

  @Autowired
  public BookSearchService(GoogleBooksService googleBooksService,
      HttpResponseToBook httpResponseToBook) {
    this.googleBooksService = googleBooksService;
    this.httpResponseToBook = httpResponseToBook;
  }

  public List<Book> searchByTitle(String searchTerm, int maxResults) {
    return searchByTitle(searchTerm, 0, maxResults);
  }

  public List<Book> searchByTitle(String searchTerm, int startIndex, int maxResults) {
    if (maxResults < 0 || maxResults > 40) {
      throw new IllegalArgumentException("maxResults should be between 0 and 40");
    }

    HttpResponse<String> httpResponse = googleBooksService.searchVolumeByTitle(
        SearchTermUtils.encodeURLTerm(searchTerm),
        startIndex,
        maxResults
    );

    return httpResponseToBook.extractFromHttpResponse(httpResponse);
  }

  public List<Book> searchByAuthor(String searchTerm, int maxResults) {
    return searchByAuthor(searchTerm, 0, maxResults);
  }

  public List<Book> searchByAuthor(String searchTerm, int startIndex, int maxResults) {
    HttpResponse<String> httpResponse = googleBooksService.searchVolumeByAuthor(
        SearchTermUtils.encodeURLTerm(searchTerm),
        startIndex,
        maxResults
    );

    return httpResponseToBook.extractFromHttpResponse(httpResponse);
  }

  /**
   * This method performs the following tasks: 1. Makes a request to the Google Books API using the
   * provided ISBN to gather data related to the book. 2. If no book data is found based on the
   * ISBN, it initiates another search using the title and author of the book. 3. If the book data
   * is still not found, it leads to an `IllegalArgumentException`.
   */
  public Book getBookData(String isbn, String title, String authors) {
    List<Book> books = getBooksFromResponse(googleBooksService.searchVolumeByIsbn(
        SearchTermUtils.encodeURLTerm(isbn),
        0,
        1
    ), title);

    if (!books.isEmpty()) {
      return books.get(0);
    } else {
      return searchByTitleAndAuthor(title, authors);
    }
  }

  private Book searchByTitleAndAuthor(String title, String authors) {
    //when doing an ISBN search, vue pass the title and authors as "undefined"
    if (title.equals("undefined") && authors.equals("undefined")) {
      throw new IllegalArgumentException(
          "No book found with title: %s and author: %s".formatted(title, authors));
    }

    List<Book> books = getBooksFromResponse(googleBooksService.searchVolumeByTitleAndAuthors(
        SearchTermUtils.encodeURLTerm(title),
        SearchTermUtils.encodeURLTerm(authors)
    ), title);

    if (books.isEmpty()) {
      throw new IllegalArgumentException(
          "No book found with title: %s and author: %s".formatted(title, authors));
    }

    return books.get(0);
  }

  private List<Book> getBooksFromResponse(HttpResponse<String> response, String title) {
    List<Book> books = httpResponseToBook.extractFromHttpResponse(response);
    return books.isEmpty() || !isResultValid(books.get(0), title) ? Collections.emptyList() : books;
  }

  private boolean isResultValid(Book book, String title) {
    //when titles are not available, vue pass them as "undefined"
    //this method can be called with no title (search by isbn from search)
    //partial match done as the results from google books api often have extra words (Graphic Books and Manga category)
    return title.equals("undefined") || book.title().toLowerCase().contains(title.toLowerCase());
  }

}
