package edu.sakovichKseniya.junit;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

public class TestLauncher {
    public static void main(String[] args) {
        Launcher launcher = LauncherFactory.create();

        SummaryGeneratingListener summaryGeneratingListener = new SummaryGeneratingListener();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder
                .request()
//                .selectors(DiscoverySelectors.selectClass(edu.sakovichKseniya.junit.UserServiceTest.class))
                .selectors(DiscoverySelectors.selectDirectory("edu.sakovichKseniya.junit.service"))
                .build();

        launcher.execute(request, summaryGeneratingListener);
    }
}
