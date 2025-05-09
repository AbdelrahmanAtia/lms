package com.atos.piam.lms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atos.piam.lms.common.AvailabilityStatus;
import com.atos.piam.lms.exception.InvalidInputException;
import com.atos.piam.lms.repository.LdapRepository;
import com.atos.piam.lms.service.BookServiceImpl;
import com.atos.piam.lms.service.dto.Book;
import com.unboundid.ldap.sdk.Entry;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private LdapRepository ldapRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private String baseDn = "dc=example,dc=com";
    private String isbn = "1234567890";

    @BeforeEach
    void setUp() {
        // Initialize a sample book
        book = new Book();
        book.setIsbn(isbn);
        book.setTitle("Test Book");
        book.setAuthor("Author Name");
        book.setPublisher("Publisher");
        book.setPublicationDate(LocalDate.now());
        book.setLanguage("English");
        book.setQuantity(5);
        book.setAvailability(AvailabilityStatus.AVAILABLE);

        // Use reflection to set the private baseDn field
        try {
            var field = BookServiceImpl.class.getDeclaredField("baseDn");
            field.setAccessible(true);
            field.set(bookService, baseDn);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set baseDn", e);
        }

    }

    @Test
    void createBook_Success() {
        // Arrange
        when(ldapRepository.isEntryExistsByDn(anyString())).thenReturn(false);
        doNothing().when(ldapRepository).addEntry(any(Entry.class));

        // Act
        bookService.createBook(book);

        // Assert
        verify(ldapRepository).isEntryExistsByDn("cn=" + isbn + ",ou=books," + baseDn);
        verify(ldapRepository).addEntry(any(Entry.class));
    }

    @Test
    void createBook_BookAlreadyExists_ThrowsInvalidInputException() {
        // Arrange
        when(ldapRepository.isEntryExistsByDn(anyString())).thenReturn(true);

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> bookService.createBook(book));
        assertEquals("A book with isbn " + isbn + " already exists.", exception.getMessage());
        verify(ldapRepository).isEntryExistsByDn("cn=" + isbn + ",ou=books," + baseDn);
        verify(ldapRepository, never()).addEntry(any(Entry.class));
    }

   
}