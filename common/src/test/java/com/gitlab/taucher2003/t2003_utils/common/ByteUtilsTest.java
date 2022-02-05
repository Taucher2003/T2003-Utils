package com.gitlab.taucher2003.t2003_utils.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
class ByteUtilsTest {

    @Test
    void uuidToByte() {
        var uuid = UUID.randomUUID();
        var bytes = ByteUtils.uuidToByte(uuid);
        var recoveredUuid = ByteUtils.byteToUuid(bytes);
        assertThat(recoveredUuid).isEqualTo(uuid);
    }

    @Test
    void stringToBinary() {
        var testString = "Some random string";
        var bytes = ByteUtils.stringToBinary(testString);
        var recoveredString = ByteUtils.binaryToString(bytes);
        assertThat(recoveredString).isEqualTo(testString);
    }
}