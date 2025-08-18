FROM openjdk:8
LABEL authors="Brugnara <mb@disi.unitn.eu>, Matteo Lissandrini <ml@disi.unitn.eu>"

ENV GREMLIN3_TAG=3.2.9
ENV GREMLIN3_HOME=/opt/gremlin
ENV PATH=/opt/gremlin/bin:$PATH

# 基础包
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        build-essential \
        libgoogle-perftools4 \
        ca-certificates \
        pwgen \
        wget \
        openssl \
        golang \
        curl \
        bash \
        maven \
        ant \
        git \
        locales \
        sudo \
        gnupg && \
    rm -rf /var/lib/apt/lists/*

# 生成 UTF-8 locale（PG 需要）
RUN localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8
ENV LANG=en_US.utf8

# ====== PostgreSQL 15 安装（PGDG bullseye 源）======
ARG PG_MAJOR=15
ENV PG_MAJOR=${PG_MAJOR}

# 导入 PGDG key 并添加 bullseye 源（不要 jessie）
RUN set -eux; \
    apt-get update; \
    apt-get install -y --no-install-recommends ca-certificates curl gnupg; \
    install -d /usr/share/keyrings; \
    curl -fsSL https://www.postgresql.org/media/keys/ACCC4CF8.asc \
      | gpg --dearmor -o /usr/share/keyrings/postgresql.gpg; \
    echo "deb [signed-by=/usr/share/keyrings/postgresql.gpg] http://apt.postgresql.org/pub/repos/apt bullseye-pgdg main" \
      > /etc/apt/sources.list.d/pgdg.list; \
    apt-get update; \
    apt-get install -y --no-install-recommends postgresql-common; \
    sed -ri 's/#(create_main_cluster) .*$/\1 = false/' /etc/postgresql-common/createcluster.conf; \
    apt-get install -y --no-install-recommends \
      postgresql-${PG_MAJOR} postgresql-contrib-${PG_MAJOR}; \
    rm -rf /var/lib/apt/lists/*

# postgres 用户与运行目录
RUN getent group postgres >/dev/null || groupadd -r postgres --gid=999 && \
    id -u postgres >/dev/null 2>&1 || useradd -r -g postgres --uid=999 postgres && \
    mkdir -p /var/run/postgresql && \
    chown -R postgres:postgres /var/run/postgresql && \
    chmod 2777 /var/run/postgresql

# 配置模板（注意路径仍然以 PG_MAJOR 区分）
RUN mv -v /usr/share/postgresql/${PG_MAJOR}/postgresql.conf.sample /usr/share/postgresql/ && \
    cp /usr/share/postgresql/postgresql.conf.sample /tmp/postgresql.conf.sample

# RUN apt-get update && apt-get install -y --no-install-recommends python3 && \
#     rm -rf /var/lib/apt/lists/*

# # pgtune（使用 version=15）
# RUN git clone https://github.com/andreif/pgtune.git --branch allthethings --single-branch pgtune && \
#     pgtune/pgtune --type=Web --version=${PG_MAJOR} \
#       -i /usr/share/postgresql/postgresql.conf.sample \
#       -o /tmp/postgresql.conf.sample.tuned && \
#     mv /tmp/postgresql.conf.sample.tuned /usr/share/postgresql/postgresql.conf.sample && \
#     sed -ri "s!^#?(listen_addresses)\\s*=\\s*\\S+.*!\\1 = '*'!" /usr/share/postgresql/postgresql.conf.sample && \
#     sed -i  "s/#max_locks_per_transaction\\ =\\ 64/max_locks_per_transaction = 256/" /usr/share/postgresql/postgresql.conf.sample && \
#     ln -sv ../postgresql.conf.sample /usr/share/postgresql/${PG_MAJOR}/

# PATH 与数据目录
ENV PATH=/usr/lib/postgresql/${PG_MAJOR}/bin:$PATH
ENV PGDATA=/var/lib/postgresql/data
RUN mkdir -p "$PGDATA" && chown -R postgres:postgres "$PGDATA" && chmod 777 "$PGDATA"
# (We need to commit the data)
# VOLUME /var/lib/postgresql/data


# Gremlin
#RUN curl -L -o /tmp/gremlin.zip \
#    http://mirror.nohup.it/apache/tinkerpop/${GREMLIN3_TAG}/apache-tinkerpop-gremlin-console-${GREMLIN3_TAG}-bin.zip && \
ADD libs/apache-tinkerpop-gremlin-console-${GREMLIN3_TAG}-bin.tgz /opt
RUN ln -s /opt/apache-tinkerpop-gremlin-console-${GREMLIN3_TAG} ${GREMLIN3_HOME}

WORKDIR /tmp
COPY extra/dot_groovy           /root/.groovy
COPY extra/*-pg.groovy          /tmp/

RUN  ${GREMLIN3_HOME}/bin/gremlin.sh -e /tmp/install-pg.groovy && \
     rm -vf ${GREMLIN3_HOME}/lib/groovy-swing-2.4.*.jar && \
     rm -vf ${GREMLIN3_HOME}/lib/groovy-jsr223-2.4.*-indy.jar && \
     rm -vf ${GREMLIN3_HOME}/lib/groovy-xml-2.4.*.jar && \
     ${GREMLIN3_HOME}/bin/gremlin.sh -e /tmp/activate-pg.groovy

COPY init/pg-init.sh /
RUN chmod 777 /pg-init.sh

COPY extra/pg-label-hash.go /pghash.go

ENV INDEX_QUERY_PREFIX='pg-'

# standard port
#EXPOSE 9999

WORKDIR /runtime
CMD ["/pg-init.sh"]