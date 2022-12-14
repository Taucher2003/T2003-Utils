package com.gitlab.taucher2003.t2003_utils.common.i18n;

import java.util.function.Function;

/**
 * Interface for locking generic types of the {@link Function}.
 * Used primarily by {@link Replacement}.
 */
@FunctionalInterface
public interface StringFormat extends Function<String, String> {
}
