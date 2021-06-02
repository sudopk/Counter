package com.sudopk.counter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.commit
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {
  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.preferences, rootKey)
  }
}

class SettingsDialogFragment : AppCompatDialogFragment() {
  companion object {
    val TAG: String = SettingsDialogFragment::class.java.name
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    dialog?.setCanceledOnTouchOutside(true)
    return inflater.inflate(R.layout.container, container, false)
  }

  override fun onResume() {
    super.onResume()
    childFragmentManager.commit { replace(R.id.container, SettingsFragment()) }
  }
}
