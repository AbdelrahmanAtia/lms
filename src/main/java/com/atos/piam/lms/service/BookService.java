package com.atos.piam.lms.service;

import java.util.List;

import com.atos.piam.lms.common.BookSearchCriteria;
import com.atos.piam.lms.service.dto.Book;

public interface BookService {

	void createBook(Book book);
	
	void updateBook(Book book);

	void deleteBook(String isbn);

	List<Book> searchBooks(BookSearchCriteria criteria);

}
