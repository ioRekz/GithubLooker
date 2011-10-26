package controllers

import play.Play
import play.mvc.Http
import play.Logger

object ScalateTemplate {

  import org.fusesource.scalate._
  import org.fusesource.scalate.util._

  lazy val scalateEngine = {
    val engine = new TemplateEngine
    engine.resourceLoader = new FileResourceLoader(Some(Play.getFile("/app/templates")))
    engine.classpath = Play.getFile("/tmp/classes").getAbsolutePath
    engine.workingDirectory = Play.getFile("tmp")
    engine.combinedClassPath = true
    engine.classLoader = Play.classloader
    engine
  }

  case class Template(name: String) {
    val scalateType = "." + Play.configuration.get("scalate");

    def render(args: (Symbol, Any)*) = {
      scalateEngine.layout(name + scalateType, args.map {
        case (k, v) => k.name -> v
      } toMap)
    }
  }

  def apply(template: String) = Template(template)
}

trait Scalate {

  def render(args: (Symbol, Any)*) = {
    def defaultTemplate = Http.Request.current().action.replace(".", "/")
    ScalateTemplate(defaultTemplate).render(args: _*);
  }
}