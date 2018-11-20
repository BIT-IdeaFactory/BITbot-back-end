package bitbot.server.graphQL.schemas

import akka.http.scaladsl.model.DateTime
import bitbot.server.graphQL.violations.DateTimeCoerceViolation
import sangria.ast.StringValue
import sangria.schema.ScalarType

object GraphQLConversions {
  implicit val GraphQLDateTime = ScalarType[DateTime](
    "DateTime",
    coerceOutput = (dt, _) => dt.toString,
    coerceInput = {
      case StringValue(dt, _, _, _, _) => DateTime.fromIsoDateTimeString(dt).toRight(DateTimeCoerceViolation)
      case _ => Left(DateTimeCoerceViolation)
    },
    coerceUserInput = {
      case s: String => DateTime.fromIsoDateTimeString(s).toRight(DateTimeCoerceViolation)
      case _ => Left(DateTimeCoerceViolation)
    }
  )
  
}
