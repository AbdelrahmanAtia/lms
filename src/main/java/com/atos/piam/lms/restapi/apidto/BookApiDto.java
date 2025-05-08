package com.atos.piam.lms.restapi.apidto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookApiDto {
    
	@NotBlank(message = "book title is required")
	private String title;
    
	private String author;
	
	private LocalDate publicationDate;
	
	private String publisher;
	
	private String language;
	
	@NotNull(message = "quantity field is required")
	private Integer quantity; //number of copies
	
	private boolean availability;
}
