@file:Suppress("unused")

@Element("fuc")
class FUC1(
    @Attribute("codigo")
    val code: String,
    @Element(tagName = "nome")
    val name: String,
    @Element
    val ects: Double,
    val observations: String,
    @Element(tagName = "avaliacao", createParent = true)
    val evaluation: List<EvaluationComponent1>
)

@Element("componente")
class EvaluationComponent1(
    @Attribute(name = "nome")
    val name: String,
    @Attribute(name = "peso")
    val weight: Int
)

@Element("fuc", elementSorting = CustomElementSort::class)
class FUC2(
    @Attribute("codigo")
    val code: String,
    @Element(tagName = "nome")
    val name: String,
    @Element
    val ects: Double,
    val observations: String,
    @Element(tagName = "avaliacao")
    val evaluation: List<EvaluationComponent2>
)

@Element("componente")
class EvaluationComponent2(
    @Attribute(name = "nome")
    val name: String,
    @Attribute(name = "peso", AddPercentage::class)
    val weight: Int
)

class CustomElementSort : Comparator<XMLElement> {
    
    private val tagNameOrder = listOf("nome", "ects", "avaliacao", "componente")
    
    override fun compare(element1: XMLElement, element2: XMLElement): Int {
        val tagName1 = tagNameOrder.indexOf(element1.getTagName())
        val tagName2 = tagNameOrder.indexOf(element2.getTagName())
        return tagName1.compareTo(tagName2)
    }
}

class AddPercentage : StringTransformer {
    override fun transform(inputString: String): String {
        return "$inputString%"
    }
}
