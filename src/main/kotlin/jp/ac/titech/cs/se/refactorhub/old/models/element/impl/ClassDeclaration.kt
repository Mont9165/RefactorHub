package jp.ac.titech.cs.se.refactorhub.old.models.element.impl

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jp.ac.titech.cs.se.refactorhub.old.models.element.Element
import jp.ac.titech.cs.se.refactorhub.old.models.element.data.Location

@JsonDeserialize(`as` = ClassDeclaration::class)
data class ClassDeclaration(
    val name: String = "",
    override val location: Location = Location()
) : Element {
    override val type: Element.Type get() = Element.Type.ClassDeclaration
}
