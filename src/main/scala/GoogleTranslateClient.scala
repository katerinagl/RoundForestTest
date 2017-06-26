import java.io.FileReader
import java.util.concurrent.TimeUnit.SECONDS

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, _}
import akka.stream.scaladsl.{Sink, Source}
import akka.stream._
import akka.util.ByteString
import com.opencsv.CSVReader
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

/**
  * Created by katerinaglushchenko on 6/22/17.
  */
object GoogleTranslateClient extends JsonFormats {
  def main(args: Array[String]): Unit = {
    if (args.length == 1) {
      import scala.collection.JavaConversions._

      val path = args(0)

      val maxMessageLength = 1000
      val buffer = new StringBuffer(maxMessageLength + 1)

      val delimiter = "|"
      val csvTextColumnIndex = 9
      val inputLang = "en"
      val outputLang = "fr"

      implicit val system = ActorSystem()
      import system.dispatcher
      implicit val materializer = ActorMaterializer()

      val reader = new CSVReader(new FileReader(path))

      val data = reader.toStream.tail.map(row => row(csvTextColumnIndex))
      val size = data.size
      val messages = data.zipWithIndex.flatMap { value =>
        if (buffer.length() + value._1.length < maxMessageLength && value._2 != size - 1) {
          buffer.append(value._1 + delimiter)
          None
        } else {
          val res = buffer.append(value._1 + delimiter).toString
          buffer.setLength(0)
          Some(res)
        }
      }

      val http = Http()
      val connection = http.outgoingConnection("localhost", 8080)

      def transformEntity(entity: ResponseEntity) = {
        entity.dataBytes.runFold(ByteString(""))(_ ++ _).map { body =>
          val str = body.utf8String
          val json = str.parseJson
          json.convertTo[TranslateResponse]
        }
      }

      val response = Source.fromIterator[HttpRequest](() =>
        messages.map(message => HttpRequest(method = HttpMethods.POST, uri = "http://localhost:8080/translate",
          entity = HttpEntity(MediaTypes.`application/json`,
            TranslateRequest(inputLang, outputLang, message).toJson.toString))).toIterator).via(connection)
        .throttle(100, FiniteDuration(1, SECONDS), 100, ThrottleMode.Shaping)
        .runWith(Sink.fold(Future(List[String]())) { (list, newValue) =>
          list.flatMap(l => transformEntity(newValue.entity).map(v => l ++ v.text.split("\\|").toList))
        })

      response.flatMap(a => a).onComplete {
        case Success(s) =>
          println("success")
          s.foreach(p => println(p + "\n"))
          system.terminate()
        case Failure(f) =>
          println("Failure")
          println(f)
          system.terminate()
      }
    } else {
      println("Please add review.csv path as agrument ")
    }
  }
}