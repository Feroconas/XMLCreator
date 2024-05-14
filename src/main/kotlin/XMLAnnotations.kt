import kotlin.reflect.KClass

/**
 * Marks a class as representing an element in the XML file hierarchy.
 *
 * The [Element] annotation can be used in the following cases:
 *
 * 1 - When applied to a class, designates it as an XML element. Additional annotations should be used on that class' properties to
 * specify the XML element's tag text, attributes, and children.
 *
 * 2 - When applied to a property of a type (class) annotated with [Element], a child XML element is created. If that property is a
 * [Collection], a child is created for each element in it.
 *
 * 3 - When applied to a property of a type not annotated with [Element], that property is considered a child (with no children
 * of its own), where its value dictates the tag text. If that property is a [Collection], a child is created for each element
 * in it.
 *
 * @property tagName The tag name of the XML element. If not provided or empty, class name or property name is used, depending on
 * whether it was applied to a class or a property.
 * @property createParent Only relevant in case 2. If true, an additional parent with tag name equal to the property's name is created
 * between the parent and the child/children.
 */

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class Element(val tagName: String = "", val createParent: Boolean = false)

/**
 * Marks the value of a property as the tag text of an XML element.
 *
 * Must be applied to a property in a class annotated with [Element], and should be used at most once per class.
 */

@Target(AnnotationTarget.PROPERTY)
annotation class TagText

/**
 * Marks a property as an attribute of an XML element.
 *
 * The value of the property dictates the attribute's value.
 *
 * Must be applied to a property in a class annotated with [Element].
 *
 * @property name The attribute's name. If not provided or empty, the name of the property is used.
 */

@Target(AnnotationTarget.PROPERTY)
annotation class Attribute(val name: String = "")

/**
 * Specifies a transformation to be performed on the [XMLElement] to be created based on a class with the [Element] annotation.
 *
 * Must be applied to a class annotated with [Element].
 *
 * @property elementTransform The [XMLElementTransform] to be applied to the [XMLElement]. Note that the transform is performed after all its
 * children are created.
 */

@Target(AnnotationTarget.CLASS)
annotation class ElementTransform(val elementTransform: KClass<out XMLElementTransform>)

/**
 * Specifies a transformation to be performed on the value of an XML attribute.
 *
 * Must be applied to a property with the [Attribute] annotation.
 *
 * @property stringTransform The [XMLStringTransform] to be applied to the attribute's value.
 */

@Target(AnnotationTarget.PROPERTY)
annotation class AttributeTransform(val stringTransform: KClass<out XMLStringTransform>)