rootProject.name = "jwebview"
include(
    ":api",
    ":internals",
    ":bridge",
    ":natives",
    ":natives:all",
    ":natives:linux",
    ":natives:linux:amd64",
    ":natives:windows",
    ":natives:windows:amd64",
    ":natives:macos",
    ":natives:macos:amd64",
    ":natives:macos:arm64"
)
