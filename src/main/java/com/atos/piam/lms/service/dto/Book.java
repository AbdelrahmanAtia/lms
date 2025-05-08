package com.atos.piam.lms.service.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Book {
	
	private String title;
	private String author;
	private LocalDate publicationDate;
	private String publisher;
	private String language;
	private int quantity; //number of copies
	private boolean availability;
}
