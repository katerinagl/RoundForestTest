import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import scala.concurrent.duration._
import scala.io.StdIn

/**
  * Created by katerinaglushchenko on 6/22/17.
  */
trait ActorImplicits {
  implicit val system = ActorSystem("readings-data-receiver")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatchers.lookup("my-dispatcher")
  implicit val timeout = Timeout(100 seconds)
}

case class TranslateRequest(input_lang: String, output_lang: String, text: String)

case class TranslateResponse(text: String)


object GoogleTranslateEmulator extends ActorImplicits with Directives with JsonFormats {

  def main(args: Array[String]): Unit = {
    val route = path("translate") {
      post {
        entity(as[TranslateRequest]) { translateEntity =>
          println("in translate server " + translateEntity)
          complete(TranslateResponse(translateEntity.text.reverse))
        }
      }
    }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()

    bindingFuture.flatMap(_.unbind()).onComplete { _ =>
      system.terminate()
    }
  }
}