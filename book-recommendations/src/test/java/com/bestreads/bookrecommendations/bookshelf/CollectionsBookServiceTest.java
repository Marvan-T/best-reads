package com.bestreads.bookrecommendations.bookshelf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bestreads.bookrecommendations.book.Book;
import com.bestreads.bookrecommendations.book.BookDAO;
import com.bestreads.bookrecommendations.book.BookDAOService;
import com.bestreads.bookrecommendations.book.ImageLinks;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CollectionsBookServiceTest {

  @Mock
  private CollectionsRepository collectionsRepository;

  @Mock
  private BookDAOService bookDAOService;

  @InjectMocks
  private CollectionsBookService collectionsBookService;

  private CollectionProjection collectionProjectionOne;
  private CollectionProjection collectionProjectionTwo;

  @BeforeEach
  void setUp() {
    collectionProjectionOne = new CollectionProjection() {
      @Override
      public Long getId() {
        return 1L;
      }

      @Override
      public String getName() {
        return "Collection 1";
      }
    };

    collectionProjectionTwo = new CollectionProjection() {
      @Override
      public Long getId() {
        return 2L;
      }

      @Override
      public String getName() {
        return "Collection 2";
      }
    };

  }

  @Test
  void getCollectionsForBook() {
    String userId = "123";
    String isbn = "9783161484100";
    List<CollectionProjection> associatedCollections = List.of(collectionProjectionOne);
    List<CollectionProjection> unassociatedCollections = List.of(collectionProjectionTwo);

    when(collectionsRepository.findByUserIdAndBookDAOS_Isbn(userId, isbn))
        .thenReturn(associatedCollections);
    when(collectionsRepository.findUnassociatedCollectionsForIsbn(userId, isbn))
        .thenReturn(unassociatedCollections);

    LinkedHashSet<CollectionBookJson> expected = new LinkedHashSet<>(List.of(
        new CollectionBookJson(1L, "Collection 1", true),
        new CollectionBookJson(2L, "Collection 2", false)
    ));

    LinkedHashSet<CollectionBookJson> result = collectionsBookService.getCollectionsForBook(userId,
        isbn);
    assertEquals(expected, result);
  }

  @Test
  void getCollectionsForBook_noCollections() {
    String userId = "123";
    String isbn = "9783161484100";
    List<CollectionProjection> associatedCollections = Collections.emptyList();
    List<CollectionProjection> unassociatedCollections = Collections.emptyList();

    when(collectionsRepository.findByUserIdAndBookDAOS_Isbn(userId, isbn))
        .thenReturn(associatedCollections);
    when(collectionsRepository.findUnassociatedCollectionsForIsbn(userId, isbn))
        .thenReturn(unassociatedCollections);

    LinkedHashSet<CollectionBookJson> expected = new LinkedHashSet<>();

    LinkedHashSet<CollectionBookJson> result = collectionsBookService.getCollectionsForBook(userId,
        isbn);
    assertEquals(expected, result);
  }

  /**
   * This method is a bit complex hence the test covers few scenarios to accompany it. Behaviours
   * tested: - Creating a new collection, - adding a new book to the newly created collection. This
   * is tested while we already have an existing collection
   */
  @Test
  void updateCollectionsForBook_NewBookAndNewCollection() throws ParseException {
    // Set up test data
    String userId = "123";
    String isbn = "9783161484100";

    Book book = new Book(
        "1",
        "The Hitchhiker's Guide to the Galaxy",
        Arrays.asList("Douglas Adams"),
        "Pan Books",
        "1979-10-12",
        "A comedy sci-fi novel by Douglas Adams about Arthur Dent's escape from Earth.",
        180,
        Arrays.asList("Science Fiction", "Humor"),
        new ImageLinks("https://tinyurl.com/2x4j7v6y", "https://tinyurl.com/3f8z5w9a"),
        "en",
        4,
        12345,
        isbn
        , "https://tinyurl.com/2x4j7v6y", "213145gnase");

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Date publishedDate = format.parse(book.publishedDate());

    BookDAO newBookDAOForBook = new BookDAO(
        book.title(),
        String.join(", ", book.authors()),
        book.imageLinks().thumbnail(),
        publishedDate,
        book.isbn(),
        book.categories().toString(),
        book.publisher()
    );

    CollectionDAO existingCollection = new CollectionDAO(
        "Existing Collection",
        userId,
        new LinkedHashSet<>()
    );
    existingCollection.setId(2L);

    CollectionBookRootJson collectionBookRootJson = new CollectionBookRootJson(
        List.of(
            new CollectionBookJson(-1L, "New Collection", true),
            new CollectionBookJson(existingCollection.getId(), "Existing Collection", false)
        ),
        book
    );

    // Set up mock behavior
    Set<CollectionDAO> existingCollections = Set.of(existingCollection);
    when(collectionsRepository.findAllCollectionByUserId(userId)).thenReturn(existingCollections);

    when(bookDAOService.addNewBook(any())).thenAnswer(invocation -> {
      Object argument = invocation.getArgument(0);
      if (argument instanceof Book && ((Book) argument).isbn().equals(isbn)) {
        return newBookDAOForBook;
      }
      return argument;
    });

    when(collectionsRepository.saveAll(anyIterable())).thenAnswer(invocation -> {
      Object argument = invocation.getArgument(0);
      if (argument instanceof HashSet) {
        var receivedCollections = (HashSet<CollectionDAO>) argument;
        receivedCollections.stream().filter(c -> c.getId() == null)
            .findFirst().get().setId(3L);
      }
      return argument;
    });

    // Invoke the method under test
    Set<CollectionBookJson> result = collectionsBookService.updateCollectionsForBook(
        userId,
        collectionBookRootJson
    );

    // Verify the results
    ArgumentCaptor<Book> bookArg = ArgumentCaptor.forClass(Book.class);
    ArgumentCaptor<Set<CollectionDAO>> collectionArg = ArgumentCaptor.forClass(Set.class);

    verify(bookDAOService).addNewBook(bookArg.capture());
    verify(collectionsRepository).saveAll(collectionArg.capture());

    Set<CollectionBookJson> expected = new HashSet<>();
    expected.add(new CollectionBookJson(3L, "New Collection", true));
    expected.add(new CollectionBookJson(2L, "Existing Collection", false));

    assertEquals(expected, result);

    // Verify the book was saved
    Book savedBook = bookArg.getValue();
    assertEquals(book.title(), savedBook.title());

    // Verify the collections were saved
    Set<CollectionDAO> savedCollections = collectionArg.getValue();
    assertEquals(2, savedCollections.size());
    assertEquals("Existing Collection", savedCollections.stream().filter(c -> c.getId() == 2L)
        .findFirst().get().getName());
    assertEquals("New Collection", savedCollections.stream().filter(c -> c.getId() != 2L)
        .findFirst().get().getName());
  }
}
