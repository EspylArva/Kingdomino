package com.iteration.kingdomino.ui.appendix

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.iteration.kingdomino.R
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import timber.log.Timber
import java.util.stream.Collectors.toList

class AppendixFragment : Fragment() {

    private lateinit var appendixViewModel: AppendixViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        appendixViewModel =
                ViewModelProvider(this).get(AppendixViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_appendix, container, false)

        val lbl: TextView = root.findViewById(R.id.lbl_rules_html)

        Timber.i("=== Starting parsing Kingdomino rules ===")

        val resourceName = resources.getResourceName(R.raw.rules)
        resources.openRawResource(R.raw.rules).use {
            val text = it.reader().readLines().joinToString("\r\n")
            Timber.d("Parsed this from resource=[$resourceName@${R.raw.rules}]:\n$text")
            val flavour = CommonMarkFlavourDescriptor()
            val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(text)
            Timber.d("parsedTree=$parsedTree (parent=${parsedTree.parent}, children=${parsedTree.children.stream().map { ast -> ast.getTextInNode(text) }.collect(toList())}")
            val html = HtmlGenerator(text, parsedTree, flavour).generateHtml()

            Timber.d("HTML: $html")

            lbl.text = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
        }

        return root
    }
}