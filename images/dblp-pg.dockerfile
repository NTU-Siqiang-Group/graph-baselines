FROM gremlin-pg_twitch.json3:latest

ADD extra/postgresql.conf /var/lib/postgresql/data/postgresql.conf
