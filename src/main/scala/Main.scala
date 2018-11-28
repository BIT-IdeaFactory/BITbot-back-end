import DAOS.MongoDBDAO
import Documents.{Answer, AnswerVerification, Talker}
import com.typesafe.config.{Config, ConfigFactory}


object Main extends App {
    println("I'm BitBot")
    val config: Config = ConfigFactory.load("resources/application.conf")
    val mdb = MongoDBDAO(config)
    mdb.insertDocument(
        Answer("Czy ?"
            , "Tak"
            , new Talker("4", "Józio", "Kowalski")
            , Some(List(
                new AnswerVerification(new Talker("3", "Józia", "Kowalska"), true, Some("Tak"))
                , new AnswerVerification(new Talker("2", "Ala", "Alowska"), true, Some("Nie"))
            ))))

    //    Some(List(
    //        new AnswerVerification(new Talker("3", "Józia", "Kowalska"), true, Some("Tak"))
    //        , new AnswerVerification(new Talker("2", "Ala", "Alowska"), true, Some("Nie"))
    //    ))
}
