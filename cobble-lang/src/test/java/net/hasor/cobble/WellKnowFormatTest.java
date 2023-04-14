package net.hasor.cobble;
import org.junit.Test;

import java.time.OffsetDateTime;

public class WellKnowFormatTest {
    @Test
    public void testWKF_0() {
        assert "2021-04-26+08:00".equals(WellKnowFormat.WKF_OFFSET_DATE10.format(OffsetDateTime.parse("2021-04-26T12:12:12+08:00")));
    }
}
