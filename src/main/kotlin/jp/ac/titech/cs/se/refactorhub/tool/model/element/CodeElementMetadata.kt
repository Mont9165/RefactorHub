package jp.ac.titech.cs.se.refactorhub.tool.model.element

import jp.ac.titech.cs.se.refactorhub.tool.model.editor.autofill.Autofill

data class CodeElementMetadata(
    val type: CodeElementType,
    val multiple: Boolean = false,
    val required: Boolean = false,
    val autofills: List<Autofill> = listOf()
)
