package com.gmail.kenzhang0.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.kenzhang0.MainActivity
import com.gmail.kenzhang0.common.CommonFragment
import com.gmail.kenzhang0.databinding.RecentSearchFragmentBinding
import com.gmail.kenzhang0.databinding.SearchListItemBinding
import com.gmail.kenzhang0.vo.Query
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDispose
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecentSearchFragment : CommonFragment() {
    companion object {
        fun newInstance() = RecentSearchFragment()
    }

    interface ListItemClicked {
        fun sendQuery(query: Query)
    }

    private lateinit var binding: RecentSearchFragmentBinding
    private val viewModel by viewModel<RecentSearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RecentSearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView(viewModel.queryList)
    }

    private fun initRecyclerView(queries: List<Query>) {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = SearchAdapter(queries).apply {
                getPositionClicks()
                    .autoDispose(scope())
                    .subscribe {
                        //click
                        backPreviousPage()
                        (requireActivity() as MainActivity).sendQuery(it)
                    }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }
}

class SearchAdapter(private var list: List<Query>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    private val onClickSubject = PublishSubject.create<Query>()

    override fun onCreateViewHolder(viewHolder: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            SearchListItemBinding.inflate(LayoutInflater.from(viewHolder.context), viewHolder, false)
        return ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, pos: Int) {
        viewHolder.bind(list[pos])
        val item = list[pos]
        viewHolder.itemView.setOnClickListener {
            onClickSubject.onNext(item)
        }
    }

    fun getPositionClicks(): Observable<Query> {
        return onClickSubject
    }

    class ViewHolder(private val binding: SearchListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(query: Query) {
            binding.query = query
            binding.executePendingBindings()
        }
    }
}