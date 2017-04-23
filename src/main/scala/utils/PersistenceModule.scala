package utils

import akka.actor.{ActorPath, ActorSelection, Props, ActorRef}
import persistence.dal._
import slick.backend.DatabaseConfig
import slick.driver.{JdbcProfile}
import persistence.entities._
import slick.lifted.TableQuery



trait Profile {
	val profile: JdbcProfile
}

trait DbModule extends Profile{
	val db: JdbcProfile#Backend#Database
}

trait PersistenceModule {

	val httpServerDal: BaseDal[HttpServerMonitorTable,HttpServerMonitorEntity]
	val httpServerKeyDal: BaseDal[HttpServerKeyTable,HttpServerKeys]
}


trait PersistenceModuleImpl extends PersistenceModule with DbModule{
	this: Configuration  =>

	// use an alternative database configuration ex:
	// private val dbConfig : DatabaseConfig[JdbcProfile]  = DatabaseConfig.forConfig("pgdb")
//	private val dbConfig : DatabaseConfig[JdbcProfile]  = DatabaseConfig.forConfig("h2db")
//	private val dbConfig : DatabaseConfig[JdbcProfile]  = DatabaseConfig.forConfig("mysqldb")
	private val dbConfig : DatabaseConfig[JdbcProfile]  = DatabaseConfig.forConfig("oracledb")

	override implicit val profile: JdbcProfile = dbConfig.driver
	override implicit val db: JdbcProfile#Backend#Database = dbConfig.db

	override val httpServerDal = new BaseDalImpl[HttpServerMonitorTable,HttpServerMonitorEntity](TableQuery[HttpServerMonitorTable]) {}
	override val httpServerKeyDal = new BaseDalImpl[HttpServerKeyTable,HttpServerKeys](TableQuery[HttpServerKeyTable]) {}
	
	val self = this

}

trait PersistenceMysqlModuleImpl extends PersistenceModule with DbModule{
	this: Configuration  =>

	private val dbConfig : DatabaseConfig[JdbcProfile]  = DatabaseConfig.forConfig("mysqldb")

	override implicit val profile: JdbcProfile = dbConfig.driver
	override implicit val db: JdbcProfile#Backend#Database = dbConfig.db

	override val httpServerDal = new BaseDalImpl[HttpServerMonitorTable,HttpServerMonitorEntity](TableQuery[HttpServerMonitorTable]) {}
	override val httpServerKeyDal = new BaseDalImpl[HttpServerKeyTable,HttpServerKeys](TableQuery[HttpServerKeyTable]) {}
	
	val self = this

}

trait PersistenceOraModuleImpl extends PersistenceModule with DbModule{
	this: Configuration  =>
	private val dbConfig : DatabaseConfig[JdbcProfile]  = DatabaseConfig.forConfig("oracledb")

	override implicit val profile: JdbcProfile = dbConfig.driver
	override implicit val db: JdbcProfile#Backend#Database = dbConfig.db

	override val httpServerDal = new BaseDalImpl[HttpServerMonitorTable,HttpServerMonitorEntity](TableQuery[HttpServerMonitorTable]) {}
	override val httpServerKeyDal = new BaseDalImpl[HttpServerKeyTable,HttpServerKeys](TableQuery[HttpServerKeyTable]) {}
	
	val self = this

}

