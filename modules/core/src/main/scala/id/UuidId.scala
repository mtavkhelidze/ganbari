//package ge.zgharbi.ganbari.core
//package id
//
//import scala.annotation.{MacroAnnotation, experimental}
//import scala.quoted.*
//
//@experimental
//class newId extends MacroAnnotation {
//  def transform(using
//      Quotes,
//  )(
//      definition: quotes.reflect.Definition,
//      companion: Option[quotes.reflect.Definition],
//  ): List[quotes.reflect.Definition] = {
//    import quotes.reflect.*
//
//    definition match
//      case TypeDef(name, _) =>
//        val owner = Symbol.spliceOwner
//
//        // 1. Create `private object <Name>` symbol
//        val modSym = Symbol.newModule(
//          owner,
//          name,
//          Flags.Private, // modFlags  — the val side
//          Flags.EmptyFlags, // clsFlags  — the class side
//          parents = List(TypeRepr.of[Object]),
//          decls = clsSym =>
//            // 2. `sealed trait Tag` inside the object
//            val tagSym = Symbol.newClass(
//              clsSym,
//              "Tag",
//              parents = List(TypeRepr.of[Object]),
//              decls = _ => Nil,
//              selfType = None,
//            )
//            // 3. `type Type = IdType[Tag]`
//            val typeSym = Symbol.newTypeAlias(
//              clsSym,
//              "Type",
//              Flags.EmptyFlags,
//              TypeRepr.of[IdType].appliedTo(tagSym.typeRef),
//              Symbol.noSymbol,
//            )
//            List(tagSym, typeSym)
//          ,
//          privateWithin = Symbol.noSymbol,
//        )
//
//        val clsSym = modSym.moduleClass
//        val tagSym = clsSym.declaredType("Tag").head
//        val typeSym = clsSym.declaredType("Type").head
//
//        // 4. Build the `sealed trait Tag` tree
//        val tagDef = ClassDef(
//          tagSym,
//          parents = List(TypeTree.of[Object]),
//          body = Nil,
//        )
//
//        // 5. Build `type Type = IdType[Tag]` tree
//        val typeDef = TypeDef(typeSym, TypeIdent(tagSym)) // RHS is Tag
//
//        // 6. Build the object body via ClassDef.module
//        val (modValDef, modClassDef) =
//          ClassDef.module(
//            modSym,
//            List(TypeTree.of[Object]),
//            List(tagDef, typeDef),
//          )
//
//        // 7. Rewrite the opaque type RHS: `<Name> = <name_object>.Type`
//        val newRhs = TypeSelect(Ref(modSym), "Type")
//        val newTypeDef = TypeDef.copy(definition)(name, newRhs)
//
//        List(modValDef, modClassDef, newTypeDef)
//
//      case _ =>
//        report.errorAndAbort(
//          "@newId must annotate an opaque type",
//          definition.pos,
//        )
//  }
//}
