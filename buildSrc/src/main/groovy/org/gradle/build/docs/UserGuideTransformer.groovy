package org.gradle.build.docs

import org.gradle.api.GradleException
import org.gradle.api.dependencies.FileCollection
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document
import groovy.xml.dom.DOMCategory
import groovy.xml.FactorySupport
import groovy.xml.MarkupBuilder

/**
 * Transforms userguide source into docbook, replacing custom xml elements.
 */
public class UserGuideTransformer {
    String version
    String javadocUrl
    File sourceFile
    File destFile
    FileCollection classpath;

    def execute() {
        ['version', 'sourceFile', 'destFile', 'classpath', 'javadocUrl'].each {
            if (getProperty(it) == null) {
                throw new GradleException("Property not set: $it")
            }
        }

        Element root

        destFile.parentFile.mkdirs()

        // Set the thread context classloader to pick up the correct XML parser
        def uris = classpath.getFiles().collect {it.toURI().toURL()}
        def classloader = new URLClassLoader(uris as URL[], getClass().classLoader)
        def oldClassloader = Thread.currentThread().getContextClassLoader()
        Thread.currentThread().setContextClassLoader(classloader)
        try {

            root = loadAndTransform()

        } finally {
            Thread.currentThread().setContextClassLoader(oldClassloader)
        }

        destFile.parentFile.mkdirs()
        destFile.withOutputStream {OutputStream stream ->
            groovy.xml.dom.DOMUtil.serialize(root, stream)
        }
    }

    private Element loadAndTransform() {
        Document doc = parseSourceFile()
        Element root = doc.documentElement

        use(DOMCategory) {
            addVersionInfo(doc)
            transformSamples(doc)
            transformApiLinks(doc)
            fixProgramListings(doc)
        }

        return root
    }

    private def addVersionInfo(Document doc) {
        Element releaseInfo = doc.createElement('releaseinfo')
        releaseInfo.appendChild(doc.createTextNode(version.toString()))
        doc.documentElement.bookinfo[0].appendChild(releaseInfo)
    }

    private def fixProgramListings(Document doc) {
        doc.documentElement.depthFirst().findAll { it.name() == 'programlisting' }.each {Element element ->
            element.setTextContent(normalise(element.getTextContent()))
        }
    }

    private String normalise(String content) {
        return content.trim().replace('\t', '    ')
    }

    private def transformApiLinks(Document doc) {
        File linksFile = new File(destFile.parentFile, 'links.xml')
        linksFile.withWriter {Writer writer ->
            MarkupBuilder xml = new MarkupBuilder(writer)
            xml.links {
                doc.documentElement.depthFirst().findAll { it.name() == 'apilink' }.each {Element element ->
                    String className = element.'@class'
                    String methodName = element.'@method'

                    Element ulinkElement = doc.createElement('ulink')
                    String href = "$javadocUrl/${className.replace('.', '/')}.html"
                    ulinkElement.setAttribute('url', href)

                    Element classNameElement = doc.createElement('classname')
                    ulinkElement.appendChild(classNameElement)

                    String classBaseName = className.tokenize('.').last()
                    String linkText = methodName ? "$classBaseName.$methodName()" : classBaseName
                    classNameElement.appendChild(doc.createTextNode(linkText))

                    element.parentNode.replaceChild(ulinkElement, element)

                    xml.link(className: className)
                }
            }
        }
    }

    private def transformSamples(Document doc) {
        File samplesFile = new File(destFile.parentFile, 'samples.xml')
        samplesFile.withWriter {Writer writer ->
            MarkupBuilder xml = new MarkupBuilder(writer)
            xml.samples {
                doc.documentElement.depthFirst().findAll { it.name() == 'sample' }.each {Element element ->
                    String id = element.'@id'
                    if (!id) {
                        throw new RuntimeException("No id attribute specified for sample.")
                    }
                    String srcDir = element.'@dir'
                    if (!srcDir) {
                        throw new RuntimeException("No dir attribute specified for sample '$id'.")
                    }

                    xml.sample(id: id, dir: srcDir)

                    element.children().each {Element child ->
                        if (child.name() == 'sourcefile') {
                            String file = child.'@file'
                            if (!file) {
                                throw new RuntimeException("No file attribute specified for source file in sample '$id'.")
                            }

                            Element exampleElement = doc.createElement('example')

                            Element titleElement = doc.createElement('title')
                            titleElement.appendChild(doc.createTextNode("$file ($srcDir)"))
                            exampleElement.appendChild(titleElement)

                            Element programListingElement = doc.createElement('programlisting')
                            File srcFile = new File(sourceFile.parentFile, "../../../src/samples/$srcDir/$file")
                            programListingElement.appendChild(doc.createTextNode(normalise(srcFile.text)))
                            exampleElement.appendChild(programListingElement)

                            element.parentNode.insertBefore(exampleElement, element)
                        } else if (child.name() == 'output') {
                            String args = child.'@args'
                            if (args == null) {
                                throw new RuntimeException("No args attribute specified for output for sample '$id'.")
                            }

                            xml.sample(id: id, dir: srcDir, args: args)

                            Element exampleElement = doc.createElement('example')

                            Element titleElement = doc.createElement('title')
                            titleElement.appendChild(doc.createTextNode("gradle $args ($srcDir)"))
                            exampleElement.appendChild(titleElement)

                            Element screenElement = doc.createElement('screen')
                            File srcFile = new File(sourceFile.parentFile, "../../../src/samples/userguideOutput/${id}.out")
                            screenElement.appendChild(doc.createTextNode(normalise(srcFile.text)))
                            exampleElement.appendChild(screenElement)

                            element.parentNode.insertBefore(exampleElement, element)

                        } else {
                            throw new RuntimeException("Unrecognised sample type ${child.name()} found.")
                        }
                    }

                    element.parentNode.removeChild(element)
                }
            }
        }
    }

    private Document parseSourceFile() {
        DocumentBuilderFactory factory = FactorySupport.createDocumentBuilderFactory()
        factory.setNamespaceAware(true)
        return factory.newDocumentBuilder().parse(sourceFile)
    }
}