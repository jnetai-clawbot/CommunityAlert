package com.jnetai.communityalert.ui.create

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.jnetai.communityalert.R
import com.jnetai.communityalert.data.entity.Alert
import com.jnetai.communityalert.data.entity.AlertCategory
import com.jnetai.communityalert.data.entity.Severity
import com.jnetai.communityalert.databinding.FragmentCreateAlertBinding
import com.jnetai.communityalert.ui.viewmodel.AlertViewModel
import com.jnetai.communityalert.util.AlertScheduler
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CreateAlertFragment : Fragment() {

    private var _binding: FragmentCreateAlertBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AlertViewModel
    private var reminderDateTime: LocalDateTime? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[AlertViewModel::class.java]

        setupSpinners()

        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutReminderTime.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        binding.btnSetReminder.setOnClickListener {
            showDateTimePicker()
        }

        binding.btnSubmitAlert.setOnClickListener {
            submitAlert()
        }
    }

    private fun setupSpinners() {
        val categories = AlertCategory.values().map { it.displayName }
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = categoryAdapter

        val severities = Severity.values().map { it.displayName }
        val severityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, severities)
        severityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSeverity.adapter = severityAdapter
    }

    private fun showDateTimePicker() {
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()

        DatePickerDialog(requireContext(), { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                reminderDateTime = LocalDateTime.of(year, month + 1, day, hour, minute)
                binding.textReminderSet.text = getString(
                    R.string.reminder_set_at,
                    reminderDateTime!!.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
                )
            }, currentTime.hour, currentTime.minute, true).show()
        }, currentDate.year, currentDate.monthValue - 1, currentDate.dayOfMonth).show()
    }

    private fun submitAlert() {
        val title = binding.editTitle.text.toString().trim()
        val description = binding.editDescription.text.toString().trim()

        if (title.isEmpty()) {
            binding.editTitle.error = "Title is required"
            return
        }
        if (description.isEmpty()) {
            binding.editDescription.error = "Description is required"
            return
        }

        val category = AlertCategory.values()[binding.spinnerCategory.selectedItemPosition]
        val severity = Severity.values()[binding.spinnerSeverity.selectedItemPosition]

        val alert = Alert(
            title = title,
            description = description,
            category = category,
            severity = severity,
            reminderTime = if (binding.switchReminder.isChecked) reminderDateTime else null
        )

        viewModel.insertAlert(alert)

        // Schedule reminder if set
        if (binding.switchReminder.isChecked && reminderDateTime != null) {
            // We need to schedule after the alert is inserted to get the ID
            // For now, we schedule with a temporary approach
            AlertScheduler.scheduleReminder(requireContext(), alert.copy(id = System.currentTimeMillis()))
        }

        Toast.makeText(requireContext(), "Alert created successfully", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}