package Day6.Q6

import java.sql.{Connection, DriverManager, SQLException}

object DatabaseConnection {
  private val url =
    "jdbc:mysql://abc.com:3306/amartya"
  private val username = "admin"
  private val password = "XXXXXXXXXX" // replace with your real password

  def getConnection: Option[Connection] = {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver")
      val conn = DriverManager.getConnection(url, username, password)
      println("✅ Connected to Azure MySQL successfully!")
      Some(conn)
    } catch {
      case e: ClassNotFoundException =>
        println("❌ MySQL JDBC Driver not found!")
        e.printStackTrace()
        None
      case e: SQLException =>
        println("❌ SQL Exception while connecting to Azure MySQL!")
        e.printStackTrace()
        None
    }
  }
}
