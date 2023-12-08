package com.universal.core.library.developer;

public class DeveloperHelper {

    public static Boolean isDebug() {
        var args = java.lang.management.ManagementFactory.getRuntimeMXBean().
                getInputArguments().toString();
        return args.indexOf("-agentlib:jdwp") > 0
                || args.indexOf("intellij-coverage-agent") > 0
                ;
    }
}
