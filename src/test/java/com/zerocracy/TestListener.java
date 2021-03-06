/*
 * Copyright (c) 2016-2018 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy;

import com.jcabi.log.Logger;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.cactoos.collection.Filtered;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

/**
 * Run listener for the entire test suite.
 * @since 1.0
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class TestListener extends RunListener {

    /**
     * Full log.
     */
    private final ByteArrayOutputStream log = new ByteArrayOutputStream();

    @Override
    public void testRunStarted(final Description description) throws Exception {
        super.testRunStarted(description);
        org.apache.log4j.Logger.getRootLogger().addAppender(
            new WriterAppender(new SimpleLayout(), this.log)
        );
    }

    @Override
    public void testRunFinished(final Result result) throws Exception {
        super.testRunFinished(result);
        final Collection<Thread> alive = new Filtered<>(
            thread -> thread.getName().startsWith("Terminator-")
                || thread.getName().startsWith("AsyncFlush-")
                || thread.getName().startsWith("RvFarm-"),
            Thread.getAllStackTraces().keySet()
        );
        if (!alive.isEmpty()) {
            for (final Thread thread : alive) {
                Logger.warn(
                    this, "Thread is still alive: %d/%s (%b)",
                    thread.getId(), thread.getName(), thread.isDaemon()
                );
            }
            throw new IllegalStateException(
                String.format(
                    "%d threads are still alive, it's a bug, see above",
                    alive.size()
                )
            );
        }
        final String stdout = new String(
            this.log.toByteArray(), StandardCharsets.UTF_8
        );
        if (stdout.contains("Caused by: ")) {
            throw new IllegalStateException(
                "There were some exceptions in the log above"
            );
        }
    }

}
