package com.atos.piam.lms.repository;

import com.unboundid.ldap.sdk.Entry;

public interface LdapRepository {

	void addEntry(Entry bookEntry);

	void updateEntry(Entry bookUpdateEntry);

	void deleteEntryByDn(String isbn);

	boolean isEntryExistsByDn(String dn);

}