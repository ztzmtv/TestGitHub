package com.aztown.githubapi.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aztown.githubapi.R
import com.aztown.githubapi.databinding.FragmentRepositoriesListBinding
import com.aztown.githubapi.presentation.adapter.GithubListAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class RepositoriesListFragment : Fragment() {

    private var _binding: FragmentRepositoriesListBinding? = null

    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var githubAdapter: GithubListAdapter

    private val component by lazy {
        (requireActivity().application as GithubApplication).component
    }

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[RepositoriesViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepositoriesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupListeners()
    }

    private fun setupAdapter() {
        binding.rvGithubList.adapter = githubAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.gitRepoFlow.collectLatest {
                githubAdapter.submitData(it)
            }
        }
    }

    private fun setupListeners() {
        setupAdapterClickListener()
        setupSearchViewClickListener()
        setupSwipeToRefreshListener()
    }

    private fun setupSwipeToRefreshListener() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupSearchViewClickListener() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return if (query != null) {
                    viewModel.loadRepositories(query)
                    true
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Запрос пустой. Введите запрос",
                        Toast.LENGTH_SHORT
                    ).show()
                    false
                }
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.loadRepositories(newText)
                return true
            }
        })
    }

    private fun setupAdapterClickListener() {
        githubAdapter.onRepoClickListener = { username ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, RepositoryUserFragment.newInstance(username))
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = RepositoriesListFragment()
    }
}