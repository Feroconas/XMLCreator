data class XMLAttribute(private var name: String, private var value: String) {
    
    init {
        require(isValidName(name))
        require(isValidValue(value))
    }
    
    companion object {
        
        fun isValidName(name: String): Boolean {
            return XMLElement.isValidTagName(name)
        }
        
        fun isValidValue(value: String): Boolean {
            return value.isNotBlank() && !value.contains('"')
        }
    }
    
    fun getName(): String {
        return name
    }
    
    fun getValue(): String {
        return value
    }
    
    internal fun setName(name: String) {
        require(isValidName(name))
        this.name = name
    }
    
    internal fun setValue(value: String) {
        require(isValidValue(value))
        this.value = value
    }
    
    override fun toString(): String {
        return "$name=\"$value\""
    }
}

