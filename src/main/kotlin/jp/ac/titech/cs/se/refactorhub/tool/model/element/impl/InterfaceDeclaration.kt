package jp.ac.titech.cs.se.refactorhub.tool.model.element.impl

import jp.ac.titech.cs.se.refactorhub.tool.model.element.CodeElement
import jp.ac.titech.cs.se.refactorhub.tool.model.element.CodeElementType
import jp.ac.titech.cs.se.refactorhub.tool.model.element.data.Location

data class InterfaceDeclaration(
    val name: String,
    override val location: Location
) : CodeElement {
    override val type: CodeElementType get() = CodeElementType.InterfaceDeclaration
}
