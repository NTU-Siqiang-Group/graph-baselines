FROM maven:3.6.3-jdk-8

# point to archived repos for EOL buster
RUN printf 'deb http://archive.debian.org/debian buster main contrib non-free\n' \
        > /etc/apt/sources.list && \
    printf 'deb http://archive.debian.org/debian buster-updates main contrib non-free\n' \
        >> /etc/apt/sources.list && \
    printf 'deb http://archive.debian.org/debian-security buster/updates main contrib non-free\n' \
        >> /etc/apt/sources.list && \
    apt-get -o Acquire::Check-Valid-Until=false update && \
    apt-get install -y --no-install-recommends unzip && \
    rm -rf /var/lib/apt/lists/*

ADD libs/janusgraph-1.0.0.zip /opt

RUN cd /opt \
  && unzip janusgraph-1.0.0.zip

ENV PATH $PATH:/opt/janusgraph-1.0.0/bin
ENV JANUS_HOME /opt/janusgraph-1.0.0

ADD extra/janus-jvm.options $JANUS_HOME/conf/
ADD init/janusgraph.sh /root/janusgraph.sh

WORKDIR $JANUS_HOME

RUN chmod +x /root/janusgraph.sh

CMD ["/root/janusgraph.sh"]