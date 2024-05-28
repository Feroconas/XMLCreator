import kotlin.reflect.KClass
import kotlin.reflect.full.*
import kotlin.reflect.typeOf

/**
 * Represents an XML element with a tag name, optional tag text, and a parent element.
 *
 * @property tagName The tag name of the XML element. Must be a valid tag name.
 * @property tagText The text content of the XML element. Must be valid tag text. Defaults to null.
 * @property parent The parent element of this XML element. Defaults to null.
 * @constructor Creates an XMLElement with the specified tag name, tag text, and parent.
 *
 * @throws IllegalArgumentException If the tag name or tag text is invalid.
 */

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
        
        /**
         * Converts any object annotated with [Element] to an XMLElement, creating a nested structure based on the object's
         * properties and their annotations.
         *
         * @receiver Any object to be converted to an XML element.
         * @param parent The parent XMLElement to which this element will be attached. Defaults to null.
         * @return The resulting XMLElement representing the object.
         * @throws AnnotationConfigurationException If the object or its properties have invalid or missing annotations.
         */
        
        fun Any.toXMLElement(parent: XMLElement? = null): XMLElement {
            
            validateXMLAnnotations()
            val classElementAnnotation = this::class.findAnnotation<Element>()!!
            val mainElementTagName = classElementAnnotation.tagName.ifEmpty { this::class.simpleName!! }
            val mainElement = XMLElement(mainElementTagName, null, parent)
            
            fun String.transformUsing(transformer: KClass<out StringTransformer>): String {
                return transformer.createInstance().transform(this)
            }
            
            this::class.memberProperties.forEach { property ->
                val propertyStringValue = property.call(this).toString()
                property.annotations.forEach { annotation ->
                    when (annotation) {
                        is Element -> {
                            
                            fun determineElementParent(): XMLElement {
                                return if (annotation.createParent)
                                    XMLElement(annotation.tagName.ifEmpty { property.name }, null, mainElement)
                                else
                                    mainElement
                            }
                            
                            val elementParent = determineElementParent()
                            
                            fun createChildElement(childObject: Any?) {
                                if (childObject == null)
                                    return
                                if (childObject::class.hasAnnotation<Element>())
                                    childObject.toXMLElement(elementParent)
                                else
                                    XMLElement(
                                        annotation.tagName.ifEmpty { property.name },
                                        childObject.toString().transformUsing(annotation.tagTextTransformer),
                                        elementParent
                                    )
                            }
                            
                            if (property.returnType.isSubtypeOf(typeOf<Collection<*>>())) {
                                val collection = property.call(this) as Collection<*>
                                collection.forEach { createChildElement(it) }
                            }
                            else
                                createChildElement(property.call(this))
                        }
                        
                        is TagText -> {
                            mainElement.setTagText(propertyStringValue.transformUsing(classElementAnnotation.tagTextTransformer))
                        }
                        
                        is Attribute -> {
                            val attributeValue = propertyStringValue.transformUsing(annotation.attributeTransformer)
                            mainElement.addAttribute(annotation.name.ifEmpty { property.name }, attributeValue)
                        }
                    }
                }
            }
            mainElement.children.sortWith(classElementAnnotation.elementSorting.createInstance())
            return mainElement
        }
        
        fun createElement(
            tagName: String,
            tagText: String? = null,
            attributes: MutableList<Pair<String, String>> = mutableListOf(),
            build: XMLElement.() -> Unit = {}
        ): XMLElement {
            
            val element = XMLElement(tagName, tagText, null)
            attributes.forEach {
                element.addAttribute(it.first, it.second)
            }
            return element.apply { (build(this)) }
        }
        
        fun createElement(
            tagName: String,
            tagText: String? = null,
            attributes: Pair<String, String>,
            build: XMLElement.() -> Unit = {}
        ): XMLElement {
            return createElement(tagName, tagText, mutableListOf(attributes), build)
        }
        
        infix fun Pair<String, String>.and(other: Pair<String, String>): MutableList<Pair<String, String>> {
            return mutableListOf(this, other)
        }
        
        infix fun MutableList<Pair<String, String>>.and(other: Pair<String, String>): MutableList<Pair<String, String>> {
            add(other)
            return this
        }
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
    
    fun element(
        tagName: String,
        tagText: String? = null,
        attributes: Pair<String, String>,
        build: XMLElement.() -> Unit = {}
    ): XMLElement {
        return element(tagName, tagText, mutableListOf(attributes), build)
    }
    
    fun element(
        tagName: String,
        tagText: String? = null,
        attributes: MutableList<Pair<String, String>> = mutableListOf(),
        build: XMLElement.() -> Unit = {}
    ): XMLElement {
        
        val element = XMLElement(tagName, tagText, this)
        attributes.forEach {
            element.addAttribute(it.first, it.second)
        }
        return element.apply { (build(this)) }
    }
    
    operator fun get(index: Int): XMLElement = children[index]
    
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

