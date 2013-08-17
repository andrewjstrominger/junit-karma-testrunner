package de.is24.util.karmatestrunner;


import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import de.is24.util.karmatestrunner.browsers.*;

import java.util.*;

public class TestReporter {

    private HashMap<String, Class> browsers = new HashMap<String, Class>();
    private JSONParser parser = new JSONParser();
    private RunNotifier notifier;

    TestReporter(RunNotifier notifier) {
        this.notifier = notifier;
    }


    ContainerFactory containerFactory = new ContainerFactory(){
        public List creatArrayContainer() {
            return new LinkedList();
        }

        public Map createObjectContainer() {
            return new LinkedHashMap();
        }

    };


    public void handleMessage(String data) {
        try {
            Map message = (Map) parser.parse(data, containerFactory);
            String message_type = (String) message.get("type");
            if (message_type.equalsIgnoreCase("test")) {
                reportTestResult(message);
            }
            if (message_type.equalsIgnoreCase("browsers")) {
                setBrowsers(message);
            }
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void setBrowsers(Map browsers) {
        for (int i = 0; i < ((List) browsers.get("list")).size(); i++) {
            Map browser = (Map) ((List) browsers.get("list")).get(i);


            String browserName = (String) browser.get("name");
            Class browserClass = UnknownBrowser.class;
            if (browserName.toLowerCase(Locale.GERMAN).contains("chrome")) {
                browserClass = Chrome.class;
            }
            if (browserName.toLowerCase(Locale.GERMAN).contains("phantom")) {
                browserClass = PhantomJS.class;
            }
            if (browserName.toLowerCase(Locale.GERMAN).contains("firefox")) {
                browserClass = FireFox.class;
            }

            this.browsers.put((String) browser.get("browserId"), browserClass);
        }
    }

    private void reportTestResult(Map message) {
        String browserId = (String) message.get("browserId");
        Map result = (Map) message.get("result");
        Long time = (Long) result.get("time");
        Boolean skipped = (Boolean) result.get("skipped");
        String label = (String) result.get("description");
        Boolean success = (Boolean) result.get("success");
        String suite = result.get("suite").toString();


        Description description = describeChild(browsers.get(browserId), suite + " " + label);

        try {
            if (skipped){
                notifier.fireTestIgnored(description);
            } else {
                notifier.fireTestStarted(description);
            }

            if (!success){

                String log = result.get("log").toString();
                    JSTestFailure failure = new JSTestFailure(description, label,
                            "Failures: "+log);
                    notifier.fireTestFailure(failure);
                }

        } finally {
            notifier.fireTestFinished(description);

        }

    }

    private Description describeChild(Class browser, String description) {

        return Description
                .createTestDescription(browser, description);
    }

    /**
     * A JavaScript execution failure object.
     */
    private static class JSTestFailure extends Failure {
        private final String name;

        public JSTestFailure(Description description, String name, String message) {
            super(description, new RuntimeException(message));
            this.name = name;
        }

        @Override
        public String getTestHeader() {
            return name;
        }

        @Override
        public String getTrace() {
            // The stack means nothing here.
            return getMessage();
        }

        @Override
        public String toString() {
            return getTestHeader();
        }

    }


}