FROM gcr.io/distroless/java11@sha256:520bf091f91e13f7627e60cdd1a515911ac024d178fe72266c3a8170f60082d0
COPY target/ms-claim-manager-*.jar /claim-manager.jar

COPY --from=pik94420.live.dynatrace.com/linux/oneagent-codemodules:java / /
ENV LD_PRELOAD /opt/dynatrace/oneagent/agent/lib64/liboneagentproc.so

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/claim-manager.jar"]
