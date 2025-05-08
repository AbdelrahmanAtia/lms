/*
 * package com.atos.piam.lms.service;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.ldap.core.LdapTemplate; import
 * org.springframework.ldap.support.LdapNameBuilder; import
 * org.springframework.stereotype.Service;
 * 
 * import com.atos.piam.lms.service.dto.Book;
 * 
 * import javax.naming.directory.Attributes;
 * 
 * import javax.naming.Name;
 * 
 * import javax.naming.directory.BasicAttributes; import
 * javax.naming.directory.BasicAttribute; import lombok.extern.slf4j.Slf4j;
 * 
 * @Slf4j
 * 
 * @Service public class BookServiceImplOld implements BookService {
 * 
 * @Autowired private LdapTemplate ldapTemplate;
 * 
 * // private static final String BASE_DN = "ou=books,dc=example,dc=com";
 * 
 * @Override public void createBook(Book book) {
 * log.info("starting to create a new book");
 * 
 * Name dn = LdapNameBuilder.newInstance().add("ou", "books").add("cn",
 * "How to play chess").build();
 * 
 * BasicAttribute objectClass = new BasicAttribute("objectClass");
 * objectClass.add("document"); objectClass.add("top");
 * 
 * Attributes attributes = new BasicAttributes(); attributes.put(objectClass);
 * attributes.put(new BasicAttribute("documentIdentifier", "3"));
 * attributes.put(new BasicAttribute("documentPublisher", "Bob fishcher"));
 * 
 * ldapTemplate.bind(dn, null, attributes); }
 * 
 * }
 */