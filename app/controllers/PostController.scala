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

	def create = Action.async(BodyParsers.parse.json) { 
    implicit request =>
      // TODO Define and clean this structure.
      val id = (request.body \ Id).as[String]
      val name = (request.body \ Name).as[String]
      val description = (request.body \ Description).as[String]
      val owner = (request.body \ Owner).as[String]
      val fullAddress = (request.body \ FullAddress).as[String]
      val simplifiedAddress = (request.body \ SimplifiedAddress).as[String]
      val telephone = (request.body \ Telephone).as[String]
      val latLong = (request.body \ LatLong).as[String]
      
      postRepo.save(BSONDocument(Id -> id, Name -> name, Description -> description, 
          Owner -> owner, FullAddress -> fullAddress, SimplifiedAddress -> simplifiedAddress,
          Telephone -> telephone, LatLong -> latLong)).map(result => Created)
  }

	def read(id: String) = Action.async { 
    implicit request => postRepo.select(BSONDocument(Id -> BSONObjectID(id))).map(post => Ok(Json.toJson(post))) }

	def update(id: String) = Action.async(BodyParsers.parse.json) {
    implicit request =>
      // TODO Clean this as well.
      val id = (request.body \ Id).as[String]
      val name = (request.body \ Name).as[String]
      val description = (request.body \ Description).as[String]
      val owner = (request.body \ Owner).as[String]
      val fullAddress = (request.body \ FullAddress).as[String]
      val simplifiedAddress = (request.body \ SimplifiedAddress).as[String]
      val telephone = (request.body \ Telephone).as[String]
      val latLong = (request.body \ LatLong).as[String]
      
      postRepo.update(BSONDocument(Id -> BSONObjectID(id)),
          BSONDocument("$set" -> BSONDocument(Id -> id, Name -> name, Description -> description, 
          Owner -> owner, FullAddress -> fullAddress, SimplifiedAddress -> simplifiedAddress,
          Telephone -> telephone, LatLong -> latLong))).map(result => Accepted)
  }

	def delete(id: String) = Action.async {
    postRepo.remove(BSONDocument(Id -> BSONObjectID(id))).map(result => Accepted) 
  }
}

object PostFields {
  val Id = "id"
  val Name = "name"
  val Description = "description"
  val Owner = "owner"
  val FullAddress = "fullAddress"
  val SimplifiedAddress = "simplifiedAddress"
  val Telephone = "telephone"
  val LatLong = "latLong"
}