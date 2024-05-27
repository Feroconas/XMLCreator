interface StringTransformer {
    fun transform(inputString: String): String
}

internal class NoStringTransformer : StringTransformer {
    override fun transform(inputString: String): String {
        return inputString
    }
}

internal class NoElementSorting : Comparator<XMLElement> {
    override fun compare(element1: XMLElement, element2: XMLElement): Int {
        return 0
    }
}