package repos

import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import play.api.libs.json.{JsObject, Json}
import reactivemongo.api.commands.WriteResult
import play.modules.reactivemongo.ReactiveMongoApi
import javax.inject._
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.ReadPreference

trait PostRepo {
	def find() (implicit ec: ExecutionContext): Future[List[JsObject]]

	def select(selector: BSONDocument) (implicit ec: ExecutionContext): Future[Option[JsObject]]

	def update(selector: BSONDocument, update: BSONDocument) (implicit ec: ExecutionContext): Future[WriteResult]

	def remove(document: BSONDocument) (implicit ec: ExecutionContext): Future[WriteResult]

	def save(document: BSONDocument) (implicit ec: ExecutionContext): Future[WriteResult]

}

class PostRepoImpl @Inject() (reactiveMongoApi: ReactiveMongoApi) extends PostRepo {
  def collection = reactiveMongoApi.db.collection[JSONCollection]("posts");
  
  override def find() (implicit ec: ExecutionContext): Future[List[JsObject]] = {
    val genericQueryBuilder = collection.find(Json.obj());
    val cursor = genericQueryBuilder.cursor[JsObject](ReadPreference.Primary);
    cursor.collect[List]()
  }

  def select(selector: BSONDocument)(implicit ec: ExecutionContext): Future[Option[JsObject]] = {
    collection.find(selector).one[JsObject]
  }

  def update(selector: BSONDocument, update: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = {
    collection.update(selector,update)
  }

  def remove(document: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = {
    collection.remove(document)
  }

  def save(document: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = {
    collection.update(BSONDocument("_id" -> document.get("_id").getOrElse(BSONObjectID.generate)), document, upsert = true)
  }
  
}