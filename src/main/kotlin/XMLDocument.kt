import java.io.File

class XMLDocument(private val root: XMLElement) {
    private val version: XMLAttribute = XMLAttribute("version", DEFAULT_VERSION)
    private val encoding: XMLAttribute = XMLAttribute("encoding", DEFAULT_ENCODING)

    companion object {
        const val DEFAULT_VERSION = "1.0"
        const val DEFAULT_ENCODING = "UTF-8"
    }

    fun getVersion(): String {
        return version.value
    }

    fun getEncoding(): String {
        return encoding.value
    }

    fun setVersion(version: String) {
        this.version.value = version
    }

    fun setEncoding(encoding: String) {
        this.encoding.value = encoding
    }

    fun addAttributeGlobally(tagName: String, attributeName: String, attributeValue: String) {
        root.accept { if (it.tagName == tagName) it.addAttribute(attributeName, attributeValue) }
    }

    fun renameAttributeGlobally(name: String, newName: String) {
        root.accept { it.renameAttribute(name, newName) }
    }

    fun removeAttributeGlobally(name: String) {
        root.accept { it.removeAttribute(name) }
    }

    fun renameElementGlobally(tagName: String, newTagName: String) {
        root.accept { if (it.tagName == tagName) it.tagName = newTagName }
    }

    fun removeElementGlobally(tagName: String) { // TODO() Isto funciona mas as "children" dos elementos removidos não são removidas
        root.accept { it.getChildren().removeIf { child -> child.tagName == tagName } }
    }

    fun saveToFile(filePath: String) {
        val file = File(filePath)
        file.printWriter().use { output -> output.print(this) }
    }

    override fun toString(): String {
        return "<?xml $version $encoding?>\n$root"
    }

    /*
    *
    *
    *
    * */
    fun findElementsByXPath(xPathExpression: String): MutableList<XMLElement> {

        val elements = mutableListOf<XMLElement>()
        val tagNames = xPathExpression.split("/")
        if (tagNames.isEmpty())
            return elements

        fun XMLElement.search(pointer: Int) {
            if (tagNames[pointer] == tagName) {
                if (pointer == tagNames.lastIndex) {
                    elements.add(this)
                    return
                }
                getChildren().forEach { it.search(pointer + 1) }
            }
            getChildren().forEach { it.search(0) }
        }

        root.search(0)
        return elements
    }
}
