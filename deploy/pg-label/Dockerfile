FROM alpine:3

RUN apk add --no-cache \
    bash docker-cli postgresql-client

ENTRYPOINT ["bash", "app.sh"]

COPY app.sh ./
