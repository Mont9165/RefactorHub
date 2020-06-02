package jp.ac.titech.cs.se.refactorhub.models.element.impl

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jp.ac.titech.cs.se.refactorhub.models.element.Element
import jp.ac.titech.cs.se.refactorhub.models.element.ElementInMethod
import jp.ac.titech.cs.se.refactorhub.models.element.data.Location

@JsonDeserialize(`as` = VariableDeclaration::class)
data class VariableDeclaration(
    val name: String = "",
    override val methodName: String = "",
    override val className: String = "",
    override val location: Location = Location()
) : ElementInMethod {
    override val type: Element.Type get() = Element.Type.VariableDeclaration
}
