package jp.ac.titech.cs.se.refactorhub.models.element.data

import java.io.Serializable

data class Range(
    var startLine: Int = 0,
    var startColumn: Int = 0,
    var endLine: Int = 0,
    var endColumn: Int = 0
) : Serializable {
    fun isEmpty() = startLine == 0 && startColumn == 0 && endLine == 0 && endColumn == 0
}
