package com.spertus.jacquard.common;

import com.google.common.annotations.VisibleForTesting;
import com.spertus.jacquard.exceptions.*;

/**
 * A singleton class containing configuration information. The client must
 * initialize the Autograder before calling other Jacquard code. This can
 * be done throw {@link Builder} or {@link #init()}.
 */
public final class Autograder {
    private static Autograder instance;

    /**
     * The Java level of student code.
     */
    public final int javaLevel;

    /**
     * The number of milliseconds tests should run before timing out.
     */
    public final long timeoutMillis;

    /**
     * The visibility level of {@link Grader} results.
     */
    public final Visibility visibility;

    /**
     * The level of debugging information to provide.
     */
    public final DebugLevel debugLevel;

    /**
     * A singleton class for building the Autograder.
     */
    @SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "PMD.AvoidFieldNameMatchingTypeName"})
    public static class Builder {
        /**
         * The default timeout for a {@link Grader}, in milliseconds.
         */
        public static final long DEFAULT_TIMEOUT_MS = 10_000L;

        /**
         * The default Java language level.
         */
        public static final int DEFAULT_JAVA_LEVEL = 17;

        /**
         * The default visibility of test results.
         */
        public static final Visibility DEFAULT_VISIBILITY = Visibility.VISIBLE;

        /**
         * The default debug level.
         */
        public static final DebugLevel DEFAULT_DEBUG_LEVEL = DebugLevel.LOW;

        private static final Builder INSTANCE = new Builder();

        private boolean built = false;
        private long timeoutMillis = DEFAULT_TIMEOUT_MS;
        private int javaLevel = DEFAULT_JAVA_LEVEL;
        private Visibility visibility = DEFAULT_VISIBILITY;
        private DebugLevel debugLevel;

        private Builder() {
        }

        /**
         * Gets the singleton instance of Builder.
         *
         * @return the instance
         */
        public static Builder getInstance() {
            return INSTANCE;
        }

        private void verifyMutability() {
            if (built) {
                throw new ClientException("The builder must not be modified after build() is called.");
            }
        }
        /**
         * Sets the timeout for {@link Grader} execution (or 0 for no timeout).
         * If this method is not called, {@link #DEFAULT_TIMEOUT_MS} is used.
         *
         * @param timeout the timeout in milliseconds or 0 for no timeout
         * @return the builder
         * @throws ClientException if this builder has already been built
         */
        public Builder timeout(final long timeout) {
            verifyMutability();
            timeoutMillis = timeout;
            return this;
        }

        /**
         * Sets the Java language level. If this method is not called,
         * {@link #DEFAULT_JAVA_LEVEL} is used.
         *
         * @param javaLevel the Java language level
         * @return the builder
         * @throws ClientException if this builder has already been built
         */
        public Builder javaLevel(final int javaLevel) {
            verifyMutability();
            this.javaLevel = javaLevel;
            return this;
        }

        /**
         * Sets the visibility of {@link Grader} results. If this method is no
         * called, {@link #DEFAULT_VISIBILITY} is used.
         *
         * @param visibility the visibility
         * @return the builder
         * @throws ClientException if this builder has already been built
         */
        public Builder visibility(final Visibility visibility) {
            verifyMutability();
            this.visibility = visibility;
            return this;
        }

        /**
         * Sets debug level, which is by default {@link #DEFAULT_DEBUG_LEVEL}.
         *
         * @param debugLevel the level of debugging information
         * @return the builder
         * @throws ClientException if this builder has already been built
         * @see DebugLevel
         */
        public Builder debugLevel(final DebugLevel debugLevel) {
            verifyMutability();
            this.debugLevel = debugLevel;
            return this;
        }

        /**
         * Builds the Autograder using information from this builder. This
         * may be called only once per program execution (unless
         * {@code VisibleForTesting} methods are used).
         *
         * @throws ClientException if this builder has already been built
         */
        public void build() {
            if (built) {
                throw new ClientException("The build method can be called only once.");
            }
            makeAutograder(this);
            built = true;
        }

        private void resetForTest() {
            built = false;
            timeout(DEFAULT_TIMEOUT_MS);
            javaLevel(DEFAULT_JAVA_LEVEL);
            visibility(DEFAULT_VISIBILITY);
            debugLevel(DEFAULT_DEBUG_LEVEL);
        }
    }

    private Autograder(final Builder builder) {
        javaLevel = builder.javaLevel;
        timeoutMillis = builder.timeoutMillis;
        visibility = builder.visibility;
        debugLevel = builder.debugLevel;
    }

    private static void makeAutograder(final Builder builder) {
        if (instance != null) {
            throw new ClientException("Autograder has already been initialized.");
        }
        instance = new Autograder(builder);
    }

    /**
     * Initializes the autograder with all default values. If customized values
     * are desired, {@link Builder} should be used instead.
     *
     * @throws ClientException if the Autograder is initialized more than once
     */
    public static void init() {
        if (instance != null) {
            throw new ClientException("Autograder.init() cannot be called after the autograder has been built.");
        }
        Builder.INSTANCE.build();
    }

    /**
     * Gets the singleton Autograder instance.
     *
     * @return the singleton Autograder instance
     * @throws ClientException if the Autograder has not been initialized
     */
    public static Autograder getInstance() {
        if (instance == null) {
            throw new ClientException("Autograder not initialized.");
        }
        return instance;
    }

    /**
     * Checks whether the Autograder has been initialized.
     *
     * @return whether the autograder has been initialized
     */
    public static boolean isInitialized() {
        return instance != null;
    }

    /**
     * Resets Autograder initialization so multiple tests can
     * run independently.
     */
    @VisibleForTesting
    public static void resetForTest() {
        instance = null;
        Builder.getInstance().resetForTest();
    }

    /**
     * Resets and reinitializes the Autograder. This is a convenience method
     * to make testing more convenient. Timeout is set to 0 (no timeout).
     */
    @VisibleForTesting
    public static void initForTest() {
        resetForTest();
        Builder.getInstance().timeout(0).build();
    }
}
