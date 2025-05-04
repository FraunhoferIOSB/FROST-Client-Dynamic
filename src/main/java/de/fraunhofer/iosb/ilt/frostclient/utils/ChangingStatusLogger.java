/*
 * Copyright (C) 2024 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.fraunhofer.iosb.ilt.frostclient.utils;

import de.fraunhofer.iosb.ilt.settings.ConfigProvider;
import de.fraunhofer.iosb.ilt.settings.Settings;
import de.fraunhofer.iosb.ilt.settings.annotation.DefaultValueInt;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

/**
 * A logger that regularly logs a status message, if the status has changed.
 */
public class ChangingStatusLogger extends ConfigProvider<ChangingStatusLogger> {

    @DefaultValueInt(1000)
    public static final String TAG_CNF_LOG_INTERVAL_MS = "logInterval";

    /**
     * Implementations MUST override the equals method.
     */
    public static interface ChangingStatus {

        /**
         * Get the message template.
         *
         * @return the message template, with placeholders for the parameters,
         * to pass to the logger.
         */
        public String getLogMessageTemplate();

        /**
         * Get the parameters to pass to the logger when logging a message.
         *
         * @return The parameters to pass to the logger.
         */
        public Object[] getCurrentParams();

        /**
         * Get a copy of the parameters array.
         *
         * @return a copy of the parameters array.
         */
        public Object[] getCopyCurrentParams();

        /**
         * Check if the status changed, and if so, log the configured message to
         * the given logger. The method will be periodically called.
         *
         * @param logger The logger to log to.
         */
        public void logIfChanged(Logger logger);

        /**
         * Called before each log action. Can be used by an implementation to
         * gather statistics.
         */
        public default void process() {
            // Do nothing by default
        }

    }

    public static class ChangingStatusDefault implements ChangingStatus {

        private final String logMessageTemplate;
        private final Object[] status;
        private Object[] previous;

        public ChangingStatusDefault(String logMessageTemplate, Object[] status) {
            this.logMessageTemplate = logMessageTemplate;
            this.status = status;
        }

        public ChangingStatusDefault(String logMessageTemplate, int paramCount) {
            this.logMessageTemplate = logMessageTemplate;
            this.status = new Object[paramCount];
        }

        public final void setAllTo(Object value) {
            for (int idx = 0; idx < status.length; idx++) {
                status[idx] = value;
            }
        }

        public final void setObjectAt(int idx, Object value) {
            status[idx] = value;
        }

        @Override
        public String getLogMessageTemplate() {
            return logMessageTemplate;
        }

        @Override
        public final Object[] getCurrentParams() {
            return status;
        }

        @Override
        public Object[] getCopyCurrentParams() {
            return Arrays.copyOf(status, status.length);
        }

        @Override
        public void logIfChanged(Logger logger) {
            try {
                process();
                Object[] currentStatus = getCopyCurrentParams();
                if (!Arrays.deepEquals(currentStatus, previous)) {
                    previous = currentStatus;
                    logger.info(logMessageTemplate, previous);
                }
            } catch (RuntimeException ex) {
                logger.warn("Exception checking changes: {}", ex.getMessage());
                logger.debug("Exception:", ex);
            }
        }

    }

    private long logIntervalMs = 1000;

    private final Logger logger;
    private final List<ChangingStatus> logStatuses = new CopyOnWriteArrayList<>();

    private ScheduledExecutorService executor;
    private final Runnable task;
    private boolean running = false;

    /**
     * Create a new ChangingStatusLogger, with the given logger.
     *
     * @param settings the settings to load from.
     * @param logger The logger to log to.
     */
    public ChangingStatusLogger(Settings settings, Logger logger) {
        super(settings);
        this.logger = logger;
        task = this::maybeLog;
        logIntervalMs = getInt(TAG_CNF_LOG_INTERVAL_MS);
    }

    public ChangingStatusLogger addLogStatus(ChangingStatus logStatus) {
        logStatuses.add(logStatus);
        return this;
    }

    public ChangingStatusLogger removeLogStatus(ChangingStatus logStatus) {
        logStatuses.remove(logStatus);
        logStatus.logIfChanged(logger);
        return this;
    }

    public ChangingStatusLogger setLogIntervalMs(long logIntervalMs) {
        this.logIntervalMs = logIntervalMs;
        return this;
    }

    public ChangingStatusLogger start() {
        if (running) {
            return this;
        }
        if (executor == null) {
            executor = Executors.newSingleThreadScheduledExecutor();
        }
        running = true;
        executor.scheduleAtFixedRate(task, logIntervalMs, logIntervalMs, TimeUnit.MILLISECONDS);
        return this;
    }

    public void stop() {
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
        running = false;
        maybeLog();
    }

    private void maybeLog() {
        for (ChangingStatus status : logStatuses) {
            status.logIfChanged(logger);
        }
    }

    @Override
    public ChangingStatusLogger getThis() {
        return this;
    }

}
