/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.net.ssl.*;

/**
 * Shared JSSE utilities for certificate endpoint identities, key stores and key/trust managers.
 * <p>Certificate identity matching uses subject alternative names and Cobble's DNS normalization.
 * Callers establish certificate-chain trust separately and control when verification runs in their
 * connection lifecycle.
 */
public final class JsseUtils {
    private JsseUtils() {
    }

    // Endpoint identity verification

    /**
     * Verifies the peer certificate identity of a client engine configured for HTTPS.
     * Call after the TLS handshake completes and before exposing the connection to application code.
     * Other engine modes and endpoint identification algorithms are ignored.
     * Certificate-chain validation remains the responsibility of the engine's trust manager.
     */
    public static void verifyHttpsEndpointIdentity(SSLEngine engine) throws SSLException {
        if (!engine.getUseClientMode() || !StringUtils.equalsIgnoreCase("HTTPS", engine.getSSLParameters().getEndpointIdentificationAlgorithm())) {
            return;
        }

        String host = engine.getPeerHost();
        boolean ip = isIpHost(host);
        Certificate[] certificates = engine.getSession().getPeerCertificates();
        if (certificates.length > 0 && certificates[0] instanceof X509Certificate) {
            try {
                if (matchesCertificateIdentity((X509Certificate) certificates[0], host)) {
                    return;
                }
            } catch (CertificateParsingException | IOException e) {
                SSLHandshakeException failure = new SSLHandshakeException("Invalid HTTPS subject alternative names");
                failure.initCause(e);
                throw failure;
            }
        }

        throw new SSLHandshakeException("No HTTPS " + (ip ? "IP" : "DNS") + " subject alternative name matches " + host);
    }

    /**
     * Matches a reference DNS name or IP literal against certificate subject alternative names.
     * DNS wildcards cover one complete leftmost label; IP identities match encoded address octets.
     * This performs no DNS lookup, chain/trust validation, validity check or CN fallback.
     * Callers must establish certificate trust separately before using a match for authorization.
     * Unicode reference names use Cobble's nontransitional IDNA conversion.
     */
    public static boolean matchesCertificateIdentity(X509Certificate certificate, String host) throws IOException, CertificateParsingException {
        Objects.requireNonNull(certificate, "certificate");
        if (isIpHost(host)) {
            return matchesIp(host, certificate);
        }

        Collection<List<?>> names = certificate.getSubjectAlternativeNames();
        if (names != null) {
            for (List<?> name : names) {
                if (name.size() >= 2 && Integer.valueOf(2).equals(name.get(0)) && name.get(1) instanceof String && matchesDns(host, (String) name.get(1))) {
                    return true;
                }
            }
        }

        return false;
    }

    // IP literal classification and matching

    /**
     * Classifies a reference host for IP identity checks and SNI selection without name resolution.
     * Numeric hosts and hosts containing a colon take the IP path even when malformed, preventing
     * an invalid IP reference from falling back to DNS certificate matching.
     * This is a classification helper, not a check that an IP address is valid.
     */
    public static boolean isIpHost(String host) {
        if (StringUtils.isEmpty(host)) {
            return false;
        }

        if (host.indexOf(':') >= 0) {
            return true;
        }

        for (int i = 0; i < host.length(); i++) {
            char c = host.charAt(i);
            if (c != '.' && (c < '0' || c > '9')) {
                return false;
            }
        }

        return true;
    }

    private static boolean matchesIp(String host, X509Certificate certificate) throws IOException {
        byte[] reference = ipBytes(host);
        if (reference == null) {
            return false;
        }

        // SAN text can collapse mapped IPv6 to IPv4. Match the certificate's encoded octets instead.
        byte[] encoded = new byte[reference.length + 2];
        encoded[0] = 0x04; // DER OCTET STRING; IP addresses always fit a short-form length.
        encoded[1] = (byte) reference.length;
        System.arraycopy(reference, 0, encoded, 2, reference.length);
        X509CertSelector selector = new X509CertSelector();
        selector.addSubjectAlternativeName(7, encoded);
        return selector.match(certificate);
    }

    private static byte[] ipBytes(String host) {
        if (!isIpHost(host)) {
            return null;
        }

        if (host.indexOf(':') >= 0) {
            if (host.startsWith("[") && host.endsWith("]")) {
                host = host.substring(1, host.length() - 1);
            }

            try {
                // URI validates IPv6 syntax before InetAddress, so this never resolves a DNS name.
                new URI("https://[" + host + "]/").parseServerAuthority();
                byte[] address = InetAddress.getByName(host).getAddress();
                if (address.length == 4) {
                    byte[] mapped = new byte[16];
                    mapped[10] = mapped[11] = (byte) 0xff;
                    System.arraycopy(address, 0, mapped, 12, 4);
                    return mapped;
                }
                return address;
            } catch (UnknownHostException | URISyntaxException e) {
                return null;
            }
        }

        byte[] address = new byte[4];
        int part = 0, value = 0, digits = 0;
        for (int i = 0; i <= host.length(); i++) {
            char c = i == host.length() ? '.' : host.charAt(i);
            if (c == '.') {
                if (digits == 0 || part == 4) {
                    return null;
                }
                address[part++] = (byte) value;
                value = digits = 0;
            } else {
                if (digits == 3 || (digits > 0 && value == 0)) {
                    return null;
                }
                value = value * 10 + c - '0';
                if (value > 255) {
                    return null;
                }
                digits++;
            }
        }

        return part == 4 ? address : null;
    }

    // DNS identity matching

    private static boolean matchesDns(String host, String presented) {
        if (isIpHost(host) || presented == null) {
            return false;
        }

        for (int i = 0; i < presented.length(); i++) {
            if (presented.charAt(i) > 0x7f) {
                return false;
            }
        }

        String reference = dnsName(host);
        boolean wildcard = presented.startsWith("*.");
        String suffix = dnsName(wildcard ? presented.substring(2) : presented);
        if (reference == null || suffix == null) {
            return false;
        }

        if (!wildcard) {
            return StringUtils.equals(reference, suffix);
        }

        int firstDot = reference.indexOf('.');
        return firstDot > 0 && StringUtils.equals(reference.substring(firstDot + 1), suffix);
    }

    private static String dnsName(String name) {
        try {
            return StringUtils.removeEnd(NetworkUtils.toDNSAscii(name), ".");
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // Key stores and manager factories

    /**
     * Adapts existing trust managers to a factory without replacing their trust decisions.
     * The manager array is copied on input and output; the manager instances are retained.
     * Factory initialization is a no-op because the supplied managers are already configured.
     */
    public static TrustManagerFactory wrapTrustManagers(TrustManager... trustManagers) {
        TrustManager[] managers = Objects.requireNonNull(trustManagers, "trustManagers").clone();
        for (TrustManager manager : managers) {
            Objects.requireNonNull(manager, "trustManager");
        }
        TrustManagerFactorySpi spi = new TrustManagerFactorySpi() {
            @Override
            protected void engineInit(KeyStore keyStore) {
            }

            @Override
            protected void engineInit(ManagerFactoryParameters parameters) {
            }

            @Override
            protected TrustManager[] engineGetTrustManagers() {
                return managers.clone();
            }
        };
        Provider provider = new Provider("CobbleJSSE", "1.0", "Factory for preconfigured trust managers") {
        };
        return new TrustManagerFactory(spi, provider, "provided") {
        };
    }

    /** Initializes the supplied key manager factory, or creates one using the JDK default algorithm. */
    public static KeyManagerFactory buildKeyManagerFactory(KeyStore keyStore, char[] passwordChars, KeyManagerFactory kmf) throws GeneralSecurityException, IOException {
        // Set up key manager factory to use our key store
        if (kmf == null) {
            String keyAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
            kmf = KeyManagerFactory.getInstance(keyAlgorithm);
        }
        kmf.init(keyStore, passwordChars);
        return kmf;
    }

    /** Initializes the supplied trust manager factory, or creates one using the JDK default algorithm. */
    public static TrustManagerFactory buildTrustManagerFactory(KeyStore keyStore, TrustManagerFactory tmf) throws GeneralSecurityException, IOException {
        // Set up trust manager factory to use our key store.
        if (tmf == null) {
            String keyAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            tmf = TrustManagerFactory.getInstance(keyAlgorithm);
        }
        tmf.init(keyStore);

        return tmf;
    }

    /**
     * Initializes the supplied key store with the certificate chain and private key.
     * @param certChain a X.509 certificate chain
     * @param key a PKCS#8 private key
     * @param passwordChars the password of the private key. {@code null} if it's not password-protected.
     */
    public static void loadKeyStore(KeyStore keyStore, X509Certificate[] certChain, PrivateKey key, char[] passwordChars) throws GeneralSecurityException, IOException {
        Objects.requireNonNull(certChain, "required certChain");
        Objects.requireNonNull(key, "required key");
        keyStore.load(null, null);

        int i = 1;
        for (X509Certificate cert : certChain) {
            keyStore.setCertificateEntry(("CRT_" + i), cert);
            i++;
        }
        keyStore.setKeyEntry("key", key, passwordChars, certChain);
    }

    /**
     * Loads the supplied key store from a stream using its configured store type.
     * @param keyStoreInputStream encoded key store, or null to initialize an empty store
     * @param passwordChars the key store password
     */
    public static void loadKeyStore(KeyStore keyStore, InputStream keyStoreInputStream, char[] passwordChars) throws GeneralSecurityException, IOException {
        keyStore.load(keyStoreInputStream, passwordChars);
    }
}
