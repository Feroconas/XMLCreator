# XML Creator API for Kotlin

## Overview
This API allows you to define and manipulate XML elements using Kotlin annotations and classes. It provides a convenient way to convert Kotlin objects to XML and vice versa. The API supports nested elements, attributes, tag text, and various customization options through annotations.

## Table of Contents
- [Getting Started](#getting-started)
- [Annotations](#annotations)
  - [@Element](#element)
  - [@TagText](#tagtext)
  - [@Attribute](#attribute)
- [Classes and Methods](#classes-and-methods)
  - [XMLElement](#xmlelement)
  - [XMLAttribute](#xmlattribute)
  - [XMLDocument](#xmldocument)
- [Examples](#examples)

---
## Getting Started
To use this API, add the necessary imports and apply the provided annotations to your Kotlin classes. 

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

## Classes and Methods

### XMLElement
Represents an XML element with a tag name, optional tag text, attributes, and children.

**Methods:**

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

### XMLAttribute
Represents an attribute of an XML element.

**Methods:**

- **getName()**: Returns the attribute name.
- **getValue()**: Returns the attribute value.
- **setName(name: String)**: Sets the attribute name.
- **setValue(value: String)**: Sets the attribute value.

### XMLDocument
Represents an XML document with a root element, version, and encoding.

**Methods:**

- **getVersion()**: Returns the document version.
- **setVersion(version: String)**: Sets the document version.
- **getEncoding()**: Returns the document encoding.
- **setEncoding(encoding: String)**: Sets the document encoding.
- **saveToFile(filePath: String)**: Saves the document to a file.

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
<person name="Joe Who">
    <address>
        <address city="Lisbon">
            Avenida das Forças Armadas
        </address>
    </address>
</person>
```
