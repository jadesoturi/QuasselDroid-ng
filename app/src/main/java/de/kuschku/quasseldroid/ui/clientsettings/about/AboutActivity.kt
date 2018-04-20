package de.kuschku.quasseldroid.ui.clientsettings.about

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.SettingsActivity

class AboutActivity : SettingsActivity(AboutFragment()) {
  companion object {
    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, AboutActivity::class.java)
  }
}