package bitbot.server.graphQL.violations

import sangria.validation.Violation

case object DateTimeCoerceViolation extends Violation {
  override def errorMessage: String = "Error during parsing DateTime"
}
