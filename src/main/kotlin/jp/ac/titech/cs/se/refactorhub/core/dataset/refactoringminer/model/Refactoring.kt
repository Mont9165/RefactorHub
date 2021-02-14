package jp.ac.titech.cs.se.refactorhub.core.dataset.refactoringminer.model

import jp.ac.titech.cs.se.refactorhub.core.model.element.CodeElementHolder
import jp.ac.titech.cs.se.refactorhub.core.model.refactoring.Refactoring

data class Refactoring(
    override val type: String,
    override val commit: Commit,
    override val data: Data = Data(),
    override val description: String = ""
) : Refactoring {
    data class Data(
        override val before: MutableMap<String, CodeElementHolder> = mutableMapOf(),
        override val after: MutableMap<String, CodeElementHolder> = mutableMapOf()
    ) : Refactoring.Data
}
