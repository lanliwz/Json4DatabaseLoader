package utils

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import scala.concurrent.Future


trait HttpClientModule {
  val url: String
  val username: String
  val password: String
  val proxyHost: String 
  val proxyPort: Int
  val proxyUsername: String
  val proxyPassword: String
  val hasProxy: Boolean
}

trait HttpClientModuleImpl extends HttpClientModule {
  this: Configuration =>
  override val url = config.getString("http.client.url")
  override val username = config.getString("http.client.username")
  override val password = config.getString("http.client.password")
  override val proxyHost = config.getString("http.client.proxyHost")
  override val proxyPort = config.getInt("http.client.proxyPort")
  override val proxyUsername = config.getString("http.client.proxyUsername")
  override val proxyPassword = config.getString("http.client.proxyPassword")
  override val hasProxy = config.getBoolean("http.client.hasProxy")
}