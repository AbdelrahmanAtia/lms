package com.atos.piam.lms.service.dto;

import java.time.LocalDate;

import com.atos.piam.lms.common.AvailabilityStatus;

import lombok.Data;

@Data
public class Book {
	
	private String title;
	private String author;
	private LocalDate publicationDate;
	private String publisher;
	private String language;
	private Integer quantity; //number of copies
	private AvailabilityStatus availability;
}
