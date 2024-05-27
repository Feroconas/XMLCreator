import XMLElement.Companion.toXMLElement
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class XMLAnnotationsTest {
    
    private val fuc1 = FUC1(
        "M4310", "Programação Avançada", 6.0, "la la...",
        listOf(EvaluationComponent1("Quizzes", 20), EvaluationComponent1("Projeto", 80))
    )
    
    private val fuc2 = FUC2(
        "M4310", "Programação Avançada", 6.0, "la la...",
        listOf(EvaluationComponent2("Quizzes", 20), EvaluationComponent2("Projeto", 80))
    )
    
    private val fuc3 = FUC3("Programaçaum Abançada", listOf("ListElement1","ListElement2","ListElement3"),24)
    
    private val emptyClass = EmptyClass()
    
    private val invalidClass = InvalidClass("InvalidClass")
    
    private val invalidClass2 = InvalidClass2("name", "aaaa")
    
    /**
     * Tests the `toXMLElement` method on various objects and ensures that invalid configurations throw the appropriate exceptions.
     */
    @Test
    fun fucToXMLElement() {
        assertEquals("<fuc codigo=\"M4310\">\n" +
        "\t<ects>6.0</ects>\n" +
        "\t<avaliacao>\n" +
        "\t\t<componente nome=\"Quizzes\" peso=\"20\"/>\n" +
        "\t\t<componente nome=\"Projeto\" peso=\"80\"/>\n" +
        "\t</avaliacao>\n" +
        "\t<nome>Programação Avançada</nome>\n" +
        "</fuc>",fuc1.toXMLElement().toString())
      
        assertEquals("<fuc codigo=\"M4310\">\n" +
        "\t<nome>Programação Avançada</nome>\n" +
        "\t<ects>6.0</ects>\n" +
        "\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
        "\t<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
        "</fuc>",fuc2.toXMLElement().toString())
    
        assertEquals("<fuc>Programaçaum Abançada\n" +
        "\t<evaluation>\n" +
        "\t\t<evaluation>ListElement1</evaluation>\n" +
        "\t\t<evaluation>ListElement2</evaluation>\n" +
        "\t\t<evaluation>ListElement3</evaluation>\n" +
        "\t</evaluation>\n" +
        "\t<tagTextTransformer>24%</tagTextTransformer>\n" +
        "</fuc>",fuc3.toXMLElement().toString())
       
        assertEquals("<EmptyClass/>",emptyClass.toXMLElement().toString())
        
        assertThrows<AnnotationConfigurationException> { invalidClass.toXMLElement() }
        assertThrows<AnnotationConfigurationException> { invalidClass2.toXMLElement() }
        assertThrows<AnnotationConfigurationException> { "1234".toXMLElement() }
    }
}