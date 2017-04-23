package persistence.entities

import persistence.entities._
import spray.json.DefaultJsonProtocol
import spray.json._

import org.slf4j.{LoggerFactory,Logger}

object JsonProtocol4HttpServerResponse extends DefaultJsonProtocol {
  def logger = LoggerFactory.getLogger(JsonProtocol4HttpServerResponse.getClass)
  implicit object HttpServerJsonFormat extends JsonFormat[HttpServerMonitor] {
    def write(c:HttpServerMonitor) = JsArray (
                      JsNumber(c.agentId),
                      JsNumber(c.dnsTime),
                      JsString(c.permalink),
                      JsNumber(c.responseCode),
                      JsNumber(c.redirectTime),
                      JsNumber(c.waitTime),
                      JsNumber(c.sslTime),
                      JsNumber(c.wireSize),
                      JsNumber(c.connectTime),
                      JsString(c.serverIp),
                      JsString(c.errorType),
                      JsNumber(c.responseTime),
                      JsString(c.date),
                      JsString(c.countryId),
                      JsString(c.agentName),
                      JsNumber(c.roundId),
                      JsNumber(c.receiveTime),
                      JsNumber(c.fetchTime),
                      JsNumber(c.numRedirects))
    def read(value:JsValue) = value.asJsObject.getFields(
                     "agentId",
                     "dnsTime",
                     "permalink",
                     "responseCode",
                     "redirectTime",
                     "waitTime",
                     "sslTime",
                     "wireSize",
                     "connectTime",
                     "serverIp",
                     "errorType",
                     "responseTime",
                     "date",
                     "countryId",
                     "agentName",
                     "roundId",
                     "receiveTime",
                     "fetchTime",
                     "numRedirects") match {
      case Seq (      JsNumber(agentId),
                      JsNumber(dnsTime),
                      JsString(permalink),
                      JsNumber(responseCode),
                      JsNumber(redirectTime),
                      JsNumber(waitTime),
                      JsNumber(sslTime),
                      JsNumber(wireSize),
                      JsNumber(connectTime),
                      JsString(serverIp),
                      JsString(errorType),
                      JsNumber(responseTime),
                      JsString(date),
                      JsString(countryId),
                      JsString(agentName),
                      JsNumber(roundId),
                      JsNumber(receiveTime),
                      JsNumber(fetchTime),
                      JsNumber(numRedirects)) =>
      new HttpServerMonitor(
                      agentId.toInt,
                      dnsTime.toInt,
                      permalink,
                      responseCode.toInt,
                      redirectTime.toInt,
                      waitTime.toInt,
                      sslTime.toInt,
                      wireSize.toInt,
                      connectTime.toInt,
                      serverIp,
                      errorType,
                      responseTime.toInt,
                      date,
                      countryId,
                      agentName,
                      roundId.toInt,
                      receiveTime.toInt,
                      fetchTime.toInt,
                      numRedirects.toInt)
      case _ => {
        logger.info("No HttpServer Item")
        null
//        deserializationError("HttpServer expected")
      }
    
      
    }
  }

}