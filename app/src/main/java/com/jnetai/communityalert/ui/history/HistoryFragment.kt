package com.jnetai.communityalert.ui.history

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jnetai.communityalert.R
import com.jnetai.communityalert.adapter.AlertAdapter
import com.jnetai.communityalert.data.entity.Alert
import com.jnetai.communityalert.databinding.FragmentHistoryBinding
import com.jnetai.communityalert.ui.viewmodel.AlertViewModel
import com.jnetai.communityalert.util.AlertExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import android.widget.Toast
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AlertViewModel
    private lateinit var adapter: AlertAdapter
    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[AlertViewModel::class.java]

        adapter = AlertAdapter(
            onAcknowledge = { id -> viewModel.acknowledgeAlert(id) },
            onDismiss = { id -> viewModel.dismissAlert(id) },
            onClick = { alert ->
                val bundle = Bundle().apply { putLong("alertId", alert.id) }
                findNavController().navigate(R.id.action_historyFragment_to_alertDetailFragment, bundle)
            }
        )

        binding.recyclerHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHistory.adapter = adapter

        viewModel.alerts.observe(viewLifecycleOwner) { alerts ->
            adapter.submitList(alerts)
            binding.textEmptyHistory.visibility = if (alerts.isEmpty()) View.VISIBLE else View.GONE
        }

        setupDateFilters()

        binding.btnExportJson.setOnClickListener {
            exportAlerts()
        }
    }

    private fun setupDateFilters() {
        binding.btnStartDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                startDate = LocalDate.of(year, month + 1, day)
                binding.btnStartDate.text = startDate!!.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                applyDateFilter()
            }, LocalDate.now().year, LocalDate.now().monthValue - 1, LocalDate.now().dayOfMonth).show()
        }

        binding.btnEndDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                endDate = LocalDate.of(year, month + 1, day)
                binding.btnEndDate.text = endDate!!.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                applyDateFilter()
            }, LocalDate.now().year, LocalDate.now().monthValue - 1, LocalDate.now().dayOfMonth).show()
        }

        binding.btnClearDates.setOnClickListener {
            startDate = null
            endDate = null
            binding.btnStartDate.text = getString(R.string.start_date)
            binding.btnEndDate.text = getString(R.string.end_date)
            viewModel.setDateFilter(null, null)
        }
    }

    private fun applyDateFilter() {
        val fmt = DateTimeFormatter.ISO_LOCAL_DATE
        viewModel.setDateFilter(startDate?.format(fmt), endDate?.format(fmt))
    }

    private fun exportAlerts() {
        lifecycleScope.launch {
            val alerts = withContext(Dispatchers.IO) {
                viewModel.getAllAlertsForExport()
            }
            val json = AlertExporter.exportToJson(alerts)
            val file = File(requireContext().getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS), "alerts_export.json")
            file.parentFile?.mkdirs()
            file.writeText(json)
            Toast.makeText(requireContext(), "Exported to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}