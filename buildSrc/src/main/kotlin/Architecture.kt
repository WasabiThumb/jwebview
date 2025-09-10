import java.util.Locale

enum class Architecture {
    AMD64,
    ARM64;

    val id: String
        get() = this.name.lowercase(Locale.ROOT)

    //

    companion object {

        fun host(): Architecture {
            val arch = System.getProperty("os.arch")
            if ("x86_64" == arch || "amd64" == arch) return AMD64
            if ("aarch64" == arch || "arm64" == arch) return ARM64
            throw Error("Unsupported architecture \"${arch}\"")
        }

    }

}
