import DAOS.MongoDBDAO
import Documents._
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object Main extends App {
    println("I'm BitBot")
    val config: Config = ConfigFactory.load("resources/application.conf")
    val mdb = MongoDBDAO(config)
    val answer = Answer("Czy ?"
        , "Tak"
        , new Talker("4", "Józio", "Kowalski")
        , Some(List(
            new AnswerVerification(new Talker("3", "Józia", "Kowalska"), true, Some("Tak"))
            , new AnswerVerification(new Talker("2", "Ala", "Alowska"), true, Some("Nie"))
        )))
    //    mdb.insertDocument(Admin("admin", "tfoyaStara", "admin@admin.admin"))
    //    mdb.insertDocument(Event("SFI", Calendar.getInstance().getTime, "dupa123.dupa"))
    //    mdb.insertDocument(Event("SFI2", Calendar.getInstance().getTime, "dupa333.dupa"))
    //    mdb.getAdminUser("tfoyaStara", "admin").onComplete { case Success(Some(v)) => println(v) case r => print("Nope ", r) }
    //    mdb.getAllEvents.onComplete { case Success(v) => print(v) case r => print("Nope ", r) }
    //            mdb.insertDocument(
    //                Answer("Czy ?"
    //                    , "Tak"
    //                    , new Talker("4", "Józio", "Kowalski")
    //                    , Some(List(
    //                        new AnswerVerification(new Talker("3", "Józia", "Kowalska"), true, Some("Tak"))
    //                        , new AnswerVerification(new Talker("2", "Ala", "Alowska"), true, Some("Nie"))
    //                    ))))
    val r: Future[List[Answer]] = mdb.getAllAnswer

    mdb.updateAnswer(Await.result(r, 1 second).head, new AnswerVerification(new Talker("20", "Józinaa", "DUPSKO"), false, Some("Tak")))
    println(Await.result(mdb.getAllAnswer, 1 second))
    //    Some(List(
    //        new AnswerVerification(new Talker("3", "Józia", "Kowalska"), true, Some("Tak"))
    //        , new AnswerVerification(new Talker("2", "Ala", "Alowska"), true, Some("Nie"))
    //    ))
}
