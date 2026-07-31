package sharev

import org.mockito.Mockito
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.type.AnnotationMetadata

class UseCaseMockRegistrar : ImportBeanDefinitionRegistrar {

    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry,
    ) {
        val scanner = object : ClassPathScanningCandidateComponentProvider(false) {
            override fun isCandidateComponent(beanDefinition: AnnotatedBeanDefinition): Boolean {
                val metadata = beanDefinition.metadata
                return metadata.isInterface && metadata.className.endsWith("UseCase")
            }
        }
        scanner.addIncludeFilter { _, _ -> true }

        scanner.findCandidateComponents("sharev").forEach { candidate ->
            val className = candidate.beanClassName ?: return@forEach
            val useCaseType = Class.forName(className)
            val beanName = useCaseType.simpleName.replaceFirstChar { it.lowercase() }

            if (registry.containsBeanDefinition(beanName)) {
                return@forEach
            }

            val definition = RootBeanDefinition(useCaseType).apply {
                setInstanceSupplier { Mockito.mock(useCaseType) }
            }

            registry.registerBeanDefinition(beanName, definition)
        }
    }
}
