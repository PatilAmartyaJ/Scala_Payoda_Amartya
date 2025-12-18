package Services

import javax.net.ssl.{SSLContext, TrustManagerFactory}
import java.security.KeyStore
import java.io.FileInputStream

object SSLUtil {
  def buildSslContext(trustStorePath: String, trustStorePassword: String): SSLContext = {
    val trustStore = KeyStore.getInstance("JKS")
    trustStore.load(new FileInputStream(trustStorePath), trustStorePassword.toCharArray)

    val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
    tmf.init(trustStore)

    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, tmf.getTrustManagers, null)

    sslContext
  }
}
