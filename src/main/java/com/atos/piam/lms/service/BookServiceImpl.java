package com.atos.piam.lms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.atos.piam.lms.exception.InvalidInputException;
import com.atos.piam.lms.exception.NotFoundException;
import com.atos.piam.lms.repository.LdapRepository;
import com.atos.piam.lms.service.dto.Book;
import com.atos.piam.lms.utils.LdapUtils;
import com.unboundid.ldap.sdk.Entry;

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

	private String buildBookDn(String isbn) {
		return "cn=" + isbn + "," + "ou=books," + baseDn;
	}

}
