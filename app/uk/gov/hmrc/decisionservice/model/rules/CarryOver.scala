package uk.gov.hmrc.decisionservice.model.rules

sealed trait CarryOver {
  def value:String
  def exit:Boolean
  def name:Option[String]
  def equivalent(other:CarryOver) = value.toLowerCase == other.value.toLowerCase || other.value.isEmpty
  def isEmpty = value.isEmpty
}

object NotValidUseCase extends CarryOver {
  override def value = "NotValidUseCase"
  override def exit = false
  override def name = None
  override def toString = s">>>($value)"
}

object EmptyCarryOver extends CarryOver {
  override def value = ""
  override def exit = false
  override def name = None
  override def toString = ">>>(,)"
}

case class >>>(value:String, exit:Boolean = false, name:Option[String] = None) extends CarryOver {
  override def toString: String = if (exit) s">>>($value,$exit,${name.getOrElse("")})" else s">>>($value,${name.getOrElse("")})"
}

object >>> {
  def apply(tokens:List[String]): >>> = {
    val v = tokens.headOption.getOrElse("")
    val x = tokens.drop(1).headOption.fold(false)(_.toBoolean)
    val n = tokens.drop(2).headOption.collect{ case s if !s.isEmpty => s}
    new >>>(v,x,n)
  }
}
