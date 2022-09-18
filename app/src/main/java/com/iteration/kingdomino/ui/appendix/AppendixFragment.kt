package com.iteration.kingdomino.ui.appendix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.iteration.kingdomino.R
import com.iteration.kingdomino.components.RuleSetMarkFlavourDescriptor
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import timber.log.Timber
import java.util.stream.Collectors.toList

class AppendixFragment : Fragment() {

    private lateinit var appendixViewModel: AppendixViewModel
    private lateinit var webView: WebView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        appendixViewModel = ViewModelProvider(this).get(AppendixViewModel::class.java)
        val root = initView(inflater, container)
        initListeners()
        initObservers()
        return root
    }

    private fun initListeners() { }

    private fun initObservers() {}

    private fun initView(inflater: LayoutInflater, container: ViewGroup?): View? {
        val root = inflater.inflate(R.layout.fragment_appendix, container, false)

        webView = root.findViewById(R.id.webview_html)

        val args: AppendixFragmentArgs by navArgs()
        val resourceName = resources.getResourceName(args.pageContent)
        resources.openRawResource(args.pageContent).use {
            val text = it.reader().readLines().joinToString("\r\n")
            Timber.d("Parsed this from resource=[$resourceName@${R.raw.rules}]:\n$text")
            val flavour = RuleSetMarkFlavourDescriptor()
            val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(text)
            Timber.d("parsedTree=$parsedTree (parent=${parsedTree.parent}, children=${parsedTree.children.stream().map { ast -> ast.getTextInNode(text) }.collect(toList())}")
            val html = HtmlGenerator(text, parsedTree, flavour).generateHtml()

            Timber.d("HTML: $html")
            webView.loadDataWithBaseURL("file:///android_res/drawable/", html, "text/html", "UTF-8", null)
        }


        return root
    }

}