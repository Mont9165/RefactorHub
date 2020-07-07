package jp.ac.titech.cs.se.refactorhub.tool.model.element.impl

import jp.ac.titech.cs.se.refactorhub.tool.model.element.CodeElementInMethod
import jp.ac.titech.cs.se.refactorhub.tool.model.element.CodeElementType
import jp.ac.titech.cs.se.refactorhub.tool.model.element.data.Location

data class VariableDeclaration(
    val name: String,
    override val methodName: String,
    override val className: String,
    override val location: Location
) : CodeElementInMethod {
    override val type: CodeElementType get() = CodeElementType.VariableDeclaration
}
