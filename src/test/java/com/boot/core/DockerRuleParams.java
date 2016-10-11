package com.boot.core;

public class DockerRuleParams {

    String imageName;

    String[] ports;
    String[] mappedPorts;
    String cmd;

    String portToWaitOn;
    public int waitTimeout;
    String logToWait;
    boolean needPull;
}
