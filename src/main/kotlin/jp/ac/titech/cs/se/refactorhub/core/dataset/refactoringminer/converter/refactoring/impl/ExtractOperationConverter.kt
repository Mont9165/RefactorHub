package jp.ac.titech.cs.se.refactorhub.core.dataset.refactoringminer.converter.refactoring.impl

import gr.uom.java.xmi.diff.ExtractOperationRefactoring
import jp.ac.titech.cs.se.refactorhub.core.dataset.refactoringminer.RefactoringOracle
import jp.ac.titech.cs.se.refactorhub.core.dataset.refactoringminer.converter.element.convertElement
import jp.ac.titech.cs.se.refactorhub.core.dataset.refactoringminer.converter.refactoring.RefactoringConverter
import jp.ac.titech.cs.se.refactorhub.core.dataset.refactoringminer.model.Refactoring
import jp.ac.titech.cs.se.refactorhub.core.model.element.CodeElementHolder
import jp.ac.titech.cs.se.refactorhub.core.model.element.CodeElementType

class ExtractOperationConverter :
    RefactoringConverter<ExtractOperationRefactoring> {
    override fun convert(
        refactoring: ExtractOperationRefactoring,
        data: RefactoringOracle.Refactoring
    ): Refactoring {
        return Refactoring(
            refactoring.refactoringType.displayName,
            data.commit,
            Refactoring.Data(
                mapOf(
                    "target method" to CodeElementHolder(
                        type = CodeElementType.MethodDeclaration,
                        elements = listOf(convertElement(refactoring.sourceOperationBeforeExtraction))
                    ),
                    "extracted code" to CodeElementHolder(
                        type = CodeElementType.CodeFragment,
                        multiple = true,
                        elements = refactoring.extractedCodeFragmentsFromSourceOperation.map { convertElement(it) }
                    )
                ),
                mapOf(
                    "target method" to CodeElementHolder(
                        type = CodeElementType.MethodDeclaration,
                        elements = listOf(convertElement(refactoring.sourceOperationAfterExtraction))
                    ),
                    "extracted method" to CodeElementHolder(
                        type = CodeElementType.MethodDeclaration,
                        elements = listOf(convertElement(refactoring.extractedOperation))
                    ),
                    "invocation" to CodeElementHolder(
                        type = CodeElementType.MethodInvocation,
                        elements = refactoring.extractedOperationInvocations.map { convertElement(it) }
                    ),
                    "extracted code" to CodeElementHolder(
                        type = CodeElementType.CodeFragment,
                        multiple = true,
                        elements = refactoring.extractedCodeFragmentsToExtractedOperation.map { convertElement(it) }
                    )
                )
            ),
            refactoring.toString()
        )
    }
}
