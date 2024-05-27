import kotlin.reflect.full.*
import kotlin.reflect.typeOf

class XMLElement(
    private var tagName: String,
    private var tagText: String? = null,
    private var parent: XMLElement? = null
) {
    private val attributes = mutableListOf<XMLAttribute>()
    private val children = mutableListOf<XMLElement>()
    
    init {
        parent?.children?.add(this)
        require(isValidTagName(tagName))
        require(isValidTagText(tagText))
    }
    
    companion object {
        
        /**
         * Checks if a given tag name is valid according to XML naming rules.
         *
         * A valid tag name must:
         * 1. Be non-empty.
         * 2. Start with a letter or an underscore ('_').
         * 3. Contain only letters, digits, periods ('.'), underscores ('_'), or hyphens ('-') after the first character.
         *
         * @param tagName The tag name to validate.
         * @return True if the tag name is valid, false otherwise.
         */
        
        fun isValidTagName(tagName: String): Boolean {
            return tagName.isNotEmpty()
            && (tagName[0].isLetter() || tagName[0] == '_')
            && tagName.substring(1).all {
                it.isLetterOrDigit() || it == '.' || it == '_' || it == '-'
            }
        }
        
        /**
         * Checks if a given tag text is valid.
         *
         * Valid tag text must be either:
         * 1. Null.
         * 2. Non-blank and must not contain the '<' character.
         *
         * @param tagText The tag text to validate.
         * @return True if the tag text is valid, false otherwise.
         */
        
        fun isValidTagText(tagText: String?): Boolean {
            return tagText == null || (tagText.isNotBlank() && !tagText.contains('<'))
        }
        
        // TODO Desparguetizar TODO Desparguetizar TODO Desparguetizar TODO Desparguetizar TODO Desparguetizar
        fun Any.toXMLElement(parent: XMLElement? = null): XMLElement {
            
            this.validateXMLAnnotations()
            val classElementAnnotation = this::class.findAnnotation<Element>()!!
            val elementTagName = classElementAnnotation.tagName.ifEmpty { this::class.simpleName!! }
            val element = XMLElement(elementTagName, null, parent)
            
            this::class.memberProperties.forEach { property ->
                val propertyStringValue = property.call(this).toString()
                if (property.hasAnnotation<Attribute>()) {
                    var attributeValue = propertyStringValue
                    attributeValue = property.findAnnotation<Attribute>()!!.attributeValueTransformer.createInstance().transform(attributeValue)
                    element.addAttribute(property.findAnnotation<Attribute>()!!.name.ifEmpty { property.name }, attributeValue)
                }
                else if (property.hasAnnotation<TagText>()) {
                    element.setTagText(classElementAnnotation.tagTextTransformer.createInstance().transform(propertyStringValue))
                }
                else if (property.hasAnnotation<Element>()) {
                    val elementAnnotation = property.findAnnotation<Element>()!!
                    val elementParent = if (elementAnnotation.createParent) XMLElement(elementAnnotation.tagName.ifEmpty { property.name }, null, element) else element
                    if (property.returnType.isSubtypeOf(typeOf<Collection<*>>())) {
                        val collection = property.call(this) as Collection<*>
                        for (collectionElement in collection) {
                            if (collectionElement == null)
                                continue
                            if (collectionElement::class.hasAnnotation<Element>())
                                collectionElement.toXMLElement(elementParent)
                            else
                                XMLElement(property.name, elementAnnotation.tagTextTransformer.createInstance().transform(collectionElement.toString()), elementParent)
                        }
                    }
                    else {
                        val propertyValue = property.call(this)
                        if (propertyValue != null) {
                            if (propertyValue::class.hasAnnotation<Element>())
                                propertyValue.toXMLElement(elementParent)
                            else
                                XMLElement(elementAnnotation.tagName.ifEmpty { property.name }, elementAnnotation.tagTextTransformer.createInstance().transform(propertyStringValue), elementParent)
                        }
                    }
                }
            }
            element.children.sortWith(classElementAnnotation.elementSorting.createInstance())
            return element
        }
        
//        fun Any.toXMLElement(parent: XMLElement? = null): XMLElement {
//
//            this.validateXMLAnnotations()
//            val classElementAnnotation = this::class.findAnnotation<Element>()!!
//            val elementTagName = classElementAnnotation.tagName.ifEmpty { this::class.simpleName!! }
//            val element = XMLElement(elementTagName, null, parent)
//
//            this::class.memberProperties.forEach { property ->
//                val propertyStringValue = property.call(this).toString()
//                if (property.hasAnnotation<Attribute>()) {
//                    var attributeValue = propertyStringValue
//                    attributeValue = property.findAnnotation<Attribute>()!!.attributeValueTransformer.createInstance().transform(attributeValue)
//                    element.addAttribute(property.findAnnotation<Attribute>()!!.name.ifEmpty { property.name }, attributeValue)
//                }
//                else if (property.hasAnnotation<TagText>()) {
//                    element.setTagText(classElementAnnotation.tagTextTransformer.createInstance().transform(propertyStringValue))
//                }
//                else if (property.hasAnnotation<Element>()) {
//                    val elementAnnotation = property.findAnnotation<Element>()!!
//                    val elementParent = if (elementAnnotation.createParent) XMLElement(elementAnnotation.tagName.ifEmpty { property.name }, null, element) else element
//                    if (property.returnType.isSubtypeOf(typeOf<Collection<*>>())) {
//                        val collection = property.call(this) as Collection<*>
//                        for (collectionElement in collection) {
//                            if (collectionElement == null)
//                                continue
//                            if (collectionElement::class.hasAnnotation<Element>())
//                                collectionElement.toXMLElement(elementParent)
//                            else
//                                XMLElement(property.name, elementAnnotation.tagTextTransformer.createInstance().transform(collectionElement.toString()), elementParent)
//                        }
//                    }
//                    else {
//                        val propertyValue = property.call(this)
//                        if (propertyValue != null) {
//                            if (propertyValue::class.hasAnnotation<Element>())
//                                propertyValue.toXMLElement(elementParent)
//                            else
//                                XMLElement(elementAnnotation.tagName.ifEmpty { property.name }, elementAnnotation.tagTextTransformer.createInstance().transform(propertyStringValue), elementParent)
//                        }
//                    }
//                }
//            }
//            element.children.sortWith(classElementAnnotation.elementSorting.createInstance())
//            return element
//        }
    }
    
    /**
     * @return The tag name.
     */
    
    fun getTagName(): String {
        return tagName
    }
    
    /**
     * Sets the tag name of this XML element.
     *
     * @param tagName The new tag name to set.
     * @throws IllegalArgumentException If the tag name is not valid.
     */
    
    fun setTagName(tagName: String) {
        require(isValidTagName(tagName))
        this.tagName = tagName
    }
    
    /**
     * @return The tag text as a String, or null if there is no tag text.
     */
    
    fun getTagText(): String? {
        return tagText
    }
    
    /**
     * Sets the tag text of this XML element.
     *
     * @param tagText The new tag text to set.
     * @throws IllegalArgumentException If the tag text is not valid.
     */
    
    fun setTagText(tagText: String?) {
        require(isValidTagText(tagText))
        this.tagText = tagText
    }
    
    /**
     * Gets the parent of this XML element.
     *
     * @return The parent XML element, or null if this element has no parent.
     */
    
    fun getParent(): XMLElement? {
        return parent
    }
    
    /**
     * Gets the attributes of this XML element.
     *
     * @return A mutable list of XMLAttribute objects.
     */
    
    fun getAttributes(): MutableList<XMLAttribute> {
        return attributes
    }
    
    /**
     * Adds an attribute to this XML element.
     *
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     * @return True if the attribute was added, false if an attribute with the same name already exists.
     */
    
    fun addAttribute(name: String, value: String): Boolean {
        if (attributes.none { it.getName() == name }) {
            attributes.add(XMLAttribute(name, value))
            return true
        }
        return false
    }
    
    /**
     * Renames an attribute of this XML element.
     *
     * @param name The current name of the attribute.
     * @param newName The new name for the attribute.
     * @return True if the attribute was renamed, false if an attribute with the new name already exists or if the attribute was not found.
     */
    
    fun renameAttribute(name: String, newName: String): Boolean {
        if (attributes.any { it.getName() == newName })
            return false
        val attribute: XMLAttribute = attributes.find { it.getName() == name } ?: return false
        attribute.setName(newName)
        return true
    }
    
    /**
     * Sets the value of an attribute of this XML element.
     *
     * @param name The name of the attribute.
     * @param newValue The new value for the attribute.
     * @return True if the attribute value was set, false if the attribute was not found.
     */
    
    fun setAttributeValue(name: String, newValue: String): Boolean {
        val attribute: XMLAttribute = attributes.find { it.getName() == name } ?: return false
        attribute.setValue(newValue)
        return true
    }
    
    /**
     * Removes an attribute from this XML element.
     *
     * @param name The name of the attribute to remove.
     * @return True if the attribute was removed, false if the attribute was not found.
     */
    
    fun removeAttribute(name: String): Boolean {
        return attributes.remove(attributes.find { it.getName() == name })
    }
    
    /**
     * Gets the children of this XML element.
     *
     * @return A mutable list of child XML elements.
     */
    
    fun getChildren(): MutableList<XMLElement> {
        return children
    }
    
    /**
     * Removes a child element from this XML element.
     *
     * @param child The child element to remove.
     * @return True if the child was removed, false if the child was not found.
     */
    
    fun removeChild(child: XMLElement): Boolean {
        if (children.remove(child)) {
            child.parent = null
            return true
        }
        return false
    }
    
    /**
     * Accepts a visitor function to visit this element and all its children.
     *
     * @param visitor The visitor function to apply to this element and its children.
     */
    
    fun accept(visitor: (XMLElement) -> Unit) {
        children.forEach {
            it.accept(visitor)
        }
        visitor(this)
    }
    
    /**
     * @return The XML representation as a String.
     */
    
    override fun toString(): String {
        
        fun XMLElement.maximumDepth(): Int {
            if (children.isEmpty())
                return 0
            return (children.maxOfOrNull { it.maximumDepth() } ?: 0) + 1
        }
        
        val maximumDepth = maximumDepth()
        
        fun XMLElement.buildString(numberOfTabs: Int = 0): String {
            return buildString {
                append("\t".repeat(numberOfTabs) + "<$tagName")
                if (attributes.isNotEmpty())
                    append(attributes.joinToString(" ", " "))
                if (numberOfTabs != maximumDepth)
                    append(">")
                if (tagText != null) {
                    if (numberOfTabs == maximumDepth)
                        append(">")
                    append(tagText)
                }
                if (children.isEmpty()) {
                    if (numberOfTabs == maximumDepth && tagText == null)
                        append("/>")
                    else
                        append("</$tagName>")
                }
                else {
                    append("\n" + children.joinToString(separator = "\n", postfix = "\n" + "\t".repeat(numberOfTabs) + "</$tagName>")
                    { it.buildString(numberOfTabs + 1) })
                }
            }
        }
        
        return buildString()
    }
}

