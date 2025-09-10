package io.github.wasabithumb.jwebview.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@ApiStatus.Internal
public final class Natives {

    public static void load() throws IOException {
        try (EphemeralFile file = resolve()) {
            System.load(file.path().toAbsolutePath().toString());
        }
    }

    private static @NotNull EphemeralFile resolve() throws IOException {
        final OS os = calcHostOS();
        final Arch arch = calculateHostArch();
        final String dir = getDir(os, arch);
        final String name = getName(os);

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) cl = ClassLoader.getSystemClassLoader();

        Enumeration<URL> e = cl.getResources(dir);
        while (e.hasMoreElements()) {
            URL root = e.nextElement();
            String protocol = root.getProtocol();
            if ("file".equals(protocol)) {
                Path path = FileSystems.getDefault().getPath(root.getPath());
                Path file = path.resolve(name);
                if (Files.exists(file)) return new EphemeralFile.Basic(file);
            } else if ("jar".equals(protocol)) {
                String p = root.getPath();
                if (!p.startsWith("file:")) continue;
                int i = p.indexOf('!');
                if (i == -1) {
                    p = p.substring(5);
                } else {
                    p = p.substring(5, i);
                }
                Path archive = FileSystems.getDefault().getPath(p);
                EphemeralFile ret = resolveJar(archive, dir + "/" + name, name);
                if (ret != null) return ret;
            }
        }

        throw new IllegalStateException("Resource \"" + dir + "/" + name + "\" not found");
    }

    private static @Nullable EphemeralFile resolveJar(
            @NotNull Path archive,
            @NotNull String entry,
            @NotNull String name
    ) throws IOException {
        try (ZipFile zf = new ZipFile(archive.toFile())) {
            ZipEntry ze = zf.getEntry(entry);
            if (ze == null) return null;

            Path tempDir = Files.createTempDirectory("jwebview");
            Path tempFile = tempDir.resolve(name);
            EphemeralFile ret = new EphemeralFile.InTempDir(tempDir, tempFile);

            try (InputStream is = zf.getInputStream(ze)) {
                try (OutputStream os = Files.newOutputStream(tempFile, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                    byte[] buf = new byte[8192];
                    int read;

                    while ((read = is.read(buf)) != -1) {
                        os.write(buf, 0, read);
                    }
                    os.flush();
                }
            } catch (IOException e) {
                try {
                    ret.close();
                } catch (IOException e1) {
                    e.addSuppressed(e1);
                }
                throw e;
            }

            return ret;
        }
    }

    private static @NotNull OS calcHostOS() {
        String name = System.getProperty("os.name");
        if (name == null) throw new AssertionError("Cannot determine system OS");
        name = name.toLowerCase(Locale.ROOT);
        if (name.contains("win")) {
            if (name.contains("darwin")) return OS.MACOS;
            return OS.WINDOWS;
        } else if (name.contains("mac")) {
            return OS.MACOS;
        } else if (name.contains("linux")) {
            return OS.LINUX;
        } else {
            throw new IllegalStateException("Unsupported system OS: " + name);
        }
    }

    private static @NotNull Arch calculateHostArch() {
        final String arch = System.getProperty("os.arch");
        if ("x86_64".equals(arch) || "amd64".equals(arch)) return Arch.AMD64;
        if ("aarch64".equals(arch) || "arm64".equals(arch)) return Arch.ARM64;
        throw new IllegalStateException("Unsupported system architecture: " + arch);
    }

    private static @NotNull String getDir(@NotNull OS os, @NotNull Arch arch) {
        return "natives/" +
                os.name().toLowerCase(Locale.ROOT) +
                "/" +
                arch.name().toLowerCase(Locale.ROOT);
    }

    private static @NotNull String getName(@NotNull OS os) {
        switch (os) {
            case LINUX:
                return "libjwebview.so";
            case WINDOWS:
                return "jwebview.dll";
            case MACOS:
                return "libjwebview.dylib";
            default:
                throw new AssertionError("Unreachable code");
        }
    }

    //

    private Natives() { }

    //

    private enum OS {
        LINUX,
        WINDOWS,
        MACOS
    }

    private enum Arch {
        AMD64,
        ARM64
    }

    private interface EphemeralFile extends Closeable {

        @NotNull Path path();

        //

        final class Basic implements EphemeralFile {

            private final Path path;

            Basic(@NotNull Path path) {
                this.path = path;
            }

            //

            @Override
            public @NotNull Path path() {
                return this.path;
            }

            @Override
            public void close() { }

        }

        final class InTempDir implements EphemeralFile {

            private final Path tempDir;
            private final Path path;

            InTempDir(@NotNull Path tempDir, @NotNull Path path) {
                this.tempDir = tempDir;
                this.path = path;
            }

            //

            @Override
            public @NotNull Path path() {
                return this.path;
            }

            @Override
            public void close() throws IOException {
                recursiveDelete(this.tempDir);
            }

            private static void recursiveDelete(@NotNull Path dir) throws IOException {
                try (Stream<Path> stream = Files.list(dir)) {
                    Iterator<Path> iter = stream.iterator();
                    Path next;
                    while (iter.hasNext()) {
                        next = iter.next();
                        if (Files.isDirectory(next, LinkOption.NOFOLLOW_LINKS)) {
                            recursiveDelete(next);
                        } else {
                            Files.delete(next);
                        }
                    }
                }
                Files.delete(dir);
            }

        }

    }

}
