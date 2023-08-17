package dev.sora.relay.cheat.module.i18n

import dev.sora.relay.utils.logInfo
import dev.sora.relay.utils.logWarn
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

class I18n(private val locale: String) {
	private val translateMap = HashMap<String, String>()
	private val path = "/assets/lang"

	init {
		read(locale)
	}

	fun languageList(): List<String> {
		val folderList = mutableListOf<String>()

		val inputStream = I18n::class.java.classLoader.getResourceAsStream("$path/")
		inputStream?.use { stream ->
			BufferedReader(InputStreamReader(stream)).use { reader ->
				var line: String?
				while (reader.readLine().also { line = it } != null) {
					if (line?.endsWith("/") == true) {
						val folderName = line!!.substring(0, line!!.length - 1)
						folderList.add(folderName)
					}
				}
			}
		}

		return folderList
	}

	private fun find(str: String): InputStream {
		try {
			I18n::class.java.classLoader.getResourceAsStream("$path/${str}/language.lang")?.let {
				return it
			}
		} catch (e: Exception) {
			logWarn("Can't find language file! " + e.message)
		}

		I18n::class.java.classLoader.getResourceAsStream("$path/language.lang")?.let {
			return it
		}

		throw IllegalStateException("Can't find language file!")
	}

	private fun read(locales: String) {
		logInfo("Loading languages file...")
		val prop = Properties()

		prop.load(InputStreamReader(find(locales), Charsets.UTF_8))

		for ((key, value) in prop.entries) {
			if (key is String && value is String) {
				translateMap[key] = value
			}
		}
	}

	fun get(key: String): String {
		return translateMap[key] ?: key
	}
}
