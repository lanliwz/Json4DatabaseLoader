import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import utils.{ PersistenceModuleImpl, PersistenceMysqlModuleImpl,PersistenceOraModuleImpl,ActorModuleImpl, ConfigurationModuleImpl }
import utils.HttpClientModuleImpl
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import akka.http.scaladsl.model.headers.{ Authorization, BasicHttpCredentials, `Content-Type`, `Proxy-Authorization` }
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model._
import HttpMethods.{ GET, POST, DELETE, PUT }
import HttpProtocols._
import MediaTypes.{ `application/json`, `application/xml` }
import HttpCharsets._
import scala.io._
import java.nio.charset.Charset
import scala.util.{ Failure, Success }

import spray.json._
import DefaultJsonProtocol._
import persistence.entities.JsonProtocol4HttpServerResponse._
import persistence.entities._
import persistence.entities.Convertor

import org.apache.commons.httpclient.Credentials
import org.apache.commons.httpclient.HostConfiguration
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.HttpMethod
import org.apache.commons.httpclient.HttpStatus
import org.apache.commons.httpclient.UsernamePasswordCredentials
import org.apache.commons.httpclient.auth.AuthScope
import org.apache.commons.httpclient.methods.GetMethod

import org.slf4j.{LoggerFactory,Logger}


object Main extends App {
  // configuring modules for application, cake pattern for DI
  val logger = LoggerFactory.getLogger("Main")
  val modules = new ConfigurationModuleImpl with ActorModuleImpl with PersistenceOraModuleImpl with HttpClientModuleImpl
  implicit val system = modules.system
  implicit val materializer = ActorMaterializer()
  implicit val ec = modules.system.dispatcher
  val url: String = modules.url
  val username = modules.username
  val password = modules.password
  val hasProxy = modules.hasProxy
  val proxyHost = modules.proxyHost
  val proxyPort = modules.proxyPort
  val proxyUsername = modules.proxyUsername
  val proxyPassword = modules.proxyPassword

//  modules.httpServerDal.createTable()
//  modules.httpServerKeyDal.createTable()

  val keyField = "#key#".r

  val myheaders = Authorization(BasicHttpCredentials(username, password))

  modules.httpServerKeyDal.findAll.onComplete {
    case Success(rows) => {
      rows.map {
        row => {
          logger.info(row.key)
          loadFromHttpClient(row.key)
        }
      }
    }
    case Failure(rows) => {
      logger.error("no key found from database!")
    }
  }


  def load(key: String): Unit = {
    logger.info(List(myheaders).toString())
    val headers = List(myheaders)
    val entity = HttpEntity.empty(ContentTypes.`application/json`)
    val acturlUrl = keyField.replaceFirstIn(url, key)
    println(s"Client URL $acturlUrl $username")
    val respF: Future[HttpResponse] =
      Http().singleRequest(
        HttpRequest(
          method = GET,
          uri = acturlUrl,
          headers = headers,
          entity = entity))
    val resp: HttpResponse = Await.result(respF, 1 minute)

    resp.entity.toStrict(30000 milli).map { entity =>
      {
        val data = Charset.forName("UTF-8").decode(entity.data.asByteBuffer)
        val jsonData = data.toString()
        val jsonAst: JsValue = jsonData.parseJson
        jsonAst.asJsObject.getFields("web") match {
          case Seq(JsObject(web)) =>
            web.get("httpServer").get match {
              case v: JsArray => v.elements.map(jsv => {
                val data = jsv.convertTo[HttpServerMonitor]
                val row = Convertor(data)
                modules.httpServerDal.insert(row)
              })
            }

          case _ => println("ignore")
        }
      }
    }

  }

  def loadFromHttpClient(key: String): Unit = {
    val acturlUrl = keyField.replaceFirstIn(url, key)
    println(s"Client URL $acturlUrl $proxyHost $proxyPort $proxyUsername $proxyPassword")

    val client = new HttpClient();
    val method = new GetMethod(acturlUrl);
    val config = client.getHostConfiguration()

    //proxy settings
    if(hasProxy==true) {
      config.setProxy("bcpxy.nycnet", 8080)
      val pxyCredentials = new UsernamePasswordCredentials("lali", "Llwz1962#")
      val pxyAuthScope = new AuthScope("bcpxy.nycnet", 8080);
      client.getState().setProxyCredentials(pxyAuthScope, pxyCredentials)
        
//    config.setProxy(proxyHost, proxyPort)
//    val pxyCredentials = new UsernamePasswordCredentials(proxyUsername, proxyPassword)
//    val pxyAuthScope = new AuthScope(proxyHost, proxyPort,AuthScope.ANY_REALM)
//    client.getState().setProxyCredentials(pxyAuthScope, pxyCredentials)

      
    }


    val authScope = new AuthScope("api.thousandeyes.com", 443, AuthScope.ANY_REALM)
    val credentials = new UsernamePasswordCredentials(username, password);
    client.getState().setCredentials(authScope, credentials)
    method.setDoAuthentication(true)
    //  client.getParams().setAuthenticationPreemptive(true)

    try {
      client.executeMethod(method);

      if (method.getStatusCode() == HttpStatus.SC_OK) {
        val response = method.getResponseBodyAsString();
        System.out.println(response);
        val jsonData = response
        val jsonAst: JsValue = jsonData.parseJson
        jsonAst.asJsObject.getFields("web") match {
          case Seq(JsObject(web)) =>
            web.get("httpServer").get match {
              case v: JsArray => v.elements.map(jsv => {
                val data = jsv.convertTo[HttpServerMonitor]
                val row = Convertor(data)
                modules.httpServerDal.insert(row)
              })
            }

          case _ => println("ignore")
        }
      }
    } catch {
      case e: Exception => e.printStackTrace();
    }

  }

  Thread.sleep(600000)
  System.exit(0)

}