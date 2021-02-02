package jp.ac.titech.cs.se.refactorhub.core.model.annotator.autofill.impl

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jp.ac.titech.cs.se.refactorhub.core.model.annotator.autofill.Autofill
import jp.ac.titech.cs.se.refactorhub.core.model.annotator.autofill.AutofillType
import jp.ac.titech.cs.se.refactorhub.core.model.annotator.autofill.Follow

@JsonDeserialize(`as` = Package::class)
data class Package(
    override val follows: List<Follow>
) : Autofill {
    override val type: AutofillType get() = AutofillType.Package
}
