package com.gitlab.taucher2003.t2003_utils.log;

import ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.CoreConstants;

public class ThrowableSpacer extends ExtendedThrowableProxyConverter {

    @Override
    protected String throwableProxyToString(IThrowableProxy tp) {
        return super.throwableProxyToString(tp) + CoreConstants.LINE_SEPARATOR;
    }
}
