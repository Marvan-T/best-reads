package com.bestreads.bookrecommendations.book;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookDAOService {

  private final BookDAORepository bookDAORepository;

  @Autowired
  public BookDAOService(BookDAORepository bookDAORepository) {
    this.bookDAORepository = bookDAORepository;
  }

  public Optional<BookDAO> findBookDAOByISBN(String isbn) {
    return bookDAORepository.findByIsbn(isbn);
  }

  @Transactional
  public BookDAO addNewBook(Book bookToAdd) {
    Optional<BookDAO> currentBook = bookDAORepository.findByIsbn(bookToAdd.isbn());
    if (currentBook.isEmpty()) {
      var newBook = new BookDAO();
      newBook.setIsbn(bookToAdd.isbn());
      newBook.setAuthor(String.join(", ", bookToAdd.authors()));
      newBook.setTitle(bookToAdd.title());

      if (bookToAdd.imageLinks() != null && bookToAdd.imageLinks().thumbnail() != null) {
        newBook.setThumbnail(bookToAdd.imageLinks().thumbnail());
      } else {
        newBook.setThumbnail(bookToAdd.thumbnail());
      }

      if (bookToAdd.googleBooksId() != null) {
        newBook.setGoogleBooksId(bookToAdd.googleBooksId());
      } else {
        newBook.setGoogleBooksId(bookToAdd.id());
      }

      try {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        newBook.setPublishedDate(format.parse(bookToAdd.publishedDate()));
      } catch (ParseException e) {
        try {
          newBook.setPublishedDate(new SimpleDateFormat("yyyy-MM-dd").parse("1900-01-01"));
        } catch (ParseException ex) {
          ex.printStackTrace();
        }
      }

      newBook.setGenre(String.join(", ", bookToAdd.categories()));
      newBook.setPublisher(bookToAdd.publisher());
      newBook.setDescription(bookToAdd.description());

      return bookDAORepository.save(newBook);
    } else {
      return currentBook.get();
    }
  }


}
