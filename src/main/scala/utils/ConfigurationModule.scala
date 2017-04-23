package utils

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}

import org.slf4j.LoggerFactory
//import com.mysql.jdbc.log.LogFactory

trait Configuration {
  def config: Config
}

trait ConfigurationModuleImpl extends Configuration {
  private val internalConfig: Config = {
//    val configDefaults = ConfigFactory.load(this.getClass().getClassLoader(), "application.conf")
    val logger = LoggerFactory.getLogger("Config")
    
    scala.sys.props.get("application.config") match {
      case Some(filename) => {
        logger.info(filename)
        ConfigFactory.parseFile(new File(filename))
        //.withFallback(configDefaults)
      }
      case None => {
        logger.info("no configuration file is defined outside of jar")
        ConfigFactory.load(this.getClass().getClassLoader(), "application.conf")
      }
    }
  }
  
  def config = internalConfig
}