package de.kuschku.malheur.config

data class ReportConfig(
  val crash: CrashConfig? = CrashConfig(),
  val threads: Boolean = true,
  val logcat: LogConfig? = LogConfig(),
  val application: AppConfig? = AppConfig(),
  val device: DeviceConfig? = DeviceConfig(),
  val environment: EnvConfig? = EnvConfig()
)
