package io.github.wasabithumb.jwebview;

import io.github.wasabithumb.jwebview.bridge.GsonBindCallback;
import io.github.wasabithumb.jwebview.option.JWebViewOption;
import io.github.wasabithumb.jwebview.option.JWebViewOptions;
import io.github.wasabithumb.jwebview.param.Metrics;
import io.github.wasabithumb.xpdy.XpdyServer;
import io.github.wasabithumb.xpdy.nd.StaticContent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JWebViewTest {

    private static XpdyServer SERVER;

    @BeforeAll
    static void startServer() {
        XpdyServer server = XpdyServer.builder()
                .port(8080)
                .staticContent(StaticContent.resources("www"))
                .build();

        assertDoesNotThrow(server::start);
        SERVER = server;
    }

    @AfterAll
    static void stopServer() {
        SERVER.stop(0);
    }

    @Test
    void fiveSecondTimer() {
        JWebViewOptions options = JWebViewOptions.builder()
                .set(JWebViewOption.INITIAL_METRICS, Metrics.of(640, 480))
                .set(JWebViewOption.DEVTOOLS, true)
                .build();
        Bindings bindings = new Bindings();

        System.out.println("Starting WebView");
        WebViewHandle handle = JWebView.jWebView(options).createAsync((WebView wv) -> {
            wv.setTitle("5 Second Timer");
            wv.navigate("http://127.0.0.1:8080/");
            wv.bind("complete", GsonBindCallback.reflect(bindings, "complete"));
        });

        System.out.println("Waiting for message");
        bindings.awaitCompleted();

        System.out.println("Closing");
        handle.close();
    }

    //

    @SuppressWarnings("unused")
    private static final class Bindings {

        private final Object mutex;
        private boolean completed;

        Bindings() {
            this.mutex = new Object();
            this.completed = false;
        }

        //

        public void awaitCompleted() {
            while (true) {
                synchronized (this.mutex) {
                    if (this.completed) return;
                    try {
                        this.mutex.wait();
                    } catch (InterruptedException e) {
                        throw new IllegalStateException("Thread interrupted", e);
                    }
                }
            }
        }

        public void complete() {
            synchronized (this.mutex) {
                this.completed = true;
                this.mutex.notify();
            }
        }

    }

}
