package persistence.entities

//import slick.driver.MySQLDriver.api._
import slick.driver.H2Driver.api._

case class HttpServerMonitor(    
                              agentId:Long,
                              dnsTime:Long,
                              permalink:String,
                              responseCode:Long,
                              redirectTime:Long,
                              waitTime:Long,
                              sslTime:Long,
                              wireSize: Long,
                              connectTime: Long,
                              serverIp: String,
                              errorType: String,
                              responseTime: Long,
                              date: String,
                              countryId: String,
                              agentName: String,
                              roundId: Long,
                              receiveTime: Long,
                              fetchTime: Long,
                              numRedirects: Long)
                              
case class HttpServerMonitorEntity(
                              id:Long,
                              agentId:Long,
                              dnsTime:Long,
                              permalink:String,
                              responseCode:Long,
                              redirectTime:Long,
                              waitTime:Long,
                              sslTime:Long,
                              wireSize: Long,
                              connectTime: Long,
                              serverIp: String,
                              errorType: String,
                              responseTime: Long,
                              date: String,
                              countryId: String,
                              agentName: String,
                              roundId: Long,
                              receiveTime: Long,
                              fetchTime: Long,
                              numRedirects: Long
) extends  BaseEntity
object Convertor {
  def apply(data:HttpServerMonitor):HttpServerMonitorEntity = {
    HttpServerMonitorEntity(
                              0,
                              data.agentId, 
                              data.dnsTime, 
                              data.permalink,
                              data.responseCode,
                              data.redirectTime,
                              data.waitTime,
                              data.sslTime,
                              data.wireSize,
                              data.connectTime,
                              data.serverIp,
                              data.errorType,
                              data.responseTime,
                              data.date,
                              data.countryId,
                              data.agentName,
                              data.roundId,
                              data.receiveTime,
                              data.fetchTime,
                              data.numRedirects)
  }

}

class HttpServerMonitorTable(tag: Tag) extends BaseTable[HttpServerMonitorEntity](tag, "HTTP_SERVER_MONITOR") {
  def agentId = column[Long]("agentId")
  def dnsTime = column[Long]("dnsTime")
  def permalink = column[String]("permalink")
  def responseCode=column[Long]("responseCode")
  def redirectTime=column[Long]("redirectTime")
  def waitTime=column[Long]("waitTime")
  def sslTime=column[Long]("sslTime")
  def wireSize=column[ Long]("wireSize")
  def connectTime=column[ Long]("connectTime")
  def serverIp=column[ String]("serverIp")
  def errorType=column[String]("errorType")
  def responseTime=column[ Long]("responseTime")
  def date=column[String]("date")
  def countryId=column[String]("countryId")
  def agentName=column[String]("agentName")
  def roundId=column[Long]("roundId")
  def receiveTime=column[Long]("receiveTime")
  def fetchTime=column[Long]("fetchTime")
  def numRedirects=column[Long]("numRedirects") 
  def * = (id,agentId, dnsTime, permalink,responseCode,redirectTime,waitTime,sslTime,wireSize,connectTime,serverIp,errorType,responseTime,date,countryId,agentName,roundId,receiveTime,fetchTime,numRedirects)  <>  (HttpServerMonitorEntity.tupled, HttpServerMonitorEntity.unapply) 
}
             
