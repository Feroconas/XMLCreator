import java.io.File

class XMLDocument(val root: XMLElement) {
    private val version: XMLAttribute = XMLAttribute("version", DEFAULT_VERSION)
    private val encoding: XMLAttribute = XMLAttribute("encoding", DEFAULT_ENCODING)

    companion object {
        const val DEFAULT_VERSION = "1.0"
        const val DEFAULT_ENCODING = "UTF-8"
    }

    fun getVersion(): String {
        return version.getValue()
    }

    fun getEncoding(): String {
        return encoding.getValue()
    }

    fun setVersion(version: String) {
        this.version.setValue(version)
    }

    fun setEncoding(encoding: String) {
        this.encoding.setValue(encoding)
    }

    fun addAttributeGlobally(tagName: String, attributeName: String, attributeValue: String) {
        root.accept { if (it.getTagName() == tagName) it.addAttribute(attributeName, attributeValue) }
    }

    fun renameAttributeGlobally(name: String, newName: String) {
        root.accept { it.renameAttribute(name, newName) }
    }

    fun removeAttributeGlobally(name: String) {
        root.accept { it.removeAttribute(name) }
    }

    fun renameElementGlobally(tagName: String, newTagName: String) {
        root.accept { if (it.getTagName() == tagName) it.setTagName(newTagName) }
    }

    fun removeElementGlobally(tagName: String) {
        root.accept { parent ->
            parent.getChildren().filter { child -> child.getTagName() == tagName }.forEach {
                parent.removeChild(it)
            }
        }
    }

    fun saveToFile(filePath: String) {
        val file = File(filePath)
        file.printWriter().use { output -> output.print(this) }
    }

    override fun toString(): String {
        return "<?xml $version $encoding?>\n$root"
    }

    fun findElementsByXPath(xPathExpression: String): MutableList<XMLElement> {

        val elements = mutableListOf<XMLElement>()
        val tagNames = xPathExpression.split("/")
        if (tagNames.isEmpty())
            return elements

        fun XMLElement.search(tagNamesIndex: Int = 0) {
            when {
                tagNames[tagNamesIndex] != getTagName() -> getChildren().forEach { it.search(0) }
                tagNamesIndex != tagNames.lastIndex -> getChildren().forEach { it.search(tagNamesIndex + 1) }
                else -> elements.add(this)
            }
        }

        root.search()
        return elements
    }
}
