package uk.gov.hmrc.decisionservice.model.rules

sealed trait CarryOver {
  def value:String
  def exit:Boolean
  def name:Option[String]
  def equivalent(other:CarryOver) = value.toLowerCase == other.value.toLowerCase || other.value.isEmpty
  def isEmpty = value.isEmpty
}

case object NotValidUseCase extends CarryOver {
  override def value = "NotValidUseCase"
  override def exit = false
  override def name = None
}

case object EmptyCarryOver extends CarryOver {
  override def value = ""
  override def exit = false
  override def name = None
}

case class >>>(value:String, exit:Boolean = false, name:Option[String] = None) extends CarryOver {
  override def toString: String = List(Some(value), if (exit) Some("true") else None, name).flatten.mkString(">>>(", ",", ")")
}

object >>> {
  def apply(tokens:List[String]): >>> = {
    val v = tokens.headOption.getOrElse("")
    val x = tokens.drop(1).headOption.fold(false)(_.toBoolean)
    val n = tokens.drop(2).headOption.collect{ case s if !s.isEmpty => s}
    new >>>(v,x,n)
  }
  def equivalent(p:(CarryOver,CarryOver)):Boolean = p match { case (a,b) => a.equivalent(b) }
  def emptyPositions(cos: Iterable[CarryOver]):Set[Int] = cos.zipWithIndex.collect { case (a,i) if(a.isEmpty) => i }.toSet
}
