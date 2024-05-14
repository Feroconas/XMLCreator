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

@Element("fuc")
@ElementTransform(CustomElementSort::class)
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
    @Attribute(name = "peso")
    @AttributeTransform(AddPercentage::class)
    val weight: Int
)

class CustomElementSort : XMLElementTransform {
    override fun transform(element: XMLElement) {
        element.getChildren().sortWith { element1, element2 ->
            if (element1.getTagName() == "componente") 1 else element1.getTagName().compareTo(element2.getTagName())
        }
    }
}

class AddPercentage : XMLStringTransform {
    override fun transform(inputString: String): String {
        return "$inputString%"
    }
}
