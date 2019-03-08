#FROM maven:3.6.0-jdk-8-alpine as builder
#COPY  . /root/app/
#RUN mkdir ./.m2
#COPY /src/main/resources/settings.xml ./.m2/settings.xml
#WORKDIR /root/app
#RUN mvn install
#
#FROM oracle/graalvm-ce:1.0.0-rc11 as graalvm
#COPY --from=builder /root/app/ /home/app/
#WORKDIR /home/app
#RUN java -cp target/security-service-0.1.jar \
#            io.micronaut.graal.reflect.GraalClassLoadingAnalyzer \
#            reflect.json
#RUN native-image --no-server \
#                 --class-path target/security-service-0.1.jar \
#                 -H:ReflectionConfigurationFiles=/home/app/reflect.json \
#                 -H:EnableURLProtocols=http \
#                 -H:IncludeResources='logback.xml|application.yml|META-INF/services/*.*' \
#                 -H:+ReportUnsupportedElementsAtRuntime \
#                 -H:+AllowVMInspection \
#                 --rerun-class-initialization-at-runtime='sun.security.jca.JCAUtil$CachedSecureRandomHolder',javax.net.ssl.SSLContext \
#                 --delay-class-initialization-to-runtime=io.netty.handler.codec.http.HttpObjectEncoder,io.netty.handler.codec.http.websocketx.WebSocket00FrameEncoder,io.netty.handler.ssl.util.ThreadLocalInsecureRandom \
#                 -H:-UseServiceLoaderFeature \
#                 --allow-incomplete-classpath \
#                 -H:Name=security-service \
#                 -H:Class=security.service.Application
#
#
#FROM frolvlad/alpine-glibc
#EXPOSE 8080
#COPY --from=graalvm /home/app/security-service .
#ENTRYPOINT ["./security-service"]

FROM anapsix/alpine-java

MAINTAINER James Loveday <james.loveday@cgi.com>
COPY /target/security-service-*.jar /security-service.jar
RUN mkdir /audits

EXPOSE 9100

ENTRYPOINT ["java", "-jar", "/security-service.jar"]
