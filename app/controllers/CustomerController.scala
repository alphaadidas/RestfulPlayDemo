package controllers

import play.api.mvc.{Action, Controller}
import com.google.inject.{Inject, Singleton}
import dao.CustomerDAO
import scala.concurrent.Future
import model.Customer
import play.api.libs.json.{JsError, JsSuccess}

/**
 * @author: gmatsu
 *
 *
 */
@Singleton
class CustomerController @Inject()(dao: CustomerDAO ) extends Controller {

  def save() = Action.async(parse.json) {
    request =>
      request.body.validate[Customer] match {
        case JsSuccess(s) => Future.successful(Ok)
        case JsError(e) => Future.successful(BadRequest("Bad Json Input"))
      }
  }
  def get(id:String) = Action {

    Ok
  }

  def update(id: String) = Action.async(parse.json){
    request =>

      Future.successful(Ok)
  }

  def delete(id:String) = Action {

    Ok
  }

  def find() =  play.mvc.Results.TODO
  def merge() = play.mvc.Results.TODO
  def singleMerge()  = play.mvc.Results.TODO

  def findDuplicates() = play.mvc.Results.TODO
}
