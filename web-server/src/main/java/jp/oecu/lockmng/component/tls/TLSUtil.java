package jp.oecu.lockmng.component.tls;

import java.net.ServerSocket;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import jp.oecu.lockmng.component.CertManager;

public class TLSUtil {
    public static ServerSocket createTLSServerSocket(int port, CertManager certManager) throws Exception {
        // 鍵と証明書を取得
        PrivateKey privateKey = certManager.getPrivateKey();
        X509Certificate certificate = certManager.getCertificate();
        certManager.printFingerprint(certificate);

        // キーストアを作成し、鍵と証明書を登録
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null); // 空のキーストアを作成
        keyStore.setKeyEntry("server", privateKey, "changeit".toCharArray(), new java.security.cert.Certificate[]{certificate});

        // キーマネージャーファクトリを初期化
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, "changeit".toCharArray());

        // SSLコンテキストを作成
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());

        // サーバーソケットファクトリからTLSサーバーソケットを作成
        SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
        SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(port);

        // 必要に応じてTLSプロトコルの制限やクライアント認証の設定
        serverSocket.setEnabledProtocols(new String[]{"TLSv1.3", "TLSv1.2"});
        serverSocket.setNeedClientAuth(false); // クライアント証明書を要求しない場合

        return serverSocket;
    }
}
