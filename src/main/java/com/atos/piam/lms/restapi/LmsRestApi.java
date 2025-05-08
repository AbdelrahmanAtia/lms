package com.atos.piam.lms.restapi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.atos.piam.lms.restapi.apidto.BookApiDto;
import com.atos.piam.lms.restapi.mapper.BookApiDtoMapper;
import com.atos.piam.lms.service.BookService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class LmsRestApi {

	@Autowired
	private BookService bookService;
	
	@Autowired
	private BookApiDtoMapper mapper;

	@PostMapping("/books/add")
	public void addBook(@RequestBody BookApiDto bookApiDto) {
		log.info("recieved a rest request to create a new book with details: {}", bookApiDto);
		bookService.createBook(mapper.toDto(bookApiDto));
	}

	@GetMapping("/books")
	public List<BookApiDto> getAllBooks() {

		return null;
	}

	@PostMapping("/books/loan")
	public void loanBook() {

	}

}
