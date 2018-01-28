# STAGE I: build the plugin.
ARG ES_VERSION
ARG PLUGIN_VERSION

FROM java:8-jdk-alpine as builder

ENV _JAVA_OPTIONS '-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+TieredCompilation -Xms256m -Xmx1536m -Xss1m'
ENV BUILD_PATH '/app'

RUN \
    apk add --update \
        bash \
        ca-certificates \
    && update-ca-certificates \
    && rm -rf /var/cache/apk/*
# Take everything we need into the container
WORKDIR ${BUILD_PATH}
COPY config ${BUILD_PATH}/config
COPY gradle ${BUILD_PATH}/gradle
COPY src ${BUILD_PATH}/src
COPY gradlew ${BUILD_PATH}
COPY build.gradle ${BUILD_PATH}
COPY settings.gradle ${BUILD_PATH}
COPY plugin-descriptor.properties ${BUILD_PATH}
# ES won't run under root
RUN adduser -D -u 1000 builder
RUN chown -R builder:users ${BUILD_PATH}
USER builder
# Test and build the artifact.
ENV GRADLE_USER_HOME=/tmp/.gradle
RUN ./gradlew clean cleanTest test release

# STAGE II: build the image of ES with plugin inluded.
# Let's continue with a fresh ES installation.
# Port 9200 is exposed by default.
FROM elasticsearch:${ES_VERSION}-alpine
ARG PLUGIN_VERSION
# Do not forget about the artifact we produced
COPY --from=builder /app/build/distributions/elasticsearch-ukrainian-lemmatizer-${PLUGIN_VERSION}.zip /tmp

ENV ES_HOME '/usr/share/elasticsearch'
RUN chown -R elasticsearch ${ES_HOME}

USER elasticsearch

WORKDIR ${ES_HOME}

RUN ./bin/plugin install file:/tmp/elasticsearch-ukrainian-lemmatizer-${PLUGIN_VERSION}.zip

CMD ["./bin/elasticsearch"]

