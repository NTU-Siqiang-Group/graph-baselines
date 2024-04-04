FROM maven:3.6.3-jdk-8

RUN apt-get update \
  && apt-get -y install unzip

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