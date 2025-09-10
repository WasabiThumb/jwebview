import java.util.Locale

enum class OperatingSystem {
    LINUX,
    WINDOWS,
    MACOS;

    val id: String
        get() = this.name.lowercase(Locale.ROOT)

    //

    companion object {

        fun host(): OperatingSystem {
            val name = System.getProperty("os.name") ?: throw Error("Cannot determine host OS")
            return if (name.contains("win", ignoreCase = true)) {
                if (name.contains("darwin", ignoreCase = true)) {
                    MACOS
                } else {
                    WINDOWS
                }
            } else if (name.contains("mac", ignoreCase = true)) {
                MACOS
            } else if (name.contains("linux", ignoreCase = true)) {
                LINUX
            } else {
                throw Error("Unsupported host OS \"${name}\"")
            }
        }

    }

}