package exp

import scala.quoted.*

object Macropathya {
  inline def makeFor[EN]: Any = ${ makeForMacro[EN] }

  private def makeApplyMethod(using
      q: Quotes,
  )(
      clsSym: q.reflect.Symbol,
      caseSym: q.reflect.Symbol,
  ): List[q.reflect.Symbol] = {
    import q.reflect.*

    // Reflect on the case class constructor params
    val caseParams =
      caseSym.primaryConstructor.paramSymss.flatten.filter(_.isTerm)

    // Build MethodType: (param1: String, param2: String): CaseClass
    val methodType = MethodType(caseParams.map(_.name) :+ "param2")(
      _ => caseParams.map(_ => TypeRepr.of[String]) :+ TypeRepr.of[String],
      _ => caseSym.typeRef,
    )

    val applySym = Symbol.newMethod(
      clsSym,
      "apply",
      methodType,
      Flags.EmptyFlags,
      Symbol.noSymbol,
    )

    List(applySym)
  }

  private def makeForMacro[EN: Type](using q: Quotes): Expr[Any] = {
    import q.reflect.*

    val enumSym = TypeRepr.of[EN].typeSymbol
    val cases = enumSym.children
    // Debug: print owner and children
//    report.error(s"spliceOwner.owner.owner: ${ Symbol.spliceOwner.owner.owner }")
//    report.error(s"spliceOwner.owner: ${ Symbol.spliceOwner.owner }")
//    cases.foreach { cs =>
//      report.info(s"child: ${ cs.name } fullName: ${ cs.fullName }")
//    }
    val mods = cases.flatMap { cs =>
      val caseParams = cs.primaryConstructor.paramSymss.flatten.filter(_.isTerm)
      val objSym = Symbol.newModule(
        owner = Symbol.spliceOwner.owner.owner,
        name = cs.name + "Make",
        modFlags = Flags.EmptyFlags,
        clsFlags = Flags.EmptyFlags,
        parents = _ => List(TypeRepr.of[AnyRef]),
        decls = cls => makeApplyMethod(using q)(cls, cs),
        privateWithin = Symbol.noSymbol,
      )

      val applySym = objSym.moduleClass.declarations.find(_.name == "apply").get

      val method = DefDef(
        applySym,
        argss => {
          // argss.head = all value params including the extra param2
          // we only pass the case class params to the constructor
          val caseParams =
            cs.primaryConstructor.paramSymss.flatten.filter(_.isTerm)
          val ctorArgs = argss.head.take(caseParams.length).map(_.asExpr.asTerm)
          val construct = Apply(
            Select(New(TypeIdent(cs)), cs.primaryConstructor),
            ctorArgs,
          )
          Some(construct)
        },
      )

      val clsDef = ClassDef(
        objSym.moduleClass,
        List(TypeTree.of[AnyRef]),
        List(method),
      )
      val valDef = ValDef(objSym, None)
      List(clsDef, valDef)
    }

    Block(mods, Literal(UnitConstant())).asExpr
  }
}
