FROM maven:3.6.3-openjdk-8
LABEL authors="Brugnara <mb@disi.unitn.eu>, Matteo Lissandrini <ml@disi.unitn.eu>, Nolan Nichols <nolan.nichols@gmail.com>"

ENV GREMLIN3_TAG 3.2.9
ENV GREMLIN3_HOME /opt/gremlin
ENV PATH /opt/gremlin/bin:$PATH

ADD libs/apache-tinkerpop-gremlin-console-${GREMLIN3_TAG}-bin.tgz /opt
RUN ln -s /opt/apache-tinkerpop-gremlin-console-${GREMLIN3_TAG} ${GREMLIN3_HOME}
RUN mvn dependency:get -Dartifact=org.apache.tinkerpop:neo4j-gremlin:3.2.3
RUN mvn dependency:get -Dartifact=org.neo4j:neo4j-tinkerpop-api-impl:0.3-2.3.3

WORKDIR /tmp
COPY extra/dot_groovy /root/.groovy
COPY extra/*-neo4j-tp3.groovy /tmp/

RUN  ${GREMLIN3_HOME}/bin/gremlin.sh -e /tmp/install-neo4j-tp3.groovy && \
     rm -vf ${GREMLIN3_HOME}/lib/groovy-swing-2.4.*.jar && \
     rm -vf ${GREMLIN3_HOME}/lib/groovy-jsr223-2.4.*-indy.jar && \
     rm -vf ${GREMLIN3_HOME}/lib/groovy-xml-2.4.*.jar && \
     ${GREMLIN3_HOME}/bin/gremlin.sh -e /tmp/activate-neo4j-tp3.groovy

COPY init/neo4j-tp3-init.sh /
RUN chmod 755 /neo4j-tp3-init.sh

ENV INDEX_QUERY_PREFIX="neo4j-tp3-"

WORKDIR /runtime

CMD ["/neo4j-tp3-init.sh"]
