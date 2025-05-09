package com.atos.piam.lms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.atos.piam.lms.exception.EntryCreationException;
import com.atos.piam.lms.exception.InvalidInputException;
import com.atos.piam.lms.exception.NotFoundException;
import com.atos.piam.lms.service.dto.Book;
import com.atos.piam.lms.utils.LdapUtils;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchScope;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

	private static final String BOOK_ENTRY_OBJECT_CLASS = "librarybook";
	private final LDAPConnectionPool ldapConnectionPool;
	private final String baseDn;

	public BookServiceImpl(LDAPConnectionPool ldapConnectionPool, @Value("${ldap.base.dn}") String baseDn) {
		this.ldapConnectionPool = ldapConnectionPool;
		this.baseDn = baseDn;
	}

	@Override
	public void createBook(Book book) {
		try (LDAPConnection connection = ldapConnectionPool.getConnection()) {

			String dn = buildBookDn(book.getIsbn()); // distinguished name of the book entry
			log.info("dn: {}", dn);
			
			if (isBookEntryExist(connection, dn)) {
				throw new InvalidInputException("A book with isbn " + book.getIsbn() + " already exists.");
			}

			Entry entry = new Entry(dn);
			fillEntryAttributes(entry, book);

			connection.add(entry);
			log.info("New book added successfully with dn: {}", dn);
		} catch (LDAPException ex) {
			log.error("An error occured..");
			log.error(ex.getMessage(), ex);
			throw new EntryCreationException("Failed to create book", ex);
		}
	}

	@Override
	public void updateBook(Book book) {
		log.info("updated book details: {}", book);
		try (LDAPConnection connection = ldapConnectionPool.getConnection()) {

			String dn = buildBookDn(book.getIsbn()); //using ISBN as entry CN
			log.info("dn: {}", dn);
			
			if (!isBookEntryExist(connection, dn)) {
				throw new NotFoundException("Book with isbn " + book.getIsbn() + " not exist");
			}

			// create a new entry to get the updated attributes
			Entry updatedEntry = new Entry(dn);
			fillEntryAttributes(updatedEntry, book);

			List<Modification> modificationsList = new ArrayList<>();
			fillModificationsList(modificationsList, updatedEntry);

			// create modify request
			ModifyRequest modifyRequest = new ModifyRequest(dn, modificationsList);
			connection.modify(modifyRequest);

			log.info("Book updated successfully for dn: {}", dn);

		} catch (LDAPException ex) {
			log.error("Error updating book: {}", ex.getMessage(), ex);
			throw new RuntimeException("Failed to update book", ex);
		}
	}
	
	@Override
	public void deleteBook(String isbn) {
	    try (LDAPConnection connection = ldapConnectionPool.getConnection()) {
	        
	    	String dn = buildBookDn(isbn);
	        log.info("dn: {}", dn);
	        
	        if (!isBookEntryExist(connection, dn)) {
	            throw new NotFoundException("Book with ISBN '" + isbn + "' not found");
	        }

	        connection.delete(dn);
	        log.info("Book deleted successfully with DN: {}", dn);
	    } catch (LDAPException ex) {
	        log.error("Error deleting book with ISBN {}: {}", isbn, ex.getMessage(), ex);
	        throw new RuntimeException("Failed to delete book", ex);
	    }
	}

	private boolean isBookEntryExist(LDAPConnection connection, String dn) {
		try {
			connection.search(dn, SearchScope.BASE, "(objectClass=*)", "1.1");
			return true;
		} catch (LDAPException ex) {
			if (ex.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
				log.info("No book entry with dn: {} found", dn);
				return false;
			}
			log.error("Error checking DN existence for {}: {}", dn, ex.getMessage());
			throw new RuntimeException(ex);
		}
	}

	private void fillEntryAttributes(Entry entry, Book book) {

		// set mandatory attributes
		entry.addAttribute("objectClass", "top", "librarybook");
		entry.addAttribute("isbn", book.getIsbn());
		entry.addAttribute("bookTitle", book.getTitle());
		entry.addAttribute("quantity", book.getQuantity().toString());

		// set optional attributes
		if (book.getAuthor() != null)
			entry.addAttribute("author", book.getAuthor());

		if (book.getPublisher() != null)
			entry.addAttribute("publisher", book.getPublisher());

		if (book.getPublicationDate() != null)
			entry.addAttribute("publicationDate", LdapUtils.toGeneralizedTime(book.getPublicationDate()));

		if (book.getLanguage() != null)
			entry.addAttribute("language", book.getLanguage());

		if (book.getAvailability() != null)
			entry.addAttribute("availability", book.getAvailability().toString());
	}

	private void fillModificationsList(List<Modification> modificationsList, Entry updatedEntry) {
		// fill modifications list from the updated entry
		for (Attribute attribute : updatedEntry.getAttributes()) {
			modificationsList
					.add(new Modification(ModificationType.REPLACE, attribute.getName(), attribute.getValues()));
		}
	}

	private String buildBookDn(String isbn) {
		return "cn=" + isbn + "," + "ou=books," + baseDn;
	}



}
