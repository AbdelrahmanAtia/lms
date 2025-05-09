package com.atos.piam.lms.restapi.apidto;

import java.time.LocalDate;

import com.atos.piam.lms.common.AvailabilityStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookApiDto {
    
	@NotBlank
	private String isbn; //international standard book number
	
	@NotBlank(message = "book title is required")
	private String title;
    
	private String author;
	
	private LocalDate publicationDate;
	
	private String publisher;
	
	private String language;
	
	@NotNull(message = "quantity field is required")
	private Integer quantity; //number of copies
	
	private AvailabilityStatus availability;
}
