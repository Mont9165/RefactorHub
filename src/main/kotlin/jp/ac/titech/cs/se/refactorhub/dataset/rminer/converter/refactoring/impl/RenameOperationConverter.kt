package jp.ac.titech.cs.se.refactorhub.dataset.rminer.converter.refactoring.impl

import gr.uom.java.xmi.diff.RenameOperationRefactoring
import jp.ac.titech.cs.se.refactorhub.dataset.rminer.converter.refactoring.RefactoringConverter
import jp.ac.titech.cs.se.refactorhub.dataset.rminer.converter.refactoring.RefactoringOutput
import jp.ac.titech.cs.se.refactorhub.dataset.rminer.converter.refactoring.convertCommit
import jp.ac.titech.cs.se.refactorhub.dataset.rminer.converter.element.convertElement
import jp.ac.titech.cs.se.refactorhub.dataset.rminer.oracle.RefactoringMetadata
import jp.ac.titech.cs.se.refactorhub.models.Refactoring
import jp.ac.titech.cs.se.refactorhub.models.element.Element

class RenameOperationConverter :
    RefactoringConverter<RenameOperationRefactoring> {
    override fun convert(refactoring: RenameOperationRefactoring, metadata: RefactoringMetadata): RefactoringOutput {
        return RefactoringOutput(
            refactoring.refactoringType.displayName,
            refactoring.toString(),
            convertCommit(metadata.commit),
            Refactoring.Data(
                mutableMapOf(
                    "target method" to Element.Data(
                        type = Element.Type.MethodDeclaration,
                        required = true,
                        elements = mutableListOf(convertElement(refactoring.originalOperation))
                    )
                ),
                mutableMapOf(
                    "renamed method" to Element.Data(
                        type = Element.Type.MethodDeclaration,
                        required = true,
                        elements = mutableListOf(convertElement(refactoring.renamedOperation))
                    )
                )
            )
        )
    }
}
