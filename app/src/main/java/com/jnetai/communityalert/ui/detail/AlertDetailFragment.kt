package com.jnetai.communityalert.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jnetai.communityalert.R
import com.jnetai.communityalert.data.entity.Alert
import com.jnetai.communityalert.databinding.FragmentAlertDetailBinding
import com.jnetai.communityalert.ui.viewmodel.AlertViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter

class AlertDetailFragment : Fragment() {

    private var _binding: FragmentAlertDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AlertViewModel
    private var alertId: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[AlertViewModel::class.java]

        alertId = arguments?.getLong("alertId") ?: -1
        if (alertId == -1L) {
            findNavController().navigateUp()
            return
        }

        loadAlert()
    }

    private fun loadAlert() {
        viewLifecycleOwner.lifecycleScope.launch {
            val alert = withContext(Dispatchers.IO) {
                viewModel.getAlertById(alertId)
            }

            if (alert == null) {
                findNavController().navigateUp()
                return@launch
            }

            displayAlert(alert)
        }
    }

    private fun displayAlert(alert: Alert) {
        val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")

        binding.textDetailTitle.text = alert.title
        binding.textDetailDescription.text = alert.description
        binding.textDetailCategory.text = alert.category.displayName
        binding.textDetailSeverity.text = alert.severity.displayName
        binding.textDetailDate.text = alert.createdAt.format(dateFormatter)

        // Severity color
        val colorRes = when (alert.severity) {
            com.jnetai.communityalert.data.entity.Severity.CRITICAL -> R.color.severity_critical
            com.jnetai.communityalert.data.entity.Severity.WARNING -> R.color.severity_warning
            com.jnetai.communityalert.data.entity.Severity.INFO -> R.color.severity_info
        }
        binding.textDetailSeverity.setTextColor(
            androidx.core.content.ContextCompat.getColor(requireContext(), colorRes)
        )

        // Category icon
        val iconRes = when (alert.category) {
            com.jnetai.communityalert.data.entity.AlertCategory.WEATHER -> R.drawable.ic_weather
            com.jnetai.communityalert.data.entity.AlertCategory.SAFETY -> R.drawable.ic_safety
            com.jnetai.communityalert.data.entity.AlertCategory.TRAFFIC -> R.drawable.ic_traffic
            com.jnetai.communityalert.data.entity.AlertCategory.COMMUNITY -> R.drawable.ic_community
        }
        binding.iconDetailCategory.setImageResource(iconRes)

        // Status
        binding.textDetailStatus.text = when {
            alert.dismissed -> "Dismissed"
            alert.acknowledged -> "Acknowledged ✓"
            else -> "Active"
        }

        // Buttons
        binding.btnAcknowledge.visibility = if (!alert.acknowledged && !alert.dismissed) View.VISIBLE else View.GONE
        binding.btnDismiss.visibility = if (!alert.dismissed) View.VISIBLE else View.GONE

        binding.btnAcknowledge.setOnClickListener {
            viewModel.acknowledgeAlert(alert.id)
            loadAlert()
        }

        binding.btnDismiss.setOnClickListener {
            viewModel.dismissAlert(alert.id)
            loadAlert()
        }

        if (alert.reminderTime != null) {
            binding.textDetailReminder.text = "Reminder: ${alert.reminderTime!!.format(dateFormatter)}"
            binding.textDetailReminder.visibility = View.VISIBLE
        } else {
            binding.textDetailReminder.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}