package com.bestreads.bookrecommendations.bookshelf;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bestreads.bookrecommendations.book.BookDAO;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(IndividualBookshelfController.class)
class IndividualBookshelfControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CollectionsService collectionsService;

  private final String userId = "123";

  BookDAO book = new BookDAO();

  Long collectionId = 1L;

  CollectionBookProjection collectionBookProjection = new CollectionBookProjection() {
    @Override
    public Long getId() {
      return collectionId;
    }

    @Override
    public String getName() {
      return "CollectionName";
    }

    @Override
    public Set<BookDAO> getBookDAOS() {
      return Set.of(book);
    }
  };

  CollectionBookProjection emptyCollectionBookProjection = new CollectionBookProjection() {
    @Override
    public Long getId() {
      return collectionId;
    }

    @Override
    public String getName() {
      return "CollectionName";
    }

    @Override
    public Set<BookDAO> getBookDAOS() {
      return Set.of();
    }
  };

  @BeforeEach
  void setup() {
    book.setId(1L);
    book.setTitle("Book");
    book.setAuthor("author");
    book.setIsbn("1234");
    book.setThumbnail("web.com");
    book.setPublishedDate(Date.from(Instant.parse("2017-01-01T00:00:00.00Z")));
  }

  @Test
  void getBooksInCollection_whenUnauthenticatedThen401() throws Exception {
    mockMvc.perform(get("/api/private/bookshelf/books/1"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getBooksInCollection() throws Exception {

    when(collectionsService.findByIdAndUser(anyLong(), any()))
        .thenReturn(Optional.of(collectionBookProjection));
    var expectedJson = """
        {
            "id": 1,
            "name": "CollectionName",
            "bookDAOS":
            [
              {
                "id": 1,
                "title": "Book",
                "author": "author",
                "isbn": "1234",
                "thumbnail": "web.com",
                "publishedDate": "2017-01-01T00:00:00.000+00:00"
              }
            ]
        }

        """;

    mockMvc.perform(get("/api/private/bookshelf/books?bookshelfId=1")
            .with(jwt().jwt((jwt) -> jwt.claim("sub", userId))))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void getBooksInCollection_whenNoBooksInCollection() throws Exception {

    when(collectionsService.findByIdAndUser(anyLong(), any()))
        .thenReturn(Optional.of(emptyCollectionBookProjection));
    var expectedJson = """
        {
        }
        """;

    mockMvc.perform(get("/api/private/bookshelf/books?bookshelfId=1")
            .with(jwt().jwt((jwt) -> jwt.claim("sub", userId))))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void updateCollectionName() throws Exception {
    mockMvc.perform(put("/api/private/bookshelf/books")
        .param("bookshelfId", "1")
        .param("newCollectionName", "Wishlist")
        .with(jwt().jwt((jwt) -> jwt.claim("sub", userId)))
        .with(csrf()));

    verify(collectionsService)
        .updateCollectionName(eq(1L), eq("Wishlist"));
  }
}
