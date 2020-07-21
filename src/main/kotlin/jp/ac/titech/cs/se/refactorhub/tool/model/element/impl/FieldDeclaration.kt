package jp.ac.titech.cs.se.refactorhub.tool.model.element.impl

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jp.ac.titech.cs.se.refactorhub.tool.model.element.CodeElementInClass
import jp.ac.titech.cs.se.refactorhub.tool.model.element.CodeElementType
import jp.ac.titech.cs.se.refactorhub.tool.model.element.data.Location

@JsonDeserialize(`as` = FieldDeclaration::class)
data class FieldDeclaration(
    val name: String? = null,
    override val className: String? = null,
    override val location: Location? = null
) : CodeElementInClass {
    override val type: CodeElementType get() = CodeElementType.FieldDeclaration
}
