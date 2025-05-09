package com.atos.piam.lms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.atos.piam.lms.common.AvailabilityStatus;
import com.atos.piam.lms.common.BookSearchCriteria;
import com.atos.piam.lms.common.Constants;
import com.atos.piam.lms.exception.InvalidInputException;
import com.atos.piam.lms.exception.NotFoundException;
import com.atos.piam.lms.repository.LdapRepository;
import com.atos.piam.lms.service.dto.Book;
import com.atos.piam.lms.utils.LdapUtils;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.SearchResultEntry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

	private final String baseDn;
	private final LdapRepository ldapRepository;

	public BookServiceImpl(@Value("${ldap.base.dn}") String baseDn, LdapRepository ldapRepository) {
		this.baseDn = baseDn;
		this.ldapRepository = ldapRepository;
	}

	@Override
	public void createBook(Book book) {

		String dn = buildBookDn(book.getIsbn()); // distinguished name of the book entry
		log.info("dn: {}", dn);

		if (ldapRepository.isEntryExistsByDn(dn)) {
			throw new InvalidInputException("A book with isbn " + book.getIsbn() + " already exists.");
		}

		Entry bookEntry = new Entry(dn);
		fillEntryAttributes(bookEntry, book);

		ldapRepository.addEntry(bookEntry);

	}

	@Override
	public void updateBook(Book book) {
		log.info("updated book details: {}", book);

		String dn = buildBookDn(book.getIsbn()); // using ISBN as entry CN
		log.info("dn: {}", dn);

		if (!ldapRepository.isEntryExistsByDn(dn)) {
			throw new NotFoundException("Book with isbn " + book.getIsbn() + " not exist");
		}

		// create a new entry to get the updated attributes
		Entry updateEntry = new Entry(dn);
		fillEntryAttributes(updateEntry, book);

		ldapRepository.updateEntry(updateEntry);
	}

	@Override
	public void deleteBook(String isbn) {

		String dn = buildBookDn(isbn);
		log.info("dn: {}", dn);

		if (!ldapRepository.isEntryExistsByDn(dn)) {
			throw new NotFoundException("Book with ISBN '" + isbn + "' not found");
		}

		ldapRepository.deleteEntryByDn(dn);
	}
	
	@Override
	public List<Book> searchBooks(BookSearchCriteria criteria) {
		
		String booksBaseDn = "ou=books," + baseDn;
		String filter = buildSearchFilter(criteria);

		List<SearchResultEntry> entries = ldapRepository.searchEntries(booksBaseDn, filter,
				Constants.BOOK_ENTRY_ATTRIBUTES);
		
		log.info("{} entries found", entries.size());

		return entries.stream().map(this::convertToBook).collect(Collectors.toList());
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
	
	private String buildSearchFilter(BookSearchCriteria criteria) {
		List<String> filters = new ArrayList<>();

		filters.add("(objectClass=librarybook)"); //TODO: refer to librarybook from constants class

		if (criteria.getIsbn() != null) {
			filters.add("(isbn=" + LdapUtils.escapeForFilter(criteria.getIsbn()) + ")");
		}
		if (criteria.getTitle() != null) {
			filters.add("(bookTitle=*" + LdapUtils.escapeForFilter(criteria.getTitle()) + "*)");
		}
		if (criteria.getAuthor() != null) {
			filters.add("(author=*" + LdapUtils.escapeForFilter(criteria.getAuthor()) + "*)");
		}
		if (criteria.getPublisher() != null) {
			filters.add("(publisher=*" + LdapUtils.escapeForFilter(criteria.getPublisher()) + "*)");
		}
		if (criteria.getPublicationDate() != null) {
			filters.add("(publicationDate=" + LdapUtils.toGeneralizedTime(criteria.getPublicationDate()) + ")");
		}
		if (criteria.getLanguage() != null) {
			filters.add("(language=" + LdapUtils.escapeForFilter(criteria.getLanguage()) + ")");
		}
		if (criteria.getAvailability() != null) {
			filters.add("(availability=" + criteria.getAvailability() + ")");
		}

		if (filters.size() == 1) {
			return filters.get(0);
		}

		return "(&" + String.join("", filters) + ")";
	}
	  
	private Book convertToBook(SearchResultEntry entry) {

		Book book = new Book();
		book.setIsbn(entry.getAttributeValue("isbn"));
		book.setTitle(entry.getAttributeValue("bookTitle"));
		book.setAuthor(entry.getAttributeValue("author"));
		book.setPublisher(entry.getAttributeValue("publisher"));
		book.setLanguage(entry.getAttributeValue("language"));

		String publicationDate = entry.getAttributeValue("publicationDate");
		if (publicationDate != null) {
			book.setPublicationDate(LdapUtils.fromGeneralizedTime(publicationDate));
		}

		String availability = entry.getAttributeValue("availability");
		if (availability != null) {
			book.setAvailability(AvailabilityStatus.fromString(availability));
		}

		String quantity = entry.getAttributeValue("quantity");
		if (quantity != null) {
			book.setQuantity(Integer.parseInt(quantity));
		}

		return book;

	}

	private String buildBookDn(String isbn) {
		return "cn=" + isbn + "," + "ou=books," + baseDn;
	}

}
