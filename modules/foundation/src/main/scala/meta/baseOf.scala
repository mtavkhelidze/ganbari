//package foundation
//package meta
//
//import scala.quoted.*
//
//inline def baseOf[A]: Any = ${ baseOfMacro[A] }
//
//private def baseOfMacro[A: Type](using q: Quotes): Expr[Any] = {
//  import quotes.reflect.*
//  TypeRepr.of[A] match {
//    case AppliedType(tyCon, args) =>
//      args.head.asExpr
//    case other =>
//      report.errorAndAbort(s"Cannot extract base type from $other")
//  }
//}
