class XMLElement(
    internal var tagName: String,
    private var tagText: String? = null,
    private val parent: XMLElement? = null // TODO() Set null when removed from parent?
) {
    private val attributes = mutableListOf<XMLAttribute>()
    private val children = mutableListOf<XMLElement>()

    init {
        parent?.children?.add(this)
    }

    fun getTagName(): String {
        return tagName
    }

    fun setTagName(tagName: String) {
        this.tagName = tagName
    }

    fun getTagText(): String? {
        return tagText
    }

    fun setTagText(tagText: String?) {
        this.tagText = tagText
    }

    fun getParent(): XMLElement? {
        return parent
    }

    fun getAttributes(): MutableList<XMLAttribute> {
        return attributes
    }

    fun addAttribute(name: String, value: String): Boolean {
        if (attributes.none { it.name == name }) {
            attributes.add(XMLAttribute(name, value))
            return true
        }
        return false
    }

    fun renameAttribute(name: String, newName: String): Boolean {
        if (attributes.any { it.name == newName })
            return false
        val attribute: XMLAttribute = attributes.find { it.name == name } ?: return false
        attribute.name = newName
        return true
    }

    fun setAttributeValue(name: String, newValue: String): Boolean {
        val attribute: XMLAttribute = attributes.find { it.name == name } ?: return false
        attribute.value = newValue
        return true
    }

    fun removeAttribute(name: String): Boolean {
        return attributes.remove(attributes.find { it.name == name })
    }

    fun getChildren(): MutableList<XMLElement> {
        return children
    }

    fun removeChild(childTagName: String): Boolean { // TODO Fazer com Visitor para remover os filhos (e netos, etc) dos elementos removidos?
        val child = children.find { it.tagName == childTagName }
        child?.accept { it.children.clear() } ?: return false
        return true
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

        fun XMLElement.buildString(numberOfTabs: Int): String {
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
                } else {
                    append("\n" + children.joinToString(separator = "\n", postfix = "\n" + "\t".repeat(numberOfTabs) + "</$tagName>")
                    { it.buildString(numberOfTabs + 1) })
                }
            }
        }

        return buildString(0)
    }
}
