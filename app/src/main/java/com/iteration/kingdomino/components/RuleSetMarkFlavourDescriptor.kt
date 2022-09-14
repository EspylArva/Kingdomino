package com.iteration.kingdomino.components

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.*
import org.intellij.markdown.html.entities.EntityConverter
import org.intellij.markdown.lexer.MarkdownLexer
import org.intellij.markdown.lexer._MarkdownLexer
import org.intellij.markdown.parser.LinkMap
import org.intellij.markdown.parser.MarkerProcessorFactory
import org.intellij.markdown.parser.sequentialparsers.EmphasisLikeParser
import org.intellij.markdown.parser.sequentialparsers.SequentialParser
import org.intellij.markdown.parser.sequentialparsers.SequentialParserManager
import org.intellij.markdown.parser.sequentialparsers.impl.*

open class RuleSetMarkFlavourDescriptor(_useSafeLinks: Boolean = true, _absolutizeAnchorLinks: Boolean = false) :
    CommonMarkFlavourDescriptor(_useSafeLinks, _absolutizeAnchorLinks) {
    override fun createInlinesLexer(): MarkdownLexer {
        return MarkdownLexer(_MarkdownLexer())
    }

    override val sequentialParserManager = object : SequentialParserManager() {
        override fun getParserSequence(): List<SequentialParser> {
            return listOf(
                AutolinkParser(listOf(MarkdownTokenTypes.AUTOLINK)),
                BacktickParser(),
                ImageParser(),
                InlineLinkParser(),
                ReferenceLinkParser(),
                EmphasisLikeParser(EmphStrongDelimiterParser())
            )
        }
    }

    override fun createHtmlGeneratingProviders(linkMap: LinkMap,
                                               baseURI: URI?): Map<IElementType, GeneratingProvider> {
        val mapOfProviderByElementType = super.createHtmlGeneratingProviders(linkMap, baseURI).toMutableMap()
        mapOfProviderByElementType[MarkdownElementTypes.IMAGE] = FullWidthImageGeneratingProvider(linkMap, baseURI).makeXssSafe(useSafeLinks)
        mapOfProviderByElementType[MarkdownElementTypes.PARAGRAPH] = object : TrimmingInlineHolderProvider() {
            override fun openTag(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode) {
                visitor.consumeTagOpen(node, "p align=\"justify\"")
            }

            override fun closeTag(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode) {
                visitor.consumeTagClose("p")
            }
        }

        return mapOfProviderByElementType
    }

    class FullWidthImageGeneratingProvider(linkMap: LinkMap, baseURI: URI?) : ImageGeneratingProvider(linkMap, baseURI) {
        override fun renderLink(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode, info: RenderInfo) {
            visitor.consumeTagOpen(node, "img",
                "src=\"${makeAbsoluteUrl(info.destination)}\"",
                "style='width:100%'",
                "alt=\"${getPlainTextFrom(info.label, text)}\"",
                info.title?.let { "title=\"$it\"" },
                autoClose = true)
        }

        private fun getPlainTextFrom(node: ASTNode, text: String): CharSequence {
            return REGEX.replace(node.getTextInNode(text), "")
        }

    }
}