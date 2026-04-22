package com.jnetai.communityalert.ui.about

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jnetai.communityalert.BuildConfig
import com.jnetai.communityalert.R
import com.jnetai.communityalert.databinding.FragmentAboutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import org.json.JSONObject
import java.net.URL

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textVersion.text = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

        binding.btnCheckUpdates.setOnClickListener {
            checkForUpdates()
        }

        binding.btnShare.setOnClickListener {
            shareApp()
        }

        binding.textGithubRepo.text = "github.com/jnetai-clawbot/CommunityAlert"
    }

    private fun checkForUpdates() {
        binding.btnCheckUpdates.isEnabled = false
        binding.textUpdateStatus.text = "Checking for updates..."
        binding.textUpdateStatus.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val url = URL("https://api.github.com/repos/jnetai-clawbot/CommunityAlert/releases/latest")
                    val connection = url.openConnection()
                    connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
                    connection.connect()
                    val response = connection.getInputStream().bufferedReader().readText()
                    val json = JSONObject(response)
                    val tagName = json.getString("tag_name")
                    val htmlUrl = json.optString("html_url", "")
                    Pair(tagName, htmlUrl)
                }

                val (latestVersion, releaseUrl) = result
                val currentVersion = BuildConfig.VERSION_NAME

                if (latestVersion != currentVersion) {
                    binding.textUpdateStatus.text = "New version available: $latestVersion\nTap to download"
                    binding.textUpdateStatus.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(releaseUrl))
                        startActivity(intent)
                    }
                } else {
                    binding.textUpdateStatus.text = "You're on the latest version ($currentVersion)"
                }
            } catch (e: Exception) {
                binding.textUpdateStatus.text = "Could not check for updates: ${e.message}"
            }
            binding.btnCheckUpdates.isEnabled = true
        }
    }

    private fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out CommunityAlert - Community alert system app!\nhttps://github.com/jnetai-clawbot/CommunityAlert")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share CommunityAlert"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}