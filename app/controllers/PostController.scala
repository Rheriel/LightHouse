package controllers

import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import javax.inject.Inject
import repos.PostRepoImpl
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import reactivemongo.bson.{BSONDocument, BSONObjectID}

class PostController @Inject() (val reactiveMongoApi: ReactiveMongoApi) extends Controller 
  with MongoController with ReactiveMongoComponents {
  
  import controllers.PostFields._
   
  def postRepo = new PostRepoImpl(reactiveMongoApi)
  
	def index = Action.async { 
    implicit request => postRepo.find().map(posts => Ok(Json.toJson(posts)))
  }

	def create = TODO

	def read(id: String) = Action.async { 
    implicit request => postRepo.select(BSONDocument(Id -> BSONObjectID(id))).map(post => Ok(Json.toJson(post))) }

	def update(id: String) = TODO

	def delete(id: String) = TODO
}

object PostFields {
  val Id = "_id"
  val Name = "name"
  val Description = "description"
  val Owner = "owner"
  val FullAddress = "fullAddress"
  val SimplifiedAddress = "simplifiedAddress"
  val Telephone = "telephone"
  val LatLong = "latLong"
}