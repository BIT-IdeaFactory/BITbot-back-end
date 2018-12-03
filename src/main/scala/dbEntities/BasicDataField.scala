package dbEntities

import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class BasicDataField(question: String, userAnswer: String, userName: String)


object BasicDataField {
    def apply(question: String, userAnswer: String, userName: String): BasicDataField = new BasicDataField(question, userAnswer, userName)
    implicit object BasicDataFieldReader extends BSONDocumentReader[BasicDataField] {
        override def read(doc: BSONDocument): BasicDataField = BasicDataField(
            doc.getAs[String]("question").get,
            doc.getAs[String]("userAnswer").get,
            doc.getAs[String]("userName").get,
        )
    }
    
    implicit object BasicDataFieldWriter extends BSONDocumentWriter[BasicDataField] {
        override def write(basicData: BasicDataField): BSONDocument = BSONDocument(
            "question" → basicData.question,
            "userAnswer" → basicData.userAnswer,
            "userName" → basicData.userName
        )
    }
}