package jp.ac.titech.cs.se.refactorhub.core.model.element.data

data class Range(
    var startLine: Int,
    var startColumn: Int,
    var endLine: Int,
    var endColumn: Int
)
