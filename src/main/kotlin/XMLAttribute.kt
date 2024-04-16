data class XMLAttribute(internal var name: String, internal var value: String) {

    fun getName(): String {
        return name
    }

    fun getValue(): String {
        return value
    }

    override fun toString(): String {
        return "$name=\"$value\""
    }

}

