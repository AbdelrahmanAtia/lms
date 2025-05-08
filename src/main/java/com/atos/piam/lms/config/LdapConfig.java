package com.atos.piam.lms.config;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdapConfig {

    @Value("${ldap.url}")
    private String ldapUrl;
    
    @Value("${ldap.username}")
    private String bindDn;

    @Value("${ldap.password}")
    private String bindPassword;

    @Bean
    public LDAPConnectionPool ldapConnectionPool() throws LDAPException {
        // Parse the LDAP URL to extract host and port
        String[] urlParts = ldapUrl.replace("ldap://", "").split(":");
        String host = urlParts[0];
        int port = Integer.parseInt(urlParts[1]);

        // Create a single LDAP connection
        LDAPConnection connection = new LDAPConnection(host, port, bindDn, bindPassword);

        // Create a connection pool (e.g., 10 connections)
        return new LDAPConnectionPool(connection, 10);
    }
}