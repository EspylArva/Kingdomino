package com.iteration.kingdomino.components

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.flavours.commonmark.CommonMarkMarkerProcessor
import org.intellij.markdown.html.*
import org.intellij.markdown.lexer.MarkdownLexer
import org.intellij.markdown.lexer._MarkdownLexer
import org.intellij.markdown.parser.*
import org.intellij.markdown.parser.constraints.CommonMarkdownConstraints
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.markerblocks.MarkerBlock
import org.intellij.markdown.parser.markerblocks.MarkerBlockProvider
import org.intellij.markdown.parser.markerblocks.impl.CodeFenceMarkerBlock
import org.intellij.markdown.parser.markerblocks.providers.*
import org.intellij.markdown.parser.sequentialparsers.EmphasisLikeParser
import org.intellij.markdown.parser.sequentialparsers.SequentialParser
import org.intellij.markdown.parser.sequentialparsers.SequentialParserManager
import org.intellij.markdown.parser.sequentialparsers.impl.*
import timber.log.Timber

open class RuleSetMarkFlavourDescriptor(_useSafeLinks: Boolean = true, _absolutizeAnchorLinks: Boolean = false) : CommonMarkFlavourDescriptor(_useSafeLinks, _absolutizeAnchorLinks) {
    override fun createInlinesLexer(): MarkdownLexer {
        return MarkdownLexer(_MarkdownLexer())
    }

    override val markerProcessorFactory: MarkerProcessorFactory = MarkerProcessor.Factory

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
        mapOfProviderByElementType[MarkdownElementTypes.CODE_SPAN] = object : GeneratingProvider {
            override fun processNode(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode) {
                val output = node.children.subList(1, node.children.size - 1)
                    .joinToString("") { HtmlGenerator.leafText(text, it, false) }
                    .trim()
                visitor.consumeTagOpen(node, "code")
                visitor.consumeHtml(output)
                visitor.consumeTagClose("code")
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

    class MarkerProcessor(productionHolder: ProductionHolder, constraintsBase: MarkdownConstraints) :
        CommonMarkMarkerProcessor(productionHolder, constraintsBase) {

        override fun getMarkerBlockProviders(): List<MarkerBlockProvider<StateInfo>> {
            return listOf(
                CodeBlockProvider(),
                HorizontalRuleProvider(),
                CorrectCodeFenceProvider(),
                SetextHeaderProvider(),
                BlockQuoteProvider(),
                ListMarkerProvider(),
                AtxHeaderProvider(),
                HtmlBlockProvider(),
                LinkReferenceDefinitionProvider())
        }

        object Factory : MarkerProcessorFactory {
            override fun createMarkerProcessor(productionHolder: ProductionHolder): org.intellij.markdown.parser.MarkerProcessor<*> {
                return MarkerProcessor(productionHolder, CommonMarkdownConstraints.BASE)
            }
        }
    }

    class CorrectCodeFenceProvider : MarkerBlockProvider<org.intellij.markdown.parser.MarkerProcessor.StateInfo> {
        override fun createMarkerBlocks(pos: LookaheadText.Position, productionHolder: ProductionHolder, stateInfo: org.intellij.markdown.parser.MarkerProcessor.StateInfo):
                List<MarkerBlock> {
            val fenceAndInfo = getFenceStartAndInfo(pos, stateInfo.currentConstraints)
            return if (fenceAndInfo != null) {
                createNodesForFenceStart(pos, fenceAndInfo, productionHolder)
                val codeblocks = listOf(CodeFenceMarkerBlock(stateInfo.currentConstraints, productionHolder, fenceAndInfo.first))
                Timber.d("Parsed code block: $codeblocks")
                codeblocks
            } else {
                emptyList()
            }
        }

        override fun interruptsParagraph(pos: LookaheadText.Position, constraints: MarkdownConstraints): Boolean {
            return getFenceStartAndInfo(pos, constraints) != null
        }

        private fun createNodesForFenceStart(pos: LookaheadText.Position, fenceAndInfo: Pair<String, String>, productionHolder: ProductionHolder) {
            val infoStartPosition = pos.nextLineOrEofOffset - fenceAndInfo.second.length
            productionHolder.addProduction(listOf(SequentialParser.Node(pos.offset..infoStartPosition, MarkdownTokenTypes.CODE_FENCE_START)))
            if (fenceAndInfo.second.isNotEmpty()) {
                productionHolder.addProduction(listOf(SequentialParser.Node(infoStartPosition..pos.nextLineOrEofOffset, MarkdownTokenTypes.FENCE_LANG)))
            }
        }

        private fun getFenceStartAndInfo(pos: LookaheadText.Position, constraints: MarkdownConstraints): Pair<String, String>? {
            if (!MarkerBlockProvider.isStartOfLineWithConstraints(pos, constraints)) {
                return null
            }
            val matchResult = REGEX.find(pos.currentLineFromPosition) ?: return null
            return Pair(matchResult.groups[1]?.value!!, matchResult.groups[2]?.value!!)
        }

        companion object {
            val REGEX: Regex = Regex("^ {0,3}(~~~+|```+)([^`]*)(~~~+|```+)\$")
        }
    }
}