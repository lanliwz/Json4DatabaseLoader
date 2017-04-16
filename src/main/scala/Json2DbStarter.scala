import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import rest.SupplierRoutes
import utils.{PersistenceModuleImpl, ActorModuleImpl, ConfigurationModuleImpl}
import utils.HttpClientModuleImpl
import scala.concurrent.{Await,Future}
import scala.concurrent.duration._
import akka.http.scaladsl.model.headers.{Authorization,BasicHttpCredentials,`Content-Type`,`Proxy-Authorization`}
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model._
import HttpMethods.{GET,POST,DELETE,PUT}
import HttpProtocols._
import MediaTypes.{`application/json`,`application/xml`}
import HttpCharsets._
import scala.io._
import java.nio.charset.Charset
import scala.util.{Failure, Success}

import spray.json._
import DefaultJsonProtocol._
import persistence.entities.JsonProtocol4HttpServerResponse._
import persistence.entities._
import persistence.entities.Convertor



object StarterMain extends App {
  // configuring modules for application, cake pattern for DI
  val modules = new ConfigurationModuleImpl  with ActorModuleImpl with PersistenceModuleImpl with HttpClientModuleImpl
  implicit val system = modules.system
  implicit val materializer = ActorMaterializer()
  implicit val ec = modules.system.dispatcher
  val url:String = modules.url
  val username = modules.username
  val password = modules.password  
  val myheaders = Authorization(BasicHttpCredentials(username, password))


  println(myheaders)
  val headers = List(myheaders)
  val entity = HttpEntity.empty(ContentTypes.`application/json`)
  
  val respF:Future[HttpResponse] = 
    Http().singleRequest(
      HttpRequest(
          method = GET,
          uri = url,
          headers=headers,
          entity = entity
      )
    )
    

  //val resp:HttpResponse = Await.result(respF,1 second)
  respF.onComplete { 
    case Success(resp) =>  {  
      println(resp.status)
      resp.entity.toStrict(30000 milli).map{entity => {
      val data = Charset.forName("UTF-8").decode(entity.data.asByteBuffer)
      val jsonData = data.toString()
      val jsonAst:JsValue = jsonData.parseJson
//      println(jsonAst.prettyPrint)
      jsonAst.asJsObject.getFields("web") match {
        case Seq(JsObject(web)) => 
          web.get("httpServer").get match {
            case v:JsArray =>  v.elements.map(jsv => {
              val data = jsv.convertTo[HttpServerMonitor]
              val row = Convertor(data)
              modules.httpServerDal.insert(row)  
            }
            )
            }
          
        case _ => println("ignore")  
        }
      }

      }      
    }
    case Failure(e) => println(e)
  }
    
  Await.ready(system.whenTerminated, 1 minute)
  val terminated = system.terminate()
  
  terminated.onComplete { 
    case Success(x) => println("normal termination")  
    case Failure(x) => println("abnormal termination")
  }


}