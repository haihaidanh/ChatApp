package com.example.chat_app1204.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.chat_app1204.R
import com.example.chat_app1204.data.utils.LanguageHelper
import com.example.chat_app1204.databinding.FragmentSettingBinding
import com.example.chat_app1204.ui.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var mBinding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSettingBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentLang = LanguageHelper.getLanguage(requireContext())

        if (currentLang == "en") {
            mBinding.spinnerLanguage.setSelection(0)
        } else {
            mBinding.spinnerLanguage.setSelection(1)
        }

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.languages,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spinnerLanguage.adapter = adapter

        mBinding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                val currentLang = LanguageHelper.getLanguage(requireContext())

                val selectedLang = when (position) {
                    0 -> "en"
                    else -> "vi"
                }

                if (selectedLang != currentLang) {
                    changeLanguage(selectedLang)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

    }

    private fun changeLanguage(lang: String) {
        LanguageHelper.saveLanguage(requireContext(), lang)

        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
