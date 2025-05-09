package com.atos.piam.lms.repository;

import java.util.List;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.SearchResultEntry;

public interface LdapRepository {

	void addEntry(Entry bookEntry);

	void updateEntry(Entry bookUpdateEntry);

	void deleteEntryByDn(String isbn);

	boolean isEntryExistsByDn(String dn);

	List<SearchResultEntry> searchEntries(String baseDn, String filter, String[] attributes);

}