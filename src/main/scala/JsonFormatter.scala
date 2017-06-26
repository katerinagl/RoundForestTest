import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
  * Created by katerinaglushchenko on 6/22/17.
  */

trait JsonFormats extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val translateRequestFormat = jsonFormat3(TranslateRequest)

  implicit val translateResponseFormat = jsonFormat1(TranslateResponse)
}