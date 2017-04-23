package persistence.entities

//import slick.driver.H2Driver.api._
import com.typesafe.slick.driver.oracle.OracleDriver.api._

case class HttpServerKeys (id:Long, key:String) extends BaseEntity

 
class HttpServerKeyTable(tag: Tag) extends BaseTable[HttpServerKeys] (tag, "HTTP_SERVER_KEYS") {
  def key = column[String]("key")
  def * = (id, key) <> (HttpServerKeys.tupled, HttpServerKeys.unapply) 
}

