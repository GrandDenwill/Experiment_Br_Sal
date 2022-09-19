package com.denwill.kotlin.bristolsalary

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.denwill.kotlin.bristolsalary.databinding.FragmentRosterBinding
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class RosterListFragment: Fragment() {
    private val motor: RosterMotor by viewModel()
    private val menuMap = mutableMapOf<FilterMode, MenuItem>()
    private var binding: FragmentRosterBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_roster,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                add()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentRosterBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = RosterAdapter(
            layoutInflater,
            ::display
        )
        binding?.items?.apply {
            setAdapter(adapter)
            layoutManager = LinearLayoutManager(context)

            addItemDecoration(
                DividerItemDecoration(
                    activity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            motor.states.collect { state ->
                adapter.submitList(state.items)

                binding?.apply {
                    loading.visibility = View.GONE

                    when {
                        state.items.isEmpty() && state.filterMode == FilterMode.ALL -> {
                            empty.visibility = View.VISIBLE
                            empty.setText(R.string.msg_empty)
                        }
                        state.items.isEmpty() -> {
                            empty.visibility = View.VISIBLE
                            empty.setText(R.string.msg_empty_filtered)
                        }
                        else -> empty.visibility = View.GONE
                    }
                }

                menuMap[state.filterMode]?.isChecked = true
            }
        }
    }
    private fun display(model: SalaryModel) {
        findNavController()
            .navigate(RosterListFragmentDirections.createModel(model.id))
    }
    private fun add() {
        findNavController().navigate(RosterListFragmentDirections.createModel(null))
    }
}
