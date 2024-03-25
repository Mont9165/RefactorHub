package jp.ac.titech.cs.se.refactorhub.core.annotator.autofill

import jp.ac.titech.cs.se.refactorhub.core.annotator.autofill.impl.PackageProcessor
import jp.ac.titech.cs.se.refactorhub.core.annotator.autofill.impl.ReferenceProcessor
import jp.ac.titech.cs.se.refactorhub.core.annotator.autofill.impl.SurroundProcessor
import jp.ac.titech.cs.se.refactorhub.core.model.DiffCategory
import jp.ac.titech.cs.se.refactorhub.core.model.annotator.autofill.Autofill
import jp.ac.titech.cs.se.refactorhub.core.model.annotator.autofill.impl.Package
import jp.ac.titech.cs.se.refactorhub.core.model.annotator.autofill.impl.Reference
import jp.ac.titech.cs.se.refactorhub.core.model.annotator.autofill.impl.Surround
import jp.ac.titech.cs.se.refactorhub.core.model.element.CodeElement
import jp.ac.titech.cs.se.refactorhub.core.model.element.CodeElementMetadata

interface AutofillProcessor<T : Autofill> {
    fun process(
        autofill: T,
        sourceCategory: DiffCategory,
        sourceElement: CodeElement,
        targetCategory: DiffCategory,
        targetMetadata: CodeElementMetadata,
        getFileCodeElementsMap: (DiffCategory) -> Map<String, List<CodeElement>>,
        isInDiffHunk: (DiffCategory, filePath: String, queryLineNumber: Int) -> Boolean
    ): List<CodeElement>
}

fun autofill(
    autofill: Autofill,
    sourceCategory: DiffCategory,
    sourceElement: CodeElement,
    targetCategory: DiffCategory,
    targetMetadata: CodeElementMetadata,
    getFileCodeElementsMap: (DiffCategory) -> Map<String, List<CodeElement>>,
    isInDiffHunk: (DiffCategory, filePath: String, queryLineNumber: Int) -> Boolean
): List<CodeElement> {
    return when (autofill) {
        is Package -> PackageProcessor().process(
            autofill,
            sourceCategory,
            sourceElement,
            targetCategory,
            targetMetadata,
            getFileCodeElementsMap,
            isInDiffHunk
        )
        is Reference -> ReferenceProcessor().process(
            autofill,
            sourceCategory,
            sourceElement,
            targetCategory,
            targetMetadata,
            getFileCodeElementsMap,
            isInDiffHunk
        )
        is Surround -> SurroundProcessor().process(
            autofill,
            sourceCategory,
            sourceElement,
            targetCategory,
            targetMetadata,
            getFileCodeElementsMap,
            isInDiffHunk
        )
        else -> listOf()
    }
}
