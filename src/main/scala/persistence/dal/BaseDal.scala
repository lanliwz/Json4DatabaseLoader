package persistence.dal

import persistence.entities.{BaseTable, BaseEntity}
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile
import slick.lifted.{Tag, CanBeQueryCondition}
import utils.{DbModule, Profile}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.slf4j.{LoggerFactory,Logger}

trait BaseDal[T,A] {
  def insert(row : A): Future[Long]
  def insert(rows : Seq[A]): Future[Seq[Long]]
  def update(row : A): Future[Int]
  def update(rows : Seq[A]): Future[Unit]
  def findById(id : Long): Future[Option[A]]
  def findByFilter[C : CanBeQueryCondition](f: (T) => C): Future[Seq[A]]
  def findAll: Future[Seq[A]]
  def deleteById(id : Long): Future[Int]
  def deleteById(ids : Seq[Long]): Future[Int]
  def deleteByFilter[C : CanBeQueryCondition](f:  (T) => C): Future[Int]
  def createTable() : Future[Unit]
}

class BaseDalImpl[T <: BaseTable[A], A <: BaseEntity](tableQ: TableQuery[T])(implicit val db: JdbcProfile#Backend#Database,implicit val profile: JdbcProfile) extends BaseDal[T,A] with Profile with DbModule {
  def logger = LoggerFactory.getLogger("BaseDalImpl")
  
  import profile.api._

  override def insert(row: A): Future[Long] = {
    logger.info("Database Insert Single Row")
    insert(Seq(row)).map(_.head)
  }

  override def insert(rows: Seq[A]): Future[Seq[Long]] = {
    logger.info("Database Batch Insert")
    db.run(tableQ returning tableQ.map(_.id) ++= rows.filter(_.isValid))
  }

  override def update(row: A): Future[Int] = {
    if (row.isValid)
      db.run(tableQ.filter(_.id === row.id).update(row))
    else
      Future {
        0
      }
  }

  override def update(rows: Seq[A]): Future[Unit] = {
    db.run(DBIO.seq((rows.filter(_.isValid).map(r => tableQ.filter(_.id === r.id).update(r))): _*))
  }

  override def findById(id: Long): Future[Option[A]] = {
    db.run(tableQ.filter(_.id === id).result.headOption)
  }

  override def findByFilter[C: CanBeQueryCondition](f: (T) => C): Future[Seq[A]] = {
    db.run(tableQ.withFilter(f).result)
  }
  
  override def findAll: Future[Seq[A]] = {
    db.run(tableQ.result)
  }

  override def deleteById(id: Long): Future[Int] = {
    deleteById(Seq(id))
  }

  override def deleteById(ids: Seq[Long]): Future[Int] = {
    db.run(tableQ.filter(_.id.inSet(ids)).delete)
  }

  override def deleteByFilter[C : CanBeQueryCondition](f:  (T) => C): Future[Int] = {
    db.run(tableQ.withFilter(f).delete)
  }

  override def createTable() : Future[Unit] = {
    db.run(DBIO.seq(tableQ.schema.create))
  }

}