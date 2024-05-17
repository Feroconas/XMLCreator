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
    
    @Test
    fun fucToXMLElement() {
        fuc1.toXMLElement()
        fuc2.toXMLElement()
        assertThrows<IllegalArgumentException> { "4234".toXMLElement() }
    }
}