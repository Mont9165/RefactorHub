package jp.ac.titech.cs.se.miner2hub.converter.element

import gr.uom.java.xmi.LocationInfo
import gr.uom.java.xmi.UMLOperation
import jp.ac.titech.cs.se.miner2hub.converter.element.impl.UMLOperationConverter
import jp.ac.titech.cs.se.refactorhub.models.element.Element
import jp.ac.titech.cs.se.refactorhub.models.element.data.Location
import jp.ac.titech.cs.se.refactorhub.models.element.data.Range

interface ElementConverter<T> {
    fun convert(element: T): Element
}

fun convertElement(element: Any): Element = when (element) {
    is UMLOperation -> UMLOperationConverter().convert(element)
    else -> throw RuntimeException("${element::class.simpleName} is not implemented.")
}

fun convertLocation(locationInfo: LocationInfo): Location {
    val range = locationInfo.codeRange()
    return Location(
        locationInfo.filePath,
        Range(
            range.startLine,
            range.startColumn,
            range.endLine,
            range.endColumn
        )
    )
}
