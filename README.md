# XML Creator API for Kotlin

## Overview
This API allows you to define and manipulate XML elements using Kotlin annotations and classes. It provides a convenient way to convert Kotlin objects to XML. The API supports nested elements, attributes, tag text, and various customization options through annotations.

## Table of Contents
- [Classes and Methods](#classes-and-methods)
  - [XMLElement](#xmlelement)
  - [XMLAttribute](#xmlattribute)
  - [XMLDocument](#xmldocument)
- [Micro-XPath](#micro-xpath)
- [Domain Specific Language](#domain-specific-language)
- [Annotations](#annotations)
  - [@Element](#element)
  - [@TagText](#tagtext)
  - [@Attribute](#attribute)
- [Examples](#examples)

---

## Classes and Methods

### XMLElement
Represents an XML element with a tag name, optional tag text, attributes, and children.

**Methods:**

- **isValidTagName(tagName: String): Boolean**: Checks if a given tag name is valid according to XML naming rules.
- **isValidTagText(tagText: String?): Boolean**: Checks if a given tag text is valid.
- **Any.toXMLElement(parent: XMLElement? = null): XMLElement**: Converts any object annotated with [Element] to an XMLElement, creating a nested structure based on the object's properties and their annotations.
- **getTagName()**: Returns the tag name.
- **setTagName(tagName: String)**: Sets the tag name.
- **getTagText()**: Returns the tag text.
- **setTagText(tagText: String?)**: Sets the tag text.
- **getAttributes()**: Returns the attributes.
- **addAttribute(name: String, value: String)**: Adds an attribute.
- **removeAttribute(name: String)**: Removes an attribute.
- **getChildren()**: Returns the children.
- **addChild(child: XMLElement)**: Adds a child element.
- **removeChild(child: XMLElement)**: Removes a child element.
- **fun createElement(tagName: String, tagText: String? = null, attributes: MutableList<Pair<String, String>> = mutableListOf(), build: XMLElement.() -> Unit = {}): XMLElement**:Creates an XML element with the given tag name, optional tag text, multiple attributes, and an optional build action.
- **fun createElement(tagName: String, tagText: String? = null, attributes: Pair<String, String>, build: XMLElement.() -> Unit = {}): XMLElement**: Convenience overload of createElement allowing the specification of a single attribute as a pair.
- **infix fun Pair<String, String>.and(other: Pair<String, String>): MutableList<Pair<String, String>>**: Combines two attributes into a mutable list of pairs.
- **infix fun MutableList<Pair<String, String>>.and(other: Pair<String, String>): MutableList<Pair<String, String>>**: Adds an attribute pair to an existing mutable list of attributes and returns the updated list.
- **fun element(tagName: String,tagText: String? = null,attributes: Pair<String, String>,build: XMLElement.() -> Unit = {}): XMLElement**: Creates an XML element with the given tag name, optional tag text, a single attribute, and an optional build action.
- **fun element(tagName: String,tagText: String? = null,attributes: MutableList<Pair<String, String>> = mutableListOf(),build: XMLElement.() -> Unit = {}): XMLElement**: Creates an XML element with the given tag name, optional tag text, multiple attributes, and an optional build action.

### XMLAttribute
Represents an attribute of an XML element.

**Methods:**

- **isValidName(name: String): Boolean**: Validates whether a given name is a valid XML element attribute name.
- **isValidValue(value: String): Boolean**: Validates whether a given value is a valid XML attribute value.
- **getName()**: Returns the attribute name.
- **getValue()**: Returns the attribute value.

### XMLDocument
Represents an XML document with a root element, version, and encoding.

**Methods:**

- **getVersion()**: Gets the version of the XML document.
- **setVersion(version: String)**: Sets the version of the XML document.
- **getEncoding()**: Gets the encoding of the XML document.
- **setEncoding(encoding: String)**: Sets the encoding of the XML document.
- **addAttributeGlobally(tagName: String, attributeName: String, attributeValue: String)**: Adds an attribute to all elements with a specific tag name.
- **renameAttributeGlobally(name: String, newName: String)**: Renames an attribute globally across all elements.
- **removeAttributeGlobally(name: String)**: Removes an attribute globally from all elements.
- **renameElementGlobally(tagName: String, newTagName: String)**: Renames elements with a specific tag name globally.
- **removeElementGlobally(tagName: String)**: Removes elements with a specific tag name globally.
- **saveToFile(filePath: String)**: Saves the document to a file.
- **findElementsByXPath(xPathExpression: String): MutableList<XMLElement>**: Finds elements by an XPath expression.

## Micro-XPath

XPath uses path expressions to select nodes or node-sets in an XML document.
These path expressions look very much like the path expressions you use with traditional computer file systems.

### Example:
**XML Structure:**
```
<Lisbon>
    <Restaurant>Sem Montaditos</Restaurant>
    <home number="1">
        <room name="Bathroom" area="15"/>
        <room name="Kitchen" area="60"/>
    </home>
    <home number="2">
        <room name="Kitchen" area="55"/>
        <room name="Hall" area="20"/>
        <room name="Garage" area="30"/>
    </home>
</Lisbon>
```
Lisbon/home/room - **Returns**:
```
<room name="Bathroom" area="30"/>
<room name="Kitchen" area="60"/>
<room name="Kitchen" area="55"/>
<room name="Hall" area="20"/>
<room name="Garage" area="30"/>
```
## Domain Specific Language
Serves to instantiate XMLElements and XMLDocuments in an easier, more readable and natural way.

### Example

With the following **code**...
```
val document = XMLDocument(version = "1.0", encoding = "UTF-8",
            XMLElement.createElement("plan") {
                element("course", "Master in Computer Engineering")
                element("unit", attributes = "code" to "M4310") {
                    element("name", "Advanced Programming")
                    element("credits", "6.0")
                    element("evaluation") {
                        element("component", attributes = "name" to "Quizzes" and ("weight" to "20%"))
                        element("component", attributes = "name" to "Project" and ("weight" to "80%"))
                    }
                }
                element("unit", attributes = "code" to "03782") {
                    element("name", "Dissertation")
                    element("credits", "42.0")
                    element("evaluation") {
                        element("component", attributes = "name" to "Dissertation" and ("weight" to "60%"))
                        element("component", attributes = "name" to "Presentation" and ("weight" to "20%"))
                        element("component", attributes = "name" to "Discussion" and ("weight" to "20%"))
                    }
                }
            })
```
...you get this **XML FILE**

```
<?xml version="1.0" encoding="UTF-8"?>
<plan>
    <course>Master in Computer Engineering</course>
    <unit code="M4310">
        <name>Advanced Programming</name>
        <credits>6.0</credits>
        <evaluation>
            <component name="Quizzes" weight="20%"/>
            <component name="Project" weight="80%"/>
        </evaluation>
    </unit>
    <unit code="03782">
        <name>Dissertation</name>
        <credits>42.0</credits>
        <evaluation>
            <component name="Dissertation" weight="60%"/>
            <component name="Presentation" weight="20%"/>
            <component name="Discussion" weight="20%"/>
        </evaluation>
    </unit>
</plan>
```


## Annotations
The API provides three primary annotations: **@Element**, **@TagText**, and **@Attribute**.

### @Element
Marks a class or property as an XML element. It can be used to designate classes as XML elements and to define child elements.

**Parameters:**

- **tagName**: The tag name of the XML element. If not provided, the class or property name is used.
- **createParent**: If true, creates an additional parent element.
- **tagTextTransformer**: Specifies a transformer for the element's tag text.
- **elementSorting**: Specifies how the element's children should be sorted.

**Example:**
```
@Element(tagName = "person")
data class Person(val name: String, val age: Int)
```
### @TagText
Marks a property as the tag text of an XML element. Should be used at most once per class.

**Example:**
```
@Element
data class Greeting(@TagText val message: String)
```
### @Attribute
Marks a property as an attribute of an XML element.

**Parameters:**

- **name**: The attribute's name. If not provided, the property name is used.
- **attributeTransformer**: Specifies a transformer for the attribute's value.

**Example:**
```
@Element
data class Book(@Attribute val title: String, @Attribute val author: String)
```

### Examples
```
@Element(tagName = "person")
data class Person(
    @Attribute val name: String,
    @Element(createParent = true) val address: Address
)

@Element(tagName = "address")
data class Address(
    @Attribute val city: String,
    @TagText val street: String
)

fun main() {
    val address = Address("Lisbon", "Avenida das Forças Armadas")
    val person = Person("Joe Who", address).toXMLElement()
    println(person)
}
```
**This returns:**

```
<person name="John Doe">
    <address>
        <address city="New York">
            5th Avenue
        </address>
    </address>
</person>
```
