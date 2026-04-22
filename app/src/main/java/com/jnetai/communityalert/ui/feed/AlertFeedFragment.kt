package com.jnetai.communityalert.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jnetai.communityalert.R
import com.jnetai.communityalert.adapter.AlertAdapter
import com.jnetai.communityalert.data.entity.AlertCategory
import com.jnetai.communityalert.data.entity.Severity
import com.jnetai.communityalert.databinding.FragmentAlertFeedBinding
import com.jnetai.communityalert.ui.viewmodel.AlertViewModel

class AlertFeedFragment : Fragment() {

    private var _binding: FragmentAlertFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AlertViewModel
    private lateinit var adapter: AlertAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[AlertViewModel::class.java]

        adapter = AlertAdapter(
            onAcknowledge = { id -> viewModel.acknowledgeAlert(id) },
            onDismiss = { id -> viewModel.dismissAlert(id) },
            onClick = { alert ->
                val bundle = Bundle().apply {
                    putLong("alertId", alert.id)
                }
                findNavController().navigate(R.id.action_alertFeedFragment_to_alertDetailFragment, bundle)
            }
        )

        binding.recyclerAlerts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAlerts.adapter = adapter

        setupFilters()

        viewModel.alerts.observe(viewLifecycleOwner) { alerts ->
            adapter.submitList(alerts)
            binding.textEmptyState.visibility = if (alerts.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerAlerts.visibility = if (alerts.isEmpty()) View.GONE else View.VISIBLE
        }

        // Critical alert banner
        viewModel.criticalAlerts.observe(viewLifecycleOwner) { criticalAlerts ->
            if (criticalAlerts.isNotEmpty()) {
                binding.criticalBanner.root.visibility = View.VISIBLE
                binding.criticalBanner.textCriticalCount.text =
                    getString(R.string.critical_alerts_count, criticalAlerts.size)
            } else {
                binding.criticalBanner.root.visibility = View.GONE
            }
        }

        binding.fabCreateAlert.setOnClickListener {
            findNavController().navigate(R.id.action_alertFeedFragment_to_createAlertFragment)
        }
    }

    private fun setupFilters() {
        // Category filter
        val categories = listOf("All") + AlertCategory.values().map { it.displayName }
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategoryFilter.adapter = categoryAdapter

        // Severity filter
        val severities = listOf("All") + Severity.values().map { it.displayName }
        val severityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, severities)
        severityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSeverityFilter.adapter = severityAdapter

        binding.spinnerCategoryFilter.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val category = if (position == 0) null else AlertCategory.values()[position - 1]
                viewModel.setCategoryFilter(category)
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        binding.spinnerSeverityFilter.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val severity = if (position == 0) null else Severity.values()[position - 1]
                viewModel.setSeverityFilter(severity)
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        binding.btnClearFilters.setOnClickListener {
            binding.spinnerCategoryFilter.setSelection(0)
            binding.spinnerSeverityFilter.setSelection(0)
            viewModel.clearFilters()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}