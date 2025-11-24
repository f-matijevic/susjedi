#!/bin/bash
set +m -o pipefail
shopt -s lastpipe

log() {
    printf '%s\n' "$1" >&2
}

events() {
    docker events \
        -f "type=container" \
        -f "label=pg-label.enable" \
        -f "event=create" -f "event=destroy" \
        --format "{{.Status}},{{.Name}},{{.ID}}"
}

labels() {
    FMT='{{ range $k, $v := .Config.Labels -}}{{ $k }}={{ $v }}'$'\n''{{ end -}}'
    docker inspect -f "$FMT" "$1"
}

sql_do() {
    echo 'DO $$'; cat; echo '$$;'
}

on_create() {
    sql_do <<EOF
DECLARE
  _pg_name TEXT := '${PG_NAME:-$PG_USER}';
  _pg_user TEXT := '${PG_USER:-$PG_NAME}';
  _pg_pass TEXT := '${PG_PASS:-$PG_USER}';
  _persist BOOLEAN := '${PERSIST:-false}';
BEGIN
  RAISE NOTICE '$NAME: Stvaram bazu % za korisnika % (trajno: %)',
    quote_ident(_pg_name), quote_ident(_pg_user), _persist;
  BEGIN
    PERFORM dblink_exec('', 'CREATE DATABASE ' || quote_ident(_pg_name)) || ';';
    EXCEPTION WHEN duplicate_database THEN
      IF _persist THEN
        RAISE NOTICE 'Trajna baza vec postoji';
      ELSE
        RAISE EXCEPTION 'Postojeca baza ne smije biti privremena';
      END IF;
  END;
  BEGIN
    CREATE USER _pg_user PASSWORD _pg_pass;
    GRANT ALL PRIVILEGES ON DATABASE _pg_name TO _pg_user;
    EXCEPTION WHEN duplicate_object THEN
      IF _persist THEN
        RAISE NOTICE 'Trajni korisnik vec postoji';
      ELSE
        RAISE EXCEPTION 'Postojeci korisnik ne smije biti privremen';
      END IF;
  END;
END;
EOF
}

on_destroy() {
    sql_do <<EOF
DECLARE
  _pg_name TEXT := '${PG_NAME:-$PG_USER}';
  _pg_user TEXT := '${PG_USER:-$PG_NAME}';
  _persist BOOLEAN := '${PERSIST:-false}';
BEGIN
  IF NOT _persist THEN
    RAISE NOTICE '$NAME: Odbacujem privremenu bazu % i korisnika %',
      quote_ident(_pg_name), quote_ident(_pg_user);
    PERFORM dblink_exec('', 'DROP DATABASE IF EXISTS ' || quote_ident(_pg_name)) || ';';
    DROP USER IF EXISTS _pg_user;
  END IF;
END;
EOF
}

main() {
    sleep 1
    echo "CREATE EXTENSION IF NOT EXISTS dblink;"
    log "pg-label: spreman"

    while IFS=',' read -r EVENT NAME ID; do
        local PG_NAME="" PG_USER="" PG_PASS="" PERSIST=true

        NAME="${NAME#/}"
        log "$EVENT: $NAME ($ID)"

        labels "$ID" | while IFS='=' read -r KEY VAL; do
            case "$KEY" in
                pg-label.database) PG_NAME="$VAL";;
                pg-label.username) PG_USER="$VAL";;
                pg-label.password) PG_PASS="$VAL";;
                pg-label.ephemeral) PERSIST="";;
            esac
        done

        if [ -z "$PG_NAME$PG_USER" ]; then
            if [ -n "$PERSIST" ]; then
                log "greska: $NAME: Za trajnu bazu nije zadano barem ime ili korisnicko ime"
                continue
            fi
            PG_USER="$ID"
        fi

        case "$EVENT" in
            create) on_create;;
            destroy) on_destroy;;
        esac
    done
}

DRY_RUN=${DRY_RUN+cat}
export PGAPPNAME=pg-label

events | main | ${DRY_RUN:-psql -Antw}
