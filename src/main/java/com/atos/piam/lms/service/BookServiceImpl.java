package com.atos.piam.lms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.atos.piam.lms.service.dto.Book;
import com.atos.piam.lms.utils.LdapUtils;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

	private final LDAPConnectionPool ldapConnectionPool;
	private final String baseDn;

	public BookServiceImpl(LDAPConnectionPool ldapConnectionPool, 
			@Value("${ldap.base.dn}") String baseDn) {

		this.ldapConnectionPool = ldapConnectionPool;
		this.baseDn = baseDn;
	}

	@Override
	public void createBook(Book book) {
		try (LDAPConnection connection = ldapConnectionPool.getConnection()) {

			Entry entry = new Entry(buildDn(book.getTitle()));
			
			//TODO: when u create a custom object class called libraryBook, u have to 
			//replace "document" with "libraryBook"
			
			//set mandatory attributes
			entry.addAttribute("objectClass", "top", "librarybook");
			entry.addAttribute("bookTitle", book.getTitle()); 
			entry.addAttribute("quantity",  book.getQuantity().toString()); 

			//set optional attributes
			if (book.getAuthor() != null)
				entry.addAttribute("author", book.getAuthor());
			
			if (book.getPublisher() != null)
				entry.addAttribute("publisher", book.getPublisher());
			
			if(book.getPublicationDate() != null) {
				entry.addAttribute("publicationDate", LdapUtils.toGeneralizedTime(book.getPublicationDate()));
			}
			
			if(book.getLanguage() != null) {
				entry.addAttribute("language", book.getLanguage());
			}			
						
			
			//if (book.getStatus() != null)
			//	entry.addAttribute("status", book.getStatus());

			connection.add(entry);
			
			log.info("New Book added successfully");
			
		} catch (LDAPException ex) {
			log.error("An error occured..");
			log.error(ex.getMessage(), ex);
			
			//TODO: use exception handler to handle
			// provide a better way..
			throw new RuntimeException(ex);  
			
		}
	}
	
    // Build DN for a book
    private String buildDn(String bookTitle) {
        return "cn=" + bookTitle + "," + "ou=books," + baseDn;
    }

}
