package com.atos.piam.lms.service.dto;

import java.util.Date;

import lombok.Data;

@Data
public class Book {
	
	private String title;
	private String author;
	private Date publicationDate;
	private String publisher;
	private String language;
	private int quantity; //number of copies
	private boolean availability;
}
