package com.spertus.jacquard.common;

import com.google.common.annotations.VisibleForTesting;
import com.spertus.jacquard.exceptions.ClientException;

/**
 * A singleton class containing configuration information. The client must
 * initialize the Autograder before calling other Jacquard code. This can
 * be done throw {@link Builder} or {@link #init()}.
 */
public class Autograder {
    /**
     * A singleton class for building the Autograder.
     */
    public static class Builder {
        /**
         * The default timeout for a {@link Grader}, in milliseconds.
         */
        public static final long DEFAULT_TIMEOUT_MS = 10000L;

        /**
         * The default Java language level.
         */
        public static final int DEFAULT_JAVA_LEVEL = 17;

        /**
         * The default visibility of test results.
         */
        public static final Visibility DEFAULT_VISIBILITY = Visibility.VISIBLE;

        private static Builder INSTANCE = new Builder();

        private boolean built = false;
        private long timeoutMillis = DEFAULT_TIMEOUT_MS;
        private int javaLevel = DEFAULT_JAVA_LEVEL;
        private Visibility visibility = DEFAULT_VISIBILITY;

        private Builder() {
        }

        public static Builder getInstance() {
            return INSTANCE;
        }

        /**
         * Sets the timeout for {@link Grader} execution (or 0 for no timeout).
         * If this method is not called, {@link #DEFAULT_TIMEOUT_MS} is used.
         *
         * @param timeout the timeout in milliseconds or 0 for no timeout
         * @return the builder
         */
        public Builder setTimeout(long timeout) {
            if (built) {
                throw new ClientException("The builder must not be modified after build() is called.");
            }
            timeoutMillis = timeout;
            return this;
        }

        /**
         * Sets the Java language level. If this method is not called,
         * {@link #DEFAULT_JAVA_LEVEL} is used.
         *
         * @param javaLevel the Java language level
         * @return the builder
         */
        public Builder setJavaLevel(int javaLevel) {
            if (built) {
                throw new ClientException("The builder must not be modified after build() is called.");
            }
            this.javaLevel = javaLevel;
            return this;
        }

        /**
         * Sets the visibility of {@link Grader} results. If this method is no
         * called, {@link #DEFAULT_VISIBILITY} is used.
         *
         * @param visibility the visibility
         * @return the builder
         */
        public Builder setVisibility(Visibility visibility) {
            if (built) {
                throw new ClientException("The builder must not be modified after build() is called.");
            }
            this.visibility = visibility;
            return this;
        }

        /**
         * Builds the Autograder using information from this builder. This
         * may be called only once per program execution (unless
         * {@code VisibleForTesting} methods are used).
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
            setTimeout(DEFAULT_TIMEOUT_MS);
            setJavaLevel(DEFAULT_JAVA_LEVEL);
            setVisibility(DEFAULT_VISIBILITY);
        }
    }

    private static Autograder instance;

    public final int javaLevel;
    public final long timeoutMillis;
    private final Visibility visibility;

    private Autograder(Builder builder) {
        javaLevel = builder.javaLevel;
        timeoutMillis = builder.timeoutMillis;
        visibility = builder.visibility;
    }

    private static void makeAutograder(Builder builder) {
        if (instance != null) {
            throw new RuntimeException();
        }
        instance = new Autograder(builder);
    }

    /**
     * Initializes the autograder with all default values. If customized values
     * are desired, {@link Builder} should be used instead.
     *
     * @throws ClientException if the Autograder is initialized more than once
     */
    public static void init() throws ClientException {
        if (instance != null) {
            throw new ClientException("Autograder.init() cannot be called after the autograder has been built.");
        }
        Builder.INSTANCE.build();
    }

    /**
     * Gets the singleton Autograder instance.
     *
     * @throws ClientException if the Autograder has not been initialized
     */
    public static Autograder getInstance() throws ClientException {
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
     * to make testing more convenient.
     */
    @VisibleForTesting
    public static void initForTest() {
        resetForTest();
        init();
    }
}
