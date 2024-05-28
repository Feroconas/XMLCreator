import XMLElement.Companion.and
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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
    
    /**
     * Tests the getters and setters of the XMLElement class.
     */
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
    
    /**
     * Tests the validation of tag names and tag texts in the XMLElement class.
     */
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
    
    /**
     * Tests the validation of attributes in the XMLElement class.
     */
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
    
    /**
     * Tests removing a child element from its parent.
     */
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
    
    /**
     * Tests adding an attribute to an XML element.
     */
    @Test
    fun addAttribute() {
        assertTrue(cursoElement.addAttribute("ano", "2023"))
        assertFalse(cursoElement.addAttribute("ano", "2023"))
        assertFalse(cursoElement.addAttribute("ano", "2024"))
        assertEquals(XMLAttribute("ano", "2023"), cursoElement.getAttributes()[0])
    }
    
    /**
     * Tests renaming an attribute of an XML element.
     */
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
    
    /**
     * Tests setting the value of an attribute in an XML element.
     */
    @Test
    fun setAttributeValue() {
        assertFalse(cursoElement.setAttributeValue("ano", "2024"))
        cursoElement.addAttribute("ano", "2023")
        assertFalse(cursoElement.setAttributeValue("mes", "2022"))
        assertTrue(cursoElement.setAttributeValue("ano", "2024"))
        assertTrue(cursoElement.setAttributeValue("ano", "2024"))
        assertEquals(XMLAttribute("ano", "2024"), cursoElement.getAttributes()[0])
    }
    
    /**
     * Tests removing an attribute from an XML element.
     */
    @Test
    fun removeAttribute() {
        assertFalse(cursoElement.removeAttribute("ano"))
        cursoElement.addAttribute("ano", "2023")
        cursoElement.addAttribute("mes", "2")
        assertTrue(cursoElement.removeAttribute("mes"))
        assertEquals(1, cursoElement.getAttributes().size)
    }
    
    @Test
    fun domainSpecificLanguage() {
        val element1 =
            XMLElement.createElement("menu") {
                element("hamburguer") {
                    element("tipo", "Normal")
                    element("ingrediente", attributes = "nome" to "carne")
                    element("ingrediente", "muito fresca", attributes = ("nome" to "alface") and ("cor" to "verde"))
                    element("ingrediente", attributes = ("nome" to "queijo") and ("cor" to "amarelo"))
                }
                element("sandes") {
                    element("tipo", "Oferta Especial")
                    element("ingrediente", attributes = ("nome" to "queijo") and ("cor" to "amarelo") and ("tipo" to "cheddar"))
                    element("ingrediente", attributes = ("nome" to "pao"))
                }
            }
        
        assertEquals(
            "<menu>\n" +
            "\t<hamburguer>\n" +
            "\t\t<tipo>Normal</tipo>\n" +
            "\t\t<ingrediente nome=\"carne\"/>\n" +
            "\t\t<ingrediente nome=\"alface\" cor=\"verde\">muito fresca</ingrediente>\n" +
            "\t\t<ingrediente nome=\"queijo\" cor=\"amarelo\"/>\n" +
            "\t</hamburguer>\n" +
            "\t<sandes>\n" +
            "\t\t<tipo>Oferta Especial</tipo>\n" +
            "\t\t<ingrediente nome=\"queijo\" cor=\"amarelo\" tipo=\"cheddar\"/>\n" +
            "\t\t<ingrediente nome=\"pao\"/>\n" +
            "\t</sandes>\n" +
            "</menu>", element1.toString()
        )
        
        assertEquals("<ingrediente nome=\"queijo\" cor=\"amarelo\"/>", element1[0][3].toString())
        assertThrows<IndexOutOfBoundsException> {element1[0][4].toString()}
        val element2 = XMLElement.createElement("teste", "tagText", "name" to "value")
        assertEquals("<teste name=\"value\">tagText</teste>", element2.toString())
    }
}