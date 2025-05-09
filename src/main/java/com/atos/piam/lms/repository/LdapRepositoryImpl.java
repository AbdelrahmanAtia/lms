package com.atos.piam.lms.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.atos.piam.lms.common.Constants;
import com.atos.piam.lms.exception.LdapRepositoryException;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class LdapRepositoryImpl implements LdapRepository {

	private final LDAPConnectionPool ldapConnectionPool;

	public LdapRepositoryImpl(LDAPConnectionPool ldapConnectionPool) {
		this.ldapConnectionPool = ldapConnectionPool;
	}

	@Override
	public void addEntry(Entry bookEntry) {
		try (LDAPConnection connection = ldapConnectionPool.getConnection()) {
			connection.add(bookEntry);
			log.info("New book added successfully with dn: {}", bookEntry.getDN());
		} catch (LDAPException ex) {
			log.error("An error occured..");
			log.error(ex.getMessage(), ex);
			throw new LdapRepositoryException("Failed to add book entry", ex);
		}
	}

	@Override
	public void updateEntry(Entry bookUpdateEntry) {
		try (LDAPConnection connection = ldapConnectionPool.getConnection()) {

			List<Modification> modificationsList = new ArrayList<>();
			fillModificationsList(modificationsList, bookUpdateEntry);

			ModifyRequest modifyRequest = new ModifyRequest(bookUpdateEntry.getDN(), modificationsList);
			connection.modify(modifyRequest);

			log.info("Book updated successfully for dn: {}", bookUpdateEntry.getDN());
		} catch (LDAPException ex) {
			log.error("Error updating book: {}", ex.getMessage(), ex);
			throw new LdapRepositoryException("Failed to update book");
		}
	}

	@Override
	public void deleteEntryByDn(String bookEntryDn) {
		try (LDAPConnection connection = ldapConnectionPool.getConnection()) {
			connection.delete(bookEntryDn);
			log.info("Book deleted successfully with DN: {}", bookEntryDn);
		} catch (LDAPException ex) {
			log.error("Error deleting book: {}", ex.getMessage(), ex);
			throw new LdapRepositoryException("Failed to delete book");
		}
	}

	@Override
	public boolean isEntryExistsByDn(String dn) {
		try (LDAPConnection connection = ldapConnectionPool.getConnection()) {
			connection.search(dn, SearchScope.BASE, "(objectClass=*)", Constants.NO_ATTRIBUTES_OID);
			return true;
		} catch (LDAPException ex) {
			if (ex.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
				log.info("No book entry with dn: {} found", dn);
				return false;
			}
			log.error("Error checking DN existence for {}: {}", dn, ex.getMessage());
			throw new LdapRepositoryException("An error occured while searching for book entry");
		}
	}
	
	@Override
	public List<SearchResultEntry> searchEntries(String baseDn, String filter, String[] attributes) {
		log.info("searching for entries with the following details:-");
		log.info("baseDn: {}", baseDn);
		log.info("filter: {}", filter);
		
		try (LDAPConnection connection = ldapConnectionPool.getConnection()) {
			SearchRequest searchRequest = new SearchRequest(baseDn, SearchScope.SUB, filter, attributes);

			log.info("search request: {}", searchRequest);
			SearchResult searchResult = connection.search(searchRequest);

			List<SearchResultEntry> results = searchResult.getSearchEntries();
			
			return results == null ? new ArrayList<>() : results;

		} catch (LDAPException ex) {
			log.error("Error searching LDAP: {}", ex.getMessage(), ex);
			throw new LdapRepositoryException("Failed to search books");
		}
	}

	private void fillModificationsList(List<Modification> modificationsList, Entry updatedEntry) {
		for (Attribute attribute : updatedEntry.getAttributes()) {
			modificationsList
					.add(new Modification(ModificationType.REPLACE, attribute.getName(), attribute.getValues()));
		}
	}
}