FROM buchuitoudegou/gremlin-arangodb:latest
COPY init/arangodb-init.sh /
RUN chmod 755 /arangodb-init.sh

WORKDIR /runtime

CMD ["/arangodb-init.sh"]
