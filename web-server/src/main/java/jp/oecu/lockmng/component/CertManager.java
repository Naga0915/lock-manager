package jp.oecu.lockmng.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.oecu.lockmng.config.properties.CertConfig;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CertManager {
    private final CertConfig certConfig;
    private final String alias;
    private final char[] keystorePassword = "changeit".toCharArray();
    private final String keystorePath;

    @Autowired
    public CertManager(CertConfig certConfig) {
        this.certConfig = certConfig;
        this.alias = certConfig.getCn();
        this.keystorePath = certConfig.getFolder_name() + "/esp32_keystore.jks";
        init();
    }

    public void init() {
        try {
            initCert();
        } catch (Exception e) {
            log.error("証明書を読み込めませんでした。再起動してください", e);
        }
    }

    public String getAlias() {
        return alias;
    }

    public void initCert() throws Exception {
        File certDir = new File(certConfig.getFolder_name());
        if (certDir.exists()) {
            log.info("証明書フォルダが存在するため証明書生成はスキップします");
            return;
        }

        if (!certDir.mkdirs()) {
            throw new IOException("証明書フォルダの作成に失敗しました");
        }

        Security.addProvider(new BouncyCastleProvider());

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);

        KeyPair keyPair = generateKeyPair(certConfig.getRsa_keySize());

        X500Name dn = new X500Name(String.format(
                "CN=ESP32,OU=%s,O=%s,L=%s,C=%s",
                certConfig.getOu(),
                certConfig.getO(),
                certConfig.getL(),
                certConfig.getC()
        ));

        X509Certificate certificate = generateCertificate(keyPair, dn);

        keyStore.setKeyEntry(alias, keyPair.getPrivate(), keystorePassword, new X509Certificate[]{certificate});
        saveCertificateAsPEM(certificate, certConfig.getFolder_name() + "/" + alias + ".pem");

        try (FileOutputStream out = new FileOutputStream(keystorePath)) {
            keyStore.store(out, keystorePassword);
        }

        log.info("キーストアを証明書フォルダに保存しました");
    }

    public PrivateKey getPrivateKey() throws Exception {
        KeyStore keyStore = getKeyStore();
        Key key = keyStore.getKey(alias, keystorePassword);
        if (key instanceof PrivateKey) {
            return (PrivateKey) key;
        }
        throw new KeyStoreException("指定されたエイリアスの秘密鍵が取得できませんでした");
    }

    public X509Certificate getCertificate() throws Exception {
        KeyStore keyStore = getKeyStore();
        return (X509Certificate) keyStore.getCertificate(alias);
    }

    private KeyStore getKeyStore() throws Exception {
        File keystoreFile = new File(keystorePath);
        if (!keystoreFile.exists()) {
            throw new IOException("キーストアが存在しません: " + keystorePath);
        }

        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(keystoreFile)) {
            keyStore.load(fis, keystorePassword);
        }
        return keyStore;
    }

    private KeyPair generateKeyPair(int keySize) throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.generateKeyPair();
    }

    private X509Certificate generateCertificate(KeyPair keyPair, X500Name x500Name) throws Exception {
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = new Date();
        Date notAfter = new Date(notBefore.getTime() + 365L * 24 * 60 * 60 * 1000); // 1年

        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                x500Name, serial, notBefore, notAfter, x500Name, keyPair.getPublic());

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider("BC")
                .build(keyPair.getPrivate());

        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certBuilder.build(signer));
    }

    private void saveCertificateAsPEM(X509Certificate certificate, String fileName)
            throws IOException, CertificateException {
        try (FileOutputStream certOut = new FileOutputStream(fileName)) {
            certOut.write("-----BEGIN CERTIFICATE-----\n".getBytes());
            certOut.write(Base64.getMimeEncoder(64, new byte[]{'\n'}).encode(certificate.getEncoded()));
            certOut.write("\n-----END CERTIFICATE-----\n".getBytes());
        }
        log.info("PEMファイルを保存しました: " + fileName);
    }
}
