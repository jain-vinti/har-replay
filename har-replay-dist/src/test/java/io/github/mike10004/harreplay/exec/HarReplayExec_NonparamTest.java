package io.github.mike10004.harreplay.exec;

import io.github.mike10004.subprocess.ProcessResult;
import io.github.mike10004.subprocess.ScopedProcessTracker;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class HarReplayExec_NonparamTest extends HarReplayExecTestBase {

    @Test
    public void executeHelp() throws Exception {
        ProcessResult<String, String> result;
        try (ScopedProcessTracker processTracker = new ScopedProcessTracker()) {
            result = execute(processTracker, "--help").await();
        }
        System.out.format("stdout:%n%s%n", result.content().stdout());
        assertEquals("exit code", 0, result.exitCode());
        assertNotEquals("stdout", "", result.content().stdout());
    }

}
