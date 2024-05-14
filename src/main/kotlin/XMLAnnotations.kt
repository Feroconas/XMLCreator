import kotlin.reflect.KClass

/**
 * Marks a class as representing an element in the XML file hierarchy.
 *
 * The [Element] annotation can be used in the following ways:
 *
 * 1 - When applied to a class, designates it as an XML element. All its fields of a type (class) that also shares the [Element] annotation
 * are automatically considered its children in the XML structure. This is also true if a field is a [Collection] of a type annotated
 * with [Element]. In that case, each element in the collection is treated as a child too.
 *
 * 2 - When applied to a field of a type not annotated with [Element], that field is considered a child (with no children of its own),
 * where its value dictates the tag text. If that field is a [Collection], a children is created for each element in it.
 *
 * 3 - When applied to a field of a type already annotated with [Element], an additional parent element is created. Instead of directly
 * attaching child elements to the annotated class's XML representation, they will be nested within this intermediate parent element.
 * This is also true if that field is a [Collection] of a type annotated with [Element].
 *
 * @property tagName The tag name of the XML element. If not provided or empty, the name of the class or the name of the field is used,
 * depending on whether it was applied to a class or a field.
 */

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
annotation class Element(val tagName: String = "")

/**
 * Marks the value of a field as the tag text of an XML element.
 *
 * Must be applied to a field in a class annotated with [Element], and should be used at most once per class.
 */

@Target(AnnotationTarget.FIELD)
annotation class TagText

/**
 * Marks a field as an attribute of an XML element.
 *
 * The value of the field dictates the attribute's value.
 *
 * Must be applied to a field in a class annotated with [Element].
 *
 * @property name The attribute's name. If not provided or empty, the name of the field is used.
 */

@Target(AnnotationTarget.FIELD)
annotation class Attribute(val name: String = "")

/**
 * Specifies a transformation to be performed on the [XMLElement] to be created based on a class with the [Element] annotation.
 *
 * Must be applied to a class annotated with [Element].
 *
 * @property transform The [XMLElementTransform] to be applied to the [XMLElement]. Note that the transform is performed after all its
 * children are created.
 */

@Target(AnnotationTarget.CLASS)
annotation class ElementTransform(val transform: KClass<out XMLElementTransform>)

/**
 * Specifies a transformation to be performed on the value of an XML attribute.
 *
 * Must be applied to a field with the [Attribute] annotation.
 *
 * @property transform The [XMLStringTransform] to be applied to the attribute's value.
 */

@Target(AnnotationTarget.FIELD)
annotation class AttributeTransform(val transform: KClass<out XMLStringTransform>)
