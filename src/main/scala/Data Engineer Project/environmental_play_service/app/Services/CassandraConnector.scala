package Services

import javax.inject._
import com.datastax.oss.driver.api.core.CqlSession
import com.typesafe.config.ConfigFactory

import java.net.InetSocketAddress

@Singleton
class CassandraConnector @Inject() () {
  val config = ConfigFactory.load()


  private val session: CqlSession =
    CqlSession.builder()
      .addContactPoint(
        new InetSocketAddress(
          config.getString("cassandra.host"),
          config.getString("cassandra.port").toInt
        )
      )
      .withAuthCredentials(
        config.getString("cassandra.username"),
        config.getString("cassandra.password")
      )
      .withSslContext(
        SSLUtil.buildSslContext(   // custom helper
          config.getString("cassandra.truststore.path"),
          config.getString("cassandra.truststore.password")
        )
      )
      .withLocalDatacenter(config.getString("cassandra.datacenter"))
      .build()

  def getSession: CqlSession = session
}
/*
import com.datastax.oss.driver.api.core.CqlSession
import java.net.InetSocketAddress
import java.nio.file.Paths

class CassandraClient @Inject()(config: Configuration) {


}

*/
