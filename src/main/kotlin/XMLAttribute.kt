/**
 * Represents an attribute of an XML element.
 *
 * @property name The name of the attribute. Must be a valid XML attribute name.
 * @property value The value of the attribute. Must be a valid XML attribute value.
 * @constructor Creates an XMLAttribute with the specified name and value.
 *
 * @throws IllegalArgumentException if the name or value is invalid.
 */

data class XMLAttribute(private var name: String, private var value: String) {
    
    init {
        require(isValidName(name))
        require(isValidValue(value))
    }
    
    companion object {
        
        /**
         * Validates whether a given name is a valid XML element attribute name.
         *
         * @param name The name to validate.
         * @return True if the name is a valid tag name, false otherwise.
         */
        
        fun isValidName(name: String): Boolean {
            return XMLElement.isValidTagName(name)
        }
        
        /**
         * Validates whether a given value is a valid XML attribute value.
         *
         * @param value the value to validate.
         * @return True if the value is not blank and does not contain double quotes, false otherwise.
         */
        
        fun isValidValue(value: String): Boolean {
            return value.isNotBlank() && !value.contains('"')
        }
    }
    
    /**
     * @return The name of the attribute.
     */
    fun getName(): String {
        return name
    }
    
    /**
     * @return The value of the attribute.
     */
    
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

