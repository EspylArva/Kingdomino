package com.iteration.kingdomino.ui.appendix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.annotation.RawRes
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.iteration.kingdomino.R
import com.iteration.kingdomino.components.RuleSetMarkFlavourDescriptor
import com.iteration.kingdomino.databinding.FragmentAppendixBinding
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import timber.log.Timber
import java.util.stream.Collectors.toList

class AppendixFragment : Fragment() {

    private lateinit var binding: FragmentAppendixBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppendixBinding.inflate(inflater)

        val args: AppendixFragmentArgs by navArgs()
        setAppendix(args.pageContent)

        return binding.root
    }

    private fun setAppendix(@RawRes resourceId: Int) {
        val resourceName = resources.getResourceName(resourceId)
        resources.openRawResource(resourceId).use {
            val text = it.reader().readLines().joinToString("\r\n")
            Timber.d("Parsed this from resource=[$resourceName@${R.raw.rules}]:\n$text")
            val flavour = RuleSetMarkFlavourDescriptor()
            val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(text)
            Timber.d("parsedTree=$parsedTree (parent=${parsedTree.parent}, children=${parsedTree.children.stream().map { ast -> ast.getTextInNode(text) }.collect(toList())}")
            val html = HtmlGenerator(text, parsedTree, flavour).generateHtml()

            Timber.d("HTML: $html")
            binding.appendixWebView.loadDataWithBaseURL("file:///android_res/drawable/", html, "text/html", "UTF-8", null)
        }
    }
}