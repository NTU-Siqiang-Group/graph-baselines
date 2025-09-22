FROM docker.io/buchuitoudegou/gremlin-pg:latest
COPY init/pg-init.sh /
RUN chmod 777 /pg-init.sh
CMD ["/pg-init.sh"]
