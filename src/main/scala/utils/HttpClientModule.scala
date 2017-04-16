package utils

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import scala.concurrent.Future


trait HttpClientModule {
  val url: String
  val username: String
  val password: String
  val proxy: String 
}

trait HttpClientModuleImpl extends HttpClientModule {
  this: Configuration =>
  override val url = config.getString("http.client.url")
  override val username = config.getString("http.client.username")
  override val password = config.getString("http.client.password")
  override val proxy = config.getString("http.client.proxy")
}