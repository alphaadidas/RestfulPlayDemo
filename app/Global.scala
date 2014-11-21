
import com.google.inject.{AbstractModule, Guice}
import play.api.GlobalSettings

/**
 * @author gmatsu
 *
 *
 */
object Global extends GlobalSettings {

  val injector = Guice.createInjector(new AbstractModule {
    protected def configure() {
    }
  })


  /**
   * Controllers must be resolved through the application context. There is a special method of GlobalSettings
   * that we can override to resolve a given controller. This resolution is required by the Play router.
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)

}
