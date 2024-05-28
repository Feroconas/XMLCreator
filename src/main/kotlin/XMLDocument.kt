import java.io.File

/**
 * Represents an XML document with a root element, version, and encoding.
 *
 * @property root The root element of the XML document.
 * @constructor Creates an XMLDocument with the specified root element.
 */

class XMLDocument(val root: XMLElement) {
    private val version: XMLAttribute = XMLAttribute("version", DEFAULT_VERSION)
    private val encoding: XMLAttribute = XMLAttribute("encoding", DEFAULT_ENCODING)
    
    constructor(version: String, encoding: String, root: XMLElement) : this(root) {
        setVersion(version)
        setEncoding(encoding)
    }
    
    companion object {
        const val DEFAULT_VERSION = "1.0"
        const val DEFAULT_ENCODING = "UTF-8"
    }
    
    
    /**
     * Gets the version of the XML document.
     *
     * @return The version as a String.
     */
    
    fun getVersion(): String {
        return version.getValue()
    }
    
    /**
     * Gets the encoding of the XML document.
     *
     * @return The encoding as a String.
     */
    
    fun getEncoding(): String {
        return encoding.getValue()
    }
    
    /**
     * Sets the version of the XML document.
     *
     * @param version The version to set.
     */
    
    fun setVersion(version: String) {
        this.version.setValue(version)
    }
    
    /**
     * Sets the encoding of the XML document.
     *
     * @param encoding The encoding to set.
     */
    
    fun setEncoding(encoding: String) {
        this.encoding.setValue(encoding)
    }
    
    /**
     * Adds an attribute to all elements with a specific tag name.
     *
     * @param tagName The tag name of elements to add the attribute to.
     * @param attributeName The name of the attribute to add.
     * @param attributeValue The value of the attribute to add.
     */
    
    fun addAttributeGlobally(tagName: String, attributeName: String, attributeValue: String) {
        root.accept { if (it.getTagName() == tagName) it.addAttribute(attributeName, attributeValue) }
    }
    
    /**
     * Renames an attribute globally across all elements.
     *
     * @param name The current name of the attribute.
     * @param newName The new name for the attribute.
     */
    
    fun renameAttributeGlobally(name: String, newName: String) {
        root.accept { it.renameAttribute(name, newName) }
    }
    
    /**
     * Removes an attribute globally from all elements.
     *
     * @param name The name of the attribute to remove.
     */
    
    fun removeAttributeGlobally(name: String) {
        root.accept { it.removeAttribute(name) }
    }
    
    /**
     * Renames elements with a specific tag name globally.
     *
     * @param tagName The current tag name of the elements to rename.
     * @param newTagName The new tag name for the elements.
     */
    
    fun renameElementGlobally(tagName: String, newTagName: String) {
        root.accept { if (it.getTagName() == tagName) it.setTagName(newTagName) }
    }
    
    /**
     * Removes elements with a specific tag name globally.
     *
     * @param tagName The tag name of the elements to remove.
     */
    
    fun removeElementGlobally(tagName: String) {
        root.accept { parent ->
            parent.getChildren().filter { child -> child.getTagName() == tagName }.forEach {
                parent.removeChild(it)
            }
        }
    }
    
    /**
     * Saves the XML document to a file.
     *
     * @param filePath The path to the file where the document should be saved.
     */
    
    fun saveToFile(filePath: String) {
        val file = File(filePath)
        file.printWriter().use { output -> output.print(this) }
    }
    
    /**
     * @return The XML document as a formatted String.
     */
    
    override fun toString(): String {
        return "<?xml $version $encoding?>\n$root"
    }
    
    /**
     * Finds elements by an XPath expression.
     *
     * @param xPathExpression The XPath expression to use for finding elements.
     * @return A list of elements that match the XPath expression.
     */
    
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
