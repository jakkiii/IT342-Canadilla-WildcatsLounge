package edu.cit.canadilla.wildcatslounge.mobile.feature.profile.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import edu.cit.canadilla.wildcatslounge.mobile.R
import edu.cit.canadilla.wildcatslounge.mobile.feature.menu.data.MenuRepository
import edu.cit.canadilla.wildcatslounge.mobile.feature.auth.ui.LoginActivity
import edu.cit.canadilla.wildcatslounge.mobile.core.util.SessionManager
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private val menuRepository = MenuRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sessionManager = SessionManager(requireContext())
        val user = sessionManager.getUser()

        view.findViewById<TextView>(R.id.tvFirstName).text = user?.firstname ?: ""
        view.findViewById<TextView>(R.id.tvLastName).text = user?.lastname ?: ""
        view.findViewById<TextView>(R.id.tvEmail).text = user?.email ?: ""
        view.findViewById<TextView>(R.id.tvStudentId).text = user?.studentId ?: "Not provided"

        val description = view.findViewById<EditText>(R.id.etDescription)
        val count = view.findViewById<TextView>(R.id.tvDescriptionCount)
        description.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, countValue: Int) {
                count.text = "${s?.length ?: 0}/255"
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })

        val favorites = view.findViewById<TextView>(R.id.tvFavorites)
        viewLifecycleOwner.lifecycleScope.launch {
            val response = menuRepository.getMenuItems()
            if (response.success && response.data != null) {
                favorites.text = response.data.take(3).joinToString { it.name }
            } else {
                favorites.text = "No favorites yet"
            }
        }

        view.findViewById<Button>(R.id.btnSaveProfile).setOnClickListener {
            // Profile persistence can be wired to backend when endpoint is available.
        }

        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            sessionManager.clearSession()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}

