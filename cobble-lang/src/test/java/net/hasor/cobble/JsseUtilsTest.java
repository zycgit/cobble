/*
 * Copyright 2008-2026 the original author or authors.
 * Licensed under the Apache License, Version 2.0.
 */
package net.hasor.cobble;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import javax.net.ssl.*;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class JsseUtilsTest {
    private static final char[]          PASSWORD = "test-only".toCharArray();
    private static       KeyPair         identityKey;
    private static       X509Certificate certificate;

    @BeforeClass
    public static void createCertificate() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        identityKey = generator.generateKeyPair();
        certificate = certificate(//
                new GeneralName(GeneralName.dNSName, "www.example.com"),//
                new GeneralName(GeneralName.dNSName, "xn--bcher-kva.example"),//
                new GeneralName(GeneralName.iPAddress, "127.0.0.1"));
    }

    @Test
    public void matchesOnlyTheCertificatesDnsAndIpIdentities() throws Exception {
        assertTrue(JsseUtils.matchesCertificateIdentity(certificate, "WWW.Example.com."));
        assertTrue(JsseUtils.matchesCertificateIdentity(certificate, "bücher.example"));
        assertTrue(JsseUtils.matchesCertificateIdentity(certificate, "127.0.0.1"));
        assertFalse(JsseUtils.matchesCertificateIdentity(certificate, "other.example.com"));
        assertFalse(JsseUtils.matchesCertificateIdentity(certificate, "127.0.0.2"));
        assertFalse(JsseUtils.matchesCertificateIdentity(certificate, "localhost"));
        assertFalse(JsseUtils.matchesCertificateIdentity(certificate, "identity.invalid"));
    }

    @Test
    public void malformedIpHostsCannotFallBackToDnsIdentityMatching() throws Exception {
        for (String host : new String[] { "127.1", "256.0.0.1", "::::" }) {
            assertTrue(JsseUtils.isIpHost(host));
            assertFalse(JsseUtils.matchesCertificateIdentity(certificate(new GeneralName(GeneralName.dNSName, host)), host));
        }
        assertTrue(JsseUtils.isIpHost("[::1]"));
        assertFalse(JsseUtils.isIpHost("www.example.com"));
        assertFalse(JsseUtils.isIpHost("bücher.example"));
        assertFalse(JsseUtils.isIpHost(null));
    }

    @Test
    public void keyStoreRoundTripBuildsUsableKeyAndTrustManagers() throws Exception {
        KeyStore original = keyStore();
        ByteArrayOutputStream encoded = new ByteArrayOutputStream();
        original.store(encoded, PASSWORD);
        KeyStore restored = KeyStore.getInstance("PKCS12");
        JsseUtils.loadKeyStore(restored, new ByteArrayInputStream(encoded.toByteArray()), PASSWORD);
        assertArrayEquals(identityKey.getPrivate().getEncoded(), restored.getKey("key", PASSWORD).getEncoded());
        assertArrayEquals(new X509Certificate[] { certificate }, restored.getCertificateChain("key"));

        KeyManagerFactory keys = JsseUtils.buildKeyManagerFactory(restored, PASSWORD, null);
        TrustManagerFactory trust = JsseUtils.buildTrustManagerFactory(restored, null);
        assertTrue(keys.getKeyManagers().length > 0);
        assertTrue(Arrays.stream(trust.getTrustManagers())//
                .filter(X509TrustManager.class::isInstance)//
                .map(X509TrustManager.class::cast)//
                .anyMatch(manager -> Arrays.asList(manager.getAcceptedIssuers()).contains(certificate)));
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(keys.getKeyManagers(), trust.getTrustManagers(), null);
        assertNotNull(context.createSSLEngine());
    }

    @Test
    public void initializesAndReturnsSuppliedFactories() throws Exception {
        KeyStore store = keyStore();
        KeyManagerFactory keys = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        TrustManagerFactory trust = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        assertSame(keys, JsseUtils.buildKeyManagerFactory(store, PASSWORD, keys));
        assertSame(trust, JsseUtils.buildTrustManagerFactory(store, trust));
        assertTrue(keys.getKeyManagers().length > 0);
        assertTrue(trust.getTrustManagers().length > 0);
    }

    @Test
    public void wrongPasswordsPropagateKeyAndStoreFailures() throws Exception {
        KeyStore store = keyStore();
        char[] wrongPassword = "wrong-password".toCharArray();
        assertThrows(UnrecoverableKeyException.class, () -> JsseUtils.buildKeyManagerFactory(store, wrongPassword, null));
        ByteArrayOutputStream encoded = new ByteArrayOutputStream();
        store.store(encoded, PASSWORD);
        KeyStore restored = KeyStore.getInstance("PKCS12");
        assertThrows(IOException.class, () -> JsseUtils.loadKeyStore(restored, new ByteArrayInputStream(encoded.toByteArray()), wrongPassword));
    }

    @Test
    public void trustManagerAdapterPreservesManagersAndCopiesArrays() throws Exception {
        TrustManager[] supplied = JsseUtils.buildTrustManagerFactory(keyStore(), null).getTrustManagers();
        TrustManager original = supplied[0];
        TrustManagerFactory wrapped = JsseUtils.wrapTrustManagers(supplied);
        supplied[0] = null;
        TrustManager[] returned = wrapped.getTrustManagers();
        assertSame(original, returned[0]);
        returned[0] = null;
        wrapped.init((KeyStore) null);
        wrapped.init((ManagerFactoryParameters) null);
        assertSame(original, wrapped.getTrustManagers()[0]);
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, wrapped.getTrustManagers(), null);
        assertNotNull(context.createSSLEngine());
    }

    @Test
    public void verificationIsLimitedToHttpsClientEngines() throws Exception {
        SSLEngine engine = SSLContext.getDefault().createSSLEngine("www.example.com", 443);
        SSLParameters parameters = engine.getSSLParameters();
        parameters.setEndpointIdentificationAlgorithm("HTTPS");
        engine.setSSLParameters(parameters);
        JsseUtils.verifyHttpsEndpointIdentity(engine);

        engine.setUseClientMode(true);
        parameters.setEndpointIdentificationAlgorithm("LDAPS");
        engine.setSSLParameters(parameters);
        JsseUtils.verifyHttpsEndpointIdentity(engine);
        parameters.setEndpointIdentificationAlgorithm(null);
        engine.setSSLParameters(parameters);
        JsseUtils.verifyHttpsEndpointIdentity(engine);
        parameters.setEndpointIdentificationAlgorithm("hTtPs");
        engine.setSSLParameters(parameters);
        assertThrows(SSLException.class, () -> JsseUtils.verifyHttpsEndpointIdentity(engine));
    }

    private static KeyStore keyStore() throws Exception {
        KeyStore store = KeyStore.getInstance("PKCS12");
        JsseUtils.loadKeyStore(store, new X509Certificate[] { certificate }, identityKey.getPrivate(), PASSWORD);
        return store;
    }

    private static X509Certificate certificate(GeneralName... names) throws Exception {
        X500Principal subject = new X500Principal("CN=identity.invalid");
        JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(subject, BigInteger.ONE, //
                Date.from(Instant.parse("2020-01-01T00:00:00Z")), Date.from(Instant.parse("2040-01-01T00:00:00Z")), subject, identityKey.getPublic());
        builder.addExtension(Extension.subjectAlternativeName, false, new GeneralNames(names));
        byte[] encoded = builder.build(new JcaContentSignerBuilder("SHA256withRSA").build(identityKey.getPrivate())).getEncoded();
        return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(encoded));
    }
}
