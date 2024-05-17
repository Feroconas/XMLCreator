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
        
        fun isValidTagName(tagName: String): Boolean {
            return tagName.isNotEmpty()
            && (tagName[0].isLetter() || tagName[0] == '_')
            && tagName.substring(1).all {
                it.isLetterOrDigit() || it == '.' || it == '_' || it == '-'
            }
        }
        
        fun isValidTagText(tagText: String?): Boolean {
            return tagText == null || (tagText.isNotBlank() && !tagText.contains('<'))
        }
        
        fun Any.toXMLElement(parent: XMLElement? = null): XMLElement {
            
            this.validateXMLAnnotations()
            val element = XMLElement(this::class.findAnnotation<Element>()!!.tagName, null, parent)
            
            this::class.memberProperties.forEach { property ->
                val propertyStringValue = property.call(this).toString()
                if (property.hasAnnotation<Attribute>()) {
                    var attributeValue = propertyStringValue
                    this::class.findAnnotations<AttributeTransform>().forEach {
                        attributeValue = it.stringTransform.createInstance().transform(attributeValue)
                    }
                    element.addAttribute(property.name, attributeValue)
                }
                else if (property.hasAnnotation<TagText>())
                    element.setTagText(propertyStringValue)
                else if (property.hasAnnotation<Element>()) {
                    // TODO TODO TODO TODO TODO TODO TODO TODO
                    if (property.returnType.isSubtypeOf(typeOf<Collection<Any?>>())) {
                        val collection = property.call(this) as Collection<Any?>
                        collection.forEach {
                            if (it != null && it::class.hasAnnotation<Element>())
                                println()
                            
                        }
                    }
                    
                    println(property.returnType.isSubtypeOf(typeOf<Collection<Any>>()))
                    println(property.returnType::class.hasAnnotation<Element>())
                    // TODO TODO TODO TODO TODO TODO TODO TODO
                }
            }
            
            this::class.findAnnotations<ElementTransform>().forEach {
                it.elementTransform.createInstance().transform(element)
            }
            
            return element
        }
    }
    
    fun getTagName(): String {
        return tagName
    }
    
    fun setTagName(tagName: String) {
        require(isValidTagName(tagName))
        this.tagName = tagName
    }
    
    fun getTagText(): String? {
        return tagText
    }
    
    fun setTagText(tagText: String?) {
        require(isValidTagText(tagText))
        this.tagText = tagText
    }
    
    fun getParent(): XMLElement? {
        return parent
    }
    
    fun getAttributes(): MutableList<XMLAttribute> {
        return attributes
    }
    
    fun addAttribute(name: String, value: String): Boolean {
        if (attributes.none { it.getName() == name }) {
            attributes.add(XMLAttribute(name, value))
            return true
        }
        return false
    }
    
    fun renameAttribute(name: String, newName: String): Boolean {
        if (attributes.any { it.getName() == newName })
            return false
        val attribute: XMLAttribute = attributes.find { it.getName() == name } ?: return false
        attribute.setName(newName)
        return true
    }
    
    fun setAttributeValue(name: String, newValue: String): Boolean {
        val attribute: XMLAttribute = attributes.find { it.getName() == name } ?: return false
        attribute.setValue(newValue)
        return true
    }
    
    fun removeAttribute(name: String): Boolean {
        return attributes.remove(attributes.find { it.getName() == name })
    }
    
    fun getChildren(): MutableList<XMLElement> {
        return children
    }
    
    fun removeChild(child: XMLElement): Boolean {
        if (children.remove(child)) {
            child.parent = null
            return true
        }
        return false
    }
    
    fun accept(visitor: (XMLElement) -> Unit) {
        children.forEach {
            it.accept(visitor)
        }
        visitor(this)
    }
    
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

