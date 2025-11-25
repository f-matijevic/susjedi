global:
  checkNewVersion: false
  sendAnonymousUsage: false

providers:
  docker:
    network: ingress
    exposedByDefault: false
    defaultRule: "Host(`{{ normalize .Name }}.${INFRA_BASE_DOMAIN}`)"
  file:
    watch: false
    directory: /etc/traefik/dynamic

entryPoints:
  web:
    address: ":443"
    http:
      tls:
        certResolver: lencr
      sanitizePath: true
    observability:
      accessLogs: false
      metrics: false
      tracing: false

certificatesResolvers:
  lencr:
    acme:
      email: "${INFRA_ACME_EMAIL}"
      storage: /certs/acme.json
      tlsChallenge: {}
      keyType: EC256

log:
  level: INFO
  noColor: true
  format: common
