package bitbot.server.graphQL.schemas

import java.util.Date

import bitbot.server.graphQL.violations.DateTimeCoerceViolation
import sangria.ast.StringValue
import sangria.schema.ScalarType

object GraphQLConversions {
  implicit val GraphQLDate: ScalarType[Date] = ScalarType[Date](
    "Date",
    coerceOutput = (dt, _) => dt.toString,
    coerceInput = {
      case StringValue(dt, _, _, _, _) => Some(new Date(dt)).toRight(DateTimeCoerceViolation)
      case _ => Left(DateTimeCoerceViolation)
    },
    coerceUserInput = {
      case s: String => Some(new Date(s)).toRight(DateTimeCoerceViolation)
      case _ => Left(DateTimeCoerceViolation)
    }
  )
}
