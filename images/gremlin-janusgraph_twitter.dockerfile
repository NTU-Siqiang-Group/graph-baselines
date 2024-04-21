# docker build  -t gremlin-janusgraph_twitter-2010.json3 -f gremlin-janusgraph_twitter.dockerfile .
FROM dbtrento/gremlin-janusgraph:latest

ADD extra/janusdata.zip /

RUN mkdir -p $JANUS_HOME/db && \
  mkdir -p $JANUS_HOME/db/berkeley && \
  unzip /janusdata.zip -d $JANUS_HOME/db/berkeley/

RUN sed -i "s/..\/db\/berkeley/\$JANUS_HOME\/db\/runtime\/data\/janusdata/g" $JANUS_HOME/conf/janusgraph-berkeleyje.properties
RUN cat $JANUS_HOME/conf/janusgraph-berkeleyje.properties

RUN rm /janusdata.zip

ADD init/janusgraph-twitter.sh /root/janusgraph.sh
WORKDIR $JANUS_HOME

RUN chmod +x /root/janusgraph.sh
CMD ["/root/janusgraph.sh"]