import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

/**
 * Marks a class as representing an element in the XML file hierarchy.
 *
 * The [Element] annotation can be used in the following cases:
 *
 * 1 - When applied to a class, designates it as an XML element. Additional annotations should be used on that class' properties to
 * specify the XML element's tag text, attributes, and children.
 *
 * 2 - When applied to a property of a type (class) annotated with [Element], a child XML element is recursively created solely
 * based on that property's value. If that property is a [Collection], a child is created for each element in it.
 *
 * 3 - When applied to a property of a type not annotated with [Element], that property is considered a child (with no children
 * of its own), where its value dictates the tag text. If that property is a [Collection], a child is created for each element
 * in it.
 *
 * @property tagName The tag name of the XML element. If not provided or empty, class name or property name is used, depending on
 * whether it was applied to a class or a property. Not relevant in case 2 unless [createParent] is true.
 * @property tagTextTransformer Specifies a string transformation to apply on the element's tag text.
 * By default, no transform is applied. Only relevant if the element has tag text.
 * @property elementSorting Specifies how the element's children should be sorted. By default, the elements are ordered based on the
 * class properties' names. Only relevant in case 1.
 * @property createParent If true, an additional is created between the parent and the child/children. Only relevant in case 2.
 */

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class Element(
    val tagName: String = "",
    val createParent: Boolean = false,
    val tagTextTransformer: KClass<out StringTransformer> = NoStringTransformer::class,
    val elementSorting: KClass<out Comparator<XMLElement>> = NoElementSorting::class
)

/**
 * Marks the value of a property as the tag text of an XML element.
 *
 * Must be applied to a property in a class annotated with [Element], and should be used at most once per class.
 */

@Target(AnnotationTarget.PROPERTY)
annotation class TagText

/**
 * Marks a property as an attribute of an XML element. The value of the property dictates the attribute's value.
 *
 * Must be applied to a property in a class annotated with [Element].
 *
 * @property name The attribute's name. If not provided or empty, the name of the property is used.
 * @property attributeValueTransformer The class with the transform to be applied on the attribute's value.
 * By default, no transform is applied.
 */

@Target(AnnotationTarget.PROPERTY)
annotation class Attribute(val name: String = "", val attributeValueTransformer: KClass<out StringTransformer> = NoStringTransformer::class)

class AnnotationConfigurationException(message: String) : Exception(message)

internal fun Any.validateXMLAnnotations() {
    
    if (!this::class.hasAnnotation<Element>())
        throw AnnotationConfigurationException(("${this::class.simpleName} must be annotated with @${Element::class.simpleName}"))
    
    var tagTextAnnotationCount = 0
    
    this::class.memberProperties.forEach { property ->
        val propertyAnnotations = property.annotations
        val mutuallyExclusiveAnnotations = propertyAnnotations.filter { it is Element || it is TagText || it is Attribute }
        if (mutuallyExclusiveAnnotations.size > 1) throw AnnotationConfigurationException(
            ("The @${Element::class.simpleName}, @${TagText::class.simpleName}, and @${Attribute::class.simpleName} annotations " +
            "are mutually exclusive and can only be used once in a property. " +
            "The property ${property.name} in ${this::class.simpleName} violates this rule.")
        )
        if (property.hasAnnotation<TagText>()) {
            if (++tagTextAnnotationCount > 1)
                throw AnnotationConfigurationException(
                    "The @${TagText::class.simpleName} annotation can't be used more than once per class. " +
                    "The class ${this::class.simpleName} violates this rule."
                )
        }
    }
}
