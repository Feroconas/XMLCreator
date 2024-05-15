
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class XMLElementTest {

    //             plano
    //               /\
    //              /  \
    //          curso   filho
    //                   /  \
    //                  /    \
    //              neto2    neto
    //                          \
    //                           \
    //                          bisneto

    private val planoElement = XMLElement("plano")
    private val cursoElement = XMLElement("curso", "Mestrado em Engenharia Informática", planoElement)
    private val filhoPlano = XMLElement("filho", parent = planoElement)
    private val netoPlano = XMLElement("neto", parent = filhoPlano)
    private val netoPlano2 = XMLElement("neto2", parent = filhoPlano)
    private val bisnetoPlano = XMLElement("bisneto", "bisneto_tag", parent = netoPlano)

    @Test
    fun gettersAndSetters() {
        assertEquals("plano", planoElement.getTagName())
        planoElement.setTagName("planeamento")
        assertEquals("planeamento", planoElement.getTagName())
        assertNull(planoElement.getTagText())
        planoElement.setTagText("o grande plano")
        assertEquals("o grande plano", planoElement.getTagText())
        planoElement.setTagText(null)
        assertNull(planoElement.getTagText())
        assertNull(planoElement.getParent())
        assertEquals(planoElement, cursoElement.getParent())
        assertEquals(0, planoElement.getAttributes().size)
        planoElement.addAttribute("faculdade", "ISCTE")
        assertEquals(XMLAttribute("faculdade", "ISCTE"), planoElement.getAttributes()[0])
        assertEquals(0, netoPlano2.getChildren().size)
        assertEquals(filhoPlano, planoElement.getChildren()[1])
    }

    @Test
    fun tagNameAndTagTextValidation() {
        assertThrows(IllegalArgumentException::class.java) { planoElement.setTagName("") }
        assertThrows(IllegalArgumentException::class.java) { planoElement.setTagName("3plano") }
        assertThrows(IllegalArgumentException::class.java) { planoElement.setTagName("plano,") }
        assertThrows(IllegalArgumentException::class.java) { planoElement.setTagName("plan o") }
        assertDoesNotThrow { planoElement.setTagName("_plano") }
        assertDoesNotThrow { planoElement.setTagName("plano34234") }
        assertDoesNotThrow { planoElement.setTagName("plano.-_") }
        assertDoesNotThrow { planoElement.setTagName("p") }
        assertThrows(IllegalArgumentException::class.java) { planoElement.setTagText("") }
        assertThrows(IllegalArgumentException::class.java) { planoElement.setTagText("   ") }
        assertThrows(IllegalArgumentException::class.java) { planoElement.setTagText("plano do Ano<") }
        assertDoesNotThrow { planoElement.setTagText("plano") }
        assertDoesNotThrow { planoElement.setTagText("81n  flP-.,+º´«'fd>'") }

    }

    @Test
    fun attributeValidation() {
        assertThrows(IllegalArgumentException::class.java) { cursoElement.addAttribute("", "2024") }
        assertThrows(IllegalArgumentException::class.java) { cursoElement.addAttribute("3ano", "2024") }
        assertThrows(IllegalArgumentException::class.java) { cursoElement.addAttribute("ano,", "2024") }
        assertThrows(IllegalArgumentException::class.java) { cursoElement.addAttribute("an o", "2024") }
        assertDoesNotThrow { cursoElement.addAttribute("_ano", "2024") }
        assertDoesNotThrow { cursoElement.addAttribute("ano34234", "?????") }
        assertDoesNotThrow { cursoElement.addAttribute("ano.-_", ".,.,.,<>") }
        assertThrows(IllegalArgumentException::class.java) { cursoElement.renameAttribute("_ano", "") }
        assertThrows(IllegalArgumentException::class.java) { cursoElement.renameAttribute("_ano", "3ano") }
        assertThrows(IllegalArgumentException::class.java) { cursoElement.renameAttribute("_ano", "ano,") }
        assertThrows(IllegalArgumentException::class.java) { cursoElement.renameAttribute("_ano", "an o") }
        assertDoesNotThrow { cursoElement.renameAttribute("a", "_ano") }
        assertDoesNotThrow { cursoElement.renameAttribute("_ano", "ano34234") }
        assertDoesNotThrow { cursoElement.renameAttribute("ano34234", "ano.-_") }
        assertThrows(IllegalArgumentException::class.java) { cursoElement.setAttributeValue("_ano", "  ") }
        assertThrows(IllegalArgumentException::class.java) { cursoElement.setAttributeValue("_ano", "2024\"2024") }

    }

    @Test
    fun removeChild() {
        assertFalse(planoElement.removeChild(netoPlano))
        assertFalse(cursoElement.removeChild(bisnetoPlano))
        assertFalse(cursoElement.removeChild(cursoElement))
        assertFalse(netoPlano.removeChild(filhoPlano))
        assertTrue(planoElement.removeChild(filhoPlano))
        assertNull(filhoPlano.getParent())
        assertNotNull(netoPlano.getParent())
        assertNotNull(cursoElement.getParent())
    }

    @Test
    fun addAttribute() {
        assertTrue(cursoElement.addAttribute("ano", "2023"))
        assertFalse(cursoElement.addAttribute("ano", "2023"))
        assertFalse(cursoElement.addAttribute("ano", "2024"))
        assertEquals(XMLAttribute("ano", "2023"), cursoElement.getAttributes()[0])
    }

    @Test
    fun renameAttribute() {
        assertFalse(cursoElement.renameAttribute("ano", "anoNovo"))
        cursoElement.addAttribute("ano", "2023")
        cursoElement.addAttribute("mes", "2")
        assertTrue(cursoElement.renameAttribute("ano", "anoNovo"))
        assertFalse(cursoElement.renameAttribute("an", "anoNovo"))
        assertFalse(cursoElement.renameAttribute("mes", "mes"))
        assertFalse(cursoElement.renameAttribute("mes", "anoNovo"))
        assertEquals(XMLAttribute("anoNovo", "2023"), cursoElement.getAttributes()[0])

    }

    @Test
    fun setAttributeValue() {
        assertFalse(cursoElement.setAttributeValue("ano", "2024"))
        cursoElement.addAttribute("ano", "2023")
        assertFalse(cursoElement.setAttributeValue("mes", "2022"))
        assertTrue(cursoElement.setAttributeValue("ano", "2024"))
        assertTrue(cursoElement.setAttributeValue("ano", "2024"))
        assertEquals(XMLAttribute("ano", "2024"), cursoElement.getAttributes()[0])
    }

    @Test
    fun removeAttribute() {
        assertFalse(cursoElement.removeAttribute("ano"))
        cursoElement.addAttribute("ano", "2023")
        cursoElement.addAttribute("mes", "2")
        assertTrue(cursoElement.removeAttribute("mes"))
        assertEquals(1, cursoElement.getAttributes().size)
    }

}