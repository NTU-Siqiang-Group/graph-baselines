FROM ubuntu:20.04

RUN apt-get update && \
    apt-get install --no-install-recommends -y \
    gnupg \
    curl \
    wget \
    unzip \
    systemctl \
    openjdk-8-jdk
    
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

ENV GREMLIN3_TAG 3.0.2
ENV GREMLIN3_HOME /opt/apache-gremlin-console-${GREMLIN3_TAG}-incubating
ADD libs/apache-gremlin-console-${GREMLIN3_TAG}-incubating-bin.zip /opt
RUN unzip /opt/apache-gremlin-console-${GREMLIN3_TAG}-incubating-bin.zip -d /opt
ENV PATH=$PATH:${GREMLIN3_HOME}/bin
COPY extra/dot_groovy /root/.groovy

ENV STARDOG_VERSION 5.1.0
RUN curl http://packages.stardog.com/stardog.gpg.pub | apt-key add \
 && echo "deb http://packages.stardog.com/deb/ stable main" | tee -a /etc/apt/sources.list \
 && apt-get update \
 && apt-get install -y stardog=${STARDOG_VERSION}

ADD extra/activate-stardog.groovy /tmp
RUN mkdir -p ${GREMLIN3_HOME}/ext/stardog-gremlin/plugin \
    && find /opt/stardog/client -iname "*.jar" | xargs -I{} cp {} ${GREMLIN3_HOME}/ext/stardog-gremlin/plugin

RUN cd ${GREMLIN3_HOME}/bin/ \
    && cat /tmp/activate-stardog.groovy | ./gremlin.sh

WORKDIR /runtime