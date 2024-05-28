import XMLElement.Companion.and
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

@Suppress("unused")
class XMLDocumentTest {
    
    private val planoElement = XMLElement("plano")
    private val cursoElement = XMLElement("curso", "Mestrado em Engenharia Informática", planoElement)
    private val fucElement1 = XMLElement("fuc", parent = planoElement)
    private val fucElement1Name = XMLElement("nome", "Programação Avançada", fucElement1)
    private val ects = XMLElement("ects", "6.0", fucElement1)
    private val avaliacao = XMLElement("avaliacao", parent = fucElement1)
    private val componente1 = XMLElement("componente", parent = avaliacao)
    private val componente2 = XMLElement("componente", parent = avaliacao)
    private val fucElement2 = XMLElement("fuc", parent = planoElement)
    private val fucElement2Name = XMLElement("nome", "Dissertação", fucElement2)
    private val ects2 = XMLElement("ects", "42.0", fucElement2)
    private val avaliacao2 = XMLElement("avaliacao", parent = fucElement2)
    private val componente3 = XMLElement("componente", parent = avaliacao2)
    private val componente4 = XMLElement("componente", parent = avaliacao2)
    private val componente5 = XMLElement("componente", parent = avaliacao2)
    
    private val document = XMLDocument(planoElement)
    private val originalDocumentString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<plano>\n" +
    "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
    "\t<fuc codigo=\"M4310\">\n" +
    "\t\t<nome>Programação Avançada</nome>\n" +
    "\t\t<ects>6.0</ects>\n" +
    "\t\t<avaliacao>\n" +
    "\t\t\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
    "\t\t\t<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
    "\t\t</avaliacao>\n" +
    "\t</fuc>\n" +
    "\t<fuc codigo=\"03782\">\n" +
    "\t\t<nome>Dissertação</nome>\n" +
    "\t\t<ects>42.0</ects>\n" +
    "\t\t<avaliacao>\n" +
    "\t\t\t<componente nome=\"Dissertação\" peso=\"60%\"/>\n" +
    "\t\t\t<componente nome=\"Apresentação\" peso=\"20%\"/>\n" +
    "\t\t\t<componente nome=\"Discussão\" peso=\"20%\"/>\n" +
    "\t\t</avaliacao>\n" +
    "\t</fuc>\n" +
    "</plano>"
    
    /**
     * Adds attributes to XML elements before each test.
     */
    @BeforeEach
    fun addAttributes() {
        fucElement1.addAttribute("codigo", "M4310")
        fucElement2.addAttribute("codigo", "03782")
        componente1.addAttribute("nome", "Quizzes")
        componente1.addAttribute("peso", "20%")
        componente2.addAttribute("nome", "Projeto")
        componente2.addAttribute("peso", "80%")
        componente3.addAttribute("nome", "Dissertação")
        componente3.addAttribute("peso", "60%")
        componente4.addAttribute("nome", "Apresentação")
        componente4.addAttribute("peso", "20%")
        componente5.addAttribute("nome", "Discussão")
        componente5.addAttribute("peso", "20%")
    }
    
    /**
     * Tests the getters and setters of the XMLDocument class.
     */
    @Test
    fun gettersAndSetters() {
        assertEquals(planoElement, document.root)
        assertEquals(document.getVersion(), XMLDocument.DEFAULT_VERSION)
        assertEquals(document.getEncoding(), XMLDocument.DEFAULT_ENCODING)
        document.setVersion("2.0")
        assertEquals(document.getVersion(), "2.0")
        document.setEncoding("ASCII")
        assertEquals(document.getEncoding(), "ASCII")
    }
    
    /**
     * Tests adding an attribute globally to elements.
     */
    @Test
    fun addAttributeGlobally() {
        document.addAttributeGlobally("nao_existe", "teste", "teste")
        assertEquals(document.toString(), originalDocumentString)
        
        document.addAttributeGlobally("componente", "nome", "teste")
        assertEquals(document.toString(), originalDocumentString)
        
        document.addAttributeGlobally("ects", "teste", "teste")
        val correctDocumentString = originalDocumentString
            .replace("<ects>6.0</ects>", "<ects teste=\"teste\">6.0</ects>")
            .replace("<ects>42.0</ects>", "<ects teste=\"teste\">42.0</ects>")
        assertEquals(correctDocumentString, document.toString())
    }
    
    /**
     * Tests renaming an attribute globally across all elements.
     */
    @Test
    fun renameAttributeGlobally() {
        document.renameAttributeGlobally("nome", "peso")
        assertEquals(originalDocumentString, document.toString())
        document.renameAttributeGlobally("nao_existe", "abc")
        assertEquals(originalDocumentString, document.toString())
        document.renameAttributeGlobally("peso", "pesagem")
        val correctDocumentString = originalDocumentString.replace("peso=", "pesagem=")
        assertEquals(correctDocumentString, document.toString())
    }
    
    /**
     * Tests removing an attribute globally from all elements.
     */
    @Test
    fun removeAttributeGlobally() {
        document.removeAttributeGlobally("nao_existe")
        assertEquals(originalDocumentString, document.toString())
        document.removeAttributeGlobally("peso")
        val correctDocumentString = originalDocumentString.replace(Regex(" peso=\"\\d+%\""), "")
        assertEquals(correctDocumentString, document.toString())
    }
    
    /**
     * Tests renaming elements with a specific tag name globally.
     */
    @Test
    fun renameElementGlobally() {
        document.renameElementGlobally("nao_existe", "teste")
        assertEquals(originalDocumentString, document.toString())
        document.renameElementGlobally("componente", "teste")
        val correctDocumentString = originalDocumentString.replace("componente", "teste")
        assertEquals(correctDocumentString, document.toString())
    }
    
    /**
     * Tests removing elements with a specific tag name globally.
     */
    @Test
    fun removeElementGlobally() {
        document.removeElementGlobally("nao_existe")
        assertEquals(originalDocumentString, document.toString())
        document.removeElementGlobally("componente")
        val correctDocumentString = originalDocumentString
            .replace(
                "\t\t\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                "\t\t\t<componente nome=\"Projeto\" peso=\"80%\"/>\n", ""
            )
            .replace(
                "\t\t\t<componente nome=\"Dissertação\" peso=\"60%\"/>\n" +
                "\t\t\t<componente nome=\"Apresentação\" peso=\"20%\"/>\n" +
                "\t\t\t<componente nome=\"Discussão\" peso=\"20%\"/>\n", ""
            )
            .replace(
                "<avaliacao>\n" +
                "\t\t</avaliacao>", "<avaliacao/>"
            )
        assertEquals(correctDocumentString, document.toString())
        document.removeElementGlobally("fuc")
        val correctDocumentString2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<plano>\n" +
        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
        "</plano>"
        assertEquals(correctDocumentString2, document.toString())
    }
    
    /**
     * Tests saving the XML document to a file.
     */
    @Test
    fun saveToFile() {
        document.saveToFile("test.xml")
        val reader = File("test.xml").bufferedReader()
        val documentString: String = reader.use { it.readText() }
        assertEquals(originalDocumentString, documentString)
    }
    
    /**
     * Tests the `toString` method of the XMLDocument class.
     */
    @Test
    fun testToString() {
        assertEquals(originalDocumentString, document.toString())
        val newDocumentRoot = XMLElement("teste")
        val newDocument = XMLDocument(newDocumentRoot)
        newDocument.setVersion("1.1")
        newDocument.setEncoding("UTF-16")
        assertEquals("<?xml version=\"1.1\" encoding=\"UTF-16\"?>\n" + "<teste/>", newDocument.toString())
        val child = XMLElement("curso", "Engenharia Eletrónica", newDocumentRoot)
        child.addAttribute("ano", "2020")
        assertEquals(
            "<?xml version=\"1.1\" encoding=\"UTF-16\"?>\n" +
            "<teste>\n" +
            "\t<curso ano=\"2020\">Engenharia Eletrónica</curso>\n" +
            "</teste>",
            newDocument.toString()
        )
    }
    
    /**
     * Tests finding elements by an XPath expression.
     */
    @Test
    fun findElementsByXPath() {
        assertEquals(listOf<XMLElement>(), document.findElementsByXPath(""))
        assertEquals(listOf<XMLElement>(), document.findElementsByXPath("faculdade"))
        assertEquals(listOf(ects, ects2), document.findElementsByXPath("ects"))
        assertEquals(listOf(componente1, componente2, componente3, componente4, componente5), document.findElementsByXPath("componente"))
        assertEquals(
            listOf(componente1, componente2, componente3, componente4, componente5),
            document.findElementsByXPath("fuc/avaliacao/componente")
        )
    }
    
    @Test
    fun domainSpecificLanguage() {
        val document = XMLDocument(version = "1.0", encoding = "UTF-8",
            XMLElement.createElement("plano") {
                element("curso", "Mestrado em Engenharia Informática")
                element("fuc", attributes = "codigo" to "M4310") {
                    element("nome", "Programação Avançada")
                    element("ects", "6.0")
                    element("avaliacao") {
                        element("componente", attributes = "nome" to "Quizzes" and ("peso" to "20%"))
                        element("componente", attributes = "nome" to "Projeto" and ("peso" to "80%"))
                    }
                }
                element("fuc", attributes = "codigo" to "03782") {
                    element("nome", "Dissertação")
                    element("ects", "42.0")
                    element("avaliacao") {
                        element("componente", attributes = "nome" to "Dissertação" and ("peso" to "60%"))
                        element("componente", attributes = "nome" to "Apresentação" and ("peso" to "20%"))
                        element("componente", attributes = "nome" to "Discussão" and ("peso" to "20%"))
                    }
                }
            })
        assertEquals(originalDocumentString, document.toString())
    }
}