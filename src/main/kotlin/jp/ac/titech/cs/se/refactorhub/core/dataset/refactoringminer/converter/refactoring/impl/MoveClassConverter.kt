package jp.ac.titech.cs.se.refactorhub.core.dataset.refactoringminer.converter.refactoring.impl

import gr.uom.java.xmi.diff.MoveClassRefactoring
import jp.ac.titech.cs.se.refactorhub.core.dataset.refactoringminer.RefactoringOracle
import jp.ac.titech.cs.se.refactorhub.core.dataset.refactoringminer.converter.element.convertElement
import jp.ac.titech.cs.se.refactorhub.core.dataset.refactoringminer.converter.refactoring.RefactoringConverter
import jp.ac.titech.cs.se.refactorhub.core.dataset.refactoringminer.model.Refactoring
import jp.ac.titech.cs.se.refactorhub.core.model.element.CodeElementHolder
import jp.ac.titech.cs.se.refactorhub.core.model.element.CodeElementType

class MoveClassConverter :
    RefactoringConverter<MoveClassRefactoring> {
    override fun convert(
        refactoring: MoveClassRefactoring,
        data: RefactoringOracle.Refactoring
    ): Refactoring {
        return Refactoring(
            refactoring.refactoringType.displayName,
            data.commit,
            Refactoring.Data(
                mapOf(
                    "target class" to CodeElementHolder(
                        type = CodeElementType.ClassDeclaration,
                        elements = listOf(convertElement(refactoring.originalClass))
                    )
                ),
                mapOf(
                    "moved class" to CodeElementHolder(
                        type = CodeElementType.ClassDeclaration,
                        elements = listOf(convertElement(refactoring.movedClass))
                    )
                )
            ),
            refactoring.toString()
        )
    }
}
