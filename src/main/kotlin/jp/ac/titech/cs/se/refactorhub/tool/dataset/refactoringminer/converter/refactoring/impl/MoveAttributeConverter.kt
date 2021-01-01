package jp.ac.titech.cs.se.refactorhub.tool.dataset.refactoringminer.converter.refactoring.impl

import gr.uom.java.xmi.diff.MoveAttributeRefactoring
import jp.ac.titech.cs.se.refactorhub.tool.dataset.refactoringminer.RefactoringOracle
import jp.ac.titech.cs.se.refactorhub.tool.dataset.refactoringminer.converter.element.convertElement
import jp.ac.titech.cs.se.refactorhub.tool.dataset.refactoringminer.converter.refactoring.RefactoringConverter
import jp.ac.titech.cs.se.refactorhub.tool.dataset.refactoringminer.model.Refactoring
import jp.ac.titech.cs.se.refactorhub.tool.model.element.CodeElementHolder
import jp.ac.titech.cs.se.refactorhub.tool.model.element.CodeElementType

class MoveAttributeConverter :
    RefactoringConverter<MoveAttributeRefactoring> {
    override fun convert(
        refactoring: MoveAttributeRefactoring,
        data: RefactoringOracle.Refactoring
    ): Refactoring {
        return Refactoring(
            refactoring.refactoringType.displayName,
            data.commit,
            Refactoring.Data(
                mapOf(
                    "target field" to CodeElementHolder(
                        type = CodeElementType.FieldDeclaration,
                        elements = listOf(convertElement(refactoring.originalAttribute))
                    )
                ),
                mapOf(
                    "moved field" to CodeElementHolder(
                        type = CodeElementType.FieldDeclaration,
                        elements = listOf(convertElement(refactoring.movedAttribute))
                    )
                )
            ),
            refactoring.toString()
        )
    }
}
