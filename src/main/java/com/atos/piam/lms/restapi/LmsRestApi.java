package com.atos.piam.lms.restapi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atos.piam.lms.common.BookSearchCriteria;
import com.atos.piam.lms.restapi.apidto.BookApiDto;
import com.atos.piam.lms.restapi.mapper.BookApiDtoMapper;
import com.atos.piam.lms.service.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Library Management System API", description = "API for managing books in the Library Management System")
public class LmsRestApi {

	@Autowired
	private BookService bookService;
	
	@Autowired
	private BookApiDtoMapper mapper;

	@PostMapping("/books")
    @Operation(summary = "Add a new book", description = "Creates a new book in the library system with the provided details.")
	public ResponseEntity<String> addBook(@Valid @RequestBody BookApiDto bookApiDto) {
		log.info("recieved a rest request to create a new book with details: {}", bookApiDto);
		bookService.createBook(mapper.toDto(bookApiDto));
		return ResponseEntity.ok("Book added successfully");
	}

	@PutMapping("/books")
    @Operation(summary = "Update an existing book", description = "Updates the details of an existing book identified by its ISBN.")	
	public ResponseEntity<String> updateBook(@Valid @RequestBody BookApiDto bookApiDto) {
		log.info("recieved a rest request to update book with title: {}", bookApiDto.getTitle());
		bookService.updateBook(mapper.toDto(bookApiDto));
		return ResponseEntity.ok("Book updated successfully");
	}
	
	@DeleteMapping("/books/{isbn}")
    @Operation(summary = "Delete a book", description = "Deletes a book from the library system by its ISBN.")	
	public ResponseEntity<String> deleteBook(@PathVariable String isbn) {
		log.info("recieved a rest request to delete book with isbn: {}", isbn);
		bookService.deleteBook(isbn);
		return ResponseEntity.ok("Book deleted successfully");
	}
	
	@PostMapping("/books/search")
    @Operation(summary = "Search books", description = "Searches for books based on the provided search criteria.")	
	public List<BookApiDto> searchBooks(@RequestBody BookSearchCriteria searchCriteria) {
		log.info("recieved a rest request to search for books with criteria: {}", searchCriteria);
		return mapper.toApiDto(bookService.searchBooks(searchCriteria));
	}

}
