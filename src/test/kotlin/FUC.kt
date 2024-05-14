@Element("fuc")
class FUC1(
    @Attribute
    val codigo: String,
    @Element
    val nome: String,
    @Element
    val ects: Double,
    val observacoes: String,
    @Element
    val avaliacao: List<ComponenteAvaliacao1>
)

@Element("componente")
class ComponenteAvaliacao1(
    @Attribute
    val nome: String,
    @Attribute
    val peso: Int
)

@Element("fuc")
@ElementTransform(CustomElementSort::class)
class FUC2(
    @Attribute
    val codigo: String,
    @Element
    val nome: String,
    @Element
    val ects: Double,
    val observacoes: String,
    val avaliacao: List<ComponenteAvaliacao2>
)

@Element("componente")
class ComponenteAvaliacao2(
    @Attribute
    val nome: String,
    @Attribute
    @AttributeTransform(AddPercentage::class)
    val peso: Int
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
