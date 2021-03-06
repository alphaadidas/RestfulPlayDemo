package controllers

import play.api._
import play.api.mvc._
import com.google.inject.Singleton

@Singleton
class Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}