package com.financialapp.users.domain.model.valueObject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeviceLabelTest {

    @Test
    void shouldParseChromeOnLinuxUserAgent() {
        String ua = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
        DeviceLabel label = DeviceLabel.fromUserAgent(ua);
        assertThat(label.value()).isEqualTo("Chrome · Linux");
    }

    @Test
    void shouldParseSafariOnIosUserAgent() {
        String ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/605.1.15";
        DeviceLabel label = DeviceLabel.fromUserAgent(ua);
        assertThat(label.value()).isEqualTo("Safari · iOS");
    }

    @Test
    void shouldReturnUnknownDeviceForNullOrBlank() {
        assertThat(DeviceLabel.fromUserAgent(null).value()).isEqualTo("Unknown device");
        assertThat(DeviceLabel.fromUserAgent("").value()).isEqualTo("Unknown device");
        assertThat(DeviceLabel.fromUserAgent("   ").value()).isEqualTo("Unknown device");
    }

    @Test
    void shouldReturnUnknownDeviceForUnrecognizedUa() {
        assertThat(DeviceLabel.fromUserAgent("CustomBot/1.0").value()).isEqualTo("Unknown device");
    }

    @Test
    void shouldRejectBlankLabel() {
        assertThatThrownBy(() -> new DeviceLabel(""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new DeviceLabel("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectLabelExceeding100Chars() {
        String longString = "a".repeat(101);
        assertThatThrownBy(() -> new DeviceLabel(longString))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
