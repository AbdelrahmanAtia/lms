package com.atos.piam.lms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import com.atos.piam.lms.service.dto.Book;
import com.atos.piam.lms.utils.LdapUtils;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResult;
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
			
			String dn = buildBookDn(book.getTitle()); //distinguished name of the book entry
			
			if(isBookEntryExist(connection, dn)) {
				throw new IllegalArgumentException("A book with the DN '" + dn + "' already exists.");
			}
			
			Entry entry = new Entry(dn);
			fillEntryAttributes(entry, book);
			
			connection.add(entry);			
			log.info("New Book added successfully");
		} catch (LDAPException ex) {
			log.error("An error occured..");
			log.error(ex.getMessage(), ex);

			// TODO: use exception handler to handle
			// provide a better way..
			throw new RuntimeException(ex);

		}
	}

	/*
	private boolean isBookEntryExist(LDAPConnection connection, String dn) throws LDAPSearchException {
		String filter = "(objectClass=*)";
		SearchResult searchResult = connection.search(dn, SearchScope.BASE, filter, "1.1");
		return searchResult.getEntryCount() > 0 ? true : false;
	}
	*/
	
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

	private String buildBookDn(String bookTitle) {
		return "cn=" + bookTitle + "," + "ou=books," + baseDn;
	}

}
