package com.bestreads.bookrecommendations.book;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "books")
public class BookDAO implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String title;

  @Column
  private String author;

  @Column
  private String thumbnail;

  @Column
  private Date publishedDate;

  @Column(unique = true, length = 20)
  private String isbn;

  @Column
  private String genre;

  @Column
  private String publisher;

  @Column(unique = true)
  private String googleBooksId;

  @Column(columnDefinition = "text")
  private String description;

  public BookDAO() {
  }

  public BookDAO(String title, String author, String thumbnail, Date publishedDate, String isbn,
      String genre, String publisher) {
    this.title = title;
    this.author = author;
    this.thumbnail = thumbnail;
    this.publishedDate = publishedDate;
    this.isbn = isbn;
    this.genre = genre;
    this.publisher = publisher;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }

  public Date getPublishedDate() {
    return publishedDate;
  }

  public void setPublishedDate(Date publishedDate) {
    this.publishedDate = publishedDate;
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  public String getGenre() {
    return genre;
  }

  public void setGenre(String genre) {
    this.genre = genre;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public String getGoogleBooksId() {
    return googleBooksId;
  }

  public void setGoogleBooksId(String googleBooksId) {
    this.googleBooksId = googleBooksId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookDAO bookDAO = (BookDAO) o;
    return Objects.equals(isbn, bookDAO.isbn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isbn);
  }

}
