package com.example.firebasechat.activity

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasechat.R
import com.example.firebasechat.databinding.ArticlesBinding
import com.example.firebasechat.databinding.FragmentBookBinding
import com.google.android.material.snackbar.Snackbar
import data.ArticleContent
import data.ArticleNumbers
import data.ArticleTitles

data class Article(val number: String, val title: String, val content: String)

class BookFragment : Fragment() {
    private val articlesList: MutableList<Article> = mutableListOf()
    private lateinit var adapter: ArticleAdapter
    private lateinit var binding: FragmentBookBinding // Declare View Binding variable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookBinding.inflate(inflater, container, false) // Initialize View Binding

        // Access the list of custom titles, numbers, and content
        val customTitles = ArticleTitles.titles
        val customNumbers = ArticleNumbers.numbers
        val customContent = ArticleContent.content

        // Determine the minimum size between customTitles, customNumbers, and customContent
        val minSize = minOf(customTitles.size, customNumbers.size, customContent.size)

        // Generate sample articles with custom titles, numbers, and content
        for (i in 0 until minSize) {
            val articleNumber = customNumbers[i]
            val articleTitle = customTitles[i]
            val articleContent = customContent[i]
            val article = Article(articleNumber, articleTitle, articleContent)
            articlesList.add(article)
        }

        val recyclerView = binding.recyclerView // Use View Binding to access views
        adapter = ArticleAdapter(articlesList, parentFragmentManager)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val searchView = binding.searchView // Use View Binding to access views
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        return binding.root
    }
}

class ArticleAdapter(private val articles: List<Article>, private val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<ArticleAdapter.ViewHolder>(), Filterable {

    private var filteredArticles: List<Article> = articles

    inner class ViewHolder(private val binding: ArticlesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Access views through binding
        val articleTitleTextView: TextView = binding.articleTitleTextView
        val articleNumberTextView: TextView = binding.articleNumberTextView
        val articleContentTextView: TextView = binding.articleContentTextView

        init {
            // Set an OnClickListener to open a dialog with article details
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val article = filteredArticles[position]
                    val dialogFragment = ArticleDetailsDialogFragment(article)
                    dialogFragment.show(fragmentManager, "article_details_dialog")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ArticlesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = filteredArticles[position]
        holder.articleTitleTextView.text = article.title
        holder.articleNumberTextView.text = "ARTICLE - ${article.number}"
        holder.articleContentTextView.text = article.content
    }

    override fun getItemCount(): Int {
        return filteredArticles.size
    }
    class ArticleDetailsDialogFragment(private val article: Article) : DialogFragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.dialog_article_details, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val articleNumberTextView = view.findViewById<TextView>(R.id.textViewArticleNumber)
            val articleTitleTextView = view.findViewById<TextView>(R.id.textViewArticleTitle)
            val articleContentTextView = view.findViewById<TextView>(R.id.textViewArticleContent)

            articleNumberTextView.text = article.number
            articleTitleTextView.text = article.title
            articleContentTextView.text = article.content
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<Article>()
                if (constraint.isNullOrEmpty()) {
                    filteredList.addAll(articles)
                } else {
                    val filterPattern = constraint.toString().trim()
                    for (article in articles) {
                        if (article.number.contains(filterPattern, ignoreCase = true)) {
                            filteredList.add(article)
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.values is List<*>) {
                    @Suppress("UNCHECKED_CAST")
                    filteredArticles = results.values as List<Article>
                    notifyDataSetChanged()
                }
            }
        }
    }
}
